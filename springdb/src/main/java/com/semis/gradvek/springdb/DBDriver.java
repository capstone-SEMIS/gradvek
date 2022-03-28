package com.semis.gradvek.springdb;

import java.util.Set;
import java.util.List;

import com.semis.gradvek.entity.AdverseEvent;
import com.semis.gradvek.entity.Entity;
import com.semis.gradvek.entity.EntityType;

public interface DBDriver {

	/**
	 * Performs the command to add this entity to the database
	 * @param entity
	 */
	<T extends Entity> void add (T entity);

	/**
	 * Performs the command to add this list of entities to the database
	 * @param entity
	 */
	<T extends Entity> void add (Set<T> entities, boolean canCombine);

	/**
	 * Clears the database
	 */
	void clear ();

	/**
	 * Executes the command in write mode
	 * @param command
	 */
	void write (String command);

	/**
	 * Counts the entities of the given type
	 * @param type the entity type for the query
	 * @return the number of entities of this type in the database
	 */
	int count (EntityType type);

	/**
	 * Creates an index on entries of the specified type, if this type supports indexing
	 * and the index does not yet exist
	 * @param type the type of the entities to index
	 */
	void index (EntityType type);
	
	/**
	 * Prunes duplicate entries of the specified type, if this type supports indexing
	 * @param type the type of the entities to prune
	 */
	void unique (EntityType type);
	
	public List<AdverseEventIntObj> getAEByTarget (String target);

}