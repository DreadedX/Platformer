package com.mtgames.firerpg.entities.particles;

import com.mtgames.firerpg.entities.Particle;
import com.mtgames.firerpg.gfx.Screen;
import com.mtgames.firerpg.level.Level;

public class DashParticle extends Particle {

	public DashParticle(Level level, int x, int y, int particleOffset) {
		super(level, (int) (x + particleOffset + Math.random() * 15),
				(int) (y - 8 + Math.random() * 16),
				(int) (1 + Math.random() * 10));
	}

	public void tick() {
		super.tick();
	}

	public void render(Screen screen) {
		if (!isOnScreen(screen)) {
			return;
		}

		screen.drawPoint(x, y, 0xff68afff);
	}
}
