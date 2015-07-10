package com.mtgames.platformer.level.tiles;

import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.gfx.lwjgl.TextureLoader;

class BaseTile extends Tile {

	private int textureID = 0;
	private final boolean visible;
	private final int size;
	private final int loc;

	public BaseTile(String name, boolean visible, boolean external) {
		super(name);
		this.solid = false;
		this.visible = visible;
		this.size = 16;
		this.loc = 0;
		if (visible) {
			this.textureID = TextureLoader.loadTexture("/assets/graphics/tiles/" + name + ".png", external);
		}
	}

	BaseTile(String name, boolean external) {
		super(name);
		this.solid = false;
		this.visible = true;
		this.size = 16;
		this.loc = 0;
		this.textureID = TextureLoader.loadTexture("/assets/graphics/tiles/" + name + ".png", external);
	}

	public void render(int x, int y) {
		if (visible) {
			Screen.renderTile(x, y, textureID, size, loc);
		}
	}
}
