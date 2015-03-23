package com.mtgames.firerpg.debug;

import com.mtgames.firerpg.level.Level;

public class Command {
	public static void exec(String command, Level level) {
		String[] commands = command.split(" ");

		switch (commands[0]) {
			default:
				Debug.log(Debug.WARNING, "'" + commands[0] + "' is not a valid command");
				break;

			case "load":
				if (commands.length > 1) {
					level.path = "levels/" + commands[1] + ".map";
					level.reload = true;
				}
				break;

			case "move":
				if (commands.length > 1) {
					level.entities.get(Integer.parseInt(commands[1])).y = 0;
				}
				break;

			case "exit":
				System.exit(0);
				break;

			case "test":
				Debug.log(Debug.DEBUG, "Test");
				break;
		}
	}
}
