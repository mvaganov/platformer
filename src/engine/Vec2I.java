package engine;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

public class Vec2I extends Point {
	/** */
	private static final long serialVersionUID = 2365733506723414130L;

	public Vec2I(int x, int y) {super(x,y);}
	public Vec2I(double x, double y) {super((int)x,(int)y);}
	public Vec2I() {}
	public Vec2I(int[] nums){x=nums[0];y=nums[1];}
	public Vec2I(Point2D v){x=(int)v.getX();y=(int)v.getX();}
	public Vec2I(MouseEvent e){x=e.getX();y=e.getY();}

	public Vec2I sum(Point2D v)		{return new Vec2I(x+v.getX(), y+v.getY());}
	public Vec2I difference(Point2D v){return new Vec2I(x-v.getX(), y-v.getY());}
	public Vec2I product(Point2D v)	{return new Vec2I(x*v.getX(), y*v.getY());}
	public Vec2I quotient(Point2D v){return new Vec2I(x/v.getX(), y/v.getY());}
	public void add(Point2D v)		{x += v.getX(); y += v.getY();}
	public void subtract(Point2D v)	{x -= v.getX(); y -= v.getY();}
	public void multiply(Point2D v)	{x *= v.getX(); y *= v.getY();}
	public void divide(Point2D v)	{x /= v.getX(); y /= v.getY();}

	public Vec2I product(double d)	{return new Vec2I((int)(x*d), (int)(y*d));}
	public void multiply(double d)	{x *= d; y *= d;}
	public Vec2I quotient(double d)	{return new Vec2I((int)(x/d), (int)(y/d));}
	public void divide(double d)	{x /= d; y /= d;}
	public void set(int x, int y) {this.x=x;this.y=y;}
	public void set(Vec2I v) {set(v.x,v.y);}
	public boolean isZero(){return x==0 && y==0;}
}
