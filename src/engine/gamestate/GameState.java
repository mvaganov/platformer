package engine.gamestate;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

public abstract class GameState {
	
	protected StateManager gsm;
	public StateManager getGameStateManager(){return gsm;}
	
	public abstract void init();
	public abstract void update();
	public abstract void draw(Graphics2D g);
	public abstract void keyPressed(int k);
	public abstract void keyReleased(int k);
	public abstract void mousePress(MouseEvent arg0);
	public abstract void release();
}
