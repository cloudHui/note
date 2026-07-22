package com.gamer.data.excel.view;

import com.gamer.data.gd.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.function.Consumer;

/**
 * 通用分页控制器：负责一行分页控件的 UI 和交互
 */
public class PaginationController {
    public final int defaultSize;
    public final Consumer<Integer> pageChange;
    public final Consumer<Integer> pageSizeChange;

    // 通用分页状态（供外部直接读取或复用）
    public int currentPage = 1;
    public int pageSize;
    public int totalPages = 1;

    public JLabel pageLabel;
    public JButton prevPageBtn;
    public JButton nextPageBtn;
    public JSpinner pageSpinner;
    public JComboBox<Integer> pageSizeComboBox;

    // 关联的数据面板组件（可选，由外部注入）
    public JTable table;
    public DefaultTableModel tableModel;
    public JLabel rowLabel;
    public JLabel colLabel;
    public JScrollPane scrollPane;

    public PaginationController(int defaultSize, Consumer<Integer> pageChange, Consumer<Integer> sizeChange) {
        this.defaultSize = defaultSize;
        this.pageChange = pageChange;
        this.pageSizeChange = sizeChange;
        this.pageSize = defaultSize;
    }

    public JPanel createPaginationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JLabel pageSizeLabel = new JLabel("每页:");
        pageSizeComboBox = new JComboBox<>(Utils.pageSizes);
        pageSizeComboBox.setSelectedItem(pageSize);
        pageSizeComboBox.addActionListener(e -> {
            Integer pageSize = (Integer)pageSizeComboBox.getSelectedItem();
            if (pageSize != null && pageSizeChange != null) {
                pageSizeChange.accept(pageSize);
            }
        });

        pageLabel = new JLabel("第 1/1 页");
        prevPageBtn = new JButton("上一页");
        prevPageBtn.setEnabled(false);
        prevPageBtn.addActionListener(e -> {
            if (pageSpinner != null) {
                int current = (Integer)pageSpinner.getValue();
                if (current > 1) {
                    int newPage = current - 1;
                    pageSpinner.setValue(newPage);
                    if (pageChange != null) {
                        pageChange.accept(newPage);
                    }
                }
            }
        });

        pageSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1, 1));
        pageSpinner.addChangeListener(e -> {
            if (pageChange != null) {
                int page = (Integer)pageSpinner.getValue();
                pageChange.accept(page);
            }
        });

        nextPageBtn = new JButton("下一页");
        nextPageBtn.setEnabled(false);
        nextPageBtn.addActionListener(e -> {
            if (pageSpinner != null) {
                SpinnerNumberModel model = (SpinnerNumberModel)pageSpinner.getModel();
                int current = (Integer)pageSpinner.getValue();
                int max = (Integer)model.getMaximum();
                if (current < max) {
                    int newPage = current + 1;
                    pageSpinner.setValue(newPage);
                    if (pageChange != null) {
                        pageChange.accept(newPage);
                    }
                }
            }
        });

        return getJPanel(panel, pageSizeLabel, pageSizeComboBox, prevPageBtn, pageLabel, pageSpinner, nextPageBtn);
    }

    public static JPanel getJPanel(JPanel panel, JLabel pageSizeLabel, JComboBox<Integer> pageSizeComboBox,
        JButton prevPageBtn, JLabel pageLabel, JSpinner pageSpinner, JButton nextPageBtn) {
        panel.add(pageSizeLabel);
        panel.add(pageSizeComboBox);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(prevPageBtn);
        panel.add(pageLabel);
        panel.add(new JLabel("跳转到:"));
        panel.add(pageSpinner);
        panel.add(nextPageBtn);
        return panel;
    }

    public void updateControls(int currentPage, int totalPages) {
        // 同步内部状态
        this.currentPage = currentPage;
        this.totalPages = totalPages;

        pageLabel.setText(String.format("第 %d/%d 页", currentPage, totalPages));
        prevPageBtn.setEnabled(currentPage > 1);
        nextPageBtn.setEnabled(currentPage < totalPages);
        SpinnerNumberModel model = (SpinnerNumberModel)pageSpinner.getModel();
        model.setMaximum(totalPages);
        int old = (Integer)pageSpinner.getValue();
        if (old != currentPage) {
            pageSpinner.setValue(currentPage);
        }
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
        if (pageSizeComboBox != null) {
            pageSizeComboBox.setSelectedItem(pageSize);
        }
    }
}