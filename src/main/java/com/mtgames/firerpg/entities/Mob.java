package com.mtgames.firerpg.entities;

import com.mtgames.firerpg.level.Level;
import com.mtgames.firerpg.level.Tile;

public abstract class Mob extends Entity {
	
	protected String	name;
	protected int		speed;
	protected int		gravity		= 1;
	protected int		gravityWait	= 0;
	protected int		movingDir;
	protected int		scale		= 1;
	protected boolean	isMoving;
	protected int		xMin;
	protected int		xMax;
	protected int		yMin;
	protected int		yMax;
	
	public Mob(Level level, String name, int x, int y, int speed) {
		super(level);
		this.name = name;
		this.x = x;
		this.y = y;
		this.speed = speed;
	}
	
	public void move(int xa, int ya) {
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
			for (int i = 0; i > xa * speed; i--) {
				if (!hasCollided(-1, 0)) {
					x -= 1;
				}
			}
		}
		
		if (xa > 0) {
			movingDir = 1;
			for (int i = 0; i < xa * speed; i++) {
				if (!hasCollided(1, 0)) {
					x += 1;
				}
			}
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
	
	protected boolean isSolidTile(int xa, int ya, int x, int y) {
		if (level == null)
			return false;
		
		Tile lastTile = level.getTile((this.x + x) >> 3, (this.y + y) >> 3);
		Tile newTile = level.getTile((this.x + x + xa) >> 3, (this.y + y + ya) >> 3);
		
		if (!lastTile.equals(newTile) && newTile.isSolid()) {
			return true;
		}
		
		return false;
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
		
		if (gravityWait > 2) {
			if (ya < 6)
				ya = ya + gravity;
			gravityWait = 0;
		}
		
		gravityWait++;
		return ya;
	}
	
	public String getName() {
		return name;
	}
}
