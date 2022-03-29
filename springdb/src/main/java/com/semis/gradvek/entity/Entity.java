package com.semis.gradvek.entity;

import java.util.List;

import org.apache.parquet.example.data.Group;

/**
 * The base class for OpenTarget entities
 * @author ymachkasov
 *
 */
public abstract class Entity {
	
	/**
	 * The Cypher command to add this entity to the database
	 * @return the string representation of the command
	 */
	public abstract List<String> addCommands ();
	
	/**
	 * Optional filter which indicates if this entity should be imported
	 * @param data the Parquet data for this entity
	 * @return if returns true, the entity is included for import
	 */
	public boolean filter (Group data) {
		return (true);
	}
	
	public abstract EntityType getType ();
	
}
