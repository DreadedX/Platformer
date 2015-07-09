package com.mtgames.platformer.level.tiles;

import com.mtgames.platformer.gfx.Screen;
import com.mtgames.utils.Debug;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public abstract class Tile {

	//	private static final int SMALL = 8;
	//	private static final int BIG   = 32;

	private static final int size    = 1024;
	private static       int idCount = 0;

	private static final String basePath = "assets/graphics/tiles/";

	public static Tile[] tiles = new Tile[size];

	private final byte   id;
	private final String name;
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

//		Debug.log("Assigned " + this.id + " to " + this.name, Debug.DEBUG);
	}

	public byte getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public boolean isSolid() {
		return solid;
	}

	public static void clear() {
		tiles = new Tile[size];
		idCount = 0;

//		Special tiles
		new BaseTileSolid("void", false, false);
		new BaseTile("grid", false, false);
	}

	public static void load(String[] names) {
		for (int i = 0; i < names.length; i++) {
			String name = names[i];

			boolean external = false;
			if (ClassLoader.getSystemResource(basePath + name + ".png") == null) {
				if (new File("platformer/" + basePath + name + ".png").exists()) {
					external = true;
				} else {
					Debug.log("The file " + name + " does not exist", Debug.WARNING);
					return;
				}
			}

			for (int p = 2; p < tiles.length; p++) {
				if (tiles[p] == null) {
					add(name, external);
					break;
				}
				if (tiles[p].getName().equals(name)) {
					break;
				}
			}
		}
	}

	public static void load(String path) {
		File[] files;
		boolean external = false;
		if (ClassLoader.getSystemResource(basePath + path) == null) {
			File dir = new File("platformer/" + basePath + path);
			if (dir.exists()) {
				files = dir.listFiles();
				external = true;
			} else {
				Debug.log("The directory " + path + " does not exist", Debug.WARNING);
				return;
			}
		} else {
			files = new File(ClassLoader.getSystemResource(basePath + path).getFile()).listFiles();
		}
		Arrays.sort(files);
		for (File file : files) {
			if (file.isFile()) {
				String name = file.getName();
				if (name.substring(name.length()-4, name.length()).toLowerCase().equals(".png")) {
					name = name.substring(0, name.length() - 4);
					name = path + "/" + name;

					for (int i = 2; i < tiles.length; i++) {
						if (tiles[i] == null) {
							add(name, external);
							break;
						}
						if (tiles[i].getName().equals(name)) {
							break;
						}
					}
				}
			}
		}
	}

	private static void add(String name, boolean external) {
		if (ClassLoader.getSystemResource(basePath + name + ".solid") != null || new File("platformer/" + basePath + name + ".solid").exists()) {
			new BaseTileSolid(name, external);
		} else {
			new BaseTile(name, external);
		}
	}

	public abstract void render(Screen screen, int x, int y);

	public static final Comparator<Tile> TileNameComparator = (tile1, tile2) -> {
		if (tile1 == null || tile2 == null) {
			return 0;
		}

		String tile1Name = tile1.getName();
		String tile2Name = tile2.getName();

		return tile1Name.compareTo(tile2Name);
	};

}
