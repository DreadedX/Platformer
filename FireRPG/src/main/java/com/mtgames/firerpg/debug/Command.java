package com.mtgames.firerpg.debug;

import com.mtgames.firerpg.InputHandler;
//import com.mtgames.firerpg.entities.FreeCamera;
import com.mtgames.firerpg.entities.Player;
import com.mtgames.firerpg.entities.enemies.BasicEnemy;
import com.mtgames.firerpg.level.Level;

import java.util.Objects;

public class Command {
	private static Level level = null;
	private static InputHandler input = null;

	public static void exec(String command) {
		String[] commands = command.split(" ");

		switch (commands[0]) {
			default:
				Debug.log(Debug.WARNING, "'" + commands[0] + "' is not a valid command");
				break;

			case "load":
				if (commands.length == 2) {
					level.path = "levels/" + commands[1] + ".map";
					level.reload = true;
				}
				break;

			case "move":
				if (commands.length == 4) {
					if (Objects.equals(commands[1], "*")) {
						for (int i = 0; i < level.entities.size(); i++) {
							level.entities.get(i).x = Integer.parseInt(commands[2]);
							level.entities.get(i).y = Integer.parseInt(commands[3]);
						}
					} else {
						level.entities.get(Integer.parseInt(commands[1])).x = Integer.parseInt(commands[2]);
						level.entities.get(Integer.parseInt(commands[1])).y = Integer.parseInt(commands[3]);
					}
				}
				break;

			case "spawn":
				if (commands.length == 4) {
					switch (Integer.parseInt(commands[1])) {
						case 0:
							level.addEntity(new Player(level, Integer.parseInt(commands[2]), Integer.parseInt(commands[3]), input));
							break;

						case 1:
							level.addEntity(new BasicEnemy(level, Integer.parseInt(commands[2]), Integer.parseInt(commands[3])));
							break;

//						TODO: FIX CAMERA BEFORE ENABLING THIS
//						case 99:
//							level.addEntity(new FreeCamera(level, Integer.parseInt(commands[2]), Integer.parseInt(commands[3]), input));
//							break;
					}
				}
				break;

			case "kill":
				if (commands.length == 2) {
					level.entities.remove(Integer.parseInt(commands[1]));
				}
				break;

			case "exit":
				System.exit(0);
				break;

		}
	}

	public static void set(Level level, InputHandler input) {
		Command.level = level;
		Command.input = input;
	}
}
