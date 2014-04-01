package engine.entity;

import java.awt.Color;
import java.awt.Graphics2D;

import engine.AABB;
import engine.Timer;
import engine.Vec2D;

public class FloatingText extends MapObject {
	public String text;
	public Color color;
	public double pxUpPerSecond;
	public int ms, timer;
	
	public FloatingText(Vec2D pos, String text, Color color, int durationMs, double pxUpPerSecond){
		this.pos = pos;
		this.text = text;
		this.color = color;
		this.ms = durationMs;
		this.pxUpPerSecond = pxUpPerSecond;
	}
	
	public AABB getCollisionRectangle() {return null;}
	public AABB getRectangle()
	{
		Graphics2D g = (Graphics2D)game.getGameStateManager().getPanel().getGraphics();
		AABB r = new AABB(g.getFont().getStringBounds(text, g.getFontRenderContext()));
		Vec2D p = new Vec2D(pos);
		p.y -= timer * pxUpPerSecond / 1000.0;
		r.translate(p);
		return r;
	}
	public void update() {
		timer += Timer.deltaTimeMS;
		if(ms > 0){
			if(timer >= ms){
				shouldDispose = true;
			}
		}
	}
	public void draw(Graphics2D g) {
		Vec2D p2 = new Vec2D(pos);
		p2.add(game.getOffset());
		p2.y -= timer * pxUpPerSecond / 1000.0;
		g.setColor(color);
		g.drawString(text, (int)p2.x, (int)p2.y);
	}
	public void drawDebug(Graphics2D g) {
		getRectangle().draw(g);
	}
	protected void init() {}
}
