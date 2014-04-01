package engine.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import engine.AABB;
import engine.HasRectangle;
import engine.Vec2D;

public abstract class MOB extends Entity {
	
	public static double MIN_COLLISION_RESOLUTION = 1/128.0f;

	protected Vec2D dir = new Vec2D();

	protected Vec2D nextPos = new Vec2D();
	protected boolean colliding, canFall = true;
	protected boolean left, right, up, down, jumping, falling;	
	protected double moveSpeed, maxSpeed, 
	stopSpeed, fallSpeed, maxFallSpeed, jumpStart, stopJumpSpeed;
	
	protected double footing;
	
	public static AABB getCollisionRectangle(HasRectangle hr){
		if(hr instanceof Entity){
			Entity e = (Entity)hr;
			return e.getCollisionRectangle();
		} else {
			return hr.getRectangle();
		}
	}
	
	void doRectCollision()
	{
		Vec2D collisionForce = new Vec2D();
		pos.add(dir);
		AABB tempRect = getCollisionRectangle();
		// ask the game what this is colliding with, and squeeze out of it!
		ArrayList<HasRectangle> collide = game.getCollisionsFor(this, tempRect);
		HasRectangle hr;
		AABB r;
		for(int i = 0; i < collide.size(); ++i)
		{
			hr = collide.get(i);
			r = getCollisionRectangle(hr);
			tempRect.squeezOutOf(r, collisionForce);
		}
		colliding = !collisionForce.isZero();
		// move back according to collision
		pos.add(collisionForce);
		// check vertical collision force
		double backPedalDistance = Math.abs(dir.y+collisionForce.y);
		if(collisionForce.y != 0 && backPedalDistance < MIN_COLLISION_RESOLUTION){
			if(dir.y > 0)
				this.falling = false;
			dir.y = 0;
		}
		// how much horizontal collision force is being applied?
		backPedalDistance = Math.abs(dir.x+collisionForce.x);
		if(collisionForce.x != 0 && backPedalDistance < MIN_COLLISION_RESOLUTION){
			dir.x = 0;
		}
		// check if this should be falling
		if(canFall && !falling){
			tempRect.min.y = tempRect.max.y;
			tempRect.max.y++;
			collide = game.getCollisionsFor(this, tempRect);
			if(collide.size() > 0){
				r = getCollisionRectangle(collide.get(0));
				footing = r.getWidth();
			} else {
				footing = 0;
				falling = true;
			}
		}
	}
	
	public void setVector(double dx, double dy) {
		this.dir.x = dx;
		this.dir.y = dy;
	}
	protected void calculateMotion() {
		Vec2D accel = new Vec2D();
		if(left) {
			accel.x -= maxSpeed;
		}if(right) {
			accel.x += maxSpeed;
		}
		if(!left && !right){
			if(dir.x > stopSpeed) {
				accel.x -= stopSpeed;
			} else if(dir.x < -stopSpeed) {
				accel.x += stopSpeed;
			} else {
				accel.x = -dir.x;
			}
		}
		dir.add(accel);
		// limit motion
		if(dir.x < -maxSpeed)		dir.x = -maxSpeed;
		else if(dir.x > maxSpeed)	dir.x = maxSpeed;
		if(falling) {
			dir.y += fallSpeed;
			if(dir.y > 0) jumping = false;
			if(dir.y < 0 && !jumping) dir.y += stopJumpSpeed;
			if(dir.y > maxFallSpeed) dir.y = maxFallSpeed;
		}
	}

	public void update() {
		super.update();
		if(shouldDispose())
			return;
		calculateMotion();
		doRectCollision();
	}
	
	public void drawDebug(Graphics2D g){
		super.drawDebug(g);
		AABB ir = getImageRectangle();
		AABB show = new AABB();
		g.setColor(Color.blue);
		if(left){
			show.set(ir.min.x, ir.min.y, ir.min.x+1, ir.max.y);
			show.draw(g);
		}if(right){
			show.set(ir.max.x-1, ir.min.y, ir.max.x, ir.max.y);
			show.draw(g);
		}if(up){
			show.set(ir.min.x, ir.min.y, ir.max.x, ir.min.y+1);
			show.draw(g);
		}if(down){
			show.set(ir.min.x, ir.max.y-1, ir.max.x, ir.max.y);
			show.draw(g);
		}if(jumping){
			show.set(ir.min.x, ir.min.y-ir.getHeight()/4, ir.max.x, ir.min.y-1);
			show.draw(g);			
		}if(falling){
			show.set(ir.min.x, ir.max.y+1, ir.max.x, ir.max.y+ir.getHeight()/4);
			show.draw(g);
		}
	}
	
	public void setLeft(boolean b){ left = b; if(b){sprite.flipped = b;} }
	public void setRight(boolean b){ right = b; if(b){sprite.flipped = !b;} }
	public void setUp(boolean b){ up = b; }
	public void setDown(boolean b){ down = b; }
	public void setJumping(boolean b){ jumping = b; }
}
