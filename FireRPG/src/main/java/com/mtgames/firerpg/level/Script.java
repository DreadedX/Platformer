package com.mtgames.firerpg.level;

import com.mtgames.firerpg.debug.Debug;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Objects;

public class Script {

	private final ScriptEngineManager manager = new ScriptEngineManager();
	private final ScriptEngine        engine  = manager.getEngineByName("JavaScript");
	private String script;

	public Script(String scriptPath) {
		if (scriptPath != null) {
			this.script = scriptPath;
			Debug.msg(Debug.SCRIPT, "Loading: " + script);
			load();
		}
	}

	public void load() {
		try {
			engine.eval(new InputStreamReader(ClassLoader.getSystemResourceAsStream(script)));
			Debug.msg(Debug.SCRIPT, "Loaded: " + script);
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
        if (new File("cheats").exists()) {
            cheat(function, new String[] { "Godmode.js" });
        }

		Invocable invocable = (Invocable) engine;

		try {
			invocable.invokeFunction(function);
		} catch (NoSuchMethodException | ScriptException e) {
			e.printStackTrace();
		}
	}

	void cheat(String function, String[] cheats) {
		for (String cheat : cheats) {
			try {
				engine.eval(new InputStreamReader(new FileInputStream("cheats/" + cheat)));
			} catch (ScriptException | FileNotFoundException e) {
				e.printStackTrace();
			}

			String execScript = (String) get("execScript");
			String execFunction = (String) get("execFunction");

			//            Debug.msg(Debug.DEBUG, "scripts/" + execScript);
			//            Debug.msg(Debug.DEBUG, script);
			if (Objects.equals(execFunction, function) && Objects.equals(execScript, script)) {
				invoke("cheat");
			}
		}
	}
}
