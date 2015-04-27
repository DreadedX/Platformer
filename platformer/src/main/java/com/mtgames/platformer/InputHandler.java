package com.mtgames.platformer;

import org.lwjgl.glfw.GLFWKeyCallback;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class InputHandler extends GLFWKeyCallback {
	public static boolean[] keys = new boolean[65535];

	@Override public void invoke(long window, int key, int scancode, int action, int mods) {
		if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ) {
			glfwSetWindowShouldClose(window, GL_TRUE);
		}
		keys[key] = action != GLFW_RELEASE;
	}

	public boolean isPressed(int key) {
		return keys[key];
	}

	public void set(int key, boolean state) {
		keys[key] = state;
	}
}
