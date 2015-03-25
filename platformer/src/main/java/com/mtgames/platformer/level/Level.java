package com.mtgames.platformer.level;

import com.mtgames.platformer.InputHandler;
import com.mtgames.platformer.debug.Debug;
import com.mtgames.platformer.entities.Entity;
import com.mtgames.platformer.entities.Particle;
import com.mtgames.platformer.gfx.Background;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.level.tiles.Tile;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Level {

	public final  List<Entity>     entities  = new ArrayList<>();
	private final List<Particle>   particles = new ArrayList<>();
	private final List<Background> layers    = new ArrayList<>();
	private final Script script;
	private final Random generator = new Random(11);
	private final InputHandler input;

	private byte[] tiles;
	private int    width;
	private int    height;

	public String  path;
	public boolean reload;

	public Level(String scriptPath, InputHandler input) {
		this.script = new Script(scriptPath);
		this.input = input;
		script.doInit();
	}

	public void tick() {
		if (reload) {
			load(path);
			reload = false;
		}

		script.doTick();

		entities.forEach(Entity::tick);

		particles.forEach(Particle::tick);

		Iterator<Particle> iter = particles.iterator();
		while (iter.hasNext()) {
			Particle particle = iter.next();

			if (!particle.isAlive()) {
				iter.remove();
			}
		}
	}

	public void renderTiles(Screen screen, int xOffset, int yOffset) {
		if (xOffset < 0) {
			xOffset = 0;
		}

		if (xOffset > ((width << 4) - screen.width)) {
			xOffset = (width << 4) - screen.width;
		}

		if (yOffset < 0) {
			yOffset = 0;
		}

		if (yOffset > ((height << 4) - screen.height)) {
			yOffset = (height << 4) - screen.height;
		}

		screen.setOffset(xOffset, yOffset);

		for (int y = (yOffset >> 4); y <= (yOffset + screen.height >> 4); y++) {
			for (int x = (xOffset >> 4); x <= (xOffset + screen.width >> 4); x++) {
				getTile(x, y).render(screen, x << 4, y << 4);
			}
		}
	}

	public void renderBackground(Screen screen) {
		layers.forEach(screen::renderBackground);
	}

	public void renderEntities(Screen screen) {
		for (Entity e : entities) {
			e.render(screen);
		}
	}

	public void renderParticles(Screen screen) {
		for (Particle p : particles) {
			p.render(screen);
		}
	}

	public Tile getTile(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return Tile.VOID;
		return Tile.tiles[tiles[x + y * width]];
	}

	public void addEntity(Entity entity) {
		this.entities.add(entity);
	}

	public void addParticle(Particle particle) {
		this.particles.add(particle);
	}

	public void addBackground(Background background) {
		this.layers.add(background);
	}

	void load(String path) {
		boolean external = false;

		if (ClassLoader.getSystemResource(path) == null) {
			if (new File("platformer/" + path).exists()) {
				external = true;
				path = "platformer/" + path;
			} else {
				Debug.log("The file '" + path + "' does not exist", Debug.WARNING);
				return;
			}
		}

		if (this.entities.size() > 0) {
			this.entities.clear();
		}

		if (this.particles.size() > 0) {
			this.particles.clear();
		}

		if (this.layers.size() > 0) {
			this.layers.clear();
		}

		try {
			LevelLoader loader = new LevelLoader(this, input, path, external);
			width = loader.getWidth();
			height = loader.getHeight();

			tiles = loader.getTiles();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
