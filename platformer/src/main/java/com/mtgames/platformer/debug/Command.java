package com.mtgames.platformer.debug;

import com.mtgames.platformer.Game;
import com.mtgames.platformer.entities.AdvancedEntity;
import com.mtgames.platformer.scripting.Jython;
import com.mtgames.platformer.settings.Properties;
import com.mtgames.platformer.entities.particles.DashParticle;
import com.mtgames.platformer.entities.particles.Torch;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.level.Level;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.mtgames.utils.Debug;
import org.json.JSONObject;

public class Command {
	private static Level  level  = null;
	private static Screen screen = null;

	private static List<Runnable> queue = new ArrayList<>();

	public static void execute() {
		for (int i = 0; i < queue.size(); i++) {
			queue.get(i).run();
		}
		queue.clear();
	}

	public static void queue(String command) {
		queue.add(() -> {
			String[] commands = command.split(" ");

			switch (commands[0].toLowerCase()) {
				default:
					Debug.log("'" + commands[0] + "' is not a valid command", Debug.WARNING);
					break;

				case "load":
					if (commands.length == 2) {
						level.path = "assets/levels/" + commands[1] + ".jsp";
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

						if (commands[2].equals("~")) {
							commands[2] = String.valueOf(level.entities.get(0).x);
						}

						if (commands[3].equals("~")) {
							commands[3] = String.valueOf(level.entities.get(0).y);
						}

						Properties properties;
						JSONObject obj = new JSONObject("{}");
						if (commands.length == 5) {
							obj = new JSONObject(commands[4]);
						}

						//					TODO: Add check to see if the entity type is valid
						String type = commands[1];
						properties = new Properties(type.split("\\.")[0]);
						properties.set(obj);
						level.addEntity(new AdvancedEntity(properties, Integer.parseInt(commands[2]), Integer.parseInt(commands[3]),
								"entities/" + Character.toUpperCase(type.charAt(0)) + type.substring(1) + ".py"));
					} else {
						Debug.log("Invalid arguments, usage: spawn <name> <x> <y>", Debug.WARNING);
					}
					break;

				case "light":
					if (commands.length == 4 || commands.length == 5) {

						if (commands[2].equals("~")) {
							commands[2] = String.valueOf(level.entities.get(0).x);
						}

						if (commands[3].equals("~")) {
							commands[3] = String.valueOf(level.entities.get(0).y);
						}

						Properties properties;
						JSONObject obj = new JSONObject("{}");
						if (commands.length == 5) {
							obj = new JSONObject(commands[4]);
						}

						switch (commands[1]) {
							default:
								Debug.log("'" + commands[1] + "' is not a valid light type", Debug.WARNING);
								break;

							case "torch":
								properties = new Properties("torch");
								properties.set(obj);
								level.addEntity(new Torch(Integer.parseInt(commands[2]), Integer.parseInt(commands[3]), properties));
								break;

							case "dashParticle":
								properties = new Properties("dashParticle");
								properties.set(obj);
								level.addEntity(new DashParticle(Integer.parseInt(commands[2]), Integer.parseInt(commands[3]), properties));
								break;

						}
					} else {
						Debug.log("Invalid arguments, usage: light <name> <x> <y>", Debug.WARNING);
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

				//			case "freecam":
				//				if (commands.length == 1) {
				//					Properties properties = level.entities.get(0).getProperties();
				//					if (level.entities.get(0) instanceof Player) {
				//						level.entities.set(0, new FreeCamera(level.entities.get(0).x, level.entities.get(0).y, properties));
				//					} else if (level.entities.get(0) instanceof FreeCamera) {
				//						level.entities.set(0, new Player(level.entities.get(0).x, level.entities.get(0).y, properties));
				//					} else {
				//						Debug.log("entity 0 is not a Player or a FreeCamera: " + level.entities.get(0).getClass(), Debug.ERROR);
				//					}
				//				} else {
				//					Debug.log("Invalid arguments, usage: freecam", Debug.WARNING);
				//				}
				//				break;

				case "lighting":
					if (commands.length == 1) {
						screen.lighting = !screen.lighting;
					} else {
						Debug.log("Invalid arguments, usage: lighting", Debug.WARNING);
					}
					break;

				//			TODO: These commands are not finished

				case "debugl":
					Game.lightDebug = !Game.lightDebug;

				case "pause":
					Game.paused = !Game.paused;
					break;

				case "exit":
					if (commands.length == 2) {
						System.exit(Integer.parseInt(commands[1]));
					}
					System.exit(0);
					break;

				case "tile":
					if (commands.length == 4) {
						level.tiles[Integer.parseInt(commands[2]) + Integer.parseInt(commands[3]) * level.width] = Byte.parseByte((commands[1]));
					}
					break;

				case "export":
					String export =
							"{\"width\": 64, \"height\": 48, \"tiles\": " + Arrays.toString(level.tiles) + ", \"tiles0\":" + Arrays.toString(level.tiles0) + "}";
					Debug.log(export, Debug.INFO);
					try {
						PrintWriter out = new PrintWriter("pack/export/tiles.json");
						out.println(export);
						out.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					break;

				case "new":
					if (commands.length == 3) {
						level.create(Integer.parseInt(commands[1]), Integer.parseInt(commands[2]));
					}
					break;

				case "py":
					Jython.run(command.substring(3));

			}
		});
	}

	public static void set(Level level, Screen screen) {
		Command.level = level;
		Command.screen = screen;
	}
}
