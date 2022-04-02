package com.semis.gradvek.entity;

import java.util.Collections;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.parquet.example.data.Group;

/**
 * The immutable object representing an adverse effect from the OpenTargets
 * database
 *
 * @author ymachkasov, ychen
 *
 */
public class AdverseEvent extends NamedEntity {
	/**
	 * The code of the adverse event according to Medical Dictionary for Regulatory
	 * Activities (https://www.meddra.org/)
	 */
	private final String mMeddraCode;
	
	public AdverseEvent (String name, String id, String code) {
		super (name);
		mMeddraCode = code;
	}

	/**
	 * Constructor from Parquet data
	 *
	 * @param data the full Parquet entity for this event
	 */
	public AdverseEvent (Group data) {
		super (data.getString ("event", 0));
		mMeddraCode = data.getString ("meddraCode", 0);
	}

	public String getMeddraCode () {
		return mMeddraCode;
	}

	@Override
	public final List<String> addCommands () {
		return Collections.singletonList("CREATE (:AdverseEvent " 
				+ " {" + "meddraCode:\'" + mMeddraCode + "\', "
				+ "adverseEventId:\'" + StringEscapeUtils.escapeEcmaScript (getName ()) + "\'"
				+ "})");
	}
	
	public final EntityType getType () {
		return EntityType.AdverseEvent;
	}
	
	@Override
	public boolean equals (Object otherObj) {
		if (otherObj instanceof AdverseEvent) {
			return ((AdverseEvent) otherObj).mMeddraCode.equals (mMeddraCode);
		} else {
			return (false);
		}
	}
	
	@Override
	public int hashCode () {
		return (mMeddraCode.hashCode ());
	}
	

	@Override
	public String getId () {
		return (getMeddraCode ());
	}

}
