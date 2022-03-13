package com.semis.gradvek.entity;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import org.apache.parquet.example.data.simple.SimpleGroup;

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


	public static <T extends Entity> T fromParquet (Class<T> entityClass, SimpleGroup parquet) {
		try {
			return entityClass.getConstructor (SimpleGroup.class).newInstance (parquet);
		} catch (NoSuchMethodException|InvocationTargetException|IllegalAccessException|InstantiationException nsmx) {
			mLogger.severe ("Couldn't create entity of type " + entityClass.getName () + " with Parquet data");
			return null;
		}
	}
}
