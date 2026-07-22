package com.gamer.data.gd;

import com.gamer.data.excel.DataType;
import com.gamer.data.excel.Log;
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

public class SheetOperate {
    public int columns;
    public int rows;// 数据行数
    public int totalRows;// 总行数
    public List<String[]> dataRows;
    public final Set<Integer> vindex = new HashSet<>();
    public Sheet sheet;
    public List<String[]> headRows;
    public String sheetName;

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public List<String[]> getDataRows() {
        return dataRows;
    }

    public List<String[]> getHeadRows() {
        return headRows;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public SheetOperate(Sheet sheet) {
        this.sheet = sheet;
        sheetName = sheet.getSheetName();
    }

    private List<String[]> getData(int from, int to, boolean headData, Log log) {
        List<String[]> data = new ArrayList<>();
        int columns = 0;

        for (int rowIdx = from; rowIdx < to; ++rowIdx) {
            Row row = sheet.getRow(rowIdx);

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
                                    DataType tp = getColumnType(columnIdx, log);
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
                                    log.logMessage("数值类型转换错误(" + e.getMessage() + "),Sheet(" + this.sheet.getSheetName()
                                        + "),第" + rowIdx + 1 + ",第" + columnIdx + 1 + ",列类型:float,值:" + value, true);
                                }
                            case FORMULA:
                                log.logMessage("请使用公式计算出的数字值,不要使用公式,Sheet(" + this.sheet.getSheetName() + "),第" + rowIdx
                                    + 1 + ",第" + columnIdx + 1 + ",列类型:float,值:" + value, true);
                            case BOOLEAN:
                                value = cell.getBooleanCellValue() ? "Y" : "N";
                                break;
                            case ERROR:
                                log.logMessage("CELL_TYPE_ERROR,Sheet(" + this.sheet.getSheetName() + "),第" + rowIdx + 1
                                    + ",第" + columnIdx + 1 + ",列类型:float,值:" + value, true);
                            default:
                                value = "";
                        }
                    }

                    if (!headData) {
                        String range = "";
                        String[] r = null;
                        try {
                            range = ((String[])this.getHeadRows().get(4))[columnIdx];
                            r = range.split("~");
                        } catch (Exception var20) {
                            log.logMessage("读取列错误,Sheet(" + this.sheet.getSheetName() + "),第" + rowIdx + 1 + ",第"
                                + columnIdx + 1 + ",列类型:float,值:" + value, true);
                        }

                        if (((String[])this.getHeadRows().get(2))[columnIdx].isEmpty()) {
                            continue;
                        }

                        if (DataType.parse(((String[])this.getHeadRows().get(2))[columnIdx]) == DataType.TYPE_FLOAT) {
                            float f = 0;

                            try {
                                f = Float.parseFloat(value);
                            } catch (Exception var24) {
                                log.logMessage("数据类型错误,Sheet(" + this.sheet.getSheetName() + "),第" + rowIdx + 1 + ",第"
                                    + columnIdx + 1 + ",列类型:float,值:" + value);
                            }

                            float indA = 0;
                            float indB = 0;

                            try {
                                indA = Float.parseFloat(r[0]);
                                indB = Float.parseFloat(r[1]);
                            } catch (Exception var23) {
                                log.logMessage("范围值错误,Sheet(" + this.sheet.getSheetName() + "),第" + rowIdx + 1 + ",第"
                                    + columnIdx + 1 + ",列类型:float,值:" + value + ",范围:" + range, true);
                            }

                            if (f < indA || f > indB) {
                                log.logMessage("超出数值范围,Sheet(" + this.sheet.getSheetName() + "),第" + rowIdx + 1 + ",第"
                                    + columnIdx + 1 + ",列类型:float,值:" + value + ",范围:" + range, true);
                            }
                        } else if (DataType
                            .parse(((String[])this.getHeadRows().get(2))[columnIdx]) == DataType.TYPE_INT) {
                            int i = 0;
                            if (value.contains(".")) {
                                value = value.substring(0, value.indexOf("."));
                            }

                            try {
                                i = Integer.parseInt(value);
                            } catch (Exception var22) {
                                log.logMessage("数据类型错误,Sheet(" + this.sheet.getSheetName() + "),第" + rowIdx + 1 + ",第"
                                    + columnIdx + 1 + ",列类型:int,值:" + value, true);
                            }

                            int indA = 0;
                            int indB = 0;

                            try {
                                indA = Integer.parseInt(r[0]);
                                indB = Integer.parseInt(r[1]);
                            } catch (Exception var21) {
                                log.logMessage("范围值错误,Sheet(" + this.sheet.getSheetName() + "),第" + rowIdx + 1 + ",第"
                                    + columnIdx + 1 + ",列类型:int,值:" + value + ",范围:" + range, true);
                            }

                            if (i < indA || i > indB) {
                                log.logMessage("超出数值范围,Sheet(" + this.sheet.getSheetName() + "),第" + rowIdx + 1 + ",第"
                                    + columnIdx + 1 + ",列类型:int,值:" + value + ",范围:" + range, true);
                            }
                        } else if (DataType
                            .parse(((String[])this.getHeadRows().get(2))[columnIdx]) == DataType.TYPE_V_IDX) {
                            int indx = 0;
                            if (value.contains(".")) {
                                value = value.substring(0, value.indexOf("."));
                            }

                            try {
                                indx = Integer.parseInt(value);
                            } catch (Exception var19) {
                                log.logMessage("数据类型错误,Sheet(" + this.sheet.getSheetName() + "),第" + rowIdx + 1 + ",第"
                                    + columnIdx + 1 + ",列类型:vindex,值:" + value, true);
                            }

                            if (this.vindex.contains(indx)) {
                                log.logMessage("唯一列值重复,Sheet(" + this.sheet.getSheetName() + "),第" + rowIdx + 1 + ",第"
                                    + columnIdx + 1 + ",列类型:vindex,值:" + value, true);
                            }

                            int indSmall = 0;
                            int indBig = 0;

                            try {
                                indSmall = Integer.parseInt(r[0]);
                                indBig = Integer.parseInt(r[1]);
                            } catch (Exception var18) {
                                log.logMessage("范围值错误,Sheet(" + this.sheet.getSheetName() + "),第" + rowIdx + 1 + ",第"
                                    + columnIdx + 1 + ",列类型:vindex,值:" + value + ",范围:" + range, true);
                            }

                            if (indx < indSmall || indx > indBig) {
                                log.logMessage("超出数值范围,Sheet(" + this.sheet.getSheetName() + "),第" + rowIdx + 1 + ",第"
                                    + columnIdx + 1 + ",列类型:vindex,值:" + value + ",范围:" + range, true);
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

    public void init(Log log) {
        headRows = getData(0, 5, true, log);
        Utils.checkColumnNameSame(headRows.get(0), log);
        columns = Utils.storeHeader(headRows, log);
        if (totalRows == 0 || rows == 0) {
            totalRows = Utils.getRealRows(sheet);
            rows = totalRows - 5;
            if (rows <= 0) {
                log.logMessage("SheetOperate->init 总行数或数据行数小于5,解析出错 " + sheet.getSheetName(), true);
                return;
            }
        }
        if (columns < 0) {
            log.logMessage("SheetOperate->init 列数小于0,解析出错 " + sheet.getSheetName(), true);
        } else {
            dataRows = getData(5, totalRows, false, log);
        }
    }

    public int getRealColumns(Row row) {
        for (int i = 0; i < row.getLastCellNum(); ++i) {
            Cell cell = row.getCell(i);
            if (cell == null) {
                return i;
            }

            cell.setCellType(CellType.STRING);
            if (cell.getStringCellValue() == null || cell.getStringCellValue().isEmpty()) {
                return i;
            }
        }

        return row.getLastCellNum();
    }

    public DataType getColumnType(int col, Log log) {
        if (headRows.size() > 3) {
            try {
                return DataType.parse(((String[])headRows.get(2))[col]);
            } catch (Exception e) {
                log.logMessage("获取列类型异常: " + e.getMessage(), true);
                return null;
            }
        } else {
            log.logMessage("表头行数 < 3", true);
            return null;
        }
    }

    public void close() {
        this.sheet = null;
    }
}
