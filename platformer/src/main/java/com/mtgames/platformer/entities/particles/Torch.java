package com.mtgames.platformer.entities.particles;

import com.mtgames.platformer.entities.BasicEntity;
import com.mtgames.platformer.settings.Properties;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.entities.LightSource;

public class Torch extends BasicEntity {

	private final LightSource lightSource;

	public Torch(int x, int y, Properties properties) {
		super(properties, (int) (x + Math.random() * 30), (int) (y - 16 + Math.random() * 32));
		lightSource = new LightSource(x, y, properties);
		level.addLightSource(lightSource);
	}

	public void tick() {
		lightSource.move(x, y);
	}

	public void render(Screen screen) {
	}
}
