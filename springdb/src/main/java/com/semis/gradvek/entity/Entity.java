package com.semis.gradvek.entity;

import org.apache.parquet.example.data.simple.SimpleGroup;

import com.fasterxml.jackson.databind.JsonNode;
import com.semis.gradvek.springdb.Neo4jDriver;

public abstract class Entity {
	
	public static Entity parse (JsonNode json) {
		return (null);
	}

	public abstract String toCommand ();
	
	public abstract String getType ();
	
	public boolean importParquet (Neo4jDriver driver, SimpleGroup data) {
		return false;
	}
}
