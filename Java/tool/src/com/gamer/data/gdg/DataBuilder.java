package com.gamer.data.gdg;

import java.io.File;

public class DataBuilder {
    public static String LANGUAGE = "zh";

    public static void main(String[] args) throws Exception {
        String language = System.getProperty("language");
        if (language != null && !language.isEmpty()) {
            LANGUAGE = language;
        }

        File baseDir = new File(".");
        File[] files = baseDir.listFiles((dir, name) -> {
            if (isBlank(name)) {
                return false;
            } else {
                String fname = name.trim().toLowerCase();
                return fname.endsWith(".xls") || fname.endsWith(".xlsx");
            }
        });
        if (files != null && files.length > 0) {
            for (File f : files) {
                ExcelOperate eo = new ExcelOperate();
                eo.init(f);
                eo.parseSheet();
                eo.destroy();
            }
        } else {
            System.exit(0);
        }
    }

    public static boolean isBlank(CharSequence cs) {
        if (cs != null) {
            int strLen = length(cs);
            if (strLen != 0) {
                for (int i = 0; i < strLen; ++i) {
                    if (!Character.isWhitespace(cs.charAt(i))) {
                        return false;
                    }
                }

            }
        }
        return true;
    }

    public static int length(CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }
}
