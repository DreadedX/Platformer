package com.mtgames.platformer.gfx.gui;

import com.mtgames.platformer.Game;
import com.mtgames.platformer.gfx.Font;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.utils.Text;
import com.sun.javafx.geom.Vec3f;
import com.sun.javafx.geom.Vec4f;

import java.util.ArrayList;
import java.util.List;

import static com.mtgames.platformer.settings.Settings.*;

public class GUI {

	private static boolean pressed = false;

	private static final List<Runnable> list   = new ArrayList<>();

	public static void textBox(String title, String msgRaw) {
		add(() -> {
			//		msg = Text.wrap(msg, (screen.width >> 3) - 4);
			String msg = Text.wrap(msgRaw, 72);

			int xTitle = Screen.xOffset + Screen.width / 2 - title.length() * 4;
			int height = msg.length() - msg.replace("|", "").length() + 1;

			Screen.drawRectangle(8, Screen.height / 2 - 48, Screen.width - 8, Screen.height / 2 - 8 + 10 * height, new Vec4f(0.1f, 0.1f, 0.1f, 1.0f));
			Font.render(title.toUpperCase(), xTitle, Screen.yOffset + Screen.height / 2 - 40);
			Font.render(msg, Screen.xOffset + 16, Screen.yOffset + Screen.height / 2 - 24);
		});
	}

	public static void progressBar(int x, int y, int height, int length, float ratio, Vec3f colour) {
		add(() -> {
			Screen.drawRectangle(x - length / 2, y - height / 2, x + length / 2, y + height / 2, new Vec4f(0.1f, 0.1f, 0.1f, 1.0f));
			float lengthRatio = length * ratio - length / 2;
			Screen.drawRectangle(x - length / 2, y - height / 2, (int) (x + lengthRatio), y + height / 2, new Vec4f(colour.x, colour.y, colour.z, 1.0f));
		});
	}

	public static void buttonText(int x, int y, String msg, Vec3f colour, Runnable task) {
		add(() -> {
			int x1 = x - (msg.length() * 8) / 2 - 2;
			int x2 = x + (msg.length() * 8) / 2 + 2;
			int y1 = y - 6;
			int y2 = y + 6;

			button(x, y, msg.length() * 8 + 4, 12, () -> {
				Screen.drawRectangle(x1, y1, x2, y2, new Vec4f(colour.x, colour.y, colour.z, 1.0f));
				Font.render(msg, Screen.xOffset + x1 + 2, Screen.yOffset + y - 5);
				Screen.drawRectangle(x1, y1, x2, y2 - 1, new Vec4f(1.0f, 1.0f, 1.0f, 0.15f));
			}, task);

		});
	}

	public static void button(int x, int y, int width, int height, Runnable render, Runnable task) {
		add(() -> {
			int x1 = x - width / 2;
			int x2 = x + width / 2;
			int y1 = y - height / 2;
			int y2 = y + height / 2;

			boolean hover = false;

			for (int ix = x1; ix < x2; ix++) {
				for (int iy = y1; iy < y2; iy++) {
					if (Game.mx == ix && Game.my == iy) {
						hover = true;
						break;
					}
				}
			}

			render.run();

			if (hover && Game.input.isPressed(KEY_SELECT)) {
				Screen.drawRectangle(x1, y1, x2, y2, new Vec4f(0.0f, 0.0f, 0.0f, 0.1f));
				pressed = true;
			} else if (hover) {
				Screen.drawRectangle(x1, y1, x2, y2, new Vec4f(1.0f, 1.0f, 1.0f, 0.1f));
			} else {
				Screen.drawRectangle(x1, y1, x2, y2, new Vec4f(1.0f, 1.0f, 1.0f, 0.0f));
			}

			if (hover && !Game.input.isPressed(KEY_SELECT) && pressed) {
				pressed = false;
				task.run();
			}
		});
	}

//	GUI Framework
	private static void add(Runnable task) {
		list.add(task);
	}

	public static void render() {
//		list.forEach(java.lang.Runnable::run);
		for (int i =0; i < list.size(); i++) {
			list.get(i).run();
		}
		list.clear();
	}
}
