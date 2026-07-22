package swing.tcs;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * 完成的功能：添加重开一局
 */
public class SnakeFrame extends Frame implements Runnable {
	//方格的宽度和长度
	public static final int BLOCK_WIDTH = 15;
	public static final int BLOCK_HEIGHT = 15;
	//界面的方格的行数和列数
	public static final int ROW = 40;
	public static final int COL = 40;

	//得分
	private int score;

	//上次重绘时间
	private long lastPaintTimes;

	//当前刷新间隔 毫秒
	private int currPaintSpeed;

	private boolean pause;

	private Image offScreenImage = null;

	private Snake snake = new Snake(this);

	private final Egg egg = new Egg();

	public static void main(String[] args) {
		SnakeFrame sf = new SnakeFrame();
		sf.launch();
		sf.reset();
		new Thread(sf).start();
	}

	public void launch() {

		this.setTitle("Snake");
		this.setSize(ROW * BLOCK_HEIGHT, COL * BLOCK_WIDTH);
		this.setLocation(30, 40);
		this.setBackground(Color.WHITE);
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}

		});
		this.setResizable(false);
		this.setVisible(true);

		//为界面添加监听事件
		this.addKeyListener(new KeyMonitor());
	}


	private boolean b_gameOver;

	public void gameOver() {
		b_gameOver = true;
	}


	/**
	 * 重置
	 */
	private void reset() {
		b_gameOver = false;
		pause = false;
		currPaintSpeed = 300;
		score = 0;
		lastPaintTimes = 0;
	}

	/**
	 * 重写update方法
	 */
	@Override
	public void update(Graphics g) {
		if (b_gameOver) {
			g.drawString("游戏结束！！！", ROW / 2 * BLOCK_HEIGHT, COL / 2 * BLOCK_WIDTH);
			return;
		}
		if (offScreenImage == null) {
			offScreenImage = this.createImage(ROW * BLOCK_HEIGHT, COL * BLOCK_WIDTH);
		}
		long currTime = System.currentTimeMillis();
		if (lastPaintTimes != 0) {
			long diff = currTime - lastPaintTimes;
			if (diff < currPaintSpeed) {
				return;
			}
		}
		lastPaintTimes = currTime;

		Graphics offg = offScreenImage.getGraphics();
		//先将内容画在虚拟画布上
		paint(offg);
		//然后将虚拟画布上的内容一起画在画布上
		g.drawImage(offScreenImage, 0, 0, null);
		if (!pause) {
			snake.draw(g);
			boolean b_Success = snake.eatEgg(egg);
			//吃一个加5分
			if (b_Success) {
				score += 5;
				//每次加速 毫秒
				int speedFrame = 50;
				//最小刷新毫秒
				int minFrame = 100;
				if (currPaintSpeed >= speedFrame + minFrame) {
					currPaintSpeed -= speedFrame;
				}
			}
		}
		egg.draw(g);
		displaySomeInfo(g);
		repaint();
	}

	/**
	 * 函数功能：在界面上显示一些提示信息
	 */
	public void displaySomeInfo(Graphics g) {
		Color c = g.getColor();
		g.setColor(Color.RED);
		g.drawString("使用说明:空格键---暂停，再按键空格---暂停后开始,r---重新开始", 5 * BLOCK_HEIGHT, 3 * BLOCK_WIDTH);
		g.drawString("得分:" + score, 5 * BLOCK_HEIGHT, 5 * BLOCK_WIDTH);
		g.setColor(c);

	}

	@Override
	public void paint(Graphics g) {
		Color c = g.getColor();
		g.setColor(Color.GRAY);
		//将界面画成由ROW*COL的方格构成,两个for循环即可解决
		for (int i = 0; i < ROW; i++) {
			g.drawLine(0, i * BLOCK_HEIGHT, COL * BLOCK_WIDTH, i * BLOCK_HEIGHT);
		}
		for (int i = 0; i < COL; i++) {
			g.drawLine(i * BLOCK_WIDTH, 0, i * BLOCK_WIDTH, ROW * BLOCK_HEIGHT);
		}

		g.setColor(c);
	}

	@Override
	public void run() {
		while (true) {
			update(this.getGraphics());
		}
	}


	private class KeyMonitor extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			switch (key) {
				case KeyEvent.VK_SPACE:
					pause = !pause;
					break;
				case KeyEvent.VK_R:
					reset();
					snake = new Snake(snake.getSf());
					break;
				case KeyEvent.VK_ESCAPE:
					snake = null;
					pause = true;
					break;
				default:
					snake.keyPressed(e);
					break;
			}
		}
	}
}