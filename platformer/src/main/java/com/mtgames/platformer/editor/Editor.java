package com.mtgames.platformer.editor;

import com.mtgames.platformer.Game;
import com.mtgames.platformer.debug.Command;
import com.mtgames.platformer.level.tiles.Tile;
import com.sun.javafx.geom.Vec4f;

import static org.lwjgl.glfw.GLFW.*;

public class Editor extends Game {

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

		if(input.isPressed(GLFW_MOUSE_BUTTON_LEFT)) {
			level.tiles[(mx + screen.xOffset >> 4) + (my + screen.yOffset >> 4) * level.width] = 3;
		}
		if(input.isPressed(GLFW_MOUSE_BUTTON_RIGHT)) {
			level.tiles[(mx + screen.xOffset >> 4) + (my + screen.yOffset >> 4) * level.width] = 1;
		}
	}

	protected void render() {
		super.render();

		int mxBox = (mx + screen.xOffset) >> 4;
		int myBox = (my + screen.yOffset) >> 4;

		if (!paused) {
			Tile.BRICK_S.render(screen, mxBox << 4, myBox << 4);
			screen.drawRectangle((mxBox << 4) - screen.xOffset, (myBox << 4) - screen.yOffset, (mxBox << 4) + 16 - screen.xOffset, (myBox << 4) + 16 - screen.yOffset, new Vec4f(1.0f, 1.0f, 1.0f, 0.3f));
		}
	}
}
