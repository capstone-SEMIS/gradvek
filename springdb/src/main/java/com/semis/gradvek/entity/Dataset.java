package com.semis.gradvek.entity;

import java.util.Collections;
import java.util.List;

import org.apache.parquet.example.data.Group; 

public class Dataset extends Entity {

	private final String mDataset;
	private final String mDescription;
	private final String mSource;
	private final long mTimestamp;
	private final boolean mEnabled;
	
	public Dataset (String dataset, String description, String source, long timestamp, boolean enabled) {
		mDataset = dataset;
		mDescription = description;
		mSource = source;
		mTimestamp = Long.valueOf (timestamp);
		mEnabled = enabled;
	}

	public Dataset (Group data) {
		this (
			data.getString ("dataset", 0),
			data.getString ("description", 0),
			data.getString ("source", 0),
			data.getLong ("timestamp", 0),
			data.getBoolean ("enabled", 0)
		);
	}

	@Override
	public List<String> addCommands () {
		return Collections.singletonList("MERGE (d:Dataset {dataset:\'" + mDataset + "\'}) " 
				+ "SET d.description=\'" + mDescription + "\', "
				+ "SET d.source=\'" + mSource + "\', "
				+ "SET d.timestamp=" + mTimestamp
				+ "SET d.enabled=" + mEnabled
		);
	}

	@Override
	public EntityType getType () {
		return (EntityType.Dataset);
	}
}
