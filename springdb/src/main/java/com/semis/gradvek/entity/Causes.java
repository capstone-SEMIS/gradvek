package com.semis.gradvek.entity;

import java.util.Map;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.parquet.example.data.simple.SimpleGroup;

import com.semis.gradvek.parquet.ParquetUtils;

/**
 * The immutable class representing a causal connection between a drug
 * and an adverse event 
 * @author ymachkasov
 *
 */
public class Causes extends Edge {

	public Causes (String type, String from, String to, Map<String, String> params) {
		super (from, to, params);
	}

	private String mAdverseEvent;

	/**
	 * Constructor from Parquet data
	 * 
	 * @param data the full Parquet entity for this event
	 */
	public Causes (SimpleGroup data) {
		super (data.getString ("chembl_id", 0), data.getString ("meddraCode", 0),
				ParquetUtils.extractParams (data, "llr", "critval"/*, "count" is there, but irrelevant */));
		mAdverseEvent = data.getString ("event", 0);
	}

	/**
	 * The Cypher command to create this entity cannot be batched because of the many-to-many
	 * nature of the relationship 
	 */
	@Override
	public boolean canCombine () {
		return (false);
	}

	/**
	 * The Cypher command to create this entity
	 */
	@Override
	public String addCommand () {
		String jsonMap = ParquetUtils.paramsAsJSON (getParams ());

		return ("MATCH (from:Drug) WHERE from.chembl_code=\'" + getFrom () + "\'\n" 
				+ "MERGE (to:AdverseEvent {meddraCode:\'" + getTo () 
				+ "\', adverseEventId: \'" + StringEscapeUtils.escapeEcmaScript (mAdverseEvent) + "\'})\n"
				+ "CREATE (from)-[:CAUSES " + (jsonMap != null ? "{" + jsonMap + "}": "") + "]->(to)");
	}

}
