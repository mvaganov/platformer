package game.entity;

import java.awt.geom.Point2D;

import engine.entity.Entity;

public class HitParticle extends Entity {
	public HitParticle(Point2D pos) {
		this.pos.setLocation(pos);
		setSprite("/sprites/hit.txt", 0);
	}
	public void update() {
		super.update();
		if(sprite.getRepetitions() > 0){
			shouldDispose = true;
		}
	}
	protected void init() {}
}
