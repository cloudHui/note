package com.gamer.data.excel;

/**
 * 数据类型
 * 
 * @author liuyunhui
 * @date 2025/12/05
 */
public enum DataType {
    /* 非存在 */
    NON_EXISTENT("null", "非存在"),
    /* vindex */
    TYPE_V_IDX("vindex", "v"),
    /* int */
    TYPE_INT("int", "i"),
    /* float */
    TYPE_FLOAT("float", "f"),
    /* string */
    TYPE_STR("string", "s");

    private String name;
    private final String shortName;

    DataType(String name, String shortName) {
        this.name = name;
        this.shortName = shortName;
    }

    public static DataType parse(String name) {
        DataType[] var4;
        int var3 = (var4 = values()).length;

        for (int var2 = 0; var2 < var3; ++var2) {
            DataType type = var4[var2];
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }

        return NON_EXISTENT;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return this.shortName;
    }
}
