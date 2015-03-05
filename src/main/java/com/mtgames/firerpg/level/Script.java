package com.mtgames.firerpg.level;

import java.io.InputStreamReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.mtgames.firerpg.debug.Debug;

public class Script {
	
	private String						script;
	private ScriptEngineManager	manager	= new ScriptEngineManager();
	private ScriptEngine			engine	= manager.getEngineByName("JavaScript");
	
	public Script(String scriptPath) {
		if (scriptPath != null) {
			this.script = scriptPath;
			Debug.message(Debug.SCRIPT, "Loading: " + scriptPath);
			load();
		}
	}
	
	public void load() {
		try {
			engine.eval(new InputStreamReader(ClassLoader.getSystemResourceAsStream(script)));
			Debug.message(Debug.SCRIPT, "Loaded: " + script);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		
	}
	
	public Object get(String key) {
		Object value = engine.get(key);
		return value;
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
		Invocable invocable = (Invocable) engine;
		
		try {
			invocable.invokeFunction(function);
		} catch (NoSuchMethodException | ScriptException e) {
			e.printStackTrace();
		}
	}
	
}
