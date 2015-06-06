package com.mtgames.platformer.editor;

import com.mtgames.platformer.Game;
import com.mtgames.platformer.debug.Command;
import com.mtgames.platformer.gfx.gui.GUI;
import com.mtgames.platformer.level.tiles.Tile;
import com.sun.javafx.geom.Vec3f;
import com.sun.javafx.geom.Vec4f;

import static org.lwjgl.glfw.GLFW.*;

public class Editor extends Game {

	private static int tile = 3;

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
		Command.set(level, screen);
		Command.exec("load debug_level");
		Command.exec("lighting");
	}

	protected void tick() {
		super.tick();

		if (!input.isPressed(GLFW_KEY_SPACE) && !paused) {
			if (input.isPressed(GLFW_MOUSE_BUTTON_LEFT)) {
				level.tiles[(mx + screen.xOffset >> 4) + (my + screen.yOffset >> 4) * level.width] = (byte) tile;
			}
			if (input.isPressed(GLFW_MOUSE_BUTTON_RIGHT)) {
				level.tiles[(mx + screen.xOffset >> 4) + (my + screen.yOffset >> 4) * level.width] = 1;
			}
		}
	}

	protected void render() {
		super.render();

		int mxBox = (mx + screen.xOffset) >> 4;
		int myBox = (my + screen.yOffset) >> 4;

		if (!paused && !input.isPressed(GLFW_KEY_SPACE)) {
			Tile.tiles[tile].render(screen, mxBox << 4, myBox << 4);
			screen.drawRectangle((mxBox << 4) - screen.xOffset, (myBox << 4) - screen.yOffset, (mxBox << 4) + 16 - screen.xOffset, (myBox << 4) + 16 - screen.yOffset, new Vec4f(1.0f, 1.0f, 1.0f, 0.3f));
		}

		if (!paused	&& input.isPressed(GLFW_KEY_SPACE)) {
			for (int i = 2; i < Tile.tiles.length; i++) {
				if (Tile.tiles[i] == null) {
					return;
				}
				final int finalI = i;
				GUI.add(() -> GUI.button(8 + 16 * (finalI - 2), 8, 16, 16,
						() -> Tile.tiles[finalI].render(screen, 16 * (finalI - 2) + screen.xOffset, screen.yOffset), () -> tile = finalI));
			}
		}

		if (paused) {
			GUI.add(() -> GUI.buttonText(WIDTH / 2, 184, "Save", new Vec3f(0.2f, 0.2f, 0.7f), () -> Command.exec("export")));
		}

		GUI.render();
	}
}
