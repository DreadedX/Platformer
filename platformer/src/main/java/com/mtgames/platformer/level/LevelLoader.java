package com.mtgames.platformer.level;

import com.mtgames.platformer.InputHandler;
import com.mtgames.platformer.debug.Debug;
import com.mtgames.platformer.entities.AutoScoll;
import com.mtgames.platformer.entities.Player;
import com.mtgames.platformer.entities.enemies.BaseEnemy;
import com.mtgames.platformer.gfx.Background;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.json.*;

import java.io.*;
import java.util.zip.GZIPInputStream;

class LevelLoader {

	private static int    width;
	private static int    height;
	private static byte[] tiles;

	public LevelLoader(Level level, InputHandler input, String path, boolean external) throws Exception {
		GZIPInputStream gzIn;
		if (external) {
			gzIn = new GZIPInputStream(new FileInputStream(path));
		} else {
			gzIn = new GZIPInputStream(ClassLoader.getSystemResourceAsStream(path));
		}

		String inBackground = null;
		String inTiles = null;
		String inEntities = null;
		String inLights = null;

		TarArchiveInputStream tarIn = new TarArchiveInputStream(new BufferedInputStream(gzIn));
		TarArchiveEntry entry = tarIn.getNextTarEntry();
		BufferedReader br;
		while (entry != null) {
			br = new BufferedReader(new InputStreamReader(tarIn)); // Read directly from tarInput
			String line;
			String lines = "";
			while ((line = br.readLine()) != null) {
				lines += line;
			}
			switch (entry.getName()) {
				case "background.json":
					inBackground = lines;
					break;

				case "tiles.json":
					inTiles = lines;
					break;

				case "entities.json":
					inEntities = lines;
					break;

				case "lights.json":
					inLights = lines;
					break;

			}
			entry = tarIn.getNextTarEntry(); // You forgot to iterate to the next file
		}
		tarIn.close();

		if (inBackground == null || inTiles == null || inEntities == null) {
			Debug.log("Incorrect level file", Debug.ERROR);
			return;
		}

		JSONObject objBackground = new JSONObject(inBackground);
		for (int i = 0; i < objBackground.length(); i++) {
			String name = objBackground.getJSONObject(String.valueOf(i)).getString("name");
			int speed = objBackground.getJSONObject(String.valueOf(i)).getInt("speed");
			level.addBackground(new Background("/graphics/backgrounds/" + name + ".png", speed));
		}

		JSONObject objTiles = new JSONObject(inTiles);
		width = objTiles.getInt("width");
		height = objTiles.getInt("height");
		JSONArray tilesJSON = objTiles.getJSONArray("tiles");

		tiles = new byte[width*height];
		for (int i = 0; i < tilesJSON.length(); i++) {
			tiles[i] = (byte) tilesJSON.optInt(i);
		}

		JSONObject objEntities = new JSONObject(inEntities);
		for (int i = 0; i < objEntities.length(); i++) {
			String type = objEntities.getJSONObject(String.valueOf(i)).getString("type");
			int x = objEntities.getJSONObject(String.valueOf(i)).getInt("x");
			int y = objEntities.getJSONObject(String.valueOf(i)).getInt("y");

			switch (type) {
				case "player":
					level.addEntity(new Player(level, x, y, input));
					break;

				case "baseEnemy":
					level.addEntity(new BaseEnemy(level, x, y));
					break;

				case "autoScroll":
					level.addEntity(new AutoScoll(level, x, y));
					break;

				default:
					Debug.log(type + " is not a valid entity type", Debug.ERROR);
			}
		}
	}


	public byte[] getTiles() {
		return tiles;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}