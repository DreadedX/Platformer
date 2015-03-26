package com.mtgames.platformer.gfx;

public class Font {
	private static final Sheet sheet = new Sheet("/graphics/font.png");

	public static void render(String msg, Screen screen, int x, int y) {
		int line = 0;
		int location = 0;
		for (int i = 0; i < msg.length(); i++) {

			if (msg.charAt(i) == '|') {
				line++;
				location = 0;
				continue;
			}

			String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ      abcdefghijklmnopqrstuvwxyz      0123456789.,:;'\"!?$%()-=+/*[]";
			int charIndex = chars.indexOf(msg.charAt(i));
			if (charIndex >= 0)
				screen.render(x + (location * 8), y + 9 * line, sheet, charIndex, true);
			location++;
		}
	}
}