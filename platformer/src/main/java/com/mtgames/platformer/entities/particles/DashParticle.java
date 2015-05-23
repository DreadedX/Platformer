package com.mtgames.platformer.entities.particles;

import com.mtgames.platformer.entities.BasicEntity;
import com.mtgames.platformer.entities.Properties;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.gfx.lighting.LightSource;
import com.mtgames.utils.Debug;

public class DashParticle extends BasicEntity {

	private final LightSource lightSource;
	private int ya = (int) (Math.random() * 2);

	public DashParticle(int x, int y, int particleOffset, Properties properties) {
		super(properties, (int) (x + particleOffset + Math.random() * 30), (int) (y - 16 + Math.random() * 32));
		lightSource = new LightSource(x, y, properties);
		level.addLightSource(lightSource);
		life = (int) (Math.random() * 15);
	}

	public void tick() {
		life--;
		move(0, ya);
		ya = gravity(ya);
		lightSource.move(x, y);
	}


	public void render(Screen screen) {
	}
}
