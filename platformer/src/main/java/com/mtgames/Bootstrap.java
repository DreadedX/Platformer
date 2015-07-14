package com.mtgames;

import com.mtgames.platformer.Launcher;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;

public class Bootstrap extends Application {
	public static void main(String[] args) {
		System.out.println("[INFO] Bootstrap(v1)");

		String[] files = new String[]{"libs/commons-compress-1.9.jar", "libs/json-20141113.jar", "libs/jython-standalone-2.7.0.jar", "libs/lwjgl.jar", "libs/utils.jar"};

		for (int i = 0; i < files.length; i++) {
			if (!new File(files[i]).exists()) {
				System.out.println("\u001b[31m[ERROR] Missing file: " + files[i] + "\u001b[0m");
				System.exit(0);
				return;
			}
		}

		Launcher.main(args);
	}

	@Override public void start(Stage stage) throws Exception {

	}
}
