package com.mtgames.firerpg.level;

import java.io.InputStreamReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptLoader {
	
	private String						script;
	private static ScriptEngineManager	manager	= new ScriptEngineManager();
	private static ScriptEngine			engine	= manager.getEngineByName("JavaScript");
	
	public ScriptLoader(String scriptPath) {
		if (scriptPath != null) {
			this.script = scriptPath;
			System.out.println("Loading: " + scriptPath);
			load();
		}
	}
	
	public void load() {
		try {
			engine.eval(new InputStreamReader(ClassLoader.getSystemResourceAsStream(script)));
			System.out.println("Loaded: " + script);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		
	}
	
	public Object get(String object) {
		Object value = engine.get(object);
		return value;
	}
	
	public void init() {
		invoke("init");
	}
	
	public void tick() {
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
