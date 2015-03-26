package com.mtgames.platformer.gfx;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Sheet {

	public int width;

	public int[] pixels;

	public Sheet(String path) {
		BufferedImage image = null;
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
}
