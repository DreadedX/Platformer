package com.mtgames.platformer.gfx;

import com.mtgames.platformer.Game;
import com.sun.javafx.geom.Vec4f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

public class Screen {

	public final int     width    = Game.WIDTH;
	public final int     height   = Game.HEIGHT;
	public final int scale = Game.scale;
	public       int     xOffset  = 0;
	public       int     yOffset  = 0;
	public       boolean lighting = true;

	public void renderTile(int x, int y, int textureID) {
		renderTile(x, y, textureID, 16, 0);
	}

	public void renderTile(int x, int y, int textureID, int size) {
		renderTile(x, y, textureID, size, 0);
	}

	public void renderTile(int x, int y, int textureID, int size, int part) {
		x -= xOffset;
		y -= yOffset;
		x *= scale;
		y *= scale;
		int modifier = size * scale;

		float partX1 = 0;
		float partY1 = 0;
		float partX2 = 1.0f;
		float partY2 = 1.0f;

		if (size > 16) {
			float partSize = 16f / size;

			modifier = 16 * scale;

			partX1 = (partSize * part) % 1.0f;
			partX2 = partX1 + partSize % 1.0f;

			partY1 = partSize * (part / (size / 16));
			partY2 = partY1 + partSize;

		}

		glEnable(GL_TEXTURE_2D);

		glBindTexture(GL_TEXTURE_2D, textureID);
		glBegin(GL_QUADS);
			glTexCoord2f(partX1, partY1); // top left
			glVertex2f(x, y);

			glTexCoord2f(partX2, partY1); // top right
			glVertex2f(x + modifier, y);

			glTexCoord2f(partX2, partY2); // bottom right
			glVertex2f(x + modifier, y + modifier);

			glTexCoord2f(partX1, partY2); // bottom left
			glVertex2f(x, y + modifier);
		glEnd();

		glDisable(GL_TEXTURE_2D);
	}

	public void renderEntity(int x, int y, int textureID, int size, boolean flipX) {
		x -= xOffset + size / 2;
		y -= yOffset + size / 2;
		x *= scale;
		y *= scale;
		int modifier = size * scale;

		glEnable(GL_TEXTURE_2D);

		glBindTexture(GL_TEXTURE_2D, textureID);
		glBegin(GL_QUADS);
		if (flipX) {
			glTexCoord2f(0, 0);
			glVertex2f(x + modifier, y);

			glTexCoord2f(1, 0);
			glVertex2f(x, y);

			glTexCoord2f(1, 1);
			glVertex2f(x, y + modifier);

			glTexCoord2f(0, 1);
			glVertex2f(x + modifier, y + modifier);
		} else {
			glTexCoord2f(0, 0);
			glVertex2f(x, y);

			glTexCoord2f(1, 0);
			glVertex2f(x + modifier, y);

			glTexCoord2f(1, 1);
			glVertex2f(x + modifier, y + modifier);

			glTexCoord2f(0, 1);
			glVertex2f(x, y + modifier);
		}
		glEnd();

		glDisable(GL_TEXTURE_2D);

//		glDeleteTextures(textureID);
	}

	public void renderBackground(int textureID, int speed, int levelWidth) {
		int xOffsetSpeed = xOffset / speed;
		levelWidth = levelWidth << 4;
		float repeat = (float) levelWidth / width;

		glEnable(GL_TEXTURE_2D);

		glBindTexture(GL_TEXTURE_2D, textureID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glBegin(GL_QUADS);
			glTexCoord2f(0, 0);
			glVertex2f(-xOffsetSpeed, 0);

			glTexCoord2f(repeat, 0);
			glVertex2f(levelWidth * scale - xOffsetSpeed, 0);

			glTexCoord2f(repeat, 1);
			glVertex2f(levelWidth * scale - xOffsetSpeed, height * scale);

			glTexCoord2f(0, 1);
			glVertex2f(-xOffsetSpeed, height * scale);
		glEnd();

		glDisable(GL_TEXTURE_2D);
	}

	public  void renderLight(int x, int y, int lightID) {
		x -= xOffset;
		y -= yOffset;
		x *= scale;
		y *= scale;

		int numSubdivisions = 32;
		int radius = 80 * scale;
		float intensity = 0f;

		glBegin(GL_TRIANGLE_FAN);
			glColor4f(0f, 0f, 0f, intensity);
			glVertex2f(x, y);
			glColor4f(0f, 0f, 0f, 0.9f);

			for (float angle = 0; angle<=Math.PI*2; angle+=((Math.PI*2)/numSubdivisions)) {
				glVertex2f(radius * (float) Math.cos(angle) + x, radius * (float) Math.sin(angle) + y);
			}

			glVertex2f(radius + x, y);
		glEnd();

		glColor3f(1f, 1f, 1f);
	}

	public void drawRectangle(int x1, int y1, int x2, int y2, Vec4f colour) {
		x1 *= scale;
		x2 *= scale;
		y1 *= scale;
		y2 *= scale;

		glColor4f(colour.x, colour.y, colour.z, colour.w);
		glBegin(GL_QUADS);
			glVertex2f(x1, y1);
			glVertex2f(x2, y1);
			glVertex2f(x2, y2);
			glVertex2f(x1, y2);
		glEnd();
		glColor3f(1.0f, 1.0f, 1.0f);
	}

	public void setOffset(int xOffset, int yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
}
