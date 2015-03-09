package com.mtgames.firerpg.entities;

import com.mtgames.firerpg.gfx.Screen;
import com.mtgames.firerpg.level.Level;

public abstract class Entity {

	public    int   x;
	public    int   y;
	protected Level level;

	Entity(Level level) {
		init(level);
	}

	final void init(Level level) {
		this.level = level;
	}

	public abstract void tick();

	public abstract void render(Screen screen);
}
