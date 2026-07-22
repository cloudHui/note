package com.gamer.data.gd;

import com.gamer.data.excel.DataType;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class WriteTools {
    public static final String SUFFIX = ".gd";
    public static final ByteOrder byteOrder;

    public static void write2Gd(SheetOperate sheetOpt) throws Exception {
        File outFile = new File(sheetOpt.getSheet().getSheetName() + SUFFIX);
        FileOutputStream fos = new FileOutputStream(outFile);
        fos.write(getFirstStepBytes(sheetOpt).array());
        fos.write(getSecondStepBytes(sheetOpt).array());
        fos.write(getThirdStepBytes(sheetOpt, fos));
        fos.flush();
        fos.close();
    }

    private static ByteBuffer getFirstStepBytes(SheetOperate opt) {
        ByteBuffer buf = ByteBuffer.allocate(16);
        buf.order(byteOrder);
        buf.putInt(opt.rows);
        int col = opt.getColumns();
        int c = 0;

        for (String s : opt.getHeadRows().get(2)) {
            if (s.isEmpty()) {
                ++c;
            }
        }

        col -= c;
        buf.putInt(col);
        buf.putInt(getColName(opt).toString().getBytes().length);
        buf.putInt(getColType(opt).toString().getBytes().length);
        buf.flip();
        return buf;
    }

    private static StringBuffer getColType(SheetOperate opt) {
        StringBuffer colTp = new StringBuffer();

        for (String colType : opt.getHeadRows().get(2)) {
            if (!colType.isEmpty()) {
                colTp.append(DataType.parse(colType).getShortName()).append('\u0000');
            }
        }

        return colTp;
    }

    private static StringBuffer getColName(SheetOperate opt) {
        StringBuffer colNa = new StringBuffer();

        for (String colName : opt.getHeadRows().get(0)) {
            if (!colName.isEmpty()) {
                colNa.append(colName).append('\u0000');
            }
        }

        return colNa;
    }

    private static ByteBuffer getSecondStepBytes(SheetOperate opt) {
        byte[] colNames = getColName(opt).toString().getBytes();
        byte[] colTypes = getColType(opt).toString().getBytes();
        ByteBuffer buf = ByteBuffer.allocate(colNames.length + colTypes.length);
        buf.order(byteOrder);
        buf.put(colNames);
        buf.put(colTypes);
        buf.flip();
        return buf;
    }

    private static byte[] getThirdStepBytes(SheetOperate opt, FileOutputStream out) throws Exception {
        ByteBuffer buf = ByteBuffer.allocate(4194304);
        buf.order(byteOrder);
        List<String[]> datas = opt.getDataRows();
        List<String[]> headDatas = opt.getHeadRows();
        String[] types = headDatas.get(2);
        int rows = opt.getRows();

        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < types.length; ++j) {
                if (!types[j].isEmpty()) {
                    if (DataType.parse(types[j]) == DataType.TYPE_STR) {
                        byte[] strByte = ((String[])datas.get(i))[j].getBytes(StandardCharsets.UTF_8);
                        buf.putInt(strByte.length);
                        buf.put(strByte);
                    } else if (DataType.parse(types[j]) == DataType.TYPE_FLOAT) {
                        buf.putFloat(Float.parseFloat(((String[])datas.get(i))[j]));
                    } else if (DataType.parse(types[j]) == DataType.TYPE_V_IDX
                        || DataType.parse(types[j]) == DataType.TYPE_INT) {
                        try {
                            buf.putInt(Integer.parseInt(((String[])datas.get(i))[j]));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        int pos = buf.position();
        ByteBuffer len = ByteBuffer.allocate(4);
        len.order(byteOrder);
        len.putInt(pos);
        len.flip();
        out.write(len.array());
        buf.flip();
        return Arrays.copyOfRange(buf.array(), 0, pos);
    }

    static {
        byteOrder = ByteOrder.LITTLE_ENDIAN;
    }
}
