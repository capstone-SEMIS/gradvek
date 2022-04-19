package com.semis.gradvek.graphdb;

import com.semis.gradvek.csv.CsvFile;
import com.semis.gradvek.cytoscape.CytoscapeEntity;
import com.semis.gradvek.entity.Dataset;
import com.semis.gradvek.entity.Entity;
import com.semis.gradvek.entity.EntityType;
import com.semis.gradvek.springdb.AdverseEventIntObj;

import java.util.List;
import java.util.Map;

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
	void add (List<Entity> entities, boolean canCombine);

	/**
	 * Clears the database
	 */
	void clear ();

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
	
//	public List<AdverseEventIntObj> getAEByTarget (String target);

	public void loadCsv(String url, CsvFile csvFile);

	public List<Dataset> getDatasets ();

	public void enableDataset (String dataset, boolean enable);
	
	public String getUri();

	List<CytoscapeEntity> getAEPathByTarget(String target);

	List<AdverseEventIntObj> getAEByTarget(String target);

	List<Map> getWeightsByDrug(String target, String ae);

  List<Map> getTargetSuggestions(String hint);
  
}