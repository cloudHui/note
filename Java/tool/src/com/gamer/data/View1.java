package com.gamer.data;

import com.gamer.data.excel.view.DiffCheck;

/**
 * View一项目
 *
 * @author liuyunhui
 * @date 2025/12/26
 */
public class View1 {
    public static String GD_PATH = "/GD/china";

    public static String XML_PATH = "/ConfigurationExcel";

    public static void main(String[] args) {
        new DiffCheck().view(GD_PATH, XML_PATH, true);
    }
}
