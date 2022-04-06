package com.semis.gradvek.entity;

import java.util.Collections;
import java.util.List;

import org.apache.parquet.example.data.Group; 

public class Dataset extends Entity {

	private final String mDataset;
	private final String mDescription;
	private final String mSource;
	private final long mTimestamp;
	private boolean mEnabled;
	
	public Dataset (String dataset, String description, String source, long timestamp) {
		mDataset = dataset;
		mDescription = description;
		mSource = source;
		mTimestamp = Long.valueOf (timestamp);
		mEnabled = true;
	}

	public Dataset (Group data) {
		this (
			data.getString ("dataset", 0),
			data.getString ("description", 0),
			data.getString ("source", 0),
			data.getLong ("timestamp", 0)
		);
	}

	public String getDataset () {
		return mDataset;
	}

	public String getDescription () {
		return mDescription;
	}

	public String getSource () {
		return mSource;
	}

	public long getTimestamp () {
		return mTimestamp;
	}

	public boolean isEnabled () {
		return mEnabled;
	}

	public void setEnabled (boolean enabled) {
		mEnabled = enabled;
	}

	@Override
	public List<String> addCommands () {
		return Collections.singletonList("MERGE (d:Dataset {dataset:\'" + mDataset + "\'}) " 
				+ "SET d = {"
				+ "dataset: \'" + mDataset + "\', "
				+ "description: \'" + mDescription + "\', "
				+ "source: \'" + mSource + "\', "
				+ "timestamp: " + mTimestamp + ", "
				+ "enabled: " + mEnabled
				+ "} RETURN d.dataset"
		);
	}

	@Override
	public EntityType getType () {
		return (EntityType.Dataset);
	}
	
	@Override
	public String getId ( ) {
		return (getDataset ());
	}
}
