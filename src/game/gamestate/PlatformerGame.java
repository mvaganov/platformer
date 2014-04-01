package game.gamestate;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import main.GameApp;
import main.GamePanel;
import engine.Vec2D;
import engine.entity.Enemy;
import engine.entity.MapObject;
import engine.gamestate.PopulatedScrollingGame;
import game.entity.Explosion;
import game.entity.Player;

public abstract class PlatformerGame extends PopulatedScrollingGame {
	
	public static boolean isTestingPlayerStates = false;
	public Player player;
	protected PlayerHUD hud;
	
	public void release()
	{
		bgMusic.stop();
	}

	public void init(String map, String bg){
		super.init(map, bg);
		player = new Player();
		player.init(this);
		player.setPosition(initialLocation);
		addEntity(player, true);
		hud = new PlayerHUD(player);
	}
	
	public void update() {
		super.fixedUpdate();
		MapObject mobj;
		int objectsToCheck = mobjects.size();
		for(int i = 0; i < objectsToCheck; ++i){
			mobj = mobjects.get(i);
			if(mobj.shouldDispose()){
				if(mobj == player){
					player.setPosition(initialLocation);
					player.init(this);
				}
				if(mobj instanceof Enemy){
					Explosion explode = new Explosion(mobj.getPos());
					explode.init(this);
					addEntity(explode, false);
				}
			}
		}
		Vec2D pos = player.getPos();
		setPosition(
				GamePanel.pixelSize.x / 2 - pos.x,
				GamePanel.pixelSize.y / 2 - pos.y);
		bg.setPosition(getOffset().x, getOffset().y);
		super.lateUpdate();
	}

	public void draw(Graphics2D g)
	{
		super.draw(g);
		hud.draw(g);		
	}

	public void keyPressed(int k) {
		switch(k){
		case KeyEvent.VK_LEFT:	player.setLeft(true);	break;
		case KeyEvent.VK_RIGHT:	player.setRight(true);	break;
		case KeyEvent.VK_UP:	player.setUp(true);	break;
		case KeyEvent.VK_DOWN:	player.setDown(true);	break;
		case KeyEvent.VK_W:	player.setJumping(true);	break;
		case KeyEvent.VK_E:	player.setGliding(true);	break;
		case KeyEvent.VK_R:	player.setSwiping();	break;
		case KeyEvent.VK_F:	player.setFiring();	break;
		case KeyEvent.VK_ESCAPE:	System.exit(0);	break;
		case KeyEvent.VK_F1:	
			GameApp.DEBUG = !GameApp.DEBUG;
			isTestingPlayerStates = false;
			break;
		}
		if(GameApp.DEBUG){
			isTestingPlayerStates = false;
			switch(k){
			case KeyEvent.VK_0:	
			case KeyEvent.VK_1:	
			case KeyEvent.VK_2:	
			case KeyEvent.VK_3:	
			case KeyEvent.VK_4:	
			case KeyEvent.VK_5:	
			case KeyEvent.VK_6:	
			case KeyEvent.VK_7:	
			case KeyEvent.VK_8:	
			case KeyEvent.VK_9:	
				isTestingPlayerStates = true;
				System.out.println("set state "+(k-KeyEvent.VK_0));
				player.setCurrentState(k-KeyEvent.VK_0);
				break;
			case KeyEvent.VK_BACK_QUOTE:
				System.out.println("reloading...");
				player.getSprite().getSource().reload();
				System.out.println("...reloaded");
				break;
			}
		}
	}

	public void keyReleased(int k) {
		switch(k){
		case KeyEvent.VK_LEFT:	player.setLeft(false);	break;
		case KeyEvent.VK_RIGHT:	player.setRight(false);	break;
		case KeyEvent.VK_UP:	player.setUp(false);	break;
		case KeyEvent.VK_DOWN:	player.setDown(false);	break;
		case KeyEvent.VK_W:	player.setJumping(false);	break;
		case KeyEvent.VK_E:	player.setGliding(false);	break;
		}
	}
	public void mousePress(MouseEvent e){}
}
