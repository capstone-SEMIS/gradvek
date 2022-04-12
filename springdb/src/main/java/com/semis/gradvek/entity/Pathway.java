package com.semis.gradvek.entity;

import java.util.Collections;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.parquet.example.data.Group;

/**
 * The immutable object representing a pathway from the OpenTargets
 * database
 * 
 * @author ymachkasov
 *
 */

public class Pathway extends NamedEntity {

	private final String mId;
	private final String mTerm;

/*
 * Parquet data (subgroup of target data)
    required group element {
        optional binary pathwayId (STRING);
        optional binary pathway (STRING);
        optional binary topLevelTerm (STRING);
      }
    }
  }
}

	 */

	public Pathway(Group data) {
		super(data.getString ("pathway", 0));
		mId = data.getString ("pathwayId", 0);
		mTerm = data.getString ("topLevelTerm", 0);
	}	
	
	public Pathway (String name, String id, String term) {
		super (name);
		mId = id;
		mTerm = term;
	}
	
	@Override
	public String getId () {
		return mId;
	}
	
	@Override
	public final List<String> addCommands () {
		return Collections.singletonList("CREATE (:Pathway" 
				+ " {" 
				+ "pathwayCode:\'" + StringEscapeUtils.escapeEcmaScript (super.toString ()) + "\', "
				+ "pathwayId:\'" + StringEscapeUtils.escapeEcmaScript (mId) + "\', "
				+ "dataset: \'" + getDataset () + "\', "
				+ "topLevelTerm:\'" + StringEscapeUtils.escapeEcmaScript (mTerm) + "\'"
				+ "})");

	}
	
	
	public final EntityType getType () {
		return EntityType.Pathway;
	}

	@Override
	public boolean equals (Object otherObj) {
		if (otherObj instanceof Pathway) {
			return ((Pathway) otherObj).mId.equals (mId);
		} else {
			return (false);
		}
	}
	
	@Override
	public int hashCode () {
		return (mId.hashCode ());
	}

}
