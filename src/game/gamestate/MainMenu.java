package game.gamestate;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import engine.AABB;
import engine.Background;
import engine.Vec2I;
import engine.gamestate.GameState;
import engine.gamestate.StateManager;
import main.GamePanel;

public class MainMenu extends GameState {
	
	private Background bg;
	
	private int currentChoice = 0;
	private String[] options = {
			"Start",
			"Help",
			"Quit"
	};
	
	private Color titleColor;
	private Font titleFont;
	private Font font;
	
	public static double MENU_ITEM_HEIGHT_BUFFER = 20;
	
	public MainMenu(StateManager gsm)
	{
		this.gsm = gsm;
		
		try{
			bg = new Background("/backgrounds/menubg.gif", 1);
			bg.setVector(-0.1, 0);
			titleColor = new Color(128,0,0);
			titleFont = new Font("Century Gothic", Font.PLAIN, 28);
			font = new Font("Arial", Font.PLAIN, 12);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void init() {
	}

	public void update() {
		bg.update();
	}
	
	Vec2I getScreenCenter(){return new Vec2I(GamePanel.pixelSize.x/2, GamePanel.pixelSize.y/2);}
	
	AABB getBoundsForMenuItem(int a_index)
	{
		Graphics2D g = (Graphics2D)gsm.getPanel().getGraphics();
		Vec2I p = getScreenCenter();
		AABB r = new AABB(font.getStringBounds(options[a_index], g.getFontRenderContext()));
		p.x -= (int)(r.getWidth()/2);
		int itemHeight = (int)(MENU_ITEM_HEIGHT_BUFFER);
		p.y += (a_index+2)*itemHeight;
		r.translate(p);
		return r;
	}

	public void draw(Graphics2D g) {
		bg.draw(g);
		g.setColor(titleColor);
		g.setFont(titleFont);
		String title = "Platformer";
		Rectangle2D r = titleFont.getStringBounds(title, g.getFontRenderContext());
		Vec2I p = getScreenCenter();
		g.drawString(title, p.x - (int)r.getWidth()/2, p.y);
		
		// menu options
		g.setFont(font);
		r = font.getStringBounds(options[0], g.getFontRenderContext());
		AABB menuItemRect = null;
		for(int i = 0; i < options.length; ++i)
		{
			if(i == currentChoice){
				g.setColor(Color.BLACK);
			}else {
				g.setColor(Color.lightGray);
			}
			menuItemRect = getBoundsForMenuItem(i);
			g.drawString(options[i], (int)menuItemRect.min.x, (int)menuItemRect.max.y);
			//menuItemRect.draw(g);
		}
	}
	
	protected void select(){
		switch(currentChoice)
		{
		case 0:	gsm.setState(StateManager.LEVEL1STATE);	break;
		case 1:	break;
		case 2:	System.exit(0);	break;
		}
	}

	public void keyPressed(int k) {
		switch(k){
		case KeyEvent.VK_ENTER:	select();	break;
		case KeyEvent.VK_UP:	currentChoice--;	break;
		case KeyEvent.VK_DOWN:	currentChoice++;	break;
		}
		while(currentChoice < 0){currentChoice += options.length;}
		while(currentChoice >= options.length){currentChoice -= options.length;}
	}

	public void keyReleased(int k) {}
	public void mousePress(MouseEvent arg0) {
		Vec2I mouse = new Vec2I(arg0);
		mouse.divide(GamePanel.SCALE);
		for(int i = 0; i < options.length; ++i)
		{
			AABB r = getBoundsForMenuItem(i);
			if(r.contains(mouse)){
				if(currentChoice == i){
					select();
				} else {
					currentChoice = i;
				}
			}
		}
	}
	public void release(){}
}
