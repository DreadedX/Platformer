package com.mtgames.platformer.gfx.gui;

import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.gfx.Sheet;

public class Hud {

	private static       double dashRatio   = 0;
	private static       double healthRatio = 0;
	private static final Sheet  sheet       = new Sheet("/graphics/hud.png");

	public static void render(Screen screen) {
		renderOther(screen);
		renderDash(screen);
		renderHealth(screen);
		//		renderLives(screen);
	}

	public static void setDash(double dashRatio) {
		Hud.dashRatio = dashRatio;
	}

	public static void setHealth(double healthRatio) {
		Hud.healthRatio = healthRatio;
	}

	private static void renderDash(Screen screen) {
		/* Left part of dash bar */
		screen.render(screen.xOffset - 4, screen.yOffset - 4, sheet, 8);
		screen.render(screen.xOffset - 4, screen.yOffset + 12, sheet, 9);
		screen.render(screen.xOffset - 4, screen.yOffset + 28, sheet, 8, 0x02);
		
		/* Right part of dash bar */
		screen.render(screen.xOffset + 140, screen.yOffset - 4, sheet, 8, 0x01);
		screen.render(screen.xOffset + 140, screen.yOffset + 12, sheet, 9, 0x01);
		screen.render(screen.xOffset + 140, screen.yOffset + 28, sheet, 8, 0x03);
		
		/* Center part of dash bar */
		for (int i = 0; i < 8; i++) {
			screen.render(screen.xOffset + 12 + i * 16, screen.yOffset - 4, sheet, 7);
			screen.render(screen.xOffset + 12 + i * 16, screen.yOffset + 28, sheet, 7, 0x02);
		}

		screen.drawRectangle(12, 12, 140, 28, 0xff2f558d, true);
		screen.drawRectangle(12, 12, (int) (dashRatio * 140), 28, 0xff3662a2, true);
	}

	private static void renderHealth(Screen screen) {
		/* Left part of dash bar */
		screen.render(screen.xOffset + screen.width - 156, screen.yOffset - 4, sheet, 5);
		screen.render(screen.xOffset + screen.width - 156, screen.yOffset + 12, sheet, 6);
		screen.render(screen.xOffset + screen.width - 156, screen.yOffset + 28, sheet, 5, 0x02);
		
		/* Right part of dash bar */
		screen.render(screen.xOffset + screen.width - 12, screen.yOffset - 4, sheet, 5, 0x01);
		screen.render(screen.xOffset + screen.width - 12, screen.yOffset + 12, sheet, 6, 0x01);
		screen.render(screen.xOffset + screen.width - 12, screen.yOffset + 28, sheet, 5, 0x03);
		
		/* Center part of dash bar */
		for (int i = 0; i < 8; i++) {
			screen.render(screen.xOffset + screen.width - 140 + i * 16, screen.yOffset - 4, sheet, 4);
			screen.render(screen.xOffset + screen.width - 140 + i * 16, screen.yOffset + 28, sheet, 4, 0x02);
		}

		screen.drawRectangle(screen.width - 140, 12, screen.width - 12, 28, 0xff9d3d2f, true);
		screen.drawRectangle(screen.width - 140, 12, (int) ((screen.width - 156) + (healthRatio * 144)), 28, 0xffb44c36, true);
	}

//	private static void renderLives(Screen screen) {
//		TODO: This needs to be fixed
		/* Left part of dash bar */
//		screen.render(screen.xOffset + screen.width / 2 - 40, screen.yOffset - 4, sheet, 2);
//		screen.render(screen.xOffset + screen.width / 2 - 40, screen.yOffset + 12, sheet, 3);
//		screen.render(screen.xOffset + screen.width / 2 - 40, screen.yOffset + 28, sheet, 2, 0x02);
		
		/* Right part of dash bar */
//		screen.render(screen.xOffset + screen.width / 2 + 32, screen.yOffset - 4, sheet, 2, 0x01);
//		screen.render(screen.xOffset + screen.width / 2 + 32, screen.yOffset + 12, sheet, 3, 0x01);
//		screen.render(screen.xOffset + screen.width / 2 + 32, screen.yOffset + 28, sheet, 2, 0x03);
		
//		screen.drawRectangle(screen.width / 2 - 24, 10, screen.width / 2 + 32, 8, 0xff484848, true);
//	}

	private static void renderOther(Screen screen) {
		screen.render(screen.xOffset - 4, screen.yOffset - 4, sheet, 0);
		screen.render(screen.xOffset - 4, screen.yOffset + 28, sheet, 0, 0x02);

		screen.render(screen.xOffset + screen.width - 12, screen.yOffset - 4, sheet, 0, 0x01);
		screen.render(screen.xOffset + screen.width - 12, screen.yOffset + 28, sheet, 0, 0x03);

		for (int i = 0; i < ((screen.width / 2) + 16) >> 4; i++) {
			screen.render(screen.xOffset + 140 + i * 16, screen.yOffset - 4, sheet, 0);
			screen.render(screen.xOffset + 140 + i * 16, screen.yOffset + 28, sheet, 0, 0x02);
		}
	}
}
