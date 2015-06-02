package com.mtgames.platformer.gfx.gui;

import com.mtgames.platformer.Game;
import com.mtgames.platformer.gfx.Font;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.utils.Text;
import com.sun.javafx.geom.Vec3f;
import com.sun.javafx.geom.Vec4f;

import java.util.ArrayList;
import java.util.List;

public class GUI {

	private static Screen    screen = Game.screen;
	private static List<Runnable> list   = new ArrayList<>();

	public static void textBox(String title, String msg) {
		msg = Text.wrap(msg, (screen.width >> 3) - 4);

		int xTitle = screen.xOffset + screen.width / 2 - title.length() * 4;
		int height = msg.length() - msg.replace("\n", "").length();

		screen.drawRectangle(8, screen.height / 2 - 48, screen.width - 8, screen.height / 2 - 8 + 8 * height, new Vec4f(0.1f, 0.1f, 0.1f, 1.0f));
		Font.render(title.toUpperCase(), screen, xTitle, screen.yOffset + screen.height / 2 - 40);
		Font.render(msg, screen, screen.xOffset + 16, screen.yOffset + screen.height / 2 - 24);
	}

	public static void progressBar(int x, int y, int length, float ratio, Vec3f colour) {
		screen.drawRectangle(x-length/2, y-16, x+length/2, y+16, new Vec4f(0.1f, 0.1f, 0.1f, 1.0f));
		float lengthRatio = length * ratio - length/2;
		screen.drawRectangle(x-length/2, y-16, (int) (x+lengthRatio), y+16, new Vec4f(colour.x, colour.y, colour.z, 1.0f));
	}

	public static void add(Runnable task) {
		list.add(task);
	}

	public static void render() {
		list.forEach(java.lang.Runnable::run);
		list.clear();
	}
}
