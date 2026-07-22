//package myswing.card.mycard;
//
//import java.awt.*;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
//import java.awt.image.BufferedImage;
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//import javax.imageio.ImageIO;
//import javax.swing.*;
//
//import myswing.card.fxgame.enums.Color;
//
//// 自定义面板类
//public class Card extends JPanel {
//	private static final String FILE_PATH = System.getProperty("user.dir") + "/use/src/main/resources/picture/PokerCard/";
//	private static final String HEAD = "pai";
//	private static final String END = ".png";
//	private static final String LINE = "_";
//	private static final String ZERO = "0";
//	private static final String SMALL = "small";
//	private static final String SE = "hua_se";
//
//	private final String value;
//
//	private final boolean color;
//	private final boolean joker;
//	private final Color color;
//
//	/**
//	 * 划过的牌
//	 */
//	private static final List<CardInfo> touchCards = new ArrayList<>();
//
//	public Card(String value, boolean color, boolean joker, Color colorEnums) {
//		this.value = value;
//		this.color = color;
//		this.joker = joker;
//		this.color = colorEnums;
//
//		addListener();
//	}
//
//	/**
//	 * 初始化牌图片
//	 */
//	private BufferedImage initPicture() {
//		try {
//			if ("".equals(value)) {
//				return null;
//			}
//			String pictureSrc = getPictureSrc();
//			File file = new File(pictureSrc);
//			if (!file.exists()) {
//				throw new Exception("file " + color + " not exit " + pictureSrc);
//			}
//			return ImageIO.read(file);
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		return null;
//	}
//
//	/**
//	 * 获取卡片图片地址
//	 */
//	private String getPictureSrc() {
//		if (joker) {
//			if (!color) {
//				// 加载第一张图片（背景图片）
//				return FILE_PATH + HEAD + LINE + value + END;
//			} else {
//				//（中间）
//				return FILE_PATH + SE + LINE + value + END;
//			}
//		} else {
//			//pai_0_1
//			if (!color) {
//				// 加载第一张图片（背景图片）
//				return FILE_PATH + HEAD + LINE + ZERO + LINE + value + END;
//			} else {
//				//hua_se_small_2
//				//（中间）
//				return FILE_PATH + SE + LINE + SMALL + LINE + color.getId() + END;
//			}
//		}
//	}
//
//	@Override
//	protected void paintComponent(Graphics g) {
//		super.paintComponent(g);
//		BufferedImage image = initPicture();
//		if (image == null) {
//			return;
//		}
//		int x = getX();
//		int y = getY();
//		System.out.println(value + " " + color + " x: " + x + "  y: " + y);
//		if (!color) {
//			// 绘制第一张图片（左上角）
//			g.drawImage(image, 40, 68, 40, 50, this);
//		} else {
//			// 设置第二张图片的位置和大小（居中）
//			g.drawImage(image, 50, 50, 40, 50, this);
//		}
//	}
//
//	private void addListener() {
//		// 添加点击事件监听器
//		addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				System.out.println(value + "" + color + " 面板点击位置: (" + e.getPoint().toString() + ")");
//			}
//		});
//
//		addMouseListener(new MouseListener() {
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				System.out.println(value + "" + color + " 面板点击位置: (" + e.getPoint().toString() + ")");
//			}
//
//			@Override
//			public void mousePressed(MouseEvent e) {
//				System.out.println("mousePressed card " + value);
//				if (!touchCards.isEmpty()) {
//					touchCards.clear();
//				}
//				CardInfo cardInfo = new CardInfo(color, Integer.parseInt(value));
//				touchCards.add(cardInfo);
//				System.out.println("mousePressed cardInfo " + cardInfo.toString());
//			}
//
//			@Override
//			public void mouseReleased(MouseEvent e) {
//				System.out.println("mouseReleased card " + value);
//				if (!"".equals(value)) {
//					CardInfo cardInfo = new CardInfo(color, Integer.parseInt(value));
//					touchCards.add(cardInfo);
//					System.out.println("mouseReleased cardInfo " + cardInfo.toString());
//				}
//			}
//
//			@Override
//			public void mouseEntered(MouseEvent e) {
//				System.out.println("mouseEntered card " + value);
//				if (!"".equals(value)) {
//					CardInfo cardInfo = new CardInfo(color, Integer.parseInt(value));
//					touchCards.add(cardInfo);
//					System.out.println("mouseEntered cardInfo " + cardInfo.toString());
//				} else {
//
//				}
//			}
//
//			@Override
//			public void mouseExited(MouseEvent e) {
//				//先退再进
//				System.out.println("mouseExited card " + value);
//				if (!"".equals(value)) {
//					CardInfo cardInfo = new CardInfo(color, Integer.parseInt(value));
//					touchCards.add(cardInfo);
//					System.out.println("mouseExited cardInfo " + cardInfo.toString());
//				}
//			}
//		});
//	}
//}