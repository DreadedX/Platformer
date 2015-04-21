package com.mtgames.platformer;

import com.mtgames.platformer.debug.Debug;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;

class Launcher {
	public static void main(String[] args) throws NoSuchFieldException {
		getNatives();

		System.setProperty("java.library.path", "natives");

		try {
			Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
			fieldSysPath.setAccessible(true);
			fieldSysPath.set(null, null);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		Game.main(args);
	}

	private static void getNatives() {
		String osName = System.getProperty("os.name");
		if (osName.contains(" ")) {
			osName = osName.substring(0, osName.indexOf(' '));
		}
		String osArch = System.getProperty("sun.arch.data.model");
		String libExt = libExt(osName);
		String libArch = libArch(osArch);

		Debug.log("Detected system: " + osName + "_" + osArch, Debug.LAUNCHER);

		File directory = new File("natives");
		if(!directory.exists()) {
			Debug.log("Creating natives folder", Debug.LAUNCHER);
			if (!directory.mkdir()) {
				Debug.log("Unable to create natives folder", Debug.ERROR);
			}
		}

		extract("natives/libaparapi_" + libArch + "." + libExt);

		directory.deleteOnExit();
	}

	private static void extract(String location) {
		File file = new File(location);
		if (!file.exists()) {
			Debug.log("Extracting: " + location, Debug.LAUNCHER);
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
				Debug.log("Unsupported OS: " + osName, Debug.ERROR);
				break;

			case "Linux":
				return "so";

			case "Mac":
				return "dylib";

			case "Windows":
				return "dll";
		}
		return "";
	}

	private static String libArch(String osArch) {
		switch (osArch) {
			default:
				Debug.log("Unsupported architecture: " + osArch, Debug.ERROR);
				break;

			case "64":
				return "x86_64";

			case "86":
				return "x86";
		}
		return "";
	}
}
