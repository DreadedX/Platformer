package com.mtgames.platformer.scripting;

import com.mtgames.utils.Debug;
import org.python.util.PythonInterpreter;

import java.io.InputStream;

public class Jython {
	private static final PythonInterpreter python = new PythonInterpreter();

	public static void run(String code) {
		python.exec(code);
	}

	public static void execute(String path) {
		if (!path.startsWith("/")) {
			path = "/" + path;
		}

		PythonInterpreter interpreter = new PythonInterpreter();

		if (ClassLoader.getSystemResource("python" + path) == null) {
			interpreter.execfile("platformer/python" + path);
		} else {
			if (Boolean.getBoolean("com.mtgames.jython")) {
				path = "src/main/resources/python" + path;
				interpreter.execfile(path);
			} else {
				InputStream s = ClassLoader.getSystemResourceAsStream("python" + path);
				if (s == null) {
					Debug.log(path + " does not exist!", Debug.ERROR);
				}
				interpreter.execfile(s);
			}
		}
	}
}
