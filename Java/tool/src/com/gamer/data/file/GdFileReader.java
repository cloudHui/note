package com.gamer.data.file;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * GD文件读取器 用于读取WriteTools生成的.gd文件
 *
 * @author liuyunhui
 * @date 2025/12/05
 */
public class GdFileReader {

    /**
     * 读取GD 文件
     */
    public static GdData readGdFile(File gdFile) throws IOException {
        if (gdFile == null || !gdFile.exists()) {
            throw new FileNotFoundException("GD文件不存在: " + gdFile);
        }

        try (FileInputStream fis = new FileInputStream(gdFile);
            BufferedInputStream bis = new BufferedInputStream(fis)) {

            // 读取所有字节
            byte[] allBytes = readAllBytes(bis);
            ByteBuffer buffer = ByteBuffer.wrap(allBytes);
            buffer.order(ByteOrder.LITTLE_ENDIAN);

            // 解析头部
            GdHeader header = parseHeader(buffer);

            // 解析数据部分
            List<Object[]> dataRows = parseData(buffer, header);

            return new GdData(header, dataRows);
        }
    }

    /**
     * 读取InputStream 中的所有字节
     */
    private static byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[8192];
        int bytesRead;

        while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytesRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }

    /**
     * 解析GD 文件头部
     */
    private static GdHeader parseHeader(ByteBuffer buffer) {
        GdHeader header = new GdHeader();

        try {
            // 读取第一部分的16字节
            if (buffer.remaining() < 16) {
                throw new RuntimeException("GD文件格式错误: 头部信息不足");
            }

            header.rows = buffer.getInt();
            header.columns = buffer.getInt();
            header.colNameLength = buffer.getInt();
            header.colTypeLength = buffer.getInt();

            // 验证数据
            if (header.rows < 0 || header.columns < 0 || header.colNameLength < 0 || header.colTypeLength < 0) {
                throw new RuntimeException("GD文件格式错误: 头部数据异常");
            }

            // 读取列名
            if (header.colNameLength > 0) {
                if (buffer.remaining() < header.colNameLength) {
                    throw new RuntimeException("GD文件格式错误: 列名数据长度不足");
                }

                byte[] colNameBytes = new byte[header.colNameLength];
                buffer.get(colNameBytes);
                String colNameStr = new String(colNameBytes, StandardCharsets.UTF_8);

                // 按空字符分割列名
                String[] colNames = colNameStr.split("\u0000");
                for (String name : colNames) {
                    if (!name.trim().isEmpty()) {
                        header.columnNames.add(name.trim());
                    }
                }
            }

            // 读取列类型
            if (header.colTypeLength > 0) {
                if (buffer.remaining() < header.colTypeLength) {
                    throw new RuntimeException("GD文件格式错误: 列类型数据长度不足");
                }

                byte[] colTypeBytes = new byte[header.colTypeLength];
                buffer.get(colTypeBytes);
                String colTypeStr = new String(colTypeBytes, StandardCharsets.UTF_8);

                // 按空字符分割列类型
                String[] colTypes = colTypeStr.split("\u0000");
                for (String type : colTypes) {
                    if (!type.trim().isEmpty()) {
                        header.columnTypes.add(type.trim());
                    }
                }
            }

            // 验证列名和列类型数量是否匹配
            if (header.columnNames.size() != header.columnTypes.size()) {
                throw new RuntimeException(String.format("GD文件格式错误: 列名数量(%d)与列类型数量(%d)不匹配", header.columnNames.size(),
                    header.columnTypes.size()));
            }

            // 如果列名数量与columns不一致，使用columns作为列数
            if (header.columnNames.size() != header.columns) {
                System.out.printf("警告: 实际列名数量(%d)与声明的列数(%d)不一致%n", header.columnNames.size(), header.columns);
                header.columns = header.columnNames.size();
            }

        } catch (Exception e) {
            throw new RuntimeException("解析GD文件头部失败: " + e.getMessage(), e);
        }

        return header;
    }

    /**
     * 解析数据部分
     */
    private static List<Object[]> parseData(ByteBuffer buffer, GdHeader header) {
        List<Object[]> dataRows = new ArrayList<>();

        try {
            // 跳过数据部分长度标记（4字节）
            if (buffer.remaining() < 4) {
                throw new RuntimeException("GD文件格式错误: 数据长度标记缺失");
            }
            buffer.position(buffer.position() + 4);

            // 读取每一行数据
            for (int i = 0; i < header.rows; i++) {
                Object[] row = new Object[header.columns];

                for (int j = 0; j < header.columns; j++) {
                    String type = header.columnTypes.get(j);

                    switch (type) {
                        case "s": // 字符串类型
                            int strLength = buffer.getInt();
                            if (strLength > 0) {
                                if (buffer.remaining() < strLength) {
                                    throw new RuntimeException(
                                        String.format("GD文件格式错误: 第%d行第%d列字符串数据不足", i + 1, j + 1));
                                }
                                byte[] strBytes = new byte[strLength];
                                buffer.get(strBytes);
                                row[j] = new String(strBytes, StandardCharsets.UTF_8);
                            } else {
                                row[j] = "";
                            }
                            break;

                        case "f": // 浮点数类型
                            float floatValue = buffer.getFloat();
                            row[j] = floatValue;
                            break;

                        case "i": // 整数类型
                        case "v": // vindex 类型
                            int intValue = buffer.getInt();
                            row[j] = intValue;
                            break;

                        default:
                            throw new RuntimeException("未知的列类型: " + type);
                    }
                }

                dataRows.add(row);
            }

        } catch (Exception e) {
            throw new RuntimeException("解析GD文件数据失败: " + e.getMessage(), e);
        }

        return dataRows;
    }
}