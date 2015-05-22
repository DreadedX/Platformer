package com.mtgames.platformer.entities;

import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.level.Level;

public abstract class Entity {

	public          int        x;
	public          int        y;
	int xa;
	int ya;
	protected final Level      level;
	private final   Properties properties;
	int gravityWait = 0;

	Entity(Properties properties) {
		this.level = properties.getLevel();
		this.properties = properties;
	}

	public abstract void tick();

	public abstract void render(Screen screen);

	protected abstract boolean hasCollided(int xa, int ya);

	protected int gravity(int ya) {
		if (hasCollided(0, 1) && ya > 0) {
			ya = 0;
		}

		if (hasCollided(0, 1)) {
			gravityWait = 0;
			return ya;
		}

		if (hasCollided(0, 1) && ya > 0) {
			ya = 0;
		}

		if (hasCollided(0, -1))
			ya = 0;

		if (gravityWait > 1) {
			int gravity = 1;
			if (ya < 12)
				ya = ya + gravity;
			gravityWait = 0;
		}

		gravityWait++;
		return ya;
	}

	public Properties getProperties() {
		return properties;
	}
}
