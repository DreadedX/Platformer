package com.mtgames.firerpg.debug;

public class Debug {
	public final static  int INFO    = 0;
	public final static  int LEVEL   = 1;
	public final static  int SCRIPT  = 2;
	public final static  int DEBUG   = 3;
	private final static int WARNING = 4;
	public final static  int ERROR   = 5;
	public static int priority = WARNING;

	public static void log(int type, String message) {
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
				System.out.println("[WARNING] " + message);
				break;

			case 5:
				System.out.println("[ERROR] " + message);
				break;
		}
	}
}
