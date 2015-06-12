package com.mtgames.platformer.entities.enemies;

import com.mtgames.platformer.entities.AdvancedEntity;
import com.mtgames.platformer.settings.Properties;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.entities.LightSource;
import com.mtgames.platformer.gfx.lwjgl.TextureLoader;

public class BaseEnemy extends AdvancedEntity {

	private final int JUMPSPEED;

	private int ya = 0;

	private final LightSource lightSource;

	private static final int[] textureIDStand = TextureLoader.loadTextureArray("/assets/graphics/entities/baseEnemy", 6);

	public BaseEnemy(int x, int y, Properties properties) {
		super(properties, x, y);

		level.addLightSource(lightSource = new LightSource(x, y, properties));

		JUMPSPEED = properties.getJumpSpeed();

		speed = properties.getSpeed();

		movingDir = (int) (Math.random() + .5);

		collide = true;
	}

	public void tick() {
		int xa = 0;

		if (!hasCollided(3, -16) && hasCollided(3, 0)) {
			ya = -JUMPSPEED;
			xa += speed;
		} else if (!hasCollided(-3, -16) && hasCollided(-3, 0)) {
			ya = -JUMPSPEED;
			xa -= speed;
		} else if (hasCollided(-3, 0)) {
			movingDir = 1;
			xa += speed;
		} else if (hasCollided(3, 0)) {
			movingDir = 0;
			xa -= speed;
		} else {
			if (movingDir == 0) {
				xa -= speed;
			}

			if (movingDir == 1) {
				xa += speed;
			}
		}

		move(xa, ya);
		ya = gravity(ya);
		lightSource.move(x, y);
	}

	public void render(Screen screen) {
		int xTile = 0;

		boolean flipX = false;

		if (movingDir == 0) {
			flipX = true;
		}

		if (isJumping) {
			xTile = 4;
		}

		if (animationFrame == 1) {
			xTile += 1;
		} else if (animationFrame == 2) {
			xTile += 2;
		} else if (animationFrame == 3) {
			xTile += 3;
		}
		int textureID = textureIDStand[xTile];

		screen.renderEntity(x, y, textureID, 32, flipX);
	}
}
