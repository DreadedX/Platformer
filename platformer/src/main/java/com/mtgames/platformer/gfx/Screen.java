package com.mtgames.platformer.gfx;

import com.mtgames.platformer.debug.Debug;

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

	private BufferedImage overlayBig  = null;
	private BufferedImage overlayDash = null;
	private final int[] overlayAlpha;
	private       int[] overlayLightPixelsBig;
	private       int[] overlayLightPixelsDash;

	public Screen(int width, int height) {
		this.width = width;
		this.height = height;

		pixels = new int[width * height];
		overlayAlpha = new int[width * height];

		try {
			overlayBig = ImageIO.read(Sheet.class.getResourceAsStream("/graphics/lights/big.png"));
			overlayDash = ImageIO.read(Sheet.class.getResourceAsStream("/graphics/lights/dash.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (overlayBig == null || overlayDash == null) {
			return;
		}

		overlayLightPixelsBig = overlayBig.getRGB(0, 0, overlayBig.getWidth(), overlayBig.getHeight(), null, 0, overlayBig.getWidth());
		overlayLightPixelsDash = overlayDash.getRGB(0, 0, overlayDash.getWidth(), overlayDash.getHeight(), null, 0, overlayDash.getWidth());
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

	private void render(int xPos, int yPos, Sheet sheet, int tile, boolean small, int mirrorDir) {
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

	public void addLighting(int x1, int y1, int type) {
		addLighting(x1, y1, type, 0x00);
	}

//	TODO: Make coloured lighting render over all entities
	@SuppressWarnings("ConstantConditions") public void addLighting(int x1, int y1, int type, int modifier) {
		if (lighting && modifier < 0xff) {
			BufferedImage overlay;
			int[] overlayLightPixels;
			int overlayLightColour;

			switch (type) {
//				Mobs
				case 0:
					overlay = overlayBig;
					overlayLightPixels = overlayLightPixelsBig;
					overlayLightColour = 0xffae00;
					break;

//				Dash
				case 1:
					overlay = overlayDash;
					overlayLightPixels = overlayLightPixelsDash;
					overlayLightColour = 0x68afaf;
					break;

//				Glowstick
				case 2:
					overlay = overlayBig;
					overlayLightPixels = overlayLightPixelsBig;
					overlayLightColour = 0x27a10d;
					break;

				default:
					Debug.log(type + " is not a valid light type!", Debug.ERROR);
					return;
			}

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
					int c1Alpha = overlayAlpha[x + y * width];
					int c2Alpha = new Color(overlayLightPixels[(x - x1) + (y - y1) * overlay.getWidth()], true).getAlpha() - modifier;

					int alpha;
					if (c2Alpha <= 0) {
						alpha = c1Alpha;
					} else {
						alpha = c1Alpha - c2Alpha;
						if (alpha <= 0) {
							alpha = 1;
						}
					}

					c2Alpha -= modifier;

					if (c2Alpha > 70) {
						c2Alpha = 70;
					}

					if (c2Alpha < 0) {
						c2Alpha = 0;
					}

					overlayAlpha[x + y * width] = alpha;
					if (overlayLightColour > 0) {
//						TODO: Make colour radius bigger
						pixels[x + y * width] = alphaBlend(pixels[x + y * width], overlayLightColour + (c2Alpha << 24));
					}
				}
			}
		}
	}

	public void renderLighting() {
		if (lighting) {
			for (int i = 0; i < overlayAlpha.length; i++) {
				pixels[i] = alphaBlend(pixels[i], overlayAlpha[i] << 24);
			}

			for (int i = 0; i < overlayAlpha.length; i++) {
//				TODO: Check if the if statement improves performance
//				TODO: This should be 0xea, but because overflow problems it is temp 0x7f
				if (overlayAlpha[i] != 0x7f) {
					overlayAlpha[i] = 0x7f;
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

	private int alphaBlend(int c1Hex, int c2Hex) {
//		TODO: FIX THE OVERFLOW ISSUE, MAX COLOUR SIZE IS 0x7FffFFff, NEEDS TO BE 0xFFffFFff
		if (c2Hex >> 24 == 0) {
			return c1Hex;
		}

		int c1Alpha = c1Hex >> 24;
		int c2Alpha = c2Hex >> 24;

		int c1Red = (c1Hex >> 16) - (c1Alpha << 8);
		int c2Red = (c2Hex >> 16) - (c2Alpha << 8);

		int c1Green = (c1Hex >> 8) - (c1Red << 8) - (c1Alpha << 16);
		int c2Green = (c2Hex >> 8) - (c2Red << 8) - (c2Alpha << 16);

		int c1Blue = (c1Hex) - (c1Red << 16) - (c1Green << 8) - (c1Alpha << 24);
		int c2Blue = (c2Hex) - (c2Red << 16) - (c2Green << 8) - (c2Alpha << 24);

		int resultRed = (c2Red * c2Alpha + c1Red * (255 - c2Alpha)) / 255;
		int resultGreen = (c2Green * c2Alpha + c1Green * (255 - c2Alpha)) / 255;
		int resultBlue = (c2Blue * c2Alpha + c1Blue * (255 - c2Alpha)) / 255;

		return ((resultRed << 16) + (resultGreen << 8) + (resultBlue));
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
