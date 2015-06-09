package com.mtgames.platformer.entities;

import com.mtgames.platformer.InputHandler;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.settings.Properties;
import com.sun.javafx.geom.Vec4f;

import static com.mtgames.platformer.settings.KeyBindings.*;

public class FreeCamera extends AdvancedEntity {

	private final InputHandler input;
	private final LightSource  lightSource;

	public FreeCamera(int x, int y, Properties properties) {
		super(properties, x, y);

		level.addLightSource(lightSource = new LightSource(x, y, properties));

		movingDir = 1;
		speed = 4;

		this.input = properties.getInput();
	}

	public void tick() {
		int xa = 0;
		int ya = 0;

		if (input.isPressed(KEY_MOD)) {
			speed = 8;
		} else {
			speed = 3;
		}

		if (input.isPressed(KEY_LEFT)) {
			xa -= speed;
		}

		if (input.isPressed(KEY_RIGHT)) {
			xa += speed;
		}

		if (input.isPressed(KEY_UP)) {
			ya -= speed;
		}

		if (input.isPressed(KEY_DOWN)) {
			ya += speed;
		}

		move(xa, ya);
		lightSource.move(x, y);
	}

	public void render(Screen screen) {
		screen.drawRectangle(x-4-screen.xOffset, y-4-screen.yOffset ,x+4-screen.xOffset, y+4-screen.yOffset, new Vec4f(0.0f, 0.0f, 0.0f, 0.5f));
	}

	@Override public boolean hasCollided(int xa, int ya) {
		return false;
	}
}
