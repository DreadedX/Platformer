package com.mtgames.platformer.entities.particles;

import com.mtgames.platformer.entities.Particle;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.gfx.Sheet;
import com.mtgames.platformer.level.Level;

public class Torch extends Particle {

//	private int xa;
//	private       int    ya       = -10 - (int) (Math.random());
//	private final Sheet  sheet    = new Sheet("/graphics/items/glowstick.png");
//	private       double modifier = 0;
    private int colour = (int) (Math.random() * 0xffffff);

	public Torch(Level level, int x, int y) {
		super(level, (int) (x + Math.random() * 30), (int) (y - 16 + Math.random() * 32), 60000);
	}

	public void tick() {
	}

	public void render(Screen screen) {
		screen.addLighting(x, y, 3, colour);
	}
}
