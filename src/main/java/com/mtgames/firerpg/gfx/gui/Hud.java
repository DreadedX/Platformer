package com.mtgames.firerpg.gfx.gui;

import com.mtgames.firerpg.gfx.Screen;

public class Hud {

	public static void renderDash(Screen screen, int dashWait) {
		/* Left part of dash bar */
		screen.render(screen.xOffset - 2, screen.yOffset - 2, 30);
		screen.render(screen.xOffset - 2, screen.yOffset + 6, 31);
		screen.render(screen.xOffset - 2, screen.yOffset + 14, 30, 0x02);

		/* Right part of dash bar */
		screen.render(screen.xOffset + 70, screen.yOffset - 2, 30, 0x01);
		screen.render(screen.xOffset + 70, screen.yOffset + 6, 31, 0x01);
		screen.render(screen.xOffset + 70, screen.yOffset + 14, 30, 0x03);

		/* Center part of dash bar */
		for (int i = 0; i < 8; i++) {
			screen.render(screen.xOffset + 6 + i * 8, screen.yOffset - 2, 29);
			screen.render(screen.xOffset + 6 + i * 8, screen.yOffset + 14, 29,
					0x02);
		}

		screen.drawRectangle(6, 6, 70, 14, 0xff3c3c47);
		screen.drawRectangle(6, 6, dashWait - 2, 14, 0xff68afff);
		/*
		 * Font.render("D.A.S.H.", screen, screen.xOffset+8, screen.yOffset+7,
		 * Colours.get(-1, -1, -1, 222));
		 */
	}
}
