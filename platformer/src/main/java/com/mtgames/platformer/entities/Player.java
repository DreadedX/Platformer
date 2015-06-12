package com.mtgames.platformer.entities;

import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.scripting.JythonFactory;
import com.mtgames.platformer.scripting.interfaces.EntityInterface;
import com.mtgames.platformer.settings.Properties;

public class Player extends AdvancedEntity {

	private EntityInterface entityInterface;

	public Player(int x, int y, Properties properties) {
		super(properties, x, y);

		persistent = true;

		collide = true;
		entityInterface = (EntityInterface) JythonFactory
				.getJythonObject("com.mtgames.platformer.scripting.interfaces.EntityInterface", "python/entities/Player.py");
		entityInterface.init(this);
	}

	public void tick() {
		entityInterface.tick(this);
	}

	public void render(Screen screen) {
		entityInterface.render(this, screen);
	}
}
