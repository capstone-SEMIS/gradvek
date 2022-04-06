package com.semis.gradvek.entity;

import java.lang.reflect.Field;
import java.util.List;

import javax.management.RuntimeErrorException;

import org.apache.parquet.example.data.Group;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * The base class for OpenTarget entities
 * 
 * @author ymachkasov
 *
 */
public abstract class Entity {

	private String mFromDataset = null;
	
	public String getDataset () {
		return (mFromDataset != null ? mFromDataset : getType ().toString ());
	}

	public void setDataset (String dataset) {
		mFromDataset = dataset;
	}

	/**
	 * The Cypher command to add this entity to the database
	 * 
	 * @return the string representation of the command
	 */
	public abstract List<String> addCommands ();

	/**
	 * Optional filter which indicates if this entity should be imported
	 * 
	 * @param data the Parquet data for this entity
	 * @return if returns true, the entity is included for import
	 */
	public boolean filter (Group data) {
		return (true);
	}

	public abstract EntityType getType ();

	public static final Gson mGson = new GsonBuilder ()
		.setFieldNamingStrategy (new FieldNamingStrategy () {
			@Override
			public String translateName (Field f) {
				String name = f.getName ();
				if (name.startsWith ("m")) { // remove the prefix and decamelize
					name = Character.toLowerCase (name.charAt (1)) + name.substring (2);
				}
	
				return (name);
			}
		}).create ();

	public String toJson () {
		return (mGson.toJson (this));
	}
	
	public static <T> T fromJson (String json, Class<T> entityClass) {
		return (mGson.fromJson (json, entityClass));
	}
	
	public String getId () {
		return (null);
	}

}
