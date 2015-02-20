package com.mtgames.firerpg.entities;

import com.mtgames.firerpg.InputHandler;
import com.mtgames.firerpg.entities.particles.DashParticle;
import com.mtgames.firerpg.gfx.Screen;
import com.mtgames.firerpg.gfx.gui.Hud;
import com.mtgames.firerpg.level.Level;

public class Player extends Mob {
	
	private InputHandler	input;
	private int				xa			= 0;
	private int				xaDash		= 0;
	private int				ya			= 0;
	private int				dir;
	private int				modifier;
	private int				jumpWait	= 0;
	private int				dashWait	= 0;
	private int				dashTime	= 0;
	private int				staggerTime	= 0;
	public boolean			canJump		= false;
	public boolean			canDash		= true;
	public boolean			isStaggered	= false;
	public boolean			isJumping	= false;
	public boolean			isDashing	= false;
	
	public Player(Level level, int x, int y, InputHandler input) {
		super(level, "Player", x, y, 2);
		this.level = level;
		this.input = input;
		movingDir = 1;
		xMin = -4;
		xMax = 3;
		yMin = -7;
		yMax = 7;
	}
	
	public void tick() {
		if (staggerTime == 0) {
			isStaggered = false;
		} else {
			isStaggered = true;
		}
		
		if (!isStaggered) {
			if (hasCollided(0, 1) && jumpWait > 2) {
				canJump = true;
				jumpWait = 0;
			}
			
			if (hasCollided(0, 1) && !canJump) {
				jumpWait += 1;
			}
			
			if (!hasCollided(0, 1)) {
				canJump = false;
			}
			
			if (input.space.isPressed() && canJump) {
				ya = -5;
				canJump = false;
			}
			
			if (input.up.isPressed() && canDash) {
				xaDash = 5;
				canDash = false;
				dashWait = 0;
			}
			
			if (input.left.isPressed()) {
				xa--;
			}
			
			if (input.right.isPressed()) {
				xa++;
			}
			
		} else {
			staggerTime -= 1;
		}
		
		if (staggerTime > 20) {
			ya = 0;
		}
		
		if (!hasCollided(0, 1)) {
			isJumping = true;
		} else {
			isJumping = false;
		}
		
		if (xaDash != 0) {
			isDashing = true;
		} else {
			isDashing = false;
		}
		
		move(xa, ya);
		dash();
		ya = gravity(ya);
		
		if (isDashing) {
			int particleOffset;
			
			if (movingDir == 1) {
				particleOffset = -19;
			} else {
				particleOffset = 4;
			}
			
			for (int i = 0; i < 20; i++) {
				level.addParticle(new DashParticle(level, x, y, particleOffset));
			}
		}
	}
	
	public void render(Screen screen) {
		int xTile = 0;
		int yTile = 27;
		
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
		
		if (isJumping && !isDashing) {
			xTile = 2;
		}
		
		if (isDashing) {
			xTile = 4;
		}
		
		if (isStaggered) {
			xTile = 6;
		}
		
		screen.render(xOffset + modifier, yOffset, xTile + yTile * 32, dir);
		screen.render(xOffset + 8 - modifier, yOffset, (xTile + 1) + yTile * 32, dir);
		screen.render(xOffset + modifier, yOffset + 8, xTile + (yTile + 1) * 32, dir);
		screen.render(xOffset + 8 - modifier, yOffset + 8, (xTile + 1) + (yTile + 1) * 32, dir);
		
		/* Draw overlay on character */
		/*
		 * screen.render(xOffset+modifier, yOffset, (xTile+2)+yTile*32, colour,
		 * dir);
		 */
		/*
		 * screen.render(xOffset+8-modifier, yOffset, (xTile+3)+yTile*32,
		 * colour, dir);
		 */
		
		Hud.renderDash(screen, dashWait);
	}
	
	private void dash() {
		if (dashWait >= 72 || canDash == true) {
			dashWait = 72;
		} else {
			dashWait++;
		}
		
		if (dashWait == 72 && !hasCollided(0, 1)) {
			canDash = true;
		} else {
			canDash = false;
		}
		
		if (xaDash == 0) {
			xa = 0;
			return;
		}
		
		if (hasCollided(-1, 0) || hasCollided(1, 0)) {
			staggerTime = 30;
			xaDash = 0;
		}
		
		ya = 0;
		gravityWait = 0;
		
		if (movingDir == 0) {
			xa = -xaDash;
		} else {
			xa = xaDash;
		}
		
		if (dashTime > 2) {
			xaDash--;
			dashTime = 0;
		}
		
		dashTime++;
	}
}
