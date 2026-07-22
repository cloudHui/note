package myswing.card.sw;

import java.awt.*;
import javax.swing.*;

/**
 * 登录/注册界面
 */
public class LoginRegisterFrame extends JFrame {

	public LoginRegisterFrame() {
		JTextField usernameField;
		JPasswordField passwordField;
		setTitle("登录/注册");
		setSize(300, 200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel(new GridLayout(3, 2));

		panel.add(new JLabel("用户名:"));
		usernameField = new JTextField();
		panel.add(usernameField);

		panel.add(new JLabel("密码:"));
		passwordField = new JPasswordField();
		panel.add(passwordField);

		JButton loginButton = new JButton("登录");
		JButton registerButton = new JButton("注册");

		panel.add(loginButton);
		panel.add(registerButton);

		add(panel);

		// 登录按钮事件
		loginButton.addActionListener(e -> {
			String username = usernameField.getText();
			String password = new String(passwordField.getPassword());
			if (!username.isEmpty() && !password.isEmpty()) {
				JOptionPane.showMessageDialog(this, "登录成功！");
				dispose(); // 关闭当前窗口
				new HallFrame(username).setVisible(true); // 打开主界面
			} else {
				JOptionPane.showMessageDialog(this, "请输入用户名和密码！");
			}
		});

		// 注册按钮事件
		registerButton.addActionListener(e -> {
			String username = usernameField.getText();
			String password = new String(passwordField.getPassword());
			if (!username.isEmpty() && !password.isEmpty()) {
				JOptionPane.showMessageDialog(this, "注册成功！");
			} else {
				JOptionPane.showMessageDialog(this, "请输入用户名和密码！");
			}
		});
	}
}