package com.mtgames.platformer.gfx.gui;

import com.mtgames.platformer.Game;
import com.mtgames.platformer.gfx.Font;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.utils.Text;
import com.sun.javafx.geom.Vec3f;
import com.sun.javafx.geom.Vec4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class GUI {

	private static final Screen         screen = Game.screen;
	private static final List<Runnable> list   = new ArrayList<>();

	public static void textBox(String title, String msg) {
		//		msg = Text.wrap(msg, (screen.width >> 3) - 4);
		msg = Text.wrap(msg, 72);

		int xTitle = screen.xOffset + screen.width / 2 - title.length() * 4;
		int height = msg.length() - msg.replace("|", "").length() + 1;

		screen.drawRectangle(8, screen.height / 2 - 48, screen.width - 8, screen.height / 2 - 8 + 10 * height, new Vec4f(0.1f, 0.1f, 0.1f, 1.0f));
		Font.render(title.toUpperCase(), screen, xTitle, screen.yOffset + screen.height / 2 - 40);
		Font.render(msg, screen, screen.xOffset + 16, screen.yOffset + screen.height / 2 - 24);
	}

	public static void progressBar(int x, int y, int height, int length, float ratio, Vec3f colour) {
		screen.drawRectangle(x-length/2, y-height/2, x+length/2, y+height/2, new Vec4f(0.1f, 0.1f, 0.1f, 1.0f));
		float lengthRatio = length * ratio - length/2;
		screen.drawRectangle(x-length/2, y-height/2, (int) (x+lengthRatio), y+height/2, new Vec4f(colour.x, colour.y, colour.z, 1.0f));
	}

	public static void button(int x, int y, String msg, Vec3f colour, Runnable task) {
		int x1 = x-(msg.length()*8)/2-2;
		int x2 = x+(msg.length()*8)/2+2;
		int y1 = y-6;
		int y2 = y+7;

		boolean hover = false;

		for(int ix = x1; ix < x2; ix++) {
			for (int iy = y1; iy < y2; iy++) {
				if (Game.mx == ix && Game.my == iy) {
					hover = true;
					break;
				}
			}
		}

		if (hover) {
			screen.drawRectangle(x1, y1, x2, y2, new Vec4f(colour.x + 0.1f, colour.y + 0.1f, colour.z + 0.1f, 1.0f));
			if (Game.input.isPressed(GLFW_MOUSE_BUTTON_LEFT)) {
				task.run();
				Game.input.unset(GLFW_MOUSE_BUTTON_LEFT);
			}
		} else {
			screen.drawRectangle(x1, y1, x2, y2, new Vec4f(colour.x, colour.y, colour.z, 1.0f));
		}
		Font.render(msg, screen, screen.xOffset+x1+2, screen.yOffset+y-4);
	}

//	GUI Framework
	public static void add(Runnable task) {
		list.add(task);
	}

	public static void render() {
		list.forEach(java.lang.Runnable::run);
		list.clear();
	}
}
