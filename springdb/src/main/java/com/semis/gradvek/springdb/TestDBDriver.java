package com.semis.gradvek.springdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.semis.gradvek.entity.AdverseEvent;
import com.semis.gradvek.entity.Dataset;
import com.semis.gradvek.entity.Entity;
import com.semis.gradvek.entity.EntityType;
import org.neo4j.driver.Driver;

public class TestDBDriver implements DBDriver {

	private final Map<EntityType, Set<? extends Entity>> mDB = new HashMap<> ();

	@Override
	public <T extends Entity> void add (T entity) {
		@SuppressWarnings ("unchecked")
		Set<T> thisTypeEntities = (Set<T>) mDB.getOrDefault (entity.getType (), null);
		if (thisTypeEntities == null) {
			thisTypeEntities = new HashSet<T> ();
			mDB.put (entity.getType (), thisTypeEntities);
		}

		thisTypeEntities.add (entity);
	}

	@Override
	public <T extends Entity> void add (Set<T> entities, boolean canCombine) {
		entities.forEach (e -> add (e));
	}

	@Override
	public void clear () {
		mDB.clear ();
	}

	@Override
	public void write (String command) {
		throw new RuntimeException ("should not be called on the mock driver");
	}

	@Override
	public int count (EntityType type) {
		Set<? extends Entity> thisTypeEntities = mDB.getOrDefault (type, null);
		return (thisTypeEntities != null ? thisTypeEntities.size () : 0);
	}

	@Override
	public void index (EntityType type) {
		// nothing
	}

	@Override
	public void unique (EntityType type) {
		// nothing
	}
	
//	public <T extends Entity> T getById (Class<T> entityClass, String id) {
//		EntityType type = EntityType.fromEntityClass (entityClass);
//		if (type.getIndexField () == null) {
//			return null;
//		}
//		
//		Set<Entity> thisTypeEntities = mDB.getOrDefault (type, null);
//		for ()
//	}

	@Override
	public List<AdverseEventIntObj> getAEByTarget (String target) {
		List<AdverseEventIntObj> ret = new ArrayList<> ();
		Set<? extends Entity> thisTypeEntities = mDB.getOrDefault (EntityType.AdverseEvent, null);
		if (thisTypeEntities == null) {
			return (ret);
		}

		thisTypeEntities.stream ().map (e -> (AdverseEvent) e).forEach (ae -> {
//			if (ae.)
		});

		return (ret);
	}

	@Override
	public void loadCsv(String url, List<String> columns) {
	}

	@Override
	public List<Dataset> getDatasets () {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void enableDataset (String dataset, boolean enable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getUri() {
		return null;
	}
}
