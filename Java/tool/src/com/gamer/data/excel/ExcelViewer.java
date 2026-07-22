package com.gamer.data.excel;

import com.gamer.data.excel.view.PaginationController;
import com.gamer.data.excel.view.PanelCreationConfig;
import com.gamer.data.gd.SheetOperate;
import com.gamer.data.gd.Utils;
import com.gamer.data.gen.Title;
import com.gamer.data.gen.model.ModelGen;
import com.gamer.data.gen.view.CheckBoxTreeCellRenderer;
import com.gamer.data.gen.view.CheckBoxTreeNodeSelectionListener;
import com.gamer.data.gen.view.SheetLineNode;
import com.gamer.data.gen.view.SheetNode;
import com.gamer.data.map.MapRingUtil;
import com.gamer.data.map.MapViewerPanel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.gamer.data.excel.BaseCheck.GD;
import static com.gamer.data.excel.view.ExcelGDDiffView.getNumber;

/**
 * ExcelOperate查看器，增加GD文件对比功能
 *
 * @author liuyunhui
 * @date 2025/12/05
 */
public class ExcelViewer extends AbstractViewFrame {
    // Sheet 列表组件（使用父类的sheetSelector，但为了兼容保留sheetList）
    private JList<String> sheetList;
    private DefaultListModel<String> sheetListModel;

    // 列信息表格模型
    private DefaultTableModel columnInfoTableModel;
    private DefaultTableModel gdColumnInfoTableModel;

    // 当前选中的Sheet 和GD 数据
    private Sheet currentSheet;
    private GdData currentGdData;

    // 主面板
    private JPanel mainPanel;
    // 当前显示的面板
    private JPanel currentCenterPanel;
    // 底部状态栏（非地图页面显示）
    private JPanel statusBarPanel;

    public ExcelViewer(BaseCheck baseCheck) {
        super(baseCheck);
        initUI();
        setupDragAndDrop();
    }

    /**
     * 重写updateSheetSelector，同时更新sheetListModel
     */
    @Override
    public void updateSheetSelector(final List<SheetOperate> sheetDataList) {
        // 先调用父类方法更新 sheetSelectorModel
        super.updateSheetSelector(sheetDataList);

        // 同时更新sheetListModel（用于ExcelOperateViewer的JList显示）
        if (sheetListModel != null) {
            SwingUtilities.invokeLater(() -> {
                sheetListModel.clear();
                if (sheetDataList != null) {
                    // 使用Set去重，避免重复添加
                    Set<String> addedSheets = new HashSet<>();
                    for (SheetOperate sheetData : sheetDataList) {
                        String sheetName = sheetData.sheetName;
                        // 检查是否已添加
                        if (!addedSheets.contains(sheetName)) {
                            String displayName = (sheetListModel.size() + 1) + ". " + sheetName;
                            sheetListModel.addElement(displayName);
                            addedSheets.add(sheetName);
                        }
                    }
                    logMessage("已更新Sheet列表: " + sheetListModel.size() + " 个Sheet");

                    // 自动选中第一个Sheet（延迟执行，确保面板已创建）
                    if (!sheetListModel.isEmpty() && sheetList != null && sheetList.getSelectedIndex() < 0) {
                        // 延迟执行，确保分页控制器已初始化
                        SwingUtilities.invokeLater(() -> {
                            if (excelPagination1 != null && gdPagination1 != null) {
                                sheetList.setSelectedIndex(0);
                            } else {
                                // 如果分页控制器还没初始化，再延迟一下
                                SwingUtilities.invokeLater(() -> {
                                    if (excelPagination1 != null && gdPagination1 != null) {
                                        sheetList.setSelectedIndex(0);
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * 使用SwingWorker 在后台加载数据
     */
    private void initSwingWorker() {
        SwingWorker<Void, String> dataLoader = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() {
                File[] files = listExcelFiles(baseCheck.XML_PATH);
                publish("开始加载Excel文件列表，共 " + (files == null ? 0 : files.length) + " 个文件");
                if (files != null && files.length > 0) {
                    // 首先清空列表
                    SwingUtilities.invokeLater(() -> listModel.clear());
                    // 逐个加载文件信息
                    for (File file : files) {
                        try {
                            // 在UI线程中更新列表
                            SwingUtilities.invokeLater(() -> listModel.addElement(file.getName()));
                        } catch (Exception e) {
                            publish("文件信息获取失败: " + e);
                            SwingUtilities.invokeLater(() -> listModel.addElement("文件信息获取失败"));
                        }
                    }
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String message : chunks) {
                    logMessage(message);
                }
            }

            @Override
            protected void done() {
                try {
                    get();
                    if (listModel.isEmpty()) {
                        logMessage("没有可用的Excel 文件");
                        JOptionPane.showMessageDialog(ExcelViewer.this, "没有找到可用的Excel文件", "提示",
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        logMessage("Excel文件列表加载完成!");
                        // 自动选中第一个文件
                        if (!listModel.isEmpty()) {
                            fileList.setSelectedIndex(0);
                        }
                    }
                } catch (Exception e) {
                    logMessage("加载数据失败: " + e);
                }
            }
        };
        dataLoader.execute();
    }

    /**
     * 查找对应的GD文件（根据Sheet名称）
     */
    private File findGdFile(String excelFileName, String sheetName) {
        if (excelFileName == null || !baseCheck.GD_PATH.exists()) {
            return null;
        }

        // 尝试多种可能的GD文件名格式

        // 1. Excel文件名_Sheet名.gd (最可能)
        String baseName = excelFileName;
        int dotIndex = excelFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            baseName = excelFileName.substring(0, dotIndex);
        }

        // 尝试不带Sheet名
        String[] possibleNames = {
            // Excel文件名_Sheet名.gd
            baseName + GD, // Excel文件名.gd
            sheetName + GD, // Sheet名.gd
        };

        // 在GD目录中查找
        for (String gdFileName : possibleNames) {

            String gdFile = gdFileName.substring(0, gdFileName.lastIndexOf("."));
            if (!sheetName.equals(gdFile)) {
                continue;
            }
            File file = new File(baseCheck.GD_PATH, gdFileName);
            if (file.exists() && file.isFile()) {
                return file;
            }
        }

        // 在子目录中查找
        File[] subDirs = baseCheck.GD_PATH.listFiles(File::isDirectory);
        if (subDirs != null) {
            for (File subDir : subDirs) {
                for (String gdFileName : possibleNames) {
                    File gdFile = new File(subDir, gdFileName);
                    if (gdFile.exists() && gdFile.isFile()) {
                        return gdFile;
                    }
                }
            }
        }

        logMessage("未找到GD文件，尝试了: " + String.join(", ", possibleNames));
        return null;
    }

    /**
     * 创建左侧面板
     */
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("Excel文件列表"));

        // 文件列表
        JScrollPane fileScrollPane = init();
        panel.add(fileScrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 异步加载GD文件（根据Sheet名称）
     */
    private void loadGdFileAsync(String excelFileName, String sheetName) {
        SwingWorker<Void, String> gdLoader = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() {
                publish("正在查找Sheet对应的GD文件...");
                publish("Excel文件: " + excelFileName + ", Sheet: " + sheetName);

                File gdFile = findGdFile(excelFileName, sheetName);
                if (gdFile == null) {
                    publish("未找到对应的GD文件");
                    return null;
                }

                publish("找到GD文件: " + gdFile.getAbsolutePath());
                publish("开始读取GD文件...");

                try {
                    currentGdData = GdFileReader.readGdFile(gdFile);
                    publish("GD文件读取成功: " + currentGdData.getRowCount() + "行, " + currentGdData.getColumnCount() + "列");
                } catch (Exception e) {
                    publish("读取GD文件失败: " + e);
                }

                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String message : chunks) {
                    logMessage(message);
                }
            }

            @Override
            protected void done() {
                try {
                    get();
                    // 确保分页控制器已初始化
                    if (gdPagination1 == null) {
                        logMessage("警告: GD分页控制器未初始化，延迟显示GD数据...");
                        SwingUtilities.invokeLater(() -> {
                            if (gdPagination1 != null && currentGdData != null) {
                                displayGdData(currentGdData);
                            } else if (currentGdData == null) {
                                gdColumnInfoTableModel.setRowCount(0);
                            }
                        });
                        return;
                    }

                    if (currentGdData != null) {
                        displayGdData(currentGdData);
                    } else {
                        // 如果没有GD数据，清空GD列信息
                        gdColumnInfoTableModel.setRowCount(0);
                    }
                } catch (Exception e) {
                    logMessage("加载GD文件时出错: " + e);
                }
            }
        };

        gdLoader.execute();
    }

    /**
     * 显示GD文件数据（复用AbstractViewFrame的逻辑）
     */
    private void displayGdData(GdData gdData) {
        if (gdData == null) {
            logMessage("GD数据为空");
            gdColumnInfoTableModel.setRowCount(0);
            if (gdPagination1 != null) {
                clearDataPanel(gdPagination1.tableModel, gdPagination1.rowLabel, gdPagination1.colLabel);
            }
            return;
        }

        try {
            // 存储所有数据到上下文
            gdDiffContext.allData1.clear();
            gdDiffContext.allData1.addAll(gdData.dataRows);

            // 设置列名（包含数据类型）
            int columnCount = gdData.getColumnCount();
            String[] columnHeaders = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                columnHeaders[i] = gdData.getFormattedColumnHeader(i);
            }
            gdDiffContext.columnNames1 = columnHeaders;

            logMessage("GD文件数据显示完成: " + gdData.getRowCount() + "行, " + gdData.getColumnCount() + "列");

            // 更新GD列信息表格
            updateGdColumnInfo(gdData);

            // 使用父类的分页显示方法
            SwingUtilities.invokeLater(() -> {
                // 确保分页控制器已初始化
                if (gdPagination1 == null) {
                    logMessage("错误: GD分页控制器未初始化", true);
                    return;
                }

                if (gdPagination1.tableModel != null && gdDiffContext.columnNames1 != null
                    && !gdDiffContext.allData1.isEmpty()) {
                    logMessage(
                        "准备显示GD数据: " + gdDiffContext.allData1.size() + "行, " + gdDiffContext.columnNames1.length + "列");
                    // 初始化分页控制器
                    initPaginationController(gdPagination1);
                    // 显示第一页数据
                    showGdPage(1);
                } else {
                    logMessage("ExcelOperateViewer GD数据未准备好 - gdPagination1: " + true + ", tableModel: "
                        + (gdPagination1.tableModel != null) + ", columnNames1: " + (gdDiffContext.columnNames1 != null)
                        + ", dataSize: " + gdDiffContext.allData1.size());
                    if (gdPagination1 != null && gdPagination1.tableModel != null) {
                        clearDataPanel(gdPagination1.tableModel, gdPagination1.rowLabel, gdPagination1.colLabel);
                    }
                }
            });

        } catch (Exception e) {
            logMessage("显示GD数据时出错: " + e);
        }
    }

    /**
     * 更新GD列信息表格
     */
    private void updateGdColumnInfo(GdData gdData) {
        if (gdData == null) {
            gdColumnInfoTableModel.setRowCount(0);
            return;
        }

        try {
            gdColumnInfoTableModel.setRowCount(0);
            // 使用GdData中的getColumnInfo方法获取列信息
            List<Object[]> columnInfo = gdData.getColumnInfo();
            for (Object[] row : columnInfo) {
                gdColumnInfoTableModel.addColumn(row);
            }
            logMessage("GD列信息已更新: " + columnInfo.size() + "列");
        } catch (Exception e) {
            logMessage("更新GD列信息时出错: " + e);
        }
    }

    /**
     * 设置整窗拖放，接受 .gd 文件并显示内容（与 FileMain 一致）
     */
    @SuppressWarnings("unchecked")
    private void setupDragAndDrop() {
        new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    Transferable transferable = dtde.getTransferable();

                    if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                        List<File> files = (List<File>)transferable.getTransferData(DataFlavor.javaFileListFlavor);

                        if (!files.isEmpty()) {
                            File file = files.get(0);
                            String name = file.getName();
                            boolean isGd = name.toLowerCase().endsWith(".gd");

                            if (isGd) {
                                SwingUtilities.invokeLater(() -> {
                                    try {
                                        Thread.sleep(100);
                                        openGdFileFromDrop(file);
                                    } catch (Exception e) {
                                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(ExcelViewer.this,
                                            "打开GD文件失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE));
                                    }
                                });

                                dtde.dropComplete(true);
                                return;
                            }
                        }
                    }

                    dtde.dropComplete(true);
                } catch (Exception e) {
                    dtde.dropComplete(false);
                    JOptionPane.showMessageDialog(ExcelViewer.this, "拖放失败: " + e, "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * 从拖放或选择打开 GD 文件：新开独立窗口（与 FileMain 一致的全面版），不拆分本面板。
     *
     * @param file
     *            GD 文件
     */
    private void openGdFileFromDrop(File file) {
        try {
            GdData gdData = GdFileReader.readGdFile(file);
            logMessage(
                "GD文件读取成功: " + file.getName() + " - " + gdData.getRowCount() + "行, " + gdData.getColumnCount() + "列");
            final GdViewerFrame frame = new GdViewerFrame(file, gdData);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    frame.setVisible(true);
                }
            });
        } catch (Exception e) {
            logMessage("读取GD文件失败: " + e.getMessage(), true);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(ExcelViewer.this, "读取GD文件失败: " + e.getMessage(), "错误",
                        JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }

    /**
     * 选择 GD 文件并打开显示（与 FileMain 一致，点击「拖放GD文件」时调用）
     */
    private void selectGdFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择GD文件");
        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    return true;
                }
                String name = f.getName();
                return name.toLowerCase().endsWith(".gd");
            }

            @Override
            public String getDescription() {
                return "GD文件 (*.gd)";
            }
        });

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            openGdFileFromDrop(file);
        }
    }

    /**
     * 初始化 UI
     */
    private void initUI() {
        setTitle("Excel 与GD 文件查看器和excel生成器和地图展示");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        createMenuBar();
        createInitPanel();
    }

    /**
     * 创建菜单栏
     */
    private void createMenuBar() {
        // 创建菜单栏
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("文件");

        // 创建模型代码生成菜单项
        JMenuItem genCodeItem = new JMenuItem("生成模型代码");
        genCodeItem.addActionListener(e -> showExcelSelectionPanel(baseCheck.XML_PATH));
        fileMenu.add(genCodeItem);

        // 创建Excel查看器菜单项
        JMenuItem excelViewerItem = new JMenuItem("Excel与GD文件对比查看器");
        excelViewerItem.addActionListener(e -> showExcelPanel());
        fileMenu.add(excelViewerItem);

        // 地图范围查看器菜单项
        JMenuItem mapViewerItem = new JMenuItem("地图范围查看器");
        mapViewerItem.addActionListener(e -> showMapViewerPanel());
        fileMenu.add(mapViewerItem);

        // 打开GD文件菜单项
        JMenuItem openGdItem = new JMenuItem("打开GD文件");
        openGdItem.addActionListener(e -> selectGdFile());
        fileMenu.add(openGdItem);

        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    /**
     * 创建初始面板
     */
    private void createInitPanel() {
        // 全屏显示
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        // 创建主面板（BorderLayout）
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 创建中部欢迎面板
        currentCenterPanel = createWelcomePanel();
        mainPanel.add(currentCenterPanel, BorderLayout.CENTER);

        // 添加底部状态栏（共享的日志区域）
        statusBarPanel = createStatusBar();
        mainPanel.add(statusBarPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setVisible(true);

        // 直接展示生成模型代码页面（默认路径 Excel 树）
        SwingUtilities.invokeLater(() -> showExcelSelectionPanel(baseCheck.XML_PATH));
    }

    /**
     * 创建欢迎面板
     */
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(50, 50, 50, 50));

        JLabel welcomeLabel = new JLabel("Excel与GD文件查看器和代码生成器", JLabel.CENTER);
        welcomeLabel.setFont(new Font("宋体", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(0, 102, 204));

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(welcomeLabel, BorderLayout.CENTER);

        // 添加说明文字
        JTextArea description = getTextArea(panel);

        contentPanel.add(description, BorderLayout.SOUTH);

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    private static JTextArea getTextArea(JPanel panel) {
        JTextArea description = new JTextArea();
        description.setEditable(false);
        description.setFont(new Font("宋体", Font.PLAIN, 16));
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setText(
            "功能说明：\n" + "1. 选择\"生成模型代码\"菜单项，可以生成Excel对应的模型代码\n" + "2. 选择\"Excel与GD文件对比查看器\"菜单项，可以查看和对比Excel与GD文件数据\n"
                + "3. 选择\"地图范围查看器\"菜单项，可加载 map/level 地图并查看 getPointsInRingByLevel 范围\n" + "\n" + "使用步骤：\n"
                + "1. 选择相应功能菜单项\n" + "2. 操作将在底部日志区域显示进度和结果\n" + "3. 所有日志信息都会显示在底部的状态栏中");
        description.setBackground(panel.getBackground());
        return description;
    }

    /**
     * 显示Excel文件选择和Sheet选择面板
     */
    private void showExcelSelectionPanel(File dir) {
        toggleStatusBar(true);
        logMessage("开始扫描Excel文件...");

        // 清空之前的中心面板
        if (currentCenterPanel != null) {
            mainPanel.remove(currentCenterPanel);
        }

        // 创建主面板
        JPanel selectionPanel = new JPanel(new BorderLayout(10, 10));
        selectionPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 标题
        JLabel titleLabel = new JLabel("选择要生成模型代码的Excel文件和Sheet", JLabel.CENTER);
        titleLabel.setFont(new Font("宋体", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204));
        selectionPanel.add(titleLabel, BorderLayout.NORTH);

        // 创建树形选择面板
        JPanel treePanel = new JPanel(new BorderLayout(5, 5));
        treePanel.setBorder(new TitledBorder("Excel文件和Sheet列表"));

        // 创建树模型
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Excel文件");
        JTree tree = new JTree(root);

        // 使用SwingWorker在后台加载Excel文件
        loadXml(dir, tree);
        // 设置树渲染器，显示复选框
        tree.setCellRenderer(new CheckBoxTreeCellRenderer());
        tree.addMouseListener(new CheckBoxTreeNodeSelectionListener(tree));

        JScrollPane treeScrollPane = new JScrollPane(tree);
        treeScrollPane.setPreferredSize(new Dimension(400, 300));
        treePanel.add(treeScrollPane, BorderLayout.CENTER);

        treePanel.add(createControl(tree), BorderLayout.SOUTH);
        selectionPanel.add(treePanel, BorderLayout.CENTER);

        // 更新当前中心面板
        currentCenterPanel = selectionPanel;
        mainPanel.add(currentCenterPanel, BorderLayout.CENTER);

        // 刷新界面
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /**
     * 加载 xml
     */
    private void loadXml(File dir, JTree tree) {
        SwingWorker<Void, FileWithSheets> worker = new SwingWorker<Void, FileWithSheets>() {
            @Override
            protected Void doInBackground() {
                File[] excelFiles = getExcelFiles(dir);
                if (excelFiles == null || excelFiles.length == 0) {
                    logMessage("没有找到Excel文件", true);
                    return null;
                }
                baseCheck.XML_PATH = dir;
                logMessage("找到 " + excelFiles.length + " 个Excel文件,正在读取Sheet信息...");
                FileWithSheets fileWithSheets;
                for (File file : excelFiles) {
                    try {
                        fileWithSheets = getExcelSheets(file);
                        if (fileWithSheets != null && !fileWithSheets.sheets.isEmpty()) {
                            logMessage("读取文件: " + file.getName() + " 成功");
                            // 需要把结果传给process
                            publish(fileWithSheets);
                        } else {
                            logMessage("读取文件: " + file.getName() + " 失败" + "没有找到Sheet信息", true);
                        }
                    } catch (Exception e) {
                        logMessage("读取文件失败: " + file.getName() + " - " + Arrays.toString(e.getStackTrace()), true);
                    }
                }

                return null;
            }

            @Override
            protected void process(List<FileWithSheets> chunks) {
                addSelectedNode(tree, chunks);
            }

            @Override
            protected void done() {
                try {
                    get();
                    logMessage("Excel文件扫描完成");
                } catch (Exception e) {
                    logMessage("扫描Excel文件时出错: " + e, true);
                }
            }
        };

        worker.execute();
    }

    /**
     * 处理选中的数据
     */
    private void addSelectedNode(JTree tree, List<FileWithSheets> chunks) {
        DefaultMutableTreeNode fileNode;
        DefaultMutableTreeNode sheetNode;
        for (FileWithSheets fileWithSheets : chunks) {
            if (fileWithSheets.excelName == null || fileWithSheets.sheets == null) {
                // 没有找到文件的情况
                JOptionPane.showMessageDialog(ExcelViewer.this, "没有找到Excel文件", "提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // 添加文件节点
            fileNode = new DefaultMutableTreeNode(new SheetNode(fileWithSheets.excelName, false));

            // 添加Sheet子节点
            for (Map.Entry<String, List<Title>> entry : fileWithSheets.sheets.entrySet()) {
                String sheetName = entry.getKey();
                sheetNode = new DefaultMutableTreeNode(new SheetNode(sheetName, false));
                // 这里我还想把sheet的列名也加到sheetNode子节点中 列名就是Title的
                for (Title title : entry.getValue()) {
                    sheetNode.add(new DefaultMutableTreeNode(new SheetLineNode(title, false)));
                }
                fileNode.add(sheetNode);
            }

            // 这里节点没显示出来
            ((DefaultMutableTreeNode)tree.getModel().getRoot()).add(fileNode);
            // 更新树
            ((DefaultTreeModel)tree.getModel()).reload();
            logMessage("已加载: " + fileWithSheets.excelName + " (" + fileWithSheets.sheets.size() + "个Sheet)");
        }
    }

    /**
     * 创建控制面板
     */
    private JPanel createControl(JTree tree) {
        // 创建控制按钮面板
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // 全选按钮
        JButton selectAllButton = new JButton("全选");
        selectAllButton.addActionListener(e -> selectAllNodes(tree, true));

        // 取消全选按钮
        JButton selectNoneButton = new JButton("取消全选");
        selectNoneButton.addActionListener(e -> selectAllNodes(tree, false));

        // 生成按钮
        JButton generateButton = getJButton(tree);

        controlPanel.add(selectAllButton);
        controlPanel.add(selectNoneButton);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(generateButton);
        return controlPanel;
    }

    private JButton getJButton(JTree tree) {
        JButton generateButton = new JButton("生成模型代码");
        generateButton.setFont(new Font("宋体", Font.BOLD, 14));
        generateButton.setForeground(Color.RED);
        generateButton.addActionListener(e -> {
            Map<String, FileWithSheets> selectedData = getSelectedData(tree);
            if (selectedData.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请选择至少一个Sheet", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            logMessage("开始生成选中的模型代码...");
            System.out.println("开始生成选中的模型代码...");
            ModelGen.genCode(new ArrayList<>(selectedData.values()), this);
        });
        return generateButton;
    }

    /**
     * 获取目录下的所有Excel文件
     */
    private File[] getExcelFiles(File dir) {
        return dir.listFiles((file) -> {
            String name = file.getName().toLowerCase();
            String[] split = name.split("\\.");
            if (split.length != 2) {
                return false;
            }
            return !Utils.checkNotFitSheetName(split[0]) && (split[1].equals("xlsx") || split[1].equals("xls"));
        });
    }

    /**
     * 获取Excel文件的Sheet列表（立即关闭工作簿）
     */
    private FileWithSheets getExcelSheets(File file) {
        FileWithSheets fileWithSheets = null;

        // 目前遇到了上面这个方法会改打开后改表
        // try (Workbook workbook = WorkbookFactory.create(file)) {
        try (Workbook workbook = WorkbookFactory.create(file, null, true)) {
            int sheetCount = workbook.getNumberOfSheets();

            for (int i = 0; i < sheetCount; i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();
                if (sheetName == null) {
                    continue;
                }
                sheetName = sheetName.trim();
                if (sheetName.isEmpty()) {
                    continue;
                }

                if (Utils.checkNotFitSheetName(sheetName)) {
                    continue;
                }
                if (fileWithSheets == null) {
                    fileWithSheets = new FileWithSheets(file.getName());
                }
                // 把sheet 列名读出来存到fileWithSheets.sheets中
                ModelGen.setJavaTitle(fileWithSheets, sheetName, sheet);
            }

            logMessage("读取文件: " + file.getName() + ", Sheet数量: " + sheetCount);
        } catch (Exception e) {
            logMessage("读取文件失败: " + file.getName() + " - " + e, true);
            e.printStackTrace();
        }
        return fileWithSheets;
    }

    /**
     * 选择或取消选择所有节点
     */
    private void selectAllNodes(JTree tree, boolean select) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree.getModel().getRoot();
        selectAllNodesRecursive(root, select);
        tree.repaint();
    }

    /**
     * 递归选择或取消选择所有节点
     */
    private void selectAllNodesRecursive(DefaultMutableTreeNode node, boolean select) {
        if (node.getUserObject() instanceof SheetNode) {
            SheetNode data = (SheetNode)node.getUserObject();
            data.selected = select;
        }

        for (int i = 0; i < node.getChildCount(); i++) {
            selectAllNodesRecursive((DefaultMutableTreeNode)node.getChildAt(i), select);
        }
    }

    /**
     * 获取选中的数据
     */
    private Map<String, FileWithSheets> getSelectedData(JTree tree) {
        Map<String, FileWithSheets> selectedData = new HashMap<>();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree.getModel().getRoot();
        for (int index = 0; index < root.getChildCount(); index++) {
            getSelectedDataRecursive((DefaultMutableTreeNode)root.getChildAt(index), selectedData);
        }
        return selectedData;
    }

    /**
     * 递归获取选中的数据
     */
    private void getSelectedDataRecursive(DefaultMutableTreeNode node, Map<String, FileWithSheets> selectedData) {
        FileWithSheets sheets = null;
        if (node.getUserObject() instanceof SheetNode) {
            SheetNode data = (SheetNode)node.getUserObject();
            if (data.selected && data.name != null) {
                // 这是一个文件节点
                sheets = selectedData.computeIfAbsent(data.name, k -> new FileWithSheets(data.name));
            }
        }

        // 收集选中的Sheet
        if (node.getChildCount() > 0) {
            for (int i = 0; i < node.getChildCount(); i++) {
                DefaultMutableTreeNode child = (DefaultMutableTreeNode)node.getChildAt(i);
                if (child.getUserObject() instanceof SheetNode) {
                    SheetNode childData = (SheetNode)child.getUserObject();
                    if (childData.selected) {
                        // 找到对应的文件节点
                        if (sheets == null) {
                            logMessage("没有找到对应的文件节点", true);
                            continue;
                        }
                        // 把子节点中的Title加到sheets中
                        for (int j = 0; j < child.getChildCount(); j++) {
                            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)child.getChildAt(j);
                            if (childNode == null) {
                                continue;
                            }
                            SheetLineNode lineNode = (SheetLineNode)childNode.getUserObject();
                            if (lineNode == null) {
                                continue;
                            }
                            if (!lineNode.selected) {
                                continue;
                            }
                            sheets.addSheet(childData.name, lineNode.title);
                        }
                    }
                }
            }
        }
    }

    /**
     * 显示Excel面板
     */
    private void showExcelPanel() {
        toggleStatusBar(true);
        logMessage("初始化Excel与GD文件对比查看器...");

        // 清空之前的中心面板
        if (currentCenterPanel != null) {
            mainPanel.remove(currentCenterPanel);
        }

        // 创建主分割面板（左右）
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // 设置文件列表宽度为屏幕宽度的1/8
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int fileListWidth = screenSize.width / 8;
        mainSplitPane.setDividerLocation(fileListWidth);
        mainSplitPane.setResizeWeight(0.125); // 1/8 = 0.125

        // 左侧面板（文件列表）
        mainSplitPane.setLeftComponent(createLeftPanel());

        // 右侧面板（详细信息）
        mainSplitPane.setRightComponent(createRightPanel());

        // 创建容器面板
        JPanel excelPanel = new JPanel(new BorderLayout());
        excelPanel.add(mainSplitPane, BorderLayout.CENTER);

        // 更新当前中心面板
        currentCenterPanel = excelPanel;
        mainPanel.add(currentCenterPanel, BorderLayout.CENTER);

        // 刷新界面
        mainPanel.revalidate();
        mainPanel.repaint();

        // 初始化数据加载
        initSwingWorker();
        logMessage("等待加载Excel文件...");
    }

    /**
     * 显示地图范围查看器面板
     */
    private void showMapViewerPanel() {
        // 地图查看器隐藏底部状态栏，让地图向下铺满
        toggleStatusBar(false);
        logMessage("初始化地图范围查看器...");
        if (currentCenterPanel != null) {
            mainPanel.remove(currentCenterPanel);
        }
        File mapDir = new File(baseCheck.baseDir, MapRingUtil.MAP);
        if (!mapDir.isDirectory()) {
            mapDir = new File(baseCheck.CURR_DIR.getParentFile().getParentFile(), MapRingUtil.MAP);
        }
        currentCenterPanel = new MapViewerPanel(mapDir);
        mainPanel.add(currentCenterPanel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
        logMessage("地图范围查看器已打开，目录: " + mapDir.getAbsolutePath());
    }

    /**
     * 切换状态栏的显示与隐藏
     * 
     * @param visible
     *            是否显示
     */
    private void toggleStatusBar(boolean visible) {
        if (mainPanel == null || statusBarPanel == null) {
            return;
        }
        if (visible) {
            if (statusBarPanel.getParent() != mainPanel) {
                mainPanel.add(statusBarPanel, BorderLayout.SOUTH);
            }
        } else if (statusBarPanel.getParent() == mainPanel) {
            mainPanel.remove(statusBarPanel);
        }
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /**
     * 创建右侧面板
     */
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // 创建右侧垂直分割面板（Sheet列表在上，数据在下）
        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        // Sheet列表高度设为150px
        rightSplitPane.setDividerLocation(150);
        rightSplitPane.setResizeWeight(0.15);

        // 上部面板（Sheet列表）
        rightSplitPane.setTopComponent(createSheetListPanel());

        // 下部面板（数据展示）
        rightSplitPane.setBottomComponent(createBottomPanel());

        panel.add(rightSplitPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 创建Sheet列表面板
     */
    private JPanel createSheetListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("Sheet列表 (点击切换)"));
        panel.setPreferredSize(new Dimension(0, 150));

        sheetListModel = new DefaultListModel<>();
        sheetList = new JList<>(sheetListModel);
        sheetList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sheetList.setFont(new Font("宋体", Font.PLAIN, 14));
        sheetList.setFixedCellHeight(30);

        // Sheet列表选择监听 - 修复：切换Sheet时重新加载GD文件
        sheetList.addListSelectionListener(listSelectionListener);

        JScrollPane sheetScrollPane = new JScrollPane(sheetList);
        panel.add(sheetScrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Sheet 列表选择监听器
     */
    private final ListSelectionListener listSelectionListener = new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = sheetList.getSelectedIndex();
                if (selectedIndex >= 0 && currentExcelOperate != null) {
                    // 获取Sheet 显示名称（格式：1. SheetName）
                    String selectedItem = sheetListModel.getElementAt(selectedIndex);
                    // 提取实际的Sheet名称（去掉序号前缀）
                    String sheetName = extractSheetName(selectedItem);

                    logMessage("选择Sheet: " + sheetName + " (索引: " + selectedIndex + ")");
                    // 确保面板已创建
                    if (excelPagination1 == null || gdPagination1 == null) {
                        logMessage("警告: 分页控制器未初始化，等待面板创建...");
                        // 延迟一下，等待面板创建
                        SwingUtilities.invokeLater(() -> {
                            if (excelPagination1 != null && gdPagination1 != null) {
                                loadSheetData(sheetName);
                            } else {
                                logMessage("错误: 分页控制器仍未初始化", true);
                            }
                        });
                    } else {
                        // 加载Sheet 数据
                        loadSheetData(sheetName);
                    }
                }
            }
        }
    };

    /**
     * 从显示名称中提取实际的Sheet名称（去掉"序号. "前缀）
     */
    private String extractSheetName(String displayName) {
        if (displayName == null) {
            return "";
        }
        // 格式：1. SheetName 或 10. SheetName
        int dotIndex = displayName.indexOf(". ");
        if (dotIndex > 0) {
            return displayName.substring(dotIndex + 2);
        }
        return displayName;
    }

    /**
     * 创建底部面板（复用AbstractViewFrame的逻辑）
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // 创建底部水平分割面板，分成三部分：列信息、Excel数据、GD数据
        JSplitPane bottomSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        bottomSplitPane.setDividerLocation(300);
        bottomSplitPane.setResizeWeight(0.2);
        rightSplitPane.setDividerLocation(0.5);
        rightSplitPane.setResizeWeight(0.5);

        // 左侧：列信息面板
        bottomSplitPane.setLeftComponent(createColumnInfoPanel());

        // 中间：Excel数据表格面板（使用父类方法）
        bottomSplitPane.setRightComponent(createExcelDataPanel());

        // 右侧：GD数据表格面板（使用父类方法）
        rightSplitPane.setRightComponent(createGdDataPanel());

        rightSplitPane.setLeftComponent(bottomSplitPane);
        panel.add(rightSplitPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 创建列信息面板
     */
    private JPanel createColumnInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("列信息对比"));

        // 创建选项卡面板，包含Excel列信息和GD列信息
        JTabbedPane tabbedPane = new JTabbedPane();

        // Excel列信息表格
        String[] columnNames = {"序号", "列名", "数据类型", "示例值"};
        columnInfoTableModel = initDefaultTableModel(columnNames);

        tabbedPane.addTab("Excel列", newJScrollPane(columnInfoTableModel));

        // GD列信息表格
        gdColumnInfoTableModel = initDefaultTableModel(columnNames);

        tabbedPane.addTab("GD列", newJScrollPane(gdColumnInfoTableModel));

        panel.add(tabbedPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 创建默认表格模型
     */
    private DefaultTableModel initDefaultTableModel(String[] columnNames) {
        return new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };
    }

    /**
     * 创建新的滚动面板
     */
    private JScrollPane newJScrollPane(DefaultTableModel tableModel) {
        JTable excelColumnInfoTable = new JTable(tableModel);
        excelColumnInfoTable.setRowHeight(25);
        excelColumnInfoTable.setFont(new Font("宋体", Font.PLAIN, 12));

        // 设置列宽
        excelColumnInfoTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        excelColumnInfoTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        excelColumnInfoTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        excelColumnInfoTable.getColumnModel().getColumn(3).setPreferredWidth(150);

        return new JScrollPane(excelColumnInfoTable);
    }

    /**
     * 创建Excel数据面板（复用AbstractViewFrame的逻辑）
     */
    private JPanel createExcelDataPanel() {
        String title = "Excel数据预览";

        // 创建分页控制器
        excelPagination1 = new PaginationController(Utils.DEFAULT_PAGE_SIZE, PAGE_CHANGE, PREV_PAGE_CHANGE);

        // 创建自定义TableModel（Excel特有的颜色显示）

        // 设置 tableModel
        excelPagination1.tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };

        // 创建 JTable
        JTable table = new JTable(excelPagination1.tableModel);
        table.setRowHeight(25);
        table.setFont(new Font("宋体", Font.PLAIN, 12));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        excelPagination1.table = table;

        // 创建 JScrollPane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        excelPagination1.scrollPane = scrollPane;

        // 创建标签
        JLabel rowLabel = new JLabel("行数: 0");
        JLabel colLabel = new JLabel("列数: 0");
        excelPagination1.rowLabel = rowLabel;
        excelPagination1.colLabel = colLabel;

        // 创建面板
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder(title));

        // 创建头部面板（使用BorderLayout以支持分页控件）
        JPanel headerPanel = new JPanel(new BorderLayout());
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.add(rowLabel);
        statsPanel.add(Box.createHorizontalStrut(20));
        statsPanel.add(colLabel);
        headerPanel.add(statsPanel, BorderLayout.WEST);

        // 添加分页控件
        JPanel paginationPanel = excelPagination1.createPaginationPanel();
        headerPanel.add(paginationPanel, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 创建GD数据面板（复用AbstractViewFrame的逻辑）
     */
    private JPanel createGdDataPanel() {
        String title = "GD文件数据预览";

        // 创建分页控制器
        gdPagination1 = new PaginationController(Utils.DEFAULT_PAGE_SIZE, PAGE_CHANGE_GD, PREV_PAGE_CHANGE_GD);

        // 使用父类的工厂方法创建config
        PanelCreationConfig config = createPanelConfig(title, gdPagination1, gdDiffContext);

        return createPanel(config, gdPagination1, null);
    }

    /**
     * 异步加载Excel数据（复用AbstractViewFrame的逻辑）
     */
    private void loadExcelDataAsync(String sheetName) {
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            private final List<Object[]> columnInfoList = new ArrayList<>();

            @Override
            protected Void doInBackground() {
                publish("开始加载Excel数据: " + sheetName);

                // 获取表头信息
                analyzeExcelHeaders(columnInfoList);

                // 加载所有数据到上下文
                loadAllExcelDataToContext();

                publish("Excel数据加载完成,共 " + excelDiffContext.allData1.size() + " 行");
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String message : chunks) {
                    logMessage(message);
                }
            }

            @Override
            protected void done() {
                try {
                    get();
                    // 初始化列信息表格
                    updateColumnInfoTable(columnInfoList);

                    // 使用父类的分页显示方法
                    SwingUtilities.invokeLater(() -> {
                        // 确保分页控制器已初始化
                        if (excelPagination1 == null) {
                            logMessage("错误: Excel分页控制器未初始化", true);
                            return;
                        }

                        if (excelPagination1.tableModel != null && excelDiffContext.columnNames1 != null
                            && !excelDiffContext.allData1.isEmpty()) {
                            logMessage("准备显示Excel数据: " + excelDiffContext.allData1.size() + "行, "
                                + excelDiffContext.columnNames1.length + "列");
                            // 初始化分页控制器
                            initPaginationController(excelPagination1);
                            // 显示第一页数据
                            showExcelPage(1);
                        } else {
                            logMessage("ExcelOperateViewer Excel数据未准备好 - excelPagination1: " + true + ", tableModel: "
                                + (excelPagination1.tableModel != null) + ", columnNames1: "
                                + (excelDiffContext.columnNames1 != null) + ", dataSize: "
                                + excelDiffContext.allData1.size());
                            if (excelPagination1 != null && excelPagination1.tableModel != null) {
                                clearDataPanel(excelPagination1.tableModel, excelPagination1.rowLabel,
                                    excelPagination1.colLabel);
                            }
                        }
                    });

                    logMessage("Excel Sheet数据加载完成: " + sheetName);
                } catch (Exception e) {
                    logMessage("加载Excel数据失败: " + e, true);
                    JOptionPane.showMessageDialog(ExcelViewer.this, "加载Excel数据失败: " + e, "错误",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    /**
     * 分析Excel表头
     */
    private void analyzeExcelHeaders(List<Object[]> columnInfoList) {
        Row headerRow = currentSheet.getRow(0);
        if (headerRow == null) {
            logMessage("没有找到表头行");
            return;
        }

        int columnCount = headerRow.getLastCellNum();
        String[] columnNames = new String[columnCount];
        logMessage("Excel列数: " + columnCount);

        int realRows = currentSheet.getLastRowNum() + 1;
        int sampleRows = Math.min(10, realRows - 1);

        // 分析每一列的数据
        for (int col = 0; col < columnCount; col++) {
            Cell headerCell = headerRow.getCell(col);
            String columnName = headerCell != null ? getCellValueAsString(headerCell) : "列" + (col + 1);
            columnNames[col] = columnName;

            // 收集列信息和示例值
            String dataType = analyzeColumnDataType(col, sampleRows);
            String sampleValue = getSampleValue(col, realRows);
            columnInfoList.add(new Object[] {col + 1, columnName, dataType, sampleValue});
            logMessage("分析列 " + columnName + ": " + dataType);
        }

        // 存储列名到上下文
        excelDiffContext.columnNames1 = columnNames;
    }

    /**
     * 加载所有Excel数据到上下文（复用AbstractViewFrame的逻辑）
     */
    private void loadAllExcelDataToContext() {
        int realRows = currentSheet.getLastRowNum() + 1;
        int columnCount = excelDiffContext.columnNames1 != null ? excelDiffContext.columnNames1.length : 0;

        // 清空之前的数据
        excelDiffContext.allData1.clear();

        // 加载所有数据行（从第1行开始，第0行是表头）
        for (int rowNum = 1; rowNum < realRows; rowNum++) {
            Row row = currentSheet.getRow(rowNum);
            if (row == null) {
                excelDiffContext.allData1.add(new Object[columnCount]);
                continue;
            }

            Object[] rowData = new Object[columnCount];
            for (int col = 0; col < columnCount; col++) {
                Cell cell = row.getCell(col);
                rowData[col] = cell != null ? getCellValueAsString(cell) : "";
            }
            excelDiffContext.allData1.add(rowData);

            // 每加载100行更新一次进度
            if (rowNum % 100 == 0) {
                logMessage("已加载 " + rowNum + " 行Excel数据");
            }
        }
    }

    /**
     * 更新列信息表格
     */
    private void updateColumnInfoTable(List<Object[]> columnInfoList) {
        columnInfoTableModel.setRowCount(0);
        for (Object[] row : columnInfoList) {
            columnInfoTableModel.addRow(row);
        }
    }

    /**
     * 创建状态栏（共享的日志区域）
     */
    private JPanel createStatusBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        // 添加标题标签
        JLabel titleLabel = new JLabel("日志输出:");
        titleLabel.setFont(new Font("宋体", Font.BOLD, 14));
        panel.add(titleLabel, BorderLayout.NORTH);

        logArea = new JTextPane();
        logArea.setEditable(false);
        logArea.setFont(new Font("宋体", Font.PLAIN, 14));

        JScrollPane logScrollPane = new JScrollPane(logArea);
        // 设置滚动条策略，确保在内容超出时显示滚动条
        logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        logScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        // 设置固定高度
        logScrollPane.setPreferredSize(new Dimension(0, 200));

        panel.add(logScrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * 加载Sheet数据
     */
    private void loadSheetData(String sheetName) {
        if (currentExcelOperate == null) {
            logMessage("当前Excel文件为空，无法加载Sheet数据");
            return;
        }

        // 确保分页控制器已初始化
        if (excelPagination1 == null || gdPagination1 == null) {
            logMessage("警告: 分页控制器未初始化，延迟加载Sheet数据...");
            SwingUtilities.invokeLater(() -> loadSheetData(sheetName));
            return;
        }

        try {
            // 如果workbook已关闭，重新打开
            if (currentExcelOperate.workbook == null) {
                logMessage("Workbook已关闭，重新打开文件: " + getFileName(currentExcelOperate));
                currentExcelOperate.init(log);
                if (!currentExcelOperate.init || currentExcelOperate.workbook == null) {
                    logMessage("重新打开文件失败: " + getFileName(currentExcelOperate));
                    return;
                }
            }

            Workbook workbook = currentExcelOperate.workbook;

            // 根据sheetName查找实际的sheet索引（因为sheetIndex可能不准确）
            int actualSheetIndex = -1;
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                if (sheet.getSheetName().equals(sheetName)) {
                    actualSheetIndex = i;
                    break;
                }
            }

            if (actualSheetIndex < 0) {
                logMessage("未找到Sheet: " + sheetName);
                return;
            }

            currentSheet = workbook.getSheetAt(actualSheetIndex);
            String excelFileName = currentExcelOperate.file != null ? currentExcelOperate.file.getName() : "未知文件";

            logMessage("加载Sheet: " + sheetName + " (来自文件: " + excelFileName + ", 索引: " + actualSheetIndex + ")");

            // 先清空所有数据上下文
            excelDiffContext.allData1.clear();
            excelDiffContext.columnNames1 = null;
            gdDiffContext.allData1.clear();
            gdDiffContext.columnNames1 = null;

            // 清空GD数据表格和列信息
            gdColumnInfoTableModel.setRowCount(0);
            if (gdPagination1 != null) {
                clearDataPanel(gdPagination1.tableModel, gdPagination1.rowLabel, gdPagination1.colLabel);
                initPaginationController(gdPagination1);
            }

            // 清空Excel数据表格
            if (excelPagination1 != null) {
                clearDataPanel(excelPagination1.tableModel, excelPagination1.rowLabel, excelPagination1.colLabel);
                initPaginationController(excelPagination1);
            }

            // 同时加载GD文件
            if (currentExcelOperate.file != null) {
                loadGdFileAsync(currentExcelOperate.file.getName(), sheetName);
            }
            // 使用SwingWorker在后台加载Excel数据
            loadExcelDataAsync(sheetName);
        } catch (Exception e) {
            logMessage("加载Sheet数据失败: " + e, true);
        }
    }

    /**
     * 分析列数据类型
     */
    private String analyzeColumnDataType(int columnIndex, int sampleRows) {
        int stringCount = 0;
        int numberCount = 0;
        int dateCount = 0;
        int booleanCount = 0;
        for (int rowNum = 1; rowNum <= Math.min(sampleRows + 1, currentSheet.getLastRowNum()); rowNum++) {
            Row row = currentSheet.getRow(rowNum);
            if (row == null)
                continue;

            Cell cell = row.getCell(columnIndex);
            if (cell == null) {
                continue;
            }

            switch (cell.getCellType()) {
                case STRING:
                    stringCount++;
                    break;
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        dateCount++;
                    } else {
                        numberCount++;
                    }
                    break;
                case BOOLEAN:
                    booleanCount++;
                    break;
                case FORMULA:
                    // 公式单元格，尝试获取其值类型
                    try {
                        switch (cell.getCachedFormulaResultType()) {
                            case NUMERIC:
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    dateCount++;
                                } else {
                                    numberCount++;
                                }
                                break;
                            case BOOLEAN:
                                booleanCount++;
                                break;
                            default:
                                stringCount++;
                        }
                    } catch (Exception e) {
                        stringCount++;
                    }
                    break;
                default:
                    break;
            }
        }

        // 确定主要数据类型
        if (numberCount > stringCount && numberCount > dateCount && numberCount > booleanCount) {
            return "数字";
        } else if (dateCount > stringCount && dateCount > numberCount && dateCount > booleanCount) {
            return "日期";
        } else if (booleanCount > stringCount && booleanCount > numberCount && booleanCount > dateCount) {
            return "布尔";
        } else {
            return "字符串";
        }
    }

    /**
     * 获取样本值
     */
    private String getSampleValue(int columnIndex, int totalRows) {
        for (int rowNum = 1; rowNum <= Math.min(5, totalRows - 1); rowNum++) {
            Row row = currentSheet.getRow(rowNum);
            if (row == null)
                continue;

            Cell cell = row.getCell(columnIndex);
            if (cell != null) {
                String value = getCellValueAsString(cell);
                if (value != null && !value.trim().isEmpty()) {
                    // 如果值太长，截断显示
                    return value.length() > 20 ? value.substring(0, 20) + "..." : value;
                }
            }
        }
        return "[空]";
    }

    /**
     * 获取单元格值
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null)
            return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return getNumber(cell);
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    switch (cell.getCachedFormulaResultType()) {
                        case STRING:
                            return cell.getStringCellValue();
                        case NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                Date date = cell.getDateCellValue();
                                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
                            } else {
                                return String.valueOf(cell.getNumericCellValue());
                            }
                        case BOOLEAN:
                            return String.valueOf(cell.getBooleanCellValue());
                        default:
                            return cell.getCellFormula();
                    }
                } catch (Exception e) {
                    return cell.getCellFormula();
                }
            default:
                return "";
        }
    }
}