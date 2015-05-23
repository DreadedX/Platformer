package com.mtgames.platformer.entities;

import com.mtgames.platformer.gfx.Screen;
import com.mtgames.utils.Debug;

public class AutoScroll extends AdvancedEntity {

	public AutoScroll(int x, int y, Properties properties) {
		super(properties, x, y);
		movingDir = 1;
		speed = properties.getSpeed();
		Debug.log(String.valueOf(properties.getSpeed()), Debug.DEBUG);
	}

	public void tick() {
		int xa = 0;
		int ya = 0;

		xa += speed;

		move(xa, ya);

		y = level.entities.get(1).y;
	}

	public void render(Screen screen) {
	}

	@Override public boolean hasCollided(int xa, int ya) {
		return false;
	}
}
