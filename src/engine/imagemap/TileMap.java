package engine.imagemap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.util.ArrayList;

import engine.AABB;
import engine.CellSpacePartition;
import engine.HasRectangle;
import engine.Res;
import engine.Vec2D;
import engine.Vec2I;
import engine.gamestate.PopulatedScrollingGame;
import main.GameApp;
import main.GamePanel;

public class TileMap {
	PopulatedScrollingGame game;
	private int[][] map;
	protected Vec2I tileSize = new Vec2I();
	/** size of entire map in grid squares */
	private Vec2I gridSize = new Vec2I();
	/** size of entire map in pixels */
	private Vec2I pixelSize = new Vec2I();

	private AABB pixelArea;
	
	/** the tiles available in this level */
	private Tile[] palette;
	
	/** which grid squares need to be read and drawn */
	private Rectangle gridView = new Rectangle();
	
	public TileMap(PopulatedScrollingGame game, int tileSize) {
		this.game = game;
	}

	public void loadMap(BufferedReader br)
	{
		try{
			// start reading the palette
			int numPaletteEntries = Res.getLineNumber(br);
			palette = new Tile[numPaletteEntries];
			String line;
			int symbol, type;
			int minTileSymbol = -1, maxTileSymbol = -1;
			String extraData = null;
			for(int i = 0; i < palette.length; ++i){
				line = Res.getLine(br);
				symbol = line.charAt(0);
				extraData = null;
				switch(line.charAt(1)){
				case ' ':	type = Tile.NORMAL;		break;
				case 'X':	type = Tile.BLOCKED;	break;
				case 'e':
					type = Tile.ENEMY_SPAWN_POINT;
					extraData = line.substring(2);
					break;
				case 'p':
					type = Tile.PLAYER_SPAWN_POINT;
					break;
				default:	type = Tile.NONE;		break;
				}
				palette[i] = new Tile(null, type, symbol);
				if(extraData != null){
					palette[i].setExtraData(extraData);
				}
				if(minTileSymbol == -1 || symbol < minTileSymbol){
					minTileSymbol = symbol;
				}
				if(maxTileSymbol == -1 || symbol > maxTileSymbol){
					maxTileSymbol = symbol;
				}
			}
			// make the lookup table for the palette, used later in the load
			int tileSymbolLookupTableWidth = maxTileSymbol - minTileSymbol + 1;
			int[] symbolLookupTable = new int[tileSymbolLookupTableWidth];
			for(int i = 0; i < palette.length; ++i){
				symbolLookupTable[palette[i].getSymbol()-minTileSymbol] = i;
			}
			// load the tileset image
			String filename = Res.getLine(br).trim();
			int numFrameSets = Res.getLineNumber(br);
			TileSet.Description[] data = new TileSet.Description[numFrameSets];
			int[] nums;
			for(int i = 0; i < numFrameSets; ++i){
				nums = Res.getLineNumbers(br);
				Vec2I tileSize = new Vec2I(nums[1],nums[2]);
				if(this.tileSize.isZero()){
					this.tileSize = new Vec2I(tileSize);
				}
				data[i] = new TileSet.Description(nums[0], tileSize);
			}
			TileSet ts = new TileSet();
			ts.load(filename, data);
			int col = 0, row = 0;
			BufferedImage img;
			// populate palette with the loaded tileset image
			for(int i = 0; i < palette.length; ++i){
				img = ts.getTiles()[row][col];
				palette[i].setImage(img);
				col++;
				if(col > ts.getTiles()[row].length){
					col = 0;
					row++;
				}
			}
			// load the map
			nums = Res.getLineNumbers(br);
			gridSize.x = nums[0];
			gridSize.y = nums[1];
			map = new int[gridSize.y][gridSize.x];
			pixelSize = gridSize.product(tileSize);
			pixelArea = new AABB(new Vec2D(), new Vec2D(pixelSize));
			for(row = 0; row < gridSize.y; ++row) {
				line = Res.getLine(br);
				for(col = 0; col < gridSize.x; col++) {
					symbol = line.charAt(col);//map[row][col];
					System.out.print((char)symbol);
					int paletteIndex = symbolLookupTable[symbol-minTileSymbol];
					map[row][col] = paletteIndex;
				}
				System.out.println();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		gridView.width = GamePanel.pixelSize.x / this.tileSize.x + 2;
		gridView.height = GamePanel.pixelSize.y / this.tileSize.y + 2;
	}

	public Vec2I getPixelSize() {return pixelSize;}
	public AABB getPixelArea() {return pixelArea;}
	public Vec2I getGridSize() {return gridSize;}
	public int getType(int row, int col) {
		return palette[map[row][col]].getType();
	}
	public int getSymbol(int row, int col) {
		return palette[map[row][col]].getSymbol();
	}
	public Tile getTile(int row, int col) {
		return palette[map[row][col]];
	}
	public void setPosition(Vec2D offset) {
		gridView.x = (int)-offset.x / tileSize.x;
		gridView.y = (int)-offset.y / tileSize.y;
	}

	
	public void draw(Graphics2D g) {
		int minx = gridView.x,
			miny = gridView.y,
			maxx = gridView.x + gridView.width,
			maxy = gridView.y + gridView.height;
		if(minx < 0)	minx = 0;
		if(miny < 0)	miny = 0;
		if(maxx >= gridSize.x)	maxx = gridSize.x;
		if(maxy >= gridSize.y)	maxy = gridSize.y;
		int x = (int)game.getOffset().x;
		int y = (int)game.getOffset().y;
		for(int row = miny; row < maxy; ++row)
		{
			for(int col = minx; col < maxx; ++col)
			{
				if(map[row][col] == 0)	continue;
				g.drawImage(palette[map[row][col]].getImage(),
						x + col * tileSize.x, 
						y + row * tileSize.y, null);
			}
		}
		if(GameApp.DEBUG)
		{
			g.setColor(Color.red);
			for(int row = miny; row < maxy; ++row)
			{
				for(int col = minx; col < maxx; ++col)
				{
					if(palette[map[row][col]].getType() != Tile.BLOCKED)	continue;
					g.drawRect(
							x + col * tileSize.x, 
							y + row * tileSize.y, 
							tileSize.x,
							tileSize.y);
				}
			}
		}
	}
	
	public ArrayList<HasRectangle> getRectAt(AABB area, boolean collisionsOnly)
	{
		ArrayList<HasRectangle> result = new ArrayList<HasRectangle>();
		AABB rect = CellSpacePartition.getGridIndexeRange(area, tileSize, gridSize);
		for(int row = (int)rect.min.y; row < rect.max.y+1; ++row){
			for(int col = (int)rect.min.x; col < rect.max.x+1; ++col){
				Tile t = getTile(row, col);
				if(!collisionsOnly || t.getType() == Tile.BLOCKED){
					AABB r = t.boundingFor(row, col, tileSize);
					result.add(r);
				}
			}
		}
		return result;
	}
	public ArrayList<AABB> getCollisionAt(AABB area)
	{
		ArrayList<HasRectangle> tileRects = getRectAt(area, true);
		ArrayList<AABB> collisions = new ArrayList<AABB>();
		AABB union;
		for(int i = 0; i < tileRects.size(); ++i){
			union = area.getUnion(tileRects.get(i).getRectangle());
			if(union != null){
				collisions.add(union);
			}
		}
		if(collisions.size() > 0)
			AABB.merge(collisions);
		return collisions;
	}
	public Vec2I getTileSize() {
		return tileSize;
	}

}
