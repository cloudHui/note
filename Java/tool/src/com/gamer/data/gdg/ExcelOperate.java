package com.gamer.data.gdg;

import com.gamer.data.gd.Utils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.regex.Pattern;

public class ExcelOperate {
    private boolean init = false;
    private File file = null;
    private BufferedInputStream bis = null;
    private Workbook workbook = null;

    public void init(File file) {
        if (file != null && file.isFile()) {
            try {
                if (!file.exists()) {
                    System.out.println("文件不存在: " + file.getName());
                    return;
                }

                this.file = file;
                String fileName = file.getName();
                String fileExt = fileName.substring(fileName.lastIndexOf(".")).toLowerCase().trim();
                this.bis = new BufferedInputStream(Files.newInputStream(file.toPath()));
                if (fileExt.equalsIgnoreCase(".xls")) {
                    this.workbook = new HSSFWorkbook(this.bis);
                } else if (fileExt.equalsIgnoreCase(".xlsx")) {
                    this.workbook = new XSSFWorkbook(this.bis);
                }

                if (this.workbook == null) {
                    throw new FileNotFoundException();
                }

                this.init = true;
            } catch (Exception var4) {
                System.out.println("文件不存在: " + file.getName());
            }

        }
    }

    public void destroy() {
        try {
            if (this.workbook != null) {
                this.workbook.close();
            }

            this.workbook = null;
        } catch (Exception var3) {
            this.workbook = null;
        }

        try {
            if (this.bis != null) {
                this.bis.close();
            }

            this.bis = null;
        } catch (Exception var2) {
            this.bis = null;
        }

        this.file = null;
    }

    public void parseSheet() {
        if (this.init) {
            for (int sheetIndex = 0; sheetIndex < this.workbook.getNumberOfSheets(); ++sheetIndex) {
                Sheet sheet = this.workbook.getSheetAt(sheetIndex);
                if (!checkSheetName(sheet.getSheetName())) {
                    System.out.println(this.file.getName() + " Sheet name error! name: " + sheet.getSheetName());
                    System.out.println();
                } else {
                    int rows = getRealRows(sheet);
                    if (rows < 5) {
                        System.out.printf("error sheet %s rows : %s %n", sheet.getSheetName(), rows);
                        System.out.println();
                    } else {
                        System.out.println(this.file.getName() + " Sheet【" + sheet.getSheetName() + "】  行数【" + rows
                            + "】 开始生成gd......");

                        try {
                            SheetOperate sheetOpt = new SheetOperate(sheet);
                            sheetOpt.setRows(rows - 5);
                            sheetOpt.init();
                            WriteTools.write2Gd(sheetOpt);
                            System.out.println(this.file.getName() + " Sheet【" + sheet.getSheetName() + "】 生成gd文件成功 "
                                + sheet.getSheetName() + ".gd");
                        } catch (Exception e) {
                            System.out.println(this.file.getName() + " Sheet【" + sheet.getSheetName()
                                + "】 生成gd失败. 错误信息: " + e.getMessage());
                        }

                        System.out.println();
                    }
                }
            }

        }
    }

    public static boolean checkSheetName(String name) {
        return Pattern.matches("[a-zA-Z][a-zA-Z_]+", name);
    }

    public static int getRealRows(Sheet sheet) {
        return Utils.getRealRows(sheet);
    }
}
