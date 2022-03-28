package com.semis.gradvek.springdb;

import java.util.HashSet;
import java.util.Set;

import com.semis.gradvek.entity.Entity;
import com.semis.gradvek.entity.EntityFactory;
import com.semis.gradvek.entity.EntityType;
import com.semis.gradvek.parquet.Parquet;

public class Importer {

	private final DBDriver mDriver;
	
	public Importer (DBDriver driver) {
		mDriver = driver;
	}
	
	public final void importParquet (Parquet parquet, EntityType type) {
		final Set<Entity> toImport = new HashSet<> ();
		parquet.getData ().stream ().forEach (p -> {
			Entity entity = EntityFactory.fromParquet (type.getEntityClass (), p);
			if (entity != null) {
				toImport.add (entity);
			}
		});
		
		if (toImport.size () >= 0) {
			mDriver.add (toImport, type.canCombine ());
		}
	}
}
