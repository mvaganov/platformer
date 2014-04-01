package game.entity;

import java.awt.geom.Point2D;

import engine.entity.Entity;

public class Explosion extends Entity {
	public Explosion(Point2D pos) {
		this.pos.setLocation(pos);
		setSprite("/sprites/explosion.txt", 0);
	}
	public void update() {
		super.update();
		if(sprite.getRepetitions() > 0){
			shouldDispose = true;
		}
	}
	protected void init() {}
}
