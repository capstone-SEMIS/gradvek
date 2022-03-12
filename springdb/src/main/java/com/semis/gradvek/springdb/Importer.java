package com.semis.gradvek.springdb;

import java.util.List;

import org.apache.parquet.example.data.simple.SimpleGroup;

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
		List<SimpleGroup> data = parquet.getData ();
		data.stream ().forEach (e -> {
			Entity entity = EntityFactory.newEntity (EntityType.getEntityClass (type));
			entity.importParquet (mDriver, e);
		});
	}
}
