package engine.imagemap;

import java.awt.image.BufferedImage;

import engine.AABB;
import engine.Vec2D;
import engine.Vec2I;

public class Tile {
	private BufferedImage image;
	private int type, symbol;
	private String extraData;
	
	public AABB boundingFor(int row, int col, Vec2I tileSize){
		Vec2D pos = new Vec2D(tileSize.x*col, tileSize.y*row);
		Vec2D size = getCollisionSize();
		return new AABB(pos,pos.sum(size));
	}
	
	public static final int NONE = 0;
	public static final int NORMAL = 1;
	public static final int BLOCKED = 2;
	public static final int ENEMY_SPAWN_POINT = 3;
	public static final int PLAYER_SPAWN_POINT = 4;
	
	public Vec2D getCollisionSize(){
		return new Vec2D(image.getWidth(), image.getHeight());
	}

	public Tile(BufferedImage image, int type, int charRepresentation) {
		this.image = image;
		this.type = type;
		this.symbol = charRepresentation;
	}

	public Tile(BufferedImage image) {
		this.image = image;
		this.type = NONE;
	}
	public void setImage(BufferedImage img){image = img;}
	public BufferedImage getImage() {return image;}
	public int getType(){return type;}
	public void setType(int t){type = t;}
	public int getSymbol(){return symbol;}
	public String getExtraData(){return extraData;}
	public void setExtraData(String data){extraData=data;}
}
