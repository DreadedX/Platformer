package com.mtgames.firerpg.entities;

import com.mtgames.firerpg.InputHandler;
import com.mtgames.firerpg.entities.particles.DashParticle;
import com.mtgames.firerpg.gfx.Screen;
import com.mtgames.firerpg.gfx.gui.Hud;
import com.mtgames.firerpg.level.Level;
import com.mtgames.firerpg.level.Script;

public class Player extends Mob {
	
	private final static String	NAME		= "Player";
	private final int			JUMPWAIT;
	private final int			JUMPSPEED;
	private final int			DASHSPEED;
	private final int			DASHWAIT;
	private final int			STAGGERLENGTH;
	
	private InputHandler		input;
	private Script				script;
	
	private int					xa			= 0;
	private int					xaDash		= 0;
	private int					ya			= 0;
	private int					dir;
	private int					modifier;
	private int					jumpWait	= 0;
	private int					dashWait	= 0;
	private int					dashTime	= 0;
	private int					staggerTime	= 0;
	public boolean				canJump		= false;
	public boolean				canDash		= true;
	public boolean				isStaggered	= false;
	public boolean				isDashing	= false;
	
	public Player(Level level, int x, int y, InputHandler input) {
		super(level, NAME, x, y);
		
		this.script = new Script("scripts/Player.js");
		script.doInit();
		
		JUMPWAIT = (int) script.get("JUMPWAIT");
		JUMPSPEED = (int) script.get("JUMPSPEED");
		DASHSPEED = (int) script.get("DASHSPEED");
		DASHWAIT = (int) script.get("DASHWAIT");
		STAGGERLENGTH = (int) script.get("STAGGERLENGTH");
		
		speed = (int) script.get("speed");
		xMin = (int) script.get("xMin");
		xMax = (int) script.get("xMax");
		yMin = (int) script.get("yMin");
		yMax = (int) script.get("yMax");
		
		this.level = level;
		this.input = input;
	}
	
	public void tick() {
		script.set("x", x);
		script.set("xa", xa);
		script.invoke("tick");
		xa = (int) script.get("xa");
		x = (int) script.get("x");
		
		if (input.reload.isPressed()) {
			script.load();
		}
		
		if (staggerTime == 0) {
			isStaggered = false;
		} else {
			isStaggered = true;
		}
		
		if (!isStaggered) {
			if (hasCollided(0, 1) && jumpWait > JUMPWAIT) {
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
				ya = -JUMPSPEED;
				canJump = false;
				animationFrame = 0;
			}
			
			if (input.up.isPressed() && canDash) {
				xaDash = DASHSPEED;
				canDash = false;
				dashWait = 0;
				animationFrame = 0;
			}
			
			if (input.left.isPressed()) {
				xa--;
			}
			
			if (input.right.isPressed()) {
				xa++;
			}
			
		} else {
			staggerTime -= 1;
			ya = 0;
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
		
		if (isJumping && !isDashing && !isStaggered) {
			xTile = 8;
		} else if (isDashing) {
			xTile = 12;
			animationFrame = 0;
		} else if (isStaggered) {
			xTile = 14;
			animationFrame = 0;
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
		
		/* Draw overlay on character */
		/*
		 * screen.render(xOffset+modifier, yOffset, (xTile+2)+yTile*32, colour,
		 * dir);
		 */
		/*
		 * screen.render(xOffset+8-modifier, yOffset, (xTile+3)+yTile*32,
		 * colour, dir);
		 */
		
		double dashRatio = ((dashWait * 10d) / (DASHWAIT * 10d));
		Hud.renderDash(screen, dashRatio);
	}
	
	private void dash() {
		if (dashWait >= DASHWAIT || canDash == true) {
			dashWait = DASHWAIT;
		} else {
			dashWait++;
		}
		
		if (dashWait == DASHWAIT && !hasCollided(0, 1)) {
			canDash = true;
		} else {
			canDash = false;
		}
		
		if (xaDash == 0) {
			xa = 0;
			return;
		}
		
		if (hasCollided(-1, 0) || hasCollided(1, 0)) {
			staggerTime = STAGGERLENGTH;
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
