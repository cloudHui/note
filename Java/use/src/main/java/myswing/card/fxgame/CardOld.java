//package myswing.card.fxgame;
//
//import javafx.scene.Node;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.Pane;
//import javafx.scene.layout.StackPane;
//import javafx.scene.paint.Color;
//import javafx.scene.text.Font;
//import javafx.scene.text.Text;
//import myswing.card.fxgame.enums.CardColor;
//import myswing.card.fxgame.enums.CardValue;
//
///**
// * 牌
// */
//public class CardOld extends Pane {
//
//
//	private final CardColor cardColor;
//
//	private final CardValue cardValue;
//
//	private int insteadValue;//代替的值
//
//	public CardOld(CardColor cardColor, CardValue cardValue, Node... children) {
//		super(children);
//		this.cardColor = cardColor;
//		this.cardValue = cardValue;
//		init();
//	}
//
//	private void init() {
//		// 创建一个 StackPane 容器，用于叠加 ImageView 和 Text
//		StackPane cardPane = new StackPane();
//
//		// 加载扑克牌背景图片
//		Image cardImage = new Image("file:card_back.png"); // 替换为你的扑克牌图片路径
//		ImageView imageView = new ImageView(cardImage);
//		imageView.setFitWidth(120); // 设置宽度
//		imageView.setPreserveRatio(true); // 保持宽高比
//
//		// 在左上角添加牌值 (例如 "A")K Q J JOKER
//		Text valueText = new Text(cardValue.getDesc());
//		valueText.setFont(Font.font("Arial", 18));
//		valueText.setFill(Color.BLACK);
//		valueText.setStyle("-fx-font-weight: bold;"); // 设置字体加粗
//		valueText.setLayoutX(10); // 左边距
//		valueText.setLayoutY(25); // 上边距
//
//		// 在正中间添加花色 (例如 "♠")♥ ♣ ♦ (小王)🃏  大王 🃏 🃏
//		Text suitText = new Text(cardColor.getDesc());
//		suitText.setFont(Font.font("Arial", 36));
//		suitText.setFill(Color.BLACK); // 根据需要设置花色颜色
//		//suitText.setFill(Color.RED); // 根据需要设置花色颜色
//		suitText.setStyle("-fx-font-weight: bold;");
//
//		// 将 ImageView 和 Text 添加到 StackPane 中
//		cardPane.getChildren().addAll(imageView, valueText, suitText);
//	}
//
//
//
//	public CardColor getColor() {
//		return cardColor;
//	}
//
//	public CardValue getValue() {
//		return cardValue;
//	}
//
//	public int getInsteadValue() {
//		return insteadValue;
//	}
//
//	public void setInsteadValue(int insteadValue) {
//		this.insteadValue = insteadValue;
//	}
//}
