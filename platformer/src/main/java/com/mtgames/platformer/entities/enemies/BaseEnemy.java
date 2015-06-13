package com.mtgames.platformer.entities.enemies;

import com.mtgames.platformer.entities.AdvancedEntity;
import com.mtgames.platformer.scripting.JythonFactory;
import com.mtgames.platformer.scripting.interfaces.EntityInterface;
import com.mtgames.platformer.settings.Properties;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.entities.LightSource;
import com.mtgames.platformer.gfx.lwjgl.TextureLoader;

public class BaseEnemy extends AdvancedEntity {

	private final EntityInterface entityInterface;

	public BaseEnemy(int x, int y, Properties properties) {
		super(properties, x, y);

		entityInterface = (EntityInterface) JythonFactory.getJythonObject("com.mtgames.platformer.scripting.interfaces.EntityInterface", "entities/BaseEnemy.py");
		entityInterface.init(this);
	}

	public void tick() {
		entityInterface.tick(this);
	}

	public void render(Screen screen) {
		entityInterface.render(this, screen);
	}
}
