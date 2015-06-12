package com.mtgames.platformer.entities;

import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.scripting.JythonFactory;
import com.mtgames.platformer.scripting.interfaces.PlayerInterface;
import com.mtgames.platformer.settings.Properties;

public class Player extends AdvancedEntity {

	private PlayerInterface pi;

	public Player(int x, int y, Properties properties) {
		super(properties, x, y);

		persistent = true;

		collide = true;
		pi = (PlayerInterface) JythonFactory.getJythonObject("com.mtgames.platformer.scripting.interfaces.PlayerInterface", "python/entities/Player.py");
		pi.init(this);
	}

	public void tick() {
		pi.tick(this);
	}

	public void render(Screen screen) {
		pi.render(this, screen);
	}
}
