package myswing.card.fxgame;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import myswing.card.fxgame.enums.CardColor;
import myswing.card.fxgame.enums.CardValue;

public class GamePlayHall extends Application {

	private Stage primaryStage;
	private final static int WEIGHT = 40;
	public final static int INIT_WEIGHT = 100;
	public final static int PRE_HIGH = 600;
	private final static int NUM = 22;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		//loginPage();
		gamePage();
	}

	// 登录注册界面
	private void loginPage() {
		VBox root = new VBox(10);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(20));

		Label titleLabel = new Label("登录/注册");
		titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

		TextField username = new TextField();
		username.setPromptText("用户名");

		PasswordField password = new PasswordField();
		password.setPromptText("密码");

		Button loginButton = new Button("登录");
		Button registerButton = new Button("注册");

		loginButton.setOnAction(e -> {
			String name = username.getText();
			String pass = password.getText();
			if (!name.isEmpty() && !pass.isEmpty()) {
				hallPage(name);
			} else {
				Alert alert = new Alert(Alert.AlertType.ERROR, "请输入用户名和密码！", ButtonType.OK);
				alert.showAndWait();
			}
		});

		registerButton.setOnAction(e -> {
			String name = username.getText();
			String pass = password.getText();
			Alert alert;
			if (!name.isEmpty() && !pass.isEmpty()) {
				alert = new Alert(Alert.AlertType.INFORMATION, "注册成功！", ButtonType.OK);
			} else {
				alert = new Alert(Alert.AlertType.ERROR, "请输入用户名和密码！", ButtonType.OK);
			}
			alert.showAndWait();
			if (!name.isEmpty() && !pass.isEmpty()) {
				hallPage(name);
			}
		});

		root.getChildren().addAll(titleLabel, username, password, loginButton, registerButton);

		Scene scene = new Scene(root, 300, 250);
		primaryStage.setTitle("登录/注册");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	// 展示选项界面
	private void hallPage(String username) {
		VBox root = new VBox(10);
		root.setAlignment(Pos.CENTER);
		root.setPadding(new Insets(20));

		Label welcomeLabel = new Label("欢迎, " + username);
		welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

		TextField inputField = new TextField();
		inputField.setPromptText("输入内容");

		Button enterButton = new Button("进入");
		Button quickJoinButton = new Button("快速加入");

		enterButton.setOnAction(e -> {
			Alert alert = new Alert(Alert.AlertType.INFORMATION, "你输入的内容是: " + inputField.getText(), ButtonType.OK);
			alert.showAndWait();
			//showThirdPage();
			gamePage();
		});

		quickJoinButton.setOnAction(e -> gamePage());
		root.getChildren().addAll(welcomeLabel, inputField, enterButton, quickJoinButton);

		Scene scene = new Scene(root, 400, 300);
		primaryStage.setTitle("主页面");
		primaryStage.setScene(scene);
	}

	private void gamePage() {
		Pane root = new Pane();
		root.setPrefSize((INIT_WEIGHT + WEIGHT * NUM), PRE_HIGH);
		Scene scene = new Scene(root);

		// 定义扑克牌的花色和数值
		String[] suits = { "♠", "♥", "♦", "♣" };
		String[] values = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K" };

		// 创建并添加 20 张扑克牌 + 大小王
		String suit;
		String value;
		for (int i = 0; i < 1; i++) { // 总共 22 张牌（20 张普通牌 + 2 张大小王）
			if (i == 20) {
				suit = "★"; // 小王
				value = "小王";
			} else if (i == 21) {
				suit = "☆"; // 大王
				value = "大王";
			} else {
				int suitIndex = i % suits.length; // 花色循环
				int valueIndex = i / values.length; // 数值循环
				suit = suits[suitIndex];
				value = values[valueIndex];
			}
			// 将扑克牌添加到主场景
			root.getChildren().add(new Card(suit, value, i));
		}

		// 添加点击空白区域还原所有牌的功能
		root.setOnMouseClicked(this::resetAllCards);

		primaryStage.setTitle("游戏中");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * 第三个牌展示界面
	 */
	private void romPage() {
		Pane root = new Pane();
		root.setPrefSize(INIT_WEIGHT + WEIGHT * NUM, 600);
		String suit;
		String value;
		// 创建 NUM 张卡片
		for (int index = 0; index < NUM; index++) {
			if (index == 20) {
				suit = "★"; // 小王
				value = "小王";
			} else if (index == 21) {
				suit = "☆"; // 大王
				value = "大王";
			} else {
				int suitIndex = index % CardColor.values().length; // 花色循环
				int valueIndex = index / CardValue.values().length; // 数值循环
				suit = CardColor.values()[suitIndex].getDesc();
				value = CardValue.values()[valueIndex].getDesc();
			}

			// 将扑克牌添加到主场景
			root.getChildren().add(new Card(suit, value, index));
		}

		Scene scene = new Scene(root);
		primaryStage.setTitle("游戏中");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	// 还原所有牌的位置
	private void resetAllCards(MouseEvent event) {
		Card card;
		if (event.getTarget() instanceof Card) {
			card = (Card) event.getTarget();
			if (card.getChildren().size() > 0) {
				// 如果点击的是扑克牌本身，则不执行还原
				return;
			}
		}

		for (Node node : ((Pane) event.getSource()).getChildren()) {
			if (node instanceof Card) {
				card = (Card) node;
				card.getRectangle().setTranslateY(0);
				System.out.println(card.toString());
			}
		}
	}
}
