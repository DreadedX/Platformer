package com.mtgames.platformer.scripting;

import com.mtgames.utils.Debug;
import org.python.util.PythonInterpreter;

import java.io.InputStream;

public class JythonFactory {
	public static Object getJythonObject(String interfaceName,
			String pathToJythonModule){

//		pathToJythonModule = "platformer/python/" + pathToJythonModule;
		if (!pathToJythonModule.startsWith("/")) {
			pathToJythonModule = "/" + pathToJythonModule;
		}

		Object javaInt = null;
		PythonInterpreter interpreter = new PythonInterpreter();
		if (Boolean.getBoolean("com.mtgames.jython")) {
			pathToJythonModule = "src/main/resources/python" + pathToJythonModule;
			interpreter.execfile(pathToJythonModule);
		} else {
			InputStream s = ClassLoader.getSystemResourceAsStream("python" + pathToJythonModule);
			if (s == null) {
				Debug.log(pathToJythonModule + " does not exist!", Debug.ERROR);
			}
			interpreter.execfile(s);
		}
		String tempName = pathToJythonModule.substring(pathToJythonModule.lastIndexOf("/")+1);
		tempName = tempName.substring(0, tempName.indexOf("."));
//		System.out.println(tempName);
		String instanceName = tempName.toLowerCase();
		String javaClassName = tempName.substring(0,1).toUpperCase() +
				tempName.substring(1);
		String objectDef = "=" + javaClassName + "()";
		interpreter.exec(instanceName + objectDef);
		try {
			Class JavaInterface = Class.forName(interfaceName);
			javaInt =
					interpreter.get(instanceName).__tojava__(JavaInterface);
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();  // Add logging here
		}

		return javaInt;
	}
}
