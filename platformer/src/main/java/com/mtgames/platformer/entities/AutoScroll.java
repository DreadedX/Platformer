package com.mtgames.platformer.entities;

import com.mtgames.platformer.gfx.Screen;

public class AutoScroll extends Mob {

	public AutoScroll(int x, int y, Properties properties) {
		super(properties, x, y);
		movingDir = 1;
		speed = properties.getSpeed();
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
