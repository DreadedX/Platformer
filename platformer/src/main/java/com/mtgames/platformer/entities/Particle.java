package com.mtgames.platformer.entities;

import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.level.Level;

public abstract class Particle extends Entity {
	protected final int x;
	protected final int y;
	private         int life;

	protected Particle(Level level, int x, int y, int life) {
		super(level);
		this.life = life;
		this.x = x;
		this.y = y;
	}

	public void tick() {
		life--;
	}

	public boolean isAlive() {
		return life >= 0;
	}

	protected boolean isOnScreen(Screen screen) {
		return !(x - screen.xOffset < 0 || x - screen.xOffset >= screen.width || y - screen.yOffset < 0 || y - screen.yOffset >= screen.height);

	}
}
