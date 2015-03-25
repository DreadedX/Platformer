package com.mtgames.platformer.gfx.gui;

import com.mtgames.platformer.gfx.Screen;

public class Hud {

	private static double dashRatio   = 0;
	private static double healthRatio = 0;

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
		screen.render(screen.xOffset - 4, screen.yOffset - 4, 30);
		screen.render(screen.xOffset - 4, screen.yOffset + 12, 31);
		screen.render(screen.xOffset - 4, screen.yOffset + 28, 30, 0x02);
		
		/* Right part of dash bar */
		screen.render(screen.xOffset + 140, screen.yOffset - 4, 30, 0x01);
		screen.render(screen.xOffset + 140, screen.yOffset + 12, 31, 0x01);
		screen.render(screen.xOffset + 140, screen.yOffset + 28, 30, 0x03);
		
		/* Center part of dash bar */
		for (int i = 0; i < 8; i++) {
			screen.render(screen.xOffset + 12 + i * 16, screen.yOffset - 4, 29);
			screen.render(screen.xOffset + 12 + i * 16, screen.yOffset + 28, 29, 0x02);
		}

		screen.drawRectangle(12, 12, 140, 28, 0xff2f558d);
		screen.drawRectangle(12, 12, (int) (dashRatio * 140), 28, 0xff3662a2);
	}

	private static void renderHealth(Screen screen) {
		/* Left part of dash bar */
		screen.render(screen.xOffset + screen.width - 156, screen.yOffset - 4, 27);
		screen.render(screen.xOffset + screen.width - 156, screen.yOffset + 12, 28);
		screen.render(screen.xOffset + screen.width - 156, screen.yOffset + 28, 27, 0x02);
		
		/* Right part of dash bar */
		screen.render(screen.xOffset + screen.width - 12, screen.yOffset - 4, 27, 0x01);
		screen.render(screen.xOffset + screen.width - 12, screen.yOffset + 12, 28, 0x01);
		screen.render(screen.xOffset + screen.width - 12, screen.yOffset + 28, 27, 0x03);
		
		/* Center part of dash bar */
		for (int i = 0; i < 8; i++) {
			screen.render(screen.xOffset + screen.width - 140 + i * 16, screen.yOffset - 4, 26);
			screen.render(screen.xOffset + screen.width - 140 + i * 16, screen.yOffset + 28, 26, 0x02);
		}

		screen.drawRectangle(screen.width - 140, 12, screen.width - 12, 28, 0xff9d3d2f);
		screen.drawRectangle(screen.width - 140, 12, (int) ((screen.width - 156) + (healthRatio * 144)), 28, 0xffb44c36);
	}

	private static void renderLives(Screen screen) {
//		TODO: This needs to be fixed
		/* Left part of dash bar */
		screen.render(screen.xOffset + screen.width / 2 - 40, screen.yOffset - 4, 24);
		screen.render(screen.xOffset + screen.width / 2 - 40, screen.yOffset + 12, 25);
		screen.render(screen.xOffset + screen.width / 2 - 40, screen.yOffset + 28, 24, 0x02);
		
		/* Right part of dash bar */
		screen.render(screen.xOffset + screen.width / 2 + 32, screen.yOffset - 4, 24, 0x01);
		screen.render(screen.xOffset + screen.width / 2 + 32, screen.yOffset + 12, 25, 0x01);
		screen.render(screen.xOffset + screen.width / 2 + 32, screen.yOffset + 28, 24, 0x03);
		
		screen.drawRectangle(screen.width / 2 - 24, 10, screen.width / 2 + 32, 30, 0xff484848);
	}

	private static void renderOther(Screen screen) {
		screen.render(screen.xOffset - 4, screen.yOffset - 4, 22);
		screen.render(screen.xOffset - 4, screen.yOffset + 28, 22, 0x02);

		screen.render(screen.xOffset + screen.width - 12, screen.yOffset - 4, 22, 0x01);
		screen.render(screen.xOffset + screen.width - 12, screen.yOffset + 28, 22, 0x03);

		for (int i = 0; i < ((screen.width / 2) + 16) >> 4; i++) {
			screen.render(screen.xOffset + 140 + i * 16, screen.yOffset - 4, 22);
			screen.render(screen.xOffset + 140 + i * 16, screen.yOffset + 28, 22, 0x02);
		}
	}
}
