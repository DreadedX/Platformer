package com.mtgames.platformer.entities;

import com.mtgames.platformer.InputHandler;
import com.mtgames.platformer.entities.particles.DashParticle;
import com.mtgames.platformer.entities.particles.Torch;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.gfx.Sheet;
import com.mtgames.platformer.gfx.gui.Hud;
import com.mtgames.platformer.gfx.gui.Text;
import com.mtgames.platformer.gfx.lighting.LightSource;
import org.json.JSONObject;

import static org.lwjgl.glfw.GLFW.*;

public class Player extends Mob {

	private final int JUMPWAIT;
	private final int JUMPSPEED;
	private final int DASHSPEED;
	private final int DASHWAIT;
	private final int STAGGERLENGTH;
	private final int MAXHEALTH;

	private final InputHandler input;
	private final Sheet sheet = new Sheet("/assets/graphics/entities/player.png");

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

	private final LightSource lightSource;

	public Player(int x, int y, Properties properties) {
		super(properties, x, y);

		level.addLightSource(lightSource = new LightSource(x, y, 0, 0xffae00));

		JUMPWAIT = properties.getJumpWait();
		JUMPSPEED = properties.getJumpSpeed();
		DASHSPEED = properties.getDashSpeed();
		DASHWAIT = properties.getDashWait();
		STAGGERLENGTH = properties.getStaggerLength();

		health = MAXHEALTH = properties.getMaxHealth();

		speed = properties.getSpeed();
		xMin = properties.getXMin();
		xMax = properties.getXMax();
		yMin = properties.getYMin();
		yMax = properties.getYMax();

		this.input = properties.getInput();
	}

	public void tick() {
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

			if (input.isPressed(GLFW_KEY_SPACE) && canJump && isAlive()) {
				ya = -JUMPSPEED;
				canJump = false;
				animationFrame = 0;
			}

			if (input.isPressed(GLFW_KEY_W) && canDash && isAlive()) {
				xaDash = DASHSPEED;
				canDash = false;
				dashWait = 0;
				animationFrame = 0;
			}

			if (input.isPressed(GLFW_KEY_A) && isAlive() && !isDashing) {
				xa -= speed;
			}

			if (input.isPressed(GLFW_KEY_D) && isAlive() && !isDashing) {
				xa += speed;
			}

			if (input.isPressed(GLFW_KEY_Q) && isAlive()) {
//				level.addParticle(new Glowstick(x, y, movingDir, new Properties("glowstick")));
				Properties properties = new Properties("torch");
				properties.set(new JSONObject("{\"colour\":" + Math.random() * 0xffffff +"}"));
				level.addParticle(new Torch(x, y, properties));
				input.set(GLFW_KEY_Q, false);
			}

		} else {
			staggerTime -= 1;
			ya = 0;
		}

		isDashing = xaDash != 0;

		if (xa > 0) {
			xa -= 1;
		} else if (xa < 0) {
			xa += 1;
		}

		if (xa > speed && !isDashing) {
			xa = speed;
		} else if (xa < -speed && !isDashing) {
			xa = -speed;
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

			for (int i = 0; i < 40; i++) {
				level.addParticle(new DashParticle(x, y, particleOffset, new Properties("dashParticle")));
			}
		}
		lightSource.move(x, y);
	}

	public void render(Screen screen) {
		int xTile = 0;

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

//		screen.render(xOffset - 16 + modifier, yOffset - 16, sheet, xTile, dir);
//		screen.render(xOffset - modifier, yOffset - 16, sheet, xTile + 1, dir);
//		screen.render(xOffset - 16 + modifier, yOffset, sheet, xTile + sheet.width/16, dir);
//		screen.render(xOffset - modifier, yOffset, sheet, xTile + 1 + sheet.width / 16, dir);

//		screen.addLighting(x, y, 0, 0xffae00);

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
//			xa = 0;
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

	private boolean isAlive() {
		return health > 0;
	}

}
