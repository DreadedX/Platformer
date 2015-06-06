package com.mtgames.platformer;

import com.mtgames.platformer.debug.Command;
import com.mtgames.platformer.editor.Editor;
import com.mtgames.utils.Debug;
import com.mtgames.utils.LauncherBase;
import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

class Launcher extends LauncherBase {

	static private ComboBox<Integer> scale;
	static private CheckBox editor;

	public static void main(String[] args) {
		consoleEnabled = true;

		scale = new ComboBox<>(FXCollections.observableArrayList(1, 2));
		scale.setValue(2);

		editor = new CheckBox();
		if(Boolean.getBoolean("com.mtgames.editor")) {
			editor.setSelected(true);
		}

		addOption("Scale", scale);
		addOption("Editor", editor);

		launch(args);
	}

	@Override protected void run() {
		System.setProperty("com.mtgames.scale", String.valueOf(scale.getValue()));
		System.setProperty("org.lwjgl.librarypath", "native");

		if (editor.isSelected()) {
			Debug.log("Launching editor", Debug.INFO);
			Editor.main(new String[] { "" });
		} else {
			Game.main(new String[] { "" });
		}
	}

	@Override protected void consoleCommand(String s) {
		Command.exec(s);
	}
}

