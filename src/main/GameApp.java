package main;

import javax.swing.JApplet;
import javax.swing.JFrame;

public class GameApp extends JApplet{
	public static boolean isApplet = false;
	public static boolean DEBUG = false;
	/** */
	private static final long serialVersionUID = 3574970253019934671L;
	public static void main(String[] args)
	{
		JFrame window = new JFrame("Platformer");
		window.setContentPane(new GamePanel());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.pack();
		window.setVisible(true);
	}
	public void init()
	{
		System.out.println("applet init");
		isApplet = true;
		add(new GamePanel());
	}
}
