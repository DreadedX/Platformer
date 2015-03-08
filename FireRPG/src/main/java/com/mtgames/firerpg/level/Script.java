package com.mtgames.firerpg.level;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.mtgames.firerpg.debug.Debug;

public class Script {
	
	private String						script;
	private final ScriptEngineManager	manager	= new ScriptEngineManager();
	private final ScriptEngine			engine	= manager.getEngineByName("JavaScript");

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
		Invocable invocable = (Invocable) engine;
		
		try {
			invocable.invokeFunction(function);
		} catch (NoSuchMethodException | ScriptException e) {
			e.printStackTrace();
		}

        cheat(function, new String[]{"Godmode.js"});

    }

    public void cheat(String function, String[] cheats) {
        for (int i = 0; i < cheats.length; i++) {
            try {
                engine.eval(new InputStreamReader(new FileInputStream("cheats/" + cheats[i])));
            } catch (ScriptException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            String execScript = (String) get("execScript");
            String execFunction = (String) get("execFunction");

//            Debug.msg(Debug.DEBUG, "scripts/" + execScript);
//            Debug.msg(Debug.DEBUG, script);
            if (execFunction == function && execScript == script) {
                invoke("cheat");
            }
        }
    }
}
