package com.mtgames.platformer;

import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;

import static org.lwjgl.glfw.GLFW.*;

public class InputHandler {
	private static final boolean[] keys = new boolean[65535];
	public GLFWKeyCallback keyCallback = null;
	public GLFWMouseButtonCallback mouseButtonCallback = null;

	public InputHandler() {
		keyCallback = new GLFWKeyCallback() {
			@Override public void invoke(long window, int key, int scancode, int action, int mods) {
//				if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
//					glfwSetWindowShouldClose(window, GL_TRUE);
//				}
				keys[key] = action != GLFW_RELEASE;
			}
		};

		mouseButtonCallback = new GLFWMouseButtonCallback() {
			@Override public void invoke(long window, int key, int action, int mods) {
				keys[key] = action != GLFW_RELEASE;
			}
		};
	}


	public boolean isPressed(int key) {
		return keys[key];
	}

	public void unset(int key) {
		keys[key] = false;
	}
}
