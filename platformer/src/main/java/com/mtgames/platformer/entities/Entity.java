package com.mtgames.platformer.entities;

import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.level.Level;

public abstract class Entity {

	public          int        x;
	public          int        y;
	protected final Level      level;
	private         Properties properties;

	Entity(Properties properties) {
		this.level = properties.getLevel();
		this.properties = properties;
	}

	public abstract void tick();

	public abstract void render(Screen screen);

	public Properties getProperties() {
		return properties;
	}
}
