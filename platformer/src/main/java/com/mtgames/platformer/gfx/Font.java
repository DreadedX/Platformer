package com.mtgames.platformer.gfx;

import com.mtgames.platformer.gfx.shaders.TextureLoader;

public class Font {
	private static final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789.,:;'\"!?$%()-=+/*[] ";
	private static int textureID;
	private static final int charCount = chars.length();

	public static void init() {
		textureID = TextureLoader.loadTexture("/assets/graphics/font/font.png");
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
				screen.renderFont(x + (location * 8), y + 11 * line, textureID, charCount, charIndex);
			}
			location++;
		}
	}
}
