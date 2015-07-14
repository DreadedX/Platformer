package com.mtgames.platformer.settings;

import com.mtgames.utils.JSP;
import com.sun.javafx.geom.Vec3f;
import org.json.JSONObject;

public class Properties {
	private int   jumpWait      = 0;
	private int   jumpSpeed     = 0;
	private int   dashSpeed     = 0;
	private int   dashWait      = 0;
	private int   staggerLength = 0;
	private int   maxHealth     = 0;
	private int   speed         = 0;
	private int   xMin          = 0;
	private int   xMax          = 0;
	private int   yMin          = 0;
	private int   yMax          = 0;
	private int   radius        = 80;
	private float intensity     = 1.0f;
	private String execute		= "";
	private Vec3f colour        = new Vec3f(1.0f, 1.0f, 1.0f);

	private String name = "";

	private static final JSP jsp = new JSP("base.jsp", false);

	public Properties(String type) {
		if (type.equals("")) {
			return;
		}

		set(jsp.get(type));
		name = type;
	}

	public void set(JSONObject obj) {
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
			intensity = obj.getInt("intensity")/1000f;
		}
		if (obj.has("execute")) {
			execute = obj.getString("execute");
		}
		if (obj.has("colour")) {
			int hexColour = obj.getInt("colour");

			int colourR = (hexColour >> 16);
			int colourG = (hexColour >> 8) - (colourR << 8);
			int colourB = (hexColour) - (colourR << 16) - (colourG << 8);

			colour = new Vec3f((float) colourR/0xff, (float) colourG/0xff, (float) colourB/0xff);
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

	public String getExecute() {
		return execute;
	}

	public Vec3f getColour() {
		return colour;
	}

	public String getName() {
		return name;
	}
}
