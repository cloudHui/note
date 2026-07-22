package com.gamer.data.mpcserver.commands.excel;

import com.gamer.data.excel.ExcelExtensions;
import com.gamer.data.mpcserver.core.McpUtils;
import com.gamer.data.mpcserver.core.Process;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedInputStream;
import java.io.File;
import java.lang.ref.SoftReference;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Excel Workbook 进程级缓存（基于文件 lastModified 时间戳）。
 *
 * <p>解决 ExcelReadCommand / ExcelDescribeSheetsCommand 每次都完整解析 Workbook 的性能问题。
 * 同一文件在未修改的情况下只解析一次，后续调用直接复用缓存中的 Workbook 对象。</p>
 *
 * <p>设计要点：</p>
 * <ul>
 *   <li>缓存键：文件规范化绝对路径。</li>
 *   <li>失效策略：按文件 {@code lastModified()} 戳匹配；文件被修改则重新解析。</li>
 *   <li>内存策略：使用 {@link SoftReference} 包裹 Workbook，在 JVM 内存紧张时允许被 GC。</li>
 *   <li>容量上限：LRU 淘汰，最多缓存 {@value #MAX_ENTRIES} 个 Workbook，防止大量不同文件撑爆内存。</li>
 *   <li>线程安全：内部用 synchronized 串行化，MCP server 当前为单线程，无额外锁竞争。</li>
 * </ul>
 *
 * <p>注意：Workbook 不是线程安全的，调用方不得并发使用同一个 Workbook 实例。
 * 当前 MCP server 是单线程 stdio 模型，符合此约束。</p>
 */
@Process("excel_workbook_cache")
public final class ExcelWorkbookCache {

    private static final int MAX_ENTRIES = 8;

    /** 缓存条目：Workbook 软引用 + 文件修改时间戳。 */
    private static class Entry {
        /** 用 SoftReference 持有 Workbook，内存紧张时允许被 GC，避免 OOM。 */
        private SoftReference<Workbook> ref;
        /** 与磁盘 lastModified 对齐；成功 save 后由 {@link #markFileSaved} 更新，避免误淘汰仍有效的缓存实例。 */
        private long lastModified;

        private Entry(Workbook wb, long lastModified) {
            this.ref = new SoftReference<>(wb);
            this.lastModified = lastModified;
        }

        private Workbook get() {
            return ref == null ? null : ref.get();
        }
    }

    /**
     * 基于 accessOrder 的 LRU LinkedHashMap；超过 MAX_ENTRIES 时淘汰最久未使用的条目。
     * removeEldestEntry 需要关闭被淘汰 Workbook，所以在 put 前手动检查。
     */
    private final Map<String, Entry> cache;

    /** 全局单例。 */
    private static final ExcelWorkbookCache INSTANCE = new ExcelWorkbookCache();

    private ExcelWorkbookCache() {
        cache = new LinkedHashMap<String, Entry>(MAX_ENTRIES, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Entry> eldest) {
                if (size() > MAX_ENTRIES) {
                    closeEntry(eldest.getValue());
                    return true;
                }
                return false;
            }
        };
    }

    public static ExcelWorkbookCache getInstance() {
        return INSTANCE;
    }

    /**
     * 获取（或创建）指定文件对应的 Workbook。
     *
     * <p>若缓存命中且文件未修改，直接返回缓存对象；否则重新解析并更新缓存。</p>
     *
     * @param file 必须存在且是 .xls/.xlsx 文件
     * @return 可直接读取的 Workbook
     */
    public synchronized Workbook get(File file) throws Exception {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("Excel文件不存在: " + (file == null ? "null" : file.getAbsolutePath()));
        }

        String key = cacheKey(file);
        long lastMod = file.lastModified();

        Entry entry = cache.get(key);
        if (entry != null) {
            Workbook cached = entry.get();
            if (cached != null && entry.lastModified == lastMod) {
                return cached;
            }
            // 软引用被 GC 或文件已更新，移除旧条目。
            cache.remove(key);
            closeEntry(entry);
        }

        Workbook wb = parseWorkbook(file);
        cache.put(key, new Entry(wb, lastMod));
        return wb;
    }

    /**
     * 在同一 Workbook 实例上完成写盘成功后调用：把条目中记录的时间戳更新为当前文件修改时间，
     * 避免紧接着的 {@link #get(File)} 因 mtime 漂移而关闭并重建整本 Workbook。
     *
     * @param file 已保存的 Excel 文件
     * @param wb   必须与 {@link #get(File)} 返回的为同一引用
     */
    public synchronized void markFileSaved(File file, Workbook wb) {
        if (file == null || wb == null) {
            return;
        }
        String key = cacheKey(file);
        Entry entry = cache.get(key);
        if (entry == null) {
            return;
        }
        if (entry.get() != wb) {
            return;
        }
        entry.lastModified = file.lastModified();
    }

    private static Workbook parseWorkbook(File file) throws Exception {
        String fileName = file.getName();
        String ext = fileName.substring(fileName.lastIndexOf(".")).toLowerCase().trim();
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(Files.newInputStream(file.toPath()));
            if (ext.equalsIgnoreCase(ExcelExtensions.FILE_EXT_XLS)) {
                return new HSSFWorkbook(bis);
            } else if (ext.equalsIgnoreCase(ExcelExtensions.FILE_EXT_XLSX)) {
                return new XSSFWorkbook(bis);
            } else {
                throw new IllegalArgumentException("不支持的Excel扩展名: " + ext);
            }
        } finally {
            McpUtils.tryClose(bis);
        }
    }

    private static void closeEntry(Entry entry) {
        if (entry == null) {
            return;
        }
        Workbook wb = entry.get();
        if (wb != null) {
            McpUtils.tryClose(wb);
        }
        entry.ref = null;
    }

    private static String cacheKey(File file) {
        try {
            return file.getCanonicalPath();
        } catch (Exception e) {
            return file.getAbsolutePath();
        }
    }
}
