package com.btr.proxy.selector.pac;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.btr.proxy.util.Logger;
import com.btr.proxy.util.Logger.LogLevel;

/****************************************************************************
 * Utility to check availablility of javax.script
 * 
 * @author Bernd Rosstauscher (proxyvole@rosstauscher.de) Copyright 2009
 ***************************************************************************/
abstract class ScriptAvailability {
	
	/*************************************************************************
	 * Checks whether javax.script is available or not.
	 * Completely done per Reflection to allow compilation under Java 1.5
	 * @return true if javax.script is available; false otherwise
	 ************************************************************************/
	public static boolean isJavaxScriptingAvailable() {
		Object engine = null;
		try {
			Class<?> managerClass = Class.forName("javax.script.ScriptEngineManager");
			Method m = managerClass.getMethod("getEngineByMimeType", String.class);
			engine = m.invoke(managerClass.newInstance(), "text/javascript");
		} catch (ClassNotFoundException e) {
			// javax.script not available
			Logger.log(ScriptAvailability.class, LogLevel.TRACE, "ClassNotFoundException", e);
		} catch (NoSuchMethodException e) {
			// javax.script not available
			Logger.log(ScriptAvailability.class, LogLevel.TRACE, "NoSuchMethodException", e);
		} catch (IllegalAccessException e) {
			// javax.script not available
			Logger.log(ScriptAvailability.class, LogLevel.TRACE, "IllegalAccessException", e);
		} catch (InvocationTargetException e) {
			// javax.script not available
			Logger.log(ScriptAvailability.class, LogLevel.TRACE, "InvocationTargetException", e);
		} catch (InstantiationException e) {
			// javax.script not available
			Logger.log(ScriptAvailability.class, LogLevel.TRACE, "InstantiationException", e);
		}

		return engine != null;
	}

	/*************************************************************************
	 * Constructor
	 ************************************************************************/
	
	ScriptAvailability() {
		super();
	}
}
