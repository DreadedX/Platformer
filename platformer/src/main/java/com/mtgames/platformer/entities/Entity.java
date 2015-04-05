package com.mtgames.platformer.entities;

import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.level.Level;

public abstract class Entity {

	public    int   x;
	public    int   y;
	protected Level level;

	Entity(Level level) {
		init(level);
	}

	private void init(Level level) {
		this.level = level;
	}

	public abstract void tick();

	public abstract void render(Screen screen);
}
