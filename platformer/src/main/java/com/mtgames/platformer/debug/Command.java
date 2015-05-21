package com.mtgames.platformer.debug;

import com.mtgames.platformer.Game;
import com.mtgames.platformer.entities.AutoScroll;
import com.mtgames.platformer.entities.Player;
import com.mtgames.platformer.entities.Properties;
import com.mtgames.platformer.entities.enemies.BaseEnemy;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.level.Level;

import java.util.Objects;

import com.mtgames.platformer.entities.FreeCamera;
import com.mtgames.utils.Debug;
import org.json.JSONObject;

public class Command {
	private static Level        level = null;
	private static Screen screen = null;

	public static void exec(String command) {
		String[] commands = command.split(" ");

		switch (commands[0].toLowerCase()) {
			default:
				Debug.log("'" + commands[0] + "' is not a valid command", Debug.WARNING);
				break;

			case "load":
				if (commands.length == 2) {
					level.path = "assets/levels/" + commands[1] + ".mp";
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
					Debug.log("Invalid arguments, usage: move <uid> <x> <y>", Debug.WARNING);
				}
				break;

			case "spawn":
				if (commands.length == 4 || commands.length == 5) {

					if (commands[2].equals("*")) {
						commands[2] = String.valueOf(level.entities.get(0).x);
					}

					if (commands[3].equals("*")) {
						commands[3] = String.valueOf(level.entities.get(0).y);
					}

					Properties properties;
					JSONObject obj = new JSONObject("{}");
					if (commands.length == 5) {
						obj = new JSONObject(commands[4]);
					}

					switch (commands[1]) {
						default:
							Debug.log("'" + commands[1] + "' is not a valid id", Debug.WARNING);
							break;

						case "player":
							properties = new Properties("player");
							properties.set(obj);
							level.addEntity(new Player(Integer.parseInt(commands[2]), Integer.parseInt(commands[3]), properties));
							break;

						case "baseEnemy":
							properties = new Properties("baseEnemy");
							properties.set(obj);
							level.addEntity(new BaseEnemy(Integer.parseInt(commands[2]), Integer.parseInt(commands[3]), properties));
							break;

						case "autoScroll":
							properties = new Properties("autoScroll");
							properties.set(obj);
							level.addEntity(new AutoScroll(Integer.parseInt(commands[2]), Integer.parseInt(commands[3]), properties));
							break;

						case "freeCamera":
							properties = new Properties("freeCamera");
							properties.set(obj);
							level.addEntity(new FreeCamera(Integer.parseInt(commands[2]), Integer.parseInt(commands[3]), properties));
							break;
					}
				} else {
					Debug.log("Invalid arguments, usage: spawn <name> <x> <y>", Debug.WARNING);
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
//				TODO: This needs to be fixed
				if (commands.length == 1) {
					Properties properties = level.entities.get(0).getProperties();
					if (level.entities.get(0) instanceof Player) {
						level.entities.set(0, new FreeCamera(level.entities.get(0).x, level.entities.get(0).y, properties));
					} else if (level.entities.get(0) instanceof FreeCamera) {
						level.entities.set(0, new Player(level.entities.get(0).x, level.entities.get(0).y, properties));
					} else {
						Debug.log("entity 0 is not a Player or a FreeCamera: " + level.entities.get(0).getClass(), Debug.ERROR);
					}
				} else {
					Debug.log("Invalid arguments, usage: freecam", Debug.WARNING);
				}
				break;

			case "lighting":
				if (commands.length == 1) {
					screen.lighting = !screen.lighting;
				} else {
					Debug.log("Invalid arguments, usage: lighting", Debug.WARNING);
				}
				break;

			case "debugl":
				Game.lightDebug = !Game.lightDebug;

			case "pause":
				Game.paused = !Game.paused;
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

	public static void set(Level level, Screen screen) {
		Command.level = level;
		Command.screen = screen;
	}
}
