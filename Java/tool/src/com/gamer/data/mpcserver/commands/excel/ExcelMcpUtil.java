package com.gamer.data.mpcserver.commands.excel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamer.data.mpcserver.core.McpUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Excel MCP 公共工具（用于写入/复制/格式化/渲染截图等）。
 *
 * <p>约束：
 * <ul>
 *   <li>避免 Java 8 lambda/stream/tw-resources，保持与 MCP server 热更新约束一致。</li>
 *   <li>返回值优先使用可读 text（截图用 base64 PNG）。</li>
 * </ul>
 * </p>
 */
public final class ExcelMcpUtil {
    private ExcelMcpUtil() {
    }

    /** A1 单元格引用示例：A1、BC12。 */
    public static final class CellPos {
        public final int row; // 0-based
        public final int col; // 0-based

        public CellPos(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }

    /** A1:A2 范围（含边界），内部使用 0-based 行列。 */
    public static final class CellRange {
        public final int startRow;
        public final int endRow;
        public final int startCol;
        public final int endCol;

        public CellRange(int startRow, int endRow, int startCol, int endCol) {
            this.startRow = startRow;
            this.endRow = endRow;
            this.startCol = startCol;
            this.endCol = endCol;
        }
    }

    private static final Pattern SIMPLE_NUMERIC = Pattern.compile("^-?\\d+(\\.\\d+)?$");

    public static Workbook loadWorkbook(File file) throws Exception {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("Excel 文件不存在: " + (file == null ? "null" : file.getAbsolutePath()));
        }

        String name = file.getName();
        String ext = "";
        int idx = name.lastIndexOf('.');
        if (idx >= 0) {
            ext = name.substring(idx + 1).toLowerCase().trim();
        }

        InputStream is = null;
        BufferedInputStream bis = null;
        try {
            is = Files.newInputStream(file.toPath());
            bis = new BufferedInputStream(is);
            if ("xls".equals(ext)) {
                return new HSSFWorkbook(bis);
            }
            if ("xlsx".equals(ext) || ext.isEmpty()) {
                return new XSSFWorkbook(bis);
            }
            throw new IllegalArgumentException("不支持的Excel扩展名: " + ext);
        } finally {
            McpUtils.tryClose(bis);
            McpUtils.tryClose(is);
        }
    }

    public static void saveWorkbook(Workbook wb, File file) throws Exception {
        if (wb == null) {
            return;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            wb.write(fos);
            fos.flush();
        } finally {
            McpUtils.tryClose(fos);
        }
    }

    /**
     * 解析 A1 或 A1:B2 范围字符串。
     *
     * <p>不支持包含 sheetName 的复杂形式（如 Sheet1!A1）。</p>
     */
    public static CellRange parseA1Range(String range) {
        if (range == null || range.trim().isEmpty()) {
            throw new IllegalArgumentException("range不能为空");
        }
        String r = range.trim().replace(" ", "");
        String[] parts = r.split(":");
        if (parts == null || parts.length == 0) {
            throw new IllegalArgumentException("非法 range: " + range);
        }
        CellPos a = parseA1Cell(parts[0]);
        CellPos b = parts.length >= 2 ? parseA1Cell(parts[1]) : a;
        int startRow = Math.min(a.row, b.row);
        int endRow = Math.max(a.row, b.row);
        int startCol = Math.min(a.col, b.col);
        int endCol = Math.max(a.col, b.col);
        return new CellRange(startRow, endRow, startCol, endCol);
    }

    /** 解析单个 A1 单元格。 */
    public static CellPos parseA1Cell(String cellRef) {
        if (cellRef == null || cellRef.trim().isEmpty()) {
            throw new IllegalArgumentException("cellRef不能为空");
        }
        String s = cellRef.trim().replace(" ", "");
        CellReference ref = new CellReference(s);
        int row = ref.getRow(); // 0-based
        int col = ref.getCol(); // 0-based
        return new CellPos(row, col);
    }

    public static List<List<String>> parseValues2D(String values, ObjectMapper mapper) throws Exception {
        List<List<String>> out = new ArrayList<>();
        if (values == null) {
            return out;
        }
        String v = values.trim();
        if (v.isEmpty()) {
            return out;
        }

        // JSON 优先：支持 [ [a,b], [c,d] ] 或 [a,b,c]
        if (v.startsWith("[") || v.startsWith("{")) {
            try {
                JsonNode root = mapper == null ? new ObjectMapper().readTree(v) : mapper.readTree(v);
                if (root != null && root.isArray()) {
                    if (root.size() > 0 && root.get(0) != null && root.get(0).isArray()) {
                        for (int i = 0; i < root.size(); i++) {
                            JsonNode rowNode = root.get(i);
                            List<String> row = new ArrayList<>();
                            if (rowNode != null && rowNode.isArray()) {
                                for (int j = 0; j < rowNode.size(); j++) {
                                    JsonNode cell = rowNode.get(j);
                                    row.add(cell == null || cell.isNull() ? "" : cell.asText());
                                }
                            }
                            out.add(row);
                        }
                        return out;
                    }
                    // 一维数组：按单行处理
                    List<String> row = new ArrayList<>();
                    for (int i = 0; i < root.size(); i++) {
                        JsonNode cell = root.get(i);
                        row.add(cell == null || cell.isNull() ? "" : cell.asText());
                    }
                    out.add(row);
                    return out;
                }
            } catch (Exception ignored) {
                // JSON 解析失败则退化为 TSV/纯文本
            }
        }

        // TSV：行以 \n 分割、列以 \t 分割
        if (v.indexOf('\t') >= 0 || v.indexOf('\n') >= 0 || v.indexOf('\r') >= 0) {
            String[] lines = v.split("\\r?\\n", -1);
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                String[] cols = line.split("\\t", -1);
                List<String> row = new ArrayList<>();
                for (int j = 0; j < cols.length; j++) {
                    row.add(cols[j] == null ? "" : cols[j]);
                }
                out.add(row);
            }
            return out;
        }

        // 纯文本：单元格值
        List<String> row = new ArrayList<>();
        row.add(values);
        out.add(row);
        return out;
    }

    /** 根据 values2D 填充目标区域。超出 values 的区域留空；values 超出区域则截断。 */
    public static void fillRangeByValues(org.apache.poi.ss.usermodel.Sheet sheet, CellRange range,
        List<List<String>> values2D, ObjectMapper mapper) {
        if (sheet == null || range == null || values2D == null) {
            return;
        }
        int maxRows = range.endRow - range.startRow + 1;
        int maxCols = range.endCol - range.startCol + 1;

        for (int r = 0; r < maxRows; r++) {
            int targetRowIdx = range.startRow + r;
            Row row = sheet.getRow(targetRowIdx);
            if (row == null) {
                row = sheet.createRow(targetRowIdx);
            }
            List<String> srcRow = r < values2D.size() ? values2D.get(r) : null;
            for (int c = 0; c < maxCols; c++) {
                int targetColIdx = range.startCol + c;
                String v = "";
                if (srcRow != null && c < srcRow.size()) {
                    v = srcRow.get(c);
                }
                Cell cell = row.getCell(targetColIdx);
                if (cell == null) {
                    cell = row.createCell(targetColIdx);
                }
                if (v == null) {
                    v = "";
                }
                String vs = v;
                vs = vs.trim();
                if (vs.isEmpty()) {
                    cell.setBlank();
                    continue;
                }
                // 简单数字识别：纯数字则写入数值，尽量保留可计算性
                if (SIMPLE_NUMERIC.matcher(vs).matches()) {
                    try {
                        double dv = Double.parseDouble(vs);
                        cell.setCellValue(dv);
                        continue;
                    } catch (Exception ignored) {
                        // 兜底写字符串
                    }
                }
                cell.setCellValue(vs);
            }
        }
    }

    public static CellStyle buildSimpleCellStyle(Workbook wb, JsonNode styleNode) {
        if (wb == null) {
            return null;
        }
        CellStyle style = wb.createCellStyle();
        if (styleNode == null || styleNode.isNull() || !styleNode.isObject()) {
            return style;
        }

        JsonNode boldNode = styleNode.get("bold");
        if (boldNode != null && !boldNode.isNull()) {
            boolean bold = boldNode.asBoolean(false);
            org.apache.poi.ss.usermodel.Font font = wb.createFont();
            font.setBold(bold);
            style.setFont(font);
        }

        JsonNode alignNode = styleNode.get("align");
        if (alignNode != null && !alignNode.isNull()) {
            String a = alignNode.asText();
            if (a != null) {
                a = a.trim().toLowerCase();
                if ("center".equals(a)) {
                    style.setAlignment(HorizontalAlignment.CENTER);
                } else if ("right".equals(a)) {
                    style.setAlignment(HorizontalAlignment.RIGHT);
                } else if ("left".equals(a)) {
                    style.setAlignment(HorizontalAlignment.LEFT);
                }
            }
        }

        JsonNode vAlignNode = styleNode.get("vAlign");
        if (vAlignNode != null && !vAlignNode.isNull()) {
            String a = vAlignNode.asText();
            if (a != null) {
                a = a.trim().toLowerCase();
                if ("middle".equals(a)) {
                    style.setVerticalAlignment(VerticalAlignment.CENTER);
                } else if ("top".equals(a)) {
                    style.setVerticalAlignment(VerticalAlignment.TOP);
                } else if ("bottom".equals(a)) {
                    style.setVerticalAlignment(VerticalAlignment.BOTTOM);
                }
            }
        }

        return style;
    }

    /** 将 sheet 的 range 渲染成简单 PNG（文本截断），返回 base64 字符串。 */
    public static String renderRangeToPngBase64(Workbook wb, org.apache.poi.ss.usermodel.Sheet sheet,
        CellRange range, int maxRows, int maxCols, int maxCellTextLen) throws Exception {
        if (wb == null || sheet == null || range == null) {
            return "";
        }

        int rowCount = range.endRow - range.startRow + 1;
        int colCount = range.endCol - range.startCol + 1;
        if (rowCount <= 0 || colCount <= 0) {
            return "";
        }
        if (rowCount > maxRows || colCount > maxCols) {
            throw new IllegalArgumentException("range too large for screenshot, rows=" + rowCount + ", cols=" + colCount
                + ", maxRows=" + maxRows + ", maxCols=" + maxCols);
        }

        int cellW = 160;
        int cellH = 54;
        int pad = 10;
        int width = colCount * cellW + pad * 2;
        int height = rowCount * cellH + pad * 2;

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        try {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);
            g.setColor(Color.GRAY);

            Font font = new Font("Arial", Font.PLAIN, 12);
            g.setFont(font);

            DataFormatter fmt = new DataFormatter();

            for (int r = 0; r < rowCount; r++) {
                int tr = range.startRow + r;
                for (int c = 0; c < colCount; c++) {
                    int tc = range.startCol + c;
                    int x = pad + c * cellW;
                    int y = pad + r * cellH;
                    g.drawRect(x, y, cellW, cellH);

                    String text = "";
                    Row row = sheet.getRow(tr);
                    if (row != null) {
                        Cell cell = row.getCell(tc);
                        if (cell != null) {
                            text = fmt.formatCellValue(cell);
                        }
                    }
                    text = text == null ? "" : text;
                    text = text.replace("\r", " ").replace("\n", " ");
                    if (text.length() > maxCellTextLen) {
                        text = text.substring(0, maxCellTextLen) + "...";
                    }

                    g.setColor(Color.BLACK);
                    g.drawString(text, x + 6, y + 24);
                    g.setColor(Color.GRAY);
                }
            }
        } finally {
            g.dispose();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            javax.imageio.ImageIO.write(img, "png", baos);
            byte[] bytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(bytes);
        } finally {
            McpUtils.tryClose(baos);
        }
    }

    /** 提取 sheet 的文本值（null 安全 + 单行化）。 */
    public static String getCellText(DataFormatter fmt, Cell cell) {
        if (cell == null) {
            return "";
        }
        if (fmt == null) {
            fmt = new DataFormatter();
        }
        String s = fmt.formatCellValue(cell);
        if (s == null) {
            return "";
        }
        return McpUtils.oneLine(s);
    }

    /** 计算 range 区域的宽高，并把它们限制在一个可截图范围。 */
    public static void validateRangeForScreenshot(CellRange range, int maxRows, int maxCols) {
        if (range == null) {
            throw new IllegalArgumentException("range不能为空");
        }
        int rowCount = range.endRow - range.startRow + 1;
        int colCount = range.endCol - range.startCol + 1;
        if (rowCount <= 0 || colCount <= 0) {
            throw new IllegalArgumentException("range is empty: " + rowCount + "x" + colCount);
        }
        if (rowCount > maxRows || colCount > maxCols) {
            throw new IllegalArgumentException("range too large for screenshot, rows=" + rowCount + ", cols=" + colCount
                + ", maxRows=" + maxRows + ", maxCols=" + maxCols);
        }
    }

    public static void clearRegion(org.apache.poi.ss.usermodel.Sheet sheet, CellRange range) {
        if (sheet == null || range == null) {
            return;
        }
        int rowCount = range.endRow - range.startRow + 1;
        int colCount = range.endCol - range.startCol + 1;
        if (rowCount <= 0 || colCount <= 0) {
            return;
        }
        for (int r = 0; r < rowCount; r++) {
            int tr = range.startRow + r;
            Row row = sheet.getRow(tr);
            if (row == null) {
                continue;
            }
            for (int c = 0; c < colCount; c++) {
                int tc = range.startCol + c;
                Cell cell = row.getCell(tc);
                if (cell != null) {
                    cell.setBlank();
                }
            }
        }
    }

    /** 根据 range 判断能覆盖的行列。 */
    public static CellRange normalizeRange(CellRange range) {
        if (range == null) {
            return null;
        }
        return range;
    }

    /** 默认 sheet range：尽可能取 used range。 */
    public static CellRange calcUsedRange(org.apache.poi.ss.usermodel.Sheet sheet) {
        if (sheet == null) {
            return new CellRange(0, 0, 0, 0);
        }
        int lastRow = sheet.getLastRowNum();
        int lastCol = 0;
        for (int r = 0; r <= lastRow; r++) {
            Row row = sheet.getRow(r);
            if (row == null) {
                continue;
            }
            short lc = row.getLastCellNum();
            if (lc > lastCol) {
                lastCol = lc;
            }
        }
        if (lastRow < 0) {
            lastRow = 0;
        }
        if (lastCol < 0) {
            lastCol = 0;
        }
        int endCol = lastCol <= 0 ? 0 : lastCol - 1;
        return new CellRange(0, lastRow, 0, endCol);
    }

    /** 把一个 styles JSON 节点应用到 range 内的所有单元格。 */
    public static void applyStyleToRange(org.apache.poi.ss.usermodel.Sheet sheet, CellRange range,
        CellStyle style) {
        if (sheet == null || range == null || style == null) {
            return;
        }
        int rowCount = range.endRow - range.startRow + 1;
        int colCount = range.endCol - range.startCol + 1;
        for (int r = 0; r < rowCount; r++) {
            int tr = range.startRow + r;
            Row row = sheet.getRow(tr);
            if (row == null) {
                row = sheet.createRow(tr);
            }
            for (int c = 0; c < colCount; c++) {
                int tc = range.startCol + c;
                Cell cell = row.getCell(tc);
                if (cell == null) {
                    cell = row.createCell(tc);
                }
                cell.setCellStyle(style);
            }
        }
    }
}

