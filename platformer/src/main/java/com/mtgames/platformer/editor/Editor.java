package com.mtgames.platformer.editor;

import com.mtgames.platformer.Game;
import com.mtgames.platformer.debug.Command;
import com.mtgames.platformer.gfx.Font;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.gfx.gui.GUI;
import com.mtgames.platformer.level.tiles.Tile;
import com.sun.javafx.geom.Vec3f;
import com.sun.javafx.geom.Vec4f;

import java.util.Arrays;

import static com.mtgames.platformer.settings.Settings.*;

public class Editor extends Game {

	private static int tile  = 3;
	private static int layer = 0;

	private Tile[] tilesSort = new Tile[Tile.tiles.length];

	public static void main(String[] args) {
		if (Integer.getInteger("com.mtgames.scale") != null) {
			Game.scale = Integer.getInteger("com.mtgames.scale");
		} else {
			Game.scale = 1;
		}

		if (Integer.getInteger("com.mtgames.debug") == 0) {
			debug = true;
		}

		new Editor().start();
	}

	protected void init() {
		super.init();
		level = new Level();
		Command.set(level);
		Command.queue("load debug_level");
		Command.queue("lighting");
		Command.execute();
	}

	protected void tick() {
		super.tick();

		if (!input.isPressed(KEY_TILE_SELECT) && !paused) {
			if (input.isPressed(KEY_TILE_PLACE)) {
				if (layer == 0) {
					level.tiles0[(mx + Screen.xOffset >> 4)][(my + Screen.yOffset >> 4)] = tile;
				}
				if (layer == 1) {
					level.tiles[(mx + Screen.xOffset >> 4)][(my + Screen.yOffset >> 4)] = tile;
				}
			}
			if (input.isPressed(KEY_TILE_REMOVE)) {
				if (layer == 0) {
					level.tiles0[(mx + Screen.xOffset >> 4)][(my + Screen.yOffset >> 4)] = 1;
				}
				if (layer == 1) {
					level.tiles[(mx + Screen.xOffset >> 4)][(my + Screen.yOffset >> 4)] = 1;
				}
			}
		}

		if (input.isPressed(KEY_LAYER0)) {
			layer = 0;
			level.renderLayer0 = true;
			level.renderLayer = false;
		} else if (input.isPressed(KEY_LAYER1)) {
			layer = 1;
			level.renderLayer0 = false;
			level.renderLayer = true;
		} else {
			level.renderLayer0 = true;
			level.renderLayer = true;
		}

		if (!paused	&& input.isPressed(KEY_TILE_SELECT)) {
			tilesSort = new Tile[Tile.tiles.length];

			System.arraycopy(Tile.tiles, 0, tilesSort, 0, tilesSort.length);

			Arrays.sort(tilesSort, Tile.TileNameComparator);
		}
	}

	protected void render() {
		super.render();

		int mxBox = (mx + Screen.xOffset) >> 4;
		int myBox = (my + Screen.yOffset) >> 4;

		if (!paused && !input.isPressed(KEY_TILE_SELECT)) {
			Tile.tiles[tile].render(mxBox << 4, myBox << 4);
			Screen.drawRectangle((mxBox << 4) - Screen.xOffset, (myBox << 4) - Screen.yOffset, (mxBox << 4) + 16 - Screen.xOffset,
					(myBox << 4) + 16 - Screen.yOffset, new Vec4f(1.0f, 1.0f, 1.0f, 0.3f));
			if (!showDebug) {
				Font.render(Tile.tiles[tile].getName(), Screen.xOffset + 1, Screen.yOffset + 1);
			}
			Font.render("Layer " + layer, Screen.width -56+ Screen.xOffset, Screen.yOffset + 1);
		}

		if (!paused	&& input.isPressed(KEY_TILE_SELECT)) {
			for (int i = 0; i < tilesSort.length; i++) {
				if (tilesSort[i] == null) {
					return;
				}
				final int finalI = i;
				GUI.button(8 + 16 * (finalI), 8, 16, 16, () -> tilesSort[finalI].render(16 * (finalI) + Screen.xOffset, Screen.yOffset), () -> tile = tilesSort[finalI].getId());
			}
		}

		if (paused) {
			GUI.buttonText(WIDTH / 2, 184, "Export", new Vec3f(0.2f, 0.2f, 0.7f), () -> Command.queue("export"));
		}

		GUI.render();
	}
}
