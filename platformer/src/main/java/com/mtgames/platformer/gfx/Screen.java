package com.mtgames.platformer.gfx;

import com.mtgames.platformer.Game;
import com.mtgames.utils.Debug;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

public class Screen {

	private static final byte BIT_MIRROR_X = 0x01;
	private static final byte BIT_MIRROR_Y = 0x02;

	public final int[] pixels;
	public final int     width    = Game.WIDTH;
	public final int     height   = Game.HEIGHT;
	public final int scale = Game.scale;
	public       int     xOffset  = 0;
	public       int     yOffset  = 0;
	public       boolean lighting = true;

	private BufferedImage overlayBig   = null;
	private BufferedImage overlayDash  = null;
	private BufferedImage overlayTorch = null;
	private final int[] overlayAlpha;
	private final int[] overlayAlphaDefault;
	private       int[] overlayLightPixelsBig;
	private       int[] overlayLightPixelsDash;
	private       int[] overlayLightPixelsTorch;

	public Screen() {
		pixels = new int[width * height];
		overlayAlpha = new int[width * height];
		overlayAlphaDefault = new int[width * height];

		for (int i = 0; i < overlayAlphaDefault.length; i++) {
			overlayAlphaDefault[i] = 0xea;
		}

		try {
			overlayBig = ImageIO.read(Sheet.class.getResourceAsStream("/assets/graphics/lights/big.png"));
			overlayDash = ImageIO.read(Sheet.class.getResourceAsStream("/assets/graphics/lights/dash.png"));
			overlayTorch = ImageIO.read(Sheet.class.getResourceAsStream("/assets/graphics/lights/torch.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (overlayBig == null || overlayDash == null || overlayTorch == null) {
			return;
		}

		overlayLightPixelsBig = overlayBig.getRGB(0, 0, overlayBig.getWidth(), overlayBig.getHeight(), null, 0, overlayBig.getWidth());
		overlayLightPixelsDash = overlayDash.getRGB(0, 0, overlayDash.getWidth(), overlayDash.getHeight(), null, 0, overlayDash.getWidth());
        overlayLightPixelsTorch = overlayTorch.getRGB(0, 0, overlayTorch.getWidth(), overlayTorch.getHeight(), null, 0, overlayTorch.getWidth());

	}

	//	Default to 16x tileset and no mirror
//	public void render(int xPos, int yPos, Sheet sheet, int tile) {
//		render(xPos, yPos, sheet, tile, false, 0x00);
//	}

	//	Default to 16x tileset
//	public void render(int xPos, int yPos, Sheet sheet, int tile, int mirrorDir) {
//		render(xPos, yPos, sheet, tile, false, mirrorDir);
//	}

	//	Default to no mirror
//	public void render(int xPos, int yPos, Sheet sheet, int tile, boolean small) {
//		render(xPos, yPos, sheet, tile, small, 0x00);
//	}

	public void render(int x, int y, int textureID) {
		render(x, y, textureID, 16, false);
	}

	public void render(int x, int y, int textureID, int size) {
		render(x, y, textureID, size, false);
	}

	public void render(int x, int y, int textureID, int size, boolean flipX) {
		x -= xOffset;
		y -= yOffset;
		x *= scale;
		y *= scale;
		int modifier = size * scale;

		glBindTexture(GL_TEXTURE_2D, textureID);
		glBegin(GL_QUADS);
		if (flipX) {
			glTexCoord2f(0, 0); // top left
			glVertex2f(x + modifier, y);

			glTexCoord2f(1, 0); // bottom left
			glVertex2f(x, y);

			glTexCoord2f(1, 1); // bottom right
			glVertex2f(x, y + modifier);

			glTexCoord2f(0, 1); // top right
			glVertex2f(x + modifier, y + modifier);
		} else {
			glTexCoord2f(0, 0); // top left
			glVertex2f(x, y);

			glTexCoord2f(1, 0); // bottom left
			glVertex2f(x + modifier, y);

			glTexCoord2f(1, 1); // bottom right
			glVertex2f(x + modifier, y + modifier);

			glTexCoord2f(0, 1); // top right
			glVertex2f(x, y + modifier);
		}
		glEnd();

//		glDeleteTextures(textureID);
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
					int c1Alpha = overlayAlpha[forX + forY * width];
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
			for (int i = 0; i < overlayAlpha.length; i++) {
				pixels[i] = alphaBlend(pixels[i], (long) (overlayAlpha[i]) << 24);
			}
			System.arraycopy(overlayAlphaDefault, 0, overlayAlpha, 0, overlayAlpha.length);
		}
	}

	public void renderBackground(int textureID) {
		glBindTexture(GL_TEXTURE_2D, textureID);
		glBegin(GL_QUADS);
			glTexCoord2f(0, 0); // top left
			glVertex2f(0, 0);

			glTexCoord2f(1, 0); // bottom left
			glVertex2f(width * scale, 0);

			glTexCoord2f(1, 1); // bottom right
			glVertex2f(width * scale, height * scale);

			glTexCoord2f(0, 1); // top right
			glVertex2f(0, height * scale);
		glEnd();


//		int speed = background.getSpeed();
//		int xOffsetSpeed = xOffset / speed;
//		int yOffsetSpeed = yOffset / speed;
//
//		for (int y = yOffset; y < (height + yOffset); y++) {
//			int yPixel = y - yOffset;
//
//			for (int x = xOffset; x < (width + xOffset); x++) {
//				int xPixel = x - xOffset;
//				int col = background.pixels[(x - xOffsetSpeed) + (y - yOffsetSpeed) * background.width];
//				if (col != 0xffff00ff && col != 0xff7f007f) {
//					if (yPixel < 0 || yPixel >= height) {
//						continue;
//					}
//
//					if (xPixel < 0 || xPixel >= width) {
//						continue;
//					}
//
//					pixels[(xPixel) + (yPixel) * width] = col;
//				}
//			}
//		}
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

	private int alphaBlend(int c1, long c2) {
		int factor = (int) (c2 >> 24);
		int f1 = 256 - factor;
//		if (factor == 0) {
//			return c1;
//		}

		return (int) ((   (  ( (c1&0xFF00FF)*f1 + (c2&0xFF00FF)*factor )  &0xFF00FF00  )  | (   ( (c1&0x00FF00)*f1 + (c2&0x00FF00)*factor )  &0x00FF0000  )   ) >>>8);
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
