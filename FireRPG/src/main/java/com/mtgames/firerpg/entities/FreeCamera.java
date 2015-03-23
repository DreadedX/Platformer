package com.mtgames.firerpg.entities;

import com.mtgames.firerpg.InputHandler;
import com.mtgames.firerpg.gfx.Screen;
import com.mtgames.firerpg.level.Level;

public class FreeCamera extends Mob {

	private final InputHandler input;

	public FreeCamera(Level level, int x, int y, InputHandler input) {
		super(level, x, y);
		this.level = level;
		this.input = input;
		movingDir = 1;
		speed = 2;
	}

	public void tick() {
		int xa = 0;
		int ya = 0;

		if (input.shift.isPressed()) {
			speed = 4;
		} else {
			speed = 2;
		}

		if (input.left.isPressed()) {
			xa--;
		}

		if (input.right.isPressed()) {
			xa++;
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
	}

	@Override public boolean hasCollided(int xa, int ya) {
		return false;
	}
}
