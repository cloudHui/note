package com.gamer.data.excel;

import com.gamer.data.excel.view.DataPanelConfig;
import com.gamer.data.excel.view.DiffContext;
import com.gamer.data.excel.view.PaginationController;
import com.gamer.data.excel.view.PanelCreationConfig;
import com.gamer.data.gd.ExcelOperate;
import com.gamer.data.gd.SheetOperate;
import com.gamer.data.gd.Utils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class AbstractViewFrame extends JFrame {
    public JList<String> fileList;
    public DefaultListModel<String> listModel;
    public JTextPane logArea;
    public final BaseCheck baseCheck;
    // 当前选中的Excel 和GD 数据
    public ExcelOperate currentExcelOperate; // 当前目录下的 Excel
    // 差异单元格标记集合（格式：行,列）key: fileName, value: sheetDataList
    public final Map<String, List<SheetOperate>> excelDataCache = new HashMap<>();
    // Sheet选择器（四个面板共用）
    public JComboBox<String> sheetSelector;
    public DefaultComboBoxModel<String> sheetSelectorModel;
    // 添加文件映射，用于将显示名称映射回ExcelOperate对象
    public final Map<String, ExcelOperate> fileMap = new HashMap<>(); // 当前目录Excel 文件映射

    // 同步标志
    public SyncLabel syncLabel = new SyncLabel();
    // 左侧面板新增组件
    public JPanel excelFileDisplayPanel; // Excel 文件显示面板
    // Excel / GD 对比上下文（封装两组数据结构）
    public final DiffContext excelDiffContext = new DiffContext();
    public final DiffContext gdDiffContext = new DiffContext();

    // 通用分页控制器（四个面板复用同一套逻辑）
    public PaginationController excelPagination1;
    public PaginationController excelPagination2;
    public PaginationController gdPagination1;
    public PaginationController gdPagination2;

    public AbstractViewFrame(BaseCheck baseCheck) {
        this.baseCheck = baseCheck;
    }

    public JScrollPane init() {
        // Excel 文件列表
        listModel = new DefaultListModel<>();
        fileList = new JList<>(listModel);
        fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fileList.setFont(new Font("宋体", Font.PLAIN, 14));
        fileList.setFixedCellHeight(30);

        // 文件列表选择监听
        fileList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = fileList.getSelectedIndex();
                if (selectedIndex >= 0 && selectedIndex < listModel.size()) {
                    // 获取选中的显示名称
                    String displayName = listModel.getElementAt(selectedIndex);
                    // 根据显示名称获取对应的ExcelOperate 对象
                    ExcelOperate selectedOperate = getExcelOperateByDisplayName(displayName);
                    if (selectedOperate != null) {
                        loadFileDetails(selectedOperate);
                    } else {
                        logMessage("无法找到对应的Excel文件: " + displayName);
                    }
                }
            }
        });

        return new JScrollPane(fileList);
    }

    public Consumer<Integer> PAGE_CHANGE = this::showExcelPage;

    public Consumer<Integer> PREV_PAGE_CHANGE = pageSize -> {
        if (syncLabel.isSyncingPagination) {
            return;
        }

        // 检查两个面板是否都有数据
        boolean hasData1 = excelDiffContext.columnNames1 != null && !excelDiffContext.allData1.isEmpty();
        boolean hasData2 = excelDiffContext.columnNames2 != null && !excelDiffContext.allData2.isEmpty();

        if (hasData1 && hasData2) {
            // 两个面板都有数据，同步变更
            syncLabel.isSyncingPagination = true;
            try {
                if (excelPagination2 != null) {
                    excelPagination2.setPageSize(pageSize);
                }
                showExcelPage(1);
                displayExcelPage2(1);
            } finally {
                syncLabel.isSyncingPagination = false;
            }
        } else if (hasData1) {
            // 只有面板1有数据，独立变更
            excelPagination1.setPageSize(pageSize);
            showExcelPage(1);
        } else if (hasData2) {
            // 只有面板2有数据，独立变更
            excelPagination2.setPageSize(pageSize);
            displayExcelPage2(1);
        }
    };

    public Consumer<Integer> PAGE_CHANGE_GD = this::showGdPage;

    public Consumer<Integer> PREV_PAGE_CHANGE_GD = pageSize -> {
        if (syncLabel.isSyncingPagination) {
            return;
        }

        // 检查两个面板是否都有数据
        boolean hasData1 = gdDiffContext.columnNames1 != null && !gdDiffContext.allData1.isEmpty();
        boolean hasData2 = gdDiffContext.columnNames2 != null && !gdDiffContext.allData2.isEmpty();

        if (hasData1 && hasData2) {
            // 两个面板都有数据，同步变更
            syncLabel.isSyncingPagination = true;
            try {
                // 设置两个面板的页大小
                if (gdPagination1 != null) {
                    gdPagination1.setPageSize(pageSize);
                }
                if (gdPagination2 != null) {
                    gdPagination2.setPageSize(pageSize);
                }

                // 重新显示第一页，两个面板都显示
                showGdPage(1);
                displayGdPage2(1);
            } finally {
                syncLabel.isSyncingPagination = false;
            }
        } else if (hasData1) {
            // 只有面板1有数据，独立变更
            if (gdPagination1 != null) {
                gdPagination1.setPageSize(pageSize);
                showGdPage(1);
            }
        } else if (hasData2) {
            // 只有面板2有数据，独立变更
            if (gdPagination2 != null) {
                gdPagination2.setPageSize(pageSize);
                displayGdPage2(1);
            }
        }
    };

    /**
     * 显示指定页的GD数据2 -
     */
    public void displayGdPage2(int page) {
        if (gdPagination2 == null || gdDiffContext.columnNames2 == null) {
            return;
        }

        // 计算总页数
        int totalRows = gdDiffContext.allData2.size();
        int totalPages = calculateTotalPages(totalRows, gdPagination2.pageSize);

        // 确保页码在有效范围内
        page = Math.max(1, Math.min(page, totalPages));

        int startIndex = (page - 1) * gdPagination2.pageSize;
        int endIndex = Math.min(startIndex + gdPagination2.pageSize, totalRows);

        if (gdPagination2.tableModel != null && gdPagination2.table != null) {
            gdPagination2.tableModel.setRowCount(0);
            gdPagination2.tableModel.setColumnCount(gdDiffContext.columnNames2.length);
            gdPagination2.tableModel.setColumnIdentifiers(gdDiffContext.columnNames2);

            gdPagination2.table.putClientProperty("startRow", startIndex);

            for (int i = startIndex; i < endIndex; i++) {
                gdPagination2.tableModel.addRow(gdDiffContext.allData2.get(i));
            }

            // 更新行数和列数标签
            if (gdPagination2.rowLabel != null) {
                gdPagination2.rowLabel.setText(String.format("行数: %d (第 %d/%d 页)", totalRows, page, totalPages));
            }
            if (gdPagination2.colLabel != null) {
                gdPagination2.colLabel.setText("列数: " + gdDiffContext.columnNames2.length);
            }
        }

        // 更新分页控制器状态
        gdPagination2.currentPage = page;
        gdPagination2.totalPages = totalPages;
        if (gdPagination2.pageSpinner != null) {
            gdPagination2.pageSpinner.setValue(page);
        }

        gdPagination2.updateControls(page, totalPages);

        // 设置列宽
        if (gdPagination2.table != null) {
            for (int i = 0; i < gdDiffContext.columnNames2.length; i++) {
                gdPagination2.table.getColumnModel().getColumn(i).setPreferredWidth(150);
            }
        }

        refreshGdTables();

        // 同步另一个分页控制器（如果有数据）
        if (!syncLabel.isSyncingPagination && gdDiffContext.columnNames1 != null && !gdDiffContext.allData1.isEmpty()) {
            syncGdPaginationForPanel2();
        }
    }

    /**
     * 从GD面板2同步到GD面板1 - 新增方法
     */
    public void syncGdPaginationForPanel2() {
        if (syncLabel.isSyncingPagination) {
            return;
        }

        // 检查两个面板是否都有数据
        boolean hasData1 = gdDiffContext.columnNames1 != null && !gdDiffContext.allData1.isEmpty();
        boolean hasData2 = gdDiffContext.columnNames2 != null && !gdDiffContext.allData2.isEmpty();

        // 如果两个面板都有数据，才进行同步
        if (hasData1 && hasData2 && gdPagination1 != null && gdPagination2 != null) {
            syncLabel.isSyncingPagination = true;
            try {
                // 同步页大小
                gdPagination1.setPageSize(gdPagination2.pageSize);

                // 同步当前页码
                gdPagination1.currentPage = gdPagination2.currentPage;
                gdPagination1.totalPages = calculateTotalPages(gdDiffContext.allData1.size(), gdPagination1.pageSize);

                // 更新控件
                gdPagination1.updateControls(gdPagination1.currentPage, gdPagination1.totalPages);
            } finally {
                syncLabel.isSyncingPagination = false;
            }
        }
    }

    /**
     * 从GD面板1同步到GD面板2 - 修改现有方法
     */
    public void syncGdPagination() {
        if (syncLabel.isSyncingPagination) {
            return;
        }

        // 检查两个面板是否都有数据
        boolean hasData1 = gdDiffContext.columnNames1 != null && !gdDiffContext.allData1.isEmpty();
        boolean hasData2 = gdDiffContext.columnNames2 != null && !gdDiffContext.allData2.isEmpty();

        // 如果两个面板都有数据，才进行同步
        if (hasData1 && hasData2 && gdPagination1 != null && gdPagination2 != null) {
            syncLabel.isSyncingPagination = true;
            try {
                // 同步页大小
                gdPagination2.setPageSize(gdPagination1.pageSize);

                // 同步当前页码
                gdPagination2.currentPage = gdPagination1.currentPage;
                gdPagination2.totalPages = calculateTotalPages(gdDiffContext.allData2.size(), gdPagination2.pageSize);

                // 更新控件
                gdPagination2.updateControls(gdPagination2.currentPage, gdPagination2.totalPages);
            } finally {
                syncLabel.isSyncingPagination = false;
            }
        }
    }

    /**
     * 计算总页数
     */
    public int calculateTotalPages(int totalRows, int pageSize) {
        if (totalRows == 0)
            return 1;
        return (int)Math.ceil((double)totalRows / pageSize);
    }

    /**
     * 同步Excel 分页控件
     */
    public void syncExcelPagination() {
        if (syncLabel.isSyncingPagination) {
            return;
        }

        // 检查两个面板是否都有数据
        boolean hasData1 = excelDiffContext.columnNames1 != null && !excelDiffContext.allData1.isEmpty();
        boolean hasData2 = excelDiffContext.columnNames2 != null && !excelDiffContext.allData2.isEmpty();

        // 如果两个面板都有数据，才进行同步
        if (hasData1 && hasData2) {
            syncLabel.isSyncingPagination = true;
            try {
                if (excelPagination2 != null) {
                    excelPagination2.setPageSize(excelPagination1.pageSize);
                    excelPagination2.updateControls(excelPagination1.currentPage, excelPagination1.totalPages);
                    updateExcelPaginationControls2();
                }
            } finally {
                syncLabel.isSyncingPagination = false;
            }
        } else if (hasData1) {
            // 只有面板1有数据，独立更新
            if (excelPagination1 != null) {
                excelPagination1.updateControls(excelPagination1.currentPage, excelPagination1.totalPages);
            }
        } else if (hasData2) {
            // 只有面板2有数据，独立更新
            if (excelPagination2 != null) {
                excelPagination2.updateControls(excelPagination2.currentPage, excelPagination2.totalPages);
            }
        }
    }

    /**
     * 显示指定页的GD数据1
     */
    public void showGdPage(int page) {
        if (gdPagination1 == null || gdDiffContext.columnNames1 == null) {
            logMessage("AbstractViewFrame GD数据未准备好 - gdPagination1: " + (gdPagination1 != null) + ", columnNames1: "
                + (gdDiffContext.columnNames1 != null));
            return;
        }

        // 计算总页数
        int totalRows = gdDiffContext.allData1.size();
        int totalPages = calculateTotalPages(totalRows, gdPagination1.pageSize);

        // 确保页码在有效范围内
        page = Math.max(1, Math.min(page, totalPages));

        int startIndex = (page - 1) * gdPagination1.pageSize;
        int endIndex = Math.min(startIndex + gdPagination1.pageSize, totalRows);

        if (gdPagination1.tableModel != null && gdPagination1.table != null) {
            gdPagination1.tableModel.setRowCount(0);
            gdPagination1.tableModel.setColumnCount(gdDiffContext.columnNames1.length);
            gdPagination1.tableModel.setColumnIdentifiers(gdDiffContext.columnNames1);

            gdPagination1.table.putClientProperty("startRow", startIndex);

            for (int i = startIndex; i < endIndex; i++) {
                gdPagination1.tableModel.addRow(gdDiffContext.allData1.get(i));
            }

            // 更新行数和列数标签
            if (gdPagination1.rowLabel != null) {
                gdPagination1.rowLabel.setText(String.format("行数: %d (第 %d/%d 页)", totalRows, page, totalPages));
            }
            if (gdPagination1.colLabel != null) {
                gdPagination1.colLabel.setText("列数: " + gdDiffContext.columnNames1.length);
            }
        }

        // 更新分页控制器状态
        gdPagination1.currentPage = page;
        gdPagination1.totalPages = totalPages;
        if (gdPagination1.pageSpinner != null) {
            gdPagination1.pageSpinner.setValue(page);
        }

        gdPagination1.updateControls(page, totalPages);

        // 设置列宽
        if (gdPagination1.table != null) {
            for (int i = 0; i < gdDiffContext.columnNames1.length; i++) {
                gdPagination1.table.getColumnModel().getColumn(i).setPreferredWidth(150);
            }
        }

        refreshGdTables();

        // 同步另一个分页控制器（如果有数据）
        if (!syncLabel.isSyncingPagination && gdDiffContext.columnNames2 != null && !gdDiffContext.allData2.isEmpty()) {
            syncGdPagination();
        }
    }

    /**
     * 更新Excel分页控件状态2
     */
    public void updateExcelPaginationControls2() {
        if (excelPagination2 != null) {
            excelPagination2.updateControls(excelPagination2.currentPage, excelPagination2.totalPages);
        }
    }

    /**
     * 刷新Excel 表格显示
     */
    public void refreshExcelTables() {
        if (excelPagination1 != null && excelPagination1.table != null) {
            excelPagination1.table.repaint();
        }
        if (excelPagination2 != null && excelPagination2.table != null) {
            excelPagination2.table.repaint();
        }
    }

    /**
     * 刷新GD 表格显示
     */
    public void refreshGdTables() {
        if (gdPagination1 != null && gdPagination1.table != null) {
            gdPagination1.table.repaint();
        }
        if (gdPagination2 != null && gdPagination2.table != null) {
            gdPagination2.table.repaint();
        }
    }

    /**
     * 根据显示名称获取对应的ExcelOperate 对象
     */
    public ExcelOperate getExcelOperateByDisplayName(String displayName) {
        if (displayName == null) {
            return null;
        }

        File file = new File(baseCheck.XML_PATH + "\\" + displayName);
        if (!file.exists()) {
            return null;
        }
        // 从文件映射中获取
        currentExcelOperate = new ExcelOperate(file);

        currentExcelOperate.init(log);

        if (currentExcelOperate.init) {
            return currentExcelOperate;
        }
        return null;
    }

    /**
     * 加载文件详情（当前目录Excel文件）
     */
    public void loadFileDetails(ExcelOperate excelOperate) {
        currentExcelOperate = excelOperate;

        try {
            if (!excelOperate.init) {
                logMessage("Excel文件未初始化: " + getFileName(excelOperate));
                return;
            }

            String fileName = getFileName(excelOperate);
            excelDataCache.remove("CURRENT_" + fileName);

            Workbook workbook = excelOperate.workbook;
            if (workbook != null) {
                loadAndCacheAllSheets(workbook, fileName);
            }
        } catch (Exception e) {
            logMessage("加载文件详情失败: " + e);
            e.printStackTrace();
        }
    }

    /**
     * 加载并缓存所有 Sheet
     */
    public void loadAndCacheAllSheets(Workbook workbook, String fileName) {
        int sheetCount = workbook.getNumberOfSheets();
        logMessage("文件: " + fileName + ", Sheet数量: " + sheetCount);
        SheetOperate sheetOperate;
        Sheet sheet;
        List<SheetOperate> sheetDataList = new ArrayList<>();
        for (int i = 0; i < sheetCount; i++) {
            sheet = workbook.getSheetAt(i);
            if (Utils.checkNotFitSheetName(sheet.getSheetName())) {
                continue;
            }
            sheetOperate = new SheetOperate(sheet);
            try {
                setSheet(sheetOperate, sheetDataList);
            } catch (Exception e) {
                logMessage("加载Sheet数据失败: " + sheet.getSheetName() + ", " + e);
            } finally {
                sheetOperate.close();
            }
        }

        excelDataCache.put("CURRENT_" + fileName, sheetDataList);
        closeWorkbook(workbook, currentExcelOperate, fileName);
        updateSheetSelector(sheetDataList);

        if (sheetCount > 0 && sheetSelector != null) {
            sheetSelector.setSelectedIndex(0);
        }
    }

    /**
     * 设置Sheet 数据
     */
    public void setSheet(SheetOperate sheetData, List<SheetOperate> sheetDataList) {
        sheetData.init(log);
        sheetData.close();
        sheetDataList.add(sheetData);
        sheetData.close();
        sheetDataList.add(sheetData);
    }

    /**
     * 关闭 Workbook
     */
    public void closeWorkbook(Workbook workbook, ExcelOperate excelOperate, String fileName) {
        try {
            workbook.close();
            excelOperate.workbook = null;
            logMessage("已关闭Workbook引用: " + fileName);
        } catch (Exception e) {
            logMessage("关闭Workbook时出错: " + e);
        }
    }

    /**
     * 更新Sheet 选择器
     */
    public void updateSheetSelector(final List<SheetOperate> sheetDataList) {
        if (sheetSelectorModel == null) {
            return;
        }
        sheetSelectorModel.removeAllElements();
        for (SheetOperate sheetData : sheetDataList) {
            sheetSelectorModel.addElement(sheetData.sheetName);
        }
    }

    /**
     * 从外部记录日志（供DataBuilderCheck调用） 所有输出都显示在底部状态栏的logArea
     */
    public void logMessage(String message) {
        logMessage(message, false);
    }

    /**
     * 从外部记录日志（供DataBuilderCheck调用） 所有输出都显示在底部状态栏的logArea
     */
    public void logMessage(String message, boolean redShow) {
        String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String logText = "[" + timestamp + "] " + message + "\n";

        // 输出到底部状态栏（保持原有逻辑，所有错误都显示红色）
        StyledDocument logDoc = logArea.getStyledDocument();
        Style logStyle = logArea.addStyle("LogStyle", null);
        StyleConstants.setFontFamily(logStyle, "宋体");
        StyleConstants.setFontSize(logStyle, 16);

        if (redShow) {
            StyleConstants.setForeground(logStyle, Color.RED);
            StyleConstants.setBold(logStyle, true);
        } else {
            StyleConstants.setForeground(logStyle, Color.BLACK);
            StyleConstants.setBold(logStyle, false);
        }

        try {
            logDoc.insertString(logDoc.getLength(), logText, logStyle);
            logArea.setCaretPosition(logDoc.getLength());
        } catch (BadLocationException e) {
            logMessage("记录日志失败: " + e);
        }
    }

    /**
     * 获取文件名
     */
    public String getFileName(ExcelOperate excelOperate) {
        return excelOperate.file != null ? excelOperate.file.getName() : "未知文件";
    }

    /**
     * 显示指定页的Excel数据1 -
     */
    public void showExcelPage(int page) {
        if (excelPagination1 == null || excelDiffContext.columnNames1 == null) {
            logMessage("AbstractViewFrame Excel数据未准备好 - excelPagination1: " + (excelPagination1 != null) + ", columnNames1: "
                + (excelDiffContext.columnNames1 != null));
            return;
        }

        // 计算总页数
        int totalRows = excelDiffContext.allData1.size();
        int totalPages = calculateTotalPages(totalRows, excelPagination1.pageSize);

        // 确保页码在有效范围内
        page = Math.max(1, Math.min(page, totalPages));

        int startIndex = (page - 1) * excelPagination1.pageSize;
        int endIndex = Math.min(startIndex + excelPagination1.pageSize, totalRows);

        if (excelPagination1.tableModel != null && excelPagination1.table != null) {
            excelPagination1.tableModel.setRowCount(0);
            excelPagination1.tableModel.setColumnCount(excelDiffContext.columnNames1.length);
            excelPagination1.tableModel.setColumnIdentifiers(excelDiffContext.columnNames1);

            excelPagination1.table.putClientProperty("startRow", startIndex);

            for (int i = startIndex; i < endIndex; i++) {
                excelPagination1.tableModel.addRow(excelDiffContext.allData1.get(i));
            }

            // 更新行数和列数标签
            if (excelPagination1.rowLabel != null) {
                excelPagination1.rowLabel.setText(String.format("行数: %d (第 %d/%d 页)", totalRows, page, totalPages));
            }
            if (excelPagination1.colLabel != null) {
                excelPagination1.colLabel.setText("列数: " + excelDiffContext.columnNames1.length);
            }
        }

        // 更新分页控制器状态
        excelPagination1.currentPage = page;
        excelPagination1.totalPages = totalPages;
        if (excelPagination1.pageSpinner != null) {
            excelPagination1.pageSpinner.setValue(page);
        }

        excelPagination1.updateControls(page, totalPages);

        // 设置列宽
        if (excelPagination1.table != null) {
            for (int i = 0; i < excelDiffContext.columnNames1.length; i++) {
                excelPagination1.table.getColumnModel().getColumn(i).setPreferredWidth(150);
            }
        }

        refreshExcelTables();

        // 同步另一个分页控制器（如果有数据）
        if (!syncLabel.isSyncingPagination && excelDiffContext.columnNames2 != null
            && !excelDiffContext.allData2.isEmpty()) {
            syncExcelPagination();
        }
    }

    /**
     * 显示指定页的Excel数据2 -
     */
    public void displayExcelPage2(int page) {
        if (excelPagination2 == null || excelDiffContext.columnNames2 == null) {
            logMessage("Excel数据未准备好 - excelPagination2: " + (excelPagination2 != null) + ", columnNames2: "
                + (excelDiffContext.columnNames2 != null));
            return;
        }

        // 计算总页数
        int totalRows = excelDiffContext.allData2.size();
        int totalPages = calculateTotalPages(totalRows, excelPagination2.pageSize);

        // 确保页码在有效范围内
        page = Math.max(1, Math.min(page, totalPages));

        int startIndex = (page - 1) * excelPagination2.pageSize;
        int endIndex = Math.min(startIndex + excelPagination2.pageSize, totalRows);

        if (excelPagination2.tableModel != null && excelPagination2.table != null) {
            excelPagination2.tableModel.setRowCount(0);
            excelPagination2.tableModel.setColumnCount(excelDiffContext.columnNames2.length);
            excelPagination2.tableModel.setColumnIdentifiers(excelDiffContext.columnNames2);

            excelPagination2.table.putClientProperty("startRow", startIndex);

            for (int i = startIndex; i < endIndex; i++) {
                excelPagination2.tableModel.addRow(excelDiffContext.allData2.get(i));
            }

            // 更新行数和列数标签
            if (excelPagination2.rowLabel != null) {
                excelPagination2.rowLabel.setText(String.format("行数: %d (第 %d/%d 页)", totalRows, page, totalPages));
            }
            if (excelPagination2.colLabel != null) {
                excelPagination2.colLabel.setText("列数: " + excelDiffContext.columnNames2.length);
            }
        }

        // 更新分页控制器状态
        excelPagination2.currentPage = page;
        excelPagination2.totalPages = totalPages;
        if (excelPagination2.pageSpinner != null) {
            excelPagination2.pageSpinner.setValue(page);
        }

        excelPagination2.updateControls(page, totalPages);

        // 设置列宽
        if (excelPagination2.table != null) {
            for (int i = 0; i < excelDiffContext.columnNames2.length; i++) {
                excelPagination2.table.getColumnModel().getColumn(i).setPreferredWidth(150);
            }
        }

        refreshExcelTables();

        // 同步另一个分页控制器（如果有数据）
        if (!syncLabel.isSyncingPagination && excelDiffContext.columnNames1 != null
            && !excelDiffContext.allData1.isEmpty()) {
            syncExcelPagination();
        }
    }

    /**
     * 清空数据面板时重置分页 -
     */
    public void clearDataPanel(DefaultTableModel tableModel, JLabel rowLabel, JLabel colLabel) {
        if (tableModel != null) {
            tableModel.setRowCount(0);
            tableModel.setColumnCount(0);
        }
        if (rowLabel != null) {
            rowLabel.setText("行数: 0");
        }
        if (colLabel != null) {
            colLabel.setText("列数: 0");
        }
    }

    /**
     * 初始化分页控制器 - 在创建面板后调用
     */
    public void initPaginationController(PaginationController pagination) {
        if (pagination != null) {
            // 重置为第一页
            pagination.currentPage = 1;
            pagination.totalPages = 1;

            // 更新控件状态
            if (pagination.pageSpinner != null) {
                pagination.pageSpinner.setValue(1);
            }
            if (pagination.pageLabel != null) {
                pagination.pageLabel.setText("第 1/1 页");
            }
            if (pagination.prevPageBtn != null) {
                pagination.prevPageBtn.setEnabled(false);
            }
            if (pagination.nextPageBtn != null) {
                pagination.nextPageBtn.setEnabled(false);
            }
        }
    }

    /**
     * 创建数据面板（通用方法）
     */
    public JPanel createPanel(PanelCreationConfig config, PaginationController thisC, PaginationController other) {
        JPanel panel = createDataPanel(config, thisC);

        // 创建并挂载通用分页控制器
        JPanel topPanel = (JPanel)panel.getComponent(0);
        JPanel paginationPanel = thisC.createPaginationPanel();
        topPanel.add(paginationPanel, BorderLayout.EAST);

        // 设置GD表格滚动同步（只有当两个面板都有数据时才同步）
        if (other != null && other.scrollPane != null) {
            // 只有在两个面板都有数据时才设置滚动同步
            setupScrollSync(other.scrollPane, thisC.scrollPane);
        }
        return panel;
    }

    /**
     * 创建PanelCreationConfig的工厂方法（用于子类访问包私有的构造函数）
     */
    protected PanelCreationConfig createPanelConfig(String title, PaginationController controller,
        DiffContext diffContext) {
        return new PanelCreationConfig(title, Object.class, controller, diffContext);
    }

    /**
     * 通用的数据面板创建方法
     */
    public JPanel createDataPanel(PanelCreationConfig config, PaginationController controller) {
        controller.tableModel = config.tableModel;

        // 创建 JTable
        JTable table = new JTable(controller.tableModel);
        table.setRowHeight(25);
        table.setFont(new Font("宋体", Font.PLAIN, 12));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setDefaultRenderer(Object.class, config.cellRendererSupplier);
        controller.table = table;

        // 创建 JScrollPane
        JScrollPane scrollPane = new JScrollPane(table);
        // 设置滚动条策略，确保在内容超出时显示滚动条
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        controller.scrollPane = scrollPane;

        // 创建标签
        JLabel rowLabel = new JLabel("行数: 0");
        JLabel colLabel = new JLabel("列数: 0");
        controller.rowLabel = rowLabel;
        controller.colLabel = colLabel;

        // 创建配置并调用 createDataPanel
        DataPanelConfig dataPanelConfig = new DataPanelConfig(config, table, rowLabel, colLabel, scrollPane);

        return createDataPanel(dataPanelConfig);
    }

    /**
     * 创建数据面板（通用方法）
     */
    private JPanel createDataPanel(DataPanelConfig config) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder(config.title));

        // 配置 TableModel
        config.tableModel.setRowCount(0);
        config.tableModel.setColumnCount(0);

        // 配置 Table
        config.table.setRowHeight(25);
        config.table.setFont(new Font("宋体", Font.PLAIN, 12));
        config.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        config.table.setDefaultRenderer(Object.class, config.cellRenderer);

        // 创建头部面板（使用BorderLayout以支持分页控件）
        JPanel headerPanel = new JPanel(new BorderLayout());
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        config.rowLabel.setText("行数: 0");
        config.colLabel.setText("列数: 0");
        statsPanel.add(config.rowLabel);
        statsPanel.add(Box.createHorizontalStrut(20));
        statsPanel.add(config.colLabel);
        headerPanel.add(statsPanel, BorderLayout.WEST);

        panel.add(headerPanel, BorderLayout.NORTH);
        if (config.scrollPane != null) {
            panel.add(config.scrollPane, BorderLayout.CENTER);
        }

        return panel;
    }

    /**
     * 设置滚动同步监听器
     */
    private void setupScrollSync(JScrollPane scrollPane1, JScrollPane scrollPane2) {
        if (scrollPane1 == null || scrollPane2 == null) {
            return;
        }

        // 移除旧的监听器（如果有）
        scrollPane1.getViewport().removeChangeListener(null);
        scrollPane2.getViewport().removeChangeListener(null);

        // 创建同步监听器

        ChangeListener syncListener1 =
            listener(scrollPane1, scrollPane2, scrollPane1.getViewport(), scrollPane2.getViewport());

        ChangeListener syncListener2 =
            listener(scrollPane1, scrollPane2, scrollPane2.getViewport(), scrollPane1.getViewport());

        // 添加监听器
        scrollPane1.getViewport().addChangeListener(syncListener1);
        scrollPane2.getViewport().addChangeListener(syncListener2);
    }

    /**
     * 加载当前目录 xml
     */
    public File[] loadCurrXml() {
        File[] files = listExcelFiles(baseCheck.CURR_DIR);

        if (files != null) {
            // 先关闭所有已打开的文件
            closeWebhook();
            fileMap.clear();

            for (File file : files) {
                ExcelOperate eo = new ExcelOperate(file);
                eo.init(log);
                if (eo.init) {
                    fileMap.put(file.getName(), eo);
                    SwingUtilities.invokeLater(() -> {
                        if (!listModel.contains(file.getName())) {
                            listModel.addElement(file.getName());
                        }
                        showExcelFileList();
                    });
                }
            }
        }
        return files;
    }

    /**
     * 显示Excel 文件列表
     */
    public void showExcelFileList() {
        CardLayout cardLayout = (CardLayout)excelFileDisplayPanel.getLayout();
        if (!listModel.isEmpty()) {
            cardLayout.show(excelFileDisplayPanel, "FILE_LIST");
            fileList.setSelectedIndex(0);
        } else {
            cardLayout.show(excelFileDisplayPanel, "NO_FILE");
        }
    }

    /**
     * 关闭所有的打开文件
     */
    public void closeWebhook() {
        closeAllExcelOperates();
    }

    public void releaseXmlPathExcel() {}

    /**
     * 关闭所有ExcelOperate 对象
     */
    public void closeAllExcelOperates() {
        for (ExcelOperate eo : fileMap.values()) {
            closeExcelOperate(eo);
        }
    }

    /**
     * 关闭单个ExcelOperate 对象
     */
    public void closeExcelOperate(ExcelOperate eo) {
        if (eo == null) {
            return;
        }
        try {
            if (eo.workbook != null) {
                eo.workbook.close();
                eo.workbook = null;
            }
            eo.destroy();
        } catch (Exception e) {
            logMessage(e.getMessage());
        }
    }

    private ChangeListener listener(JScrollPane scrollPane1, JScrollPane scrollPane2, JViewport viewport,
        JViewport viewport2) {
        return e -> {
            if (!syncLabel.isSyncingScroll && scrollPane1.isVisible() && scrollPane2.isVisible()) {
                syncLabel.isSyncingScroll = true;
                try {
                    Point viewPosition = viewport.getViewPosition();
                    viewport2.setViewPosition(viewPosition);
                } finally {
                    syncLabel.isSyncingScroll = false;
                }
            }
        };
    }

    /**
     * 列出目录中的Excel 文件
     */
    public File[] listExcelFiles(File dir) {
        return dir.listFiles((d, name) -> {
            if (name == null || name.trim().isEmpty()) {
                return false;
            }
            String fName = name.trim().toLowerCase();
            return fName.endsWith(ExcelExtensions.FILE_EXT_XLS) || fName.endsWith(ExcelExtensions.FILE_EXT_XLSX);
        });
    }

    // 日志记录器
    public final Log log = new Log() {
        @Override
        public void logMessage(String message) {
            AbstractViewFrame.this.logMessage(message);
        }

        @Override
        public void logMessage(String message, boolean redShow) {
            AbstractViewFrame.this.logMessage(message, redShow);
        }
    };
}
