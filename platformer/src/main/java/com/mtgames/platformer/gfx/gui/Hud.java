package com.mtgames.platformer.gfx.gui;

import com.mtgames.platformer.gfx.Screen;

public class Hud {

	private static double dashRatio   = 0;
	private static double healthRatio = 0;

	public static void render(Screen screen) {
		renderOther(screen);
		renderDash(screen);
		renderHealth(screen);
		renderLives(screen);
	}

	public static void setDash(double dashRatio) {
		Hud.dashRatio = dashRatio;
	}

	public static void setHealth(double healthRatio) {
		Hud.healthRatio = healthRatio;
	}

	private static void renderDash(Screen screen) {
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
			screen.render(screen.xOffset + 6 + i * 16, screen.yOffset - 2, 29);
			screen.render(screen.xOffset + 6 + i * 16, screen.yOffset + 14, 29, 0x02);
		}

		screen.drawRectangle(6, 6, 70, 14, 0xff2f558d);
		screen.drawRectangle(6, 6, (int) (dashRatio * 70), 14, 0xff3662a2);
		/*
		 * Font.render("D.A.S.H.", screen, screen.xOffset+8, screen.yOffset+7,
		 * Colours.get(-1, -1, -1, 222));
		 */
	}

	private static void renderHealth(Screen screen) {
		/* Left part of dash bar */
		screen.render(screen.xOffset + screen.width - 78, screen.yOffset - 2, 27);
		screen.render(screen.xOffset + screen.width - 78, screen.yOffset + 6, 28);
		screen.render(screen.xOffset + screen.width - 78, screen.yOffset + 14, 27, 0x02);
		
		/* Right part of dash bar */
		screen.render(screen.xOffset + screen.width - 6, screen.yOffset - 2, 27, 0x01);
		screen.render(screen.xOffset + screen.width - 6, screen.yOffset + 6, 28, 0x01);
		screen.render(screen.xOffset + screen.width - 6, screen.yOffset + 14, 27, 0x03);
		
		/* Center part of dash bar */
		for (int i = 0; i < 16; i++) {
			screen.render(screen.xOffset + screen.width - 70 + i * 16, screen.yOffset - 2, 26);
			screen.render(screen.xOffset + screen.width - 70 + i * 16, screen.yOffset + 14, 26, 0x02);
		}

		screen.drawRectangle(screen.width - 70, 6, screen.width - 6, 14, 0xff9d3d2f);
		screen.drawRectangle(screen.width - 70, 6, (int) ((screen.width - 76) + (healthRatio * 70)), 14, 0xffb44c36);
		/*
		 * Font.render("D.A.S.H.", screen, screen.xOffset+8, screen.yOffset+7,
		 * Colours.get(-1, -1, -1, 222));
		 */
	}

	private static void renderLives(Screen screen) {
		/* Left part of dash bar */
		screen.render(screen.xOffset + screen.width / 2 - 24, screen.yOffset - 2, 24);
		screen.render(screen.xOffset + screen.width / 2 - 24, screen.yOffset + 6, 25);
		screen.render(screen.xOffset + screen.width / 2 - 24, screen.yOffset + 14, 24, 0x02);
		
		/* Right part of dash bar */
		screen.render(screen.xOffset + screen.width / 2 + 16, screen.yOffset - 2, 24, 0x01);
		screen.render(screen.xOffset + screen.width / 2 + 16, screen.yOffset + 6, 25, 0x01);
		screen.render(screen.xOffset + screen.width / 2 + 16, screen.yOffset + 14, 24, 0x03);
		
		/* Center part of dash bar */
		for (int i = 0; i < 4; i++) {
			screen.render(screen.xOffset + screen.width / 2 - 16 + i * 16, screen.yOffset - 2, 23);
			screen.render(screen.xOffset + screen.width / 2 - 16 + i * 16, screen.yOffset + 14, 23, 0x02);
		}

		screen.drawRectangle(screen.width / 2 - 16, 6, screen.width / 2 + 16, 14, 0xff484848);
		/*
		 * Font.render("D.A.S.H.", screen, screen.xOffset+8, screen.yOffset+7,
		 * Colours.get(-1, -1, -1, 222));
		 */
	}

	private static void renderOther(Screen screen) {
		screen.render(screen.xOffset - 2, screen.yOffset - 2, 22);
		screen.render(screen.xOffset - 2, screen.yOffset + 14, 22, 0x02);

		screen.render(screen.xOffset + screen.width - 6, screen.yOffset - 2, 22, 0x01);
		screen.render(screen.xOffset + screen.width - 6, screen.yOffset + 14, 22, 0x03);

		for (int i = 0; i < ((screen.width / 2) - 86) >> 3; i++) {
			screen.render(screen.xOffset + 70 + i * 16, screen.yOffset - 2, 22);
			screen.render(screen.xOffset + 70 + i * 16, screen.yOffset + 14, 22, 0x02);
		}

		for (int i = 0; i < ((screen.width - 78) - ((screen.width / 2) + 16)) >> 4; i++) {
			screen.render(screen.xOffset + screen.width / 2 + 16 + i * 16, screen.yOffset - 2, 22);
			screen.render(screen.xOffset + screen.width / 2 + 16 + i * 16, screen.yOffset + 14, 22, 0x02);
		}

	}
}
