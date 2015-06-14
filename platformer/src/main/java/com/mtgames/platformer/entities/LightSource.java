package com.mtgames.platformer.entities;

import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.settings.Properties;
import com.sun.javafx.geom.Vec3f;

public class LightSource {
	private int life = 0;
	private final Vec3f colour;
	private final int   radius;
	private final float intensity;
	private       int   x;
	private       int   y;
	private Properties properties;

	public LightSource(int x, int y, Properties properties) {
		this.colour = properties.getColour();
		this.radius = properties.getRadius();
		this.intensity = properties.getIntensity();
		this.x = x;
		this.y = y;
		this.properties = properties;
	}

	public void move(int x, int y) {
		this.x = x;
		this.y = y;
		life = 0;
	}

	public void tick() {
		life += 20;
	}

	public synchronized void render(Screen screen) {
		screen.renderLight(x, y, colour, radius-life, intensity, properties);
	}

	public boolean isAlive() {
		return life < radius;
	}
}

