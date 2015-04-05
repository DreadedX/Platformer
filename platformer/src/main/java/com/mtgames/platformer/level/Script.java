package com.mtgames.platformer.level;

import com.mtgames.platformer.debug.Debug;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.util.Objects;

public class Script {

	private final ScriptEngineManager manager = new ScriptEngineManager();
	private final ScriptEngine        engine  = manager.getEngineByName("JavaScript");
	private String script;

	public Script(String scriptPath) {
		if (scriptPath != null) {
			this.script = scriptPath;
			Debug.log("Loading: " + script, Debug.SCRIPT);
			load();
		}
	}

	public void load() {
		try {
			engine.eval(new InputStreamReader(ClassLoader.getSystemResourceAsStream(script)));
			Debug.log("Loaded: " + script, Debug.SCRIPT);
		} catch (ScriptException e) {
			e.printStackTrace();
		}

	}

	public Object get(String key) {
		return engine.get(key);
	}

	public void set(String key, Object value) {
		engine.put(key, value);
	}

	public void doInit() {
		invoke("init");
	}

	public void doTick() {
		invoke("tick");
	}

	public void invoke(String function) {
		if (new File("platformer/cheats").exists()) {
			File f = new File("platformer/cheats");

			FilenameFilter textFilter = (dir, name) -> name.toLowerCase().endsWith(".js");

			File[] files = f.listFiles(textFilter);
			for (File file : files) {
				cheat(function, file.getName());
			}

		}

		Invocable invocable = (Invocable) engine;

		try {
			invocable.invokeFunction(function);
		} catch (NoSuchMethodException | ScriptException e) {
			e.printStackTrace();
		}
	}

	private void cheat(String function, String cheat) {
		try {
			engine.eval(new InputStreamReader(new FileInputStream("platformer/cheats/" + cheat)));
		} catch (ScriptException | FileNotFoundException e) {
			e.printStackTrace();
		}

		String execScript = (String) get("execScript");
		String execFunction = (String) get("execFunction");

		if (Objects.equals(execFunction, function) && Objects.equals(execScript, script)) {
			invoke(cheat.substring(0, cheat.lastIndexOf('.')).toLowerCase());
		}
	}
}
