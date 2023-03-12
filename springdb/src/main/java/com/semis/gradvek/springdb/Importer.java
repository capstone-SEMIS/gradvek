package com.semis.gradvek.springdb;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.semis.gradvek.entity.Entity;
import com.semis.gradvek.entity.EntityFactory;
import com.semis.gradvek.entity.EntityType;
import com.semis.gradvek.graphdb.DBDriver;
import com.semis.gradvek.parquet.Parquet;

public class Importer {

	private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
	private final DBDriver mDriver;
	
	public Importer (DBDriver driver) {
		mDriver = driver;
	}
	
	private final transient Map<String, Entity> mAdditionalEntities = new HashMap<> ();
	
	public final List<Entity> readEntities (Parquet parquet, EntityType type) {
		Class<? extends Entity> entityClass = type.getEntityClass ();
		final List<Entity> toImport = new ArrayList<> ();
		parquet.getData ().stream ().forEach (p -> {
			Entity entity = EntityFactory.fromParquet (entityClass, this, p);
			if (entity != null) {
				toImport.add (entity);
			}
		});

		return (toImport);
	}
	
	public int importParquet (Parquet parquet, EntityType type, String version) {
		final List<Entity> toImport = readEntities (parquet, type);
		int numFound = toImport.size();
		logger.info("Found " + numFound + " entities to import");
		
		if (numFound > 0) {
			numFound = mDriver.add (toImport, type.canCombine (), version + "." + type);
		}
		
		return (numFound);
	}
	
	public final void additionalEntity (Entity entity) {
		mAdditionalEntities.put (entity.getId (), entity);
	}

	public final EntityType getAdditionalEntityType () {
		// assumed here that these are all of the same type; otherwise need to traverse
		if (mAdditionalEntities.size() > 0) {
			return (mAdditionalEntities.values().iterator().next().getType());
		}
		
		return (null);
	}
	
	public int processAdditionalEntities(String dbVersion) {
		int numAdditional = mAdditionalEntities.size();
		EntityType type = getAdditionalEntityType();
		if (numAdditional > 0) {
			mDriver.add(
					new ArrayList<>(mAdditionalEntities.values()),
					type.canCombine (),
					dbVersion + "." + type
			);
		}
		mAdditionalEntities.clear();
		return (numAdditional);
	}
}
