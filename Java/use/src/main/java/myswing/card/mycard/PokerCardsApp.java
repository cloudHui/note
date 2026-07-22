package myswing.card.mycard;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class PokerCardsApp extends Application {

	private final double LIFT_HEIGHT = -20; // 提起高度

	@Override
	public void start(Stage primaryStage) {
		Pane root = new Pane();
		Scene scene = new Scene(root, 800, 600);

		// 定义扑克牌的花色和数值
		String[] suits = { "♠️", "♥️", "♦️", "♣️" };
		String[] values = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K" };

		// 创建并添加 20 张扑克牌 + 大小王
		for (int i = 0; i < 22; i++) { // 总共 22 张牌（20 张普通牌 + 2 张大小王）
			String suit;
			String value;

			if (i == 20) {
				suit = "★"; // 小王
				value = "小王";
			} else if (i == 21) {
				suit = "☆"; // 大王
				value = "大王";
			} else {
				int suitIndex = i % 4; // 花色循环
				int valueIndex = i / 4; // 数值循环
				suit = suits[suitIndex];
				value = values[valueIndex];
			}

			// 创建扑克牌的矩形背景
			Rectangle card = new Rectangle(100, 150, Color.WHITE);
			card.setStroke(Color.BLACK);
			card.setTranslateX(120 * (i % 6)); // 水平排列
			card.setTranslateY(200 * (i / 6f)); // 垂直排列

			// 添加左上角的牌值标签
			Label valueLabel = new Label(value);
			valueLabel.setLayoutX(5);
			valueLabel.setLayoutY(15);
			valueLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");

			// 添加中间的花色标签
			Label suitLabel = new Label(suit);
			suitLabel.setLayoutX(40);
			suitLabel.setLayoutY(80);
			suitLabel.setStyle("-fx-font-size: 36; -fx-font-weight: bold;");

			// 将标签添加到容器中
			Pane cardPane = new Pane(card, valueLabel, suitLabel);
			cardPane.setOnMouseEntered(event -> card.setTranslateY(LIFT_HEIGHT / 2)); // 鼠标滑过上提
			cardPane.setOnMouseExited(event -> card.setTranslateY(0)); // 鼠标移出恢复
			cardPane.setOnMouseClicked(event -> card.setTranslateY(LIFT_HEIGHT)); // 点击进一步上提

			// 将扑克牌添加到主场景
			root.getChildren().add(cardPane);
		}

		// 添加点击空白区域还原所有牌的功能
		root.setOnMouseClicked(this::resetAllCards);

		primaryStage.setTitle("扑克牌展示");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	// 还原所有牌的位置
	private void resetAllCards(MouseEvent event) {
		Pane pane;
		if (event.getTarget() instanceof Pane) {
			pane=(Pane) event.getTarget();
			if (pane.getChildren().size() > 0) {
				return; // 如果点击的是扑克牌本身，则不执行还原
			}
		}

		for (Node child : ((Pane) event.getSource()).getChildren()) {
			if (child instanceof Pane) {
				pane = (Pane) child;
				Rectangle card = (Rectangle) pane.getChildren().get(0);
				card.setTranslateY(0); // 还原所有牌的位置
			}
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
