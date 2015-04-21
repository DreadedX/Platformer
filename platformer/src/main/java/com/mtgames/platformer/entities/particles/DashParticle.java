package com.mtgames.platformer.entities.particles;

import com.mtgames.platformer.entities.Particle;
import com.mtgames.platformer.entities.Properties;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.gfx.lighting.LightSource;

public class DashParticle extends Particle {

	private final LightSource lightSource;
	private int ya = (int) (Math.random() * 2);

	public DashParticle(int x, int y, int particleOffset, Properties properties) {
		super((int) (x + particleOffset + Math.random() * 30), (int) (y - 16 + Math.random() * 32), (int) (Math.random() * 15), properties);
		lightSource = new LightSource(x, y, 1, 0x68afaf + (int) (Math.random() * 0x50));
		level.addLightSource(lightSource);
	}

	public void tick() {
		super.tick();
		move(0, ya - 1);
		ya = gravity(ya);
		lightSource.move(x, y);
	}

	public void render(Screen screen) {
	}
}
