package com.mtgames.platformer.level;

import com.mtgames.platformer.debug.Command;
import com.mtgames.platformer.gfx.Background;
import com.mtgames.utils.Debug;
import com.mtgames.utils.JSP;
import org.json.*;

public class LevelLoader {

	private static int    width;
	private static int    height;
	private static byte[] tiles;
	private static byte[] tiles0;

	private static String name = "";
	private static String description = "";
	private static String author = "";

	public LevelLoader(Level level, String path, boolean external) {

		JSP levelJSP = new JSP(path, external);

		JSONObject objInfo = levelJSP.get("info");
		if (!objInfo.has("version")) {
			Debug.log(path + " is not a valid level file, not loading", Debug.WARNING);
			return;
		} else if (!objInfo.getString("version").equals("2.1")) {
			Debug.log(path + " is an outdated level file, not loading", Debug.WARNING);
			return;
		}

		if (objInfo.has("name")) {
			name = objInfo.getString("name");
		}
		if (objInfo.has("description")) {
			description = objInfo.getString("description");
		}
		if (objInfo.has("author")) {
			author = objInfo.getString("author");
		}

		JSONObject objBackground = levelJSP.get("background");
		for (int i = 0; i < objBackground.length(); i++) {
			String name = objBackground.getJSONObject(String.valueOf(i)).getString("name");
			int speed = objBackground.getJSONObject(String.valueOf(i)).getInt("speed");
			level.addBackground(new Background("/assets/graphics/backgrounds/" + name + ".png", speed));
		}

		JSONObject objTiles = levelJSP.get("tiles");
		width = objTiles.getInt("width");
		height = objTiles.getInt("height");
		JSONArray tilesJSON = objTiles.getJSONArray("tiles");

		tiles = new byte[width*height];
		for (int i = 0; i < tilesJSON.length(); i++) {
			tiles[i] = (byte) tilesJSON.optInt(i);
		}

		tiles0 = new byte[width * height];
		if (objTiles.has("tiles0")) {
			JSONArray tiles0JSON = objTiles.getJSONArray("tiles0");

			for (int i = 0; i < tiles0JSON.length(); i++) {
				tiles0[i] = (byte) tiles0JSON.optInt(i);
			}
		} else {
			Debug.log(path + " has no tiles0", Debug.DEBUG);
		}

		JSONObject objEntities = levelJSP.get("entities");
		for (int i = 0; i < objEntities.length(); i++) {
			String type = objEntities.getJSONObject(String.valueOf(i)).getString("type");
			int x = objEntities.getJSONObject(String.valueOf(i)).getInt("x");
			int y = objEntities.getJSONObject(String.valueOf(i)).getInt("y");

			String properties = "";
			if(objEntities.getJSONObject(String.valueOf(i)).has("properties")) {
				properties = String.valueOf(objEntities.getJSONObject(String.valueOf(i)).getJSONObject("properties"));
			}

			Command.queue("spawn " + type + " " + x + " " + y + " " + properties);
		}

		JSONObject objLights = levelJSP.get("lights");
		for (int i = 0; i < objLights.length(); i++) {
			String type = objLights.getJSONObject(String.valueOf(i)).getString("type");
			int x = objLights.getJSONObject(String.valueOf(i)).getInt("x");
			int y = objLights.getJSONObject(String.valueOf(i)).getInt("y");

			String properties = "";
			if(objLights.getJSONObject(String.valueOf(i)).has("properties")) {
				properties = String.valueOf(objLights.getJSONObject(String.valueOf(i)).getJSONObject("properties"));
			}

			Command.queue("light " + type + " " + x + " " + y + " " + properties);
		}
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getAuthor() {
		return author;
	}

	public byte[] getTiles() {
		return tiles;
	}

	public byte[] getTiles0() {
		return tiles0;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}