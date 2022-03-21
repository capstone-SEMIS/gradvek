package com.semis.gradvek.entity;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.parquet.example.data.simple.SimpleGroup;

/**
 * The immutable object representing a phenotype (disease) from the OpenTargets
 * database
 * 
 * @author ymachkasov
 *
 */
public class Disease extends NamedEntity {

	private String mId;

	public Disease (String name, String id) {
		super (name);
		mId = id;
	}

	/**
	 * Constructor from Parquet data
	 * 
	 * @param data the full Parquet entity for this event
	 */
	public Disease(SimpleGroup data) {
		super(data.getString ("name", 0));
		mId = data.getString ("id", 0);
	}

	public String getId () {
		return mId;
	}

	public void setId (String id) {
		mId = id;
	}

	/**
	 * The Cypher command to create this entity
	 */
	@Override
	public final String addCommand () {
		return ("CREATE (:Disease" + " {" + "name:\'" + StringEscapeUtils.escapeEcmaScript (super.toString ()) + "\', "
				+ "id:\'" + StringEscapeUtils.escapeEcmaScript (mId) + "\'})");

	}
}
