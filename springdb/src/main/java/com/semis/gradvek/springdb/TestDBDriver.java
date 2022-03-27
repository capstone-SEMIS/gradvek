package com.semis.gradvek.springdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.semis.gradvek.entity.AdverseEvent;
import com.semis.gradvek.entity.Entity;
import com.semis.gradvek.entity.EntityType;

public class TestDBDriver implements DBDriver {

	private final Map<EntityType, Set<Entity>> mDB = new HashMap<> ();

	@Override
	public void add (Entity entity) {
		Set<Entity> thisTypeEntities = mDB.getOrDefault (entity.getType (), null);
		if (thisTypeEntities == null) {
			thisTypeEntities = new HashSet<Entity> ();
			mDB.put (entity.getType (), thisTypeEntities);
		}

		thisTypeEntities.add (entity);
	}

	@Override
	public void add (Set<Entity> entities, boolean canCombine) {
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
		Set<Entity> thisTypeEntities = mDB.getOrDefault (type, null);
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

	@Override
	public List<AdverseEvent> getAEByTarget (String target) {
		List<AdverseEvent> ret = new ArrayList<> ();
		Set<Entity> thisTypeEntities = mDB.getOrDefault (EntityType.AdverseEvent, null);
		if (thisTypeEntities == null) {
			return (ret);
		}

		thisTypeEntities.stream ().map (e -> (AdverseEvent) e).forEach (ae -> {
//			if (ae.)
		});

		return (ret);
	}

}
