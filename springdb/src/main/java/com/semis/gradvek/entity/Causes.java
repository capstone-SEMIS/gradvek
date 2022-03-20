package com.semis.gradvek.entity;

import java.util.Map;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.parquet.example.data.simple.SimpleGroup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.semis.gradvek.parquet.ParquetUtils;

public class Causes extends Edge {

	public Causes (String type, String from, String to, Map<String, String> params) {
		super (from, to, params);
	}

	private String mAdverseEvent;

	public Causes (SimpleGroup data) {
		super (data.getString ("chembl_id", 0), data.getString ("meddraCode", 0),
				ParquetUtils.extractParams (data, "llr", "critval", "count"));
		mAdverseEvent = data.getString ("event", 0);
	}

	@Override
	public String getType () {
		return ("CAUSES");
	}
	
	@Override
	public boolean canCombine () {
		return (false);
	}

	@Override
	public String addCommand () {
		String jsonMap = ParquetUtils.paramsAsJSON (getParams ());

		return ("MATCH (from:Drug) WHERE from.chembl_code=\'" + getFrom () + "\'\n" 
				+ "MERGE (to:AdverseEvent {meddraCode:\'" + getTo () 
				+ "\', adverseEventId: \'" + StringEscapeUtils.escapeEcmaScript (mAdverseEvent) + "\'})\n"
				+ "CREATE (from)-[:CAUSES " + (jsonMap != null ? "{" + jsonMap + "}": "") + "]->(to)");
	}

}
