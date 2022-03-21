package com.semis.gradvek.entity;

import org.apache.parquet.example.data.simple.SimpleGroup;

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
	public abstract String addCommand ();
	
	/**
	 * Optional filter which indicates if this entity should be imported
	 * @param data the Parquet data for this entity
	 * @return if returns true, the entity is included for import
	 */
	public boolean filter (SimpleGroup data) {
		return (true);
	}
	
	/**
	 * Indicates whether entities of this type can be created in batch mode
	 * (that is, they are not dependent on uniqueness and other Cypher variables)
	 * @return if returns true, the entity is batchable
	 */
	public boolean canCombine () {
		return (true);
	}
	
}
