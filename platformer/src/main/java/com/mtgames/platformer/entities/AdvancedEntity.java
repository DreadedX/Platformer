package com.mtgames.platformer.entities;

import com.mtgames.platformer.settings.Properties;

public abstract class AdvancedEntity extends Entity {

	protected int     speed          = 1;
	protected int     movingDir      = 1;
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

	public boolean hasCollided(int xa, int ya) {
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

//	TODO: This code is not completly correct
	public boolean hasCollidedEntity(String name) {
		for (int i = 0; i < level.entities.size(); i++) {
			Entity e = level.entities.get(i);
			if (!e.collide) {
				continue;
			}
			if (e.getClass().getSimpleName().equals(name)) {
				if (x >= (e.xMin + e.x) && x <= (e.xMax + e.x) && y >= (e.yMin + e.y) && y <= (e.yMax + e.y)) {
					return true;
				}
			}
		}
		return false;
	}
}