package com.gamer.data;

import com.gamer.data.excel.view.DiffCheck;
import com.gamer.data.message.Util;

/**
 * View
 *
 * @author liuyunhui
 * @date 2025/12/26
 */
public class View {
    public static String GD_PATH = "/GD/data";

    public static String XML_PATH = "/ConfigurationExcel";

    public static void main(String[] args) {
        // 这个单独的工具就在 ConfigurationExcel/xls2gd_zh 目录下

        if (!Util.inVM()) {
            GD_PATH = ExcelMain.GD_PATH;
            XML_PATH = ExcelMain.XML_PATH;
        }
        new DiffCheck().view(GD_PATH, XML_PATH, false);
    }
}
