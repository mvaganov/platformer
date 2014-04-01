package game.entity;

import java.util.ArrayList;

import engine.AABB;
import engine.HasRectangle;
import engine.entity.Enemy;
import engine.entity.Entity;
import engine.entity.MOB;

public class FireBall extends MOB {
	public static final int FIREBALL_ACTIVE = 0;
	public static final int EXPLODE = 1;
	
	int damage;
	
	public FireBall(boolean flipped, int damage) {
		this.damage = damage;
		moveSpeed = 3.8;
		maxSpeed = 3.8;
		canFall = false;
		this.right = !flipped;
		this.left = flipped;
		if(flipped) dir.x = -moveSpeed;
		else dir.x = moveSpeed;
		setSprite("/sprites/fireball.txt", 0);
		sprite.flipped = flipped;
	}
	public void setHit() {
		if(getCurrentState() == EXPLODE) return;
		setCurrentState(EXPLODE);
		left = false;
		right = false;
		dir.x = 0;
	}
	public void update() {
		super.update();
		switch(getCurrentState()){
		case FIREBALL_ACTIVE:
		{
			AABB dmgRect = getDamageRectangle();
			ArrayList<HasRectangle> list = game.getEntitiesAt(dmgRect, Entity.HITBOX);
			int count = list.size();
			HasRectangle rect;
			for(int f = 0; f < count; ++f) {
				rect = list.get(f);
				if(rect instanceof Enemy)
				{
					Enemy e = (Enemy)rect;
					if(dmgRect.intersects(e.getHitRectangle())) {
						e.hit(damage);
						setHit();
						break;
					}
				}
			}
			if(colliding || dir.x == 0){
				setHit();
			}
		}
			break;
		case EXPLODE:
			if(sprite.getRepetitions() > 0)
				shouldDispose = true;
			break;
		}
	}
	protected void init(){}
}
