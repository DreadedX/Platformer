package com.mtgames.platformer.gfx;

public class Screen {

	private static final byte BIT_MIRROR_X = 0x01;
	private static final byte BIT_MIRROR_Y = 0x02;

	public final  int[] pixels;
	public final  int   width;
	public final  int   height;
	public int xOffset = 0;
	public int yOffset = 0;

	public Screen(int width, int height) {
		this.width = width;
		this.height = height;

		pixels = new int[width * height];
	}

//	public void render(int xPos, int yPos, int tile) {
//		render(xPos, yPos, tile, 0x00, 1);
//	}

//	public void render(int xPos, int yPos, int tile, int mirrorDir) {
//		render(xPos, yPos, tile, mirrorDir, 1);
//	}

//	Default to 16x tileset and no mirror
	public void render(int xPos, int yPos, Sheet tileset, int tile) {
		render(xPos, yPos, tileset, tile, false, 0x00);
	}

	//	Default to 16x tileset
	public void render(int xPos, int yPos, Sheet tileset, int tile, int mirrorDir) {
		render(xPos, yPos, tileset, tile, false, mirrorDir);
	}

	//	Default to no mirror
	public void render(int xPos, int yPos, Sheet tileset, int tile, boolean small) {
		render(xPos, yPos, tileset, tile, small, 0x00);
	}

	void render(int xPos, int yPos, Sheet sheet, int tile, boolean small, int mirrorDir) {
		xPos -= xOffset;
		yPos -= yOffset;

		int tileSize;
		int bitSize;
		if (small) {
			tileSize = 8;
			bitSize = 3;
		} else {
			tileSize = 16;
			bitSize = 4;
		}

		int scale = 1;

		boolean mirrorX = (mirrorDir & BIT_MIRROR_X) > 0;
		boolean mirrorY = (mirrorDir & BIT_MIRROR_Y) > 0;

		int scaleMap = scale - 1;
		int xTile = tile % (sheet.width/tileSize);
		int yTile = tile / (sheet.width/tileSize);

		int tileOffset = (xTile << bitSize) + (yTile << bitSize) * sheet.width;
		for (int y = 0; y < tileSize; y++) {
			int ySheet = y;
			if (mirrorY) {
				ySheet = tileSize - 1 - y;
			}

			int yPixel = y + yPos + (y * scaleMap) - ((scaleMap << bitSize) / 2);
			for (int x = 0; x < tileSize; x++) {
				int xSheet = x;
				if (mirrorX) {
					xSheet = 15 - x;
				}

				int xPixel = x + xPos + (x * scaleMap) - ((scaleMap << bitSize) / 2);
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
