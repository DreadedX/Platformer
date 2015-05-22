package com.mtgames.platformer.gfx;

import com.mtgames.platformer.gfx.opengl.TextureLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Background {

	private final int speed;
	private       int width;
	private       int textureID;

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

		textureID = TextureLoader.loadTexture(path);
	}

	public void render(Screen screen, int levelWidth) {
		screen.renderBackground(textureID, speed, levelWidth);
	}

	public int getSpeed() {
		return speed;
	}
}
