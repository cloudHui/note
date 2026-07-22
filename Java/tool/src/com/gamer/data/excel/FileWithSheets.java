package com.gamer.data.excel;

import com.gamer.data.gen.Title;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件与Sheet 列表的数据类
 */
public class FileWithSheets {
    public String excelName;
    // sheets是map 存储sheet名和sheet内容的列表
    public Map<String, List<Title>> sheets = new HashMap<>();

    public FileWithSheets(String excelName) {
        this.excelName = excelName;
    }

    /**
     * 获取sheet 内容
     * 
     * @param sheetName
     *            sheet 名称
     * @return 返回sheet 内容列表
     */
    public List<Title> getSheet(String sheetName) {
        return sheets.computeIfAbsent(sheetName, k -> new ArrayList<>());
    }

    /**
     * 添加sheet 内容
     * 
     * @param sheetName
     *            sheet 名称
     * @param title
     *            添加的标题
     */
    public void addSheet(String sheetName, Title title) {
        getSheet(sheetName).add(title);
    }

    @Override
    public String toString() {
        //把sheets按key value 格式化成字符串
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<Title>> entry : sheets.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return "\nFileWithSheets{" + "fileName='" + excelName + '\n' + ", sheets=" + sb + '}';
    }
}