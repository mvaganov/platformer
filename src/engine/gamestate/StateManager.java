package engine.gamestate;

import game.gamestate.Level1;
import game.gamestate.MainMenu;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class StateManager {
	private GameState gameState;
	private int currentState;
	
	public JPanel panelContext;
	
	public JPanel getPanel(){return panelContext;}
	
	public static final int MENUSTATE = 0;
	public static final int LEVEL1STATE = 1;
	public static final int NUMGAMESTTES = 2;
	
	public StateManager(JPanel panelContext) {
		this.panelContext = panelContext;
		setState(MENUSTATE);
	}
	
	private void loadState(int state) {
		switch(state) {
		case MENUSTATE:
			gameState = new MainMenu(this);
			break;
		case LEVEL1STATE:
			gameState = new Level1(this);
		}
	}
	
	private void unloadState(int state) {
		if(gameState != null)
			gameState.release();
		gameState = null;
	}
	
	public void setState(int state) {
		unloadState(currentState);
		currentState = state;
		loadState(currentState);
	}
	
	public void update(int ms) {
		if(gameState == null)	return;
		gameState.update();
	}
	public void draw(Graphics2D g) {
		if(gameState == null)	return;
		gameState.draw(g);
	}
	public void keyPressed(int k) {
		if(gameState == null)	return;
		gameState.keyPressed(k);
	}
	public void keyReleased(int k) {
		if(gameState == null)	return;
		gameState.keyReleased(k);
	}
	public void mousePress(MouseEvent arg0) {
		if(gameState == null)	return;
		gameState.mousePress(arg0);
	}
}
