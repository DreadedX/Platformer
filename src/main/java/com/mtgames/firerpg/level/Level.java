package com.mtgames.firerpg.level;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.mtgames.firerpg.InputHandler;
import com.mtgames.firerpg.entities.Entity;
import com.mtgames.firerpg.entities.Particle;
import com.mtgames.firerpg.gfx.Screen;

public class Level {
	
	private ScriptLoader	script;
	private Random			generator	= new Random(11);
	
	private byte[]			tiles;
	public int				width;
	public int				height;
	public List<Entity>		entities	= new ArrayList<Entity>();
	public List<Particle>	particles	= new ArrayList<Particle>();
	private LevelLoader		loader;
	
	public Level(String path, String scriptPath, InputHandler input) {
		this.script = new ScriptLoader(scriptPath);
		if (path != null) {
			try {
				loader = new LevelLoader(this, input, path);
				width = loader.getWidth();
				height = loader.getHeight();
				
				tiles = loader.loadTiles();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			this.width = 64;
			this.height = 64;
			tiles = new byte[width * height];
			generateLevel();
		}
		script.init();
	}
	
	public void generateLevel() {
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
		script.tick();

		for (Entity e : entities) {
			e.tick();
		}
		
		for (Particle e : particles) {
			e.tick();
		}
		
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
				getTile(x, y).render(screen, this, x << 3, y << 3);
			}
		}
	}
	
	public void renderEntities(Screen screen) {
		for (Entity e : entities) {
			e.render(screen);
		}
	}
	
	public void renderParticles(Screen screen) {
		for (Particle e : particles) {
			e.render(screen);
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
}
