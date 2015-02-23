package com.mtgames.firerpg.level;

import java.io.InputStreamReader;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.sun.org.apache.bcel.internal.util.ClassLoader;

public class ScriptLoader {
	private ScriptEngine engine;
	
	public ScriptLoader(String script) {
		ScriptEngineManager manager = new ScriptEngineManager();
		engine = manager.getEngineByName("nashorn");

		try {
			engine.eval(new InputStreamReader(ClassLoader.getSystemResourceAsStream(script)));
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}
	
	public Object get(String object) {
		Object value = engine.get(object);
		return value;
	}
	
}
