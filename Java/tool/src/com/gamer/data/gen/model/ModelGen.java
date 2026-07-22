package com.gamer.data.gen.model;

import com.gamer.data.excel.ExcelExtensions;
import com.gamer.data.excel.ExcelViewer;
import com.gamer.data.excel.FileWithSheets;
import com.gamer.data.gen.Title;
import com.gamer.data.gen.WorkbookType;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 生成数据模板和GameConfig枚举
 *
 * @author liuyunhui
 */
public class ModelGen {

    // ================ 常量定义 ================

    /** 文件类型常量 */
    private static final String XLSX_EXTENSION = "xlsx";
    private static final String XLS_EXTENSION = "xls";

    public static final String FILE_EXT_XLS = ExcelExtensions.FILE_EXT_XLS;
    public static final String FILE_EXT_XLSX = ExcelExtensions.FILE_EXT_XLSX;

    /** 特殊处理的工作簿名称 */
    private static final String GAME_CONFIG = "GameConfig";
    private static final String MESSAGE = "Message";

    /** 数据模型包名前缀 */
    private static final String DATA_PACKAGE_PREFIX = "com.gow.common.config.";

    private static ExcelViewer view;

    // ================ 核心生成逻辑 ================

    /**
     * 生成模型或枚举代码
     *
     * @param allFiles
     *            待处理的Excel文件列表
     */
    public static void genCode(List<FileWithSheets> allFiles, ExcelViewer excelOperateViewer) {
        try {
            view = excelOperateViewer;
            List<ManagerGenInfo> allPendingManagers = new ArrayList<>();
            for (FileWithSheets file : allFiles) {
                System.out.println("开始处理文件: " + file);
                view.logMessage("开始处理文件: " + file);
                readExcelCreateJavaHead(file, allPendingManagers);
            }
            System.out.println("所有文件处理完成");
            view.logMessage("所有文件处理完成");
            // 统一处理所有收集到的 Manager（跨文件去重）
            flushPendingManagers(view.baseCheck.SERVER_PATH, allPendingManagers);
        } catch (Exception e) {
            view.logMessage("生成模型或枚举代码失败: " + e);
            System.out.println("生成模型或枚举代码失败: " + e);
        }
    }

    /**
     * 读取Excel并生成Java类头文件
     *
     * @param file
     *            Excel文件
     * @param allPendingManagers
     *            所有待处理的 Manager 列表
     */
    public static void readExcelCreateJavaHead(FileWithSheets file, List<ManagerGenInfo> allPendingManagers) {
        String fileExtension = getFileExtension(file.excelName);
        String javaName = firstUp(file.excelName.split("\\.")[0]);

        if (file.excelName.endsWith(XLSX_EXTENSION)) {
            processExcelForJavaHead(file, javaName, WorkbookType.XSSF, allPendingManagers);
        } else if (file.excelName.endsWith(XLS_EXTENSION)) {
            processExcelForJavaHead(file, javaName, WorkbookType.HSSF, allPendingManagers);
        } else {
            view.logMessage("不支持的文件类型: " + fileExtension);
            System.out.println("不支持的文件类型: " + fileExtension);
        }
    }

    /**
     * 处理Excel生成Java头文件
     * 
     * @param sheets
     *            Excel文件
     * @param javaName
     *            Java类名
     * @param type
     *            工作簿类型
     * @param allPendingManagers
     *            所有待处理的 Manager 列表
     */
    private static void processExcelForJavaHead(FileWithSheets sheets, String javaName, WorkbookType type,
        List<ManagerGenInfo> allPendingManagers) {
        try (InputStream inputStream =
            Files.newInputStream(Paths.get(view.baseCheck.XML_PATH + "\\" + sheets.excelName))) {
            // 创建工作簿
            Workbook workbook = createWorkbook(inputStream, type);
            // 处理工作簿，生成Java代码
            processGenJavaCode(view.baseCheck.SERVER_PATH, javaName, workbook, sheets, allPendingManagers);
        } catch (Exception e) {
            view.logMessage("处理Excel生成Java头文件失败: " + e);
            System.out.println("处理Excel生成Java头文件失败: " + e);
        }
    }

    /**
     * 处理工作簿，生成Java代码
     *
     * @param serverPath
     *            Server路径
     * @param javaName
     *            类名
     * @param workbook
     *            Excel工作簿
     * @param sheets
     *            文件与sheet内容
     * @param allPendingManagers
     *            所有待处理的 Manager 列表
     */
    public static void processGenJavaCode(String serverPath, String javaName, Workbook workbook, FileWithSheets sheets,
        List<ManagerGenInfo> allPendingManagers) {
        List<Title> sheetLines;
        for (int index = 0; index < workbook.getNumberOfSheets(); index++) {
            Sheet sheet = workbook.getSheetAt(index);
            if (sheet != null) {

                sheetLines = sheets.sheets.get(sheet.getSheetName());
                if (sheetLines == null || sheetLines.isEmpty()) {
                    continue;
                }
                switch (javaName) {
                    case MESSAGE:
                        continue;
                    case GAME_CONFIG:
                        // 生成配置模型
                        processSheetForJavaHead(sheet, serverPath, javaName, sheetLines, allPendingManagers);
                        // 如果是GameConfig或Message，额外生成枚举
                        processSheetForConst(sheet, serverPath, javaName);
                        break;
                    default:
                        processSheetForJavaHead(sheet, serverPath, javaName, sheetLines, allPendingManagers);
                        break;
                }
            }
        }
    }

    /**
     * 处理Sheet生成配置模型类
     */
    public static void processSheetForJavaHead(Sheet sheet, String serverPath, String javaName, List<Title> titleList,
        List<ManagerGenInfo> pendingManagers) {
        String finalJavaName = getJavaName(sheet.getSheetName(), javaName);
        try {
            String genJavaModel = genJavaModel(finalJavaName, titleList, javaName.toLowerCase());
            String replace = DATA_PACKAGE_PREFIX.replace(".", "/");
            String path = serverPath + "/common/src/" + replace + javaName.toLowerCase();
            // 写入文件
            doWrite(finalJavaName, path, genJavaModel);

            // 收集：稍后统一生成 Manager、统一更新 gameserver/worldserver 配置
            if (pendingManagers != null) {
                boolean exists =
                    pendingManagers.stream().anyMatch(info -> info.getConfigClassName().equals(finalJavaName));
                if (!exists) {
                    pendingManagers.add(
                        new ManagerGenInfo(finalJavaName, javaName.toLowerCase(), sheet.getSheetName(), titleList));
                }
            }
        } catch (Exception e) {
            view.logMessage("处理Sheet生成配置模型类失败: " + javaName + " error: " + e);
            System.out.println("处理Sheet生成配置模型类失败: " + javaName + " error: " + e);
        }
    }

    /**
     * 设置sheet 内容
     *
     * @param sheets
     *            文件与sheet 内容
     * @param sheetName
     *            sheet 名称
     * @param sheet
     *            sheet 内容
     */
    public static void setJavaTitle(FileWithSheets sheets, String sheetName, Sheet sheet) {
        Row propertyName = sheet.getRow(0);

        if (propertyName == null) {
            System.out.println("Sheet " + sheet.getSheetName() + " 没有属性行，跳过");
            view.logMessage("Sheet " + sheet.getSheetName() + " 没有属性行，跳过");
            return;
        }

        getJavaTitle(propertyName, sheet, sheets.getSheet(sheetName));
    }

    /**
     * 从Excel 表格中解析并生成Java 属性名称和类型的集合
     * <p>
     * 该方法遍历Excel表格的属性名行（第0行），逐列提取字段信息， 并为每个有效字段创建对应的Title对象添加到集合中。
     * <p>
     * Excel表格结构约定： - 第0行：属性名称（字段名） - 第1行：字段描述 - 第2行：属性类型（如 int, string, int[], int[][] 等） - 第3行：附加描述（可选）
     * <p>
     * 处理流程： 1. 获取描述行（第1行）、类型行（第2行）和附加描述行（第3行） 2. 遍历属性名行的每个单元格 3. 调用 processCellForTitle 方法处理每个单元格，生成Title对象 4.
     * 将有效的Title对象添加到titleList集合中 5. 对于string类型的字段，调用 checkStringData 检测实际数据结构， 自动识别并转换为合适的数组类型（如 int[], int[][] 等）
     *
     * @param propertyName
     *            属性名行（Excel第0行），包含所有字段名称
     * @param sheet
     *            Excel工作表对象，用于获取其他行的数据
     * @param titleList
     *            输出参数，用于存储解析后的Java属性名称和类型集合
     */
    private static void getJavaTitle(Row propertyName, Sheet sheet, List<Title> titleList) {
        Row desc = sheet.getRow(1);
        Row propertyType = sheet.getRow(2);
        Row descAp = sheet.getRow(3);

        for (int cellIndex = 0; cellIndex < propertyName.getPhysicalNumberOfCells(); cellIndex++) {
            Title title = processCellForTitle(propertyName, desc, propertyType, descAp, cellIndex);
            if (title != null) {
                titleList.add(title);
                // 检测 string 类型字段的数据结构，自动识别并转换为合适的数组类型。
                checkStringData(title, sheet, cellIndex);
            }
        }
    }

    /**
     * 处理单个单元格，生成Title对象
     * <p>
     * 该方法从Excel表格的指定列中提取属性信息，并构建对应的Title对象。
     * <p>
     * 处理流程： 1. 从属性名行（第0行）获取字段名称 2. 从类型行（第2行）获取字段类型 3. 从描述行（第1行）和附加描述行（第3行）获取字段描述，并合并 4. 验证字段名和类型是否有效（非空且类型不以'_'开头） 5.
     * 规范化类型名称（如将 String 转为 string，Integer 转为 int 等） 6. 将字段名转换为驼峰命名格式
     * 
     * @param propertyName
     *            属性名行（Excel第0行）
     * @param desc
     *            描述行（Excel第1行）
     * @param propertyType
     *            类型行（Excel第2行）
     * @param descAp
     *            附加描述行（Excel第3行）
     * @param cellIndex
     *            当前处理的列索引
     * @return 生成的Title对象，如果字段无效则返回null
     */
    private static Title processCellForTitle(Row propertyName, Row desc, Row propertyType, Row descAp, int cellIndex) {
        String name = getCellValue(propertyName.getCell(cellIndex));
        String type = getCellValue(propertyType != null ? propertyType.getCell(cellIndex) : null);
        String description = getCellValue(desc != null ? desc.getCell(cellIndex) : null);

        // 合并描述行
        if (descAp != null) {
            description += " " + getCellValue(descAp.getCell(cellIndex)).replace("\n", " ");
        }

        try {
            // 列名规范化：去除首尾空格及常见不可见字符，避免生成带空格的字段名
            if (name != null) {
                name = normalizeColumnName(name);
            }
            // 验证基本字段
            if (name == null || name.isEmpty() || type == null || type.isEmpty() || type.charAt(0) == '_') {
                return null;
            }

            // 保存原始类型
            String originalType = type.trim();

            // 规范化类型
            type = normalizeType(type);

            // 转换为驼峰命名
            String camelName = toCamelCase(name, false).replace("_", "");
            return new Title(camelName, type, description.trim(), name, originalType);
        } catch (Exception e) {
            System.out.println("处理单个单元格，生成Title对象失败: " + name + " " + type + " " + description + " error: " + e);
            view.logMessage("处理单个单元格，生成Title对象失败: " + name + " " + type + " " + description + " error: " + e);
            return null;
        }
    }

    /**
     * 规范化 Excel 列名：去除首尾空格及常见不可见字符（零宽字符等），避免生成带空格或不可见字符的 Java 字段名。
     *
     * @param columnName
     *            原始列名
     * @return 规范化后的列名
     */
    private static String normalizeColumnName(String columnName) {
        if (columnName == null) {
            return null;
        }
        String s = columnName.trim();
        // 去除常见不可见字符：零宽空格、零宽非连接符、零宽连接符、BOM
        s = s.replace("\u200B", "").replace("\u200C", "").replace("\u200D", "").replace("\uFEFF", "");
        return s.trim();
    }

    /**
     * 检测 string 类型字段的数据结构，自动识别并转换为合适的数组类型。
     * <p>
     * 该方法会分析 Excel 表格中 string 类型列的实际数据内容，判断其是否为数组结构： - 如果数据包含分隔符（如逗号、竖线等），则识别为一维数组 - 如果数据包含多层分隔符，则识别为二维数组 -
     * 同时会判断数组元素是否全为整数，以决定使用 int[] 还是 string[] 类型
     * <p>
     * 转换规则： - 纯整数一维数组 → int[] - 混合类型一维数组 → string[] - 纯整数二维数组 → int[][] - 混合类型二维数组 → string[][]
     * <p>
     * 注意：GameConfig 表不进行此转换处理
     * 
     * @param title
     *            字段标题对象，用于设置转换后的类型和注解
     * @param sheet
     *            Excel 工作表，用于读取实际数据进行分析
     * @param index
     *            列索引，指定要分析的列
     */
    private static void checkStringData(Title title, Sheet sheet, int index) {
        // 只处理 string 类型，且不是 GameConfig
        if (!title.getType().equals("string") || sheet.getSheetName().equals(GAME_CONFIG)) {
            return;
        }

        ArrayValue arrayValue = analyzeArrayStructure(sheet, index);

        // 若字段描述中同时包含拆分符号（|），则按二维数组处理（如 Pub.xlsx GachaGroup 等表）
        String des = title.getDes();
        if (des != null) {
            if (des.contains("|")) {
                arrayValue.isIntArray = true;
                arrayValue.divide = "SPLIT_PIPE";
            }
        }

        // 根据数组分析结果设置Title 的类型和注解
        applyArrayTypeToTitle(title, arrayValue);
    }

    /**
     * 根据数组分析结果设置Title 的类型和注解
     * <p>
     * 该方法根据数组分析结果设置Title 的类型和注解。
     * <p>
     * 处理流程： 1. 根据数组分析结果设置Title 的类型和注解 2. 如果数组分析结果为二维数组，则设置二维数组类型 3. 如果数组分析结果为一维数组，则设置一维数组类型 4. 如果不是数组，保持 string 类型不变
     */
    private static void applyArrayTypeToTitle(Title title, ArrayValue arrayValue) {
        String annotationCode = "value = \"" + title.getOldName() + "\", isArray = true";

        if (arrayValue.arrayTwo) {
            // 二维数组
            setTwoDimensionalArrayType(title, arrayValue, annotationCode);
        } else if (arrayValue.array) {
            // 一维数组
            setOneDimensionalArrayType(title, arrayValue, annotationCode);
        }
        // 如果不是数组，保持 string 类型不变
    }

    /**
     * 设置二维数组类型
     * <p>
     * 该方法设置二维数组类型。
     * <p>
     * 处理流程： 1. 如果数组分析结果为纯整数，则设置 int[][] 类型 2. 如果数组分析结果为混合类型，则设置 string[][] 类型
     * 
     * @param title
     *            字段标题对象，用于设置转换后的类型和注解
     * @param arrayValue
     *            数组分析结果
     * @param annotationCode
     *            注解代码
     */
    private static void setTwoDimensionalArrayType(Title title, ArrayValue arrayValue, String annotationCode) {
        if (Boolean.TRUE.equals(arrayValue.isIntArrayTwo)) {
            title.setNewCode(annotationCode);
            title.setType("int[][]");
        } else {
            title.setNewCode(annotationCode);
            title.setType("string[][]");
        }
    }

    /**
     * 设置一维数组类型
     * <p>
     * 该方法设置一维数组类型。
     * <p>
     * 处理流程： 1. 如果数组分析结果为纯整数，则设置 int[] 类型 2. 如果数组分析结果为混合类型，则设置 string[] 类型
     * 
     * @param title
     *            字段标题对象，用于设置转换后的类型和注解
     * @param arrayValue
     *            数组分析结果
     * @param annotationCode
     *            注解代码
     */
    private static void setOneDimensionalArrayType(Title title, ArrayValue arrayValue, String annotationCode) {
        if (Boolean.TRUE.equals(arrayValue.isIntArray)) {
            title.setNewCode(annotationCode);
            title.setType("int[]");
        } else {
            title.setNewCode(annotationCode);
            title.setType("String[]");
        }
    }

    /**
     * 分析数组结构
     * <p>
     * 该方法分析数组结构。
     * <p>
     * 处理流程： 1. 先检查数据行 2. 如果数据行没有检测到二维数组，检查描述列 3. 返回数组分析结果
     * 
     * @param sheet
     *            Excel工作簿
     * @param index
     *            列索引
     * @return 数组分析结果
     */
    private static ArrayValue analyzeArrayStructure(Sheet sheet, int index) {
        ArrayValue arrayValue = new ArrayValue();

        // 先检查数据行
        for (int rowNum = 5; rowNum < sheet.getPhysicalNumberOfRows(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }

            String cellValue = getCellValue(row.getCell(index));
            if (cellValue == null || cellValue.trim().isEmpty() || "-1".equals(cellValue)) {
                continue;
            }

            String[] values = extractValuesFromCell(cellValue, arrayValue);
            if (values == null || values.length <= 1) {
                continue;
            }

            // 分析数组类型，只要有一行支持最大维数就按最大拆分
            analyzeArrayType(arrayValue, values, false);

            // 如果已经检测到二维数组，直接返回
            if (arrayValue.arrayTwo) {
                return arrayValue;
            }
        }

        // 如果数据行没有检测到二维数组，检查描述列
        if (!arrayValue.arrayTwo) {
            return analyzeDes(sheet, index, arrayValue);
        }
        return arrayValue;
    }

    /**
     * 分析描述行
     * <p>
     * 该方法分析描述行。
     * <p>
     * 处理流程： 1. 检查描述列（第4行，索引为3） 2. 如果描述列不存在，返回数组值 3. 如果描述列存在，获取描述列的值 4. 如果描述列的值为空，返回数组值
     * 
     * @param sheet
     *            Excel工作簿
     * @param index
     *            列索引
     * @param arrayValue
     *            数组值
     * @return 数组值
     */
    private static ArrayValue analyzeDes(Sheet sheet, int index, ArrayValue arrayValue) {
        // 检查描述列（第4行，索引为3）
        Row desRow = sheet.getRow(3);
        if (desRow == null) {
            return arrayValue;
        }

        String desValue = getCellValue(desRow.getCell(index));
        if (desValue == null || desValue.trim().isEmpty() || "-1".equals(desValue)) {
            return arrayValue;
        }

        String[] values = extractValuesFromCell(desValue, arrayValue);
        if (values == null || values.length <= 1) {
            return arrayValue;
        }

        // 分析描述列的数组类型
        analyzeArrayType(arrayValue, values, true);
        return arrayValue;
    }

    /**
     * 从单元格值中提取分割后的字符串数组 优先使用 | 拆分，然后使用 , 拆分
     * <p>
     * 该方法从单元格值中提取分割后的字符串数组。
     * <p>
     * 处理流程： 1. 如果单元格值为空，返回 null 2. 如果单元格值包含 |，则使用 | 拆分 3. 如果单元格值包含 ,，则使用 , 拆分 4. 返回拆分后的字符串数组
     * 
     * @param cellValue
     *            单元格值
     * @param arrayValue
     *            数组值
     * @return 拆分后的字符串数组
     */
    private static String[] extractValuesFromCell(String cellValue, ArrayValue arrayValue) {
        if (cellValue == null || cellValue.trim().isEmpty()) {
            return null;
        }

        // 优先使用 | 拆分
        if (cellValue.contains("|")) {
            arrayValue.divide = "SPLIT_PIPE";
            return cellValue.split("\\|");
        }
        // 再看能不能用 , 拆分
        else if (cellValue.contains(",")) {
            arrayValue.divide = "SPLIT_COMMA";
            return cellValue.split(",");
        }

        return null;
    }

    /**
     * 分析数组类型并更新ArrayValue对象 检查是否是 int 只要有一行数据支持最大维数数组拆分就按最大拆分
     * <p>
     * 该方法分析数组类型并更新ArrayValue对象。
     * <p>
     * 处理流程： 1. 根据分析结果设置ArrayValue（取最大维数） 2. 如果分析结果为二维数组，则设置二维数组类型 3. 如果分析结果为一维数组，则设置一维数组类型
     * 
     * @param arrayValue
     *            数组值
     * @param values
     *            字符串数组
     * @param isDes
     *            是否是描述行
     */
    private static void analyzeArrayType(ArrayValue arrayValue, String[] values, boolean isDes) {
        ArrayTypeAnalysisResult result = analyzeArrayValues(arrayValue, values, isDes);

        // 根据分析结果设置ArrayValue（取最大维数）
        if (result.hasTwoDimensionalStructure) {
            // 二维数组
            arrayValue.arrayTwo = true;
            arrayValue.array = false;
            // 类型判断在 analyzeTwoDimensionalType 中已经设置
        } else if (values.length > 1) {
            // 一维数组
            arrayValue.array = true;
            setOneDimensionalArrayTypeFlags(arrayValue, result.allInt);
        }
    }

    /**
     * 分析数组值，返回分析结果
     * <p>
     * 该方法分析数组值，返回分析结果。
     * <p>
     * 处理流程： 1. 检查是否是二维数组 2. 检查是否是一维数组 3. 返回分析结果
     * 
     * @param arrayValue
     *            数组值
     * @param values
     *            字符串数组
     * @param isDes
     *            是否是描述行
     * @return 分析结果
     */
    private static ArrayTypeAnalysisResult analyzeArrayValues(ArrayValue arrayValue, String[] values, boolean isDes) {
        boolean hasTwoDimensionalStructure = false;
        boolean allInt = true;

        for (String value : values) {
            value = value.trim();
            if (value.isEmpty()) {
                continue;
            }

            ValueAnalysisResult valueResult = analyzeSingleValue(value, arrayValue.divide, isDes);
            if (valueResult.isTwoDimensional) {
                hasTwoDimensionalStructure = true;
                analyzeTwoDimensionalType(arrayValue, valueResult.subValues, isDes);
                allInt = false;
            } else if (valueResult.isNumeric) {
                // 一维数组的值，检查是否是 int
                if (isNotInteger(value) && !isDes) {
                    allInt = false;
                }
            } else {
                allInt = false;
            }
        }

        return new ArrayTypeAnalysisResult(hasTwoDimensionalStructure, allInt);
    }

    /**
     * 分析单个值
     * <p>
     * 该方法分析单个值。
     * <p>
     * 处理流程： 1. 检查是否是描述行 2. 检查是否是一维数组 3. 返回分析结果
     * 
     * @param value
     *            值
     * @param currentDivide
     *            当前拆分符
     * @param isDes
     *            是否是描述行
     * @return 分析结果
     */
    private static ValueAnalysisResult analyzeSingleValue(String value, String currentDivide, boolean isDes) {
        // 描述只拆分不校验数字，数据行校验数字
        if (isDes) {
            // 描述行：只做二维数组格式的划分拆分，不校验是否为数字类型
            String[] subValues = splitForTwoDimensional(value, currentDivide);
            if (subValues != null) {
                // 拆分成功，认为是二维数组，isNumeric设为false（描述不校验数字）
                return new ValueAnalysisResult(true, true, subValues);
            }
            // 不是二维结构，isNumeric设为false，subValues为null
            return new ValueAnalysisResult(false, true, null);
        } else {
            if (isNotNumeric(value)) {
                // 非数字，检查是否为二维数组格式
                String[] subValues = splitForTwoDimensional(value, currentDivide);
                if (subValues != null) {
                    return new ValueAnalysisResult(true, true, subValues);
                }
                return new ValueAnalysisResult(false, false, null);
            } else {
                // 是数字，检查是否能拆分成二维数组格式
                String[] subValues = splitForTwoDimensional(value, currentDivide);
                if (subValues != null) {
                    return new ValueAnalysisResult(true, true, subValues);
                }
                return new ValueAnalysisResult(false, true, null);
            }
        }
    }

    /**
     * 设置一维数组类型标志
     * <p>
     * 该方法设置一维数组类型标志。
     * <p>
     * 处理流程： 1. 如果当前行全部是整数，设置为 int[] 2. 如果当前行不是全部数字，不改变之前的状态
     * 
     * @param arrayValue
     *            数组值
     * @param allInt
     *            是否是全部整数
     */
    private static void setOneDimensionalArrayTypeFlags(ArrayValue arrayValue, boolean allInt) {
        if (allInt) {
            // 如果当前行全部是整数，设置为 int[]
            if (arrayValue.isIntArray == null) {
                arrayValue.isIntArray = true;
            }
        }
        // 如果当前行不是全部数字，不改变之前的状态
    }

    /**
     * 分析二维数组的子值类型，检查是否是 int
     * <p>
     * 该方法分析二维数组的子值类型，检查是否是 int。
     * <p>
     * 处理流程： 1. 检查是否是 int 2. 设置二维数组类型
     * 
     * @param arrayValue
     *            数组值
     * @param subValues
     *            子值数组
     * @param isDes
     *            是否是描述行
     */
    private static void analyzeTwoDimensionalType(ArrayValue arrayValue, String[] subValues, boolean isDes) {
        boolean allInt = true;

        for (String subValue : subValues) {
            subValue = subValue.trim();
            if (subValue.isEmpty()) {
                continue;
            }

            // 检查是否是 int
            if (isNotInteger(subValue) && !isDes) {
                allInt = false;
                break;
            }
        }

        // 设置二维数组类型
        if (allInt) {
            // 如果当前行全部是整数，设置为 int[][]
            if (arrayValue.isIntArrayTwo == null) {
                arrayValue.isIntArrayTwo = true;
            }
        }
    }

    /**
     * 检查字符串是否为整数
     * <p>
     * 该方法检查字符串是否为整数。
     * <p>
     * 处理流程： 1. 检查是否是整数 2. 返回是否是整数
     * 
     * @param str
     *            字符串
     * @return 是否是整数
     */
    private static boolean isNotInteger(String str) {
        try {
            Integer.parseInt(str);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    /**
     * 检查是否为二维数组格式并进行分割 如果第一层用 | 拆分，第二层用 , 拆分 如果第一层用 , 拆分，第二层用 | 拆分
     * <p>
     * 该方法检查是否为二维数组格式并进行分割。
     * <p>
     * 处理流程： 1. 如果第一层用 | 拆分，第二层用 , 拆分 2. 如果第一层用 , 拆分，第二层用 | 拆分 3. 返回拆分后的字符串数组
     * 
     * @param value
     *            值
     * @param currentDivide
     *            当前拆分符
     * @return 拆分后的字符串数组
     */
    private static String[] splitForTwoDimensional(String value, String currentDivide) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        // 如果第一层用 | 拆分，第二层用 , 拆分
        if ("SPLIT_PIPE".equals(currentDivide) && value.contains(",")) {
            return value.split(",");
        }
        // 如果第一层用 , 拆分，第二层用 | 拆分
        else if ("SPLIT_COMMA".equals(currentDivide) && value.contains("|")) {
            return value.split("\\|");
        }
        // 如果当前没有确定分隔符，优先尝试 ,
        else if (currentDivide == null) {
            if (value.contains(",")) {
                return value.split(",");
            } else if (value.contains("|")) {
                return value.split("\\|");
            }
        }

        return null;
    }

    /**
     * 检查字符串是否为非数字
     * <p>
     * 该方法检查字符串是否为非数字。
     * <p>
     * 处理流程： 1. 检查是否是数字 2. 返回是否是数字
     * 
     * @param str
     *            字符串
     * @return 是否是数字
     */
    private static boolean isNotNumeric(String str) {
        try {
            Float.parseFloat(str);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    /**
     * 获取常量值
     * <p>
     * 该方法获取常量值。
     * <p>
     * 处理流程： 1. 获取常量值 2. 返回常量值
     * 
     * @param sheet
     *            Excel工作簿
     * @return 常量值
     */
    private static List<ConstType> getConstValue(Sheet sheet) {
        List<ConstType> titleList = new ArrayList<>();

        for (int rowIndex = 5; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row != null) {
                String name = getCellValue(row.getCell(1));
                if (name == null || name.trim().isEmpty()) {
                    continue;
                }

                String value = getCellValue(row.getCell(0));
                String des = getCellValue(row.getCell(2));

                int id;
                try {
                    id = Integer.parseInt(value);
                } catch (Exception e) {
                    try {
                        id = (int)Float.parseFloat(value);
                    } catch (Exception f) {
                        System.out.println("无法解析ID: " + value);
                        view.logMessage("无法解析ID: " + value);
                        continue;
                    }
                }

                titleList.add(new ConstType(id, des, name.toUpperCase()));
            }
        }
        return titleList;
    }

    /**
     * 处理Sheet生成枚举类
     * <p>
     * 该方法处理Sheet生成枚举类。
     * <p>
     * 处理流程： 1. 获取常量值 2. 获取Java名称 3. 获取包路径 4. 生成枚举类代码 5. 写入文件
     */
    public static void processSheetForConst(Sheet sheet, String serverPath, String javaName) {
        List<ConstType> titleList = getConstValue(sheet);

        String finalJavaName = getJavaNameConst(sheet.getSheetName(), javaName);
        String packages;
        String basePath = serverPath + "/common/src/";

        // if (javaName.equals(MESSAGE)) {
        // finalJavaName = "ActionResult";
        // packages = "com/gow/common/net/action";
        // } else {
        packages = "com/gow/common/config/constant";
        // }

        try {
            doWrite(finalJavaName, basePath + packages, generateEnumCode(finalJavaName, titleList, packages));
            System.out.println(finalJavaName + ".java 文件已生成。");
            view.logMessage(finalJavaName + ".java 文件已生成。");
        } catch (Exception e) {
            System.out.println("处理Sheet生成配置模型类失败: " + javaName + " error: " + e);
            view.logMessage("处理Sheet生成配置模型类失败: " + javaName + " error: " + e);
        }
    }

    // ================ 文件操作 ================
    /**
     * 写入Java文件
     * <p>
     * 该方法写入Java文件。
     * <p>
     * 处理流程： 1. 写入Java文件 2. 返回写入后的文件路径
     * 
     * @param javaName
     *            Java名称
     * @param path
     *            文件路径
     * @param content
     *            文件内容
     */
    private static void doWrite(String javaName, String path, String content) throws IOException {
        String fileName = javaName + ".java";
        Path directoryPath = Paths.get(path);

        // 创建目录（如果不存在）
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
            System.out.println("目录已创建: " + path);
            view.logMessage("目录已创建: " + path);
        }

        // 构建完整文件路径
        Path filePath = directoryPath.resolve(fileName);

        // 如果文件已存在，先删除
        if (Files.exists(filePath)) {
            Files.delete(filePath);
            System.out.println("已删除原有文件: " + filePath);
            view.logMessage("已删除原有文件: " + filePath);
        }

        // 写入文件
        Files.write(filePath, content.getBytes(StandardCharsets.UTF_8));
        System.out.println("文件写入成功: " + filePath);
        view.logMessage("文件写入成功: " + filePath);
    }

    /**
     * 写入数据模型类
     * <p>
     * 该方法写入数据模型类。
     * <p>
     * 处理流程： 1. 写入数据模型类 2. 返回写入后的字符串
     * 
     * @param javaName
     *            Java名称
     * @param titleList
     *            标题列表
     * @param filePath
     *            文件路径
     * @return 写入后的字符串
     */
    public static String genJavaModel(String javaName, List<Title> titleList, String filePath) {
        StringBuilder sb = new StringBuilder();

        // 类注释和声明
        sb.append("/**\n");
        sb.append(" * ").append(javaName).append("\n");
        sb.append(" *\n");
        sb.append(" * @author ").append(getAuthor()).append("\n");
        sb.append(" */\n");
        sb.append("public class ").append(javaName).append(" extends CoreObject {\n\n");
        sb.append("    private static final long serialVersionUID = 1L;\n\n");

        // 构造函数
        sb.append("    public ").append(javaName).append("() {\n");
        sb.append("        // 默认构造函数\n");
        sb.append("    }\n");

        // 字段定义
        for (Title title : titleList) {
            appendCodeField(sb, title);
        }

        StringBuilder imports = new StringBuilder();
        imports.append("package ").append(DATA_PACKAGE_PREFIX).append(filePath).append(";\n\n");
        imports.append("import com.gamer.core.CoreObject;\n");
        imports.append("import com.gow.common.config.DataCell;\n");

        sb.append("\n    // Getters\n");
        for (Title title : titleList) {
            appendCodeFieldGetSet(sb, title);
        }

        sb.append("}\n");
        return imports.append("\n").append(sb).toString();
    }

    // ================ 代码生成辅助方法 ================
    /**
     * 生成枚举类代码
     * <p>
     * 该方法生成枚举类代码。
     * <p>
     * 处理流程： 1. 生成枚举类代码 2. 返回生成后的字符串
     * 
     * @param className
     *            类名称
     * @param enumItems
     *            枚举项列表
     * @param packages
     *            包路径
     * @return 生成后的字符串
     */
    public static String generateEnumCode(String className, List<ConstType> enumItems, String packages) {
        StringBuilder code = new StringBuilder();
        packages = packages.replace("/", ".");

        appendEnumPackageAndImports(code, packages);
        appendEnumClassHeader(code, className);
        appendEnumItems(code, enumItems);
        appendEnumFields(code, className);
        appendEnumStaticInit(code, className);
        appendEnumConstructor(code, className);
        appendEnumMethods(code, className);
        code.append("}");

        return code.toString();
    }

    /**
     * 添加枚举类的包声明和导入
     * <p>
     * 该方法添加枚举类的包声明和导入。
     * <p>
     * 处理流程： 1. 添加枚举类的包声明和导入 2. 返回添加后的字符串
     * 
     * @param code
     *            代码
     * @param packages
     *            包路径
     */
    private static void appendEnumPackageAndImports(StringBuilder code, String packages) {
        code.append("package ").append(packages).append(";\n\n");
        code.append("import java.util.HashMap;\n");
        code.append("import java.util.Map;\n\n");
    }

    /**
     * 添加枚举类的类注释和声明
     * <p>
     * 该方法添加枚举类的类注释和声明。
     * <p>
     * 处理流程： 1. 添加枚举类的类注释和声明 2. 返回添加后的字符串
     * 
     * @param code
     *            代码
     * @param className
     *            类名称
     */
    private static void appendEnumClassHeader(StringBuilder code, String className) {
        code.append("/**\n");
        code.append(" * ").append(className).append("\n");
        code.append(" * \n");
        code.append(" * @author ").append(getAuthor()).append("\n");
        code.append(" */\n");
        code.append("public enum ").append(className).append(" {\n\n");
    }

    /**
     * 添加枚举项
     * <p>
     * 该方法添加枚举项。
     * <p>
     * 处理流程： 1. 添加枚举项 2. 返回添加后的字符串
     * 
     * @param code
     *            代码
     * @param enumItems
     *            枚举项列表
     */
    private static void appendEnumItems(StringBuilder code, List<ConstType> enumItems) {
        for (int i = 0; i < enumItems.size(); i++) {
            ConstType item = enumItems.get(i);
            code.append("    /** ").append(item.getName()).append("(").append(item.getId()).append(", \"")
                .append(item.getDes()).append("\") */\n");
            code.append("    ").append(item.getName()).append("(").append(item.getId()).append(", \"")
                .append(item.getDes()).append("\")");
            code.append(i < enumItems.size() - 1 ? ",\n" : ";\n\n");
        }
    }

    /**
     * 添加枚举类的字段
     * <p>
     * 该方法添加枚举类的字段。
     * <p>
     * 处理流程： 1. 添加枚举类的字段 2. 返回添加后的字符串
     * 
     * @param code
     *            代码
     * @param className
     *            类名称
     */
    private static void appendEnumFields(StringBuilder code, String className) {
        code.append("    private final int code;\n\n");
        code.append("    private final String comment;\n\n");
        code.append("    private final static Map<Integer, ").append(className)
            .append("> enums = new HashMap<>();\n\n");
    }

    /**
     * 添加枚举类的静态初始化
     * <p>
     * 该方法添加枚举类的静态初始化。
     * <p>
     * 处理流程： 1. 添加枚举类的静态初始化 2. 返回添加后的字符串
     * 
     * @param code
     *            代码
     * @param className
     *            类名称
     */
    private static void appendEnumStaticInit(StringBuilder code, String className) {
        code.append("    static {\n");
        code.append("        for (").append(className).append(" e : values()) {\n");
        code.append("            enums.put(e.getCode(), e);\n");
        code.append("        }\n");
        code.append("    }\n\n");
    }

    /**
     * 添加枚举类的构造方法
     * <p>
     * 该方法添加枚举类的构造方法。
     * <p>
     * 处理流程： 1. 添加枚举类的构造方法 2. 返回添加后的字符串
     * 
     * @param code
     *            代码
     * @param className
     *            类名称
     */
    private static void appendEnumConstructor(StringBuilder code, String className) {
        code.append("    ").append(className).append("(int code, String comment) {\n");
        code.append("        this.code = code;\n");
        code.append("        this.comment = comment;\n");
        code.append("    }\n\n");
    }

    /**
     * 添加枚举类的方法
     * <p>
     * 该方法添加枚举类的方法。
     * <p>
     * 处理流程： 1. 添加枚举类的方法 2. 返回添加后的字符串
     * 
     * @param code
     *            代码
     * @param className
     *            类名称
     */
    private static void appendEnumMethods(StringBuilder code, String className) {
        code.append("    public static ").append(className).append(" fromId(int code) {\n");
        code.append("        return enums.get(code);\n");
        code.append("    }\n\n");
        code.append("    public int getCode() {\n");
        code.append("        return code;\n");
        code.append("    }\n\n");
        code.append("    public String getComment() {\n");
        code.append("        return comment;\n");
        code.append("    }\n");
    }

    /**
     * 添加字段定义
     * <p>
     * 该方法添加字段定义。
     * <p>
     * 处理流程： 1. 添加字段定义 2. 返回添加后的字符串
     * 
     * @param javaCode
     *            代码
     * @param title
     *            标题
     */
    private static void appendCodeField(StringBuilder javaCode, Title title) {
        String propertyName = title.getName();
        String propertyType = title.getType();
        String type = getTypeName(propertyType);

        javaCode.append("\n");
        javaCode.append(formatDescription(title.getDes())).append("\n");
        if (title.getNewCode() != null) {
            // 使用新格式：@DataCell(value = "EnemyFleetNum", isArray = true)
            javaCode.append("    @DataCell(").append(title.getNewCode()).append(")\n");
        } else {
            // 使用简单格式：@DataCell("ItemID")
            javaCode.append("    @DataCell(\"").append(title.getOldName()).append("\")\n");
        }
        javaCode.append("    private ").append(type).append(" ").append(propertyName).append(";\n");
    }

    /**
     * 添加字段的Getter方法
     * <p>
     * 该方法添加字段的Getter方法。
     * <p>
     * 处理流程： 1. 添加字段的Getter方法 2. 返回添加后的字符串
     * 
     * @param javaCode
     *            代码
     * @param title
     *            标题
     */
    private static void appendCodeFieldGetSet(StringBuilder javaCode, Title title) {
        String propertyName = title.getName();
        String propertyType = title.getType();
        String type = getTypeName(propertyType);

        javaCode.append("    public ").append(type).append(" get").append(firstUp(propertyName)).append("() {\n");
        javaCode.append("        return ").append(propertyName).append(";\n");
        javaCode.append("    }\n\n");
    }

    // ================ Excel处理辅助方法 ================
    /**
     * 获取文件扩展名
     * <p>
     * 该方法获取文件扩展名。
     * <p>
     * 处理流程： 1. 获取文件扩展名 2. 返回获取后的字符串
     * 
     * @param fileName
     *            文件名
     * @return 文件扩展名
     */
    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }

    /**
     * 获取单元格的值
     * <p>
     * 该方法获取单元格的值。
     * <p>
     * 处理流程： 1. 获取单元格的值 2. 返回获取后的字符串
     * 
     * @param cell
     *            单元格
     * @return 单元格的值
     */
    private static String getCellValue(Object cell) {
        if (cell == null) {
            return "";
        }

        CellType cellType;

        if (cell instanceof XSSFCell) {
            XSSFCell xssfCell = (XSSFCell)cell;
            cellType = xssfCell.getCellType();
        } else if (cell instanceof HSSFCell) {
            HSSFCell hssfCell = (HSSFCell)cell;
            cellType = hssfCell.getCellType();
        } else {
            return "";
        }

        return convertCellValue(cellType, cell);
    }

    /**
     * 根据单元格类型转换值
     * <p>
     * 该方法根据单元格类型转换值。
     * <p>
     * 处理流程： 1. 根据单元格类型转换值 2. 返回转换后的字符串
     * 
     * @param cellType
     *            单元格类型
     * @param cell
     *            单元格
     * @return 转换后的字符串
     */
    private static String convertCellValue(CellType cellType, Object cell) {
        switch (cellType) {
            case NUMERIC:
                return String.valueOf(getNumericValue(cell));
            case BOOLEAN:
                return String.valueOf(getBooleanValue(cell));
            default:
                return getStringValue(cell);
        }
    }

    /**
     * 获取数值类型的值
     * <p>
     * 该方法获取数值类型的值。
     * <p>
     * 处理流程： 1. 获取数值类型的值 2. 返回获取后的值
     * 
     * @param cell
     *            单元格
     * @return 数值类型的值
     */
    private static double getNumericValue(Object cell) {
        if (cell instanceof XSSFCell)
            return ((XSSFCell)cell).getNumericCellValue();
        if (cell instanceof HSSFCell)
            return ((HSSFCell)cell).getNumericCellValue();
        return 0;
    }

    /**
     * 获取布尔类型的值
     * <p>
     * 该方法获取布尔类型的值。
     * <p>
     * 处理流程： 1. 获取布尔类型的值 2. 返回获取后的值
     * 
     * @param cell
     *            单元格
     * @return 布尔类型的值
     */
    private static boolean getBooleanValue(Object cell) {
        if (cell instanceof XSSFCell)
            return ((XSSFCell)cell).getBooleanCellValue();
        if (cell instanceof HSSFCell)
            return ((HSSFCell)cell).getBooleanCellValue();
        return false;
    }

    /**
     * 获取字符串类型的值
     * <p>
     * 该方法获取字符串类型的值。
     * <p>
     * 处理流程： 1. 获取字符串类型的值 2. 返回获取后的值
     * 
     * @param cell
     *            单元格
     * @return 字符串类型的值
     */
    private static String getStringValue(Object cell) {
        if (cell instanceof XSSFCell)
            return ((XSSFCell)cell).getStringCellValue();
        if (cell instanceof HSSFCell)
            return ((HSSFCell)cell).getStringCellValue();
        return "";
    }

    /**
     * 创建工作簿
     * <p>
     * 该方法创建工作簿。
     * <p>
     * 处理流程： 1. 创建工作簿 2. 返回创建后的工作簿
     * 
     * @param inputStream
     *            输入流
     * @param type
     *            工作簿类型
     * @return 创建后的工作簿
     */
    public static Workbook createWorkbook(InputStream inputStream, WorkbookType type) throws IOException {
        switch (type) {
            case XSSF:
                return new XSSFWorkbook(inputStream);
            case HSSF:
                return new HSSFWorkbook(inputStream);
            default:
                throw new IllegalArgumentException("不支持的工作簿类型: " + type);
        }
    }

    // ================ 字符串处理 ================
    /**
     * 获取模型类名
     * <p>
     * 该方法获取模型类名。
     * <p>
     * 处理流程： 1. 获取模型类名 2. 返回获取后的字符串
     * 
     * @param sheetName
     *            表名
     * @param javaName
     *            类名
     * @return 模型类名
     */
    private static String getJavaName(String sheetName, String javaName) {
        // 如果名称包含Config 则不添加Config
        if (sheetName.toLowerCase().contains("config")) {
            return firstUp(sheetName);
        }
        if (!sheetName.toLowerCase().contains("sheet")) {
            return firstUp(sheetName) + "Config";
        }
        if (javaName.contains("config")) {
            return firstUp(sheetName);
        }
        return javaName + "Config";
    }

    /**
     * 获取枚举类名
     * <p>
     * 该方法获取枚举类名。
     * <p>
     * 处理流程： 1. 获取枚举类名 2. 返回获取后的字符串
     * 
     * @param sheetName
     *            表名
     * @param javaName
     *            类名
     * @return 枚举类名
     */
    private static String getJavaNameConst(String sheetName, String javaName) {
        if (!sheetName.toLowerCase().contains("sheet")) {
            return toCamelCase(sheetName, true) + "Enum";
        }
        return javaName + "Enum";
    }

    /**
     * 转换为驼峰命名
     *
     * @param str
     *            原字符串
     * @param firstUp
     *            首字母是否大写
     * @return 驼峰命名结果
     */
    public static String toCamelCase(String str, boolean firstUp) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        int consecutiveUpperCount = countConsecutiveUpperChars(str);
        boolean preserveInitialUpper = consecutiveUpperCount >= 2;

        return buildCamelCaseString(str, firstUp, consecutiveUpperCount, preserveInitialUpper);
    }

    /**
     * 计算连续大写字符的数量
     * <p>
     * 该方法计算连续大写字符的数量。
     * <p>
     * 处理流程： 1. 计算连续大写字符的数量 2. 返回计算后的值
     * 
     * @param str
     *            字符串
     * @return 连续大写字符的数量
     */
    private static int countConsecutiveUpperChars(String str) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '_' || !Character.isUpperCase(c)) {
                break;
            }
            count++;
        }
        return count;
    }

    /**
     * 构建驼峰命名字符串
     * <p>
     * 该方法构建驼峰命名字符串。
     * <p>
     * 处理流程： 1. 构建驼峰命名字符串 2. 返回构建后的字符串
     * 
     * @param str
     *            字符串
     * @param firstUp
     *            首字母是否大写
     * @param consecutiveUpperCount
     *            连续大写字符的数量
     * @param preserveInitialUpper
     *            是否保留初始大写字符
     * @return 构建后的字符串
     */
    private static String buildCamelCaseString(String str, boolean firstUp, int consecutiveUpperCount,
        boolean preserveInitialUpper) {
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            if (c == '_' || Character.isWhitespace(c)) {
                // 下划线或空格均视为分隔符，下一字母大写，且不追加到结果
                capitalizeNext = true;
            } else {
                // 使用 result.length() 判断是否为“输出中第一个字符”，以便跳过前导空格时仍正确应用 firstUp
                result.append(processCharForCamelCase(c, result.length(), firstUp, capitalizeNext, preserveInitialUpper,
                    consecutiveUpperCount));
                capitalizeNext = false;
            }
        }

        return result.toString();
    }

    /**
     * 处理单个字符用于驼峰命名
     * <p>
     * 该方法处理单个字符用于驼峰命名。
     * <p>
     * 处理流程： 1. 处理单个字符用于驼峰命名 2. 返回处理后的字符
     * 
     * @param c
     *            字符
     * @param index
     *            索引
     * @param firstUp
     *            首字母是否大写
     * @param capitalizeNext
     *            是否大写下一个字符
     * @param preserveInitialUpper
     *            是否保留初始大写字符
     * @param consecutiveUpperCount
     *            连续大写字符的数量
     * @return 处理后的字符
     */
    private static char processCharForCamelCase(char c, int index, boolean firstUp, boolean capitalizeNext,
        boolean preserveInitialUpper, int consecutiveUpperCount) {
        if (capitalizeNext) {
            return Character.toUpperCase(c);
        }

        // 如果是前几个大写字符且需要保留，则不处理大小写
        if (preserveInitialUpper && index < consecutiveUpperCount) {
            return c;
        }

        // 首字母处理
        if (index == 0) {
            return firstUp ? Character.toUpperCase(c) : Character.toLowerCase(c);
        }

        return c;
    }

    /**
     * 首字母大写（ID特殊处理）
     * <p>
     * 该方法首字母大写（ID特殊处理）。
     * <p>
     * 处理流程： 1. 首字母大写（ID特殊处理） 2. 返回处理后的字符串
     * 
     * @param str
     *            字符串
     * @return 处理后的字符串
     */
    public static String firstUp(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        if (str.equals("ID")) {
            return str;
        }
        return toCamelCase(str, true);
    }

    /**
     * 格式化描述文本
     * <p>
     * 该方法格式化描述文本。
     * <p>
     * 处理流程： 1. 格式化描述文本 2. 返回格式化后的字符串
     * 
     * @param des
     *            描述文本
     * @return 格式化后的字符串
     */
    public static String formatDescription(String des) {
        if (des == null || des.trim().isEmpty()) {
            return "    // ";
        }

        String desHead = "    // ";
        int maxLineLength = 120;
        StringBuilder result = new StringBuilder();

        // 按单词分割
        String[] words = des.trim().split("\\s+");
        StringBuilder currentLine = new StringBuilder(desHead);

        for (String word : words) {
            // 检查添加当前单词后是否超过最大长度
            if (currentLine.length() + word.length() + 1 > maxLineLength) {
                // 保存当前行并开始新行
                result.append(currentLine).append("\n");
                currentLine = new StringBuilder(desHead);
            }

            // 添加单词到当前行
            if (currentLine.length() > desHead.length()) {
                currentLine.append(" ").append(word);
            } else {
                currentLine.append(word);
            }
        }

        // 添加最后一行
        if (currentLine.length() > desHead.length()) {
            result.append(currentLine);
        }

        return result.toString();
    }

    /**
     * 规范化类型：只允许 string, int, string[], int[], int[][]
     * <p>
     * 该方法规范化类型：只允许 string, int, string[], int[], int[][]。
     * <p>
     * 处理流程： 1. 规范化类型：只允许 string, int, string[], int[], int[][] 2. 返回规范化后的类型
     * 
     * @param type
     *            类型
     * @return 规范化后的类型
     */
    private static String normalizeType(String type) {
        if (type == null || type.trim().isEmpty()) {
            return "string";
        }

        type = type.trim().toLowerCase();

        // 处理常见类型映射
        if (type.equals("vindex")) {
            return "int";
        }

        // 允许的类型：string, int, string[], int[], int[][]
        if (type.equals("string") || type.equals("int") || type.equals("string[]") || type.equals("int[]")
            || type.equals("int[][]")) {
            return type;
        }

        // 默认返回 string
        return "string";
    }

    /**
     * 获取类型名称（标准化）
     * <p>
     * 该方法获取类型名称（标准化）。
     * <p>
     * 处理流程： 1. 获取类型名称（标准化） 2. 返回获取后的字符串
     * 
     * @param type
     *            类型
     * @return 类型名称（标准化）
     */
    private static String getTypeName(String type) {
        if (type == null) {
            return "String";
        }

        switch (type.toLowerCase()) {
            case "string":
                return "String";
            case "int":
                return "int";
            case "string[]":
                return "String[]";
            case "int[]":
                return "int[]";
            case "int[][]":
                return "int[][]";
            default:
                return type;
        }
    }

    // ================ Manager 类生成 ================
    /**
     * 统一弹框 + 批量生成 Manager + 批量更新配置
     * <p>
     * 该方法统一弹框 + 批量生成 Manager + 批量更新配置。
     * <p>
     * 处理流程： 1. 统一弹框 + 批量生成 Manager + 批量更新配置 2. 返回处理后的结果
     * 
     * @param serverPath
     *            服务器路径
     * @param pendingManagers
     *            待处理的管理器列表
     */
    private static void flushPendingManagers(String serverPath, List<ManagerGenInfo> pendingManagers) {
        if (pendingManagers == null || pendingManagers.isEmpty()) {
            return;
        }
        // 按 ConfigClassName 去重（保留第一个）
        Map<String, ManagerGenInfo> uniqueMap = new LinkedHashMap<>();
        for (ManagerGenInfo info : pendingManagers) {
            uniqueMap.putIfAbsent(info.getSheetName(), info);
        }
        pendingManagers = new ArrayList<>(uniqueMap.values());
        // 只对“配置模型类”做批量（不处理枚举类生成）
        // 1) 先显示 Manager 选择对话框，让用户选择哪些需要生成
        List<ManagerSelectionItem> selectedItems = showManagerSelectionDialog(pendingManagers);
        if (selectedItems == null) {
            return;
        }
        // 检查是否有任何选中项
        boolean hasAnySelection =
            selectedItems.stream().anyMatch(item -> item.isGenerateManager() || item.isAddToConfig());
        if (!hasAnySelection) {
            view.logMessage("未选择任何 Manager 生成或配置，跳过处理");
            return;
        }
        // 检查是否需要添加到 config.xml/BeanManager
        boolean needAddToConfig = selectedItems.stream().anyMatch(ManagerSelectionItem::isAddToConfig);
        // 2) 显示服务器配置选择对话框（只有需要添加配置时才显示）
        ServerConfigSelection serverSelection = null;
        if (needAddToConfig) {
            long configCount = selectedItems.stream().filter(ManagerSelectionItem::isAddToConfig).count();
            serverSelection = showServerConfigDialog(String.valueOf(configCount));
            if (serverSelection == null) {
                return;
            }
        }
        // 3) 批量生成选中的 Manager.java（先写 common）
        List<BeanDef> beanDefs = new ArrayList<>();
        List<BeanManagerDef> beanManagerDefs = new ArrayList<>();
        for (ManagerSelectionItem item : selectedItems) {
            ManagerGenInfo info = item.getInfo();
            String managerClassName = info.getConfigClassName() + "Manager";
            // 生成 Manager 类
            if (item.isGenerateManager()) {
                try {
                    Title idTitle = findIdTitle(info.getTitleList());
                    String managerCode =
                        genManagerModel(managerClassName, info.getConfigClassName(), idTitle, info.getPackagePath());
                    String replace = DATA_PACKAGE_PREFIX.replace(".", "/");
                    String outPath = serverPath + "/common/src/" + replace + info.getPackagePath();
                    doWrite(managerClassName, outPath, managerCode);
                } catch (Exception e) {
                    view.logMessage("生成 Manager 类失败: " + managerClassName + " error: " + e);
                    System.out.println("生成 Manager 类失败: " + managerClassName + " error: " + e);
                }
            }
            // 收集需要添加到配置的项
            if (item.isAddToConfig()) {
                beanDefs.add(new BeanDef(managerClassName, info.getConfigClassName(), info.getSheetName(),
                    info.getPackagePath()));
                beanManagerDefs
                    .add(new BeanManagerDef(managerClassName, info.getConfigClassName(), info.getPackagePath()));
            }
        }
        // 4) 批量更新 gameserver/worldserver 的 config.xml（去重）
        if (serverSelection != null && !beanDefs.isEmpty()) {
            if (serverSelection.isGameServer()) {
                addConfigsToXmlBatch(serverPath + "/gameserver/src/config.xml", beanDefs);
                addMethodsToBeanManagerBatch(serverPath + "/gameserver/src/com/gow/gameserver/util/BeanManager.java",
                    beanManagerDefs);
            }
            if (serverSelection.isWorldServer()) {
                addConfigsToXmlBatch(serverPath + "/worldserver/src/config.xml", beanDefs);
                addMethodsToBeanManagerBatch(serverPath + "/worldserver/src/com/gow/worldserver/util/BeanManager.java",
                    beanManagerDefs);
            }
        }
    }

    /**
     * 单条 bean 定义（用于 config.xml）
     * <p>
     * 该方法单条 bean 定义（用于 config.xml）。
     * <p>
     * 处理流程： 1. 单条 bean 定义（用于 config.xml） 2. 返回定义后的 bean
     */
    private static class BeanDef {
        private final String managerClassName;// 管理器类名
        private final String configClassName;// 配置类名
        private final String sheetName;// 表名
        private final String packagePath;// 包路径

        private BeanDef(String managerClassName, String configClassName, String sheetName, String packagePath) {
            this.managerClassName = managerClassName;
            this.configClassName = configClassName;
            this.sheetName = sheetName;
            this.packagePath = packagePath;
        }
    }

    /**
     * 单条 BeanManager 方法定义
     * <p>
     * 该方法单条 BeanManager 方法定义。
     * <p>
     * 处理流程： 1. 单条 BeanManager 方法定义 2. 返回定义后的 BeanManager
     */
    private static class BeanManagerDef {
        private final String managerClassName;// 管理器类名
        private final String configClassName;// 配置类名
        private final String packagePath;// 包路径

        private BeanManagerDef(String managerClassName, String configClassName, String packagePath) {
            this.managerClassName = managerClassName;
            this.configClassName = configClassName;
            this.packagePath = packagePath;
        }
    }

    /**
     * 查找用于 ID 的属性
     * <p>
     * 该方法查找用于 ID 的属性。
     * <p>
     * 处理流程： 1. 查找用于 ID 的属性 2. 返回查找后的属性
     * 
     * @param titleList
     *            标题列表
     * @return 查找后的属性
     */
    private static Title findIdTitle(List<Title> titleList) {
        // 首先查找原始类型是 vindex 的字段
        for (Title title : titleList) {
            if (title.isOriginalVindex()) {
                return title;
            }
        }
        // 如果没有 vindex，查找第一个 int 类型的字段，且名称包含 id 或 Id
        for (Title title : titleList) {
            if ("int".equals(title.getType())) {
                String name = title.getName().toLowerCase();
                if (name.contains("id")) {
                    return title;
                }
            }
        }
        // 如果还没有，返回第一个 int 类型字段
        for (Title title : titleList) {
            if ("int".equals(title.getType())) {
                return title;
            }
        }
        // 如果都没有，返回第一个字段
        return titleList.isEmpty() ? null : titleList.get(0);
    }

    /**
     * 生成 Manager 类代码
     * <p>
     * 该方法生成 Manager 类代码。
     * <p>
     * 处理流程： 1. 生成 Manager 类代码 2. 返回生成后的代码
     * 
     * @param managerClassName
     *            管理器类名
     * @param configClassName
     *            配置类名
     * @param idTitle
     *            用于 ID 的属性
     * @param packagePath
     *            包路径
     * @return 生成后的代码
     */
    private static String genManagerModel(String managerClassName, String configClassName, Title idTitle,
        String packagePath) {
        StringBuilder sb = new StringBuilder();

        // 包声明
        String packageName = DATA_PACKAGE_PREFIX + packagePath;
        sb.append("package ").append(packageName).append(";\n\n");

        // 导入
        sb.append("import com.gow.common.config.BaseGenericDataManager;\n\n");

        // 类注释
        sb.append("/**\n");
        sb.append(" * ").append(configClassName).append("配置数据管理器\n");
        sb.append(" * \n");
        sb.append(" * @author ").append(getAuthor()).append("\n");
        sb.append(" */\n");

        // 类声明
        sb.append("public class ").append(managerClassName).append(" extends BaseGenericDataManager<")
            .append(configClassName).append("> {\n");

        // getId 方法
        sb.append("    @Override\n");
        sb.append("    protected int getId(").append(configClassName).append(" data) {\n");
        if (idTitle != null) {
            String getterMethod = "get" + firstUp(idTitle.getName()) + "()";
            sb.append("        return data.").append(getterMethod).append(";\n");
        } else {
            sb.append("        return 0;\n");
        }
        sb.append("    }\n\n");

        sb.append("}\n");

        return sb.toString();
    }

    /**
     * 显示 Manager 选择对话框（多选框）
     * <p>
     * 该方法显示 Manager 选择对话框（多选框）。
     * <p>
     * 处理流程： 1. 显示 Manager 选择对话框（多选框） 2. 返回用户选择的 Manager 列表，如果取消返回 null
     * 
     * @param pendingManagers
     *            待处理的 Manager 列表
     * @return 用户选择的 Manager 列表，如果取消返回 null
     */
    private static List<ManagerSelectionItem> showManagerSelectionDialog(List<ManagerGenInfo> pendingManagers) {
        try {
            List<ManagerSelectionItem> items = new ArrayList<>();
            for (ManagerGenInfo info : pendingManagers) {
                items.add(new ManagerSelectionItem(info));
            }

            // 创建复选框数组
            JCheckBox[] managerCheckBoxes = new JCheckBox[items.size()];
            JCheckBox[] configCheckBoxes = new JCheckBox[items.size()];

            for (int i = 0; i < items.size(); i++) {
                managerCheckBoxes[i] = new JCheckBox();
                managerCheckBoxes[i].setSelected(true);
                configCheckBoxes[i] = new JCheckBox();
                configCheckBoxes[i].setSelected(true);
            }

            // 创建面板
            JPanel mainPanel = new JPanel(new java.awt.BorderLayout());

            // 创建表格面板
            JPanel tablePanel = new JPanel(new java.awt.GridBagLayout());
            java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
            gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
            gbc.insets = new java.awt.Insets(2, 5, 2, 5);

            // 添加表头
            gbc.gridy = 0;
            gbc.gridx = 0;
            tablePanel.add(new JLabel("配置类名"), gbc);
            gbc.gridx = 1;
            tablePanel.add(new JLabel("Sheet名"), gbc);
            gbc.gridx = 2;
            tablePanel.add(new JLabel("生成Manager"), gbc);
            gbc.gridx = 3;
            tablePanel.add(new JLabel("添加到config"), gbc);

            // 添加数据行
            for (int i = 0; i < items.size(); i++) {
                gbc.gridy = i + 1;
                gbc.gridx = 0;
                tablePanel.add(new JLabel(items.get(i).getInfo().getConfigClassName()), gbc);
                gbc.gridx = 1;
                tablePanel.add(new JLabel(items.get(i).getInfo().getSheetName()), gbc);
                gbc.gridx = 2;
                tablePanel.add(managerCheckBoxes[i], gbc);
                gbc.gridx = 3;
                tablePanel.add(configCheckBoxes[i], gbc);
            }

            // 添加全选/取消全选按钮
            JPanel buttonPanel = getButtonPanel(managerCheckBoxes, configCheckBoxes);

            // 使用滚动面板包装表格
            JScrollPane scrollPane = new JScrollPane(tablePanel);
            scrollPane.setPreferredSize(new java.awt.Dimension(600, Math.min(400, 30 + items.size() * 30)));

            mainPanel.add(buttonPanel, java.awt.BorderLayout.NORTH);
            mainPanel.add(scrollPane, java.awt.BorderLayout.CENTER);

            String title = "选择要处理的 Manager（共 " + items.size() + " 个）";
            int result = JOptionPane.showConfirmDialog(null, mainPanel, title, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                // 更新选择状态
                for (int i = 0; i < items.size(); i++) {
                    items.get(i).setGenerateManager(managerCheckBoxes[i].isSelected());
                    items.get(i).setAddToConfig(configCheckBoxes[i].isSelected());
                }
                return items;
            }
            return null;
        } catch (Exception e) {
            view.logMessage("无法显示 Manager 选择对话框: " + e);
            System.out.println("无法显示 Manager 选择对话框: " + e);
            return null;
        }
    }

    /**
     * 获取按钮面板
     * <p>
     * 该方法获取按钮面板。
     * <p>
     * 处理流程： 1. 获取按钮面板 2. 返回获取后的按钮面板
     * 
     * @param managerCheckBoxes
     *            Manager 选择框数组
     * @param configCheckBoxes
     *            Config 选择框数组
     * @return 按钮面板
     */
    private static JPanel getButtonPanel(JCheckBox[] managerCheckBoxes, JCheckBox[] configCheckBoxes) {
        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        JButton selectAllManagerBtn = new JButton("全选Manager");
        JButton deselectAllManagerBtn = new JButton("取消全选Manager");
        JButton selectAllConfigBtn = new JButton("全选Config");
        JButton deselectAllConfigBtn = new JButton("取消全选Config");

        selectAllManagerBtn.addActionListener(e -> {
            for (JCheckBox cb : managerCheckBoxes) {
                cb.setSelected(true);
            }
        });
        deselectAllManagerBtn.addActionListener(e -> {
            for (JCheckBox cb : managerCheckBoxes) {
                cb.setSelected(false);
            }
        });
        selectAllConfigBtn.addActionListener(e -> {
            for (JCheckBox cb : configCheckBoxes) {
                cb.setSelected(true);
            }
        });
        deselectAllConfigBtn.addActionListener(e -> {
            for (JCheckBox cb : configCheckBoxes) {
                cb.setSelected(false);
            }
        });

        buttonPanel.add(selectAllManagerBtn);
        buttonPanel.add(deselectAllManagerBtn);
        buttonPanel.add(selectAllConfigBtn);
        buttonPanel.add(deselectAllConfigBtn);
        return buttonPanel;
    }

    /**
     * 显示服务器配置选择对话框
     * <p>
     * 该方法显示服务器配置选择对话框。
     * <p>
     * 处理流程： 1. 显示服务器配置选择对话框 2. 返回用户选择的服务器配置，如果取消返回 null
     * 
     * @param sheetCountText
     *            表名数量文本
     * @return 用户选择的服务器配置，如果取消返回 null
     */
    private static ServerConfigSelection showServerConfigDialog(String sheetCountText) {
        try {
            JPanel panel = new JPanel();
            panel.setLayout(new java.awt.GridLayout(2, 1));

            JCheckBox gameServerCheck = new JCheckBox("生成 gameserver 配置", false);
            JCheckBox worldServerCheck = new JCheckBox("生成 worldserver 配置", false);

            panel.add(gameServerCheck);
            panel.add(worldServerCheck);
            gameServerCheck.setSelected(true);
            int result = JOptionPane.showConfirmDialog(null, panel,
                "为 " + sheetCountText + " 个Sheet批量生成配置（Manager/Bean/config.xml）", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                return new ServerConfigSelection(gameServerCheck.isSelected(), worldServerCheck.isSelected());
            }
            return null;
        } catch (Exception e) {
            // 如果无法显示对话框（例如在非 GUI 环境），返回 null
            view.logMessage("无法显示对话框，跳过服务器配置生成: " + e);
            System.out.println("无法显示对话框，跳过服务器配置生成: " + e);
            return null;
        }
    }

    /**
     * 批量在 config.xml 中添加 bean 配置（去重，一次读写）
     * <p>
     * 该方法批量在 config.xml 中添加 bean 配置（去重，一次读写）。
     * <p>
     * 处理流程： 1. 批量在 config.xml 中添加 bean 配置（去重，一次读写） 2. 返回处理后的结果
     * 
     * @param xmlPath
     *            配置文件路径
     * @param defs
     *            定义列表
     */
    private static void addConfigsToXmlBatch(String xmlPath, List<BeanDef> defs) {
        try {
            Path path = Paths.get(xmlPath);
            if (!Files.exists(path)) {
                view.logMessage("配置文件不存在: " + xmlPath);
                return;
            }
            String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            // 查找最后一个 </bean> 标签的位置
            int lastBeanIndex = content.lastIndexOf("</bean>");
            if (lastBeanIndex == -1) {
                view.logMessage("未找到 </bean> 标签: " + xmlPath);
                return;
            }
            StringBuilder toAppend = new StringBuilder();
            for (BeanDef def : defs) {
                String beanId = toCamelCase(def.managerClassName, false);
                String fullClassName = DATA_PACKAGE_PREFIX + def.packagePath + "." + def.managerClassName;

                // 去重：id 或 class 任意存在就跳过
                if (content.contains("id=\"" + beanId + "\"") || content.contains("class=\"" + fullClassName + "\"")) {
                    continue;
                }
                toAppend.append("\n");
                toAppend.append("    <!-- ").append(def.configClassName).append(" -->\n");
                toAppend.append("    <bean id=\"").append(beanId).append("\" class=\"").append(fullClassName)
                    .append("\" parent=\"dataManager\">\n");
                toAppend.append("        <property name=\"fileName\" value=\"").append(def.sheetName).append("\"/>\n");
                toAppend.append("    </bean>");
            }

            if (toAppend.length() == 0) {
                return;
            }
            // 在最后一个 </bean> 后插入新增配置块
            String newContent = content.substring(0, lastBeanIndex + "</bean>".length()) + toAppend
                + content.substring(lastBeanIndex + "</bean>".length());
            // 写入文件
            Files.write(path, newContent.getBytes(StandardCharsets.UTF_8));
            view.logMessage("已添加 bean 配置到: " + xmlPath);
            System.out.println("已添加 bean 配置到: " + xmlPath);
        } catch (Exception e) {
            view.logMessage("添加 bean 配置失败: " + xmlPath + " error: " + e);
            System.out.println("添加 bean 配置失败: " + xmlPath + " error: " + e);
        }
    }

    /**
     * 批量在 BeanManager.java 中添加 import 与获取方法（去重，一次读写）
     * <p>
     * 该方法批量在 BeanManager.java 中添加 import 与获取方法（去重，一次读写）。
     * <p>
     * 处理流程： 1. 批量在 BeanManager.java 中添加 import 与获取方法（去重，一次读写） 2. 返回处理后的结果
     * 
     * @param beanManagerPath
     *            BeanManager 文件路径
     * @param defs
     *            定义列表
     */
    private static void addMethodsToBeanManagerBatch(String beanManagerPath, List<BeanManagerDef> defs) {
        try {
            Path path = Paths.get(beanManagerPath);
            if (!Files.exists(path)) {
                view.logMessage("BeanManager 文件不存在: " + beanManagerPath);
                System.out.println("BeanManager 文件不存在: " + beanManagerPath);
                return;
            }

            String newContent = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            StringBuilder methodAppend = new StringBuilder();

            for (BeanManagerDef def : defs) {
                String methodName = "get" + def.managerClassName;

                // 去重：方法已存在则跳过
                if (newContent.contains(methodName + "()")) {
                    continue;
                }

                String methodComment = "    /**\n" + "     * 获取" + def.configClassName + "配置管理器\n" + "     */\n";
                String methodCode = methodComment + "    public static " + def.managerClassName + " " + methodName
                    + "() {\n" + "        return BeanManager.getComponent(\"" + toCamelCase(def.managerClassName, false)
                    + "\");\n" + "    }\n\n";
                methodAppend.append(methodCode);
            }

            // ==================== 处理 com.gow.common.config.* import 按字母排序 ====================
            // 1. 按行拆分，定位现有 bean import 组
            String[] lines = newContent.split("\n", -1);
            int beanStart = -1;
            int beanEnd = -1;
            List<String> existingBeanImports = new ArrayList<>();
            for (int i = 0; i < lines.length; i++) {
                String trim = lines[i].trim();
                if (trim.startsWith("import com.gow.common.config.")) {
                    if (beanStart == -1) {
                        beanStart = i;
                    }
                    beanEnd = i;
                    existingBeanImports.add(trim);
                }
            }

            // 2. 组合“现有 import + 需要新增的 import”，做去重 + 排序
            Set<String> beanImportSet = new HashSet<>(existingBeanImports);
            for (BeanManagerDef def : defs) {
                String fullClassName = DATA_PACKAGE_PREFIX + def.packagePath + "." + def.managerClassName;
                String importLine = "import " + fullClassName + ";";
                beanImportSet.add(importLine);
            }

            // 如果没有新增方法，也没有新增 import，则跳过文件写入
            boolean hasNewImport = beanImportSet.size() > existingBeanImports.size();
            if (methodAppend.length() == 0 && !hasNewImport) {
                return;
            }

            List<String> beanImportList = new ArrayList<>(beanImportSet);
            beanImportList.sort(String::compareTo);

            // 如果最终没有任何 bean import，则后面就只追加方法
            boolean hasBeanImports = !beanImportList.isEmpty();

            // 3. 重写 bean import 组（如果不存在，则新建一个组插入到 import 区域末尾）
            StringBuilder rebuilt = new StringBuilder();
            if (hasBeanImports) {
                if (beanStart == -1) {
                    // 没有 com.gow.common.config.* 组：在最后一个 import 后面插入新组
                    int lastImportLine = -1;
                    for (int i = 0; i < lines.length; i++) {
                        if (lines[i].trim().startsWith("import ")) {
                            lastImportLine = i;
                        }
                    }

                    for (int i = 0; i < lines.length; i++) {
                        rebuilt.append(lines[i]).append("\n");
                        if (i == lastImportLine) {
                            // 紧跟在最后一个 import 后面插入 bean import 组
                            for (String imp : beanImportList) {
                                rebuilt.append(imp).append("\n");
                            }
                        }
                    }
                } else {
                    // 已存在 bean import 组：替换原 beanStart..beanEnd 这一段
                    for (int i = 0; i < lines.length; i++) {
                        if (i == beanStart) {
                            // 写入排序后的 bean import 组
                            for (String imp : beanImportList) {
                                rebuilt.append(imp).append("\n");
                            }
                            // 跳过原有 bean import 区块
                            i = beanEnd;
                        } else {
                            rebuilt.append(lines[i]).append("\n");
                        }
                    }
                }

                newContent = rebuilt.toString();
            }

            // 插入方法：放到类最后一个 } 前
            if (methodAppend.length() > 0) {
                int lastBraceIndex = newContent.lastIndexOf("}");
                if (lastBraceIndex == -1) {
                    view.logMessage("未找到类结束位置: " + beanManagerPath);
                    return;
                }
                newContent =
                    newContent.substring(0, lastBraceIndex) + methodAppend + newContent.substring(lastBraceIndex);
            }

            Files.write(path, newContent.getBytes(StandardCharsets.UTF_8));
            view.logMessage("已批量添加方法到 BeanManager: " + beanManagerPath);
            System.out.println("已批量添加方法到 BeanManager: " + beanManagerPath);
        } catch (Exception e) {
            view.logMessage("添加方法到 BeanManager 失败: " + beanManagerPath + " error: " + e);
            System.out.println("添加方法到 BeanManager 失败: " + beanManagerPath + " error: " + e);
        }
    }

    // ================ 作者信息 ================
    /**
     * 获取代码生成的作者信息。
     * <p>
     * 优先级： 1) JVM 参数覆盖：-Dgen.author=xxx 2) 当前登录用户名：Windows 环境变量 USERNAME；再兜底 user.name 3) 机器名兜底：Windows 环境变量
     * COMPUTERNAME
     */
    private static String getAuthor() {
        // 1) 允许外部覆盖（例如 CI 或共享机器）
        String author = System.getProperty("gen.author");
        if (author != null) {
            author = author.trim();
            if (!author.isEmpty()) {
                return author;
            }
        }

        // 2) 优先 Windows 用户名
        author = System.getenv("USERNAME");
        if (author != null) {
            author = author.trim();
            if (!author.isEmpty()) {
                return author;
            }
        }

        // 3) 通用兜底：JVM 用户名
        author = System.getProperty("user.name");
        if (author != null) {
            author = author.trim();
            if (!author.isEmpty()) {
                return author;
            }
        }

        // 4) 最后兜底：机器名
        String machine = System.getenv("COMPUTERNAME");
        if (machine != null) {
            machine = machine.trim();
            if (!machine.isEmpty()) {
                return machine;
            }
        }

        return "unknown";
    }
}