package com.mtgames.platformer;

import com.mtgames.platformer.debug.Command;
import com.mtgames.platformer.editor.Editor;
import com.mtgames.utils.Debug;
import com.mtgames.utils.LauncherBase;
import javafx.collections.FXCollections;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;

public class Launcher extends LauncherBase {

	static private ComboBox<Integer> scale;
	static private CheckBox editor;
	static private CheckBox jython;

	public static void main(String[] args) {
		consoleEnabled = true;

		scale = new ComboBox<>(FXCollections.observableArrayList(1, 2));
		scale.setValue(2);

		editor = new CheckBox();
		if(Boolean.getBoolean("com.mtgames.editor")) {
			editor.setSelected(true);
		}

		jython = new CheckBox();
		if(Boolean.getBoolean("com.mtgames.jython")) {
			jython.setSelected(true);
		}

		addOption("Scale", scale);
		addOption("Editor", editor);
		addOption("Use external python scripts", jython);

		launch(args);
	}

	@Override protected void run() {
		System.setProperty("com.mtgames.scale", String.valueOf(scale.getValue()));
		System.setProperty("com.mtgames.jython", String.valueOf(jython.isSelected()));
		System.setProperty("org.lwjgl.librarypath", "native");
		System.setProperty("python.security.respectJavaAccessibility", "false");

		if (editor.isSelected()) {
			Debug.log("Launching editor", Debug.INFO);
			Editor.main(new String[] { "" });
		} else {
			Game.main(new String[] { "" });
		}
	}

	@Override protected void consoleCommand(String s) {
		Command.execute(s);
	}
}

