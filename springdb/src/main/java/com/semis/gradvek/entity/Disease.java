package com.semis.gradvek.entity;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.parquet.example.data.simple.SimpleGroup;

public class Disease extends NamedEntity {

	private String mId;

	public Disease (String name, String id) {
		super (name);
		mId = id;
	}

	public Disease(SimpleGroup data) {
		super(data.getString ("name", 0));
		mId = data.getString ("id", 0);
	}

	@Override
	public String getType () {
		return ("Disease");
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
				+ "id:\'" + StringEscapeUtils.escapeEcmaScript (mId) + "\'})");

	}
}
