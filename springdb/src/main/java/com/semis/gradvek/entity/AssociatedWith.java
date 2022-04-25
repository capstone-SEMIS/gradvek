package com.semis.gradvek.entity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.parquet.example.data.Group;

import com.semis.gradvek.parquet.ParquetUtils;
import com.semis.gradvek.springdb.Importer;

/**
 * The immutable class representing a causal connection between a drug
 * and an adverse event 
 * @author ymachkasov
 *
 */
public class AssociatedWith extends Edge {

	public AssociatedWith (String from, String to, Map<String, String> params) {
		super (from, to, params);
	}

	/**
	 * Constructor from Parquet data
	 * 
	 * @param data the full Parquet entity for this event
	 */
	public AssociatedWith (Importer importer, Group data) {
		super (data.getString ("chembl_id", 0), data.getString ("meddraCode", 0),
				ParquetUtils.extractParams (data, "llr", "critval"/*, "count" is there, but irrelevant */));
		setDataset ("AssociatedWith");
	}

	/**
	 * The Cypher command to create this entity
	 */
	@Override
	public List<String> addCommands () {
		String jsonMap = ParquetUtils.paramsAsJSON (getParams ());

		return Collections.singletonList(
			"MATCH (from:Drug), (to:AdverseEvent)\n"
			+ "WHERE from." + DRUG_ID_STRING + "=\'" + getFrom () + "\'\n"
			+ "AND to." + ADVERSE_EVENT_ID_STRING +"=\'" + getTo () + "\'\n"
			+ "CREATE (from)-[:ASSOCIATED_WITH" 
			+ " { dataset: \'" + getDataset () + "\' "
			+ (jsonMap != null ? (", " + jsonMap) : "")
			+ "} " 
			+ "]->(to)"
		);
	}

	
	public final EntityType getType () {
		return EntityType.AssociatedWith;
	}
}
