package com.mtgames.platformer.level.tiles;

import com.mtgames.platformer.gfx.Screen;
import com.mtgames.utils.Debug;

@SuppressWarnings("UnusedDeclaration") public abstract class Tile {

	private static final int SMALL = 8;
	private static final int BIG   = 32;

	public static final Tile[] tiles = new Tile[1024];
	public static final Tile   VOID  = new BaseSolidTile(0, "void", false);
	public static final Tile   GRID  = new BaseTile(1, "grid", false);

//	BRICKS
	public static final Tile BRICK       = new BaseTile(2, "brick");
	public static final Tile BRICK_S = new BaseSolidTile(3, "brick_solid");
//	public static final Tile BRICK       = new BaseTile(1002, "brick");
//	public static final Tile BRICK_S = new BaseSolidTile(1003, "brick_solid");

//	WINDOW
	public static final Tile WINDOW_TTLL = new BaseTile(4, "window", 4*16, 0);
	public static final Tile WINDOW_TTL = new BaseTile(5, "window", 4*16, 1);
	public static final Tile WINDOW_TTR = new BaseTile(6, "window", 4*16, 2);
	public static final Tile WINDOW_TTRR = new BaseTile(7, "window", 4*16, 3);

	public static final Tile WINDOW_TML = new BaseTile(8, "window", 4*16, 4);
	public static final Tile WINDOW_BL = new BaseTile(9, "window", 4*16, 5);
	public static final Tile WINDOW_BR = new BaseTile(10, "window", 4*16, 6);
	public static final Tile WINDOW_TMR = new BaseTile(11, "window", 4*16, 7);

	public static final Tile WINDOW_ML = new BaseTile(12, "window", 4*16, 8);
	public static final Tile WINDOW_BC = new BaseTile(13, "window", 4*16, 9);
	public static final Tile WINDOW_TC = new BaseTile(14, "window", 4*16, 10);
	public static final Tile WINDOW_MR = new BaseTile(15, "window", 4*16, 11);

//	FLAG
	public static final Tile FLAG_TL = new BaseTile(16, "flag", 4*16, 0);
	public static final Tile FLAG_TC = new BaseTile(17, "flag", 4*16, 1);
	public static final Tile FLAG_TR = new BaseTile(18, "flag", 4*16, 2);

	public static final Tile FLAG_ML = new BaseTile(19, "flag", 4*16, 4);
	public static final Tile FLAG_C = new BaseTile(20, "flag", 4*16, 5);
	public static final Tile FLAG_MR = new BaseTile(21, "flag", 4*16, 6);

	public static final Tile FLAG_BLT = new BaseTile(22, "flag", 4*16, 8);
	public static final Tile FLAG_BSL = new BaseTile(23, "flag", 4*16, 9);
	public static final Tile FLAG_BRT = new BaseTile(24, "flag", 4*16, 10);

	public static final Tile FLAG_BLB = new BaseTile(25, "flag", 4*16, 12);
	public static final Tile FLAG_BSR = new BaseTile(26, "flag", 4*16, 13);
	public static final Tile FLAG_BRB = new BaseTile(27, "flag", 4*16, 14);

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
