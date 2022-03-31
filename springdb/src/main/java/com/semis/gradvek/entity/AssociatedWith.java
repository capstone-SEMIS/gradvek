package com.semis.gradvek.entity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.parquet.example.data.Group;

import com.semis.gradvek.parquet.ParquetUtils;

/**
 * The immutable class representing a causal connection between a drug
 * and an adverse event 
 * @author ymachkasov
 *
 */
public class AssociatedWith extends Edge {

	public AssociatedWith (String type, String from, String to, Map<String, String> params) {
		super (from, to, params);
	}

	/**
	 * Constructor from Parquet data
	 * 
	 * @param data the full Parquet entity for this event
	 */
	public AssociatedWith (Group data) {
		super (data.getString ("chembl_id", 0), data.getString ("meddraCode", 0),
				ParquetUtils.extractParams (data, "llr", "critval"/*, "count" is there, but irrelevant */));
	}

	/**
	 * The Cypher command to create this entity
	 */
	@Override
	public List<String> addCommands () {
		String jsonMap = ParquetUtils.paramsAsJSON (getParams ());

		return Collections.singletonList(
			"MATCH (from:Drug), (to:AdverseEvent)\n"
			+ "WHERE from.chembl_code=\'" + getFrom () + "\'\n"
			+ "AND to.meddraCode=\'" + getTo () + "\'\n"
			+ "CREATE (from)-[:ASSOCIATED_WITH" + (jsonMap != null ? " {" + jsonMap + "} ": "") + "]->(to)"
		);
	}

	
	public final EntityType getType () {
		return EntityType.AssociatedWith;
	}
}
