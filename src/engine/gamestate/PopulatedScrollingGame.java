package engine.gamestate;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import main.GameApp;
import main.GamePanel;

import engine.AABB;
import engine.AudioPlayer;
import engine.Background;
import engine.CellSpacePartition;
import engine.HasRectangle;
import engine.Res;
import engine.Vec2D;
import engine.Vec2I;
import engine.entity.Enemy;
import engine.entity.Entity;
import engine.entity.MapObject;
import engine.imagemap.Tile;
import engine.imagemap.TileMap;

public abstract class PopulatedScrollingGame extends GameState {
	/** scrolling offset */
	protected Vec2D offset = new Vec2D();
	/** keeps the view on the map */
	protected AABB offsetRange = new AABB();
	protected Vec2I initialLocation;

	protected double tween;
	
	protected TileMap tileMap;
	protected Background bg;
	
	protected ArrayList<MapObject> mobjects = new ArrayList<MapObject>();
	protected ArrayList<HasRectangle> collidables = new ArrayList<HasRectangle>();
	protected CellSpacePartition<HasRectangle> celp;
	
	protected static ArrayList<HasRectangle> emptyList = new ArrayList<HasRectangle>();
	
	protected String levelMusic;
	protected int levelMusicGain;
	protected AudioPlayer bgMusic;

	/**
	 * @param location
	 * @return tiles and entities at the given location
	 */
	public ArrayList<HasRectangle> getEntitiesAt(AABB location, int type){
		ArrayList<HasRectangle> result = celp.getAt(location);
		if(result == null)
			result = emptyList;
		for(int i = 0; i < result.size(); ++i){
			if(result.get(i) instanceof Entity) {
				Entity e = (Entity)result.get(i);
				if(!e.getRectangle(type).intersects(location)){
					result.remove(i--);
				}
			}
		}
		return result;
	}
	
	private class AsyncLoadMusic implements Runnable
	{
		public void run(){
			AudioPlayer startNoise = new AudioPlayer("/sfx/startnoise.wav");
			startNoise.adjustGain(-15);
			startNoise.play();
			if(levelMusic != null && levelMusic.length() > 0){
				AudioPlayer.soundsAllowedToPlay = false;
				// music found at http://opengameart.org/content/trance-menu
				bgMusic = new AudioPlayer(levelMusic);
				if(bgMusic != null){
					bgMusic.adjustGain(levelMusicGain);
					AudioPlayer.soundsAllowedToPlay = true;
					bgMusic.play(true);
				}
				AudioPlayer.soundsAllowedToPlay = true;
			}
		}
	}
	
	public void init(String map, String bg){
		tileMap = new TileMap(this, 30);
		// open up the map file
		InputStream in = getClass().getResourceAsStream(map);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		tileMap.loadMap(br);
		int[] cellspacePartitions = null;
		try{
			String[] tokens = Res.getLineTokens(br);
			levelMusic = tokens[0];
			levelMusicGain = Integer.parseInt(tokens[1]);
			cellspacePartitions = Res.getLineNumbers(br);
		}catch(Exception e){
			e.printStackTrace();
		}
		this.bg = new Background(bg, 0.1);
		populateEntities();
		AABB area = new AABB(new Vec2D(), 
				new Vec2D(tileMap.getPixelSize().x, tileMap.getPixelSize().y));
		Vec2I grid = new Vec2I(cellspacePartitions[0], cellspacePartitions[1]);

		celp = new CellSpacePartition<HasRectangle>(area, grid);
		
		setTween(0.07);
		offsetRange.min = new Vec2D(GamePanel.pixelSize.difference(tileMap.getPixelSize()));
		offsetRange.max.setLocation(0,0);
		setPosition(0,0);
		
		Thread t = new Thread(new AsyncLoadMusic());
		t.start();
	}
	
	private void populateEntities()
	{
		Vec2I tsize = tileMap.getTileSize();
		Vec2I grid = tileMap.getGridSize();
		Enemy e;
		Tile t;
		for(int row = 0; row < grid.y; ++row)
		{
			for(int col = 0; col < grid.x; ++col){
				t = tileMap.getTile(row, col);
				switch(t.getType()){
				case Tile.ENEMY_SPAWN_POINT:
					try {
						Class<?> c = Class.forName("game.entity."+
								t.getExtraData());
						Vec2I p = new Vec2I(col*tsize.x, row*tsize.y);
						e = (Enemy)c.newInstance();
						e.init(this);
						e.setPosition(p.sum(tsize.quotient(2)));
						addEntity(e, true);
					} catch (Exception ex) {ex.printStackTrace();}
					break;
				case Tile.PLAYER_SPAWN_POINT:
				{
					initialLocation = new Vec2I(col*tsize.x, row*tsize.y);
				}
					break;
				}
			}
		}

	}
	
	public Vec2D getOffset(){return offset;}
	
	public void setPosition(double x, double y) {
		Vec2D offset = getOffset();
		offset.x += (x - offset.x) * tween;
		offset.y += (y - offset.y) * tween;
		
		fixBounds();
		tileMap.setPosition(offset);
	}
	public void setTween(double t) {
		tween = t;
	}
	private void fixBounds() {
		if(offset.x < offsetRange.min.x) offset.x = offsetRange.min.x;
		if(offset.y < offsetRange.min.y) offset.y = offsetRange.min.y;
		if(offset.x > offsetRange.max.x) offset.x = offsetRange.max.x;
		if(offset.y > offsetRange.max.y) offset.y = offsetRange.max.y;
	}
	
	public void fixedUpdate(){
		celp.clear();
		for(int i = 0; i < collidables.size(); ++i){
			celp.add(collidables.get(i));
		}
		MapObject mobj;
		int objectsToCheck = mobjects.size();
		for(int i = 0; i < objectsToCheck; ++i){
			mobj = mobjects.get(i);
			mobj.update();
		}
	}
	
	public void update() {
		fixedUpdate();
		lateUpdate();
	}
	
	public void lateUpdate() {
		MapObject mobj;
		for(int i = 0; i < mobjects.size(); ++i){
			mobj = mobjects.get(i);
			if(mobj.shouldDispose()){
				removeEntity(mobj);
				i--;
			}
		}
	}
	
	public void addEntity(MapObject mobj) {
		mobjects.add(mobj);
	}
	
	public ArrayList<MapObject> getEntities(){
		return mobjects;
	}
	
	public void addCollidable(MapObject mobj){
		collidables.add(mobj);
	}	
	public void addEntity(MapObject mobj, boolean isCollidable) {
		mobjects.add(mobj);
		if(isCollidable)
			addCollidable(mobj);
	}
	public void removeEntity(MapObject mobj){
		mobjects.remove(mobj);
		collidables.remove(mobj);
	}

	public AABB getPixelArea() {
		return tileMap.getPixelArea();
	}
	
	/**
	 * @param mobj
	 * @param r
	 * @return everything that could collide with this. MapObjects and TileRectangles
	 */
	public ArrayList<HasRectangle> getCollisionsFor(MapObject mobj, AABB r)
	{
		ArrayList<HasRectangle> result = new ArrayList<HasRectangle>();
		ArrayList<AABB> collidingMapRects = tileMap.getCollisionAt(r);
		result.addAll(collidingMapRects);
		ArrayList<HasRectangle> hits = getEntitiesAt(r, Entity.COLLISION);
		hits.remove(mobj);
		result.addAll(hits);
		return result;
	}
	public void draw(Graphics2D g)
	{
		bg.draw(g);
		tileMap.draw(g);
		if(GameApp.DEBUG){
			drawDebug(g);
		}
		for(int i = 0; i < mobjects.size(); ++i)
		{
			mobjects.get(i).draw(g);
		}	
	}
	
	public void drawDebug(Graphics2D g){
		Vec2D t = getOffset();
		g.translate(t.x, t.y);
		g.setColor(Color.black);
		celp.draw(g);
		for(int i = 0; i < mobjects.size(); ++i){
			mobjects.get(i).drawDebug(g);
		}
		g.translate(-t.x, -t.y);
	}
}
