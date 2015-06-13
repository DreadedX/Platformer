package com.mtgames.platformer.entities;

import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.scripting.JythonFactory;
import com.mtgames.platformer.scripting.interfaces.EntityInterface;
import com.mtgames.platformer.settings.Properties;
import com.mtgames.utils.Debug;

public class AutoScroll extends AdvancedEntity {

	private final EntityInterface entityInterface;

	public AutoScroll(int x, int y, Properties properties) {
		super(properties, x, y);

		entityInterface = (EntityInterface) JythonFactory.getJythonObject("com.mtgames.platformer.scripting.interfaces.EntityInterface", "entities/AutoScroll.py");
		entityInterface.init(this);
	}

	public void tick() {
		entityInterface.tick(this);
	}

	public void render(Screen screen) {
		entityInterface.render(this, screen);
	}
}
