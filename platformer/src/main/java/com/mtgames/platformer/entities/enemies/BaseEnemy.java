package com.mtgames.platformer.entities.enemies;

import com.mtgames.platformer.entities.Mob;
import com.mtgames.platformer.entities.Properties;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.gfx.Sheet;
import com.mtgames.platformer.gfx.lighting.LightSource;

public class BaseEnemy extends Mob {

	private final int JUMPSPEED;

	private int ya = 0;
	private int dir;
	private int modifier;

	private final LightSource lightSource;

	private final Sheet sheet = new Sheet("/assets/graphics/entities/baseEnemy.png");

	public BaseEnemy(int x, int y, Properties properties) {
		super(properties, x, y);

		level.addLightSource(lightSource = new LightSource(x, y, 0, 0xffae00));

		JUMPSPEED = properties.getJumpSpeed();

		speed = properties.getSpeed();
		xMin = properties.getXMin();
		xMax = properties.getXMax();
		yMin = properties.getYMin();
		yMax = properties.getYMax();

		movingDir = (int) (Math.random() + .5);
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

		int xOffset = x;
		int yOffset = y;

		switch (movingDir) {
			case 0:
				dir = 0x01;
				modifier = 16;
				break;

			case 1:
				dir = 0x00;
				modifier = 0;
				break;
		}

		if (isJumping) {
			xTile = 8;
		}

		if (animationFrame == 1) {
			xTile += 2;
		} else if (animationFrame == 2) {
			xTile += 4;
		} else if (animationFrame == 3) {
			xTile += 6;
		}

		screen.render(xOffset - 16 + modifier, yOffset - 16, sheet, xTile, dir);
		screen.render(xOffset - modifier, yOffset - 16, sheet, xTile + 1, dir);
		screen.render(xOffset - 16 + modifier, yOffset, sheet, xTile + (sheet.width/16), dir);
		screen.render(xOffset - modifier, yOffset, sheet, xTile + 1 + (sheet.width/16) , dir);
	}
}
