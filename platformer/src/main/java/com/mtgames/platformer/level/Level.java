package com.mtgames.platformer.level;

import com.mtgames.utils.Debug;
import com.mtgames.platformer.entities.Entity;
import com.mtgames.platformer.gfx.Background;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.entities.LightSource;
import com.mtgames.platformer.level.tiles.Tile;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Level {

	public final    List<Entity>      entities     = new ArrayList<>();
	protected final List<Background>  backgrounds  = new ArrayList<>();
	protected final List<LightSource> lightSources = new ArrayList<>();

	public    int[][] tiles;
	public    int[][] tiles0;
	protected int     width;
	protected int     height;

	public String  path;
	public boolean reload;

	public boolean renderLayer0 = true;
	public boolean renderLayer  = true;

	public String name        = "";
	public String description = "";
	public String author      = "";

	public void tick() {
		if (reload) {
			load(path);
			reload = false;
		}

		lightSources.forEach(LightSource::tick);

		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);

			e.tick();
		}

		Iterator<Entity> iteratorParticles = entities.iterator();
		while (iteratorParticles.hasNext()) {
			Entity current = iteratorParticles.next();
			if (!current.isAlive() && !current.persistent) {
				iteratorParticles.remove();
			}
		}
		Iterator<LightSource> iteratorLightSources = lightSources.iterator();
		while (iteratorLightSources.hasNext()) {
			if (!iteratorLightSources.next().isAlive()) {
				iteratorLightSources.remove();
			}
		}
	}

	public void renderTiles(int xOffset, int yOffset) {
		if (xOffset < 0) {
			xOffset = 0;
		}

		if (xOffset > ((width << 4) - Screen.width)) {
			xOffset = (width << 4) - Screen.width;
		}

		if (yOffset < 0) {
			yOffset = 0;
		}

		if (yOffset > ((height << 4) - Screen.height)) {
			yOffset = (height << 4) - Screen.height;
		}

		Screen.setOffset(xOffset, yOffset);

		for (int y = (yOffset >> 4); y <= (yOffset + Screen.height >> 4); y++) {
			for (int x = (xOffset >> 4); x <= (xOffset + Screen.width >> 4); x++) {
				if (renderLayer0) {
					getTile0(x, y).render(x << 4, y << 4);
				}
				if (renderLayer) {
					getTile(x, y).render(x << 4, y << 4);
				}
			}
		}
	}

	public void renderBackground() {
		for (Background b : backgrounds) {
			b.render(width, height);
		}
	}

	public void renderEntities() {
		for (Entity e : entities) {
			e.render();
		}
	}

	public void renderLights() {
		for (LightSource l : lightSources) {
			l.render();
		}
	}

	public Tile getTile(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return Tile.tiles[0];
		return Tile.tiles[tiles[x][y]];
	}

	private Tile getTile0(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return Tile.tiles[0];
		return Tile.tiles[tiles0[x][y]];
	}

	public void addEntity(Entity entity) {
		this.entities.add(entity);
	}

	public void addBackground(Background background) {
		this.backgrounds.add(background);
	}

	public void addLightSource(LightSource lightSource) {
		this.lightSources.add(lightSource);
	}

	private void load(String path) {
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

		if (this.backgrounds.size() > 0) {
			this.backgrounds.clear();
		}

		if (this.lightSources.size() > 0) {
			this.lightSources.clear();
		}

		try {
			LevelLoader loader = new LevelLoader(this, path, external);
			width = loader.getWidth();
			height = loader.getHeight();

			tiles = loader.getTiles();
			tiles0 = loader.getTiles0();

			name = loader.getName();
			description = loader.getDescription();
			author = loader.getAuthor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void create(int width, int height) {
		this.width = width;
		this.height = height;
		tiles = new int[width][height];

		for (int x = 0; x < tiles.length; x++) {
			for (int y = 0; y < tiles[0].length; y++) {
				tiles[x][y] = 1;
			}
		}

		tiles0 = new int[width][height];

		for (int x = 0; x < tiles0.length; x++) {
			for (int y = 0; y < tiles0[0].length; y++) {
				tiles0[x][y] = 1;
			}
		}

		if (this.entities.size() > 0) {
			this.entities.clear();
		}

		if (this.backgrounds.size() > 0) {
			this.backgrounds.clear();
		}

		if (this.lightSources.size() > 0) {
			this.lightSources.clear();
		}
	}
}
