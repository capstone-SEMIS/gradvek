package com.semis.gradvek.entity;

import java.util.Collections;
import java.util.List;

import org.apache.parquet.example.data.Group; 

public class Dataset extends Entity {

	private final String mDataset;
	private final String mDescription;
	private final String mSource;
	private final long mTimestamp;
	
	public Dataset (String dataset, String description, String source, String timestamp) {
		mDataset = dataset;
		mDescription = description;
		mSource = source;
		mTimestamp = Long.valueOf (timestamp);
	}

	public Dataset (Group data) {
		this (
			data.getString ("dataset", 0),
			data.getString ("description", 0),
			data.getString ("source", 0),
			data.getString ("timestamp", 0)
		);
	}

	@Override
	public List<String> addCommands () {
		return Collections.singletonList("MERGE (d:Dataset {dataset:\'" + mDataset + "\'}) " 
				+ "SET d.description=\'" + mDescription + "\', "
				+ "SET d.source=\'" + mSource + "\', "
				+ "SET d.timestamp=\'" + mTimestamp + "\'"
		);
	}

	@Override
	public EntityType getType () {
		return (EntityType.Dataset);
	}
}
