package com.mtgames.platformer.entities.particles;

import com.mtgames.platformer.entities.Particle;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.gfx.Sheet;
import com.mtgames.platformer.level.Level;

public class Glowstick extends Particle {

	private int xa;
	private       int    ya       = -10 - (int) (Math.random());
	private final Sheet  sheet    = new Sheet("/graphics/items/glowstick.png");
	private       double modifier = 0;

	public Glowstick(Level level, int x, int y, int movingDir) {
		super(level, (int) (x + Math.random() * 30), (int) (y - 16 + Math.random() * 32), 60000);
		if (movingDir == 0) {
			xa = -5 - (int) (Math.random() * 2);
		} else {
			xa = 5 + (int) (Math.random() * 2);
		}
	}

	public void tick() {
		super.tick();
		move(xa, ya);
		ya = gravity(ya);

		if (hasCollided(0, 1) || hasCollided(1, 0) || hasCollided(-1, 0)) {
			xa = 0;
		}

		if (modifier < 0xff) {
			modifier += .2;
		}
	}

	public void render(Screen screen) {
		if (modifier < 0xff) {
			screen.render(x-8, y-8, sheet, 0);
		} else {
			screen.render(x-8, y-8, sheet, 1);
		}
		screen.addLighting(x, y, 2, (int) modifier);
	}
}
