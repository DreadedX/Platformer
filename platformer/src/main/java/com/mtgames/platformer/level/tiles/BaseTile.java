package com.mtgames.platformer.level.tiles;

import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.gfx.lwjgl.TextureLoader;

class BaseTile extends Tile {

	private int textureID = 0;
	private final boolean visible;
	private final int size;
	private final int loc;

	BaseTile(String name, int size, int part, boolean visible) {
		super(name);
		this.solid = false;
		this.visible = visible;
		this.size = size;
		this.loc = part;
		if (visible) {
			this.textureID = TextureLoader.loadTexture("/assets/graphics/tiles/" + name + ".png");
		}
	}

	BaseTile(String name, int size, int part) {
		super(name);
		this.solid = false;
		this.visible = true;
		this.size = size;
		this.loc = part;
		this.textureID = TextureLoader.loadTexture("/assets/graphics/tiles/" + name + ".png");
	}

	public BaseTile(String name, boolean visible) {
		super(name);
		this.solid = false;
		this.visible = visible;
		this.size = 16;
		this.loc = 0;
		if (visible) {
			this.textureID = TextureLoader.loadTexture("/assets/graphics/tiles/" + name + ".png");
		}
	}

	BaseTile(String name) {
		super(name);
		this.solid = false;
		this.visible = true;
		this.size = 16;
		this.loc = 0;
		this.textureID = TextureLoader.loadTexture("/assets/graphics/tiles/" + name + ".png");
	}

	public void render(Screen screen, int x, int y) {
		if (visible) {
			screen.renderTile(x, y, textureID, size, loc);
		}
	}
}
