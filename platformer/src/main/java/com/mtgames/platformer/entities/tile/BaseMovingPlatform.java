package com.mtgames.platformer.entities.tile;

import com.mtgames.platformer.entities.BasicEntity;
import com.mtgames.platformer.entities.Properties;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.gfx.opengl.TextureLoader;

public class BaseMovingPlatform extends BasicEntity {
	private static final int TextureID = TextureLoader.loadTexture("/assets/graphics/tiles/bigBlock2.png");

	//PLACEHOLDER NAME
	private int speed = 2;
	private int move = 300;
	private int moveMin;
	private int moveMax;
	private boolean direction = false;

	public BaseMovingPlatform(Properties properties, int x, int y) {
		super(properties, x, y);
		moveMin = x - move/2;
		moveMax = x + move/2;
	}

	@Override public void tick() {
		if (direction) {
			move(speed, 0);
		} else {
			move(-speed, 0);
		}

		if (x == moveMax || x == moveMin || betterHasCollided()) {
			direction = !direction;
		}
	}

	@Override public void render(Screen screen) {
		screen.renderEntity(x, y, TextureID, 32, false);
	}

	public boolean betterHasCollided() {
		for (int i = 0; i < 32; i++) {
			if(hasCollided(-16, i)) {
				return true;
			}

			if(hasCollided(16, i)) {
				return true;
			}
		}

		return false;
	}
}
