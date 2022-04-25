package com.semis.gradvek.springdb;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
	
	public void importParquet (Parquet parquet, EntityType type) {
		final List<Entity> toImport = readEntities (parquet, type);
		logger.fine("Found " + toImport.size() + " entities to import");
		
		if (toImport.size () > 0) {
			mDriver.add (toImport, type.canCombine ());
		}
	}
	
	public final void additionalEntity (Entity entity) {
		mAdditionalEntities.put (entity.getId (), entity);
	}

	public void processAdditionalEntities() {
		if (mAdditionalEntities.size() > 0) {
			mDriver.add(new ArrayList<>(mAdditionalEntities.values()),
					mAdditionalEntities.values().iterator().next().getType().canCombine());
		}
		mAdditionalEntities.clear();
	}
}
