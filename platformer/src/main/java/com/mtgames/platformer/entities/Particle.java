package com.mtgames.platformer.entities;

import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.level.Level;

public abstract class Particle extends Entity {
	protected int x;
	protected int y;
	protected int life;
	private int gravityWait = 0;

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

	protected void move(int xa, int ya) {
		if (ya < 0) {
			for (int i = 0; i > ya; i--) {
				y -= 1;
			}
		}

		if (ya > 0) {
			for (int i = 0; i < ya; i++) {
				y += 1;
			}
		}

		if (xa < 0) {
			for (int i = 0; i > xa; i--) {
				x -= 1;
			}
		}

		if (xa > 0) {
			for (int i = 0; i < xa; i++) {
				x += 1;
			}
		}

	}

	protected int gravity(int ya) {
		if (gravityWait > 1) {
			int gravity = 1;
			if (ya < 12)
				ya = ya + gravity;
			gravityWait = 0;
		}

		gravityWait++;
		return ya;
	}
}
