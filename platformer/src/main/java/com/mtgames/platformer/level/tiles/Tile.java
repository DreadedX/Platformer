package com.mtgames.platformer.level.tiles;

import com.mtgames.platformer.gfx.Screen;
import com.mtgames.utils.Debug;

import java.io.File;

@SuppressWarnings("UnusedDeclaration") public abstract class Tile {

	private static final int SMALL = 8;
	private static final int BIG   = 32;

	private static final int size = 1024;
	private static int idCount = 0;

	public static Tile[] tiles = new Tile[size];

	private final byte id;
	protected final String name;
	boolean solid;

	Tile(String name) {
		int id = idCount;
		idCount++;
		this.id = (byte) id;
		if (tiles[id] != null) {
			throw new RuntimeException("Tile id already exists: " + id);
		}
		this.name = name;

		tiles[id] = this;

		Debug.log(this.name, Debug.DEBUG);
	}

	public byte getId() {
		return id;
	}

	public boolean isSolid() {
		return solid;
	}

	public static void clear() {
		tiles = new Tile[size];
		idCount = 0;

		new BaseTileSolid("void", false);
		new BaseTile("grid", false);


		new BaseTile("brickLight");
		new BaseTileSolid("brickDark");


		new BaseTile("window", 4 * 16, 0);
		new BaseTile("window", 4 * 16, 1);
		new BaseTile("window", 4 * 16, 2);
		new BaseTile("window", 4 * 16, 3);

		new BaseTile("window", 4 * 16, 4);
		new BaseTile("window", 4 * 16, 5);
		new BaseTile("window", 4 * 16, 6);
		new BaseTile("window", 4 * 16, 7);

		new BaseTile("window", 4 * 16, 8);
		new BaseTile("window", 4 * 16, 9);
		new BaseTile("window", 4 * 16, 10);
		new BaseTile("window", 4 * 16, 11);


		new BaseTile("flag", 4 * 16, 0);
		new BaseTile("flag", 4 * 16, 1);
		new BaseTile("flag", 4 * 16, 2);

		new BaseTile("flag", 4 * 16, 4);
		new BaseTile("flag", 4 * 16, 5);
		new BaseTile("flag", 4 * 16, 6);

		new BaseTile("flag", 4 * 16, 8);
		new BaseTile("flag", 4 * 16, 9);
		new BaseTile("flag", 4 * 16, 10);

		new BaseTile("flag", 4 * 16, 12);
		new BaseTile("flag", 4 * 16, 13);
		new BaseTile("flag", 4 * 16, 14);


		new BaseTile("pillar");


		new BaseTileSolid("block");

		load("base");
	}

	private static void load(String path) {
		String basePath = "assets/graphics/tiles/";
		File[] files = new File(ClassLoader.getSystemResource(basePath + path).getFile()).listFiles();
		for (File file : files) {
			if (file.isFile()) {
				String name = file.getName();
				if (name.substring(name.length()-4, name.length()).toLowerCase().equals(".png")) {
					name = name.substring(0, name.length() - 4);
					name = path + "/" + name;

					if (ClassLoader.getSystemResource(basePath + name + ".solid") != null) {
						new BaseTileSolid(name);
						Debug.log("Solid", Debug.DEBUG);
					} else {
						new BaseTile(name);
					}
				}
			}
		}
	}

	public abstract void render(Screen screen, int x, int y);

}
