package swing.frame;


import swing.util.PortUtil;
import swing.util.TelnetUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

public class SmallTool {
	private JFrame frame;

	private JPanel jPanelInput;
	private JLabel jLabel;
	private JTextField ip;
	private JTextField port;
	private JButton jButton;

	private JPanel jPanelOutput;
	private JTextArea jTextArea;

	public SmallTool() {

	}

	public void init() {
		frame = new JFrame("Small Tool");    //创建Frame窗口

		addJMenuBar();

		JPanel jPanel = new JPanel();
		jPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

		frame.setContentPane(jPanel);

		jPanelInput = new JPanel();
		jPanelInput.setLayout(new FlowLayout());
		jPanel.add(jPanelInput, BorderLayout.NORTH);

		jLabel = new JLabel();
		jLabel.setFont(new Font("", Font.BOLD, 50));
		jLabel.setText("welcome to use tool");
		jPanelInput.add(jLabel);

		jPanelOutput = new JPanel();
		jPanel.add(jPanelOutput, BorderLayout.CENTER);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setFrameSize(frame);

		//永远在最上层
		frame.setAlwaysOnTop(true);
		frame.setVisible(true);
	}

	/**
	 * 设置窗口大小
	 *
	 * @param frame 窗口
	 */
	private void setFrameSize(JFrame frame) {
		// 设置宽高
		frame.setSize(800, 400);
		// 取得屏幕宽度
		double width = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		// 取得屏幕高度
		double height = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		// 设置窗体居中显示
		frame.setLocation((int) (width - frame.getWidth()) / 2,
				(int) (height - frame.getHeight()) / 2);
	}

	/**
	 * 添加菜单条
	 */
	private void addJMenuBar() {
		JMenuBar jMenuBar = new JMenuBar();
		JMenu jMenuPortTool = new JMenu("端口工具");

		addMenuItem(jMenuPortTool, "ping");
		addMenuItem(jMenuPortTool, "telnet");
		addMenuItem(jMenuPortTool, "http");
		addMenuItem(jMenuPortTool, "websocket");

		jMenuBar.add(jMenuPortTool);

		jMenuPortTool = new JMenu("娱乐");
		addMenuItem(jMenuPortTool, "chineseChess");

		jMenuBar.add(jMenuPortTool);


		jMenuPortTool = new JMenu("表格");
		addMenuItem(jMenuPortTool, "readExcel");
		addMenuItem(jMenuPortTool, "writeExcel");

		jMenuBar.add(jMenuPortTool);

		jMenuPortTool = new JMenu("数据库");
		addMenuItem(jMenuPortTool, "mysql");
		addMenuItem(jMenuPortTool, "redis");

		jMenuBar.add(jMenuPortTool);

		frame.setJMenuBar(jMenuBar);
	}

	/**
	 * 添加子菜单
	 *
	 * @param jMenuPortTool 父菜单
	 * @param name          子菜单名称
	 */
	private void addMenuItem(JMenu jMenuPortTool, String name) {
		JMenuItem jMenuItem = new JMenuItem(name);
		jMenuPortTool.add(jMenuItem);
		jMenuItem.addActionListener(e -> {
			String command = e.getActionCommand();
			switch (command) {
				case "ping":
					addPingComponents();
					break;
				case "telnet":
					addTelnetComponents();
					break;
				case "http":
					addHttpComponents();
					break;
				case "websocket":
					addWebsocketComponents();
				case "chineseChess":
					addChineseChessComponents();
					break;
				case "readExcel":
					addReadExcelComponents();
					break;
				case "writeExcel":
					addWriteExcelComponents();
					break;
				case "mysql":
					addMySqlComponents();
					break;
				case "redis":
					addRedisComponents();
					break;
				default:
					break;
			}
		});
	}

	/**
	 * 删除两个面板
	 */
	private void removeInputOutput() {
		//重要  删除所有组件 重新 update
		jPanelInput.removeAll();
		jPanelOutput.removeAll();
	}

	/**
	 * 添加 ping 组件
	 */
	private void addPingComponents() {
		removeInputOutput();

		//网格布局
		jPanelInput.setLayout(new GridLayout(1, 3));

		jLabel = new JLabel(" input  ping IP: ");
		jPanelInput.add(jLabel);

		ip = new JTextField();
		//文本框中的文本使用右对齐
		ip.setHorizontalAlignment(SwingConstants.RIGHT);
		jPanelInput.add(ip);

		jTextArea = new JTextArea();
		jPanelOutput.add(jTextArea);

		jButton = new JButton(" start ping ");
		jButton.addActionListener(e -> {
			String value = ip.getText();
			ip.setText("");
			jTextArea.setText("");
			jTextArea.append("ping :" + value + " result");
			jTextArea.append("\r\n");
			List<String> results = PortUtil.pingCmd(value);
			for (String result : results) {
				jTextArea.append(result);
				jTextArea.append("\r\n");
			}
		});
		jPanelInput.add(jButton);


		//添加或删除组件后,更新窗口
		SwingUtilities.updateComponentTreeUI(frame);
	}

	/**
	 * 添加 telnet 组件
	 */
	private void addTelnetComponents() {
		removeInputOutput();

		jPanelInput.setLayout(new GridLayout(1, 4));

		jLabel = new JLabel(" input telnet IP port ");
		jPanelInput.add(jLabel);

		ip = new JTextField();
		//文本框中的文本使用右对齐
		ip.setHorizontalAlignment(SwingConstants.RIGHT);
		jPanelInput.add(ip);

		port = new JTextField();
		//文本框中的文本使用右对齐
		port.setHorizontalAlignment(SwingConstants.RIGHT);
		jPanelInput.add(port);

		jButton = new JButton(" start telnet ");
		jButton.addActionListener(e -> {
			String ipAddress = ip.getText(), portAddress = port.getText();
			ip.setText("");
			port.setText("");
			jTextArea.setText("");
			jTextArea.append("telnet :" + ip + " " + portAddress + " result");
			jTextArea.append("\r\n");
			boolean connect = TelnetUtil.telnet(ipAddress, Integer.parseInt(portAddress), 5000);
			jTextArea.append(connect + "");
			jTextArea.append("\r\n");
		});
		jPanelInput.add(jButton);

		jTextArea = new JTextArea();
		jPanelOutput.add(jTextArea);
		//添加或删除组件后,更新窗口
		SwingUtilities.updateComponentTreeUI(frame);
	}

	/**
	 * 添加 http 组件
	 */
	private void addHttpComponents() {
		removeInputOutput();
		//这是删除控件后重新添加控件 部分控件不能显示的问题 重新加控件前 frame.pack() 加完控件后重新设置大小 frame.setSize(800, 400);
		frame.pack();

		JLabel ipJLabel = new JLabel(" IP: ");
		ipJLabel.setLocation(frame.getX() + frame.getX() / 5, frame.getY());
		ipJLabel.setSize(20, 40);
		jPanelInput.add(ipJLabel);
		JTextField ipInput = new JTextField("www.baidu.com");
		ipInput.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				ipInput.setText("");
			}
		});
		ipInput.setLocation(frame.getX() + frame.getX() / 5 * 2, frame.getY());
		ipInput.setSize(100, 40);
		jPanelInput.add(ipInput);

		JLabel methodJLabel = new JLabel(" method: ");
		methodJLabel.setLocation(frame.getX() + frame.getX() / 5, frame.getY() + frame.getY() / 5);
		methodJLabel.setSize(20, 40);
		jPanelInput.add(methodJLabel);
		JComboBox<String> method = new JComboBox<>();
		method.setLocation(frame.getX() + frame.getX() / 5 * 2, frame.getY() + frame.getY() / 5);
		method.setSize(20, 40);
		method.addItem("get");
		method.addItem("post");
		jPanelInput.add(method);

//		JLabel methodJLabel = new JLabel(" method: ");
//		jPanelInput.add(methodJLabel);
//		JRadioButton getButton = new JRadioButton("get",true);
//		jPanelInput.add(getButton);
//		JRadioButton postButton = new JRadioButton("post");
//		jPanelInput.add(postButton);


		JLabel param = new JLabel("post param: ");
		param.setLocation(frame.getX() + frame.getX() / 5, frame.getY() + frame.getY() / 5 * 2);
		param.setSize(20, 40);
		jPanelInput.add(param);

		JComboBox<String> contentType = new JComboBox<>();    //创建JComboBox
		contentType.addItem("application/json;charset=utf-8");    //向下拉列表中添加一项
		contentType.addItem("application/x-www-form-urlencoded;charset=utf-8");
		contentType.addItem("text/xml");
		contentType.setLocation(frame.getX() + frame.getX() / 5 * 2, frame.getY() + frame.getY() / 5 * 2);
		contentType.setSize(100, 40);
		jPanelInput.add(contentType);

		JTextArea jTextArea = new JTextArea("result:");
		jTextArea.setLocation(frame.getX() + frame.getX() / 5, frame.getY() + frame.getY() / 5 * 3);
		jTextArea.setSize(200, 200);
		jPanelOutput.add(jTextArea);


		JButton jButton = new JButton(" send ");

		jButton.addActionListener(e -> {
			String selectedContentType = (String) contentType.getSelectedItem();
			String selectedMethod = (String) method.getSelectedItem();
			jTextArea.setText("ip " + ipInput.getText() + selectedContentType + "  selectedMethod " + selectedMethod);
		});
		jPanelInput.add(jButton);

		frame.setSize(800, 400);
		//添加或删除组件后,更新窗口
		SwingUtilities.updateComponentTreeUI(frame);
	}

	/**
	 * 添加 websocket 组件
	 */
	private void addWebsocketComponents() {
		removeInputOutput();

		//添加或删除组件后,更新窗口
		SwingUtilities.updateComponentTreeUI(frame);
	}

	/**
	 * 添加 ChineseChess 组件
	 */
	private void addChineseChessComponents() {
		removeInputOutput();


		//添加或删除组件后,更新窗口
		SwingUtilities.updateComponentTreeUI(frame);
	}

	/**
	 * 添加 ReadExcel 组件
	 */
	private void addReadExcelComponents() {
		removeInputOutput();

		//添加或删除组件后,更新窗口
		SwingUtilities.updateComponentTreeUI(frame);
	}

	/**
	 * 添加 WriteExcel 组件
	 */
	private void addWriteExcelComponents() {
		removeInputOutput();

		//添加或删除组件后,更新窗口
		SwingUtilities.updateComponentTreeUI(frame);
	}

	/**
	 * 添加 MySql 组件
	 */
	private void addMySqlComponents() {
		removeInputOutput();

		//添加或删除组件后,更新窗口
		SwingUtilities.updateComponentTreeUI(frame);
	}

	/**
	 * 添加 Redis 组件
	 */
	private void addRedisComponents() {
		removeInputOutput();

		//添加或删除组件后,更新窗口
		SwingUtilities.updateComponentTreeUI(frame);
	}
}
