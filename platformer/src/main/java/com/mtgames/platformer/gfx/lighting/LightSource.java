package com.mtgames.platformer.gfx.lighting;

import com.mtgames.platformer.gfx.Screen;
import com.sun.javafx.geom.Vec3f;

public class LightSource {
	private int modifier = 0;
	private int life     = modifier;
	private final Vec3f colour;
	private int radius;
	private float intensity;
	private       int x;
	private       int y;

	public LightSource(int x, int y, Vec3f colour, int radius, float intensity) {
		this.colour = colour;
		this.radius = radius;
		this.intensity = intensity;
		this.x = x;
		this.y = y;
	}

	public void move(int x, int y) {
		this.x = x;
		this.y = y;
		life = modifier;
	}

	public void modify(int modifier) {
		this.modifier = modifier;
	}

	public void tick() {
		life += 20;
	}

	public synchronized void render(Screen screen) {
		screen.renderLight(x, y, colour, radius, intensity);
	}

	public boolean isAlive() {
		return life < 0xff;
	}
}

