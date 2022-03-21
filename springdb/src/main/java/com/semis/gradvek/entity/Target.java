package com.semis.gradvek.entity;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.parquet.example.data.simple.SimpleGroup;

/**
 * The immutable object representing a drug target from the OpenTargets
 * database
 * 
 * @author ymachkasov
 *
 */public class Target extends NamedEntity {

	private String mId;

	public Target (String name, String id) {
		super (name);
		mId = id;
	}

	public Target(SimpleGroup data) {
		super(data.getString ("approvedName", 0));
		mId = data.getString ("approvedSymbol", 0);
	}

	public String getId () {
		return mId;
	}

	public void setId (String id) {
		mId = id;
	}

	@Override
	public final String addCommand () {
		return ("CREATE (:Target" + " {" + "name:\'" + StringEscapeUtils.escapeEcmaScript (super.toString ()) + "\', "
				+ "targetId:\'" + StringEscapeUtils.escapeEcmaScript (mId) + "\'})");

	}
}
