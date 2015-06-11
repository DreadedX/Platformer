package com.mtgames.platformer.entities;

import com.mtgames.platformer.InputHandler;
import com.mtgames.platformer.debug.Command;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.gfx.gui.GUI;
import com.mtgames.platformer.gfx.shaders.TextureLoader;
import com.mtgames.platformer.scripting.JythonFactory;
import com.mtgames.platformer.scripting.interfaces.PlayerInterface;
import com.mtgames.platformer.settings.Properties;
import com.mtgames.utils.Debug;
import com.sun.javafx.geom.Vec3f;

import static com.mtgames.platformer.settings.Settings.*;

public class Player extends AdvancedEntity {

	private final int JUMPWAIT;
	private final int JUMPSPEED;
	private final int DASHSPEED;
	private final int DASHWAIT;
	private final int STAGGERLENGTH;
	private final int MAXHEALTH;

	private final InputHandler input;
	private static final int[] textureID = TextureLoader.loadTextureArray("/assets/graphics/entities/player", 8);

	private int xa          = 0;
	private int xaDash      = 0;
	private int ya          = 0;
	private int jumpWait    = 0;
	private int dashWait    = 0;
	private int dashTime    = 0;
	public int staggerTime = 0;

	private boolean canJump     = false;
	private boolean canDash     = true;
	private boolean isStaggered = false;
	private boolean isDashing   = false;
	private boolean dashDeplete = false;

	private PlayerInterface pi;

	public Player(int x, int y, Properties properties) {
		super(properties, x, y);

		JUMPWAIT = properties.getJumpWait();
		JUMPSPEED = properties.getJumpSpeed();
		DASHSPEED = properties.getDashSpeed();
		DASHWAIT = properties.getDashWait();
		STAGGERLENGTH = properties.getStaggerLength();

		life = MAXHEALTH = properties.getMaxHealth();

		speed = properties.getSpeed();
		xMin = properties.getXMin();
		xMax = properties.getXMax();
		yMin = properties.getYMin();
		yMax = properties.getYMax();

		input = properties.getInput();
		persistent = true;

		collide = true;
		pi = (PlayerInterface) JythonFactory.getJythonObject("com.mtgames.platformer.scripting.interfaces.PlayerInterface", "src/main/python/entities/Player.py");
		pi.init(this);
	}

	public void tick() {
		pi.tick(this);

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

			if (input.isPressed(KEY_JUMP) && canJump && isAlive()) {
				ya = -JUMPSPEED;
				canJump = false;
				animationFrame = 0;
			}

			if (input.isPressed(KEY_DASH) && canDash && isAlive()) {
				xaDash = DASHSPEED;
				canDash = false;
//				dashWait = 0;
				dashDeplete = true;
				animationFrame = 0;
			}

			if (input.isPressed(KEY_LEFT) && isAlive() && !isDashing) {
				xa -= speed;
			}

			if (input.isPressed(KEY_RIGHT) && isAlive() && !isDashing) {
				xa += speed;
			}

			if (input.isPressed(KEY_TORCH) && isAlive()) {
//				level.addEntity(new GlowStick(x, y, movingDir, new Properties("glowStick")));
//				Properties properties = new Properties("torch");
//				properties.set(new JSONObject("{\"colour\":" + (int) (Math.random() * 0xffffff) + "}"));
//				level.addEntity(new Torch(x, y, properties));

				Command.exec("light torch " + x + " " + y + " {\"colour\":" + (int) (Math.random() * 0xffffff) + "}");

				input.unset(KEY_TORCH);
			}

		} else {
			staggerTime -= 1;
			ya = 0;
		}

		isDashing = xaDash != 0;

		if (xa > 1) {
			xa -= 2;
		} else if (xa < -1) {
			xa += 2;
		} else if (xa == 1 || xa == -1) {
			xa = 0;
			Debug.log("TEST", Debug.DEBUG);
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
			for (int i = 0; i < 10; i++) {
				Command.exec("light dashParticle " + x + " " + y);
			}
		}

		if (hasCollidedEntity("BaseEnemy")) {
			life--;
			staggerTime = STAGGERLENGTH;
		}
//		TODO: Enable this again
//		lightSource.move(x, y);
	}

	public void render(Screen screen) {
		int xTile = 0;

		boolean flipX = false;

		if (movingDir == 0) {
			flipX = true;
		}

		if (isJumping && !isDashing && !isStaggered) {
			xTile = 4;
		} else if (isDashing) {
			xTile = 6;
			animationFrame = 0;
		} else if (isStaggered) {
			xTile = 7;
			animationFrame = 0;
		}

		if (animationFrame == 1) {
			xTile += 1;
		} else if (animationFrame == 2) {
			xTile += 2;
		} else if (animationFrame == 3) {
			xTile += 3;
		}

		screen.renderEntity(x, y, textureID[xTile], 16, flipX);
		pi.render(this, screen);

//		screen.addLighting(x, y, 0, 0xffae00);

		float healthRatio = ((life * 10f) / (MAXHEALTH * 10f));
		GUI.add(() -> GUI.progressBar(80, 13, 16, 150, healthRatio, new Vec3f(0.4f, 0.1f, 0.1f)));
		float dashRatio = ((dashWait * 10f) / (DASHWAIT * 10f));
		Vec3f colour;
		if (dashRatio == 1.0f) {
			colour = new Vec3f(0.1f, 0.1f, 0.5f);
		} else {
			colour = new Vec3f(0.1f, 0.1f, 0.4f);
		}

		GUI.add(() -> GUI.progressBar(screen.width-80, 13, 16, 150, dashRatio, colour));

		//		TEMP DEATH CODE
		if (!isAlive()) {
			GUI.add(() -> GUI.textBox("YOU DIED!", ""));
		}
	}

	private void dash() {
		if (dashWait >= DASHWAIT || canDash) {
			dashWait = DASHWAIT;
		} else {
			dashWait++;
		}

		canDash = dashWait == DASHWAIT && !hasCollided(0, 1);

		if (dashDeplete) {
			dashWait -= 5;
		}

		if (dashWait <= 0) {
			dashDeplete = false;
		}

		if (xaDash == 0) {
//			xa = 0;
			return;
		}

		if (hasCollided(-1, 0) || hasCollided(1, 0)) {
			staggerTime = STAGGERLENGTH;
			dashDeplete = false;
			xaDash = 0;
			life -= 10;
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
}
