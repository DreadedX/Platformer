package com.mtgames.platformer.entities;

import com.mtgames.platformer.InputHandler;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.gfx.lighting.LightSource;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.*;

public class FreeCamera extends Mob {

	private final InputHandler input;
	private final LightSource  lightSource;

	public FreeCamera(int x, int y, Properties properties) {
		super(properties, x, y);

		level.addLightSource(lightSource = new LightSource(x, y, 0, 0xffae00));

		movingDir = 1;
		speed = 4;

		this.input = properties.getInput();
	}

	public void tick() {
		int xa = 0;
		int ya = 0;

		if (input.isPressed(GLFW_KEY_LEFT_SHIFT)) {
			speed = 8;
		} else {
			speed = 3;
		}

		if (input.isPressed(GLFW_KEY_A)) {
			xa -= speed;
		}

		if (input.isPressed(GLFW_KEY_D)) {
			xa += speed;
		}

		if (input.isPressed(GLFW_KEY_W)) {
			ya -= speed;
		}

		if (input.isPressed(GLFW_KEY_S)) {
			ya += speed;
		}

		move(xa, ya);
		lightSource.move(x, y);
	}

	public void render(Screen screen) {
		screen.drawRectangle(x-4-screen.xOffset, y-4-screen.yOffset ,x+4-screen.xOffset, y+4-screen.yOffset, 0x7F000000);
	}

	@Override public boolean hasCollided(int xa, int ya) {
		return false;
	}
}
