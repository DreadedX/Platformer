package com.mtgames.firerpg.gfx;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpriteSheet {

    public int		width;

    public int[]	pixels;
	
	public SpriteSheet(String path) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(SpriteSheet.class.getResourceAsStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (image == null) {
			return;
		}

        this.width = image.getWidth();
        int height = image.getHeight();
		
		pixels = image.getRGB(0, 0, width, height, null, 0, width);

        System.arraycopy(pixels, 0, pixels, 0, pixels.length);
	}
}
