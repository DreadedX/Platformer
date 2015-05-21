package com.mtgames.platformer.entities;

import com.mtgames.platformer.InputHandler;
import com.mtgames.platformer.level.Level;
import com.mtgames.utils.Debug;
import com.sun.javafx.geom.Vec3f;
import org.json.JSONObject;

public class Properties {
	private int jumpWait      = 0;
	private int jumpSpeed     = 0;
	private int dashSpeed     = 0;
	private int dashWait      = 0;
	private int staggerLength = 0;
	private int maxHealth     = 0;
	private int speed         = 0;
	private int xMin          = 0;
	private int xMax          = 0;
	private int yMin          = 0;
	private int yMax          = 0;
	private int radius		  = 80;
	private float intensity	  = 1.0f;
	private Vec3f colour		  = new Vec3f(1.0f, 1.0f, 1.0f);

	private final Level        level = com.mtgames.platformer.Game.level;
	private final InputHandler input = com.mtgames.platformer.Game.input;

	public Properties(String type) {
		switch (type) {
			case "autoScroll":
				speed = 2;
				break;

			case "freeCamera":
//				Light
				colour = new Vec3f(1.0f, 0.68f, 0.0f);
				break;

			case "player":
//				General
				jumpWait = 2;
				jumpSpeed = 9;
				dashSpeed = 5;
				dashWait = 72;
				staggerLength = 20;
				maxHealth = 100;
				speed = 6;

//				Hitbox
				xMin = -10;
				xMax = 8;
				yMin = -16;
				yMax = 15;
				break;

			case "baseEnemy":
//				General
				jumpSpeed = 4;
				speed = 2;

//				Hitbox
				xMin = -8;
				xMax = 5;
				yMin = -13;
				yMax = 15;

//				Light
				colour = new Vec3f(1.0f, 0.68f, 0.0f);
				break;

			case "dashParticle":
				colour = new Vec3f(0.2f, 0.3f, 1f);
				radius = 2;
				intensity = 0.7f;
				break;

			case "glowStick":
				colour = new Vec3f(0.0f, 0.68f, 0.0f);
				radius = 100;
				break;

			case "torch":
				colour = new Vec3f(1.0f, 1.0f, 1.0f);
				radius = 150;
		}
	}

	public void set(JSONObject obj) {
		Debug.log("Setting properties: " + String.valueOf(obj), Debug.DEBUG);

		if (obj.has("jumpWait")) {
			jumpWait = obj.getInt("jumpWait");
		}
		if (obj.has("jumpSpeed")) {
			jumpSpeed = obj.getInt("jumpSpeed");
		}
		if (obj.has("dashSpeed")) {
			dashSpeed = obj.getInt("dashSpeed");
		}
		if (obj.has("dashWait")) {
			dashWait = obj.getInt("dashWait");
		}
		if (obj.has("staggerLength")) {
			staggerLength = obj.getInt("staggerLength");
		}
		if (obj.has("maxHealth")) {
			maxHealth = obj.getInt("maxHealth");
		}
		if (obj.has("speed")) {
			speed = obj.getInt("speed");
		}
		if (obj.has("xMin")) {
			xMin = obj.getInt("xMin");
		}
		if (obj.has("xMax")) {
			xMax = obj.getInt("xMax");
		}
		if (obj.has("yMin")) {
			yMin = obj.getInt("yMin");
		}
		if (obj.has("yMax")) {
			yMax = obj.getInt("yMax");
		}
		if (obj.has("radius")) {
			radius = obj.getInt("radius");
		}
		if (obj.has("intensity")) {
			radius = obj.getInt("intensity");
		}
		if (obj.has("colour")) {
			int hexColour = obj.getInt("colour");

			int colourR = (hexColour >> 16);
			int colourG = (hexColour >> 8) - (colourR << 8);
			int colourB = (hexColour) - (colourR << 16) - (colourG << 8);

			colour = new Vec3f((float) colourR/0xff, (float) colourG/0xff, (float) colourB/0xff);

			Debug.log(colour.x + " " + colour.y + " " + colour.z, Debug.DEBUG);
		}
	}

	public int getJumpWait() {
		return jumpWait;
	}

	public int getJumpSpeed() {
		return jumpSpeed;
	}

	public int getDashSpeed() {
		return dashSpeed;
	}

	public int getDashWait() {
		return dashWait;
	}

	public int getStaggerLength() {
		return staggerLength;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public int getSpeed() {
		return speed;
	}

	public int getXMin() {
		return xMin;
	}

	public int getXMax() {
		return xMax;
	}

	public int getYMin() {
		return yMin;
	}

	public int getYMax() {
		return yMax;
	}

	public int getRadius() {
		return radius;
	}

	public float getIntensity() {
		return intensity;
	}

	public Vec3f getColour() {
		return colour;
	}

	public Level getLevel() {
		return level;
	}

	public InputHandler getInput() {
		return input;
	}
}
