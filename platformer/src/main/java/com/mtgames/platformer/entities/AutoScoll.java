package com.mtgames.platformer.entities;

import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.level.Level;

public class AutoScoll extends Mob {

	public AutoScoll(Level level, int x, int y) {
		super(level, x, y);
		this.level = level;
		movingDir = 1;
		speed = 2;
	}

	public void tick() {
		int xa = 0;
		int ya = 0;

		xa++;

		move(xa, ya);

		y = level.entities.get(1).y;
	}

	public void render(Screen screen) {
	}

	@Override public boolean hasCollided(int xa, int ya) {
		return false;
	}
}
