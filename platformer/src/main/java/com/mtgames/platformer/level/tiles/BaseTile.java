package com.mtgames.platformer.level.tiles;

import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.gfx.Sheet;

class BaseTile extends Tile {

	private final int   tileId;
	private final Sheet tileset;

	public BaseTile(int id, String name, int tileId) {
		super(id, false);
		this.tileId = tileId;
		this.tileset = new Sheet("/graphics/tiles/" + name + ".png");
	}

	public void render(Screen screen, int x, int y) {
		screen.render(x, y, tileset, tileId);
	}
}
