package com.mtgames.platformer.entities.particles;

import com.mtgames.platformer.entities.Particle;
import com.mtgames.platformer.entities.Properties;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.gfx.lighting.LightSource;

public class Torch extends Particle {

	private final LightSource lightSource;

	public Torch(int x, int y, Properties properties) {
		super((int) (x + Math.random() * 30), (int) (y - 16 + Math.random() * 32), -10, properties);
		lightSource = new LightSource(x, y, 2, properties.getColour());
		level.addLightSource(lightSource);
	}

	public void tick() {
		super.tick();
		lightSource.move(x, y);
	}

	public void render(Screen screen) {
	}
}
