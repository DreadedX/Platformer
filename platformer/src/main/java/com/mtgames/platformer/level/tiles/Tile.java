package com.mtgames.platformer.level.tiles;

import com.mtgames.platformer.gfx.Screen;

@SuppressWarnings("UnusedDeclaration") public abstract class Tile {

	private static final        int SMALL  = 8;
	private static final        int BIG    = 32;

	public static final Tile[] tiles = new Tile[256];
	public static final Tile   VOID  = new BaseSolidTile(0, "void", false);
	public static final Tile   AIR   = new BaseTile(1, "air", false);

	public static final Tile BLOCK1 = new BaseSolidTile(2, "block1");
	public static final Tile BLOCK2 = new BaseSolidTile(3, "block2");

	public static final Tile BIGBLOCK1TL = new BaseSolidTile(32, "bigBlock1", BIG, 0);
	public static final Tile BIGBLOCK1TR = new BaseSolidTile(33, "bigBlock1", BIG, 1);
	public static final Tile BIGBLOCK1BL = new BaseSolidTile(64, "bigBlock1", BIG, 2);
	public static final Tile BIGBLOCK1BR = new BaseSolidTile(65, "bigBlock1", BIG, 3);

	public static final Tile BIGBLOCK2TL = new BaseSolidTile(34, "bigBlock2", BIG, 0);
	public static final Tile BIGBLOCK2TR = new BaseSolidTile(35, "bigBlock2", BIG, 1);
	public static final Tile BIGBLOCK2BL = new BaseSolidTile(66, "bigBlock2", BIG, 2);
	public static final Tile BIGBLOCK2BR = new BaseSolidTile(67, "bigBlock2", BIG, 3);

	private final byte id;
	boolean solid;

	Tile(int id, boolean isSolid) {
		this.id = (byte) id;
		if (tiles[id] != null) {
			throw new RuntimeException("Tile id already exists: " + id);
		}

		this.solid = isSolid;
		tiles[id] = this;
	}

	public byte getId() {
		return id;
	}

	public boolean isSolid() {
		return solid;
	}

	public abstract void render(Screen screen, int x, int y);

}
