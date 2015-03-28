package com.mtgames.platformer.entities.enemies;

import com.mtgames.platformer.entities.Mob;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.gfx.Sheet;
import com.mtgames.platformer.level.Level;
import com.mtgames.platformer.level.Script;

public class BaseEnemy extends Mob {

	private final int JUMPSPEED;

	private final Script script;

	private int ya = 0;
	private int dir;
	private int modifier;

	private final Sheet sheet = new Sheet("/graphics/sprites/baseEnemy.png");

	public BaseEnemy(Level level, int x, int y) {
		super(level, x, y);

		this.script = new Script("scripts/BaseEnemy.js");
		script.doInit();

		JUMPSPEED = (int) script.get("JUMPSPEED");

		speed = (int) script.get("speed");
		xMin = (int) script.get("xMin");
		xMax = (int) script.get("xMax");
		yMin = (int) script.get("yMin");
		yMax = (int) script.get("yMax");
		movingDir = (int) (Math.random() + .5);

		this.level = level;
	}

	public void tick() {
		script.invoke("tick");

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

		screen.setLighting(x, y);
	}
}
