package com.mtgames.firerpg.gfx.gui;

import com.mtgames.firerpg.gfx.Screen;

public class Hud {
	
	public static void renderDash(Screen screen, double dashRatio) {
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
			screen.render(screen.xOffset + 6 + i * 8, screen.yOffset + 14, 29, 0x02);
		}
		
		screen.drawRectangle(6, 6, 70, 14, 0xff2f558d);
		screen.drawRectangle(6, 6, (int) (dashRatio * 70), 14, 0xff3662a2);
		/*
		 * Font.render("D.A.S.H.", screen, screen.xOffset+8, screen.yOffset+7,
		 * Colours.get(-1, -1, -1, 222));
		 */
	}
	
	public static void renderHealth(Screen screen, double healthRatio) {
		/* Left part of dash bar */
		screen.render(screen.xOffset + screen.width - 78, screen.yOffset - 2, 27);
		screen.render(screen.xOffset + screen.width - 78, screen.yOffset + 6, 28);
		screen.render(screen.xOffset + screen.width - 78, screen.yOffset + 14, 27, 0x02);
		
		/* Right part of dash bar */
		screen.render(screen.xOffset + screen.width - 6, screen.yOffset - 2, 27, 0x01);
		screen.render(screen.xOffset + screen.width - 6, screen.yOffset + 6, 28, 0x01);
		screen.render(screen.xOffset + screen.width - 6, screen.yOffset + 14, 27, 0x03);
		
		/* Center part of dash bar */
		for (int i = 0; i < 8; i++) {
			screen.render(screen.xOffset + screen.width - 70 + i * 8, screen.yOffset - 2, 26);
			screen.render(screen.xOffset + screen.width - 70 + i * 8, screen.yOffset + 14, 26, 0x02);
		}
		
		screen.drawRectangle(screen.width - 70, 6, screen.width - 6, 14, 0xff9d3d2f);
		screen.drawRectangle(screen.width - 70, 6, (int) ((screen.width - 76) + (healthRatio * 70)), 14, 0xffb44c36);
		/*
		 * Font.render("D.A.S.H.", screen, screen.xOffset+8, screen.yOffset+7,
		 * Colours.get(-1, -1, -1, 222));
		 */
	}
}
