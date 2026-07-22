package com.gamer.data.file;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 「目录执行 cmd」模态对话框：路径列表与左侧目录树独立，使用 {@link Const#CMD_PATHS}。
 */
final class ExeCmdDialog {

    public static final String SVN_UPDATE = "svn update";// 更新
    public static final String ANT = "ant";// 打包
    public static final String CALL_PROTOC_JAVA_BAT = "call protoc_java.bat";// 生成协议
    public static final String CALL_OPCODE_JAVA_BAT = "call opcode_java.bat";// 生成协议 id
    public static final String JAVA_JAR_GEN_TOOLS_JAR = "java -jar GenTools.jar";// 执行生成配置代码工具
    public static final String SVN_UPDATE_R = "svn update -r";// 更新指定版本

    private static final String[] presetCommands = {// 预设命令
        SVN_UPDATE, // 更新
        ANT, // 打包
        CALL_PROTOC_JAVA_BAT, // 生成协议
        CALL_OPCODE_JAVA_BAT, // 生成协议 id
        JAVA_JAR_GEN_TOOLS_JAR, // 执行生成配置代码工具
        SVN_UPDATE_R// 更新指定版本
    };// 预设命令

    public static final String BIN = "D:\\code\\WorkSpace\\Common\\Tools\\Bin";// 公共工具根目录
    public static final String SERVER = "D:\\code\\WorkSpace\\Common\\Tools\\Bin\\server";// 服务器代码目录
    public static final String DOCUMENT = "D:\\code\\WorkSpace\\Document";// 文档目录
    public static final String COMMON = "D:\\code\\WorkSpace\\Server\\common";// 公共代码目录
    public static final String COMMON_PROTO = "D:\\code\\WorkSpace\\Server\\common-proto";// 公共协议目录

    private static JComboBox<String> pathComboBox = new JComboBox<>();// 路径下拉框
    private static JComboBox<String> commandComboBox = new JComboBox<>(presetCommands);// 命令下拉框
    private static JButton executeButton = new JButton("执行");// 执行按钮
    private static JTextArea resultArea = new JTextArea();// 结果区域

    /**
     * 构造函数
     */
    private ExeCmdDialog() {}

    private static void init() {
        pathComboBox = new JComboBox<>();// 路径下拉框
        commandComboBox = new JComboBox<>(presetCommands);// 命令下拉框
        executeButton = new JButton("执行");// 执行按钮
        resultArea = new JTextArea();// 结果区域
    }

    /**
     * 显示对话框（阻塞至用户关闭）。
     *
     * @param owner
     *            父窗口，仅用于模态与文件选择器挂载
     */
    static void show(final FileMain owner) {
        init();
        final JDialog dialog = new JDialog(owner, "目录执行 cmd", true);
        dialog.setLayout(new BorderLayout(8, 8));
        dialog.setSize(760, 520);
        dialog.setLocationRelativeTo(owner);// 设置对话框位置

        pathComboBox.setEditable(true);// 设置路径下拉框可编辑
        refillCmdPathCombo(null);// 填充路径下拉框
        buildCommandComboBox();// 构建命令下拉框

        // 构建顶部面板
        JPanel north = buildNorthPanel(dialog);

        buildResultTextArea();// 构建结果区域
        JScrollPane scroll = new JScrollPane(resultArea);// 构建结果区域滚动面板
        scroll.setBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), "执行结果"));// 构建结果区域边框

        JPanel south = buildSouthPanel(dialog);// 构建底部面板

        wireExecute(dialog);// 绑定「执行」：校验路径与命令后调用异步执行器；执行过的路径可记入 CMD 列表。

        dialog.add(north, BorderLayout.NORTH);// 添加顶部面板
        dialog.add(scroll, BorderLayout.CENTER);// 添加结果区域
        dialog.add(south, BorderLayout.SOUTH);// 添加底部面板
        dialog.setVisible(true);// 显示对话框
    }

    /**
     * 执行命令
     * 
     * @param path
     *            执行路径
     * @param cmd
     *            执行命令
     */
    static void executeCmd(String path, String cmd) {
        if (path == null || cmd == null) {
            return;
        }
        // 根据命令定位 pathComboBox 中的位置
        for (int i = 0; i < pathComboBox.getItemCount(); i++) {
            String item = pathComboBox.getItemAt(i);
            if (item != null && item.equals(path)) {
                pathComboBox.setSelectedIndex(i);// 设置路径下拉框选中索引
                break;
            }
        }

        for (int i = 0; i < commandComboBox.getItemCount(); i++) {
            String item = commandComboBox.getItemAt(i);
            if (item != null && item.equals(cmd)) {
                commandComboBox.setSelectedIndex(i);// 设置命令下拉框选中索引
                break;
            }
        }

        executeButton.doClick();// 点击执行按钮

    }

    /**
     * 构建只读结果区，使用支持中文的字体避免显示异常。
     */
    private static void buildResultTextArea() {
        resultArea.setEditable(false);
        resultArea.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
    }

    /**
     * 构建 cmd 命令下拉框：支持预选项与自定义输入。
     */
    private static void buildCommandComboBox() {
        commandComboBox.setEditable(true);
        commandComboBox.setSelectedIndex(0);
    }

    /**
     * 构建路径、命令输入与「选择目录」按钮所在面板。
     */
    private static JPanel buildNorthPanel(final JDialog dialog) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(new JLabel("执行路径(CMD)"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(pathComboBox, gbc);

        JButton chooseDir = new JButton("选择目录");
        chooseDir.addActionListener(e -> onChooseCmdDirectory(dialog));
        gbc.gridx = 2;
        gbc.weightx = 0;
        panel.add(chooseDir, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        panel.add(new JLabel("cmd 命令"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        panel.add(commandComboBox, gbc);

        // 快捷操作按钮面板
        addQuickButton(gbc, panel);
        return panel;
    }

    /**
     * 添加快捷操作按钮 到面板
     * 
     * @param gbc
     *            网格布局约束
     * @param panel
     *            面板
     */
    private static void addQuickButton(GridBagConstraints gbc, JPanel panel) {
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(createButton("执行GenTools", SERVER, JAVA_JAR_GEN_TOOLS_JAR));
        buttonPanel.add(createButton("更新bin目录", BIN, SVN_UPDATE));
        buttonPanel.add(createButton("更新GD和文档", DOCUMENT, SVN_UPDATE));
        buttonPanel.add(createButton("生成opcode", BIN, CALL_OPCODE_JAVA_BAT));
        buttonPanel.add(createButton("生成协议", BIN, CALL_PROTOC_JAVA_BAT));
        buttonPanel.add(createButton("打包协议", COMMON_PROTO, ANT));
        buttonPanel.add(createButton("打包common", COMMON, ANT));
        panel.add(buttonPanel, gbc);
    }

    /**
     * 添加按钮
     * 
     * @param text
     *            按钮文本
     * @param path
     *            执行路径
     * @param cmd
     *            执行命令
     */
    private static JButton createButton(String text, String path, String cmd) {
        JButton button = new JButton(text);
        button.addActionListener(e -> executeCmd(path, cmd));
        return button;
    }

    /**
     * 选择目录后加入 CMD 路径列表并持久化（不加入左侧目录树）。
     */
    private static void onChooseCmdDirectory(JDialog dialog) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setCurrentDirectory(new File((String)Objects.requireNonNull(pathComboBox.getSelectedItem())));
        int result = chooser.showOpenDialog(dialog);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File selectedDir = chooser.getSelectedFile();
        if (selectedDir == null) {
            return;
        }
        String absPath = selectedDir.getAbsolutePath();
        Const.ensureCmdPathListed(absPath);
        refillCmdPathCombo(absPath);
    }

    /**
     * 构建底部「执行」「关闭」按钮条。
     */
    private static JPanel buildSouthPanel(final JDialog dialog) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(e -> dialog.dispose());
        panel.add(executeButton);
        panel.add(closeButton);
        return panel;
    }

    /**
     * 绑定「执行」：校验路径与命令后调用异步执行器；执行过的路径可记入 CMD 列表。
     */
    private static void wireExecute(final JDialog dialog) {
        executeButton.addActionListener(e -> {
            Object selected = pathComboBox.getEditor().getItem();
            String path = selected == null ? "" : selected.toString().trim();
            Object cmdSelected = commandComboBox.getEditor().getItem();
            String command = cmdSelected == null ? "" : cmdSelected.toString().trim();
            if (path.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "请选择执行目录", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (command.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "请输入 cmd 命令", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }
            File workDir = new File(path);
            if (!workDir.exists() || !workDir.isDirectory()) {
                JOptionPane.showMessageDialog(dialog, "目录不存在或不可用: " + path, "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Const.ensureCmdPathListed(workDir.getAbsolutePath());
            refillCmdPathCombo(workDir.getAbsolutePath());
            executeButton.setEnabled(false);// 禁用执行按钮
            runAsync(workDir, command);// 异步执行命令
        });
    }

    /**
     * 用 {@link Const#CMD_PATHS} 填充下拉框并可选中指定路径。
     */
    private static void refillCmdPathCombo(String selectedPath) {
        pathComboBox.removeAllItems();
        for (String p : Const.CMD_PATHS) {
            pathComboBox.addItem(p);
        }
        if (selectedPath != null && !selectedPath.trim().isEmpty()) {
            pathComboBox.setSelectedItem(selectedPath);
        }
    }

    /**
     * 异步执行命令：禁用执行按钮，清空输出区，在后台线程读流并在 EDT 追加文本。
     *
     * @param workDir
     *            工作目录
     * @param command
     *            传给 cmd /c 的完整命令行
     */
    public static void runAsync(File workDir, String command) {
        appendRunHeader(workDir, command);// 写入本次执行的目录与命令说明
        Thread worker = new Thread(() -> runInBackground(workDir, command), "directory-cmd-runner");
        worker.start();// 启动线程
    }

    /**
     * 写入本次执行的目录与命令说明。
     */
    private static void appendRunHeader(File workDir, String command) {
        resultArea.append("目录: " + workDir.getAbsolutePath() + "\n");// 写入目录
        resultArea.append("命令: " + command + "\n");// 写入命令
        resultArea.append("------------------------------------------------------------\n");
    }

    /**
     * 在后台线程中启动进程、按系统控制台编码读取输出并回写 UI。
     */
    private static void runInBackground(File workDir, String command) {
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", command);
            pb.directory(workDir);// 设置工作目录
            pb.redirectErrorStream(true);// 重定向错误流
            Process process = pb.start();// 启动进程

            Charset charset = resolveCmdPipeCharset();// 获取命令行编码
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), charset));// 创建缓冲区读取器

            String line;
            while ((line = reader.readLine()) != null) {
                appendLineOnEdt(line);
            }

            final int exitCode = process.waitFor();// 等待进程结束
            appendFooterOnEdt(exitCode);// 写入结束信息与退出码
        } catch (final Exception ex) {
            appendErrorOnEdt(ex);// 写入异常说明
        } finally {
            enableButtonOnEdt(executeButton);// 恢复执行按钮可用
        }
    }

    /**
     * 在事件线程中追加一行输出。
     */
    private static void appendLineOnEdt(final String line) {
        SwingUtilities.invokeLater(() -> {
            resultArea.append(line);
            resultArea.append("\n");
        });
    }

    /**
     * 在事件线程中追加结束信息与退出码。
     */
    private static void appendFooterOnEdt(final int exitCode) {
        SwingUtilities.invokeLater(() -> {
            resultArea.append("------------------------------------------------------------\n");
            resultArea.append("执行完成，退出码: " + exitCode + "\n");
        });
    }

    /**
     * 在事件线程中追加异常说明。
     */
    private static void appendErrorOnEdt(final Exception ex) {
        resultArea.append("执行失败: " + ex.toString() + "\n");
    }

    /**
     * 在事件线程中恢复执行按钮。
     */
    private static void enableButtonOnEdt(final JButton executeButton) {
        executeButton.setEnabled(true);
    }

    /**
     * Windows 下 cmd 管道输出多为系统 ANSI/OEM 代码页，中文环境通常与 GBK 一致；非 Windows 使用 UTF-8。
     */
    private static Charset resolveCmdPipeCharset() {
        String os = System.getProperty("os.name", "");
        if (!os.toUpperCase().startsWith("WINDOWS")) {
            return StandardCharsets.UTF_8;
        }
        try {
            return Charset.forName("GBK");
        } catch (Exception e) {
            return StandardCharsets.UTF_8;
        }
    }
}
