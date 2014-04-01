package engine;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
/**
 * Axis Aligned Bounding Box
 * @author codeGiraffe
 *
 */
public class AABB implements HasRectangle {
	public static AABB zero = new AABB(0,0,0,0);
	public Vec2D min = new Vec2D(), max = new Vec2D();
	public void set(double minx, double miny, double maxx, double maxy){
		min.x = minx;	min.y = miny;
		max.x = maxx;	max.y = maxy;
	}
	public void set(Vec2D min, Vec2D max){
		this.min.set(min);
		this.max.set(max);
	}
	public AABB(double minx, double miny, double maxx, double maxy){
		set(minx,miny,maxx,maxy);
	}
	public AABB(Rectangle2D r){
		set(r.getMinX(),r.getMinY(),r.getMaxX(),r.getMaxY());
	}
	public AABB(AABB r){
		min.set(r.min);
		max.set(r.max);
	}
	public AABB(){}
	public AABB(Vec2D min, Vec2D max){
		set(min, max);
	}
	public static AABB createFrom(Point2D center, Point2D dimensions){
		Vec2D half = new Vec2D(dimensions.getX()/2, dimensions.getY()/2);
		return new AABB(center.getX()-half.x, center.getY()-half.y,
						center.getX()+half.x, center.getY()+half.y);
	}
	public boolean intersects(AABB r){
		if(!isValid())return false;
		return 
			!( min.x >= r.max.x || max.x <= r.min.x
			|| min.y >= r.max.y || max.y <= r.min.y);
	}
	
	public boolean contains(Point2D p)
	{
		return p.getX() >= min.x && p.getX() < max.x
			&& p.getY() >= min.y && p.getY() < max.y;
	}
	public void inset(double inset){
		Vec2D in = new Vec2D(inset,inset);
		min.add(in);
		max.subtract(in);
	}
	public double getWidth(){return max.x-min.x;}
	public double getHeight(){return max.y-min.y;}
	/** width/height vector */
	public Vec2D getSize() {return new Vec2D(getWidth(), getHeight());}
	public void draw(Graphics2D g) {
		g.drawRect((int)min.x, (int)min.y, 
				(int)getWidth()-1, (int)getHeight()-1);
	}
	public void fill(Graphics2D g) {
		g.fillRect((int)min.x, (int)min.y, 
				(int)getWidth(), (int)getHeight());
	}
	public void translate(Point2D delta){
		min.add(delta);
		max.add(delta);
	}
	public void translate(double dx, double dy){
		Vec2D delta = new Vec2D(dx,dy);
		translate(delta);
	}
	public void clapToInt() {
		min.clapToInt();
		max.clapToInt();
	}
	public void roundToInt() {
		min.roundToInt();
		max.roundToInt();
	}
	public void set(AABB r) {
		min.set(r.min);
		max.set(r.max);
	}
	public String toString(){
		return "[min("+min.x+","+min.y+"), max("+max.x+","+max.y+"), w/h("+(int)getWidth()+","+(int)getHeight()+")]";
	}
	public Vec2D getCenter(){
		Vec2D c = min.sum(max);
		c.divide(2);
		return c;
	}
	public boolean contains(AABB r) {
		return min.x <= r.min.x && max.x >= r.max.x
			&& min.y <= r.min.y && max.y >= r.max.y;
	}
	public AABB getRectangle(){return this;}
	public boolean isValid(){return getWidth() > 0 && getHeight() > 0;}
	/**
	 * @param r
	 * @return where this and r overlap
	 */
	public AABB getUnion(AABB r){
		if(!intersects(r))	return null;
		AABB union = new AABB(
				Math.max(min.x, r.min.x),Math.max(min.y, r.min.y),
				Math.min(max.x, r.max.x),Math.min(max.y, r.max.y));		
		return union.isValid()?union:null;
	}
	
	public void add(AABB r){
		if(r.min.x < min.x)	min.x = r.min.x;
		if(r.min.y < min.y)	min.y = r.min.y;
		if(r.max.x > max.x)	max.x = r.max.x;
		if(r.max.y > max.y)	max.y = r.max.y;
	}
	public void add(Point2D p)
	{
		if(p.getX() < min.x)	min.x = p.getX();
		if(p.getY() < min.y)	min.y = p.getY();
		if(p.getX() > max.x)	max.x = p.getX();
		if(p.getY() > max.y)	max.y = p.getY();
	}
	public boolean canMerge(AABB r){
		return (min.y == r.min.y && max.y == r.max.y && (min.x == r.max.x || max.x == r.min.x))
			|| (min.x == r.min.x && max.x == r.max.x && (min.y == r.max.y || max.y == r.min.y));
	}
	
	public static void merge(ArrayList<AABB> list){
		AABB ra, rb;
		for(int a = 0; a < list.size(); ++a){
			ra = list.get(a);
			if(!ra.isValid()){
				list.remove(a);
				--a;
				continue;
			}
			for(int b = a+1; b < list.size(); ++b){
				rb = list.get(b);
				if(ra.canMerge(rb)){
					ra.add(rb);
					list.remove(b);
					--b;
				}
			}
		}
	}
	/** move this rectangle assuming, this rectangle is the unit grid size */
	public void gridTranslate(int colTranslate, int rowTranslate) {
		Vec2D delta = new Vec2D(getWidth()*colTranslate, getHeight()*rowTranslate);
		translate(delta);
	}
	/** a new rectangle that would be translated, assuming this rectangle is the unit grid size */
	public AABB getGridTranslated(int colTranslate, int rowTranslate) {
		AABB moved = new AABB(this);
		moved.gridTranslate(colTranslate, rowTranslate);
		return moved;
	}
	public static void draw(Graphics2D g, ArrayList<AABB> list){
		if(list == null || list.size() == 0)
			return;
		for(int i = 0; i < list.size(); ++i){
			list.get(i).draw(g);
		}
	}
	public static void fill(Graphics2D g, ArrayList<AABB> list){
		if(list == null || list.size() == 0)
			return;
		for(int i = 0; i < list.size(); ++i){
			list.get(i).fill(g);
		}
	}
	/** collision with the North side of the rectangle */
	public static final int N=0;
	/** collision with the West side of the rectangle */
	public static final int W=1;
	/** collision with the South side of the rectangle */
	public static final int S=2;
	/** collision with the East side of the rectangle */
	public static final int E=3;
	/** @return {@link #N},{@link #W},{@link #S},{@link #E}*/
	public int squeezOutOf(AABB r, Vec2D a_out)
	{
		// up, left, down, right
		double[] squeeze = {max.y-r.min.y, max.x-r.min.x, r.max.y-min.y, r.max.x-min.x};
		int collidingSide = 0;
		for(int i = 0; i < squeeze.length; ++i)
		{
			if(squeeze[i] < squeeze[collidingSide])
				collidingSide = i;
		}
		double dx = 0, dy = 0;
		switch(collidingSide)
		{
		case N:	dy = -squeeze[0];break;
		case W:	dx = -squeeze[1];break;
		case S:	dy = squeeze[2];break;
		case E:	dx = squeeze[3];break;
		}
		if(dx != 0 || dy != 0)
		{
			if(a_out != null)
			{
				a_out.x += dx;
				a_out.y += dy;
			}
			translate(dx, dy);
			return collidingSide;
		}
		return -1;
	}
	public void multiply(double d) {
		min.multiply(d);
		max.multiply(d);
	}
	public void multiply(Point2D v) {
		min.multiply(v);
		max.multiply(v);
	}
	private static Vec2I hflip = new Vec2I(-1,1);
	public void horizontalFlip(){
		multiply(hflip);
		correctNegative();
	}
	public void correctNegative()
	{
		double t;
		if(min.x > max.x){t=min.x; min.x=max.x; max.x=t;}
		if(min.y > max.y){t=min.y; min.y=max.y; max.y=t;}
	}
}
