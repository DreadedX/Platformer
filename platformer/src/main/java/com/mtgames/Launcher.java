package com.mtgames;

import com.mtgames.platformer.Game;
import com.mtgames.utils.LauncherBase;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;

public class Launcher extends LauncherBase {

	static private ComboBox<Integer> scale;

	public static void main(String[] args) {
		scale = new ComboBox<>(FXCollections.observableArrayList(1, 2));
		scale.setValue(2);

		addOption("Scale", scale);

		launch(args);
	}

	@Override protected void run() {
		System.setProperty("com.mtgames.scale", String.valueOf(scale.getValue()));

		Game.main(new String[]{""});
	}
}

