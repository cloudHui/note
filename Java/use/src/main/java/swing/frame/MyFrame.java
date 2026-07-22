package swing.frame;

import javax.swing.*;
import java.awt.*;

public class MyFrame {


	public MyFrame() {

	}

	public void old() {
		JFrame jFrame = new JFrame("MyFrame");
		jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jFrame.setBounds(800, 300, 400, 200);

		JPanel jPanel = new JPanel();
		JLabel jLabel = new JLabel("my first jframe");
		jPanel.add(jLabel);
		jFrame.add(jPanel);//居中上对其

//		jFrame.add(jLabel);//左中对齐

//		jFrame.getContentPane().add(jLabel);//左中对齐
		jFrame.setVisible(true);
	}

	public void init() {
		JFrame frame=new JFrame("MyFrame");    //创建Frame窗口
		frame.setLayout(new BorderLayout());    //为Frame窗口设置布局为BorderLayout
		JButton button1=new JButton ("上");
		JButton button2=new JButton("左");
		JButton button3=new JButton("中");
		JButton button4=new JButton("右");
		JButton button5=new JButton("下");
		frame.add(button1,BorderLayout.NORTH);
		frame.add(button2,BorderLayout.WEST);
		frame.add(button3,BorderLayout.CENTER);
		frame.add(button4,BorderLayout.EAST);
		frame.add(button5,BorderLayout.SOUTH);
		frame.setBounds(300,200,600,300);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
