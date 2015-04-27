package com.mtgames.platformer.gfx;

import com.mtgames.platformer.gfx.opengl.TextureLoader;

public class Font {
	private static final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.,:;'\"!?$%()-=+~*[] ";
	private static final int[] textureID = new int[chars.length()];

	public static void init() {
		for (int i = 0; i < chars.length() - 1; i++) {
			textureID[i] = TextureLoader.loadTexture(TextureLoader.loadImage("/assets/graphics/font/" + chars.charAt(i) + ".png"));
		}

		textureID[chars.length()-1] = TextureLoader.loadTexture(TextureLoader.loadImage("/assets/graphics/font/space.png"));

	}

	public static void render(String msg, Screen screen, int x, int y) {
		int line = 0;
		int location = 0;
		for (int i = 0; i < msg.length(); i++) {

			if (msg.charAt(i) == '|') {
				line++;
				location = 0;
				continue;
			}

			int charIndex = chars.indexOf(msg.charAt(i));
			if (charIndex >= 0) {
				screen.render(x + (location * 8), y + 9 * line, textureID[charIndex], 8);
			}
			location++;
		}
	}
}
