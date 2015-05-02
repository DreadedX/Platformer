package com.mtgames.platformer.gfx.lighting;

import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.gfx.opengl.TextureLoader;
import com.sun.javafx.geom.Vec3f;

import static org.lwjgl.opengl.GL11.*;

public class LightSource {
	private int modifier = 0;
	private int life     = modifier;
	private final Vec3f colour;
	private int radius;
	private       int x;
	private       int y;

	public LightSource(int x, int y, Vec3f colour, int radius) {
		this.colour = colour;
		this.radius = radius;
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
		screen.renderLight(x, y, colour, radius);
	}

	public boolean isAlive() {
		return life < 0xff;
	}
}

