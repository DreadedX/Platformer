package com.mtgames.firerpg.level;

import com.mtgames.firerpg.InputHandler;
import com.mtgames.firerpg.debug.Debug;
import com.mtgames.firerpg.entities.Entity;
import com.mtgames.firerpg.entities.Particle;
import com.mtgames.firerpg.gfx.Background;
import com.mtgames.firerpg.gfx.Screen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Level {

	public final  List<Entity>   entities  = new ArrayList<>();
	private final List<Particle> particles = new ArrayList<>();
	private final List<Background> layers = new ArrayList<>();
	private final Script script;
	private final Random generator = new Random(11);
	private byte[] tiles;
	private int    width;
	private int    height;

	public String scriptPath;
	public boolean reload;

	private InputHandler input;

	public Level(String path, String scriptPath, InputHandler input) {
		this.script = new Script(scriptPath);
		this.input = input;
		if (path != null) {
			load(path);
		} else {
			this.width = 64;
			this.height = 64;
			tiles = new byte[width * height];
			generateLevel();
		}
		script.doInit();

		this.scriptPath = scriptPath;
	}

	void generateLevel() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (generator.nextDouble() > .9) {
					tiles[x + y * width] = Tile.BLOCK1.getId();
				} else {
					tiles[x + y * width] = Tile.GRID.getId();
				}
			}
		}
	}

	public void tick() {
		if (reload) {
			load(scriptPath);
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

		if (xOffset > ((width << 3) - screen.width)) {
			xOffset = (width << 3) - screen.width;
		}

		if (yOffset < 0) {
			yOffset = 0;
		}

		if (yOffset > ((height << 3) - screen.height)) {
			yOffset = (height << 3) - screen.height;
		}

		screen.setOffset(xOffset, yOffset);

		for (int y = (yOffset >> 3); y <= (yOffset + screen.height >> 3); y++) {
			for (int x = (xOffset >> 3); x <= (xOffset + screen.width >> 3); x++) {
				getTile(x, y).render(screen, x << 3, y << 3);
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

	public void load(String path) {
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
			LevelLoader loader = new LevelLoader(this, input, path);
			width = loader.getWidth();
			height = loader.getHeight();

			tiles = loader.loadTiles();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
