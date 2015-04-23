package com.mtgames;

import com.mtgames.platformer.Game;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.Objects;

class Launcher {
	public static void main(String[] args) throws IOException, NoSuchFieldException {
		getNatives();

		System.setProperty("java.library.path", "natives");
		if (args.length > 1) {
			if (Objects.equals(args[1], "debug")) {
				System.setProperty("com.amd.aparapi.enableShowGeneratedOpenCL", "true");
			}
		}

		try {
			Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		System.out.println("[LAUNCHER] Starting: " + Game.NAME);
		Game.main(args);
	}

	private static void getNatives() {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains(" ")) {
			osName = osName.substring(0, osName.indexOf(' '));
		}
		String osArch = System.getProperty("sun.arch.data.model");
		String libExt = libExt(osName);
		String libArch = libArch(osArch);

		System.out.println("[LAUNCHER] Detected system: " + osName + "-" + osArch);

		File directory = new File("natives");
		if(!directory.exists()) {
			System.out.println("[LAUNCHER] Creating natives folder");
			if (!directory.mkdir()) {
				System.out.println("\u001b[31m[ERROR] Unable to create natives folder\u001b[0m");
				System.exit(0);
			}
		}

		extract("natives/libaparapi_" + libArch + "." + libExt);

		directory.deleteOnExit();
	}

	private static void extract(String location) {
		File file = new File(location);
		if (!file.exists()) {
			System.out.println("[LAUNCHER] Extracting: " + location);
			InputStream link = (ClassLoader.getSystemResourceAsStream(location));
			try {
				Files.copy(link, file.getAbsoluteFile().toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		file.deleteOnExit();
	}

	private static String libExt(String osName) {
		switch (osName) {
			default:
				System.out.println("\u001b[31m[ERROR] Unsupported OS: " + osName + "\u001b[0m");
				System.exit(0);
				break;

			case "linux":
				return "so";

			case "mac":
				return "dylib";

			case "windows":
				return "dll";
		}
		return "";
	}

	private static String libArch(String osArch) {
		switch (osArch) {
			default:
				System.out.println("\u001b[31m[ERRORR] Unsupported architecture: " + osArch + "\u001b[0m");
				System.exit(0);
				break;

			case "64":
				return "x86_64";

			case "32":
				return "x86";
		}
		return "";
	}
}
