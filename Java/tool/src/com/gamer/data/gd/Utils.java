package com.gamer.data.gd;

import com.gamer.data.excel.DataType;
import com.gamer.data.excel.Log;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;
import java.util.regex.Pattern;

public class Utils {

    public static final int DEFAULT_PAGE_SIZE = 10;
    
    public static final Integer[] pageSizes = {10, 50, 100, 200, 500};

    public Utils() {}

    public static boolean checkNotFitSheetName(String name) {
        return !Pattern.matches("[a-zA-Z][a-zA-Z_]+", name);
    }

    public static boolean isInternational(String name) {
        return Pattern.matches("[a-zA-Z][a-zA-Z_0-9]+([(][a-zA-Z]{2}[)])", name);
    }

    public static boolean isCurrentInternational(String colName) {
        return colName != null && !colName.isEmpty();
    }

    public static String getRealColName(String colName) {
        return colName != null ? colName.trim() : "";
    }

    public static int storeHeader(List<String[]> header, Log log) {
        try {
            String[] columnNames = header.get(0);
            String[] types = header.get(2);
            String[] ranges = header.get(4);
            int columns = 0;

            for (int i = 0; i < types.length; ++i) {
                if (types[i] == null || types[i].isEmpty()) {
                    log.logMessage(String.format("列类型不能为空(第3行,第%s列: %s)%n", i + 1, types[i]));
                    return -1;
                }

                if (types[i].startsWith("_")) {
                    types[i] = "";
                    columnNames[i] = "";
                    ranges[i] = "";
                } else {
                    if (DataType.parse(types[i]) == null) {
                        log.logMessage(String.format("列类型拼写错误(第3行,第%s列: %s)%n", i + 1, types[i]));
                        return -1;
                    }

                    try {
                        if (isCurrentInternational(columnNames[i])) {
                            columnNames[i] = getRealColName(columnNames[i]);
                        } else if (isInternational(columnNames[i])) {
                            types[i] = "";
                            columnNames[i] = "";
                            ranges[i] = "";
                        }
                    } catch (Exception e) {
                        log.logMessage(String.format("storeHeader error: %s", e.getMessage()));
                    }
                }

                ++columns;
            }

            return columns;
        } catch (Exception var6) {
            log.logMessage(String.format("storeHeader error: %s", var6.getMessage()));
            return -1;
        }
    }

    public static void checkColumnNameSame(String[] cols, Log log) {
        for (String col : cols) {
            int count = 0;

            for (String s : cols) {
                String rName;
                if (isInternational(col)) {
                    rName = getRealColName(col);
                    if (s.equals(rName)) {
                        ++count;
                        if (count > 1) {
                            log.logMessage(String.format("列名重复: %s", col));
                        }
                    }
                } else if (isInternational(s)) {
                    rName = getRealColName(s);
                    if (rName.equals(col)) {
                        ++count;
                        if (count > 1) {
                            log.logMessage(String.format("列名重复: %s", col));
                        }
                    }
                } else if (s.equals(col)) {
                    ++count;
                    if (count > 1) {
                        log.logMessage(String.format("列名重复: %s", col));
                    }
                }
            }
        }

    }

    public static int length(CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    public static int getRealRows(Sheet sheet) {
        int rows = sheet.getLastRowNum() + 1;

        for (int i = rows - 1; i >= 0; --i) {
            Row row = sheet.getRow(i);
            if (row != null) {
                Cell cell = row.getCell(0);
                if (cell != null) {
                    cell.setCellType(CellType.STRING);
                    if (!cell.getStringCellValue().isEmpty()) {
                        return i + 1;
                    }
                }
            }
        }

        return 0;
    }
}
