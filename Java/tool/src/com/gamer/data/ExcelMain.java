package com.gamer.data;

import com.gamer.data.excel.DataBuilderCheck;
import com.gamer.data.message.Util;

/**
 * ExcelMain
 *
 * @author liuyunhui
 * @date 2025/12/5
 */
public class ExcelMain {
    public static String GD_PATH = "/Document/DevelopmentGD&ConfigurationExcel/GD/data";

    public static String XML_PATH = "/Document/DevelopmentGD&ConfigurationExcel/ConfigurationExcel";

    public static void main(String[] args) {
        new DataBuilderCheck().view(GD_PATH, XML_PATH, Util.inVM());
    }
}
