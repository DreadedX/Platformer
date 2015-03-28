package com.mtgames.platformer.entities.particles;

import com.mtgames.platformer.entities.Particle;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.level.Level;

public class DashParticle extends Particle {

	private int ya;
	private int colour = 0xff68afaf + (int) (Math.random() * 0x50); // 0xff68afff

	public DashParticle(Level level, int x, int y, int particleOffset) {
		super(level, (int) (x + particleOffset + Math.random() * 30), (int) (y - 16 + Math.random() * 32), (int) (Math.random() * 20));
	}

	public void tick() {
		super.tick();
		move(0, ya);
		ya = gravity(ya);
	}

	public void render(Screen screen) {
		if (!isOnScreen(screen)) {
			return;
		}

		screen.drawPoint(x, y, colour);
		screen.addLighting(x, y, 1);
	}
}
