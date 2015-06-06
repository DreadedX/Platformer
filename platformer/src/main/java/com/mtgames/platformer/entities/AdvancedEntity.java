package com.mtgames.platformer.entities;

public abstract class AdvancedEntity extends Entity {

	protected int     speed          = 1;
	protected int     movingDir      = 1;
	protected int     xMin           = -1;
	protected int     xMax           = 1;
	protected int     yMin           = -1;
	protected int     yMax           = 1;
	protected int     animationFrame = 0;
	protected boolean isJumping      = false;
	private int walkingAnimationFrame = 0;

	protected AdvancedEntity(Properties properties, int x, int y) {
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

	protected boolean hasCollided(int xa, int ya) {
		this.xa = xa;
		this.ya = ya;

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