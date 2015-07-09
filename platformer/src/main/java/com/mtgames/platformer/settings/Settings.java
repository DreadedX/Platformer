package com.mtgames.platformer.settings;

import com.mtgames.utils.Debug;
import com.mtgames.utils.Json;
import org.json.JSONObject;

import java.io.File;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;

public class Settings {
	//	List of all keybindings for easier rebinding
	public static int KEY_DEBUG  = GLFW_KEY_F3;                            //292
	public static int KEY_MOD    = GLFW_KEY_LEFT_SHIFT;                    //340
	public static int KEY_SELECT = GLFW_MOUSE_BUTTON_LEFT;                //  0

	public static int KEY_JUMP = GLFW_KEY_SPACE;                        // 32

	public static int KEY_UP    = GLFW_KEY_W;                                // 87
	public static int KEY_LEFT  = GLFW_KEY_A;                            // 65
	public static int KEY_DOWN  = GLFW_KEY_S;                            // 83
	public static int KEY_RIGHT = GLFW_KEY_D;                            // 68

	public static  int KEY_MESSAGE = GLFW_KEY_M;                            // 77
	public static int KEY_DASH    = GLFW_KEY_W;                            // 87
	public static int KEY_TORCH   = GLFW_KEY_Q;                            // 81

	public static int KEY_PAUSE = GLFW_KEY_ESCAPE;                        //256

	public static int KEY_TILE_SELECT = GLFW_KEY_SPACE;                    // 32
	public static int KEY_LAYER0      = GLFW_KEY_1;                            // 49
	public static int KEY_LAYER1      = GLFW_KEY_2;                            // 50

	public static int KEY_TILE_PLACE  = GLFW_MOUSE_BUTTON_LEFT;            //  0
	public static int KEY_TILE_REMOVE = GLFW_MOUSE_BUTTON_RIGHT;        //  1

	public Settings() {
		if (new File("platformer/settings.json").exists()) {
			JSONObject settings = new Json().get("platformer/settings.json", true);
			if (settings.has("version")) {
				if (Objects.equals(settings.getString("version"), "1.0")) {
					setKeyBindings(settings);
				} else {
					Debug.log("Config file is outdated, using defaults", Debug.WARNING);
				}
			} else {
				Debug.log("platformer/settings.json is not a valid settings file.", Debug.ERROR);
			}
		} else {
			Debug.log("Could not find config file, using defaults", Debug.WARNING);
		}
	}

	private void setKeyBindings(JSONObject object) {
		if (object.has("keyBindings")) {
			JSONObject keyBindings = object.getJSONObject("keyBindings");
			if (keyBindings.has("debug")) {
				KEY_DEBUG = keyBindings.getInt("debug");
			}
			if (keyBindings.has("mod")) {
				KEY_MOD = keyBindings.getInt("mod");
			}
			if (keyBindings.has("select")) {
				KEY_SELECT = keyBindings.getInt("select");
			}
			if (keyBindings.has("jump")) {
				KEY_JUMP = keyBindings.getInt("jump");
			}
			if (keyBindings.has("up")) {
				KEY_UP= keyBindings.getInt("up");
			}
			if (keyBindings.has("left")) {
				KEY_LEFT = keyBindings.getInt("left");
			}
			if (keyBindings.has("down")) {
				KEY_DOWN = keyBindings.getInt("down");
			}
			if (keyBindings.has("right")) {
				KEY_RIGHT = keyBindings.getInt("right");
			}
			if (keyBindings.has("message")) {
				KEY_MESSAGE = keyBindings.getInt("message");
			}
			if (keyBindings.has("dash")) {
				KEY_DASH = keyBindings.getInt("dash");
			}
			if (keyBindings.has("torch")) {
				KEY_TORCH = keyBindings.getInt("torch");
			}
			if (keyBindings.has("pause")) {
				KEY_PAUSE = keyBindings.getInt("pause");
			}
			if (keyBindings.has("tileSelect")) {
				KEY_TILE_SELECT = keyBindings.getInt("tileSelect");
			}
			if (keyBindings.has("layer0")) {
				KEY_LAYER0 = keyBindings.getInt("layer0");
			}
			if (keyBindings.has("layer1")) {
				KEY_LAYER1 = keyBindings.getInt("layer1");
			}
			if (keyBindings.has("tilePlace")) {
				KEY_TILE_PLACE = keyBindings.getInt("tilePlace");
			}
			if (keyBindings.has("tileRemove")) {
				KEY_TILE_REMOVE = keyBindings.getInt("tileRemove");
			}
		}
	}
}
