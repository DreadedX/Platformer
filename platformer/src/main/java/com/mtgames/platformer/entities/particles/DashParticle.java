package com.mtgames.platformer.entities.particles;

import com.mtgames.platformer.level.Level;
import com.mtgames.platformer.entities.BasicEntity;
import com.mtgames.platformer.settings.Properties;
import com.mtgames.platformer.entities.LightSource;

public class DashParticle extends BasicEntity {

	private final LightSource lightSource;
	private int ya = 0;

	public DashParticle(int x, int y, Properties properties) {
		super(properties, (int) (x - 8 + Math.random() * 16), (int) (y - 6 + Math.random() * 12));
		lightSource = new LightSource(x, y, properties);
		Level.addLightSource(lightSource);
		life = (int) (Math.random() * 20);
	}

	public void tick() {
		life--;
		move(0, ya);
		ya = gravity(ya);
		lightSource.move(x, y);
	}


	public void render() {
	}
}
