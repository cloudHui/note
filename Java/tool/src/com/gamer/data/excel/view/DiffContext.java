package com.gamer.data.excel.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Excel 差异对比上下文：封装源/当前目录两份 Excel 数据的公共结构
 */
public class DiffContext {
    public final List<Object[]> allData1 = new ArrayList<>();
    public final List<Object[]> allData2 = new ArrayList<>();

    public final Map<Integer, Integer> rowMapping1 = new HashMap<>();
    public final Map<Integer, Integer> rowMapping2 = new HashMap<>();

    public String[] columnNames1;
    public String[] columnNames2;

    public final Set<String> differenceCells = new HashSet<>();
    public final Set<Integer> differenceRows = new HashSet<>();
}
