package com.mtgames.platformer.gfx;

import com.amd.aparapi.Kernel;
import com.mtgames.platformer.Game;
import com.mtgames.platformer.debug.Debug;
import com.mtgames.platformer.gfx.lighting.OpenCL;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Screen {

	private static final byte BIT_MIRROR_X = 0x01;
	private static final byte BIT_MIRROR_Y = 0x02;

	public int[] pixels;
	public final int     width    = Game.WIDTH;
	public final int     height   = Game.HEIGHT;
	public       int     xOffset  = 0;
	public       int     yOffset  = 0;
	public       boolean lighting = true;

	private BufferedImage overlayBig   = null;
	private BufferedImage overlayDash  = null;
	private BufferedImage overlayTorch = null;
	private long[] overlayAlpha;
	private int[]  overlayLightPixelsBig;
	private int[]  overlayLightPixelsDash;
	private int[]  overlayLightPixelsTorch;

	private OpenCL openCL;

	public Screen() {
		pixels = new int[width * height];
		overlayAlpha = new long[width * height];

		try {
			overlayBig = ImageIO.read(Sheet.class.getResourceAsStream("/graphics/lights/big.png"));
			overlayDash = ImageIO.read(Sheet.class.getResourceAsStream("/graphics/lights/dash.png"));
			overlayTorch = ImageIO.read(Sheet.class.getResourceAsStream("/graphics/lights/torch.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (overlayBig == null || overlayDash == null || overlayTorch == null) {
			return;
		}

		overlayLightPixelsBig = overlayBig.getRGB(0, 0, overlayBig.getWidth(), overlayBig.getHeight(), null, 0, overlayBig.getWidth());
		overlayLightPixelsDash = overlayDash.getRGB(0, 0, overlayDash.getWidth(), overlayDash.getHeight(), null, 0, overlayDash.getWidth());
        overlayLightPixelsTorch = overlayTorch.getRGB(0, 0, overlayTorch.getWidth(), overlayTorch.getHeight(), null, 0, overlayTorch.getWidth());

		openCL = new OpenCL(pixels, overlayAlpha);
//		openCL.setMode(Kernel.EXECUTION_MODE.GPU);
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

//	TODO: Make coloured lighting render over all entities
	@SuppressWarnings("ConstantConditions") public void addLighting(int x, int y, int type, int colour, int modifier) {
		if (lighting && modifier < 0xff) {
			BufferedImage overlay;
			int[] overlayLightPixels;
//			int colour;

			switch (type) {
//				Mobs
				case 0:
					overlay = overlayBig;
					overlayLightPixels = overlayLightPixelsBig;
					break;

//				Dash
				case 1:
					overlay = overlayDash;
					overlayLightPixels = overlayLightPixelsDash;
					break;

//				Torch
                case 2:
                    overlay = overlayTorch;
                    overlayLightPixels = overlayLightPixelsTorch;
                    break;

				default:
					Debug.log(type + " is not a valid light type!", Debug.ERROR);
					return;
			}

			x -= overlay.getWidth()/2 + xOffset;
			y -= overlay.getHeight()/2 + yOffset;

			int x2 = x + overlay.getWidth();
			int y2 = y + overlay.getHeight();

			for (int forY = y; forY < y2; forY++) {
				if (forY < 0 || forY >= height) {
					continue;
				}

				for (int forX = x; forX < x2; forX++) {
					if (forX < 0 || forX >= width) {
						continue;
					}
					int c1Alpha = (int) overlayAlpha[forX + forY * width];
					int c2Alpha = new Color(overlayLightPixels[(forX - x) + (forY - y) * overlay.getWidth()], true).getAlpha() - modifier;

					int alpha;
					if (c2Alpha <= 1) {
						alpha = c1Alpha;
					} else {
						alpha = c1Alpha - c2Alpha;
						if (alpha <= 0) {
							alpha = 1;
						}
					}

					overlayAlpha[forX + forY * width] = alpha;

					if (c2Alpha > 70) {
						c2Alpha = 70;
					}

					if (c2Alpha < 0) {
						c2Alpha = 0;
					}

					if (colour > 0) {
//						TODO: Make colour radius bigger
						pixels[forX + forY * width] = alphaBlend(pixels[forX + forY * width], colour + (c2Alpha << 24));
					}
				}
			}
		}
	}

	public void renderLighting() {
		if (lighting) {
			if (openCL.running()) {
				int[] pixelsCL = pixels;
				long[] overlayAlphaCL = overlayAlpha;

				openCL.alphaBlendCL.put(pixelsCL).put(overlayAlphaCL).execute(pixels.length).get(pixelsCL).get(overlayAlphaCL);

				pixels = pixelsCL;
				overlayAlpha = overlayAlphaCL;
			} else {
				for (int i = 0; i < overlayAlpha.length; i++) {
					pixels[i] = alphaBlend(pixels[i], overlayAlpha[i] << 24);
				}

				for (int i = 0; i < overlayAlpha.length; i++) {
					if (overlayAlpha[i] != openCL.ALPHA) {
						overlayAlpha[i] = openCL.ALPHA;
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

	public void drawRectangle(int x1, int y1, int x2, int y2, long colour, boolean opaque) {
		for (int y = y1; y < y2; y++) {
			if (y < 0 || y >= height) {
				continue;
			}

			for (int x = x1; x < x2; x++) {
				if (x < 0 || x >= width) {
					continue;
				}

				if (opaque) {
					pixels[x + y * width] = (int) colour;
				} else {
					pixels[x + y * width] = alphaBlend(pixels[x + y * width], colour);
				}
			}
		}
	}

	private int alphaBlend(int c1Hex, long c2Hex) {
		if (c2Hex >> 24 == 0) {
			return c1Hex;
		}

		int c1Alpha = c1Hex >> 24;
		long c2Alpha = c2Hex >> 24;

		int c1Red = (c1Hex >> 16) - (c1Alpha << 8);
		long c2Red = ((c2Hex >> 16) - (c2Alpha << 8));

		int c1Green = (c1Hex >> 8) - (c1Red << 8) - (c1Alpha << 16);
		long c2Green = ((c2Hex >> 8) - (c2Red << 8) - (c2Alpha << 16));

		int c1Blue = (c1Hex) - (c1Red << 16) - (c1Green << 8) - (c1Alpha << 24);
		long c2Blue = ((c2Hex) - (c2Red << 16) - (c2Green << 8) - (c2Alpha << 24));

		long resultRed = ((c2Red * c2Alpha + c1Red * (255 - c2Alpha)) / 255);
		long resultGreen = ((c2Green * c2Alpha + c1Green * (255 - c2Alpha)) / 255);
		long resultBlue = ((c2Blue * c2Alpha + c1Blue * (255 - c2Alpha)) / 255);

		return (int) ((resultRed << 16) + (resultGreen << 8) + (resultBlue));
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
