package engine.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.GamePanel;
import engine.AABB;
import engine.HasRectangle;
import engine.Res;
import engine.Vec2D;
import engine.Vec2I;
import engine.imagemap.Sprite;
import engine.imagemap.SpriteResource;

/**
 * A visible object in the game world.
 * @author codeGiraffe
 *
 */
public abstract class Entity extends MapObject {
	public Sprite sprite;
	public Vec2I getPixelSize(){
		return new Vec2I(
				sprite.currentFrame.getWidth(),
				sprite.currentFrame.getHeight());
	}
	public static final int BOUNDING = -1, COLLISION = 0, DAMAGE = 1, HITBOX = 2;
	public static void onlyKeepCollisionType(ArrayList<HasRectangle> collidables, int type, AABB condition)
	{
		if(collidables != null){
			for(int i = 0; i < collidables.size(); ++i){
				Entity e;
				if(collidables.get(i) instanceof Entity){
					e = (Entity)collidables.get(i);
					if(!e.getRectangle(type).intersects(condition)){
						collidables.remove(i--);
					}
				}
			}
		}
	}
	public AABB getRectangle(int type) {
		SpriteResource.AnimationDescription srad = sprite.getAnimationDetails();
		AABB r = null;
		switch(type){
		case BOUNDING:	r = new AABB(srad.boundingRectangle);	break;
		case COLLISION:
		case DAMAGE:
		case HITBOX:
			if(srad.collisionRectangles != null
			&& srad.collisionRectangles.length > type){
				r = new AABB(srad.collisionRectangles[type]);
			}
			break;
		}
		if(type == HITBOX && r == null) {
			return getRectangle(COLLISION);
		}
		if(r != null){
			if(sprite.flipped)
				r.horizontalFlip();
			r.translate(getPos());
			return r;
		}
		return AABB.zero;
	}
	public AABB getRectangle(){
		return getRectangle(BOUNDING);
	}
	public AABB getBoundingRectangle(){
		return getRectangle(BOUNDING);
	}
	public AABB getCollisionRectangle(){
		return getRectangle(COLLISION);
	}
	public AABB getDamageRectangle(){
		return getRectangle(DAMAGE);
	}
	public AABB getHitRectangle(){
		return getRectangle(HITBOX);
	}
	public AABB getImageRectangle()
	{
		BufferedImage img = sprite.currentFrame;
		if(img != null)
		{
			double w2 = img.getWidth()/2, h2 = img.getHeight()/2;
			return new AABB(
					pos.x - w2, pos.y - h2,
					pos.x + w2, pos.y + h2);
		}
		return new AABB(
				pos.x, pos.y,
				pos.x, pos.y);
	}
	public int getCurrentState(){return sprite.getAnimationIndex();}
	public void setCurrentState(int state){
		if(sprite != null && state != sprite.getAnimationIndex()) {
			sprite.setState(state);
		}
	}
	
	public void setSprite(String filename, int state){
		SpriteResource sprRes = Res.getSprite(filename);
		if(sprite == null || sprite.getSource() != sprRes)
		{
			sprite = sprRes.create();
		}
		setCurrentState(state);
	}
	public Sprite getSprite(){
		return sprite;
	}

	public boolean notOnScreen() {
		BufferedImage f = sprite.currentFrame;
		if(f==null)return true;
		int w = f.getWidth(), h = f.getHeight();
		Vec2D mapPos = game.getOffset();
		return pos.x + mapPos.x + w < 0
			|| pos.x + mapPos.x - w > GamePanel.pixelSize.x
			|| pos.y + mapPos.y + h < 0
			|| pos.y + mapPos.y - h > GamePanel.pixelSize.y;
	}
	public void draw(Graphics2D g) {
		if(notOnScreen())
		{
			//System.out.println("not drawing "+this);
			return;
		}
		BufferedImage f = sprite.currentFrame;
		Vec2D mapPos = game.getOffset();
		if(f != null){ // don't draw if the image is still loading.
			sprite.position.x = (int)(pos.x + mapPos.x);
			sprite.position.y = (int)(pos.y + mapPos.y);
			sprite.draw(g);
		}
	}
	public void drawDebug(Graphics2D g)
	{
		// collision rectangle
		AABB r = getBoundingRectangle();
		g.setColor(Color.magenta);
		r.fill(g);
		// sprite rectangle
		r = getImageRectangle();
		g.setColor(Color.white);
		r.draw(g);
		
		SpriteResource.AnimationDescription srad = sprite.getAnimationDetails();
		AABB[] rec = srad.collisionRectangles;
		for(int i = 0; i < rec.length; ++i)
		{
			switch(i){
			case COLLISION:			g.setColor(Color.cyan);	break;
			case DAMAGE:			g.setColor(Color.red);	break;
			case HITBOX:			g.setColor(Color.green);	break;
			default:				g.setColor(Color.black);	break;
			}
			r.set(rec[i]);
			if(sprite.flipped){
				r.horizontalFlip();
			}
			r.translate(pos);
			r.draw(g);
		}
	}
	public void update(){
		sprite.update();
		if(!game.getPixelArea().contains(getCollisionRectangle()))
			shouldDispose = true;
	}
}
