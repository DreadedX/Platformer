package com.mtgames.firerpg.entities;

import com.mtgames.firerpg.level.Level;
import com.mtgames.firerpg.level.Tile;

public abstract class Mob extends Entity {

    private final String	name;
	protected int		speed					= 1;
    int		gravityWait				= 0;
	protected int		movingDir				= 1;
    protected int		xMin					= -1;
	protected int		xMax					= 1;
	protected int		yMin					= -1;
	protected int		yMax					= 1;
	protected int		animationFrame			= 0;
	private int		walkingAnimationFrame	= 0;
	protected boolean	isJumping				= false;
	
	protected Mob(Level level, String name, int x, int y) {
		super(level);
		this.name = name;
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
		
		if (xa != 0 && ya == 0 && !hasCollided(-1, 0) && !hasCollided(1,0)) {
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
	
	boolean isSolidTile(int xa, int ya, int x, int y) {
		if (level == null)
			return false;
		
		Tile lastTile = level.getTile((this.x + x) >> 3, (this.y + y) >> 3);
		Tile newTile = level.getTile((this.x + x + xa) >> 3, (this.y + y + ya) >> 3);

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
		
		if (gravityWait > 2) {
            int gravity = 1;
            if (ya < 6)
				ya = ya + gravity;
			gravityWait = 0;
		}
		
		gravityWait++;
		return ya;
	}
	
	void walkingAnimation() {
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
	
	public String getName() {
		return name;
	}
}