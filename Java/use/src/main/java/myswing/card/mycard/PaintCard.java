//package myswing.card.mycard;
//
//import java.awt.*;
//import javax.swing.*;
//
//import myswing.card.fxgame.enums.Color;
//
//public class PaintCard {
//
//	public static void main(String[] args) {
//		SwingUtilities.invokeLater(PaintCard::initPanel);
//	}
//
//	private static void initPanel() {
//		JFrame frame = new JFrame("图片布局与点击事件");
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		frame.setLayout(new GridLayout(2, 15));
//		frame.setSize(1400, 400);
//		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//
//		// 计算窗口应该出现的左下角的位置
//		int y = (int) (screenSize.getHeight() - frame.getHeight());
//
//
//		// 设置窗口的位置
//		frame.setLocation(10, y);
//		for (int value = 0; value <= 15; value++) {
//			frame.add(new Card("", false, false, Color.NULL));
//		}
//		for (int value = 501; value <= 502; value++) {
//			frame.add(new Card(value + "", false, true, Color.NULL));
//			frame.add(new Card(value + "", true, true, Color.NULL));
//		}
//
//		for (int value = 1; value <= 13; value++) {
//			frame.add(new Card(value + "", true, false, Color.CLUB));
//			frame.add(new Card(value + "", false, false, Color.CLUB));
//		}
//
//		frame.setVisible(true);
//	}
//}
//
//
