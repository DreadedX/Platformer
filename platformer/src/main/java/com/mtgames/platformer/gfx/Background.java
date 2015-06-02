package com.mtgames.platformer.gfx;

import com.mtgames.platformer.gfx.opengl.TextureLoader;
import com.mtgames.utils.Debug;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Background {

	private final int speed;
	private       int width;
	private       int height;
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
		this.height = image.getHeight();

		textureID = TextureLoader.loadTexture(path);
	}

	public void render(Screen screen, int levelWidth, int levelHeight) {
		screen.renderBackground(textureID, speed, levelWidth, levelHeight, width, height);
	}

	public int getSpeed() {
		return speed;
	}
}
