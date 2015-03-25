package com.mtgames.platformer.entities.particles;

import com.mtgames.platformer.entities.Particle;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.level.Level;

public class DashParticle extends Particle {

	public DashParticle(Level level, int x, int y, int particleOffset) {
		super(level, (int) (x + particleOffset + Math.random() * 15), (int) (y - 16 + Math.random() * 32), (int) (1 + Math.random() * 10));
	}

	public void render(Screen screen) {
		if (!isOnScreen(screen)) {
			return;
		}

		screen.drawPoint(x, y, 0xff68afff);
	}
}
