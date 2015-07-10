package com.mtgames.platformer.entities;

import com.mtgames.platformer.level.Level;
import com.mtgames.platformer.scripting.JythonFactory;
import com.mtgames.platformer.scripting.interfaces.EntityInterface;
import com.mtgames.platformer.settings.Properties;
import com.mtgames.utils.Debug;

import java.util.Objects;

public class AdvancedEntity extends Entity {

	protected int     speed                 = 1;
	protected int     movingDir             = 1;
	protected int     animationFrame        = 0;
	protected boolean isJumping             = false;
	private   int     walkingAnimationFrame = 0;

	private final EntityInterface entityInterface;

	public AdvancedEntity(Properties properties, int x, int y, String path) {
		super(properties);
		this.x = x;
		this.y = y;

		xMin = properties.getXMin();
		xMax = properties.getXMax();
		yMin = properties.getYMin();
		yMax = properties.getYMax();

		entityInterface = (EntityInterface) JythonFactory.getJythonObject("com.mtgames.platformer.scripting.interfaces.EntityInterface", path);
		entityInterface.init(this);

		Debug.log(name, Debug.DEBUG);
	}

	public void tick() {
		entityInterface.tick(this);
	}

	public void render() {
		entityInterface.render(this);

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

		if (xa != 0 && ya == 0 && !hasCollided(-1, 0) && !hasCollided(1, 0)) {
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
		this.xa = xa;
		this.ya = ya;

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

	private void walkingAnimation() {
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

//	TODO: This code is not completly correct
	public boolean hasCollidedEntity(String name) {
		for (int i = 0; i < Level.entities.size(); i++) {
			Entity e = Level.entities.get(i);
			if (!e.collide) {
				continue;
			}
			if (Objects.equals(e.getProperties().getName(), name)) {
				if (x >= (e.xMin + e.x) && x <= (e.xMax + e.x) && y >= (e.yMin + e.y) && y <= (e.yMax + e.y)) {
					return true;
				}
			}
		}
		return false;
	}
}