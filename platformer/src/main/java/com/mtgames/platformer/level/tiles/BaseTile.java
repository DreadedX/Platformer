package com.mtgames.platformer.level.tiles;

import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.gfx.opengl.TextureLoader;

class BaseTile extends Tile {

	private final int   tileId;
	private final int textureID;

	public BaseTile(int id, String name, int tileId) {
		super(id, false);
		this.tileId = tileId;
		this.textureID = TextureLoader.loadTexture(TextureLoader.loadImage("/assets/graphics/tiles/" + name + ".png"));
		System.out.println(textureID);
	}

	public void render(Screen screen, int x, int y) {
		screen.render(x, y, textureID);
	}
}
