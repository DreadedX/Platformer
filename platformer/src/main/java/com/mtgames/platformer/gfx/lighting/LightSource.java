package com.mtgames.platformer.gfx.lighting;

import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.gfx.opengl.TextureLoader;

import static org.lwjgl.opengl.GL11.*;

public class LightSource {
	private int modifier = 0;
	private int life     = modifier;
	private final int type;
	private final int colour;
	private       int x;
	private       int y;
	private static int lightID = TextureLoader.loadTexture("/assets/graphics/lights/torch.png");

	public LightSource(int x, int y, int type, int colour) {
		this.type = type;
		this.colour = colour;
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
		screen.renderLight(x, y, lightID);
	}

	public boolean isAlive() {
		return life < 0xff;
	}
}

