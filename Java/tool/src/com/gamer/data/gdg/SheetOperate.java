package com.gamer.data.gdg;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class SheetOperate {
    private int columns;
    private int rows;
    private List<String[]> dataRows;
    private final Set<Integer> vindex = new HashSet<>();
    private Sheet sheet;
    private List<String[]> headRows;

    public int getColumns() {
        return this.columns;
    }

    public int getRows() {
        return this.rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public List<String[]> getDataRows() {
        return this.dataRows;
    }

    public List<String[]> getHeadRows() {
        return this.headRows;
    }

    public Sheet getSheet() {
        return this.sheet;
    }

    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    public SheetOperate(Sheet sheet) {
        this.sheet = sheet;
    }

    private List<String[]> getData(int from, int to, boolean headData) throws Exception {
        List<String[]> data = new ArrayList<>();
        int columns = 0;

        for (int rowIdx = from - 1; rowIdx <= to - 1; ++rowIdx) {
            Row row = this.sheet.getRow(rowIdx);

            if (headData) {
                columns = columns == 0 ? this.getRealColumns(row) : columns;
            } else {
                columns = this.columns;
            }

            String value;
            String[] perRow = new String[columns];
            Arrays.fill(perRow, "");
            if (row != null) {
                for (int columnIdx = 0; columnIdx < columns; ++columnIdx) {
                    value = "";
                    Cell cell = row.getCell(columnIdx);
                    if (cell != null) {
                        switch (cell.getCellType()) {
                            case NUMERIC:
                            case STRING:
                                cell.setCellType(CellType.STRING);
                                value = cell.getStringCellValue();
                                if (headData) {
                                    break;
                                }

                                try {
                                    DataType tp = this.getColumnType(columnIdx);
                                    if (tp != DataType.TYPE_INT && tp != DataType.TYPE_V_IDX) {
                                        if (tp == DataType.TYPE_FLOAT) {
                                            DecimalFormat df = new DecimalFormat("0.0000");
                                            value = df.format(Double.parseDouble(value));
                                        }
                                        break;
                                    }

                                    value = String.valueOf(Integer.parseInt(value));
                                    break;
                                } catch (Exception e) {
                                    throw new Exception(String.format("数值类型转换错误(%s),Sheet(%s),第%s行,第%s列 值:%s",
                                        e.getMessage(), this.sheet.getSheetName(), rowIdx + 1, columnIdx + 1, value));
                                }
                            case FORMULA:
                                throw new Exception(String.format("请使用公式计算出的数字值,不要使用公式,Sheet(%s),第%s行,第%s列 值:%s",
                                    this.sheet.getSheetName(), rowIdx + 1, columnIdx + 1, value));
                            case BOOLEAN:
                                value = cell.getBooleanCellValue() ? "Y" : "N";
                                break;
                            case ERROR:
                                throw new Exception(String.format("CELL_TYPE_ERROR,Sheet(%s),第%s行,第%s列 值:%s",
                                    this.sheet.getSheetName(), rowIdx + 1, columnIdx + 1, value));
                            default:
                                value = "";
                        }
                    }

                    if (!headData) {
                        String range;
                        String[] r;
                        try {
                            range = ((String[])this.getHeadRows().get(4))[columnIdx];
                            r = range.split("~");
                        } catch (Exception var20) {
                            throw new Exception(String.format("读取列错误,Sheet(%s),第%s行,第%s列,列类型:float,值:%s",
                                this.sheet.getSheetName(), rowIdx + 1, columnIdx + 1, value));
                        }

                        if (((String[])this.getHeadRows().get(2))[columnIdx].isEmpty()) {
                            continue;
                        }

                        if (DataType
                            .parseName(((String[])this.getHeadRows().get(2))[columnIdx]) == DataType.TYPE_FLOAT) {
                            float f;

                            try {
                                f = Float.parseFloat(value);
                            } catch (Exception var24) {
                                throw new Exception(String.format("数据类型错误,Sheet(%s),第%s行,第%s列,列类型:float,值:%s",
                                    this.sheet.getSheetName(), rowIdx + 1, columnIdx + 1, value));
                            }

                            float indA;
                            float indB;

                            try {
                                indA = Float.parseFloat(r[0]);
                                indB = Float.parseFloat(r[1]);
                            } catch (Exception var23) {
                                throw new Exception(String.format("范围值错误,Sheet(%s),第%s行,第%s列,列类型:float,值:%s,范围:%s",
                                    this.sheet.getSheetName(), rowIdx + 1, columnIdx + 1, value, range));
                            }

                            if (f < indA || f > indB) {
                                throw new Exception(String.format("超出数值范围,Sheet(%s),第%s行,第%s列,列类型:float,值:%s,范围:%s",
                                    this.sheet.getSheetName(), rowIdx + 1, columnIdx + 1, value, range));
                            }
                        } else if (DataType
                            .parseName(((String[])this.getHeadRows().get(2))[columnIdx]) == DataType.TYPE_INT) {
                            int i;
                            if (value.contains(".")) {
                                value = value.substring(0, value.indexOf("."));
                            }

                            try {
                                i = Integer.parseInt(value);
                            } catch (Exception var22) {
                                throw new Exception(String.format("数据类型错误,Sheet(%s),第%s行,第%s列,列类型:int,值:%s",
                                    this.sheet.getSheetName(), rowIdx + 1, columnIdx + 1, value));
                            }

                            int indA;
                            int indB;

                            try {
                                indA = Integer.parseInt(r[0]);
                                indB = Integer.parseInt(r[1]);
                            } catch (Exception var21) {
                                throw new Exception(String.format("范围值错误,Sheet(%s),第%s行,第%s列,列类型:int,值:%s,范围:%s",
                                    this.sheet.getSheetName(), rowIdx + 1, columnIdx + 1, value, range));
                            }

                            if (i < indA || i > indB) {
                                throw new Exception(String.format("超出数值范围,Sheet(%s),第%s行,第%s列,列类型:int,值:%s,范围:%s",
                                    this.sheet.getSheetName(), rowIdx + 1, columnIdx + 1, value, range));
                            }
                        } else if (DataType
                            .parseName(((String[])this.getHeadRows().get(2))[columnIdx]) == DataType.TYPE_V_IDX) {
                            int indx;
                            if (value.contains(".")) {
                                value = value.substring(0, value.indexOf("."));
                            }

                            try {
                                indx = Integer.parseInt(value);
                            } catch (Exception var19) {
                                throw new Exception(String.format("数据类型错误,Sheet(%s),第%s行,第%s列,列类型:vindex,值:%s",
                                    this.sheet.getSheetName(), rowIdx + 1, columnIdx + 1, value));
                            }

                            if (this.vindex.contains(indx)) {
                                throw new Exception(String.format("唯一列值重复,Sheet(%s),第%s行,第%s列,列类型:vindex,值:%s",
                                    this.sheet.getSheetName(), rowIdx + 1, columnIdx + 1, value));
                            }

                            int indSmall;
                            int indBig;

                            try {
                                indSmall = Integer.parseInt(r[0]);
                                indBig = Integer.parseInt(r[1]);
                            } catch (Exception var18) {
                                throw new Exception(String.format("范围值错误,Sheet(%s),第%s行,第%s列,列类型:vindex,值:%s,范围:%s",
                                    this.sheet.getSheetName(), rowIdx + 1, columnIdx + 1, value, range));
                            }

                            if (indx < indSmall || indx > indBig) {
                                throw new Exception(String.format("超出数值范围,Sheet(%s),第%s行,第%s列,列类型:vindex,值:%s,范围:%s",
                                    this.sheet.getSheetName(), rowIdx + 1, columnIdx + 1, value, range));
                            }

                            this.vindex.add(indx);
                        }
                    }

                    perRow[columnIdx] = value;
                }
            }

            data.add(perRow);
        }

        return data;
    }

    public void init() throws Exception {
        this.headRows = this.getData(1, 5, true);
        checkColumnNameSame((String[])this.headRows.get(0));
        this.columns = storeHeader(this.headRows);
        if (this.columns < 0) {
            throw new Exception("解析出错");
        } else {
            this.dataRows = this.getData(6, this.rows + 5, false);
        }
    }

    public int getRealColumns(Row row) {
        int columns = row.getLastCellNum();

        for (int i = 0; i < columns; ++i) {
            Cell cell = row.getCell(i);
            if (cell == null) {
                return i;
            }

            cell.setCellType(CellType.STRING);
            if (cell.getStringCellValue() == null || cell.getStringCellValue().isEmpty()) {
                return i;
            }
        }

        return columns;
    }

    public DataType getColumnType(int col) throws Exception {
        if (this.headRows.size() > 3) {
            DataType returnType;

            try {
                returnType = DataType.parseName(((String[])this.headRows.get(2))[col]);
                return returnType;
            } catch (Exception e) {
                throw new Exception("获取列类型异常: " + e.getMessage());
            }
        } else {
            throw new Exception("表头行数 < 3");
        }
    }

    public static int storeHeader(List<String[]> header) {
        try {
            String[] columnNames = header.get(0);
            String[] types = header.get(2);
            String[] ranges = header.get(4);
            int columns = 0;

            for (int i = 0; i < types.length; ++i) {
                if (types[i] == null || types[i].isEmpty()) {
                    System.out.printf("列类型不能为空(第3行,第%s列: %s)%n", i + 1, types[i]);
                    return -1;
                }

                if (types[i].startsWith("_")) {
                    types[i] = "";
                    columnNames[i] = "";
                    ranges[i] = "";
                } else {
                    if (DataType.parseName(types[i]) == null) {
                        System.out.printf("列类型拼写错误(第3行,第%s列: %s)%n", i + 1, types[i]);
                        return -1;
                    }

                    if (isCurrentInternational(columnNames[i], DataBuilder.LANGUAGE)) {
                        columnNames[i] = getRealColName(columnNames[i]);
                    } else if (isInternational(columnNames[i])) {
                        types[i] = "";
                        columnNames[i] = "";
                        ranges[i] = "";
                    }
                }

                ++columns;
            }

            return columns;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static void checkColumnNameSame(String[] cols) throws Exception {
        for (String col : cols) {
            int count = 0;

            for (String s : cols) {
                if (isInternational(col)) {
                    String cName = getRealColName(col);
                    if (s.equals(cName)) {
                        ++count;
                        if (count > 1) {
                            throw new Exception("列名重复: " + col);
                        }
                    }
                } else if (isInternational(s)) {
                    String rName = getRealColName(s);
                    if (rName.equals(col)) {
                        ++count;
                        if (count > 1) {
                            throw new Exception("列名重复: " + col);
                        }
                    }
                } else if (s.equals(col)) {
                    ++count;
                    if (count > 1) {
                        throw new Exception("列名重复: " + col);
                    }
                }
            }
        }
    }

    public static boolean isCurrentInternational(String colName, String locale) {
        return colName != null && colName.endsWith("(" + locale + ")");
    }

    public static String getRealColName(String colName) {
        return colName != null && colName.length() > 4 ? colName.substring(0, colName.length() - 4) : "";
    }

    public static boolean isInternational(String name) {
        return Pattern.matches("[a-zA-Z][a-zA-Z_0-9]+([(][a-zA-Z]{2}[)])", name);
    }
}
