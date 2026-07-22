package com.gamer.data.gen.view;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

/**
 * 复选框树单元格渲染器
 */
public class CheckBoxTreeCellRenderer extends JPanel implements TreeCellRenderer {
    private final JCheckBox checkBox;
    private final JLabel label;

    public CheckBoxTreeCellRenderer() {
        setLayout(new BorderLayout());
        checkBox = new JCheckBox();
        label = new JLabel();
        add(checkBox, BorderLayout.WEST);
        add(label, BorderLayout.CENTER);
        setOpaque(false);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
        boolean leaf, int row, boolean hasFocus) {
        if (value instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            Object userObject = node.getUserObject();

            if (userObject instanceof SheetNode) {
                SheetNode data = (SheetNode)userObject;
                checkBox.setSelected(data.selected);
                label.setText(data.name);

                // 设置不同的图标
                if (data.name != null) {
                    label.setIcon(UIManager.getIcon("FileView.fileIcon"));
                } else {
                    label.setIcon(UIManager.getIcon("Tree.leafIcon"));
                }
            } else {
                checkBox.setSelected(false);
                label.setText(userObject.toString());
                label.setIcon(UIManager.getIcon("Tree.openIcon"));
            }
        }

        return this;
    }
}