package com.mtgames.platformer.entities.particles;

import com.mtgames.platformer.entities.Particle;
import com.mtgames.platformer.entities.Properties;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.level.Level;

public class Torch extends Particle {

	private final int colour = (int) (Math.random() * 0xffffff);

	public Torch(int x, int y, Properties properties) {
		super((int) (x + Math.random() * 30), (int) (y - 16 + Math.random() * 32), 60000, properties);
	}

	public void tick() {
	}

	public void render(Screen screen) {
		screen.addLighting(x, y, 3, colour);
	}
}
