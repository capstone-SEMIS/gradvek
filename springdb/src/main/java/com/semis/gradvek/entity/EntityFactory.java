package com.semis.gradvek.entity;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

public class EntityFactory {
	private static final Logger mLogger = Logger.getLogger (EntityFactory.class.getName ());


	public static <T extends Entity> T newEntity (Class<T> entityClass) {
		try {
			return entityClass.getConstructor ().newInstance ();
		} catch (NoSuchMethodException|InvocationTargetException|IllegalAccessException|InstantiationException nsmx) {
			mLogger.severe ("Couldn't create entity of type " + entityClass.getName ());
			return null;
		}
	}
}
