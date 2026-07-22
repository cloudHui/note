package com.gamer.data.excel;

import java.util.ArrayList;
import java.util.List;

// GD 文件头部信息
public class GdHeader {
    public int rows;
    public int columns;
    public int colNameLength;
    public int colTypeLength;
    public List<String> columnNames;
    public List<String> columnTypes;

    public GdHeader() {
        columnNames = new ArrayList<>();
        columnTypes = new ArrayList<>();
    }
}