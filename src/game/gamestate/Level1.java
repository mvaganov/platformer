package game.gamestate;

import engine.gamestate.StateManager;

public class Level1 extends PlatformerGame {
	
	public Level1(StateManager gsm) {
		this.gsm = gsm;
		init();
	}

	public void init() {
		super.init("/maps/map1_1.txt", "/backgrounds/grassbg1.gif");
	}
}
