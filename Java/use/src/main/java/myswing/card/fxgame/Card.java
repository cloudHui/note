package myswing.card.fxgame;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


/**
 * 牌
 */
public class Card extends Pane {
	private final static int UP_PX = 10;
	private final static int WEIGHT = 40;
	private final static int HIGH = 60;
	private final String cardColor;

	private final String cardValue;

	private int insteadValue;//代替的值

	private final Rectangle rectangle;

	public Card(String cardColor, String cardValue, int index) {
		// 创建扑克牌的矩形背景
		rectangle = new Rectangle(WEIGHT, HIGH, Color.GREENYELLOW);
		rectangle.setStroke(Color.GREENYELLOW);


		rectangle.setLayoutY(index * WEIGHT + GamePlayHall.INIT_WEIGHT / 2f); // 水平排列
		rectangle.setLayoutY(GamePlayHall.PRE_HIGH - HIGH); // 垂直排列

		// 添加左上角的牌值标签
		Label valueLabel = new Label(cardValue);
		valueLabel.setLayoutX(index * WEIGHT + WEIGHT / 4f);
		valueLabel.setLayoutY(GamePlayHall.PRE_HIGH - HIGH);
		valueLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

		// 添加中间的花色标签
		Label suitLabel = new Label(cardColor);
		suitLabel.setLayoutX(index * WEIGHT + WEIGHT);
		suitLabel.setLayoutY(GamePlayHall.PRE_HIGH - HIGH);
		suitLabel.setStyle("-fx-font-size: 36; -fx-font-weight: bold;");

		// 将标签添加到容器中
		getChildren().add(valueLabel);
		getChildren().add(suitLabel);
		setOnMouseEntered(event -> rectangle.setTranslateY(UP_PX / 2f)); // 鼠标滑过上提
		setOnMouseExited(event -> rectangle.setTranslateY(0)); // 鼠标移出恢复
		setOnMouseClicked(event -> rectangle.setTranslateY(UP_PX)); // 点击进一步上提
		this.cardColor = cardColor;
		this.cardValue = cardValue;
	}


	public String getColor() {
		return cardColor;
	}

	public String getValue() {
		return cardValue;
	}

	public int getInsteadValue() {
		return insteadValue;
	}

	public void setInsteadValue(int insteadValue) {
		this.insteadValue = insteadValue;
	}

	public Rectangle getRectangle() {
		return rectangle;
	}

	@Override
	public String toString() {
		return "Card{" +
				"cardColor='" + cardColor + '\'' +
				", cardValue='" + cardValue + '\'' +
				", insteadValue=" + insteadValue +
				'}';
	}
}
