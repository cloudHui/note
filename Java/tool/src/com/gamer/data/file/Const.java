package com.gamer.data.file;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 常量与路径配置：单一 {@link #INITIAL_PATHS_FILE} 内用 {@link #SECTION_CMD_MARKER} 分隔「目录树」与「CMD 执行」路径。
 */
public class Const {
    public static final String GD = "gd";

    public static final String TXT = "txt";

    public static final String BAT = "bat";

    public static final String PROTO = "proto";

    /** 初始路径配置文件名（与 jar 同目录，或与 build.xml 同目录即 user.dir） */
    public static final String INITIAL_PATHS_FILE = "initial-paths.txt";

    /**
     * 分隔行：此前为左侧目录树根路径，此后为「目录执行 cmd」下拉专用路径（互不混用）。
     */
    public static final String SECTION_CMD_MARKER = "---CMD---";

    /**
     * 左侧目录树根节点路径（与 CMD 路径列表独立）
     */
    public static final List<String> INITIAL_PATHS = new ArrayList<>();

    /**
     * 「目录执行 cmd」下拉路径（默认仅两条，可在配置文件中追加）
     */
    public static final List<String> CMD_PATHS = new ArrayList<>();

    /**
     * 目录树默认补齐路径（写入文件「---CMD---」之前区域）
     */
    public static final List<String> DEFAULT_INITIAL_PATHS = new ArrayList<>();

    /**
     * CMD 下拉默认路径（仅两条，写入文件「---CMD---」之后区域）
     */
    public static final List<String> DEFAULT_CMD_PATHS = new ArrayList<>();

    private static final Comparator<String> PATH_COMPARATOR = (path1, path2) -> {
        String[] parts1 = path1.split("\\\\");
        String[] parts2 = path2.split("\\\\");
        int minLength = Math.min(parts1.length, parts2.length);
        for (int i = 0; i < minLength; i++) {
            int cmp = parts1[i].compareToIgnoreCase(parts2[i]);
            if (cmp != 0) {
                return cmp;
            }
        }
        return Integer.compare(parts1.length, parts2.length);
    };

    static {
        DEFAULT_INITIAL_PATHS.add("D:\\code\\WorkSpace\\Common\\Tools\\Bin");
        DEFAULT_INITIAL_PATHS.add("D:\\code\\WorkSpace\\Document");
        DEFAULT_CMD_PATHS.add("D:\\code\\WorkSpace\\Common\\Tools\\Bin");
        DEFAULT_CMD_PATHS.add("D:\\code\\WorkSpace\\Document");
        loadInitialPaths();
    }

    /**
     * 获取配置文件：jar 运行时为 jar 同目录，否则为 user.dir（与 build.xml 同目录运行时为项目根）
     */
    public static File getConfigFile() {
        try {
            URL location = Const.class.getProtectionDomain().getCodeSource().getLocation();
            URI uri = location.toURI();
            File base = new File(uri);
            if (base.isFile() && base.getName().toLowerCase().endsWith(".jar")) {
                return new File(base.getParent(), INITIAL_PATHS_FILE);
            }
            return new File(System.getProperty("user.dir"), INITIAL_PATHS_FILE);
        } catch (Exception e) {
            return new File(System.getProperty("user.dir"), INITIAL_PATHS_FILE);
        }
    }

    /**
     * 从配置文件加载目录树路径与 CMD 路径；无 {@link #SECTION_CMD_MARKER} 的旧文件视为整份均为目录树，CMD 区初始化为默认两条并回写。
     */
    public static void loadInitialPaths() {
        INITIAL_PATHS.clear();
        CMD_PATHS.clear();
        File configFile = getConfigFile();
        boolean sawCmdMarker = false;

        if (configFile.exists() && configFile.isFile()) {
            try {
                List<String> lines = Files.readAllLines(configFile.toPath(), StandardCharsets.UTF_8);
                sawCmdMarker = parsePathsLines(lines);
            } catch (Exception e) {
                // 忽略
            }
        }

        boolean changed = false;
        if (!sawCmdMarker) {
            CMD_PATHS.clear();
            for (String p : DEFAULT_CMD_PATHS) {
                if (!CMD_PATHS.contains(p)) {
                    CMD_PATHS.add(p);
                }
            }
            changed = true;
        }

        for (String defaultPath : DEFAULT_INITIAL_PATHS) {
            if (!INITIAL_PATHS.contains(defaultPath)) {
                INITIAL_PATHS.add(defaultPath);
                changed = true;
            }
        }

        for (String defaultPath : DEFAULT_CMD_PATHS) {
            if (!CMD_PATHS.contains(defaultPath)) {
                CMD_PATHS.add(defaultPath);
                changed = true;
            }
        }

        INITIAL_PATHS.sort(PATH_COMPARATOR);
        CMD_PATHS.sort(PATH_COMPARATOR);

        if (changed || !configFile.exists()) {
            savePathsConfig();
        }
    }

    /**
     * 解析配置文件行：# 开头为注释；遇 {@link #SECTION_CMD_MARKER} 后归入 CMD 路径。
     *
     * @return 是否出现过 CMD 分隔行
     */
    private static boolean parsePathsLines(List<String> lines) {
        boolean inCmd = false;
        boolean sawMarker = false;
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            if (trimmed.startsWith("#")) {
                continue;
            }
            if (SECTION_CMD_MARKER.equals(trimmed)) {
                inCmd = true;
                sawMarker = true;
                continue;
            }
            if (!inCmd) {
                if (!INITIAL_PATHS.contains(trimmed)) {
                    INITIAL_PATHS.add(trimmed);
                }
            } else {
                if (!CMD_PATHS.contains(trimmed)) {
                    CMD_PATHS.add(trimmed);
                }
            }
        }
        return sawMarker;
    }

    /**
     * 将目录树路径与 CMD 路径一并写回 {@link #INITIAL_PATHS_FILE}。
     */
    public static void savePathsConfig() {
        File configFile = getConfigFile();
        try (Writer writer =
            new OutputStreamWriter(Files.newOutputStream(configFile.toPath()), StandardCharsets.UTF_8)) {
            writer.write("# 左侧目录树根节点（每行一个路径）\n");
            for (String path : INITIAL_PATHS) {
                writer.write(path);
                writer.write("\n");
            }
            writer.write(SECTION_CMD_MARKER);
            writer.write("\n");
            writer.write("# 「目录执行 cmd」下拉专用（默认两条，可追加，与上方目录树独立）\n");
            for (String path : CMD_PATHS) {
                writer.write(path);
                writer.write("\n");
            }
        } catch (Exception e) {
            // 写失败仅忽略
        }
    }

    /**
     * 保存目录树路径（会同时保留 CMD 区，勿丢段）
     */
    public static void saveInitialPaths() {
        savePathsConfig();
    }

    /**
     * 将路径加入 CMD 下拉列表并持久化（不修改目录树列表）
     */
    public static void ensureCmdPathListed(String path) {
        if (path == null || path.trim().isEmpty()) {
            return;
        }
        String absPath = new File(path).getAbsolutePath();
        if (!CMD_PATHS.contains(absPath)) {
            CMD_PATHS.add(absPath);
            CMD_PATHS.sort(PATH_COMPARATOR);
            savePathsConfig();
        }
    }
}
