package com.mtgames.platformer.entities;

import com.mtgames.platformer.level.tiles.Tile;

public abstract class Mob extends Entity {

	protected int     speed          = 1;
	protected int     movingDir      = 1;
	protected int     xMin           = -1;
	protected int     xMax           = 1;
	protected int     yMin           = -1;
	protected int     yMax           = 1;
	protected int     animationFrame = 0;
	protected boolean isJumping      = false;
	private int walkingAnimationFrame = 0;

	protected Mob(Properties properties, int x, int y) {
		super(properties);
		this.x = x;
		this.y = y;
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
			movingDir = 0;
			for (int i = 0; i > xa; i--) {
				if (!hasCollided(-1, 0)) {
					x -= 1;
				}
			}
		}

		if (xa > 0) {
			movingDir = 1;
			for (int i = 0; i < xa; i++) {
				if (!hasCollided(1, 0)) {
					x += 1;
				}
			}
		}

		isJumping = !hasCollided(0, 1);

		if (xa != 0 && ya == 0 && !hasCollided(-1, 0) && !hasCollided(1, 0)) {
			walkingAnimation();
		} else {
			animationFrame = 0;
		}

		if (ya < 0 && !hasCollided(0, 1)) {
			animationFrame = 0;
		} else if (!hasCollided(0, 1)) {
			animationFrame = 1;
		}
	}

	public boolean hasCollided(int xa, int ya) {
		for (int x = xMin; x <= xMax; x++) {
			if (isSolidTile(xa, ya, x, yMin)) {
				return true;
			}
		}

		for (int x = xMin; x <= xMax; x++) {
			if (isSolidTile(xa, ya, x, yMax)) {
				return true;
			}
		}

		for (int y = yMin; y <= yMax; y++) {
			if (isSolidTile(xa, ya, xMin, y)) {
				return true;
			}
		}

		for (int y = yMin; y <= yMax; y++) {
			if (isSolidTile(xa, ya, xMax, y)) {
				return true;
			}
		}

		return false;
	}

	private boolean isSolidTile(int xa, int ya, int x, int y) {
		if (level == null)
			return false;

		Tile lastTile = level.getTile((this.x + x) >> 4, (this.y + y) >> 4);
		Tile newTile = level.getTile((this.x + x + xa) >> 4, (this.y + y + ya) >> 4);

		return !lastTile.equals(newTile) && newTile.isSolid();

	}

	private void walkingAnimation() {
		int ANIMATIONWAIT = 7;
		if (walkingAnimationFrame >= ANIMATIONWAIT) {
			walkingAnimationFrame = 0;
			animationFrame++;
			if (animationFrame == 4) {
				animationFrame = 0;
			}
		} else {
			walkingAnimationFrame++;
		}
	}
}