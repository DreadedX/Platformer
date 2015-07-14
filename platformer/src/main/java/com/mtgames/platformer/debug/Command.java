package com.mtgames.platformer.debug;

import com.mtgames.platformer.Game;
import com.mtgames.platformer.entities.AdvancedEntity;
import com.mtgames.platformer.entities.Entity;
import com.mtgames.platformer.level.tiles.Tile;
import com.mtgames.platformer.scripting.Jython;
import com.mtgames.platformer.settings.Properties;
import com.mtgames.platformer.entities.particles.DashParticle;
import com.mtgames.platformer.entities.particles.Torch;
import com.mtgames.platformer.gfx.Screen;
import com.mtgames.platformer.level.Level;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.mtgames.utils.Debug;
import org.json.JSONObject;

public class Command {
	private static final List<Runnable> queue = new ArrayList<>();

	public static void execute() {
		for (int i = 0; i < queue.size(); i++) {
			queue.get(i).run();
		}
		queue.clear();
	}

	public static void queue(String command) {
		String[] commands2 = command.split(" & ");
		if (commands2.length > 1) {
			for (int i = 0; i < commands2.length; i++) {
				queue(commands2[i]);
			}
			return;
		}
		queue.add(() -> {
			String[] commands = command.split(" ");

			switch (commands[0].toLowerCase()) {
				default:
					Debug.log("'" + commands[0] + "' is not a valid command", Debug.WARNING);
					break;

				case "load":
					if (commands.length == 2) {
						Level.path = "levels/" + commands[1] + ".jsp";
						Level.reload = true;
					} else {
						Debug.log("Invalid arguments, usage: load <level name>", Debug.WARNING);
					}
					break;

				case "reload":
					if (commands.length == 1) {
						Level.reload = true;
					} else {
						Debug.log("Invalid arguments, usage: load", Debug.WARNING);
					}
					break;

				case "move":
					if (commands.length == 4) {
						if (Objects.equals(commands[1], "*")) {
							for (int i = 0; i < Level.entities.size(); i++) {
								Level.entities.get(i).x = Integer.parseInt(commands[2]);
								Level.entities.get(i).y = Integer.parseInt(commands[3]);
							}
						} else if (Integer.parseInt(commands[1]) < Level.entities.size()) {
							Level.entities.get(Integer.parseInt(commands[1])).x = Integer.parseInt(commands[2]);
							Level.entities.get(Integer.parseInt(commands[1])).y = Integer.parseInt(commands[3]);
						} else {
							Debug.log("'" + commands[1] + "' is not a valid id", Debug.WARNING);
						}

					} else {
						Debug.log("Invalid arguments, usage: move <uid> <x> <y>", Debug.WARNING);
					}
					break;

				case "spawn":
					if (commands.length >= 4) {

						if (commands[2].equals("~")) {
							commands[2] = String.valueOf(Level.entities.get(0).x);
						}

						if (commands[3].equals("~")) {
							commands[3] = String.valueOf(Level.entities.get(0).y);
						}

						Properties properties;
						JSONObject obj = new JSONObject("{}");
						if (commands.length >= 5) {
							String objRaw = "";
							for (int i = 4; i < commands.length; i++) {
								objRaw += commands[i] + " ";
							}
							obj = new JSONObject(objRaw);
						}

						//					TODO: Add check to see if the entity type is valid
						String type = commands[1];
						properties = new Properties(type.split("\\.")[0]);
						properties.set(obj);
						Level.addEntity(
								new AdvancedEntity(properties, Integer.parseInt(commands[2]), Integer.parseInt(commands[3]), type));
					} else {
						Debug.log("Invalid arguments, usage: spawn <name> <x> <y>", Debug.WARNING);
					}
					break;

				case "light":
					if (commands.length == 4 || commands.length == 5) {

						if (commands[2].equals("~")) {
							commands[2] = String.valueOf(Level.entities.get(0).x);
						}

						if (commands[3].equals("~")) {
							commands[3] = String.valueOf(Level.entities.get(0).y);
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
								Level.addEntity(new Torch(Integer.parseInt(commands[2]), Integer.parseInt(commands[3]), properties));
								break;

							case "dashParticle":
								properties = new Properties("dashParticle");
								properties.set(obj);
								Level.addEntity(new DashParticle(Integer.parseInt(commands[2]), Integer.parseInt(commands[3]), properties));
								break;

						}
					} else {
						Debug.log("Invalid arguments, usage: light <name> <x> <y>", Debug.WARNING);
					}
					break;

				case "kill":
					if (commands.length == 2) {
						if (Integer.parseInt(commands[1]) < Level.entities.size()) {
							Level.entities.remove(Integer.parseInt(commands[1]));
						} else {
							Debug.log("'" + commands[1] + "' is not a valid id", Debug.WARNING);
						}
					} else {
						Debug.log("Invalid arguments, usage: kill <uid>", Debug.WARNING);
					}
					break;

				case "lighting":
					if (commands.length == 1) {
						Screen.lighting = !Screen.lighting;
					} else {
						Debug.log("Invalid arguments, usage: lighting", Debug.WARNING);
					}
					break;

				case "hitbox":
					if (commands.length == 1) {
						Screen.hitBox = !Screen.hitBox;
					} else {
						Debug.log("Invalid arguments, usage: hitbox", Debug.WARNING);
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
						Level.tiles[Integer.parseInt(commands[2])][Integer.parseInt(commands[3])] = Byte.parseByte((commands[1]));
					}
					break;

				case "export":
					String export = "{\"tileId\":{";

					for (int i = 2; i < Tile.tiles.length; i++) {
						if (Tile.tiles[i] == null) {
							break;
						}
						export += "\"" + Tile.tiles[i].getId() + "\":\"" + Tile.tiles[i].getName() + "\",";
					}

					export = export.substring(0, export.length()-1);

					export += "},\"width\":64,\"height\":48,\"tiles\":{";
					for(int x = 0; x < Level.tiles.length; x++) {
						for (int y = 0; y < Level.tiles[0].length; y++) {
							if (Level.tiles[x][y] != 1) {
								export += "\"" + x + "." + y + "\":" + Level.tiles[x][y] + ",";
							}
						}
					}
					export = export.substring(0, export.length()-1);
					export += "},\"tiles0\":{";
					for(int x = 0; x < Level.tiles0.length; x++) {
						for (int y = 0; y < Level.tiles0[0].length; y++) {
							if (Level.tiles0[x][y] != 1) {
								export += "\"" + x + "." + y + "\":" + Level.tiles0[x][y] + ",";
							}
						}
					}
					export = export.substring(0, export.length()-1);
					export += "}}";
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
						Level.create(Integer.parseInt(commands[1]), Integer.parseInt(commands[2]));
					}
					break;

				case "py":
					Jython.run(command.substring(3));
					break;

				case "freecam":
					Entity eCam = Level.entities.get(0);
					Properties propCam = eCam.getProperties();
					Level.entities.set(0, new AdvancedEntity(propCam, eCam.x, eCam.y, "freeCamera"));
					break;

				case "god":
					Entity eGod = Level.entities.get(0);
					Properties propGod = eGod.getProperties();
					Level.entities.set(0, new AdvancedEntity(propGod, eGod.x, eGod.y, "player.god"));
					break;

			}
		});
	}
}
