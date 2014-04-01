package game.entity;

import java.util.ArrayList;

import engine.AABB;
import engine.HasRectangle;
import engine.entity.Enemy;
import engine.entity.Entity;
import engine.entity.Mortal;
import game.gamestate.PlatformerGame;

public class Player extends Mortal {
	private int fire, maxFire;
	
	private boolean firing;
	private int fireCost;
	private int fireBallDamage;
	
	private boolean swiping;
	private int swipeDamage;
	private boolean gliding;
	private double baseFallSpeed, glideFallSpeed, baseMaxFall, glideMaxFall;
	
	private static final int IDLE = 0, 
			WALKING = 1, JUMPING = 2, FALLING = 3,
			GLIDING = 4, FIREBALL = 5, SCRATCHING = 6, OUCH = 7;
	
	protected void init() {
		setSprite("/sprites/player.txt", 0);
		moveSpeed = 0.3;
		maxSpeed = 1.6;
		stopSpeed = 0.4;
		baseFallSpeed = 0.15;
		glideFallSpeed = 0.01;
		baseMaxFall = 4.0;
		glideMaxFall = 1;
		jumpStart = -4.8;
		stopJumpSpeed = 0.3;
		setFlichDurationMS(1000);
		setFlinchFlashSpeedMS(100);
		health = maxHealth = 5;
		fire = maxFire = 2500;
		fireCost = 200;
		fireBallDamage = 5;
		swipeDamage = 8;
		shouldDispose = false;
		setCurrentState(IDLE);
		hit(0);
	}

	public int getFire() { return fire;}
	public int getMaxFire() {return maxFire;}
	public void setFiring() {
		firing = true;
	}
	public void setSwiping() {
		swiping = true;
	}
	public void setGliding(boolean b) {
		gliding = b;
	}
	
	public void checkDamaged() {
		if(!isFlinching()){
			Enemy e;
			ArrayList<HasRectangle> mobjects =
					game.getEntitiesAt(getHitRectangle(), Entity.DAMAGE);
			for(int i = 0; i < mobjects.size(); ++i)
			{
				if(mobjects.get(i) instanceof Enemy)
				{
					e = (Enemy)mobjects.get(i);
					if(!e.isFlinching()){
						this.setCurrentState(OUCH);
					}
					hit(e.getDamage());
					break;	// only 1 hit at a time
				}
			}
		}
	}
	
	public void checkAttack() {
		Enemy e;
		AABB attackArea = new AABB();
		attackArea = getDamageRectangle();
		ArrayList<HasRectangle> mobjects = game.getEntitiesAt(attackArea, Entity.HITBOX);
		for(int i = 0; i < mobjects.size(); ++i)
		{
			if(mobjects.get(i) instanceof Enemy)
			{
				e = (Enemy)mobjects.get(i);
				e.hit(swipeDamage);
				break;	// only 1 enemy gets hit per swipe
			}
		}
	}
	
	protected void calculateMotion() {
		boolean fallGlide = gliding && dir.y > 0;
		fallSpeed = (fallGlide)?glideFallSpeed:baseFallSpeed;
		maxFallSpeed = (fallGlide)?glideMaxFall:baseMaxFall;
		super.calculateMotion();
		if((sprite.getCurrentState() == SCRATCHING
		|| sprite.getCurrentState() == FIREBALL)
		&& !(jumping || falling)) {
			dir.x = 0;
		}
		if(jumping && !falling) {
			dir.y = jumpStart;
			falling = true;
		}
	}
	
	public void update() {
		super.update();
		if(shouldDispose())
			return;
		checkDamaged();
		int currentAction = getCurrentState();
		if(currentAction == SCRATCHING) {
			if(sprite.getRepetitions() > 0)
				swiping = false;
		}
		if(currentAction == FIREBALL) {
			if(sprite.getRepetitions() > 0)
				firing = false;
		}
		fire += 1;
		if(fire > maxFire) fire = maxFire;
		int lastAction = currentAction;
		if(swiping) {
			if(currentAction != SCRATCHING) {	currentAction = SCRATCHING;	}
			checkAttack();
		}else if(firing) {
			if(currentAction != FIREBALL) {	currentAction = FIREBALL;	}
		}else if(dir.y > 0) {
			if(gliding) {
				if(currentAction != GLIDING) {
					currentAction = GLIDING;	
				}
			} else if(currentAction != FALLING) {	currentAction = FALLING;	}
		}else if(dir.y < 0) {
			if(currentAction != JUMPING) {	currentAction = JUMPING;	}
		}else if(left || right) {
			if(currentAction != WALKING) {	currentAction = WALKING;	}
		} else {
			if(currentAction != IDLE && !PlatformerGame.isTestingPlayerStates) {
				if(currentAction != OUCH || sprite.getRepetitions() > 5)
					currentAction = IDLE;
			}
		}
		if(lastAction != currentAction){
			setCurrentState(currentAction);
		}

		if(firing && currentAction == FIREBALL && lastAction != FIREBALL) {
			if(fire > fireCost) {
				fire -= fireCost;
				FireBall fb = new FireBall(sprite.flipped, fireBallDamage);
				fb.init(game);
				AABB r = getDamageRectangle();
				fb.setPosition(r.getCenter());
				game.addEntity(fb, true);
			}
		}
	}
}
