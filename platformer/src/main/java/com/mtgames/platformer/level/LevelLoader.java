package com.mtgames.platformer.level;

import com.mtgames.platformer.debug.Command;
import com.mtgames.platformer.gfx.Background;
import com.mtgames.utils.JSP;
import org.json.*;

class LevelLoader {

	private static int    width;
	private static int    height;
	private static byte[] tiles;

	public LevelLoader(Level level, String path, boolean external) {

		JSP levelJSP = new JSP(path, external);

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

		JSONObject objEntities = levelJSP.get("entities");
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