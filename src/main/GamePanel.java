package main;

import engine.Timer;
import engine.Vec2I;
import engine.gamestate.StateManager;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable, KeyListener, MouseListener {
	/** */
	private static final long serialVersionUID = 2275808897949938723L;
	// dimensions
	public static final Vec2I pixelSize = new Vec2I(320,240);
	public static final int WIDTH = 320;
	public static final int HEIGHT = 240;
	public static final int SCALE = 2;
	public static boolean isApplet = false;
	// game thread
	private Thread thread;
	private boolean running;
	private int FPS = 60;
	private int targetTime = 1000/FPS;
	
	// image
	private BufferedImage image;
	private Graphics2D g;
	
	// game state manager
	private StateManager gsm;
	
	public GamePanel() {
		super();
		System.out.println("GamePanel construction");
		setPreferredSize(new Dimension(pixelSize.x * SCALE, pixelSize.y * SCALE));
		setFocusable(true);
		requestFocus();
	}
	
	public void addNotify() {
		super.addNotify();
		if(thread == null) {
			thread = new Thread(this);
			addKeyListener(this);
			addMouseListener(this);
			thread.start();
		}
	}
	
	public void init() {
		System.out.println("GamePanel init");
		image = new BufferedImage(pixelSize.x, pixelSize.y, 
				BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D)image.getGraphics();
		gsm = new StateManager(this);
		running = true;
	}
	
	public void run(){
		System.out.println("GamePanel Thread started");
		init();
		
		int wait;
		System.out.println("GamePanel game loop starting");
		// game loop
		while(running) {
			Timer.update();
			update((int)Timer.deltaTimeMS);
			draw();
			repaint();//drawToScreen();
			
			wait = targetTime - Timer.currentFrameDurationMS();
			if(wait > 0){
				try {
					Thread.sleep(wait);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	private void update(int ms) {gsm.update(ms);}
	private void draw() {if(running)gsm.draw(g);}
//	private void drawToScreen() {
//		Graphics g2 = getGraphics();
//		g2.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
//		g2.dispose();
//	}
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.drawImage(image, 0, 0, pixelSize.x*SCALE, pixelSize.y*SCALE, null);
	}
	
	public void keyTyped(KeyEvent key) {}
	public void keyPressed(KeyEvent key) {gsm.keyPressed(key.getKeyCode());}
	public void keyReleased(KeyEvent key) {gsm.keyReleased(key.getKeyCode());}
	public void mouseClicked(MouseEvent arg0) {}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent arg0) {gsm.mousePress(arg0);}
	public void mouseReleased(MouseEvent arg0) {
	}
}
