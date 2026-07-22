package com.gamer.data.gen.view;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 复选框树节点选择监听器
 */
public class CheckBoxTreeNodeSelectionListener extends MouseAdapter {
    private final JTree tree;

    public CheckBoxTreeNodeSelectionListener(JTree tree) {
        this.tree = tree;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int row = tree.getRowForLocation(x, y);

        if (row < 0)
            return;

        TreePath path = tree.getPathForRow(row);
        if (path == null)
            return;

        DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
        Object userObject = node.getUserObject();

        // 点击复选框区域
        Rectangle rect = tree.getRowBounds(row);
        if (x <= rect.x + 20) { // 假设复选框宽度为20
            // 点击 SheetLineNode（Title节点）时，只切换自身状态，不联动父节点
            if (userObject instanceof SheetLineNode) {
                SheetLineNode data = (SheetLineNode)userObject;
                data.selected = !data.selected;
                if (data.selected) {
                    // 自动选中父节点（Sheet节点）及祖父节点（文件节点）
                    setParentSelected(node, true);
                } else {
                    // 取消选中时：若同级兄弟都未选中，则取消父节点选中（递归向上）
                    uncheckParentIfAllSiblingsUnchecked(node);
                }
                tree.repaint();
            }
            // 点击 SheetNode（文件节点或Sheet节点）时，联动选中/取消所有子节点
            else if (userObject instanceof SheetNode) {
                SheetNode data = (SheetNode)userObject;
                data.selected = !data.selected;

                // 联动所有子节点（无论是 Sheet 还是 Title）
                for (int i = 0; i < node.getChildCount(); i++) {
                    DefaultMutableTreeNode child = (DefaultMutableTreeNode)node.getChildAt(i);
                    if (child.getUserObject() instanceof SheetNode) {
                        SheetNode childData = (SheetNode)child.getUserObject();
                        childData.selected = data.selected;
                    }
                    // Sheet 下的 SheetLineNode 也联动
                    for (int j = 0; j < child.getChildCount(); j++) {
                        DefaultMutableTreeNode lineNode = (DefaultMutableTreeNode)child.getChildAt(j);
                        if (lineNode.getUserObject() instanceof SheetLineNode) {
                            ((SheetLineNode)lineNode.getUserObject()).selected = data.selected;
                        }
                    }
                }

                if (data.selected) {
                    setParentSelected(node, true);
                } else {
                    uncheckParentIfAllSiblingsUnchecked(node);
                }
                tree.expandPath(path);
                tree.repaint();
            }
        }
    }

    /**
     * 向上设置父节点为选中（直到根）
     */
    private void setParentSelected(DefaultMutableTreeNode node, boolean selected) {
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
        if (parent == null || !(parent.getUserObject() instanceof SheetNode)) {
            return;
        }
        SheetNode parentData = (SheetNode)parent.getUserObject();
        parentData.selected = selected;
        setParentSelected(parent, selected);
    }

    /**
     * 当本节点取消选中时：若所有同级兄弟都未选中，则取消父节点选中，并递归向上检查
     */
    private void uncheckParentIfAllSiblingsUnchecked(DefaultMutableTreeNode node) {
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
        if (parent == null || !(parent.getUserObject() instanceof SheetNode)) {
            return;
        }
        boolean allSiblingsUnselected = true;
        for (int i = 0; i < parent.getChildCount(); i++) {
            DefaultMutableTreeNode sibling = (DefaultMutableTreeNode)parent.getChildAt(i);
            Object uo = sibling.getUserObject();
            if (uo instanceof SheetNode && ((SheetNode)uo).selected) {
                allSiblingsUnselected = false;
                break;
            }
            if (uo instanceof SheetLineNode && ((SheetLineNode)uo).selected) {
                allSiblingsUnselected = false;
                break;
            }
        }
        if (allSiblingsUnselected) {
            ((SheetNode)parent.getUserObject()).selected = false;
            uncheckParentIfAllSiblingsUnchecked(parent);
        }
    }
}