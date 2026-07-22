package com.gamer.data.file;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.io.File;

/**
 * 自定义树节点渲染器
 */
public class FileTreeCellRenderer extends DefaultTreeCellRenderer {

    private final FileSystemView fsv = FileSystemView.getFileSystemView();

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
        boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        if (value instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
            Object userObject = node.getUserObject();

            if (userObject instanceof File) {
                File file = (File)userObject;
                setIcon(fsv.getSystemIcon(file));
                setText(fsv.getSystemDisplayName(file));
            } else if (userObject instanceof String) {
                setIcon(fsv.getSystemIcon(new File(System.getProperty("user.home"))));
            }
        }

        return this;
    }
}
