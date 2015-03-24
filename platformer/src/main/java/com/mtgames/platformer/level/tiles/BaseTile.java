package com.mtgames.platformer.level.tiles;

import com.mtgames.platformer.gfx.Screen;

class BaseTile extends Tile {

	private final int tileId;

	public BaseTile(int id, int x, int y) {
		super(id, false);
		this.tileId = x + y * 32;
	}

	public void render(Screen screen, int x, int y) {
		screen.render(x, y, tileId);
	}
}
