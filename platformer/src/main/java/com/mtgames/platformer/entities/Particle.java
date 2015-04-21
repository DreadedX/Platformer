package com.mtgames.platformer.entities;

import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.level.tiles.Tile;

public abstract class Particle extends Entity {
	protected int x;
	protected int y;
	private   int life;
	private int gravityWait = 0;

	protected Particle(int x, int y, int life, Properties properties) {
		super(properties);
		this.life = life;
		this.x = x;
		this.y = y;
	}

	public void tick() {
		life--;
	}

	public boolean isAlive() {
		return life > 0 || life <= -10;
	}

	protected boolean isOnScreen(Screen screen) {
		return !(x - screen.xOffset < 0 || x - screen.xOffset >= screen.width || y - screen.yOffset < 0 || y - screen.yOffset >= screen.height);

	}

	protected void move(int xa, int ya) {
		if (ya < 0) {
			for (int i = 0; i > ya; i--) {
				if (!hasCollided(0, -1)) {
					y -= 1;
				}
			}
		}

		if (ya > 0) {
			for (int i = 0; i < ya; i++) {
				if (!hasCollided(0, 1)) {
					y += 1;
				}
			}
		}

		if (xa < 0) {
			for (int i = 0; i > xa; i--) {
				if (!hasCollided(-1, 0)) {
					x -= 1;
				}
			}
		}

		if (xa > 0) {
			for (int i = 0; i < xa; i++) {
				if (!hasCollided(1, 0)) {
					x += 1;
				}
			}
		}
	}

	protected boolean hasCollided(int xa, int ya) {
		return isSolidTile(xa, ya, 0, 0);

	}

//	TODO: Move this into entity code, now it is in both mobs and particles
private boolean isSolidTile(int xa, int ya, int x, int y) {
		if (level == null)
			return false;

		Tile lastTile = level.getTile((this.x + x) >> 4, (this.y + y) >> 4);
		Tile newTile = level.getTile((this.x + x + xa) >> 4, (this.y + y + ya) >> 4);

		return !lastTile.equals(newTile) && newTile.isSolid();

	}

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
}
