package com.mtgames.platformer.settings;

import static org.lwjgl.glfw.GLFW.*;

public class KeyBindings {
//	List of all keybindings for easier rebinding
	public static int KEY_DEBUG = GLFW_KEY_F3;
	public static int KEY_MOD = GLFW_KEY_LEFT_SHIFT;
	public static int KEY_SELECT = GLFW_MOUSE_BUTTON_LEFT;

	public static int KEY_JUMP = GLFW_KEY_SPACE;

	public static int KEY_UP = GLFW_KEY_W;
	public static int KEY_LEFT = GLFW_KEY_A;
	public static int KEY_DOWN = GLFW_KEY_S;
	public static int KEY_RIGHT = GLFW_KEY_D;

	public static int KEY_MESSAGE = GLFW_KEY_M;
	public static int KEY_DASH = GLFW_KEY_W;
	public static int KEY_TORCH = GLFW_KEY_Q;

	public static int KEY_PAUSE = GLFW_KEY_ESCAPE;

	public static int KEY_TILE_SELECT = GLFW_KEY_SPACE;
	public static int KEY_LAYER0 = GLFW_KEY_1;
	public static int KEY_LAYER1 = GLFW_KEY_2;

	public static int KEY_PLACE_TILE = GLFW_MOUSE_BUTTON_LEFT;
	public static int KEY_REMOVE_TILE = GLFW_MOUSE_BUTTON_RIGHT;
}
