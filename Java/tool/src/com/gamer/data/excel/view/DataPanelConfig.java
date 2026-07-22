package com.gamer.data.excel.view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * 创建数据面板的配置类
 */
public class DataPanelConfig {
    public String title;
    public DefaultTableCellRenderer cellRenderer;
    public DefaultTableModel tableModel;
    public JTable table;
    public JLabel rowLabel;
    public JLabel colLabel;
    public JScrollPane scrollPane;

    public DataPanelConfig(PanelCreationConfig config, JTable table, JLabel rowLabel, JLabel colLabel,
                           JScrollPane scrollPane) {
        this.title = config.title;
        this.cellRenderer = config.cellRendererSupplier;
        this.tableModel = config.tableModel;
        this.table = table;
        this.rowLabel = rowLabel;
        this.colLabel = colLabel;
        this.scrollPane = scrollPane;
    }
}