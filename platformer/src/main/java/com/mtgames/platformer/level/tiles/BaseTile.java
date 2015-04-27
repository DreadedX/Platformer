package com.mtgames.platformer.level.tiles;

import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.gfx.opengl.TextureLoader;

class BaseTile extends Tile {

	private int textureID = 0;
	private final boolean visible;
	private final int size;

	public BaseTile(int id, String name, int size, boolean visible) {
		super(id, false);
		this.visible = visible;
		this.size = size;
		if (visible) {
			this.textureID = TextureLoader.loadTexture(TextureLoader.loadImage("/assets/graphics/tiles/" + name + ".png"));
		}
	}

	public void render(Screen screen, int x, int y) {
		if (visible) {
			screen.render(x, y, textureID, size);
		}
	}
}
