package myswing.card.sw;

import java.awt.*;
import javax.swing.*;

/**
 * 游戏界面
 */
public class GameFrame extends JFrame {

	public GameFrame() {
		setTitle("方块展示界面");
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel(new GridLayout(1, 25)); // 5x5网格布局，共25个方块

		for (int index = 0; index < 25; index++) {
			JButton blockButton = new JButton("方块 " + (index + 1));
			blockButton.setBackground(Color.LIGHT_GRAY);
			blockButton.setFont(new Font("Arial", Font.BOLD, 14));
			int finalI = index;
			blockButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "你点击了方块 " + (finalI + 1)));
			panel.add(blockButton);
		}

		JButton pickupButton = new JButton("提起");
		pickupButton.setFont(new Font("Arial", Font.BOLD, 16));
		pickupButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "你按下了'提起'按钮！"));

		JPanel controlPanel = new JPanel();
		controlPanel.add(pickupButton);

		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);
		add(controlPanel, BorderLayout.SOUTH);
	}
}