package com.mtgames.platformer.debug;

import com.mtgames.platformer.Game;
import com.mtgames.platformer.InputHandler;
import com.mtgames.platformer.entities.Player;
import com.mtgames.platformer.entities.enemies.BaseEnemy;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.level.Level;

import java.util.Objects;

import com.mtgames.platformer.entities.FreeCamera;

public class Command {
	private static Level        level = null;
	private static InputHandler input = null;
	private static Screen screen = null;

	public static void exec(String command) {
		String[] commands = command.split(" ");

		switch (commands[0].toLowerCase()) {
			default:
				Debug.log("'" + commands[0] + "' is not a valid command", Debug.WARNING);
				break;

			case "load":
				if (commands.length == 2) {
					level.path = "levels/" + commands[1] + ".map";
					level.reload = true;
				} else {
					Debug.log("Invalid arguments, usage: load <level name>", Debug.WARNING);
				}
				break;

			case "reload":
				if (commands.length == 1) {
					level.reload = true;
				} else {
					Debug.log("Invalid arguments, usage: load", Debug.WARNING);
				}
				break;

			case "move":
				if (commands.length == 4) {
					if (Objects.equals(commands[1], "*")) {
						for (int i = 0; i < level.entities.size(); i++) {
							level.entities.get(i).x = Integer.parseInt(commands[2]);
							level.entities.get(i).y = Integer.parseInt(commands[3]);
						}
					} else if (Integer.parseInt(commands[1]) < level.entities.size()) {
						level.entities.get(Integer.parseInt(commands[1])).x = Integer.parseInt(commands[2]);
						level.entities.get(Integer.parseInt(commands[1])).y = Integer.parseInt(commands[3]);
					} else {
						Debug.log("'" + commands[1] + "' is not a valid id", Debug.WARNING);
					}

				} else {
					Debug.log("Invalid arguments, usage: move <id> <x> <y>", Debug.WARNING);
				}
				break;

			case "spawn":
				if (commands.length == 4) {

					if (commands[2].equals("*")) {
						commands[2] = String.valueOf(level.entities.get(0).x);
					}

					if (commands[3].equals("*")) {
						commands[3] = String.valueOf(level.entities.get(0).y);
					}

					switch (Integer.parseInt(commands[1])) {
						default:
							Debug.log("'" + commands[1] + "' is not a valid id", Debug.WARNING);
							break;

						case 0:
							level.addEntity(new Player(level, Integer.parseInt(commands[2]), Integer.parseInt(commands[3]), input));
							break;

						case 1:
							level.addEntity(new BaseEnemy(level, Integer.parseInt(commands[2]), Integer.parseInt(commands[3])));
							break;

						case 99:
							level.addEntity(new FreeCamera(level, Integer.parseInt(commands[2]), Integer.parseInt(commands[3]), input));
							break;
					}
				} else {
					Debug.log("Invalid arguments, usage: spawn <id> <x> <y>", Debug.WARNING);
				}
				break;

			case "kill":
				if (commands.length == 2) {
					if (Integer.parseInt(commands[1]) < level.entities.size()) {
						level.entities.remove(Integer.parseInt(commands[1]));
					} else {
						Debug.log("'" + commands[1] + "' is not a valid id", Debug.WARNING);
					}
				} else {
					Debug.log("Invalid arguments, usage: kill <uid>", Debug.WARNING);
				}
				break;

			case "freecam":
				if (commands.length == 1) {
					if (level.entities.get(0) instanceof Player) {
						level.entities.set(0, new FreeCamera(level, level.entities.get(0).x, level.entities.get(0).y, input));
					} else if (level.entities.get(0) instanceof FreeCamera) {
						level.entities.set(0, new Player(level, level.entities.get(0).x, level.entities.get(0).y, input));
					} else {
						Debug.log("entity 0 is not a Player or a FreeCamera: " + level.entities.get(0).getClass(), Debug.ERROR);
					}
				} else {
					Debug.log("Invalid arguments, usage: freecam true/false", Debug.WARNING);
				}
				break;

			case "lighting":
				if (commands.length == 1) {
					screen.lighting = !screen.lighting;
				} else {
					Debug.log("Invalid arguments, usage: lighting", Debug.WARNING);
				}
				break
						;

			case "shake":
				Game.shakeCam = !Game.shakeCam;
				break;

			case "exit":
//				TODO: Add check to make sure the command is valid
				if (commands.length == 2) {
					System.exit(Integer.parseInt(commands[1]));
				}
				System.exit(0);
				break;

		}
	}

	public static void set(Level level, InputHandler input, Screen screen) {
		Command.level = level;
		Command.input = input;
		Command.screen = screen;
	}
}
