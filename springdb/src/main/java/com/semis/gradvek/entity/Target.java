package com.semis.gradvek.entity;

import org.apache.commons.text.StringEscapeUtils;
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
	public final String toCommand () {
		return (
			"(:" + getType()
			+ " {"
			+ "name:\'" + StringEscapeUtils.escapeEcmaScript (super.toString()) + "\', "
			+ "targetId:\'" + StringEscapeUtils.escapeEcmaScript (mId)
			+ "\'})"
		);
			
	}
	
	@Override
	public boolean importParquet (Neo4jDriver driver, SimpleGroup data) {
		setName (data.getString ("approvedName", 0));
		setId (data.getString ("approvedSymbol", 0));
		return (true);
	}

}
