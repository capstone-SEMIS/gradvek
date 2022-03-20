package com.semis.gradvek.entity;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.parquet.example.data.simple.SimpleGroup;

public class Target extends NamedEntity {

	private String mId;

	public Target (String name, String id) {
		super (name);
		mId = id;
	}

	public Target(SimpleGroup data) {
		super(data.getString ("approvedName", 0));
		mId = data.getString ("approvedSymbol", 0);
	}

	@Override
	public String getType () {
		return ("Target");
	}

	public String getId () {
		return mId;
	}

	public void setId (String id) {
		mId = id;
	}

	@Override
	public final String addCommand () {
		return ("CREATE (:" + getType () + " {" + "name:\'" + StringEscapeUtils.escapeEcmaScript (super.toString ()) + "\', "
				+ "targetId:\'" + StringEscapeUtils.escapeEcmaScript (mId) + "\'})");

	}
}
