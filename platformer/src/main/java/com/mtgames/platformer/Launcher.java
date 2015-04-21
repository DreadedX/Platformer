package com.mtgames.platformer;

import com.mtgames.platformer.debug.Debug;

import java.util.Objects;

public class Launcher {
	public static void main(String[] args) {
		if (args.length > 0) {
			Game.scale = Integer.parseInt(args[0]);
		} else {
			Game.scale = 2;
		}

		if (args.length > 1) {
			if (Objects.equals(args[1], "debug")) {
				Debug.priority = Debug.INFO;
				Debug.debug = true;
			}
		}

		new Game().start();
	}
}
