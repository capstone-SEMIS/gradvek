package com.semis.gradvek.graphdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.semis.gradvek.csv.CsvFile;
import com.semis.gradvek.cytoscape.CytoscapeEntity;
import com.semis.gradvek.entity.AdverseEvent;
import com.semis.gradvek.entity.AssociatedWith;
import com.semis.gradvek.entity.Dataset;
import com.semis.gradvek.entity.Entity;
import com.semis.gradvek.entity.EntityType;
import com.semis.gradvek.entity.MechanismOfAction;
import com.semis.gradvek.springdb.AdverseEventIntObj;

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
	public void add (List<Entity> entities, boolean canCombine) {
		entities.forEach (e -> add (e));
	}

	@Override
	public void clear () {
		mDB.clear ();
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
	
	private <T extends Entity> T getEntity (Class<T> entityClass, String id) {
		Set<T> thisTypeEntities = getEntities (entityClass);
		for (Entity e: thisTypeEntities) {
			if (id.equals (e.getId ())) {
				return (entityClass.cast (e));
			}
		};
		
		return (null);
	}
	
	private <T extends Entity> Set<T> getEntities (Class<T> entityClass) {
		EntityType type = EntityType.fromEntityClass (entityClass);
		Set<? extends Entity> thisTypeEntities = mDB.getOrDefault (type, null);
		if (thisTypeEntities == null) {
			return (null);
		}
		return (thisTypeEntities.stream ().map (e -> entityClass.cast (e)).collect (Collectors.toSet ()));
		
	}
	
	private boolean checkDataset (EntityType type, String... ids) {
		boolean ret = true;
				
		for (String id: ids) {
			Entity e = getEntity (type.getEntityClass (), id);
			if (e != null) {
				Dataset ds = getEntity (Dataset.class, e.getDataset ());
				if (ds != null) {
					ret = ret && ds.isEnabled ();
				}
			}
		}
		
		return (ret);
	}
	
	@Override
	public List<AdverseEventIntObj> getAEByTarget (String target) {
		final List<AdverseEventIntObj> ret = new ArrayList<> ();
		
		// retrieve all drug-AE associations
		Set<AssociatedWith> associations = getEntities (AssociatedWith.class);
		if (associations == null) {
			return (ret);
		}

		// retrieve all drug-target associations
		Set<MechanismOfAction> mechanisms = getEntities (MechanismOfAction.class);
		if (mechanisms == null) {
			return (ret);
		}

		// retrieve all drugs acting this target
		final Set<String> drugs = new HashSet<> ();
		for (MechanismOfAction m: mechanisms) {
			if (m.getTo ().contains (target)) {
				if (checkDataset (EntityType.Target, target)) {
					drugs.addAll (m.getFrom ());
				}
			}
		};

		associations.stream ().forEach (a -> {
			String drugId = a.getFrom ();
			if (drugs.contains (drugId)) {
				if (checkDataset (EntityType.Drug, drugId) && checkDataset (EntityType.AdverseEvent, a.getTo ())) {
					AdverseEventIntObj ae = new AdverseEventIntObj (
							getEntity (AdverseEvent.class, a.getTo ()),
							a.getParams ().get ("llr")
					);
					ret.add (ae);
				}
			}
		});
		
		return (ret);
	}

	@Override
	public List<Map> getTargetSuggestions(String hint) {
		return new ArrayList<>();
	}

	@Override
	public List<CytoscapeEntity> getPathsTargetAe(String target, String ae) {
		return new ArrayList<>();
	}

	@Override
	public List<Map> getActions() {
		return new ArrayList<>();
	}

	@Override
	public List<Map> getActions(String target) {
		return new ArrayList<>();
	}

	@Override
	public List<AdverseEventIntObj> getAEByTarget(String target, List<String> actions) {
		return new ArrayList<>();
	}

	@Override
	public void loadCsv(String url, CsvFile csvFile) {
	}

	@Override
	public List<Dataset> getDatasets () {
		Set<Dataset> datasets = getEntities (Dataset.class);
		return datasets.stream ().collect (Collectors.toList ());
	}

	@Override
	public void enableDataset (String dataset, boolean enable) {
		Dataset d = getEntity (Dataset.class, dataset);
		d.setEnabled (enable);
	}

	@Override
	public String getUri() {
		return null;
	}

	@Override
	public List<CytoscapeEntity> getAEPathByTarget(String target) {
		final List<CytoscapeEntity> ret = new ArrayList<> ();
		return ret; // TODO Yan
	}

	@Override
	public List<Map> getWeightsByDrug(String target, String ae) {
		return new ArrayList<>();
	}

	@Override
	public List<Map> getWeightsByDrug(String target, List<String> actions, String ae) {
		return new ArrayList<>();
	}
}
