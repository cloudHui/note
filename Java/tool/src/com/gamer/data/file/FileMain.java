package com.gamer.data.file;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 高级文件管理器主窗口：目录树、文件列表、GD 预览与拖放；cmd 执行路径与目录树路径在 initial-paths.txt 中分段存储，见
 * {@link ExeCmdDialog}、{@link Const#SECTION_CMD_MARKER}。
 */
public class FileMain extends JFrame {
    private static final long serialVersionUID = 1L;

    // 左侧工具面板
    private JPanel leftToolPanel;

    // 中间目录树
    private JTree directoryTree;
    private Thread directoryTreeThread;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;

    // 右侧文件展示区
    private JTabbedPane rightTabbedPane;
    private JTable fileTable;
    private DefaultTableModel tableModel;
    GdData currGD;

    // GD文件查看相关
    private JTable gdTable;
    private DefaultTableModel gdTableModel;
    private JLabel gdInfoLabel;
    private JLabel gdPageLabel;
    private JButton gdBtnPrevPage;
    private JButton gdBtnNextPage;
    private final List<Object[]> gdAllRows = new ArrayList<>();
    private int gdCurrentPage = 1;
    private final int gdPageSize = 50;

    // 数据
    private final Map<String, DefaultMutableTreeNode> pathNodes = new HashMap<>();
    private final List<String> initialPaths = new ArrayList<>();

    // 分页相关
    private List<String> fileLines = new ArrayList<>();
    private int currentPage = 1;
    private final int pageSize = 50;
    private File currentFile;
    private String currentFileType;

    // 拖放相关
    private JPanel dropTargetPanel;
    private JLabel dropLabel;

    // 文件列表分页相关
    private final List<File> allFilesInDirectory = new ArrayList<>();
    private int fileListCurrentPage = 1;
    private final int fileListPageSize = 50;
    private JLabel fileListPageLabel;
    private JButton fileListBtnPrevPage;
    private JButton fileListBtnNextPage;

    // 当前选中的目录
    private File currentDirectory;

    /** 导航起始点：点击左侧 INITIAL_PATHS 中某一项时设置，返回上级时不能超过此路径 */
    private String navigationRootPath;

    /** 左侧「返回上级」按钮，用于根据是否可返回更新启用状态 */
    private JButton btnGoUp;

    // 处理目录树双击事件 - 打开文件位置
    private void handleTreeDoubleClick(MouseEvent e) {
        TreePath path = directoryTree.getPathForLocation(e.getX(), e.getY());
        if (path == null) {
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
        Object userObject = node.getUserObject();
        if (userObject instanceof File) {
            openFileLocation((File)userObject);
        }
    }

    /**
     * 处理目录树单击事件 - 加载目录内容或打开文件
     */
    private void handleTreeSingleClick(MouseEvent e) {
        TreePath path = directoryTree.getPathForLocation(e.getX(), e.getY());
        if (path == null) {
            return;
        }
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
        Object userObject = node.getUserObject();
        if (userObject instanceof File) {
            File file = (File)userObject;
            if (file.isDirectory()) {
                // 点击左侧 INITIAL_PATHS 中的一项时，将其设为右侧「返回上级」的起始点
                if (Const.INITIAL_PATHS.contains(file.getAbsolutePath())) {
                    navigationRootPath = file.getAbsolutePath();
                }
                loadDirectoryContents(file);
            } else {
                currentDirectory = file;
                choiceOpenFile(file);
            }
        }
    }

    /**
     * 切换到文件列表标签页
     */
    private void switchToFileListTab() {
        for (int i = 0; i < rightTabbedPane.getTabCount(); i++) {
            if ("文件列表".equals(rightTabbedPane.getTitleAt(i))) {
                rightTabbedPane.setSelectedIndex(i);
                break;
            }
        }
    }

    private final MouseAdapter mouseAdapter = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            // 判断两次点击是否是同一行
            TreePath path = directoryTree.getPathForLocation(e.getX(), e.getY());
            TreePath currentSelection = directoryTree.getSelectionPath();

            if (currentSelection == null) {
                return;
            }
            switchToFileListTab();
            boolean isSameRow = currentSelection.equals(path);
            if (directoryTreeThread != null && isSameRow) {
                // 如果已有计时器，点击时立即执行另一个函数
                directoryTreeThread.interrupt();
                directoryTreeThread = null;
                handleTreeDoubleClick(e);
                System.out.println("再次点击");
                return;
            }
            directoryTreeThread = new Thread(() -> {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e1) {
                    directoryTreeThread = null;
                    return;
                }
                if (directoryTreeThread == null) {
                    return;
                }
                handleTreeSingleClick(e);
                System.out.println("执行完成");
                directoryTreeThread = null;
            });
            directoryTreeThread.start();
        }
    };

    /**
     * 文件列表点击事件（使用MouseAdapter替代ListSelectionListener以支持重复点击） 注意：需要使用 fileTable.addMouseListener() 而不是
     * fileTable.getSelectionModel().addListSelectionListener()
     */
    private final MouseAdapter fileTableListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            int row = fileTable.rowAtPoint(e.getPoint());
            if (row >= 0) {
                fileTable.setRowSelectionInterval(row, row);
                int startIndex = (fileListCurrentPage - 1) * fileListPageSize;
                int actualIndex = startIndex + row;

                if (actualIndex >= 0 && actualIndex < allFilesInDirectory.size()) {
                    File file = allFilesInDirectory.get(actualIndex);
                    currentDirectory = file;
                    handleFileClick(file);
                }
            }
        }
    };

    /**
     * 文件列表选择事件（保留用于键盘导航等场景） 构造函数
     */
    public FileMain() {
        initialPaths.addAll(Const.INITIAL_PATHS);
        initUI();
        loadInitialPaths();
        setupDragAndDrop();
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        setTitle("高级文件管理器 - GD文件拖放支持");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 获取屏幕尺寸
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)(screenSize.width * 0.6);
        int height = (int)(screenSize.height * 0.6);

        setSize(width, height);
        setLocationRelativeTo(null);

        // 创建主容器
        Container mainContainer = getContentPane();
        mainContainer.setLayout(new BorderLayout(5, 5));

        // 创建左侧工具面板
        createLeftToolPanel();
        mainContainer.add(leftToolPanel, BorderLayout.WEST);

        // 创建中间目录树面板
        JPanel centerPanel = createCenterPanel();

        // 创建右侧文件展示面板
        createRightPanel();

        // 使用JSplitPane将中间和右侧面板分开
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, centerPanel, rightTabbedPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(width / 5);
        splitPane.setResizeWeight(0.3);

        mainContainer.add(splitPane, BorderLayout.CENTER);
    }

    /**
     * 创建左侧工具面板
     */
    private void createLeftToolPanel() {
        leftToolPanel = new JPanel();
        leftToolPanel.setLayout(new BoxLayout(leftToolPanel, BoxLayout.Y_AXIS));
        leftToolPanel.setBackground(new Color(240, 240, 240));
        leftToolPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        leftToolPanel.setPreferredSize(new Dimension(120, getHeight()));

        Font buttonFont = new Font("微软雅黑", Font.PLAIN, 12);
        // 添加 按钮
        populateLeftToolButtons(buttonFont);
        updateGoUpButtonState();
        appendLeftToolVersionLabel();
    }

    /**
     * 将主要操作按钮加入左侧面板。
     */
    private void populateLeftToolButtons(Font buttonFont) {
        JButton btnAddDirectory = createToolButton("添加目录", "📁", buttonFont);
        btnAddDirectory.addActionListener(e -> addDirectory());
        addButton(btnAddDirectory);

        JButton btnDirectorySvn = createToolButton("目录执行CMD", "🛠", buttonFont);
        btnDirectorySvn.addActionListener(e -> openDirectoryCmdDialog());
        addButton(btnDirectorySvn);
        JButton delCurrDirectory = createToolButton("删除当前目录", "📁", buttonFont);
        delCurrDirectory.addActionListener(e -> delDirectory());
        addButton(delCurrDirectory);
        JButton btnRefresh = createToolButton("刷新目录", "🔄", buttonFont);
        btnRefresh.addActionListener(e -> refreshTree());
        addButton(btnRefresh);
        btnGoUp = createToolButton("返回上级", "⬆", buttonFont);
        btnGoUp.addActionListener(e -> goToParentDirectory());
        addButton(btnGoUp);
    }

    /**
     * 添加按钮
     * 
     * @param button
     *            按钮
     */
    private void addButton(JButton button) {
        leftToolPanel.add(Box.createVerticalStrut(10));
        leftToolPanel.add(button);
    }

    /**
     * 左下角版本说明标签。
     */
    private void appendLeftToolVersionLabel() {
        JLabel versionLabel = new JLabel("v3.1 - 支持GD拖放");
        versionLabel.setFont(new Font("微软雅黑", Font.PLAIN, 10));
        versionLabel.setForeground(Color.GRAY);
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftToolPanel.add(Box.createVerticalGlue());
        leftToolPanel.add(versionLabel);
    }

    /**
     * 返回上级目录。未设置起始点时不能返回；设置了起始点后，当前目录为起始点时不再响应。
     */
    private void goToParentDirectory() {
        if (currentDirectory == null || currentDirectory.getParentFile() == null) {
            return;
        }
        // 已到达左侧设置的起始点，不再返回上级
        if (navigationRootPath != null && isSamePath(currentDirectory.getAbsolutePath(), navigationRootPath)) {
            return;
        }
        File parentDir = currentDirectory.getParentFile();
        loadDirectoryContents(parentDir);
        // 若父目录在树中已有节点，则选中并展开该节点（使用树中已有节点，否则 setSelectionPath 不生效）
        DefaultMutableTreeNode parentNode = pathNodes.get(parentDir.getAbsolutePath());
        if (parentNode != null) {
            TreePath path = new TreePath(parentNode.getPath());
            directoryTree.setSelectionPath(path);
            directoryTree.expandPath(path);
            directoryTree.scrollPathToVisible(path);
        }
    }

    /**
     * 判断两个路径是否表示同一目录（规范化后比较）
     */
    private static boolean isSamePath(String path1, String path2) {
        if (path1 == null || path2 == null) {
            return Objects.equals(path1, path2);
        }
        return new File(path1).getAbsolutePath().equals(new File(path2).getAbsolutePath());
    }

    /**
     * 根据当前目录和起始点更新「返回上级」按钮的可用状态
     */
    private void updateGoUpButtonState() {
        if (btnGoUp == null) {
            return;
        }
        boolean canGoUp = currentDirectory != null && currentDirectory.getParentFile() != null
            && (navigationRootPath == null || !isSamePath(currentDirectory.getAbsolutePath(), navigationRootPath));
        btnGoUp.setEnabled(canGoUp);
    }

    /**
     * 创建工具按钮
     *
     * @param text
     *            按钮文本
     * @param icon
     *            按钮图标
     * @param font
     *            按钮字体
     * @return 工具按钮
     */
    private JButton createToolButton(String text, String icon, Font font) {
        JButton button = new JButton(icon + " " + text);
        button.setFont(font);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(100, 35));
        button.setMinimumSize(new Dimension(100, 35));
        button.setPreferredSize(new Dimension(100, 35));
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        button.setFocusPainted(false);

        // 添加鼠标悬停效果
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(225, 245, 255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.WHITE);
            }
        });

        return button;
    }

    /**
     * 创建中心面板
     *
     * @return 中心面板
     */
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), "目录树"));

        // 初始化树
        rootNode = new DefaultMutableTreeNode("我的目录");
        treeModel = new DefaultTreeModel(rootNode);
        directoryTree = new JTree(treeModel);
        directoryTree.setFont(new Font("微软雅黑", Font.PLAIN, 11));

        // 设置树渲染器
        directoryTree.setCellRenderer(new FileTreeCellRenderer());

        // 添加树点击事件（单击跳转目录，双击加载内容或打开文件）
        directoryTree.addMouseListener(mouseAdapter);

        // 创建拖放区域面板
        createDropTargetPanel();

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(dropTargetPanel, BorderLayout.EAST);

        centerPanel.add(northPanel, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(directoryTree), BorderLayout.CENTER);

        return centerPanel;
    }

    /**
     * 创建拖放区域面板
     */
    private void createDropTargetPanel() {
        dropTargetPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        dropTargetPanel.setOpaque(false);

        dropLabel = new JLabel("拖放GD文件");
        dropLabel.setFont(new Font("微软雅黑", Font.PLAIN, 10));
        dropLabel.setForeground(Color.BLUE);
        dropLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        dropLabel.setToolTipText("将GD文件拖放到此处快速查看");

        dropLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectGdFile();
            }
        });

        dropTargetPanel.add(dropLabel);
    }

    /**
     * 设置拖放区域
     */
    private void setupDragAndDrop() {
        new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                handleMainWindowDrop(dtde);// 处理主窗口拖放：GD 打开预览，其它文件询问是否加入目录树。
            }
        });
    }

    /**
     * 处理主窗口拖放：GD 打开预览，其它文件询问是否加入目录树。
     */
    private void handleMainWindowDrop(DropTargetDropEvent dtde) {
        try {
            dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);// 接受拖放操作
            Transferable transferable = dtde.getTransferable();

            if (!transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {// 判断是否支持文件列表数据格式
                dtde.dropComplete(true);
                return;
            }

            List<File> files = (List<File>)transferable.getTransferData(DataFlavor.javaFileListFlavor);// 获取文件列表
            if (files.isEmpty()) {
                dtde.dropComplete(true);// 完成拖放操作
                return;
            }

            File file = files.get(0);// 获取第一个文件
            String extension = Utils.getFileExtension(file.getName());

            if (Const.GD.equalsIgnoreCase(extension)) {
                runGdDropWithUiFeedback(file);// 运行GD文件拖放：更新提示标签并在后台短暂延迟后打开文件。
                dtde.dropComplete(true);
                return;
            }

            promptAddNonGdFileToTree(file);// 非 GD 文件：询问用户是否把其目录加入左侧树。
            dtde.dropComplete(true);
        } catch (Exception e) {
            dtde.dropComplete(false);// 完成拖放操作
            JOptionPane.showMessageDialog(this, "拖放失败: " + e, "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 拖放 GD：更新提示标签并在后台短暂延迟后打开文件。
     */
    private void runGdDropWithUiFeedback(final File file) {
        SwingUtilities.invokeLater(() -> {
            dropLabel.setText("正在加载...");// 更新提示标签       设置提示标签前景色为橙色
            dropLabel.setForeground(Color.ORANGE);// 设置提示标签前景色为橙色
        });

        SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(100);// 短暂延迟后打开文件
                openGdFile(file);// 打开文件
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(FileMain.this,
                    "打开GD文件失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE));// 显示错误信息
            } finally {
                SwingUtilities.invokeLater(() -> {
                    dropLabel.setText("拖放GD文件");// 更新提示标签 设置提示标签前景色为蓝色
                    dropLabel.setForeground(Color.BLUE);// 设置提示标签前景色为蓝色
                });
            }
        });
    }

    /**
     * 非 GD 文件：询问用户是否把其目录加入左侧树。
     */
    private void promptAddNonGdFileToTree(File file) {// 非 GD 文件：询问用户是否把其目录加入左侧树。
        int choice = JOptionPane.showConfirmDialog(this, "文件: " + file.getName() + "\n不是GD文件。是否添加到目录树？", "添加文件",
            JOptionPane.YES_NO_OPTION);
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        if (file.isDirectory()) {
            addDirectoryToTree(file);// 添加目录到树
        } else {
            addDirectoryToTree(file.getParentFile());// 添加目录到树
        }
    }

    /**
     * 打开GD文件
     *
     * @param file
     *            GD文件
     */
    private void openGdFile(File file) {
        try {
            currGD = GdFileReader.readGdFile(file);// 读取GD文件

            SwingUtilities.invokeLater(() -> {
                displayGdFileData(file);// 显示GD文件数据  切换到GD文件查看选项卡
                rightTabbedPane.setSelectedIndex(1);// 切换到GD文件查看选项卡
            });

        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "读取GD文件失败: " + e.getMessage(), "错误",
                JOptionPane.ERROR_MESSAGE));// 显示错误信息
        }
    }

    /**
     * 选择GD文件
     */
    private void selectGdFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("选择GD文件");
        chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;
                return Const.GD.equalsIgnoreCase(Utils.getFileExtension(f.getName()));
            }

            @Override
            public String getDescription() {
                return "GD文件 (*.gd)";
            }
        });

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            openGdFile(file);// 打开文件
        }
    }

    /**
     * 创建右侧面板
     */
    private void createRightPanel() {
        rightTabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);// 创建右侧面板
        rightTabbedPane.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        rightTabbedPane.setPreferredSize(new Dimension(800, 600));// 设置右侧面板大小

        // 创建文件列表选项卡
        JPanel fileListPanel = createFileListPanel();
        rightTabbedPane.addTab("文件列表", fileListPanel);

        // 创建GD文件查看选项卡
        JPanel gdViewerPanel = createGdViewerPanel();
        rightTabbedPane.addTab("GD文件查看", gdViewerPanel);
    }

    /**
     * 创建GD文件查看面板
     *
     * @return GD文件查看面板
     */
    private JPanel createGdViewerPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 创建GD文件信息标签
        gdInfoLabel = new JLabel("请打开GD文件或拖放GD文件到窗口");
        gdInfoLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        gdInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gdInfoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 创建GD数据表格
        gdTableModel = new DefaultTableModel();
        gdTable = new JTable(gdTableModel);
        gdTable.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        gdTable.setRowHeight(20);
        gdTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 11));
        gdTable.getTableHeader().setBackground(new Color(240, 240, 240));
        gdTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane gdScrollPane = new JScrollPane(gdTable);
        gdScrollPane.setBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), "GD文件数据"));

        // 创建分页工具栏
        JPanel gdPageToolPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
        gdPageToolPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        gdBtnPrevPage = new JButton("◀ 上一页");
        gdBtnPrevPage.setEnabled(false);
        gdBtnPrevPage.addActionListener(e -> showGdPrevPage());

        gdPageLabel = new JLabel("第 1 页");
        gdPageLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));

        gdBtnNextPage = new JButton("下一页 ▶");
        gdBtnNextPage.setEnabled(false);
        gdBtnNextPage.addActionListener(e -> showGdNextPage());

        gdPageToolPanel.add(gdBtnPrevPage);
        gdPageToolPanel.add(gdPageLabel);
        gdPageToolPanel.add(gdBtnNextPage);

        // 创建工具栏
        JPanel toolPanel = new JPanel(new BorderLayout());
        toolPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton btnOpenGd = new JButton("打开GD");
        btnOpenGd.addActionListener(e -> selectGdFile());
        btnOpenGd.setFont(new Font("微软雅黑", Font.PLAIN, 11));

        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftButtons.add(btnOpenGd);

        toolPanel.add(leftButtons, BorderLayout.WEST);
        toolPanel.add(gdInfoLabel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(gdPageToolPanel, BorderLayout.CENTER);

        panel.add(toolPanel, BorderLayout.NORTH);
        panel.add(gdScrollPane, BorderLayout.CENTER);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * 显示GD文件数据
     *
     * @param file
     *            GD文件
     */
    private void displayGdFileData(File file) {
        gdAllRows.clear();
        gdAllRows.addAll(currGD.dataRows);
        gdCurrentPage = 1;

        gdInfoLabel.setText(
            String.format("文件: %s | 行数: %d | 列数: %d", file.getName(), currGD.dataRows.size(), currGD.header.columns));

        showGdPage();
        updateGdPagingControls();

        for (int i = 0; i < gdTable.getColumnCount(); i++) {
            gdTable.getColumnModel().getColumn(i).setPreferredWidth(120);
        }

        JOptionPane.showMessageDialog(this, String.format("成功加载GD文件:\n文件: %s\n行数: %d\n列数: %d", file.getName(),
            currGD.dataRows.size(), currGD.header.columns), "GD文件加载成功", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * 显示GD文件页码
     */
    private void showGdPage() {
        int totalPages = getGdTotalPages();
        int startIndex = (gdCurrentPage - 1) * gdPageSize;
        int endIndex = Math.min(startIndex + gdPageSize, gdAllRows.size());

        List<Object[]> pageData = new ArrayList<>();
        for (int i = startIndex; i < endIndex; i++) {
            pageData.add(gdAllRows.get(i));
        }

        gdTableModel = new DefaultTableModel(new Object[0][], getGdColumnNames()) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Object[] row : pageData) {
            gdTableModel.addRow(row);
        }

        gdTable.setModel(gdTableModel);
        gdPageLabel.setText(String.format("第 %d 页 / 共 %d 页", gdCurrentPage, totalPages));
    }

    /**
     * 获取GD文件列名
     *
     * @return GD文件列名
     */
    private String[] getGdColumnNames() {
        if (currGD != null) {
            return currGD.header.columnNames.toArray(new String[0]);
        }
        if (gdAllRows.isEmpty()) {
            return new String[0];
        }
        int columnCount = gdAllRows.get(0).length;
        String[] columnNames = new String[columnCount];
        for (int i = 0; i < columnCount; i++) {
            columnNames[i] = "列" + (i + 1);
        }
        return columnNames;
    }

    /**
     * 获取GD文件总页数
     *
     * @return GD文件总页数
     */
    private int getGdTotalPages() {
        return (int)Math.ceil((double)gdAllRows.size() / gdPageSize);
    }

    /**
     * 更新GD文件分页控制
     */
    private void updateGdPagingControls() {
        int totalPages = getGdTotalPages();
        boolean hasPrev = gdCurrentPage > 1;
        boolean hasNext = gdCurrentPage < totalPages;

        gdBtnPrevPage.setEnabled(hasPrev && totalPages > 0);
        gdBtnNextPage.setEnabled(hasNext && totalPages > 0);
        gdPageLabel.setText(String.format("第 %d 页 / 共 %d 页", gdCurrentPage, totalPages));
    }

    /**
     * 显示上一页
     */
    private void showGdPrevPage() {
        if (gdCurrentPage > 1) {
            gdCurrentPage--;
            showGdPage();
            updateGdPagingControls();
        }
    }

    /**
     * 显示下一页
     */
    private void showGdNextPage() {
        if (gdCurrentPage < getGdTotalPages()) {
            gdCurrentPage++;
            showGdPage();
            updateGdPagingControls();
        }
    }

    /**
     * 创建文件列表面板
     *
     * @return 文件列表面板
     */
    private JPanel createFileListPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 创建文件表格
        String[] columns = {"文件名", "类型", "大小", "修改时间"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };

        fileTable = new JTable(tableModel);
        fileTable.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        fileTable.setRowHeight(22);
        fileTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 12));
        fileTable.getTableHeader().setBackground(new Color(240, 240, 240));
        fileTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // 设置列宽
        fileTable.getColumnModel().getColumn(0).setPreferredWidth(250);
        fileTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        fileTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        fileTable.getColumnModel().getColumn(3).setPreferredWidth(150);

        // 添加表格选择监听器
        fileTable.addMouseListener(fileTableListener);

        // 创建分页工具栏
        JPanel pageToolPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
        pageToolPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        fileListBtnPrevPage = new JButton("◀ 上一页");
        fileListBtnPrevPage.setEnabled(false);
        fileListBtnPrevPage.addActionListener(e -> showFileListPrevPage());

        fileListPageLabel = new JLabel("第 1 页");
        fileListPageLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));

        fileListBtnNextPage = new JButton("下一页 ▶");
        fileListBtnNextPage.setEnabled(false);
        fileListBtnNextPage.addActionListener(e -> showFileListNextPage());

        pageToolPanel.add(fileListBtnPrevPage);
        pageToolPanel.add(fileListPageLabel);
        pageToolPanel.add(fileListBtnNextPage);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(pageToolPanel, BorderLayout.CENTER);

        panel.add(new JScrollPane(fileTable), BorderLayout.CENTER);
        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * 加载初始路径
     */
    private void loadInitialPaths() {
        for (String pathStr : initialPaths) {
            File path = new File(pathStr);
            if (path.exists() && path.isDirectory()) {
                addDirectoryToTree(path);
            }
        }
    }

    /**
     * 添加目录到树
     *
     * @param directory
     *            目录
     */
    void addDirectoryToTree(File directory) {
        if (pathNodes.containsKey(directory.getAbsolutePath())) {
            return;
        }
        String absPath = directory.getAbsolutePath();
        if (!Const.INITIAL_PATHS.contains(absPath)) {
            Const.INITIAL_PATHS.add(absPath);
            Collections.sort(Const.INITIAL_PATHS);
            Const.saveInitialPaths();
        }

        DefaultMutableTreeNode dirNode = new DefaultMutableTreeNode(directory);
        rootNode.add(dirNode);
        pathNodes.put(directory.getAbsolutePath(), dirNode);
        treeModel.reload();

        directoryTree.expandPath(new TreePath(rootNode.getPath()));
    }

    /**
     * 添加目录到树
     *
     */
    private void addDirectory() {
        File file = new File(System.getProperty("user.dir"));
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setCurrentDirectory(file);
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File dir = chooser.getSelectedFile();
            addDirectoryToTree(dir);
        }
    }

    /**
     * 打开目录 cmd 执行窗口（逻辑在 {@link ExeCmdDialog}）
     */
    private void openDirectoryCmdDialog() {
        ExeCmdDialog.show(this);
    }

    /**
     * 删除目录到树
     */
    private void delDirectory() {
        if (currentDirectory == null) {
            return;
        }
        String path = currentDirectory.getAbsolutePath();
        DefaultMutableTreeNode node = pathNodes.get(path);
        if (node != null && node != rootNode) {
            node.removeFromParent();
            pathNodes.remove(path);
            Const.INITIAL_PATHS.remove(path);
            Const.saveInitialPaths();
            treeModel.reload();
        }
    }

    /**
     * 刷新树
     */
    private void refreshTree() {
        Enumeration<?> e = rootNode.depthFirstEnumeration();
        List<DefaultMutableTreeNode> nodesToRemove = new ArrayList<>();

        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.nextElement();
            if (node != rootNode) {
                Object userObject = node.getUserObject();
                if (userObject instanceof File) {
                    File file = (File)userObject;
                    if (!file.exists()) {
                        nodesToRemove.add(node);
                        pathNodes.remove(file.getAbsolutePath());
                    }
                }
            }
        }

        for (DefaultMutableTreeNode node : nodesToRemove) {
            node.removeFromParent();
        }

        treeModel.reload();
        Utils.expandAllTreeNodes(directoryTree);
    }

    /**
     * 加载目录内容
     *
     * @param directory
     *            目录
     */
    private void loadDirectoryContents(File directory) {
        closeFileContentViewer();

        allFilesInDirectory.clear();
        fileListCurrentPage = 1;

        if (directory == null || !directory.exists() || !directory.isDirectory()) {
            tableModel.setRowCount(0);
            updateFileListPagingControls();
            return;
        }

        currentDirectory = directory;

        updateGoUpButtonState();

        File[] files = directory.listFiles();
        if (files != null) {
            List<File> dirs = new ArrayList<>();
            List<File> fileList = new ArrayList<>();

            for (File file : files) {
                if (file.isDirectory()) {
                    dirs.add(file);
                } else {
                    fileList.add(file);
                }
            }

            dirs.sort(Comparator.comparing(File::getName));
            fileList.sort(Comparator.comparing(File::getName));

            allFilesInDirectory.addAll(dirs);
            allFilesInDirectory.addAll(fileList);

            showFileListPage();
        }

        updateFileListPagingControls();
    }

    /**
     * 显示文件列表页
     */
    private void showFileListPage() {
        tableModel.setRowCount(0);

        int startIndex = (fileListCurrentPage - 1) * fileListPageSize;
        int endIndex = Math.min(startIndex + fileListPageSize, allFilesInDirectory.size());

        for (int i = startIndex; i < endIndex; i++) {
            File file = allFilesInDirectory.get(i);
            addFileToTable(file);
        }

        fileListPageLabel.setText(String.format("第 %d 页 / 共 %d 页", fileListCurrentPage,
            Utils.getFileListTotalPages(allFilesInDirectory, fileListPageSize)));
    }

    /**
     * 显示上一页
     */
    private void showFileListPrevPage() {
        if (fileListCurrentPage > 1) {
            fileListCurrentPage--;
            showFileListPage();
            updateFileListPagingControls();
        }
    }

    /**
     * 显示下一页
     */
    private void showFileListNextPage() {
        if (fileListCurrentPage < Utils.getFileListTotalPages(allFilesInDirectory, fileListPageSize)) {
            fileListCurrentPage++;
            showFileListPage();
            updateFileListPagingControls();
        }
    }

    /**
     * 更新文件列表分页控制
     */
    private void updateFileListPagingControls() {
        int totalPages = Utils.getFileListTotalPages(allFilesInDirectory, fileListPageSize);
        boolean hasPrev = fileListCurrentPage > 1;
        boolean hasNext = fileListCurrentPage < totalPages;

        fileListBtnPrevPage.setEnabled(hasPrev && totalPages > 0);
        fileListBtnNextPage.setEnabled(hasNext && totalPages > 0);
        fileListPageLabel.setText(String.format("第 %d 页 / 共 %d 页", fileListCurrentPage, totalPages));
    }

    /**
     * 添加文件到表格
     *
     * @param file
     *            文件
     */
    private void addFileToTable(File file) {
        String name = file.getName();
        String type = file.isDirectory() ? "文件夹" : Utils.getFileExtension(name).toUpperCase();
        String size = file.isDirectory() ? "" : Utils.formatFileSize(file.length());
        String modified = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(file.lastModified()));

        tableModel.addRow(new Object[] {name, type, size, modified});
    }

    /**
     * 处理文件点击
     *
     * @param file
     *            点击的文件
     */
    private void handleFileClick(File file) {
        if (file.isDirectory()) {
            loadDirectoryContents(file);
        } else {
            // 弹出选择对话框
            String[] options = {"打开", "跳转"};
            int choice = JOptionPane.showOptionDialog(this, "请选择操作:\n" + file.getName(), "文件操作",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            switch (choice) {
                case 0:// 选择"打开"
                    choiceOpenFile(file);
                    break;
                case 1: // 选择"跳转"
                    // 跳转到文件所在目录
                    openSelectedFileLocation();
                    break;
                default:// 如果用户关闭对话框或选择取消，不执行任何操作
                    break;

            }
        }
    }

    /**
     * 选择打开文件
     *
     * @param file
     *            文件
     */
    private void choiceOpenFile(File file) {
        String extension = Utils.getFileExtension(file.getName());
        switch (extension) {
            case Const.GD:
                openGdFile(file);
                break;
            case Const.TXT:
            case Const.PROTO:
            case Const.BAT:
                createFileContentViewer(file);
                break;
            default:
                // 其他文件类型用本地程序打开
                try {
                    Desktop.getDesktop().open(file);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "无法打开文件: " + ex, "错误", JOptionPane.ERROR_MESSAGE);
                }
        }
    }

    /**
     * 打开选中的文件所在目录
     */
    private void openSelectedFileLocation() {
        int row = fileTable.getSelectedRow();
        if (row >= 0) {
            int startIndex = (fileListCurrentPage - 1) * fileListPageSize;
            int actualIndex = startIndex + row;

            if (actualIndex >= 0 && actualIndex < allFilesInDirectory.size()) {
                File file = allFilesInDirectory.get(actualIndex);
                openFileLocation(file);
            }
        }
    }

    /**
     * 创建文件内容查看器
     *
     * @param file
     *            文件
     */
    private void createFileContentViewer(File file) {
        try {
            loadFileContentViewerState(file);
            JTextArea textArea = createFileContentTextArea();
            JScrollPane scroll = wrapFileContentScrollPane(textArea);
            FileContentPageBar pageBar = buildFileContentPageBar(textArea);

            JPanel fileContentPanel = new JPanel(new BorderLayout());
            fileContentPanel.add(scroll, BorderLayout.CENTER);
            fileContentPanel.add(pageBar.panel, BorderLayout.SOUTH);

            updateFileContent(textArea, pageBar.pageLabel, pageBar.btnPrevPage, pageBar.btnNextPage);
            installOrReplaceFileContentTab(fileContentPanel);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "无法读取文件: " + e.getMessage());
        }
    }

    /**
     * 载入当前查看文件状态（路径、扩展名、全文行、页码复位）。
     */
    private void loadFileContentViewerState(File file) throws IOException {
        currentFile = file;
        currentFileType = Utils.getFileExtension(file.getName());
        fileLines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        currentPage = 1;
    }

    /**
     * 创建只读文本区并设置字体与制表符。
     */
    private static JTextArea createFileContentTextArea() {
        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("等线", Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setLineWrap(false);
        textArea.setTabSize(4);
        return textArea;
    }

    /**
     * 将文本区包在带标题的滚动面板中。
     */
    private static JScrollPane wrapFileContentScrollPane(JTextArea textArea) {
        JScrollPane textScrollPane = new JScrollPane(textArea);
        textScrollPane.setBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), "文件内容"));
        return textScrollPane;
    }

    /**
     * 构建分页工具栏：上一页、页码标签、下一页、跳转。
     */
    private FileContentPageBar buildFileContentPageBar(final JTextArea textArea) {
        JPanel pageToolPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
        pageToolPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        final JButton btnPrevPage = new JButton("◀ 上一页");
        btnPrevPage.setEnabled(false);

        final JLabel pageLabel = new JLabel("第 1 页");
        pageLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));

        final JButton btnNextPage = new JButton("下一页 ▶");
        btnNextPage.setEnabled(false);

        JButton btnJumpToPage = new JButton("跳转");
        btnJumpToPage.setFont(new Font("微软雅黑", Font.PLAIN, 11));

        pageToolPanel.add(btnPrevPage);
        pageToolPanel.add(pageLabel);
        pageToolPanel.add(btnNextPage);
        pageToolPanel.add(btnJumpToPage);

        wireFileContentPrevButton(btnPrevPage, textArea, pageLabel, btnNextPage);
        wireFileContentNextButton(btnNextPage, textArea, pageLabel, btnPrevPage);
        wireFileContentJumpButton(btnJumpToPage, textArea, pageLabel, btnPrevPage, btnNextPage);

        return new FileContentPageBar(pageToolPanel, btnPrevPage, pageLabel, btnNextPage);
    }

    /**
     * 文件内容分页条控件引用（避免依赖 FlowLayout 子控件顺序）。
     */
    private static final class FileContentPageBar {
        /** 分页条面板 */
        private final JPanel panel;
        /** 上一页 */
        private final JButton btnPrevPage;
        /** 页码说明 */
        private final JLabel pageLabel;
        /** 下一页 */
        private final JButton btnNextPage;

        private FileContentPageBar(JPanel panel, JButton btnPrevPage, JLabel pageLabel, JButton btnNextPage) {
            this.panel = panel;
            this.btnPrevPage = btnPrevPage;
            this.pageLabel = pageLabel;
            this.btnNextPage = btnNextPage;
        }
    }

    /**
     * 「上一页」：减少当前页并刷新展示。
     */
    private void wireFileContentPrevButton(final JButton btnPrevPage, final JTextArea textArea, final JLabel pageLabel,
        final JButton btnNextPage) {
        btnPrevPage.addActionListener(e -> {
            if (currentPage > 1) {
                currentPage--;
                updateFileContent(textArea, pageLabel, btnPrevPage, btnNextPage);
            }
        });
    }

    /**
     * 「下一页」：增加当前页并刷新展示。
     */
    private void wireFileContentNextButton(final JButton btnNextPage, final JTextArea textArea, final JLabel pageLabel,
        final JButton btnPrevPage) {
        btnNextPage.addActionListener(e -> {
            if (currentPage < getTotalPages()) {
                currentPage++;
                updateFileContent(textArea, pageLabel, btnPrevPage, btnNextPage);
            }
        });
    }

    /**
     * 「跳转」：输入页码并校验范围。
     */
    private void wireFileContentJumpButton(JButton btnJumpToPage, final JTextArea textArea, final JLabel pageLabel,
        final JButton btnPrevPage, final JButton btnNextPage) {
        btnJumpToPage.addActionListener(e -> {
            String input = JOptionPane.showInputDialog(FileMain.this, "请输入页码 (1-" + getTotalPages() + "):", "跳转到页面",
                JOptionPane.PLAIN_MESSAGE);
            if (input == null || input.trim().isEmpty()) {
                return;
            }
            try {
                int page = Integer.parseInt(input.trim());
                if (page >= 1 && page <= getTotalPages()) {
                    currentPage = page;
                    updateFileContent(textArea, pageLabel, btnPrevPage, btnNextPage);
                } else {
                    JOptionPane.showMessageDialog(FileMain.this, "页码必须在 1 到 " + getTotalPages() + " 之间");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(FileMain.this, "请输入有效的数字");
            }
        });
    }

    /**
     * 若已存在「文件内容」标签页则替换组件，否则新建标签页并选中。
     */
    private void installOrReplaceFileContentTab(JPanel fileContentPanel) {
        int tabCount = rightTabbedPane.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            if ("文件内容".equals(rightTabbedPane.getTitleAt(i))) {
                rightTabbedPane.setComponentAt(i, fileContentPanel);
                rightTabbedPane.setSelectedIndex(i);
                return;
            }
        }
        rightTabbedPane.addTab("文件内容", fileContentPanel);
        rightTabbedPane.setSelectedIndex(rightTabbedPane.indexOfComponent(fileContentPanel));
    }

    /**
     * 更新文件内容
     *
     * @param textArea
     *            文本区域
     * @param pageLabel
     *            页码标签
     * @param btnPrevPage
     *            上一页按钮
     * @param btnNextPage
     *            下一页按钮
     */
    private void updateFileContent(JTextArea textArea, JLabel pageLabel, JButton btnPrevPage, JButton btnNextPage) {
        StringBuilder content = new StringBuilder();
        content.append("文件: ").append(currentFile.getAbsolutePath()).append("\n");
        content.append("类型: ").append(currentFileType.toUpperCase()).append("\n");
        content.append("大小: ").append(Utils.formatFileSize(currentFile.length())).append("\n");
        content.append("修改时间: ")
            .append(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(currentFile.lastModified())))
            .append("\n");
        content.append("行数: ").append(fileLines.size()).append("\n");
        content.append("当前页: ").append(currentPage).append("/").append(getTotalPages()).append("\n");
        content.append("\n");
        content.append("=== 文件内容 (第 ").append(currentPage).append(" 页) ===\n\n");

        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, fileLines.size());

        for (int i = startIndex; i < endIndex; i++) {
            content.append(String.format("%6d: ", i + 1)).append(fileLines.get(i)).append("\n");
        }

        textArea.setText(content.toString());
        textArea.setCaretPosition(0);

        // 更新分页控件
        int totalPages = getTotalPages();
        btnPrevPage.setEnabled(currentPage > 1);
        btnNextPage.setEnabled(currentPage < totalPages);
        pageLabel.setText("第 " + currentPage + " 页 / 共 " + totalPages + " 页");
    }

    /**
     * 获取文件总页数
     *
     * @return 文件总页数
     */
    private int getTotalPages() {
        return (int)Math.ceil((double)fileLines.size() / pageSize);
    }

    /**
     * 关闭文件内容查看器
     */
    private void closeFileContentViewer() {
        for (int i = 0; i < rightTabbedPane.getTabCount(); i++) {
            if ("文件内容".equals(rightTabbedPane.getTitleAt(i))) {
                rightTabbedPane.removeTabAt(i);
                break;
            }
        }
    }

    /**
     * 打开文件所在目录
     *
     * @param file
     *            文件
     */
    private void openFileLocation(File file) {
        try {
            File location = file.isDirectory() ? file : file.getParentFile();
            if (location != null && location.exists()) {
                Desktop.getDesktop().open(location);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "无法打开目录: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                Font font = new Font("微软雅黑", Font.PLAIN, 11);
                Enumeration<Object> keys = UIManager.getDefaults().keys();
                while (keys.hasMoreElements()) {
                    Object key = keys.nextElement();
                    Object value = UIManager.get(key);
                    if (value instanceof Font) {
                        UIManager.put(key, font);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            FileMain app = new FileMain();
            app.setVisible(true);
        });
    }
}