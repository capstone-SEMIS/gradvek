package com.semis.gradvek.entity;

import org.apache.parquet.example.data.simple.SimpleGroup;

import com.semis.gradvek.springdb.Neo4jDriver;

public class Target extends NamedEntity {
	
	private String mId;
	
	public Target () {
		// Factory
	}

	public Target(String name, String id) {
		super(name);
		mId = id;
	}

	@Override
	public String getType() {
		return ("Target");
	}

	public String getId () {
		return mId;
	}

	public void setId (String id) {
		mId = id;
	}

	@Override
	public final String toString () {
		return (
			"(" + mId
			+ ":" + getType()
			+ " {"
			+ super.toString() + ", "
			+ "targetId:\'" + mId
			+ "\'})"
		);
			
	}
	
	@Override
	public void importParquet (Neo4jDriver driver, SimpleGroup data) {
		setName (data.getString ("approvedName", 0));
		setId (data.getString ("approvedSymbol", 0));
		driver.add (this);
		System.currentTimeMillis ();
	}

}
