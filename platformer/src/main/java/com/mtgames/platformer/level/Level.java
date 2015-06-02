package com.mtgames.platformer.level;

import com.mtgames.utils.Debug;
import com.mtgames.platformer.entities.Entity;
import com.mtgames.platformer.entities.BasicEntity;
import com.mtgames.platformer.gfx.Background;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.gfx.lighting.LightSource;
import com.mtgames.platformer.level.tiles.Tile;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Level {

	public final  List<Entity>      entities     = new ArrayList<>();
	protected final List<Background>  backgrounds  = new ArrayList<>();
	protected final List<LightSource> lightSources = new ArrayList<>();

	public  byte[] tiles;
	public  int    width;
	protected int    height;

	public String  path;
	public boolean reload;

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
			if (!iteratorParticles.next().isAlive()) {
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
		for (Background b : backgrounds) {
			b.render(screen, width, height);
		}
	}

	public void renderEntities(Screen screen) {
		for (Entity e : entities) {
			e.render(screen);
		}
	}

	public void renderLights(Screen screen) {
		for (LightSource l : lightSources) {
			l.render(screen);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
