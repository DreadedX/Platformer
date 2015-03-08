package com.mtgames.firerpg.gfx.gui;

import com.mtgames.firerpg.gfx.Font;
import com.mtgames.firerpg.gfx.Screen;

public class Text {
	
	public static void textBox(Screen screen, String title, String msg) {
		msg = wrap(msg, (screen.width >> 3) - 4, 4);
		
		int xTitle = screen.xOffset + screen.width / 2 - title.length() * 4;
		int height = msg.length() - msg.replace("|", "").length();
		
		screen.drawRectangle(8, screen.height / 2 - 48, screen.width - 8, screen.height / 2 - 8 + 8 * height, 0xff000000);
		Font.render(title.toUpperCase(), screen, xTitle, screen.yOffset + screen.height / 2 - 40);
		Font.render(msg, screen, screen.xOffset + 16, screen.yOffset + screen.height / 2 - 24);
	}
	
	private static String wrap(String msg, int maxLength, int maxLines) {
		int lastSpace;
		
		if (msg.length() > maxLength * maxLines)
			throw new RuntimeException("\"" + msg + "\" has more lines that " + maxLines);
		
		for (int i = 1; i <= maxLines; i++) {
			if (msg.length() > maxLength * i) {
				lastSpace = msg.lastIndexOf(" ", maxLength * i);
				msg = msg.substring(0, lastSpace) + "|" + msg.substring(lastSpace + 1);
			}
		}
		
		return msg;
	}
}
