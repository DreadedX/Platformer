package com.mtgames.firerpg.level;

import java.io.InputStreamReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.sun.org.apache.bcel.internal.util.ClassLoader;

public class ScriptLoader {
	
	private String script;
	
	private ScriptEngine	engine;
	
	public ScriptLoader(String script) {
		this.script = script;
		ScriptEngineManager manager = new ScriptEngineManager();
		engine = manager.getEngineByName("JavaScript");
		
		load();
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
	
	public void invoke(String function) {
		Invocable invocable = (Invocable) engine;
		
		try {
			invocable.invokeFunction(function);
		} catch (NoSuchMethodException | ScriptException e) {
			e.printStackTrace();
		}
	}
	
}
