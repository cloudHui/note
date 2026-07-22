package swing.frame;


import swing.util.Tool;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Calculator {
	public Calculator() {

	}

	public void init() {
		JFrame frame = new JFrame("Calculaor");    //创建Frame窗口

		JPanel jPanel = new JPanel();
		jPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		jPanel.setLayout(new BorderLayout(0, 0));
		frame.setContentPane(jPanel);

		JPanel jScan = new JPanel();
		jPanel.add(jScan, BorderLayout.NORTH);

		JTextField text = new JTextField();
		text.setHorizontalAlignment(SwingConstants.RIGHT);  //文本框中的文本使用右对齐
		text.setColumns(18);
		jScan.add(text);


		JPanel jClock = new JPanel();
		jPanel.add(jClock, BorderLayout.CENTER);
		jClock.setLayout(new GridLayout(5, 4, 5, 5));

		addClick(jClock, text);


		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(300, 200, 600, 300);
		frame.setVisible(true);
	}

	private void addClick(JPanel jClock, JTextField text) {
		String[] clickString = new String[]{"%", "DEL", "C", "", "7", "8", "9", "+", "4", "5", "6", "-", "3", "2",
				"1", "*", "0", ".", "=", "/"};
		JButton button;
		List<String> strings = new ArrayList<>();
		for (String click : clickString) {
			button = new JButton(click);
			jClock.add(button);
			button.addActionListener(e -> {
				String action = e.getActionCommand();
				boolean isNum = Tool.checkNumber(action);
				if (!action.equals("DEL") && !action.equals("C")) {
					strings.add(action);
					text.setText(text.getText() + action);
				}
				text.setText(text.getText() + "\\n");
				if (isNum) {

				} else {
					switch (action) {
						case "%":
							break;
						case "DEL":
							if (strings.size() > 0) {
								String temp = strings.remove((strings.size() - 1));
								String value = text.getText();
								value = value.substring(0, value.lastIndexOf(temp));
								text.setText(value);
							}
							break;
						case "C":
							text.setText("");
							strings.clear();
							break;
						case "+":
							break;
						case "-":
							break;
						case "*":
							break;
						case ".":
							break;
						case "/":
							break;
						case "=":
							break;
						default:
							break;
					}
				}
			});
		}

	}
}
