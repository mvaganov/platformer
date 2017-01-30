package engine;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

public class Vec2D extends Point2D.Double {
	/** */
	private static final long serialVersionUID = -8275467875663881762L;
	public static final Vec2D zero = new Vec2D(0,0);

	public Vec2D(double x, double y) {super(x,y);}
	public Vec2D(){}
	public Vec2D(Point2D v){x=v.getX();y=v.getY();}
	
	public Vec2D sum(Point2D v)		{return new Vec2D(x+v.getX(), y+v.getY());}
	public Vec2D difference(Point2D v){return new Vec2D(x-v.getX(), y-v.getY());}
	public Vec2D product(Point2D v)	{return new Vec2D(x*v.getX(), y*v.getY());}
	public Vec2D quotient(Point2D v)	{return new Vec2D(x/v.getX(), y/v.getY());}
	public void add(Point2D v)		{x += v.getX(); y += v.getY();}
	public void subtract(Point2D v)	{x -= v.getX(); y -= v.getY();}
	public void multiply(Point2D v)	{x *= v.getX(); y *= v.getY();}
	public void divide(Point2D v)		{x /= v.getX(); y /= v.getY();}
	
	public Vec2D product(double d)	{return new Vec2D(x*d, y*d);}
	public Vec2D quotient(double d)	{return new Vec2D(x/d, y/d);}
	public void multiply(double d)	{x *= d; y *= d;}
	public void divide(double d)	{x /= d; y /= d;}
	public void set(double x, double y) {setLocation(x, y);}
	public void set(Point2D v) {setLocation(v);}
	public void drawTo(Graphics2D g, Vec2D destination){
		g.drawLine((int)x, (int)y, (int)destination.x, (int)destination.y);
	}
	public void clapToInt(){ x = (int)x; y = (int)y; }
	public void roundToInt() { x = (int)(x+0.5); y = (int)(y+0.5); }
	public double magnitudeSq(){return x*x+y*y;}
	public double magnitude(){return Math.sqrt(magnitudeSq());}
	public void normalize(){double m=magnitude();x/=m;y/=m;}
	public double magnitudeManhattan(){return Math.abs(x)+Math.abs(y);}
	public boolean isZero() {return x == 0 && y == 0;}
}
