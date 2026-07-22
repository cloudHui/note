package com.gamer.data.excel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 独立 GD 文件查看窗口，与 FileMain 的 GD 查看一致：可拖拽、显示文件名/行数/列数、分页表格。
 * 不拆分主面板，每次在新窗口打开。
 *
 * @author liuyunhui
 */
public class GdViewerFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private static final int GD_PAGE_SIZE = 50;

    private File currentFile;
    private GdData currGD;
    private final List<Object[]> gdAllRows = new ArrayList<>();
    private int gdCurrentPage = 1;

    private JLabel gdInfoLabel;
    private JLabel gdPageLabel;
    private JButton gdBtnPrevPage;
    private JButton gdBtnNextPage;
    private JTable gdTable;
    private DefaultTableModel gdTableModel;

    /**
     * 用已读取的 GD 数据打开查看窗口
     *
     * @param file 对应文件（用于标题和显示名）
     * @param gdData 已读取的 GD 数据
     */
    public GdViewerFrame(File file, GdData gdData) {
        this.currentFile = file;
        this.currGD = gdData;
        setTitle("GD文件查看 - " + (file != null ? file.getName() : ""));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout());
        main.add(createToolPanel(), BorderLayout.NORTH);
        main.add(createTablePanel(), BorderLayout.CENTER);
        main.add(createPagePanel(), BorderLayout.SOUTH);
        setContentPane(main);

        setupDragAndDrop();
        if (gdData != null && gdData.dataRows != null) {
            gdAllRows.clear();
            gdAllRows.addAll(gdData.dataRows);
            gdCurrentPage = 1;
            refreshInfoLabel();
            showGdPage();
            updateGdPagingControls();
            setColumnWidths();
        }
    }

    private JPanel createToolPanel() {
        JPanel toolPanel = new JPanel(new BorderLayout());
        toolPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        gdInfoLabel = new JLabel("请打开GD文件或拖放GD文件到窗口");
        gdInfoLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        gdInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gdInfoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnOpenGd = new JButton("打开GD");
        btnOpenGd.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        btnOpenGd.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                selectGdFile();
            }
        });

        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftButtons.add(btnOpenGd);
        toolPanel.add(leftButtons, BorderLayout.WEST);
        toolPanel.add(gdInfoLabel, BorderLayout.CENTER);
        return toolPanel;
    }

    private JPanel createTablePanel() {
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

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(gdScrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPagePanel() {
        JPanel gdPageToolPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
        gdPageToolPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        gdBtnPrevPage = new JButton("◀ 上一页");
        gdBtnPrevPage.setEnabled(false);
        gdBtnPrevPage.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                showGdPrevPage();
            }
        });

        gdPageLabel = new JLabel("第 1 页");
        gdPageLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));

        gdBtnNextPage = new JButton("下一页 ▶");
        gdBtnNextPage.setEnabled(false);
        gdBtnNextPage.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                showGdNextPage();
            }
        });

        gdPageToolPanel.add(gdBtnPrevPage);
        gdPageToolPanel.add(gdPageLabel);
        gdPageToolPanel.add(gdBtnNextPage);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(gdPageToolPanel, BorderLayout.CENTER);
        return southPanel;
    }

    private void refreshInfoLabel() {
        if (currentFile != null && currGD != null) {
            gdInfoLabel.setText(String.format("文件: %s | 行数: %d | 列数: %d",
                currentFile.getName(), currGD.getRowCount(), currGD.getColumnCount()));
        }
    }

    private void setColumnWidths() {
        if (gdTable != null && gdTable.getColumnCount() > 0) {
            for (int i = 0; i < gdTable.getColumnCount(); i++) {
                gdTable.getColumnModel().getColumn(i).setPreferredWidth(120);
            }
        }
    }

    private void showGdPage() {
        int totalPages = getGdTotalPages();
        int startIndex = (gdCurrentPage - 1) * GD_PAGE_SIZE;
        int endIndex = Math.min(startIndex + GD_PAGE_SIZE, gdAllRows.size());

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

    private String[] getGdColumnNames() {
        if (currGD != null && currGD.header != null && currGD.header.columnNames != null) {
            return currGD.header.columnNames.toArray(new String[0]);
        }
        if (!gdAllRows.isEmpty() && gdAllRows.get(0) != null) {
            int columnCount = gdAllRows.get(0).length;
            String[] columnNames = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                columnNames[i] = "列" + (i + 1);
            }
            return columnNames;
        }
        return new String[0];
    }

    private int getGdTotalPages() {
        if (gdAllRows.isEmpty()) {
            return 0;
        }
        return (int)Math.ceil((double)gdAllRows.size() / GD_PAGE_SIZE);
    }

    private void updateGdPagingControls() {
        int totalPages = getGdTotalPages();
        boolean hasPrev = gdCurrentPage > 1;
        boolean hasNext = gdCurrentPage < totalPages;
        gdBtnPrevPage.setEnabled(hasPrev && totalPages > 0);
        gdBtnNextPage.setEnabled(hasNext && totalPages > 0);
        gdPageLabel.setText(String.format("第 %d 页 / 共 %d 页", gdCurrentPage, totalPages));
    }

    private void showGdPrevPage() {
        if (gdCurrentPage > 1) {
            gdCurrentPage--;
            showGdPage();
            updateGdPagingControls();
        }
    }

    private void showGdNextPage() {
        if (gdCurrentPage < getGdTotalPages()) {
            gdCurrentPage++;
            showGdPage();
            updateGdPagingControls();
        }
    }

    /**
     * 在本窗口内加载并显示 GD 文件（供拖放与“打开GD”使用）
     */
    private void loadAndDisplayGdFile(File file) {
        try {
            currGD = GdFileReader.readGdFile(file);
            currentFile = file;
            setTitle("GD文件查看 - " + file.getName());

            gdAllRows.clear();
            gdAllRows.addAll(currGD.dataRows);
            gdCurrentPage = 1;
            refreshInfoLabel();
            showGdPage();
            updateGdPagingControls();
            setColumnWidths();

            JOptionPane.showMessageDialog(this,
                String.format("成功加载GD文件:\n文件: %s\n行数: %d\n列数: %d",
                    file.getName(), currGD.getRowCount(), currGD.getColumnCount()),
                "GD文件加载成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "读取GD文件失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

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
                return name != null && name.toLowerCase().endsWith(".gd");
            }

            @Override
            public String getDescription() {
                return "GD文件 (*.gd)";
            }
        });
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            loadAndDisplayGdFile(file);
        }
    }

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
                            if (name != null && name.toLowerCase().endsWith(".gd")) {
                                loadAndDisplayGdFile(file);
                                dtde.dropComplete(true);
                                return;
                            }
                        }
                    }
                    dtde.dropComplete(true);
                } catch (Exception e) {
                    dtde.dropComplete(false);
                    JOptionPane.showMessageDialog(GdViewerFrame.this, "拖放失败: " + e, "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}
