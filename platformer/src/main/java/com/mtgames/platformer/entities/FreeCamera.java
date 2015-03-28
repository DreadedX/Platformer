package com.mtgames.platformer.entities;

import com.mtgames.platformer.InputHandler;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.level.Level;

public class FreeCamera extends Mob {

	private final InputHandler input;

	public FreeCamera(Level level, int x, int y, InputHandler input) {
		super(level, x, y);
		this.level = level;
		this.input = input;
		movingDir = 1;
		speed = 4;
	}

	public void tick() {
		int xa = 0;
		int ya = 0;

		if (input.shift.isPressed()) {
			speed = 8;
		} else {
			speed = 3;
		}

		if (input.left.isPressed()) {
			xa -= speed;
		}

		if (input.right.isPressed()) {
			xa += speed;
		}

		if (input.up.isPressed()) {
			ya -= speed;
		}

		if (input.down.isPressed()) {
			ya += speed;
		}

		move(xa, ya);
	}

	public void render(Screen screen) {
		screen.drawRectangle(x-4-screen.xOffset, y-4-screen.yOffset ,x+4-screen.xOffset, y+4-screen.yOffset, 0x7F000000, false);
		screen.addLighting(x, y, 0);
	}

	@Override public boolean hasCollided(int xa, int ya) {
		return false;
	}
}
