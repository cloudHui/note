package com.gamer.data.excel.view;

import com.gamer.data.excel.AbstractViewFrame;
import com.gamer.data.excel.BaseCheck;
import com.gamer.data.excel.ExcelExtensions;
import com.gamer.data.excel.GdData;
import com.gamer.data.excel.GdFileReader;
import com.gamer.data.gd.ExcelOperate;
import com.gamer.data.gd.GdMD5Util;
import com.gamer.data.gd.SheetOperate;
import com.gamer.data.gd.Utils;
import com.gamer.data.map.MapRingUtil;
import com.gamer.data.map.MapViewerPanel;
import com.gamer.data.message.Util;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gamer.data.excel.BaseCheck.GD;
import static com.gamer.data.excel.view.ViewUtils.compareValuesByType;
import static com.gamer.data.excel.view.ViewUtils.objectsNotEqual;

/**
 * ExcelGD差异查看器，增加GD文件对比功能</br>
 * 这个策划用的对比生成表格和gd 差异
 *
 * @author liuyunhui
 * @date 2025/12/30
 */
public class ExcelGDDiffView extends AbstractViewFrame {
    // 当前选中的Excel 和GD 数据
    private ExcelOperate currentExcelOperate; // 当前目录下的 Excel
    private ExcelOperate xmlPathExcelOperate; // XML_PATH目录下的Excel（按需加载）
    private GdData gdPathGdData; // GD_PATH目录下的GD数据（按需加载）
    private GdData currentDirGdData; // 当前目录下的GD 数据

    private final JPanel mainPanel;// 主面板
    private JPanel contentPanel;
    private CardLayout contentCardLayout;

    private static final String CARD_EXCEL_GD_DIFF = "EXCEL_GD_DIFF";
    private static final String CARD_MAP_VIEWER = "MAP_VIEWER";

    // 差异单元格标记集合（格式：行,列）key: fileName, value: sheetDataList
    private final Map<String, List<SheetOperate>> excelDataCache = new HashMap<>();

    public ExcelGDDiffView(BaseCheck baseCheck) {
        super(baseCheck);
        // 创建菜单栏
        createMenuBar();

        setTitle("Excel 与GD 文件对比生成复制查看器");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // 全屏显示
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(mainPanel);
        initContentPanel();
        // 初始化UI
        initExcelGdUI();
        // 加载当前目录下的Excel文件列表（默认读取第一个）
        // 左侧列表只显示当前目录的Excel文件
        SwingUtilities.invokeLater(this::loadCurrentDirExcelFiles);
    }

    private void initContentPanel() {
        contentCardLayout = new CardLayout(0, 0);
        contentPanel = new JPanel(contentCardLayout);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    private void showCard(String cardName) {
        if (contentCardLayout != null && contentPanel != null) {
            contentCardLayout.show(contentPanel, cardName);
            contentPanel.revalidate();
            contentPanel.repaint();
        }
    }

    /**
     * 创建菜单栏
     */
    private void createMenuBar() {
        // 创建菜单栏
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("文件");

        // 创建Excel 与GD 文件对比生成复制查看器菜单项
        JMenuItem item = new JMenuItem("Excel 与GD 文件对比生成复制查看器");
        item.addActionListener(e -> initExcelGdUI());
        fileMenu.add(item);
        if (!baseCheck.MORE_DEEP) {
            // 地图范围查看器菜单项
            item = new JMenuItem("地图范围查看器");
            item.addActionListener(e -> initMapViewerPanel());
            fileMenu.add(item);
        }

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    /**
     * 创建左侧面板（分成两块：上面是按钮，下面是Excel文件列表）
     */
    private JPanel createLeftPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(new TitledBorder("Excel 文件列表"));

        // 创建垂直分割面板
        JSplitPane verticalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        verticalSplit.setResizeWeight(0.11); // 按钮区域占11%

        // 第一块：三个按钮 一个选择子sheet的组合
        verticalSplit.setTopComponent(createButtonPanel());

        // 第二块：Excel文件列表
        verticalSplit.setBottomComponent(createExcelFileDisplayPanel());

        mainPanel.add(verticalSplit, BorderLayout.CENTER);

        return mainPanel;
    }

    /**
     * 创建Excel 文件显示面板
     */
    private JPanel createExcelFileDisplayPanel() {
        excelFileDisplayPanel = new JPanel(new CardLayout(5, 5));
        excelFileDisplayPanel.setBorder(new TitledBorder("当前目录Excel 文件"));

        // Exce l文件列表
        JScrollPane fileScrollPane = init();
        excelFileDisplayPanel.add(fileScrollPane, "FILE_LIST");

        // 无文件时的红色大字提示
        JLabel noExcelFileLabel = new JLabel("没有Excel 文件", JLabel.CENTER);
        noExcelFileLabel.setFont(new Font("宋体", Font.BOLD, 24));
        noExcelFileLabel.setForeground(Color.RED);
        excelFileDisplayPanel.add(noExcelFileLabel, "NO_FILE");

        return excelFileDisplayPanel;
    }

    /**
     * 创建按钮面板
     */
    private JPanel createButtonPanel() {
        // 改为按钮一行，选择器一行
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder("操作按钮"));

        // 按钮行面板
        JPanel btnPanel = getJPanel();

        panel.add(btnPanel);

        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JLabel label = new JLabel("选择Sheet:");
        sheetSelectorModel = new DefaultComboBoxModel<>();
        sheetSelector = new JComboBox<>(sheetSelectorModel);
        sheetSelector.setFont(new Font("宋体", Font.PLAIN, 14));
        sheetSelector.setPreferredSize(new Dimension(300, 30));
        selectorPanel.add(label);
        selectorPanel.add(sheetSelector);

        // Sheet 选择器监听
        sheetSelector.addActionListener(e -> {
            String selectedSheet = (String)sheetSelector.getSelectedItem();
            if (selectedSheet != null && !selectedSheet.isEmpty()) {
                File file = new File(baseCheck.CURR_DIR + "/" + selectedSheet + ExcelExtensions.FILE_EXT_XLSX);
                currentExcelOperate = new ExcelOperate(file);
                reloadAllDataPanels(selectedSheet);
            }
        });

        panel.add(selectorPanel);

        return panel;
    }

    public JPanel getJPanel() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 10)); // 将水平间距从 10 改为 5

        // 定义一个通用的边距：上下 2 像素，左右 5 像素（默认通常较大）
        Insets smallMargin = new Insets(2, 5, 2, 5);
        Font commonFont = new Font("宋体", Font.PLAIN, 14);

        // 按钮1
        JButton btn = new JButton("重加载");
        btn.setFont(commonFont);
        btn.setMargin(smallMargin); // 关键：缩小内边距
        btn.addActionListener(e -> reloadCurrentDirFiles());
        btnPanel.add(btn);

        // 按钮2
        btn = new JButton("生成GD");
        btn.setFont(commonFont);
        btn.setMargin(smallMargin);
        btn.addActionListener(e -> generateGdFiles(false));
        btnPanel.add(btn);

        // 按钮3
        btn = new JButton("生成带MD5");
        btn.setFont(commonFont);
        btn.setMargin(smallMargin);
        btn.addActionListener(e -> generateGdFiles(true));
        btnPanel.add(btn);

        // 按钮4
        btn = new JButton("复制");
        btn.setFont(commonFont);
        btn.setMargin(smallMargin);
        btn.addActionListener(e -> copyFilesToTargetDirsNew());
        btnPanel.add(btn);

        return btnPanel;
    }

    /**
     * 复制前关闭所有文件
     */
    private void closeAllFilesBeforeCopy() throws InterruptedException {
        logMessage("正在关闭已打开的文件...");

        // 关闭当前 Excel
        closeExcelOperate(currentExcelOperate);
        currentExcelOperate = null;

        // 关闭所有Excel 文件
        closeAllExcelOperates();

        // 释放XML_PATH Excel
        releaseXmlPathExcel();

        // 释放 GD数据
        releaseGdPathGdData();
        releaseCurrentDirGdData();

        // 清空缓存
        excelDataCache.clear();
        excelDiffContext.differenceCells.clear();
        excelDiffContext.differenceRows.clear();

        // 强制垃圾回收，确保资源释放
        System.gc();

        // 等待更长时间，确保文件锁释放
        Thread.sleep(500);

        logMessage("文件已关闭，开始复制...");
    }

    private File[] listExcelGdFiles(File dir) {
        return dir.listFiles((d, name) -> {
            if (name == null || name.trim().isEmpty()) {
                return false;
            }
            String fName = name.trim().toLowerCase();
            return fName.endsWith(ExcelExtensions.FILE_EXT_XLS) || fName.endsWith(ExcelExtensions.FILE_EXT_XLSX)
                || fName.endsWith(GD);
        });
    }

    /**
     * 复制文件到XML_PATH和GD_PATH
     */
    private void copyFilesToTargetDirsNew() {
        try {
            closeAllFilesBeforeCopy();
        } catch (InterruptedException e) {
            logMessage("关闭文件失败: " + e.getMessage());
        }
        // 获取当前目录下所有的Excel文件并复制删除到XML_PATH和GD_PATH
        File[] excelFiles = listExcelGdFiles(baseCheck.CURR_DIR);
        if (excelFiles != null) {
            for (File f : excelFiles) {
                // 看文件结尾是 .xls 或者 .xlsx 还是gd
                if (f.getName().endsWith(ExcelExtensions.FILE_EXT_XLS)
                    || f.getName().endsWith(ExcelExtensions.FILE_EXT_XLSX)) {
                    try {
                        copyFileToPath(f, f.getName(), baseCheck.XML_PATH);
                    } catch (Exception e) {
                        logMessage("复制文件失败: " + e.getMessage());
                    }
                } else if (f.getName().endsWith(".gd")) {
                    try {
                        copyFileToPath(f, f.getName(), baseCheck.GD_PATH);
                    } catch (Exception e) {
                        logMessage("复制文件失败: " + e.getMessage());
                    }
                } else {
                    return;
                }
                logMessage("删除文件: " + f.getName() + " 成功" + f.delete());
            }
        }
        logMessage("复制" + (excelFiles == null ? 0 : excelFiles.length) + "个文件完成");
    }

    /**
     * 复制E文件到（带重试机制）
     */
    private void copyFileToPath(File file, String fileName, File targetPath) throws Exception {
        if (targetPath.exists() && targetPath.isDirectory()) {
            File targetExcelFile = new File(targetPath + "\\" + fileName);
            copyFileWithRetry(file, targetExcelFile, fileName);
            logMessage("已复制文件到: " + targetExcelFile.getPath());
        } else {
            logMessage("目录不存在: " + targetPath.getPath());
        }
    }

    /**
     * 带重试机制的文件复制（解决文件被占用的问题）
     */
    private void copyFileWithRetry(File sourceFile, File targetFile, String fileType) throws Exception {
        int maxRetries = 5;
        int retryDelay = 200; // 毫秒

        for (int i = 0; i < maxRetries; i++) {
            try {
                // 如果目标文件存在且被占用，先尝试删除
                if (targetFile.exists() && !targetFile.delete()) {
                    logMessage("警告: 无法删除目标文件，可能被占用: " + targetFile.getPath());
                }

                Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                return; // 复制成功，退出
            } catch (FileSystemException e) {
                if (i < maxRetries - 1) {
                    logMessage(
                        fileType + "复制失败（可能被占用），" + retryDelay + "ms后重试 (" + (i + 1) + "/" + maxRetries + ")...");
                    Thread.sleep(retryDelay);
                    retryDelay *= 2; // 指数退避
                    System.gc(); // 再次尝试垃圾回收
                } else {
                    throw new Exception(
                        fileType + "复制失败: 文件可能被其他程序占用: " + targetFile.getPath() + ", 错误: " + e.getMessage(), e);
                }
            } catch (Exception e) {
                // 其他异常直接抛出
                throw new Exception(fileType + "复制失败: " + e.getMessage(), e);
            }
        }
    }

    /**
     * 生成GD 文件
     */
    private void generateGdFiles(boolean withMd5) {
        logMessage("开始生成GD文件...");
        try {
            File[] excelFiles = listExcelFiles(baseCheck.CURR_DIR);

            if (excelFiles == null || excelFiles.length == 0) {
                logMessage("生成失败 当前目录没有excel文件");
                return;
            }
            List<File> bigFile = new ArrayList<>();
            for (File file : excelFiles) {
                // 当文件大于10mb换工具生成 GD
                if (file.length() > 10 * 1024 * 1024) {
                    logMessage("文件大于10mb,使用工具生成GD: " + file.getName());
                    bigFile.add(file);
                    break;
                }
            }
            if (bigFile.isEmpty()) {
                // 使用本地工具生成GD 文件
                for (File file : excelFiles) {
                    try {
                        ExcelOperate eo = getExcelOperate(file);
                        closeExcelOperate(eo);
                        logMessage("生成GD成功: " + file.getName());
                    } catch (Exception e) {
                        logMessage("生成GD失败: " + e.getMessage(), true);
                    }
                }
                if (withMd5) {
                    GdMD5Util.buildGdMD5();
                }
            } else {
                processExcelFilesForGd(withMd5);
            }
        } catch (Exception e) {
            logMessage("生成GD文件失败: " + e.getMessage());
        }
    }

    /**
     * 获取ExcelOperate 对象
     */
    private ExcelOperate getExcelOperate(File f) {
        ExcelOperate eo = new ExcelOperate(f);
        eo.init(log);
        eo.parseSheet(log);
        eo.destroy();
        return eo;
    }

    /**
     * 处理Excel 文件生成 GD
     */
    private void processExcelFilesForGd(boolean withMd5) {
        String cmd;
        cmd = "java -jar -Xms2g -Xmx4g -Dlanguage=zh DataBuilder.jar";
        if (withMd5 && new File("DataBuilder（MD5版）.jar").exists()) {
            cmd = "java -jar -Xms2g -Xmx4g -Dlanguage=zh DataBuilder（MD5版）.jar";
        }
        try {
            String finalCmd = cmd;
            SwingUtilities.invokeLater(() -> {
                try {
                    Process process = Runtime.getRuntime().exec(finalCmd);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        logMessage(line);
                    }
                    process.waitFor();
                    logMessage("脚本执行完成");
                } catch (Exception e) {
                    logMessage("执行脚本失败: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            logMessage("执行脚本失败: " + e.getMessage());
        }
    }

    /**
     * 重新加载当前目录文件
     */
    private void reloadCurrentDirFiles() {
        logMessage("开始重新加载当前目录Excel文件...");
        closeAllExcelOperates();
        clearFileList();
        scanAndLoadExcelFiles();
        showExcelFileList();
    }

    /**
     * 清空文件列表
     */
    private void clearFileList() {
        fileMap.clear();
        listModel.clear();
        fileList.clearSelection();
    }

    /**
     * 扫描并加载当前目录Excel 文件
     */
    private void scanAndLoadExcelFiles() {
        if (baseCheck.CURR_DIR == null || !baseCheck.CURR_DIR.exists()) {
            logMessage("当前目录不存在: " + (baseCheck.CURR_DIR != null ? baseCheck.CURR_DIR.getPath() : "null"));
            return;
        }

        File[] files = loadCurrXml();
        if (files != null && files.length > 0) {
            logMessage("找到 " + files.length + " 个Excel 文件");
        } else {
            logMessage("没有Excel 文件");
        }
    }

    /**
     * 加载并缓存Sheet 数据
     */
    private SheetOperate loadAndCacheSheetData(Sheet sheet, String sheetName) {
        SheetOperate sheetOperate = new SheetOperate(sheet);
        try {
            sheetOperate.init(log);
            sheetOperate.close();
        } catch (Exception e) {
            logMessage("加载Sheet数据失败: " + sheetName + ", " + e);
            return null;
        } finally {
            sheetOperate.close();
        }
        return sheetOperate;
    }

    /**
     * 初始化 UI
     */
    private void initExcelGdUI() {
        if (contentPanel == null) {
            initContentPanel();
        }
        JComponent excelRoot = buildExcelGdRootPanel();
        contentPanel.add(excelRoot, CARD_EXCEL_GD_DIFF);
        showCard(CARD_EXCEL_GD_DIFF);
    }

    private JComponent buildExcelGdRootPanel() {
        // 创建主分割面板（左右）
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        mainSplitPane.setResizeWeight(0.06);// 文件列表区域占6%
        // 左侧面板（文件列表）
        mainSplitPane.setLeftComponent(createLeftPanel());

        // 右侧面板（详细信息）
        mainSplitPane.setRightComponent(createRightPanel());

        // 创建垂直分割面板，用于调整logArea高度
        JSplitPane verticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        verticalSplitPane.setTopComponent(mainSplitPane);
        verticalSplitPane.setBottomComponent(createStatusBar());

        // 设置初始分割位置，logArea默认高度400
        Dimension screenSize2 = Toolkit.getDefaultToolkit().getScreenSize();
        int initialLogHeight = 400;
        verticalSplitPane.setDividerLocation(screenSize2.height - initialLogHeight - 100);
        verticalSplitPane.setResizeWeight(1.0); // 让上方区域可以调整大小
        verticalSplitPane.setOneTouchExpandable(true); // 允许快速展开/折叠

        return verticalSplitPane;
    }

    /**
     * 创建地图查看器面板
     */
    private void initMapViewerPanel() {
        if (contentPanel == null) {
            initContentPanel();
        }
        File mapDir = new File(baseCheck.baseDir, MapRingUtil.MAP);
        if (Util.inVM()) {
            mapDir = new File(baseCheck.baseDir.getParentFile().getParentFile(), MapRingUtil.MAP);
        }

        JPanel mapViewerPanel;
        if (!mapDir.isDirectory()) {
            mapViewerPanel = new JPanel(new BorderLayout());
            JLabel label = new JLabel("未找到地图目录: " + mapDir.getAbsolutePath(), JLabel.CENTER);
            label.setFont(new Font("宋体", Font.BOLD, 18));
            label.setForeground(Color.RED);
            mapViewerPanel.add(label, BorderLayout.CENTER);
        } else {
            mapViewerPanel = new MapViewerPanel(mapDir, true);
        }
        contentPanel.add(mapViewerPanel, CARD_MAP_VIEWER);
        showCard(CARD_MAP_VIEWER);
    }

    /**
     * 创建右侧面板 创建四个数据面板（2x2布局）
     */
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));

        // 左上：XML_PATH Excel数据
        panel.add(createExcelDataPanel1(excelPagination2));

        // 右上：当前目录Excel数据
        panel.add(createExcelDataPanel2(excelPagination1));

        // 左下：GD_PATH GD数据
        panel.add(createGdDataPanel1(gdPagination2));

        // 右下：当前目录GD数据
        panel.add(createGdDataPanel2(gdPagination1));

        return panel;
    }

    /**
     * 创建Excel数据面板1（XML_PATH目录）
     */
    private JPanel createExcelDataPanel2(PaginationController otherController) {
        String title = "Excel数据 (当前目录)";
        PanelCreationConfig config = new PanelCreationConfig(title, String.class, excelPagination2, excelDiffContext);
        // 先创建通用分页控制器，再在创建数据面板时把组件注入到分页控制器中
        excelPagination2 = new PaginationController(Utils.DEFAULT_PAGE_SIZE, PAGE_CHANGE, PREV_PAGE_CHANGE);
        return createPanel(config, excelPagination2, otherController);
    }

    /**
     * 创建Excel数据面板1（XML_PATH目录）
     */
    private JPanel createExcelDataPanel1(PaginationController otherController) {
        String title = "Excel数据 (源Excel)";
        PanelCreationConfig config = new PanelCreationConfig(title, String.class, excelPagination1, excelDiffContext);
        // 先创建通用分页控制器，再在创建数据面板时把组件注入到分页控制器中
        excelPagination1 = new PaginationController(Utils.DEFAULT_PAGE_SIZE, PAGE_CHANGE, PREV_PAGE_CHANGE);
        return createPanel(config, excelPagination1, otherController);
    }

    /**
     * 加载当前目录下的Excel 文件列表
     */
    private void loadCurrentDirExcelFiles() {
        if (baseCheck.CURR_DIR == null || !baseCheck.CURR_DIR.exists()) {
            logMessage("当前目录不存在: " + (baseCheck.CURR_DIR != null ? baseCheck.CURR_DIR.getPath() : "null"));
            return;
        }
        logMessage("开始扫描当前目录Excel文件: " + baseCheck.CURR_DIR.getAbsolutePath());

        File[] files = loadCurrXml();

        logMessage("找到 " + (files != null ? files.length : 0) + " 个Excel 文件");
        // 显示Excel 文件列表
        showExcelFileList();

    }

    /**
     * 按文件名加载XML_PATH目录下的Excel文件
     */
    private void loadXmlPathExcelByFileName(String fileName) {
        // 先释放之前的资源
        releaseXmlPathExcel();

        File file = new File(baseCheck.XML_PATH + "\\" + fileName);
        if (!file.exists()) {
            logMessage(baseCheck.XML_PATH + " 目录下Excel文件不存在: " + file.getAbsolutePath());
            if (excelPagination1 != null) {
                clearDataPanel(excelPagination1.tableModel, excelPagination1.rowLabel, excelPagination1.colLabel);
            }
            return;
        }

        try {
            ExcelOperate eo = new ExcelOperate(file);
            eo.init(log);
            if (eo.init && eo.workbook != null) {
                xmlPathExcelOperate = eo;
                Sheet sheet;
                SheetOperate sheetData;
                // 加载并缓存所有Sheet 数据
                List<SheetOperate> sheetDataList = new ArrayList<>();
                int sheetCount = eo.workbook.getNumberOfSheets();
                for (int i = 0; i < sheetCount; i++) {
                    sheet = eo.workbook.getSheetAt(i);
                    if (Utils.checkNotFitSheetName(sheet.getSheetName())) {
                        continue;
                    }
                    sheetData = new SheetOperate(sheet);
                    setSheet(sheetData, sheetDataList);
                }
                String cacheKey = "XML_PATH_" + fileName;
                excelDataCache.put(cacheKey, sheetDataList);

                // 关闭Workbook 引用
                try {
                    eo.workbook.close();
                    eo.workbook = null;
                } catch (Exception e) {
                    logMessage("关闭XML_PATH Workbook时出错: " + e);
                }
            }
        } catch (Exception e) {
            logMessage("加载XML_PATH Excel文件失败: " + e);
        }
    }

    /**
     * 释放XML_PATH Excel资源
     */
    public void releaseXmlPathExcel() {
        if (xmlPathExcelOperate != null) {
            if (xmlPathExcelOperate.workbook != null) {
                try {
                    xmlPathExcelOperate.workbook.close();
                } catch (Exception e) {
                    // 忽略关闭错误
                }
            }
            xmlPathExcelOperate = null;
        }
    }

    /**
     * 加载Excel Sheet数据
     */
    private void loadExcelSheetData(ExcelOperate excelOperate, String sheetName, boolean isXmlPath) {
        if (excelOperate == null) {
            return;
        }

        String fileName = getFileName(excelOperate);
        String cacheKey = (isXmlPath ? "XML_PATH_" : "CURRENT_") + fileName;
        SheetOperate sheetData = getSheetDataFromCache(cacheKey, sheetName);

        if (sheetData == null) {
            sheetData = loadSheetDataFromFile(excelOperate, sheetName, fileName);
            if (sheetData != null) {
                excelDataCache.computeIfAbsent(cacheKey, k -> new ArrayList<>()).add(sheetData);
            }
        }

        closeWorkbookIfOpen(excelOperate);
        displayExcelSheetData(sheetData, isXmlPath);
    }

    /**
     * 从缓存获取Sheet 数据
     */
    private SheetOperate getSheetDataFromCache(String cacheKey, String sheetName) {
        List<SheetOperate> cachedData = excelDataCache.get(cacheKey);
        if (cachedData != null) {
            for (SheetOperate sd : cachedData) {
                if (sd.sheetName.equals(sheetName)) {
                    return sd;
                }
            }
        }
        return null;
    }

    /**
     * 从文件加载Sheet 数据
     */
    private SheetOperate loadSheetDataFromFile(ExcelOperate excelOperate, String sheetName, String fileName) {
        if (excelOperate.workbook == null) {
            if (!reopenWorkbook(excelOperate, fileName)) {
                return null;
            }
        }

        Sheet sheet = findSheet(excelOperate.workbook, sheetName);
        if (sheet == null) {
            logMessage("未找到Sheet: " + sheetName);
            return null;
        }

        return loadAndCacheSheetData(sheet, sheetName);
    }

    /**
     * 重新打开 Workbook
     */
    private boolean reopenWorkbook(ExcelOperate excelOperate, String fileName) {
        if (excelOperate.file != null && excelOperate.file.exists()) {
            logMessage("Workbook已关闭，重新打开文件: " + fileName);
            excelOperate.init(log);
            if (excelOperate.init && excelOperate.workbook != null) {
                return true;
            }
            logMessage("重新打开文件失败: " + fileName);
        } else {
            logMessage("Workbook已关闭且文件不存在，无法加载数据: " + fileName);
        }
        return false;
    }

    /**
     * 查找 Sheet
     */
    private Sheet findSheet(Workbook workbook, String sheetName) {
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (sheet.getSheetName().equals(sheetName)) {
                return sheet;
            }
        }
        return null;
    }

    /**
     * 关闭Workbook（如果打开）
     */
    private void closeWorkbookIfOpen(ExcelOperate excelOperate) {
        if (excelOperate.workbook != null) {
            try {
                excelOperate.workbook.close();
                excelOperate.workbook = null;
            } catch (Exception e) {
                logMessage("关闭Workbook时出错: " + e);
            }
        }
    }

    private void clear() {
        excelDiffContext.allData2.clear();
        excelDiffContext.columnNames2 = null;
        if (excelPagination2 != null) {
            clearDataPanel(excelPagination2.tableModel, excelPagination2.rowLabel, excelPagination2.colLabel);
        }
    }

    /**
     * 加载GD_PATH目录下的GD数据（按Sheet名称查找并加载）
     */
    private void loadGdPathGdData(String sheetName) {
        // 先释放之前的资源
        releaseGdPathGdData();

        File file = new File(baseCheck.GD_PATH + "\\" + sheetName + GD);
        if (!file.exists()) {
            logMessage("未找到" + baseCheck.GD_PATH + " 目录下的GD文件: " + file.getAbsolutePath());
            clearGdDataPanel1();
            return;
        }

        loadAndDisplayGdData(file, true);
    }

    /**
     * 释放GD_PATH GD数据资源
     */
    private void releaseGdPathGdData() {
        gdPathGdData = null;
    }

    /**
     * 加载当前目录下的GD 数据
     */
    private void loadCurrentDirGdData(String sheetName) {
        // 先释放之前的资源
        releaseCurrentDirGdData();

        if (baseCheck.CURR_DIR == null || !baseCheck.CURR_DIR.exists()) {
            clearGdDataPanel2();
            return;
        }

        String gdFileName = sheetName + GD;
        File gdFile = new File(baseCheck.CURR_DIR, gdFileName);
        if (!gdFile.exists() || !gdFile.isFile()) {
            // 在子目录中查找
            File[] subDirs = baseCheck.CURR_DIR.listFiles(File::isDirectory);
            if (subDirs != null) {
                for (File subDir : subDirs) {
                    File file = new File(subDir, gdFileName);
                    if (file.exists() && file.isFile()) {
                        gdFile = file;
                        break;
                    }
                }
            }
        }

        if (!gdFile.exists()) {
            logMessage("未找到当前目录下的GD文件: " + sheetName);
            clearGdDataPanel2();
            return;
        }

        loadAndDisplayGdData(gdFile, false);
    }

    /**
     * 释放当前目录GD 数据资源
     */
    private void releaseCurrentDirGdData() {
        currentDirGdData = null;
    }

    /**
     * 加载并显示GD 数据
     */
    private void loadAndDisplayGdData(File gdFile, boolean isGdPath) {

        try {
            GdData gdData = GdFileReader.readGdFile(gdFile);
            if (isGdPath) {
                gdPathGdData = gdData;
                displayGdData(gdData, true);
            } else {
                currentDirGdData = gdData;
                displayGdData(gdData, false);
            }

            // GD数据加载完成后，如果两个数据都已加载，进行对比
            if (gdPathGdData != null && currentDirGdData != null) {
                compareGdData();
            }
        } catch (Exception e) {
            logMessage("加载GD文件失败: " + e);
        }
    }

    /**
     * 显示GD数据（支持分页）
     */
    private void displayGdData(GdData gdData, boolean isGdPath) {
        if (gdData == null) {
            clear();
            return;
        }

        int columnCount = gdData.getColumnCount();
        String[] columnHeaders = new String[columnCount];
        for (int i = 0; i < columnCount; i++) {
            columnHeaders[i] = gdData.getFormattedColumnHeader(i);
        }

        if (isGdPath) {
            // 存储所有数据
            gdDiffContext.allData1.clear();
            gdDiffContext.allData1.addAll(gdData.dataRows);
            gdDiffContext.columnNames1 = columnHeaders;

            // 显示第一页
            displayGdPage2(1);
        } else {
            // 存储所有数据
            gdDiffContext.allData2.clear();
            gdDiffContext.allData2.addAll(gdData.dataRows);
            gdDiffContext.columnNames2 = columnHeaders;

            // 显示第一页
            displayGdPage2(1);
        }
    }

    /**
     * 对比Excel数据（面板1和面板2）
     */
    private void compareExcelData() {
        excelDiffContext.differenceCells.clear();
        excelDiffContext.differenceRows.clear();

        // 基于原始数据进行对比
        if (excelDiffContext.columnNames1 == null || excelDiffContext.columnNames2 == null
            || excelDiffContext.allData1.isEmpty() || excelDiffContext.allData2.isEmpty()) {
            refreshExcelTables();
            return;
        }

        Map<String, Integer> colMap1 = buildExcelColumnMapFromNames(excelDiffContext.columnNames1);
        Map<String, Integer> colMap2 = buildExcelColumnMapFromNames(excelDiffContext.columnNames2);
        List<String> commonCols = findCommonColumns(colMap1, colMap2);

        compareExcelDataRowsFromRaw(colMap1, colMap2, commonCols);

        // 重新显示数据（只显示差异行）
        refreshExcelDataWithFilter();
    }

    /**
     * 从列名构建Excel 列映射
     */
    private Map<String, Integer> buildExcelColumnMapFromNames(String[] columnNames) {
        Map<String, Integer> colMap = new HashMap<>();
        for (int i = 0; i < columnNames.length; i++) {
            colMap.put(columnNames[i], i);
        }
        return colMap;
    }

    /**
     * 对比Excel数据行（基于原始数据）
     */
    private void compareExcelDataRowsFromRaw(Map<String, Integer> colMap1, Map<String, Integer> colMap2,
        List<String> commonCols) {
        int rowCount1 = excelDiffContext.allData1.size();
        int rowCount2 = excelDiffContext.allData2.size();
        int maxRows = Math.max(rowCount1, rowCount2);

        for (int row = 0; row < maxRows; row++) {
            boolean rowHasDifference = false;

            Object[] rowData1 = (row < rowCount1) ? excelDiffContext.allData1.get(row) : null;
            Object[] rowData2 = (row < rowCount2) ? excelDiffContext.allData2.get(row) : null;

            for (String colName : commonCols) {
                int col1 = colMap1.get(colName);
                int col2 = colMap2.get(colName);

                Object value1 = (rowData1 != null && col1 < rowData1.length) ? rowData1[col1] : null;
                Object value2 = (rowData2 != null && col2 < rowData2.length) ? rowData2[col2] : null;

                if (objectsNotEqual(value1, value2)) {
                    excelDiffContext.differenceCells.add(row + "," + col1);
                    excelDiffContext.differenceCells.add(row + "," + col2);
                    rowHasDifference = true;
                }
            }

            addDiff(row, rowHasDifference, rowData1, rowData2, excelDiffContext);
        }
    }

    /**
     * 添加差异行
     */
    private void addDiff(int row, boolean rowHasDifference, Object[] rowData1, Object[] rowData2,
        DiffContext excelDiffContext) {
        if (rowData1 == null || rowData2 == null) {
            int colCount =
                (rowData1 == null) ? excelDiffContext.columnNames2.length : excelDiffContext.columnNames1.length;
            for (int col = 0; col < colCount; col++) {
                excelDiffContext.differenceCells.add(row + "," + col);
            }
            rowHasDifference = true;
        }

        if (rowHasDifference) {
            excelDiffContext.differenceRows.add(row);
        }
    }

    /**
     * 刷新Excel数据（只显示差异行）
     */
    private void refreshExcelDataWithFilter() {
        if (excelDiffContext.differenceRows.isEmpty()) {
            refreshExcelTables();
            return;
        }

        // 重新显示Excel面板1（只显示差异行）
        if (excelDiffContext.columnNames1 != null && !excelDiffContext.allData1.isEmpty()) {
            displayExcelDataFiltered1();
        }

        // 重新显示Excel面板2（只显示差异行）
        if (excelDiffContext.columnNames2 != null && !excelDiffContext.allData2.isEmpty()) {
            displayExcelDataFiltered2();
        }
    }

    /**
     * 显示过滤后的Excel数据1（只显示差异行）
     */
    private void displayExcelDataFiltered1() {
        if (excelDiffContext.columnNames1 == null || excelDiffContext.allData1.isEmpty()) {
            logMessage("Excel数据未准备好 - columnNames1: " + (excelDiffContext.columnNames1 != null) + ", allData1: "
                + (excelDiffContext.allData1 != null));
            return;
        }

        // 保存原始数据（用于恢复）
        List<Object[]> originalData = new ArrayList<>(excelDiffContext.allData1);

        // 获取差异行数据（显示所有列，但只显示差异行）
        List<Object[]> filteredData = new ArrayList<>();
        List<Integer> diffRowList = new ArrayList<>(excelDiffContext.differenceRows);
        Collections.sort(diffRowList);

        excelDiffContext.rowMapping1.clear();
        int filteredRowIndex = 0;
        for (int originalRow : diffRowList) {
            if (originalRow < originalData.size()) {
                filteredData.add(originalData.get(originalRow));
                excelDiffContext.rowMapping1.put(filteredRowIndex, originalRow);
                filteredRowIndex++;
            }
        }

        // 更新数据列表（列名保持不变）
        excelDiffContext.allData1.clear();
        excelDiffContext.allData1.addAll(filteredData);

        // 重新计算分页并显示（第一页）
        showExcelPage(1);

        // 同步面板2
        syncExcelPagination();
    }

    /**
     * 显示过滤后的Excel数据2（只显示差异行）
     */
    private void displayExcelDataFiltered2() {
        if (excelDiffContext.columnNames2 == null || excelDiffContext.allData2.isEmpty()) {
            return;
        }

        // 保存原始数据（用于恢复）
        List<Object[]> originalData = new ArrayList<>(excelDiffContext.allData2);

        // 获取差异行数据（显示所有列，但只显示差异行）
        List<Object[]> filteredData = new ArrayList<>();
        List<Integer> diffRowList = new ArrayList<>(excelDiffContext.differenceRows);
        Collections.sort(diffRowList);

        excelDiffContext.rowMapping2.clear();
        int filteredRowIndex = 0;
        for (int originalRow : diffRowList) {
            if (originalRow < originalData.size()) {
                filteredData.add(originalData.get(originalRow));
                excelDiffContext.rowMapping2.put(filteredRowIndex, originalRow);
                filteredRowIndex++;
            }
        }

        // 更新数据列表（列名保持不变）
        excelDiffContext.allData2.clear();
        excelDiffContext.allData2.addAll(filteredData);

        // 重新计算分页并显示（第一页）
        displayExcelPage2(1);

        // 同步面板1
        syncExcelPagination();
    }

    /**
     * 对比GD数据（面板1和面板2）- 对比与Sheet同名的GD文件差异
     */
    private void compareGdData() {
        gdDiffContext.differenceCells.clear();
        gdDiffContext.differenceRows.clear();

        // 基于原始数据进行对比
        if (gdDiffContext.columnNames1 == null || gdDiffContext.columnNames2 == null || gdDiffContext.allData1.isEmpty()
            || gdDiffContext.allData2.isEmpty()) {
            refreshGdTables();
            return;
        }

        Map<String, Integer> colMap1 = buildGdColumnMapFromNames(gdDiffContext.columnNames1);
        Map<String, Integer> colMap2 = buildGdColumnMapFromNames(gdDiffContext.columnNames2);
        List<String> commonCols = findCommonColumns(colMap1, colMap2);

        compareGdDataRowsFromRaw(colMap1, colMap2, commonCols);

        // 重新显示数据（只显示差异行）
        refreshGdDataWithFilter();
    }

    /**
     * 从列名构建GD 列映射
     */
    private Map<String, Integer> buildGdColumnMapFromNames(String[] columnNames) {
        Map<String, Integer> colMap = new HashMap<>();
        for (int i = 0; i < columnNames.length; i++) {
            // 从格式化的列名中提取原始列名（去掉类型信息）
            String colName = columnNames[i];
            int typeIndex = colName.indexOf('(');
            if (typeIndex > 0) {
                colName = colName.substring(0, typeIndex).trim();
            }
            colMap.put(colName, i);
        }
        return colMap;
    }

    /**
     * 找出共同列
     */
    private List<String> findCommonColumns(Map<String, Integer> colMap1, Map<String, Integer> colMap2) {
        List<String> commonCols = new ArrayList<>();
        for (String col : colMap1.keySet()) {
            if (colMap2.containsKey(col)) {
                commonCols.add(col);
            }
        }
        return commonCols;
    }

    /**
     * 对比GD数据行（基于原始数据）
     */
    private void compareGdDataRowsFromRaw(Map<String, Integer> colMap1, Map<String, Integer> colMap2,
        List<String> commonCols) {
        int rowCount1 = gdDiffContext.allData1.size();
        int rowCount2 = gdDiffContext.allData2.size();
        int maxRows = Math.max(rowCount1, rowCount2);

        for (int row = 0; row < maxRows; row++) {
            Object[] row1 = (row < rowCount1) ? gdDiffContext.allData1.get(row) : null;
            Object[] row2 = (row < rowCount2) ? gdDiffContext.allData2.get(row) : null;
            boolean rowHasDifference = false;

            for (String colName : commonCols) {
                Integer col1 = colMap1.get(colName);
                Integer col2 = colMap2.get(colName);

                if (col1 == null || col2 == null) {
                    continue;
                }

                Object value1 = (row1 != null && col1 < row1.length) ? row1[col1] : null;
                Object value2 = (row2 != null && col2 < row2.length) ? row2[col2] : null;

                if (compareValuesByType(value1, value2)) {
                    gdDiffContext.differenceCells.add(row + "," + col1);
                    gdDiffContext.differenceCells.add(row + "," + col2);
                    rowHasDifference = true;
                }
            }

            addDiff(row, rowHasDifference, row1, row2, gdDiffContext);
        }
    }

    /**
     * 刷新GD数据（只显示差异行）
     */
    private void refreshGdDataWithFilter() {
        if (gdDiffContext.differenceRows.isEmpty()) {
            refreshGdTables();
            return;
        }

        // 重新显示GD面板1（只显示差异行）
        if (gdDiffContext.columnNames1 != null && !gdDiffContext.allData1.isEmpty()) {
            displayGdDataFiltered1();
        }

        // 重新显示GD面板2（只显示差异行）
        if (gdDiffContext.columnNames2 != null && !gdDiffContext.allData2.isEmpty()) {
            displayGdDataFiltered2();
        }
    }

    /**
     * 显示过滤后的GD数据1（只显示差异行）
     */
    private void displayGdDataFiltered1() {
        if (gdDiffContext.columnNames1 == null || gdDiffContext.allData1.isEmpty()) {
            return;
        }

        // 保存原始数据（用于恢复）
        List<Object[]> originalData = new ArrayList<>(gdDiffContext.allData1);

        // 获取差异行数据（显示所有列，但只显示差异行）
        List<Object[]> filteredData = new ArrayList<>();
        List<Integer> diffRowList = new ArrayList<>(gdDiffContext.differenceRows);
        Collections.sort(diffRowList);

        gdDiffContext.rowMapping1.clear();
        int filteredRowIndex = 0;
        for (int originalRow : diffRowList) {
            if (originalRow < originalData.size()) {
                filteredData.add(originalData.get(originalRow));
                gdDiffContext.rowMapping1.put(filteredRowIndex, originalRow);
                filteredRowIndex++;
            }
        }

        // 更新数据列表（列名保持不变）
        gdDiffContext.allData1.clear();
        gdDiffContext.allData1.addAll(filteredData);

        // 重新计算分页并显示（第一页）
        displayGdPage2(1);

        // 同步面板2
        syncGdPagination();
    }

    /**
     * 显示过滤后的GD数据2（只显示差异行）
     */
    private void displayGdDataFiltered2() {
        if (gdDiffContext.columnNames2 == null || gdDiffContext.allData2.isEmpty()) {
            return;
        }

        // 保存原始数据（用于恢复）
        List<Object[]> originalData = new ArrayList<>(gdDiffContext.allData2);

        // 获取差异行数据（显示所有列，但只显示差异行）
        List<Object[]> filteredData = new ArrayList<>();
        List<Integer> diffRowList = new ArrayList<>(gdDiffContext.differenceRows);
        Collections.sort(diffRowList);

        gdDiffContext.rowMapping2.clear();
        int filteredRowIndex = 0;
        for (int originalRow : diffRowList) {
            if (originalRow < originalData.size()) {
                filteredData.add(originalData.get(originalRow));
                gdDiffContext.rowMapping2.put(filteredRowIndex, originalRow);
                filteredRowIndex++;
            }
        }

        // 更新数据列表（列名保持不变）
        gdDiffContext.allData2.clear();
        gdDiffContext.allData2.addAll(filteredData);

        // 重新计算分页并显示（第一页）
        displayGdPage2(1);

        // 同步面板1
        syncGdPagination();
    }

    /**
     * 创建状态栏
     */
    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEtchedBorder());

        logArea = new JTextPane();
        logArea.setEditable(false);
        logArea.setFont(new Font("宋体", Font.PLAIN, 16));

        JScrollPane logScrollPane = new JScrollPane(logArea);
        // 设置滚动条策略，确保在内容超出时显示滚动条
        logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        logScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        // 设置最小高度，但允许通过JSplitPane调整大小
        logScrollPane.setMinimumSize(new Dimension(0, 100));

        // 进度条
        JProgressBar progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);

        panel.add(logScrollPane, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.EAST);

        return panel;
    }

    public static String getNumber(Cell cell) {
        if (DateUtil.isCellDateFormatted(cell)) {
            Date date = cell.getDateCellValue();
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        } else {
            double value = cell.getNumericCellValue();
            if (value == (long)value) {
                return String.format("%d", (long)value);
            } else {
                return String.format("%.4f", value);
            }
        }
    }

    /**
     * 重新加载所有数据面板
     */
    private void reloadAllDataPanels(String sheetName) {
        // 重置分页控制器
        initPaginationController(excelPagination1);
        initPaginationController(excelPagination2);
        initPaginationController(gdPagination1);
        initPaginationController(gdPagination2);

        // 加载XML_PATH目录下的Excel数据（按当前Excel文件名查找）
        if (currentExcelOperate != null && currentExcelOperate.file != null) {
            String currentFileName = currentExcelOperate.file.getName();
            // 按文件名查找并加载XML_PATH Excel
            loadXmlPathExcelByFileName(currentFileName);

            // 如果加载成功，加载当前Sheet的数据
            if (xmlPathExcelOperate != null) {
                loadExcelSheetData(xmlPathExcelOperate, sheetName, true);
            } else {
                if (excelPagination1 != null) {
                    clearDataPanel(excelPagination1.tableModel, excelPagination1.rowLabel, excelPagination1.colLabel);
                    initPaginationController(excelPagination1);
                }
            }
        } else {
            if (excelPagination1 != null) {
                clearDataPanel(excelPagination1.tableModel, excelPagination1.rowLabel, excelPagination1.colLabel);
                initPaginationController(excelPagination1);
            }
        }

        // 加载当前目录下的Excel 数据
        if (currentExcelOperate != null) {
            loadExcelSheetData(currentExcelOperate, sheetName, false);
        } else {
            if (excelPagination2 != null) {
                clearDataPanel(excelPagination2.tableModel, excelPagination2.rowLabel, excelPagination2.colLabel);
                initPaginationController(excelPagination2);
            }
        }

        // 加载GD_PATH目录下的GD数据
        loadGdPathGdData(sheetName);

        // 加载当前目录下的GD 数据
        loadCurrentDirGdData(sheetName);

        // 对比Excel数据并高亮差异（GD数据会在加载完成后自动对比）
        compareExcelData();
    }

    /**
     * 清空GD数据面板1
     */
    private void clearGdDataPanel1() {
        if (gdPagination1 != null) {
            clearDataPanel(gdPagination1.tableModel, gdPagination1.rowLabel, gdPagination1.colLabel);
            initPaginationController(gdPagination1);
        }
        releaseGdPathGdData();
    }

    /**
     * 清空GD数据面板2
     */
    private void clearGdDataPanel2() {
        if (gdPagination2 != null) {
            clearDataPanel(gdPagination2.tableModel, gdPagination2.rowLabel, gdPagination2.colLabel);
            initPaginationController(gdPagination2);
        }
        releaseCurrentDirGdData();
    }

    /**
     * 显示Excel Sheet数据（支持分页）
     */
    private void displayExcelSheetData(SheetOperate sheetData, boolean isXmlPath) {
        if (sheetData == null) {
            if (isXmlPath) {
                if (excelPagination1 != null) {
                    clearDataPanel(excelPagination1.tableModel, excelPagination1.rowLabel, excelPagination1.colLabel);
                    initPaginationController(excelPagination1);
                }
            } else {
                if (excelPagination2 != null) {
                    clearDataPanel(excelPagination2.tableModel, excelPagination2.rowLabel, excelPagination2.colLabel);
                    initPaginationController(excelPagination2);
                }
            }
            return;
        }

        if (isXmlPath) {
            // 存储所有数据
            excelDiffContext.allData1.clear();
            excelDiffContext.allData1.addAll(sheetData.dataRows);
            excelDiffContext.columnNames1 = sheetData.headRows.get(2);

            // 重置分页控制器
            initPaginationController(excelPagination1);

            // 使用分页控制器显示第一页
            if (!excelDiffContext.allData1.isEmpty()) {
                showExcelPage(1);
            } else {
                clearDataPanel(excelPagination1.tableModel, excelPagination1.rowLabel, excelPagination1.colLabel);
            }
        } else {
            // 存储所有数据
            excelDiffContext.allData2.clear();
            excelDiffContext.allData2.addAll(sheetData.dataRows);
            excelDiffContext.columnNames2 = sheetData.headRows.get(2);

            // 重置分页控制器
            initPaginationController(excelPagination2);

            // 使用分页控制器显示第一页
            if (!excelDiffContext.allData2.isEmpty()) {
                displayExcelPage2(1);
            } else {
                clearDataPanel(excelPagination2.tableModel, excelPagination2.rowLabel, excelPagination2.colLabel);
            }
        }
    }

    /**
     * 创建GD数据面板1（GD_PATH目录） - 修复版，绑定到 showGdPage
     */
    private JPanel createGdDataPanel1(PaginationController otherController) {
        String title = "GD数据 (源GD)";
        // 创建分页控制器时绑定到 PAGE_CHANGE_GD（即 showGdPage）
        gdPagination1 = new PaginationController(Utils.DEFAULT_PAGE_SIZE, PAGE_CHANGE_GD, PREV_PAGE_CHANGE_GD);
        PanelCreationConfig config = new PanelCreationConfig(title, Object.class, gdPagination1, gdDiffContext);
        return createPanel(config, gdPagination1, otherController);
    }

    /**
     * 创建GD数据面板2（当前目录） - 修复版，绑定到 displayGdPage2
     */
    private JPanel createGdDataPanel2(PaginationController other) {
        String title = "GD数据 (当前目录)";
        // 创建分页控制器时绑定到 displayGdPage2
        gdPagination2 = new PaginationController(Utils.DEFAULT_PAGE_SIZE, this::displayGdPage2, PREV_PAGE_CHANGE_GD);
        PanelCreationConfig config = new PanelCreationConfig(title, Object.class, gdPagination2, gdDiffContext);
        return createPanel(config, gdPagination2, other);
    }
}