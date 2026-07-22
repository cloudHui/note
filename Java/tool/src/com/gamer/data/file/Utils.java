package com.gamer.data.file;

import javax.swing.*;
import java.io.File;
import java.util.List;

public class Utils {

    /**
     * 格式化文件大小
     * 
     * @param size
     *            文件大小
     * @return 格式化后的文件大小
     */
    public static String formatFileSize(long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }

    /**
     * 获取文件扩展名
     * 
     * @param filename
     *            文件名
     * @return 文件扩展名
     */
    public static String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * 获取文件列表总页数
     * 
     * @return 文件列表总页数
     */
    public static int getFileListTotalPages(List<File> allFilesInDirectory, int fileListPageSize) {
        return (int)Math.ceil((double)allFilesInDirectory.size() / fileListPageSize);
    }

    /**
     * 展开所有树节点
     * 
     * @param directoryTree
     *            目录树
     */
    public static void expandAllTreeNodes(JTree directoryTree) {
        for (int i = 0; i < directoryTree.getRowCount(); i++) {
            directoryTree.expandRow(i);
        }
    }
}
