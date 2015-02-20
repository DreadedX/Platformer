package com.mtgames.firerpg.level;

import com.mtgames.firerpg.gfx.Screen;
import com.mtgames.firerpg.level.Level;

public abstract class Tile {
	
	public static final Tile[]	tiles		= new Tile[256];
	public static final Tile	VOID		= new BaseSolidTile(0, 0, 0, 0xff000000);
	public static final Tile	GRID		= new BaseTile(1, 1, 0, 0xffffffff);
	
	public static final Tile	BLOCK1		= new BaseSolidTile(2, 2, 0, 0xff9966cc);
	public static final Tile	BLOCK2		= new BaseSolidTile(3, 3, 0, 0xff666666);
	
	public static final Tile	BIGBLOCK1TL	= new BaseSolidTile(32, 0, 1, 0xffffcc01);
	public static final Tile	BIGBLOCK1TR	= new BaseSolidTile(33, 1, 1, 0xffffcc02);
	public static final Tile	BIGBLOCK1BL	= new BaseSolidTile(64, 0, 2, 0xffffcc03);
	public static final Tile	BIGBLOCK1BR	= new BaseSolidTile(65, 1, 2, 0xffffcc04);
	
	public static final Tile	BIGBLOCK2TL	= new BaseSolidTile(34, 2, 1, 0xff996631);
	public static final Tile	BIGBLOCK2TR	= new BaseSolidTile(35, 3, 1, 0xff996632);
	public static final Tile	BIGBLOCK2BL	= new BaseSolidTile(66, 2, 2, 0xff996633);
	public static final Tile	BIGBLOCK2BR	= new BaseSolidTile(67, 3, 2, 0xff996634);
	
	protected byte				id;
	protected boolean			solid;
	protected boolean			emitter;
	private int					levelColour;
	
	public Tile(int id, boolean isSolid, boolean isEmitter, int levelColour) {
		this.id = (byte) id;
		if (tiles[id] != null) {
			throw new RuntimeException("Tile id already exists: " + id);
		}
		
		this.solid = isSolid;
		this.emitter = isEmitter;
		this.levelColour = levelColour;
		tiles[id] = this;
	}
	
	public byte getId() {
		return id;
	}
	
	public boolean isSolid() {
		return solid;
	}
	
	public boolean isEmitter() {
		return emitter;
	}
	
	public int getLevelColour() {
		return levelColour;
	}
	
	public abstract void render(Screen screen, Level level, int x, int y);
	
}
