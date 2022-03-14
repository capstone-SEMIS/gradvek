package com.semis.gradvek.springdb;

import java.util.ArrayList;
import java.util.List;

import com.semis.gradvek.entity.Entity;
import com.semis.gradvek.entity.EntityFactory;
import com.semis.gradvek.entity.EntityType;
import com.semis.gradvek.parquet.Parquet;

public class Importer {

	private final Neo4jDriver mDriver;
	
	public Importer (Neo4jDriver driver) {
		mDriver = driver;
	}
	
	public final void importParquet (Parquet parquet, EntityType type) {
		final List<Entity> toImport = new ArrayList<> ();
		parquet.getData ().stream ().forEach (p -> {
			Entity entity = EntityFactory.fromParquet (EntityType.getEntityClass (type), p);
			if (entity != null) {
				toImport.add (entity);
			}
		});
		mDriver.add (toImport);
	}
}
