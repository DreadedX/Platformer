package com.mtgames.firerpg.entities.enemies;

import com.mtgames.firerpg.entities.Mob;
import com.mtgames.firerpg.gfx.Screen;
import com.mtgames.firerpg.level.Level;
import com.mtgames.firerpg.level.Script;

public class BasicEnemy extends Mob {
	
	private final static String	NAME		= "BasicEnemy";
	private final int			JUMPSPEED;
	
	private final Script		script;

    private int			ya			= 0;
	private int			dir;
	private int			modifier;

	public BasicEnemy(Level level, int x, int y) {
		super(level, NAME, x, y);
		
		this.script = new Script("scripts/BasicEnemy.js");
		script.doInit();
		
		JUMPSPEED = (int) script.get("JUMPSPEED");

		speed = (int) script.get("speed");
		xMin = (int) script.get("xMin");
		xMax = (int) script.get("xMax");
		yMin = (int) script.get("yMin");
		yMax = (int) script.get("yMax");
		movingDir = (int) (Math.random() + .5);

		this.level = level;
	}
	
	public void tick() {
		script.invoke("tick");

        int xa = 0;
		
		if (!hasCollided(3, -8) && hasCollided(3, 0)) {
			ya = -JUMPSPEED;
			xa += speed;
		} else if (!hasCollided(-3, -8) && hasCollided(-3, 0)) {
			ya = -JUMPSPEED;
			xa -= speed;
		} else if (hasCollided(-3, 0)) {
			movingDir = 1;
			xa += speed;
		} else if (hasCollided(3, 0)) {
			movingDir = 0;
			xa -= speed;
		} else {
			if (movingDir == 0) {
				xa -= speed;
			}
			
			if (movingDir == 1) {
				xa += speed;
			}
		}
		
		move(xa, ya);
		ya = gravity(ya);
	}
	
	public void render(Screen screen) {
		int xTile = 0;
		int yTile = 25;
		
		int xOffset = x;
		int yOffset = y;
		
		switch (movingDir) {
			case 0:
				dir = 0x01;
				modifier = 8;
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
		
		screen.render(xOffset - 8 + modifier, yOffset - 8, xTile + yTile * 32, dir);
		screen.render(xOffset - modifier, yOffset - 8, (xTile + 1) + yTile * 32, dir);
		screen.render(xOffset - 8 + modifier, yOffset, xTile + (yTile + 1) * 32, dir);
		screen.render(xOffset - modifier, yOffset, (xTile + 1) + (yTile + 1) * 32, dir);
	}
}
