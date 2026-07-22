package com.gamer.data.gd;

import com.gamer.data.excel.ExcelExtensions;
import com.gamer.data.excel.Log;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;

public class ExcelOperate {
    public boolean init = false;
    public File file;
    private BufferedInputStream bis = null;
    public Workbook workbook = null;

    public ExcelOperate(File file) {
        this.file = file;
    }

    public void init(Log log) {
        if (file != null && file.isFile()) {
            try {
                if (!file.exists()) {
                    log.logMessage("文件不存在: " + file.getName());
                    return;
                }

                String fileName = file.getName();
                String fileExt = fileName.substring(fileName.lastIndexOf(".")).toLowerCase().trim();
                bis = new BufferedInputStream(Files.newInputStream(file.toPath()));
                if (fileExt.equalsIgnoreCase(ExcelExtensions.FILE_EXT_XLS)) {
                    workbook = new HSSFWorkbook(bis);
                } else if (fileExt.equalsIgnoreCase(ExcelExtensions.FILE_EXT_XLSX)) {
                    workbook = new XSSFWorkbook(bis);
                }

                if (workbook == null) {
                    throw new FileNotFoundException();
                }

                init = true;
            } catch (Exception var4) {
                log.logMessage("初始化ExcelOperate失败: " + file.getName());
            }

        }
    }

    public void destroy() {
        try {
            if (workbook != null) {
                workbook.close();
            }

            workbook = null;
        } catch (Exception var3) {
            workbook = null;
        }

        try {
            if (bis != null) {
                bis.close();
            }

            bis = null;
        } catch (Exception var2) {
            bis = null;
        }

        file = null;
    }

    public void parseSheet(Log log) {
        if (init) {
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); ++sheetIndex) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                if (Utils.checkNotFitSheetName(sheet.getSheetName())) {
                    continue;
                }
                int rows = Utils.getRealRows(sheet);
                if (rows < 5) {
                    log.logMessage("error sheet " + sheet.getSheetName() + " rows : " + rows + " %n", true);
                    continue;
                }
                log.logMessage(file.getName() + " Sheet[" + sheet.getSheetName() + "] 行数[" + rows + "] 开始生成gd......");
                try {
                    SheetOperate sheetOpt = new SheetOperate(sheet);
                    sheetOpt.totalRows = rows;
                    sheetOpt.rows = rows - 5;
                    sheetOpt.init(log);
                    WriteTools.write2Gd(sheetOpt);
                    log.logMessage(file.getName() + " Sheet[" + sheet.getSheetName() + "] 生成gd文件成功 -> "
                        + sheet.getSheetName() + ".gd");
                } catch (Exception e) {
                    log.logMessage(
                        file.getName() + " Sheet[" + sheet.getSheetName() + "] 生成gd失败. 错误信息: " + e, true);
                }
            }
        }
    }
}
