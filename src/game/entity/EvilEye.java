package game.entity;

import engine.entity.Enemy;

public class EvilEye extends Enemy {
	public void init() {
		moveSpeed = 0.3;
		maxSpeed = 0.3;
		fallSpeed = 0.2;
		maxFallSpeed = 10;
		health = maxHealth = 12;
		damage = 1;
		setSprite("/sprites/baddie.txt", 0);
		right = true;
	}
	public void update() {
		super.update();
		double width = getCollisionRectangle().getWidth();
		if(footing < width/2)
		{
			dir.x = 0;
		}
		if(dir.x == 0)
		{
			if(right) {
				setRight(false);
				setLeft(true);
			} else if(left) {
				setRight(true);
				setLeft(false);
			}
		}
	}
}
