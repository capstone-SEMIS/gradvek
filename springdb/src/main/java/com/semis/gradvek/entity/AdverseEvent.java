package com.semis.gradvek.entity;

import org.apache.parquet.example.data.simple.SimpleGroup;

/**
 * The immutable object representing an adverse effect from the OpenTargets
 * database
 * 
 * @author ymachkasov
 *
 */
public class AdverseEvent extends NamedEntity {
	/**
	 * The human-readable description of the adverse event
	 */
	private final String mId;

	/**
	 * The code of the adverse event according to Medical Dictionary for Regulatory
	 * Activities (https://www.meddra.org/)
	 */
	private final String mMeddraCode;

	public AdverseEvent (String name, String id, String code) {
		super (name);
		mId = id;
		mMeddraCode = code;
	}

	/**
	 * Constructor from Parquet data
	 * 
	 * @param data the full Parquet entity for this event
	 */
	public AdverseEvent (SimpleGroup data) {
		super (data.getString ("name", 0));
		mId = data.getString ("id", 0);
		mMeddraCode = data.getString ("meddraCode", 0);
	}

	public String getId () {
		return mId;
	}

	public String getMeddraCode () {
		return mMeddraCode;
	}

	@Override
	public final String addCommand () {
		return ("CREATE (" + getName () + ":AdverseEvent" + " {" + super.toString () + "adverseEventId: \'" + mId
				+ "\', meddraCode: \'" + mMeddraCode + "\'})"

		);
	}

}
