package com.semis.gradvek.entity;

import java.util.Collections;
import java.util.List;

/**
 * The immutable object representing a gene from the OpenTargets
 * database
 * 
 * @author ymachkasov
 *
 */
public class Gene extends NamedEntity {
	private String mId;

	public Gene(String id) {
		super(id);
		mId = id;
	}


	@Override
	public String getId () {
		return mId;
	}


	public void setId (String id) {
		mId = id;
	}


	@Override
	public final List<String> addCommands () {
		return Collections.singletonList(
			"CREATE (:Gene {geneId:\'" + mId + "\', " + getDatasetCommandString () + "})"
		);
			
	}
	
	public final EntityType getType () {
		return EntityType.Gene;
	}
}
