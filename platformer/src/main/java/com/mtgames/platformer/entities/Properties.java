package com.mtgames.platformer.entities;

import com.mtgames.platformer.InputHandler;
import com.mtgames.platformer.debug.Debug;
import com.mtgames.platformer.level.Level;
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
	private int colour		  = 0;

	private final Level        level = com.mtgames.platformer.Game.level;
	private final InputHandler input = com.mtgames.platformer.Game.input;

	public Properties(String type) {
		switch (type) {
			case "autoScroll":
				speed = 2;
				break;

			case "player":
				jumpWait = 2;
				jumpSpeed = 9;
				dashSpeed = 5;
				dashWait = 72;
				staggerLength = 20;
				maxHealth = 100;
				speed = 6;

				xMin = -10;
				xMax = 8;
				yMin = -16;
				yMax = 15;
				break;

			case "baseEnemy":
				jumpSpeed = 4;
				speed = 2;

				xMin = -8;
				xMax = 5;
				yMin = -13;
				yMax = 15;
				break;

			case "torch":
				colour = 0;
		}
	}

	public void set(JSONObject obj) {
		Debug.log("Properties: " + obj);
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
		if (obj.has("colour")) {
			colour = obj.getInt("colour");
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

	public int getColour() {
		return colour;
	}

	public Level getLevel() {
		return level;
	}

	public InputHandler getInput() {
		return input;
	}
}
