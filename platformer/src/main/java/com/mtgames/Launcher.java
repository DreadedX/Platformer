package com.mtgames;

import com.mtgames.platformer.Game;
import com.mtgames.utils.LauncherBase;
import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

import java.io.IOException;

public class Launcher extends LauncherBase {

	static private ComboBox<Integer> scale;
	static private ComboBox<String> aparapi;
	static private CheckBox showCL;

	public static void main(String[] args) throws IOException, NoSuchFieldException {
		scale = new ComboBox<>(FXCollections.observableArrayList(1, 2));
		aparapi = new ComboBox<>(FXCollections.observableArrayList("GPU", "CPU", "JTP"));
		showCL = new CheckBox();

		scale.setValue(2);
		aparapi.setValue("GPU");

		addOption("Scale", scale);
		addOption("Aparapi execution mode", aparapi);
		addOption("Show generated OpenCL code", showCL);

		launch(args);
	}

	@Override protected void run() {
		System.setProperty("com.mtgames.scale", String.valueOf(scale.getValue()));
		System.setProperty("com.amd.aparapi.executionMode", (aparapi.getValue()));
		System.setProperty("com.amd.aparapi.enableShowGeneratedOpenCL", String.valueOf(showCL.isSelected()));

		Game.main(new String[] { "" });
	}
}
