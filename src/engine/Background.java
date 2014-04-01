package engine;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import main.GamePanel;

public class Background {

	private BufferedImage image;
	private Vec2I size = new Vec2I();
	private Vec2D offset = new Vec2D();
	private Vec2D d = new Vec2D();

	private double moveScale;

	public Background(String s, double moveScale) {
		image = Res.getImage(s);
		size.move(image.getWidth(), image.getHeight());
		this.moveScale = moveScale;
	}

	public void setPosition(double x, double y) {
		offset.x = (x * moveScale);
		offset.y = (y * moveScale);
		fixPosition();
	}

	public void setVector(double dx, double dy) {
		d.x = dx;
		d.y = dy;
	}

	public void update() {
		offset.x += d.x;
		offset.y += d.y;
		fixPosition();
	}

	private void fixPosition() {
		while (offset.x <= -size.x)	offset.x += size.x;
		while (offset.x >= size.x)	offset.x -= size.x;
		while (offset.y <= -size.y)	offset.y += size.y;
		while (offset.y >= size.y)	offset.y -= size.y;
		double verticalOverreach = GamePanel.HEIGHT-(image.getHeight() + offset.y);
		double verticalUnderreach = offset.y;
		if(verticalOverreach > 0){
			offset.y += verticalOverreach;
		}
		if(verticalUnderreach > 0)
		{
			offset.y -= verticalUnderreach;
		}
}

	public void draw(Graphics2D g) {

		g.drawImage(image, (int) offset.x, (int) offset.y, null);

		if (offset.x < 0) {
			g.drawImage(image, (int) offset.x + image.getWidth(),
					(int) offset.y, null);
		}
		if (offset.x > 0) {
			g.drawImage(image, (int) offset.x - image.getWidth(),
					(int) offset.y, null);
		}
	}

}