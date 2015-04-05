package com.mtgames.platformer.debug;

public class Debug {
	public final static  int INFO     = 0;
	public final static  int LEVEL    = 1;
	public final static  int SCRIPT   = 2;
	private final static int DEBUG    = 3;
	public final static  int WARNING  = 4;
	public final static  int ERROR  = 5;
	public static        int priority = WARNING;

	public static boolean debug = false;

	public static void log(String message) {
		log(message, DEBUG);
	}

	public static void log(String message, int type) {
		if (type < priority) {
			return;
		}

		switch (type) {
			case 0:
				System.out.println("[INFO] " + message);
				break;

			case 1:
				System.out.println("[LEVEL] " + message);
				break;

			case 2:
				System.out.println("[SCRIPT] " + message);
				break;

			case 3:
				System.out.println("[DEBUG] " + message);
				break;

			case 4:
				System.out.println("\u001B[33m[WARNING] " + message +"\u001B[0m" );
				break;

			case 5:
				System.out.println("\u001b[31m[ERROR] " + message + "\u001b[0m");
				Command.exec("exit 1");
				break;
		}
	}
}
