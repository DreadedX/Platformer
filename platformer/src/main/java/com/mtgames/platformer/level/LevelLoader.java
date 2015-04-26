package com.mtgames.platformer.level;

import com.mtgames.platformer.debug.Command;
import com.mtgames.utils.Debug;
import com.mtgames.platformer.entities.AutoScroll;
import com.mtgames.platformer.entities.Player;
import com.mtgames.platformer.entities.Properties;
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

	public LevelLoader(Level level, String path, boolean external) throws Exception {
		GZIPInputStream gzIn;
		if (external) {
			gzIn = new GZIPInputStream(new FileInputStream(path));
		} else {
			gzIn = new GZIPInputStream(ClassLoader.getSystemResourceAsStream(path));
		}

		String inBackground = null;
		String inTiles = null;
		String inEntities = null;

		TarArchiveInputStream tarIn = new TarArchiveInputStream(new BufferedInputStream(gzIn));
		TarArchiveEntry entry = tarIn.getNextTarEntry();
		BufferedReader br;
		while (entry != null) {
			br = new BufferedReader(new InputStreamReader(tarIn));
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
			}
			entry = tarIn.getNextTarEntry();
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
			level.addBackground(new Background("/assets/graphics/backgrounds/" + name + ".png", speed));
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

			String properties = "";
			if(objEntities.getJSONObject(String.valueOf(i)).has("properties")) {
				properties = String.valueOf(objEntities.getJSONObject(String.valueOf(i)).getJSONObject("properties"));
			}

			Command.exec("spawn " + type + " " + x + " " + y + " " + properties);

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