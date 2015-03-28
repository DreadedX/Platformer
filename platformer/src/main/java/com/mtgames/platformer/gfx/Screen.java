package com.mtgames.platformer.gfx;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Screen {

	private static final byte BIT_MIRROR_X = 0x01;
	private static final byte BIT_MIRROR_Y = 0x02;

	public final int[] pixels;
	public final int   width;
	public final int   height;
	public int     xOffset  = 0;
	public int     yOffset  = 0;
	public boolean lighting = true;

	private BufferedImage overlay = null;
	public final int[] overlayPixels;
	private      int[] overlayLightPixels;
	private      int   overlayColour;

	public Screen(int width, int height) {
		this.width = width;
		this.height = height;

		pixels = new int[width * height];
		overlayPixels = new int[width * height];

		try {
			overlay = ImageIO.read(Sheet.class.getResourceAsStream("/graphics/lighting_test.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (overlay == null) {
			return;
		}

		overlayLightPixels = overlay.getRGB(0, 0, overlay.getWidth(), overlay.getHeight(), null, 0, overlay.getWidth());
		overlayColour = overlayLightPixels[overlay.getWidth() / 2 + overlay.getWidth() / 2 * overlay.getWidth()];
	}

	//	Default to 16x tileset and no mirror
	public void render(int xPos, int yPos, Sheet sheet, int tile) {
		render(xPos, yPos, sheet, tile, false, 0x00);
	}

	//	Default to 16x tileset
	public void render(int xPos, int yPos, Sheet sheet, int tile, int mirrorDir) {
		render(xPos, yPos, sheet, tile, false, mirrorDir);
	}

	//	Default to no mirror
	public void render(int xPos, int yPos, Sheet sheet, int tile, boolean small) {
		render(xPos, yPos, sheet, tile, small, 0x00);
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

	public void setLighting(int x1, int y1) {
		if (lighting) {
			x1 -= overlay.getWidth()/2 + xOffset;
			y1 -= overlay.getHeight()/2 + yOffset;

			int x2 = x1 + overlay.getWidth();
			int y2 = y1 + overlay.getHeight();

			for (int y = y1; y < y2; y++) {
				if (y < 0 || y >= height) {
					continue;
				}

				for (int x = x1; x < x2; x++) {
					if (x < 0 || x >= width) {
						continue;
					}
					Color c1 = new Color(overlayPixels[x + y * width], true);
					Color c2 = new Color(overlayLightPixels[(x - x1) + (y - y1) * overlay.getWidth()], true);

					int alpha;
					if (c2.getAlpha() == 0) {
						alpha = c1.getAlpha();
					} else {
						alpha = c1.getAlpha() - c2.getAlpha();
						if (alpha <= 0) {
							alpha = 1;
						}
					}

					int colour = c1.getRGB() + (alpha) << 24;
					overlayPixels[x + y * width] = colour;
				}
			}
		}
	}

	public void renderLighting() {
		if (lighting) {
			for (int i = 0; i < overlayPixels.length; i++) {
				pixels[i] = alphaBlend(pixels[i], overlayPixels[i]);
			}

			for (int i = 0; i < overlayPixels.length; i++) {
				overlayPixels[i] = overlayColour;
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

	public void drawRectangle(int x1, int y1, int x2, int y2, int colour, boolean opaque) {

		for (int y = y1; y < y2; y++) {
			if (y < 0 || y >= height) {
				continue;
			}

			for (int x = x1; x < x2; x++) {
				if (x < 0 || x >= width) {
					continue;
				}

				if (opaque) {
					pixels[x + y * width] = colour;
				} else {
					pixels[x + y * width] = alphaBlend(pixels[x + y * width], colour);
				}
			}
		}
	}

	int alphaBlend(int c1Hex, int c2Hex) {
		Color c1 = new Color(c1Hex);
		Color c2 = new Color(c2Hex, true);

		if (c2.getRGB() == 0) {
			return c1.getRGB();
		}

		Color result;
		result = new Color(((c2.getRed() * c2.getAlpha() + c1.getRed() * (255 - c2.getAlpha())) / 255), ((c2.getGreen() * c2.getAlpha() + c1.getGreen() * (255 - c2.getAlpha())) / 255),
				((c2.getBlue() * c2.getAlpha() + c1.getBlue() * (255 - c2.getAlpha())) / 255));

//		COLOR EVERYTHING RED
//		result = new Color(((c2.getRed() * c2.getAlpha() + c1.getRed() * (255 - c2.getAlpha())) / 255) << 16);

		return result.getRGB();
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
