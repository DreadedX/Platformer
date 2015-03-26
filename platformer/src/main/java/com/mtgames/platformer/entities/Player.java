package com.mtgames.platformer.entities;

import com.mtgames.platformer.InputHandler;
import com.mtgames.platformer.entities.particles.DashParticle;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.gfx.Sheet;
import com.mtgames.platformer.gfx.gui.Hud;
import com.mtgames.platformer.gfx.gui.Text;
import com.mtgames.platformer.level.Level;
import com.mtgames.platformer.level.Script;

public class Player extends Mob {

	private final int JUMPWAIT;
	private final int JUMPSPEED;
	private final int DASHSPEED;
	private final int DASHWAIT;
	private final int STAGGERLENGTH;
	private final int MAXHEALTH;

	private final InputHandler input;
	private final Script       script;
	private final Sheet sheet = new Sheet("/graphics/sprite_sheet.png");

	private int xa     = 0;
	private int xaDash = 0;
	private int ya     = 0;
	private int dir;
	private int modifier;
	private int jumpWait    = 0;
	private int dashWait    = 0;
	private int dashTime    = 0;
	private int staggerTime = 0;

	private int health;

	private boolean canJump     = false;
	private boolean canDash     = true;
	private boolean isStaggered = false;
	private boolean isDashing   = false;

	public Player(Level level, int x, int y, InputHandler input) {
		super(level, x, y);

		this.script = new Script("scripts/Player.js");
		script.doInit();

		JUMPWAIT = (int) script.get("JUMPWAIT");
		JUMPSPEED = (int) script.get("JUMPSPEED");
		DASHSPEED = (int) script.get("DASHSPEED");
		DASHWAIT = (int) script.get("DASHWAIT");
		STAGGERLENGTH = (int) script.get("STAGGERLENGTH");

		health = MAXHEALTH = (int) script.get("MAXHEALTH");

		speed = (int) script.get("speed");
		xMin = (int) script.get("xMin");
		xMax = (int) script.get("xMax");
		yMin = (int) script.get("yMin");
		yMax = (int) script.get("yMax");

		this.level = level;
		this.input = input;
	}

	public void tick() {
		if (input.reload.isPressed()) {
			script.load();
		}

		isStaggered = staggerTime != 0;

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

			if (input.space.isPressed() && canJump && isAlive()) {
				ya = -JUMPSPEED;
				canJump = false;
				animationFrame = 0;
			}

			if (input.up.isPressed() && canDash && isAlive()) {
				xaDash = DASHSPEED;
				canDash = false;
				dashWait = 0;
				animationFrame = 0;
			}

			if (input.left.isPressed() && isAlive()) {
				xa -= speed;
			}

			if (input.right.isPressed() && isAlive()) {
				xa += speed;
			}

		} else {
			staggerTime -= 1;
			ya = 0;
		}

		isDashing = xaDash != 0;

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

			for (int i = 0; i < 40; i++) {
				level.addParticle(new DashParticle(level, x, y, particleOffset));
			}
		}

		//		Scripting
		script.set("x", x);
		script.set("xa", xa);
		script.set("y", y);
		script.set("ya", ya);
		script.set("health", health);
		script.invoke("tick");
		xa = (int) script.get("xa");
		x = (int) script.get("x");
		ya = (int) script.get("ya");
		y = (int) script.get("y");
		health = (int) script.get("health");
	}

	public void render(Screen screen) {
		int xTile = 0;
		int yTile = 27;

		int xOffset = x;
		int yOffset = y;

		switch (movingDir) {
			case 0:
				dir = 0x01;
				modifier = 16;
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

		screen.render(xOffset - 16 + modifier, yOffset - 16, sheet, xTile + yTile * 32, dir);
		screen.render(xOffset - modifier, yOffset - 16, sheet, (xTile + 1) + yTile * 32, dir);
		screen.render(xOffset - 16 + modifier, yOffset, sheet, xTile + (yTile + 1) * 32, dir);
		screen.render(xOffset - modifier, yOffset, sheet, (xTile + 1) + (yTile + 1) * 32, dir);

		double dashRatio = ((dashWait * 10d) / (DASHWAIT * 10d));
		Hud.setDash(dashRatio);
		double healthRatio = ((health * 10d) / (MAXHEALTH * 10d));
		Hud.setHealth(healthRatio);

		//		TEMP DEATH CODE
		if (!isAlive()) {
			Text.textBox(screen, "YOU DIED!", "");
		}
	}

	private void dash() {
		if (dashWait >= DASHWAIT || canDash) {
			dashWait = DASHWAIT;
		} else {
			dashWait++;
		}

		canDash = dashWait == DASHWAIT && !hasCollided(0, 1);

		if (xaDash == 0) {
			xa = 0;
			return;
		}

		if (hasCollided(-1, 0) || hasCollided(1, 0)) {
			staggerTime = STAGGERLENGTH;
			xaDash = 0;
			health -= 10;
		}

		ya = 0;
		gravityWait = 0;

		if (movingDir == 0) {
			xa = -xaDash * speed;
		} else {
			xa = xaDash * speed;
		}

		if (dashTime > 2) {
			xaDash--;
			dashTime = 0;
		}

		dashTime++;
	}

	boolean isAlive() {
		return health > 0;
	}

}
