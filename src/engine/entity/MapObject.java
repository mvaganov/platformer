package engine.entity;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import engine.AABB;
import engine.HasRectangle;
import engine.Vec2D;
import engine.gamestate.PopulatedScrollingGame;

/**
 * The most basic thing that can exist on the map.
 * Has a position and some kind of area can be accessed
 * @author codeGiraffe
 */
public abstract class MapObject implements HasRectangle {
	protected Vec2D pos = new Vec2D();
	protected boolean shouldDispose;

	protected PopulatedScrollingGame game;
	public void init(PopulatedScrollingGame g){
		game = g;
		init();
	}
	public boolean shouldDispose() { return shouldDispose; }
	public Vec2D getPos(){return pos;}
	public void setPosition(double x, double y) {pos.set(x,y);}
	public void setPosition(Point2D pos) {this.pos.set(pos);}
	abstract protected void init();
	abstract public AABB getCollisionRectangle();
	abstract public AABB getRectangle();
	abstract public void update();
	abstract public void draw(Graphics2D g);
	abstract public void drawDebug(Graphics2D g);
}
