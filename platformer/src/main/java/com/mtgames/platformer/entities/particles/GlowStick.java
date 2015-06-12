package com.mtgames.platformer.entities.particles;

import com.mtgames.platformer.entities.BasicEntity;
import com.mtgames.platformer.settings.Properties;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.entities.LightSource;
import com.mtgames.platformer.gfx.lwjgl.TextureLoader;

public class GlowStick extends BasicEntity {

	private int xa;
	private              int    ya        = -10 - (int) (Math.random());
	private static final int[]  textureID = TextureLoader.loadTextureArray("/assets/graphics/items/glowStick", 2);
	private              double modifier  = 0;
	private final LightSource lightSource;

	public GlowStick(int x, int y, int movingDir, Properties properties) {
		super(properties, (int) (x + Math.random() * 30), (int) (y - 16 + Math.random() * 32));

		level.addLightSource(lightSource = new LightSource(x, y, properties));

		if (movingDir == 0) {
			xa = -5 - (int) (Math.random() * 2);
		} else {
			xa = 5 + (int) (Math.random() * 2);
		}
	}

	public void tick() {
		move(xa, ya);
		ya = gravity(ya);

		if (hasCollided(0, 1) || hasCollided(1, 0) || hasCollided(-1, 0)) {
			xa = 0;
		}

		if (modifier < 0xff) {
			modifier += .2;
		}

		lightSource.move(x, y);
	}

	public void render(Screen screen) {
		if (modifier < 0xff) {
			screen.renderTile(x - 8, y - 8, textureID[0]);
		} else {
			screen.renderTile(x - 8, y - 8, textureID[1]);
		}
	}
}
