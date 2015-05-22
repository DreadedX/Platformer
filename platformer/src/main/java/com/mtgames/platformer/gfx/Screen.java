package com.mtgames.platformer.gfx;

import com.mtgames.platformer.Game;
import com.mtgames.platformer.level.Level;
import com.sun.javafx.geom.Vec3f;
import com.sun.javafx.geom.Vec4f;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;

public class Screen {

	public final  int     width    = Game.WIDTH;
	public final  int     height   = Game.HEIGHT;
	private final int     scale    = Game.scale;
	public        int     xOffset  = 0;
	public        int     yOffset  = 0;
	public        boolean lighting = true;

	private int lightTextureID;
	private int lightBufferID;

	public void initLight() {
		lightBufferID = glGenFramebuffersEXT();
		lightTextureID = glGenTextures();
		int lightDepthBufferID = glGenRenderbuffersEXT();

		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, lightBufferID);

		glBindTexture(GL_TEXTURE_2D, lightTextureID);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width * scale, height * scale, 0, GL_RGBA, GL_INT, (ByteBuffer) null);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT, GL_COLOR_ATTACHMENT0_EXT, GL_TEXTURE_2D, lightTextureID, 0);

		glBindFramebufferEXT(GL_RENDERBUFFER_EXT, lightDepthBufferID);
		glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, GL_DEPTH_COMPONENT, width * scale, height * scale);
		glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_RENDERBUFFER_EXT, lightDepthBufferID);

		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
	}

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
//
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

	public  void renderLight(int x, int y, Vec3f colour, int radius, float intensity) {
		x -= xOffset;
		y -= yOffset;
		x *= scale;
		y *= scale;

		int numSubdivisions = 32;

		radius = radius * scale;

		glBegin(GL_TRIANGLE_FAN);
			glColor3f(colour.x * intensity, colour.y * intensity, colour.z * intensity);
			glVertex2f(x, y);
			glColor3f(0f, 0f, 0f);

			for (float angle = 0; angle<=Math.PI*2; angle+=((Math.PI*2)/numSubdivisions)) {
				glVertex2f(radius * (float) Math.cos(angle) + x, radius * (float) Math.sin(angle) + y);
			}

			glVertex2f(radius + x, y);
		glEnd();

		glColor3f(1f, 1f, 1f);

//		TODO: Cast shadow's

//		glColor3f(0.1f, 0.1f, 0.1f);
//
//		int scaleFactor = 16 * scale;
//		Level level = properties.getLevel();
//
//		for (int xTile = (x-radius+xOffset)/scaleFactor; xTile < (x+radius+xOffset)/scaleFactor; xTile++) {
//			for (int yTile = (y-radius+yOffset)/scaleFactor; yTile < (y+radius+yOffset)/scaleFactor; yTile++) {
//				if (level.getTile(xTile, yTile).isSolid()) {
//					glBegin(GL_TRIANGLE_FAN);
//						glVertex2f(x, y);
//						glVertex2f(xTile * scaleFactor - xOffset * scale, yTile * scaleFactor - yOffset * scale);
//						glVertex2f((xTile + 1) * scaleFactor - xOffset * scale, yTile * scaleFactor - yOffset * scale);
//					glEnd();
//				}
//			}
//		}
//
//		glColor3f(1f, 1f, 1f);
	}

	public void renderLightFBO(Screen screen, Level level) {
		Vec3f darkness = new Vec3f(0.1f, 0.1f, 0.1f);

		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, lightBufferID);
		glClearColor(darkness.x, darkness.y, darkness.z, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glBlendFunc(GL_SRC_ALPHA, GL_ONE);
		level.renderLights(screen);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);

		if (!Game.lightDebug) {
			glBlendFunc(GL_DST_COLOR, GL_ZERO);
		}

		glEnable(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D, lightTextureID);
		glBegin(GL_QUADS);
		glTexCoord2f(0, 1);
		glVertex2f(0, 0);

		glTexCoord2f(1, 1);
		glVertex2f(width * scale, 0);

		glTexCoord2f(1, 0);
		glVertex2f(width * scale, height * scale);

		glTexCoord2f(0, 0);
		glVertex2f(0, height * scale);
		glEnd();
		glDisable(GL_TEXTURE_2D);
		glColor3f(1.0f, 1.0f, 1.0f);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
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
