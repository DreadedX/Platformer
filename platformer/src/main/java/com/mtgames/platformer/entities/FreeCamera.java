package com.mtgames.platformer.entities;

import com.mtgames.platformer.InputHandler;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.scripting.JythonFactory;
import com.mtgames.platformer.scripting.interfaces.EntityInterface;
import com.mtgames.platformer.settings.Properties;
import com.sun.javafx.geom.Vec4f;

import static com.mtgames.platformer.settings.Settings.*;

public class FreeCamera extends AdvancedEntity {

	private final EntityInterface entityInterface;

	public FreeCamera(int x, int y, Properties properties) {
		super(properties, x, y);

		entityInterface = (EntityInterface) JythonFactory.getJythonObject("com.mtgames.platformer.scripting.interfaces.EntityInterface", "entities/FreeCamera.py");
		entityInterface.init(this);
	}

	public void tick() {
		entityInterface.tick(this);
	}

	public void render(Screen screen) {
		entityInterface.render(this, screen);
	}
}
