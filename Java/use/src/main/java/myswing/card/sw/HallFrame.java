package myswing.card.sw;

import javax.swing.*;

/**
 * 主界面
 */
public class HallFrame extends JFrame {

	public HallFrame(String username) {
		setTitle("主界面 - 欢迎 " + username);
		setSize(400, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel();
		JButton joinButton = new JButton("加入");

		panel.add(joinButton);

		add(panel);

		// 加入按钮事件
		joinButton.addActionListener(e -> {
			dispose(); // 关闭当前窗口
			new GameFrame().setVisible(true); // 打开方块展示界面
		});
	}
}