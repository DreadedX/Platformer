package com.mtgames.firerpg.entities.enemies;

import com.mtgames.firerpg.entities.Mob;
import com.mtgames.firerpg.gfx.Screen;
import com.mtgames.firerpg.level.Level;

public class BasicEnemy extends Mob {
	
	private int		xa			= 0;
	private int		ya			= 0;
	private int		dir;
	private int		modifier;
	public boolean	canJump		= false;
	public boolean	canDash		= true;
	public boolean	isStaggered	= false;
	public boolean	isDashing	= false;
	
	public BasicEnemy(Level level, int x, int y) {
		super(level, "Base Enemy", x, y, 1);
		this.level = level;
		movingDir = (int) (Math.random() + .5);
		xMin = -4;
		xMax = 3;
		yMin = -7;
		yMax = 7;
	}
	
	public void tick() {
		xa = 0;
		
		if (!hasCollided(3, -8) && hasCollided(3, 0)) {
			ya = -3;
			xa++;
		} else if (!hasCollided(-3, -8) && hasCollided(-3, 0)) {
			ya = -3;
			xa--;
		} else if (hasCollided(-3, 0)) {
			movingDir = 1;
			xa++;
		} else if (hasCollided(3, 0)) {
			movingDir = 0;
			xa--;
		} else {
			if (movingDir == 0) {
				xa--;
			}
			
			if (movingDir == 1) {
				xa++;
			}
		}
		
		move(xa, ya);
		ya = gravity(ya);
	}
	
	public void render(Screen screen) {
		int xTile = 0;
		int yTile = 25;
		
		int xOffset = x - 9;
		int yOffset = y - 8;
		
		switch (movingDir) {
			case 0:
				dir = 0x01;
				modifier = 8;
				xOffset += 2;
				break;
			
			case 1:
				dir = 0x00;
				modifier = 0;
				break;
		}
		
		if (isJumping) {
			xTile = 8;
		}

		if (animationFrame == 1) {
			xTile += 2;
		} else if (animationFrame == 2) {
			xTile += 4;
		} else if (animationFrame == 3) {
			xTile += 6;
		}
		
		screen.render(xOffset + modifier, yOffset, xTile + yTile * 32, dir);
		screen.render(xOffset + 8 - modifier, yOffset, (xTile + 1) + yTile * 32, dir);
		screen.render(xOffset + modifier, yOffset + 8, xTile + (yTile + 1) * 32, dir);
		screen.render(xOffset + 8 - modifier, yOffset + 8, (xTile + 1) + (yTile + 1) * 32, dir);
	}
}
