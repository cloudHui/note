package com.gamer.data.excel.view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

/**
 * 面板创建配置
 */
public  class PanelCreationConfig {
    public final String title;
    public final Class<?> columnClass;
    public final DefaultTableCellRenderer cellRendererSupplier;
    public final DefaultTableModel tableModel;

    public PanelCreationConfig(String title, Class<?> columnClass, PaginationController controller, DiffContext dContext) {
        this.title = title;
        this.columnClass = columnClass;
        this.cellRendererSupplier = createRenderer(controller, dContext);
        this.tableModel = createTableModel();
    }


    /**
     * 创建 TableModel
     */
    private DefaultTableModel createTableModel() {
        return new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnClass;
            }
        };
    }

    /**
     * 创建GD单元格渲染器（支持高亮差异，支持分页）
     */
    private DefaultTableCellRenderer createRenderer(PaginationController controller, DiffContext dContext) {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // 获取当前页的起始行号（用于分页时的行号映射）
                Integer startRow = (Integer)table.getClientProperty("startRow");
                int globalRow = (startRow != null ? startRow : 0) + row;

                // 判断是哪个表格，使用对应的映射
                boolean isDifference = isIsDifference(table, column, globalRow, controller, dContext);

                if (isDifference) {
                    c.setBackground(new Color(255, 200, 200)); // 浅红色背景
                    c.setForeground(Color.RED);
                } else {
                    c.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                    if (value != null) {
                        if (value instanceof Integer) {
                            c.setForeground(new Color(0, 0, 200));
                        } else if (value instanceof Float) {
                            c.setForeground(new Color(0, 100, 200));
                        } else if (value instanceof String) {
                            c.setForeground(Color.BLACK);
                        }
                    } else {
                        c.setForeground(Color.BLACK);
                    }
                }

                return c;
            }
        };
    }

    /**
     * 检查是否是差异单元格
     */
    private boolean isIsDifference(JTable table, int column, int globalRow, PaginationController controller,
                                   DiffContext diffContext) {
        JTable jTable = controller != null ? controller.table : null;
        Map<Integer, Integer> rowMapping = (table == jTable) ? diffContext.rowMapping1 : diffContext.rowMapping2;

        // 获取原始行号（如果只显示差异行，需要通过映射获取）
        Integer originalRow = rowMapping.get(globalRow);
        if (originalRow == null) {
            originalRow = globalRow; // 如果没有映射，说明显示的是所有行
        }

        // 检查是否是差异单元格（使用原始行号）
        String cellKey = originalRow + "," + column;
        return diffContext.differenceCells.contains(cellKey);
    }
}