package engine.imagemap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import engine.AABB;
import engine.AudioPlayer;
import engine.Res;
import engine.Vec2I;

public class SpriteResource extends TileSet {
	public static class AnimationDescription extends TileSet.Description{
		public int delay;
		public String name;
		public Vec2I offset;
		public boolean loop;
		public AudioPlayer sound;
		public AABB[] collisionRectangles;
		public AABB boundingRectangle;
		public AnimationDescription(int fCount, Vec2I fSize, 
				int fDelay, String name, Vec2I offset, boolean loop,
				AudioPlayer sound, AABB[] collisionRectangles){
			super(fCount, fSize);
			this.delay = fDelay;
			this.name = name;
			this.offset = offset;
			this.loop = loop;
			this.sound = sound;
			this.collisionRectangles = collisionRectangles;
			if(collisionRectangles == null || collisionRectangles.length == 0){
				boundingRectangle = AABB.zero;
			} else {
				boundingRectangle = new AABB();
				for(int i = 0; i < collisionRectangles.length; ++i){
					boundingRectangle.add(collisionRectangles[i]);
				}
			}
		}
	}
	private String filename;
	
	private AnimationDescription[] animations;
	
	public AnimationDescription[] getAnimationData(){return animations;}
	
	public String getFilename(){return filename;}
	
	public void reload(){loadSpriteFile(filename);}
	
	/**
	 * @param s name of sprite sheet
	 * @param animations animation data
	 */
	public void load(String s, AnimationDescription[] animations)
	{
		this.animations = animations;
		super.load(s, animations);
	}
	
	public SpriteResource(String s){
		loadSpriteFile(s);
	}

	public void loadSpriteFile(String filename)
	{
		this.filename = filename;
		String imgFilename;
		Vector<int[]> rows = new Vector<int[]>();
		Vector<String> names = new Vector<String>();
		Vector<Boolean> loop = new Vector<Boolean>();
		Vector<String> sounds = new Vector<String>();
		Vector<Vec2I> imgoffsets = new Vector<Vec2I>();
		Vector<AABB[]> collisionRects = new Vector<AABB[]>();
		Vector<Float> decibleGainAdjust = new Vector<Float>();
		try{
			InputStream in = getClass().getResourceAsStream(filename);
			BufferedReader br = new BufferedReader(
					new InputStreamReader(in));
			imgFilename = br.readLine();
			String[] tokens;
			int[] row;
			do{
				tokens = Res.getLineTokens(br);
				int ti = 0;
				if(tokens != null && tokens.length > 1){
					row = new int [4];
					for(int col = 0; col < row.length; col++) {
						row[col] = Integer.parseInt(tokens[col]);
					}
					ti+= 4;
					rows.add(row);
					names.add(tokens[ti++]);
					if(tokens.length > ti){
						loop.add(Integer.parseInt(tokens[ti++]) == 1);
						String soundFilename = tokens[ti++];
						if(soundFilename.equals("0"))
							soundFilename = null;
						sounds.add(soundFilename);
						decibleGainAdjust.add(Float.parseFloat(tokens[ti++]));
						imgoffsets.add(new Vec2I(
						Integer.parseInt(tokens[ti]),Integer.parseInt(tokens[ti+1])));
						ti += 2;
						int numRects = Integer.parseInt(tokens[ti++]);
						AABB[] crects = new AABB[numRects];
						for(int r = 0; r < numRects; ++r)
						{
							crects[r] = new AABB(
									Integer.parseInt(tokens[ti+0]),
									Integer.parseInt(tokens[ti+1]),
									Integer.parseInt(tokens[ti+2]),
									Integer.parseInt(tokens[ti+3]));
							ti += 4;
						}
						collisionRects.add(crects);
					}else{
						loop.add(false);
						sounds.add("");
						imgoffsets.add(new Vec2I());
						collisionRects.add(new AABB[0]);
					}
				}
			}while(tokens != null);
			AnimationDescription[] aData = new AnimationDescription[rows.size()];
			for(int i = 0; i < aData.length; ++i){
				row = rows.get(i);
				AudioPlayer ap = Res.getSound(sounds.get(i));
				if(ap != null)
				{
					ap.adjustGain(decibleGainAdjust.get(i));
				}
				aData[i] = new AnimationDescription(
						row[0], new Vec2I(row[1], row[2]), 
						row[3], names.get(i), 
						imgoffsets.get(i), loop.get(i),
						ap,
						collisionRects.get(i));
			}
			load(imgFilename, aData);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public Sprite create(){
		return new Sprite(this);
	}
}
