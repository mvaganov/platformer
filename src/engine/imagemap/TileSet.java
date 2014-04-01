package engine.imagemap;

import java.awt.image.BufferedImage;
import java.util.Vector;

import engine.Res;
import engine.Vec2I;

public class TileSet {
	
	public static class Description
	{
		public Vec2I frameSize;
		public int frameCount;
		public Description(int fCount, Vec2I fSize){
			frameCount = fCount;
			this.frameSize = fSize;
		}
	}
	
	private BufferedImage[][] tiles;
	
	public BufferedImage[][] getTiles(){return tiles;}

	/**
	 * @param s name of sprite sheet
	 * @param frameLoadInfo how big and how many frames are in each frame set
	 */
	public void load(String s, Description[] frameLoadInfo)
	{
		BufferedImage spritesheet = Res.getImage(s);
		Vec2I frameSize;
		Vec2I cursor = new Vec2I();
		tiles = new BufferedImage[frameLoadInfo.length][];
		for(int row = 0; row < frameLoadInfo.length; ++row) {
			Description fData = frameLoadInfo[row];
			frameSize = fData.frameSize;
			tiles[row] = getFrames(spritesheet, frameSize, cursor, fData.frameCount);
		}
		Res.unload(s);
	}
	
	/**
	 * @param spritesheet
	 * @param frameSize
	 * @param cursor
	 * @param count if less than 0, make as many frames as possible
	 * @return
	 */
	public BufferedImage[] getFrames(BufferedImage spritesheet, Vec2I frameSize,
			Vec2I cursor, int count)
	{
		Vector<BufferedImage> frames = new Vector<BufferedImage>();
		cursor.x = 0;
		int i = 0;
		boolean loadEverything = count < 0;
		while(loadEverything || i < count)
		{
			if(cursor.x + frameSize.x > spritesheet.getWidth()){
				cursor.x = 0;
				cursor.y += frameSize.y;
				if(cursor.y + frameSize.y > spritesheet.getHeight())
					break;
			}
			BufferedImage img = spritesheet.getSubimage(
					cursor.x, cursor.y, frameSize.x, frameSize.y);
			frames.add(img);
			cursor.x += frameSize.x;
			++i;
		}
		if(!loadEverything && cursor.y + frameSize.y > spritesheet.getWidth())
			throw new IllegalStateException("not enough image!");
		cursor.y += frameSize.y;
		BufferedImage[] result = new BufferedImage[frames.size()];
		for(int f=0;f<frames.size();++f){
			result[f] = frames.get(f);
		}
		return result;
	}
}
