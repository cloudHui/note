//package myswing.card.fxgame;
//
//import java.util.HashSet;
//import java.util.Set;
//
//import javafx.application.Application;
//import javafx.geometry.BoundingBox;
//import javafx.geometry.Bounds;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Button;
//import javafx.scene.control.ButtonType;
//import javafx.scene.control.Label;
//import javafx.scene.control.PasswordField;
//import javafx.scene.control.TextField;
//import javafx.scene.input.MouseEvent;
//import javafx.scene.layout.Pane;
//import javafx.scene.layout.VBox;
//import javafx.scene.paint.Color;
//import javafx.scene.shape.Rectangle;
//import javafx.stage.Stage;
//
//public class GamePlayHallOld extends Application {
//
//	private Stage primaryStage;
//	private final Set<Rectangle> cards = new HashSet<>();
//	private double dragOffsetX, dragOffsetY;
//	private boolean isDragging = false;
//	private final Set<Rectangle> selectedCards = new HashSet<>();
//	private final Set<Rectangle> unSelectedCards = new HashSet<>();
//	private Rectangle clickCard = null;
//	private boolean clearOldClickCard = true;
//
//	private boolean dragged = false;
//
//	private final static int WEIGHT = 40;
//	private final static int HIGH = 60;
//	private final static int INIT_WEIGHT = 100;
//	private final static int NUM = 25;
//
//	private final static int UP_PX = 10;
//
//	public static void main(String[] args) {
//		launch(args);
//	}
//
//	@Override
//	public void start(Stage primaryStage) {
//		this.primaryStage = primaryStage;
//		//loginPage();
//		romPage();
//	}
//
//	// 登录注册界面
//	private void loginPage() {
//		VBox root = new VBox(10);
//		root.setAlignment(Pos.CENTER);
//		root.setPadding(new Insets(20));
//
//		Label titleLabel = new Label("登录/注册");
//		titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
//
//		TextField username = new TextField();
//		username.setPromptText("用户名");
//
//		PasswordField password = new PasswordField();
//		password.setPromptText("密码");
//
//		Button loginButton = new Button("登录");
//		Button registerButton = new Button("注册");
//
//		loginButton.setOnAction(e -> {
//			String name = username.getText();
//			String pass = password.getText();
//			if (!name.isEmpty() && !pass.isEmpty()) {
//				hallPage(name);
//			} else {
//				Alert alert = new Alert(Alert.AlertType.ERROR, "请输入用户名和密码！", ButtonType.OK);
//				alert.showAndWait();
//			}
//		});
//
//		registerButton.setOnAction(e -> {
//			String name = username.getText();
//			String pass = password.getText();
//			Alert alert;
//			if (!name.isEmpty() && !pass.isEmpty()) {
//				alert = new Alert(Alert.AlertType.INFORMATION, "注册成功！", ButtonType.OK);
//			} else {
//				alert = new Alert(Alert.AlertType.ERROR, "请输入用户名和密码！", ButtonType.OK);
//			}
//			alert.showAndWait();
//			if (!name.isEmpty() && !pass.isEmpty()) {
//				hallPage(name);
//			}
//		});
//
//		root.getChildren().addAll(titleLabel, username, password, loginButton, registerButton);
//
//		Scene scene = new Scene(root, 300, 250);
//		primaryStage.setTitle("登录/注册");
//		primaryStage.setScene(scene);
//		primaryStage.show();
//	}
//
//	// 展示选项界面
//	private void hallPage(String username) {
//		VBox root = new VBox(10);
//		root.setAlignment(Pos.CENTER);
//		root.setPadding(new Insets(20));
//
//		Label welcomeLabel = new Label("欢迎, " + username);
//		welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
//
//		TextField inputField = new TextField();
//		inputField.setPromptText("输入内容");
//
//		Button enterButton = new Button("进入");
//		Button quickJoinButton = new Button("快速加入");
//
//		enterButton.setOnAction(e -> {
//			Alert alert = new Alert(Alert.AlertType.INFORMATION, "你输入的内容是: " + inputField.getText(), ButtonType.OK);
//			alert.showAndWait();
//			//showThirdPage();
//			romPage();
//		});
//
//		quickJoinButton.setOnAction(e -> romPage());
//		root.getChildren().addAll(welcomeLabel, inputField, enterButton, quickJoinButton);
//
//		Scene scene = new Scene(root, 400, 300);
//		primaryStage.setTitle("主页面");
//		primaryStage.setScene(scene);
//	}
//
//	/**
//	 * 第三个牌展示界面
//	 */
//	private void romPage() {
//		Pane root = new Pane();
//		root.setPrefSize(INIT_WEIGHT + WEIGHT * NUM, 600);
//		// 创建 NUM 张卡片
//		for (int index = 0; index < NUM; index++) {
//			Rectangle card = createCard(index, index % 2 == 0 ? Color.LIGHTGRAY : Color.LIGHTGREEN);
//			card.setOnMouseClicked(event -> {
//				if (clickCard != null) {
//					//还原
//					clickCard.setLayoutY(clickCard.getLayoutY() + UP_PX);
//					System.out.println("card click old down:" + clickCard.getX());
//				}
//				clickCard = card;
//				//上提
//				clickCard.setLayoutY(clickCard.getLayoutY() - UP_PX);
//				System.out.println("card click new up:" + clickCard.getX());
//				clearOldClickCard = false;
//			});
//			root.getChildren().add(card);
//			cards.add(card);
//		}
//
//		// 添加鼠标事件监听器
//		root.setOnMousePressed(this::handleMousePressed);
//		root.setOnMouseDragged(this::handleMouseDragged);
//		root.setOnMouseReleased(this::handleMouseReleased);
//
//		Scene scene = new Scene(root);
//		primaryStage.setTitle("游戏中");
//		primaryStage.setScene(scene);
//		primaryStage.show();
//	}
//
//	//创建一张卡片
//	private Rectangle createCard(double x, Color color) {
//		Rectangle card = new Rectangle(WEIGHT, HIGH, color);
//		card.setStroke(Color.BLACK);
//		card.setLayoutX(x * WEIGHT + INIT_WEIGHT / 2f);
//		card.setLayoutY(HIGH);
//		return card;
//	}
//
//	// 处理鼠标按下事件
//	private void handleMousePressed(MouseEvent event) {
//		clearOldClickCard = true;
//		isDragging = true;
//		dragOffsetX = event.getX();
//		dragOffsetY = event.getY();
//
//		// 将之前选中的卡片下移 UP_PX 个单位
//		updateSelectCard(false);
//
//		selectedCards.clear(); // 清空之前的选择
//	}
//
//	// 处理鼠标拖动事件
//	private void handleMouseDragged(MouseEvent event) {
//		dragged = true;
//		if (!isDragging) return;
//		// 检查哪些卡片在拖动区域内
//		selectedCards.clear();
//		setClickDraggedColor(event);
//	}
//
//	/**
//	 * 设置选中划过的为选中状态
//	 */
//	private void setClickDraggedColor(MouseEvent event) {
//		// 计算拖动区域的边界
//		double startX = Math.min(dragOffsetX, event.getX());
//		double endX = Math.max(dragOffsetX, event.getX());
//		double startY = Math.min(dragOffsetY, event.getY());
//		double endY = Math.max(dragOffsetY, event.getY());
//
//		//Line line = new Line(startX, startY, endX, endY);
//		//for (Rectangle card : cards) {
//		//	if (Tools.doesLineIntersectRectangle(line, card)) {
//		//		selectedCards.add(card);
//		//		card.setFill(Color.YELLOW); // 标记为选中状态
//		//	} else {
//		//		card.setFill(Color.LIGHTGRAY); // 取消选中状态
//		//	}
//		//}
//
//		// 创建拖动区域的 Bounds
//		Bounds dragBounds = new BoundingBox(startX, startY, endX - startX, endY - startY);
//		// 检查哪些卡片与拖动区域相交
//		Bounds cardBounds;
//		unSelectedCards.clear();
//		for (Rectangle card : cards) {
//			cardBounds = card.getBoundsInParent();
//			if (cardBounds.intersects(dragBounds)) {
//				if (clickCard != null && clickCard.equals(card)) {
//					continue;
//				}
//				selectedCards.add(card);
//				card.setFill(Color.YELLOW); // 标记为选中状态
//			} else {
//				unSelectedCards.add(card);
//				card.setFill(Color.LIGHTGRAY); // 取消选中状态
//			}
//		}
//	}
//
//	// 处理鼠标释放事件
//	private void handleMouseReleased(MouseEvent event) {
//		isDragging = false;
//
//		// 将选中的卡片上提 UP_PX 个单位
//		updateSelectCard(true);
//
//		// 恢复所有卡片的颜色
//		for (Rectangle card : unSelectedCards) {
//			card.setFill(Color.LIGHTGRAY);
//		}
//
//		if (dragged) {
//			if (clearOldClickCard) {
//				clickCard = null;
//			}
//			dragged = false;
//		}
//	}
//
//
//	/**
//	 * 将牌上提或者还原
//	 *
//	 * @param up true 上提 还原
//	 */
//	private void updateSelectCard(boolean up) {
//		// 将选中的卡片上提 UP_PX 个单位
//		for (Rectangle card : selectedCards) {
//			if (up) {
//				System.out.println("root press new up:" + card.getX());
//				card.setLayoutY(card.getLayoutY() - UP_PX);
//			} else {
//				System.out.println("root press new down:" + card.getX());
//				card.setLayoutY(card.getLayoutY() + UP_PX);
//			}
//		}
//	}
//}
