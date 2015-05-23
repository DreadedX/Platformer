package com.mtgames.platformer.entities;

public abstract class BasicEntity extends Entity {
	protected int x;
	protected int y;

	protected BasicEntity(Properties properties, int x, int y) {
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

	public boolean hasCollided(int xa, int ya) {
		return isSolidTile(xa, ya, x, y);
	}
}
