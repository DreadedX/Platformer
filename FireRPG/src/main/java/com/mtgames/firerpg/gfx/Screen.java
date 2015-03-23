package com.mtgames.firerpg.gfx;

public class Screen {

	private static final byte BIT_MIRROR_X = 0x01;
	private static final byte BIT_MIRROR_Y = 0x02;

	public final  int[]       pixels;
	public final  int         width;
	public final  int         height;
	private final SpriteSheet sheet;
	public int xOffset = 0;
	public int yOffset = 0;

	public Screen(int width, int height, SpriteSheet sheet) {
		this.width = width;
		this.height = height;
		this.sheet = sheet;

		pixels = new int[width * height];
	}

	public void render(int xPos, int yPos, int tile) {
		render(xPos, yPos, tile, 0x00, 1);
	}

	public void render(int xPos, int yPos, int tile, int mirrorDir) {
		render(xPos, yPos, tile, mirrorDir, 1);
	}

	void render(int xPos, int yPos, int tile, int mirrorDir, int scale) {
		xPos -= xOffset;
		yPos -= yOffset;

		boolean mirrorX = (mirrorDir & BIT_MIRROR_X) > 0;
		boolean mirrorY = (mirrorDir & BIT_MIRROR_Y) > 0;

		int scaleMap = scale - 1;
		int xTile = tile % 32;
		int yTile = tile / 32;
		int tileOffset = (xTile << 3) + (yTile << 3) * sheet.width;
		for (int y = 0; y < 8; y++) {
			int ySheet = y;
			if (mirrorY) {
				ySheet = 7 - y;
			}

			int yPixel = y + yPos + (y * scaleMap) - ((scaleMap << 3) / 2);
			for (int x = 0; x < 8; x++) {
				int xSheet = x;
				if (mirrorX) {
					xSheet = 7 - x;
				}

				int xPixel = x + xPos + (x * scaleMap) - ((scaleMap << 3) / 2);
				int col = sheet.pixels[xSheet + ySheet * sheet.width + tileOffset];
				if (col != 0xffff00ff && col != 0xff7f007f) {
					for (int yScale = 0; yScale < scale; yScale++) {
						if (yPixel + yScale < 0 || yPixel + yScale >= height) {
							continue;
						}

						for (int xScale = 0; xScale < scale; xScale++) {
							if (xPixel + xScale < 0 || xPixel + xScale >= width) {
								continue;
							}

							pixels[(xPixel + xScale) + (yPixel + yScale) * width] = col;
						}
					}
				}
			}
		}
	}

	public void renderBackground(Background background) {
		int speed = background.getSpeed();
		int xOffsetSpeed = xOffset / speed;
		int yOffsetSpeed = yOffset / speed;

		for (int y = yOffset; y < (height + yOffset); y++) {
			int yPixel = y - yOffset;

			for (int x = xOffset; x < (width + xOffset); x++) {
				int xPixel = x - xOffset;
				int col = background.pixels[(x - xOffsetSpeed) + (y - yOffsetSpeed) * background.width];
				if (col != 0xffff00ff && col != 0xff7f007f) {
					if (yPixel < 0 || yPixel >= height) {
						continue;
					}

					if (xPixel < 0 || xPixel >= width) {
						continue;
					}

					pixels[(xPixel) + (yPixel) * width] = col;
				}
			}
		}
	}

	public void drawRectangle(int x1, int y1, int x2, int y2, int colour) {
		drawRectangle(x1, y1, x2, y2, colour, false);
	}

	void drawRectangle(int x1, int y1, int x2, int y2, int colour, boolean absolute) {
		if (absolute) {
			x1 -= xOffset;
			x2 -= xOffset;
			y1 -= yOffset;
			y2 -= yOffset;
		}

		for (int y = y1; y < y2; y++) {
			if (y < 0 || y >= height) {
				continue;
			}

			for (int x = x1; x < x2; x++) {
				if (x < 0 || x >= width) {
					continue;
				}

				pixels[x + y * width] = colour;
			}
		}
	}

	public void drawPoint(int xPos, int yPos, int colour) {
		xPos -= xOffset;
		yPos -= yOffset;
		pixels[(xPos) + (yPos) * width] = colour;
	}

	public void setOffset(int xOffset, int yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
}
