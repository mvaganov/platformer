package game.entity;

import engine.entity.Enemy;

public class Floaty extends Enemy {

	public void init() {
		moveSpeed = 0.3;
		maxSpeed = 0.3;
		canFall = false;
		fallSpeed = 0;
		maxFallSpeed = 10;
		health = maxHealth = 10;
		damage = 1;
		setSprite("/sprites/floaty.txt", 0);
	}
}
