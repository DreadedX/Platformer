package com.mtgames.firerpg.level.tiles;

import com.mtgames.firerpg.gfx.Screen;

public abstract class Tile {

	public static final Tile[] tiles = new Tile[256];
	public static final Tile   VOID  = new BaseSolidTile(0, 0, 0);
	public static final Tile   GRID  = new BaseTile(1, 1, 0);

	public static final Tile BLOCK1 = new BaseSolidTile(2, 2, 0);
	public static final Tile BLOCK2 = new BaseSolidTile(3, 3, 0);

	public static final Tile BIGBLOCK1TL = new BaseSolidTile(32, 0, 1);
	public static final Tile BIGBLOCK1TR = new BaseSolidTile(33, 1, 1);
	public static final Tile BIGBLOCK1BL = new BaseSolidTile(64, 0, 2);
	public static final Tile BIGBLOCK1BR = new BaseSolidTile(65, 1, 2);

	public static final Tile BIGBLOCK2TL = new BaseSolidTile(34, 2, 1);
	public static final Tile BIGBLOCK2TR = new BaseSolidTile(35, 3, 1);
	public static final Tile BIGBLOCK2BL = new BaseSolidTile(66, 2, 2);
	public static final Tile BIGBLOCK2BR = new BaseSolidTile(67, 3, 2);

	private final byte id;
	boolean solid;
	private boolean emitter;

	Tile(int id, boolean isSolid, boolean isEmitter) {
		this.id = (byte) id;
		if (tiles[id] != null) {
			throw new RuntimeException("Tile id already exists: " + id);
		}

		this.solid = isSolid;
		this.emitter = isEmitter;
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

	public abstract void render(Screen screen, int x, int y);

}
