package com.mtgames.platformer.gfx.lighting;

import com.mtgames.platformer.gfx.Screen;

public class LightSource {
	private int modifier = 0;
	private int life = modifier;
	private final int type;
	private final int colour;
	private       int x;
	private       int y;

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

	public void render(Screen screen) {
		screen.addLighting(x, y, type, colour, life);
	}
}

