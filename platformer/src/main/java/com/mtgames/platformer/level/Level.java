package com.mtgames.platformer.level;

import com.mtgames.platformer.Game;
import com.mtgames.platformer.debug.Command;
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

	public static final    List<Entity>      entities     = new ArrayList<>();
	protected static final List<Background>  backgrounds  = new ArrayList<>();
	protected static final List<LightSource> lightSources = new ArrayList<>();

	public static    int[][] tiles;
	public static    int[][] tiles0;
	protected static int     width;
	protected static int     height;

	public static String  path;
	public static boolean reload;

	public static boolean renderLayer0 = true;
	public static boolean renderLayer  = true;

	public static String name        = "";
	public static String description = "";
	public static String author      = "";

	public static void tick() {
		if (reload) {
			load(path);
			reload = false;
		}

		if (!Game.editor) {
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
		} else {
			if (entities.size() > 1) {
				entities.get(0).tick();
			}
		}
	}

	public static void renderTiles(int xOffset, int yOffset) {
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

	public static void renderBackground() {
		for (Background b : backgrounds) {
			b.render(width, height);
		}
	}

	public static void renderEntities() {
		for (Entity e : entities) {
			e.render();
		}
	}

	public static void renderLights() {
		for (LightSource l : lightSources) {
			l.render();
		}
	}

	public static Tile getTile(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return Tile.tiles[0];
		return Tile.tiles[tiles[x][y]];
	}

	private static Tile getTile0(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return Tile.tiles[0];
		return Tile.tiles[tiles0[x][y]];
	}

	public static void addEntity(Entity entity) {
		entities.add(entity);
	}

	public static void addBackground(Background background) {
		backgrounds.add(background);
	}

	public static void addLightSource(LightSource lightSource) {
		lightSources.add(lightSource);
	}

	private static void load(String path) {
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

		if (entities.size() > 0) {
			entities.clear();
		}

		if (backgrounds.size() > 0) {
			backgrounds.clear();
		}

		if (lightSources.size() > 0) {
			lightSources.clear();
		}

		if (Game.editor) {
			Command.queue("spawn freeCamera 0 0");
		}

		try {
			LevelLoader loader = new LevelLoader(path, external);
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

	public static void create(int width, int height) {
		Level.width = width;
		Level.height = height;
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

		if (entities.size() > 0) {
			entities.clear();
		}

		if (backgrounds.size() > 0) {
			backgrounds.clear();
		}

		if (lightSources.size() > 0) {
			lightSources.clear();
		}

		if (Game.editor) {
			Command.queue("spawn freeCamera 0 0");
		}
	}
}
