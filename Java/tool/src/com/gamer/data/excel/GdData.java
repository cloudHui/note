package com.gamer.data.excel;

import java.util.ArrayList;
import java.util.List;

/**
 * GD 文件数据
 */
public class GdData {
    public GdHeader header;
    public List<Object[]> dataRows;

    public GdData(GdHeader header, List<Object[]> dataRows) {
        this.header = header;
        this.dataRows = dataRows;
    }

    public int getRowCount() {
        return dataRows != null ? dataRows.size() : 0;
    }

    public int getColumnCount() {
        return header != null && header.columnNames != null ? header.columnNames.size() : 0;
    }

    public String getColumnName(int index) {
        if (header == null || header.columnNames == null || index < 0 || index >= header.columnNames.size()) {
            return "列" + (index + 1);
        }
        return header.columnNames.get(index);
    }

    public String getColumnType(int index) {
        if (header == null || header.columnTypes == null || index < 0 || index >= header.columnTypes.size()) {
            return "?";
        }
        return header.columnTypes.get(index);
    }

    public String getDataTypeName(String shortType) {
        switch (shortType) {
            case "s":
                return "字符串";
            case "f":
                return "浮点数";
            case "i":
                return "整数";
            case "v":
                return "V索引";
            default:
                return "未知";
        }
    }

    public String getFormattedColumnHeader(int index) {
        String colName = getColumnName(index);
        String colType = getColumnType(index);
        String typeName = getDataTypeName(colType);
        return String.format("%s (%s)", colName, typeName);
    }

    /**
     * 获取GD文件的列信息列表
     */
    public List<Object[]> getColumnInfo() {
        List<Object[]> columnInfo = new ArrayList<>();
        for (int i = 0; i < getColumnCount(); i++) {
            String sampleValue = getSampleValue(i);
            columnInfo.add(new Object[] {i + 1, getColumnName(i), getDataTypeName(getColumnType(i)), sampleValue});
        }
        return columnInfo;
    }

    /**
     * 获取示例值
     */
    private String getSampleValue(int columnIndex) {
        if (dataRows == null || dataRows.isEmpty()) {
            return "[空]";
        }

        for (int i = 0; i < Math.min(5, getRowCount()); i++) {
            Object[] row = dataRows.get(i);
            if (columnIndex < row.length) {
                Object value = row[columnIndex];
                if (value != null) {
                    String strValue = value.toString();
                    if (!strValue.trim().isEmpty()) {
                        return strValue.length() > 20 ? strValue.substring(0, 20) + "..." : strValue;
                    }
                }
            }
        }

        return "[空]";
    }
}