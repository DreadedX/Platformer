package com.mtgames.platformer.gfx;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Background {

	private final int speed;
	public        int width;
	public int[] pixels;

	public Background(String path, int speed) {
		BufferedImage image = null;
		this.speed = speed;

		try {
			image = ImageIO.read(Sheet.class.getResourceAsStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (image == null) {
			return;
		}

		this.width = image.getWidth();
		int height = image.getHeight();

		pixels = image.getRGB(0, 0, width, height, null, 0, width);
	}

	public int getSpeed() {
		return speed;
	}
}
