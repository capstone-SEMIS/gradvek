package com.semis.gradvek.entity;

import java.util.Collections;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.parquet.example.data.Group;

import com.semis.gradvek.springdb.Importer;

public class Drug extends NamedEntity {

	private final String mChemblId;
	
	public Drug (String name, String code) {
		super (name);
		mChemblId = code;
	}

	/* drug Parquet schema, the fields we use are marked with ^:
message spark_schema {
  ^optional binary id (STRING); // Chembl identifier
  optional binary canonicalSmiles (STRING);
  optional binary inchiKey (STRING);
  optional binary drugType (STRING);
  optional boolean blackBoxWarning;
  ^optional binary name (STRING);
  optional int64 yearOfFirstApproval;
  optional int64 maximumClinicalTrialPhase;
  optional binary parentId (STRING);
  optional boolean hasBeenWithdrawn;
  optional boolean isApproved;
  optional group withdrawnNotice {
    optional group countries (LIST) {
      repeated group list {
        optional binary element (STRING);
      }
    }
    optional group classes (LIST) {
      repeated group list {
        optional binary element (STRING);
      }
    }
    optional int64 year;
  }
  required group tradeNames (LIST) {
    repeated group list {
      optional binary element (STRING);
    }
  }
  required group synonyms (LIST) {
    repeated group list {
      optional binary element (STRING);
    }
  }
  optional group crossReferences (MAP) {
    repeated group key_value {
      required binary key (STRING);
      optional group value (LIST) {
        repeated group list {
          optional binary element (STRING);
        }
      }
    }
  }
  optional group childChemblIds (LIST) {
    repeated group list {
      required binary element (STRING);
    }
  }
  ^optional group linkedTargets {
    required group rows (LIST) {
      repeated group list {
        required binary element (STRING); // this is the target ensembleId, used to create the relationship
      }
    }
    required int32 count;
  }
  optional group linkedDiseases {
    required group rows (LIST) {
      repeated group list {
        required binary element (STRING);
      }
    }
    required int32 count;
  }
  optional binary description (STRING);
}

	 */
	public Drug(Importer importer, Group data) {
		super(data.getString ("name", 0));
		mChemblId = data.getString ("id", 0);
		setDataset ("$" + DB_VERSION_PARAM);
	}

	public String getChemblCode () {
		return mChemblId;
	}

	@Override
	public final List<String> addCommands () {
		return Collections.singletonList("CREATE (:Drug" + " {" + "drugId:\'" + StringEscapeUtils.escapeEcmaScript (getName ()) + "\', "
				+ getDatasetCommandString () + ", "
				+ DRUG_ID_STRING + ":\'" + StringEscapeUtils.escapeEcmaScript (mChemblId) + "\'})");
	}
	
	@Override
	public boolean filter (Group data) {
		return (true);
	}
	
	
	public final EntityType getType () {
		return EntityType.Drug;
	}
	
	@Override
	public boolean equals (Object otherObj) {
		if (otherObj instanceof Drug) {
			return ((Drug) otherObj).mChemblId.equals (mChemblId);
		} else {
			return (false);
		}
	}
	
	@Override
	public int hashCode () {
		return (mChemblId.hashCode ());
	}

	@Override
	public String getId () {
		return (getChemblCode ());
	}
}
