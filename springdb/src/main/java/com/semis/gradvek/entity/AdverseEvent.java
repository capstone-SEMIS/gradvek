package com.semis.gradvek.entity;

import java.util.Collections;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.parquet.example.data.Group;

import com.semis.gradvek.springdb.Importer;

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
	private final String mMeddraId;
	
	public AdverseEvent (String name, String id, String code) {
		super (name);
		mMeddraId = code;
	}

	/**
	 * Constructor from Parquet data
	 *
	 * @param data the full Parquet entity for this event
	 */
	public AdverseEvent (Importer importer, Group data) {
		super (data.getString ("event", 0));
		setDataset ("AdverseEvent");
		mMeddraId = data.getString ("meddraCode", 0);
	}

	public String getMeddraCode () {
		return mMeddraId;
	}

	@Override
	public final List<String> addCommands () {
		return Collections.singletonList("CREATE (:AdverseEvent " 
				+ " {" + ADVERSE_EVENT_ID_STRING + ":\'" + mMeddraId + "\', "
				+ "dataset: \'" + getDataset () + "\', "
				+ "adverseEventId:\'" + StringEscapeUtils.escapeEcmaScript (getName ()) + "\'"
				+ "})");
	}
	
	public final EntityType getType () {
		return EntityType.AdverseEvent;
	}
	
	@Override
	public boolean equals (Object otherObj) {
		if (otherObj instanceof AdverseEvent) {
			return ((AdverseEvent) otherObj).mMeddraId.equals (mMeddraId);
		} else {
			return (false);
		}
	}
	
	@Override
	public int hashCode () {
		return (mMeddraId.hashCode ());
	}
	

	@Override
	public String getId () {
		return (getMeddraCode ());
	}

}
