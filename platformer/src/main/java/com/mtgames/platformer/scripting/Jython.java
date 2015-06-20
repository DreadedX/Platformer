package com.mtgames.platformer.scripting;

import org.python.util.PythonInterpreter;

public class Jython {
	private static PythonInterpreter python = new PythonInterpreter();

	public static void run(String code) {
		python.exec(code);
	}
}
