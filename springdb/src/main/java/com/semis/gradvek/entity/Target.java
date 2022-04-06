package com.semis.gradvek.entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.parquet.example.data.Group;

import com.semis.gradvek.parquet.ParquetUtils;

/**
 * The immutable object representing a drug target from the OpenTargets
 * database
 * 
 * @author ymachkasov
 *
 */public class Target extends NamedEntity {

	private final String mId;
	private final String mSymbol;
	
	private transient List<Pathway> mParquetPathways = new ArrayList<> ();

	public Target (String name, String id, String symbol) {
		super (name);
		mId = id;
		mSymbol = symbol;
	}
	
	/* target Parquet schema, the fields we use are marked with ^:
message spark_schema {
  ^optional binary id (STRING); // ensemble ID
  ^optional binary approvedSymbol (STRING);
  optional binary biotype (STRING);
  optional group transcriptIds (LIST) {
    repeated group list {
      optional binary element (STRING);
    }
  }
  required group genomicLocation {
    optional binary chromosome (STRING);
    optional int64 start;
    optional int64 end;
    optional int32 strand;
  }
  optional group alternativeGenes (LIST) {
    repeated group list {
      optional binary element (STRING);
    }
  }
  ^required binary approvedName (STRING);
  optional group go (LIST) {
    repeated group list {
      optional group element {
        optional binary id (STRING);
        optional binary source (STRING);
        optional binary evidence (STRING);
        optional binary aspect (STRING);
        optional binary geneProduct (STRING);
        optional binary ecoId (STRING);
      }
    }
  }
  optional group hallmarks {
    optional group attributes (LIST) {
      repeated group list {
        required group element {
          optional int64 pmid;
          optional binary description (STRING);
          optional binary attribute_name (STRING);
        }
      }
    }
    optional group cancerHallmarks (LIST) {
      repeated group list {
        required group element {
          optional int64 pmid;
          optional binary description (STRING);
          optional binary impact (STRING);
          optional binary label (STRING);
        }
      }
    }
  }
  required group synonyms (LIST) {
    repeated group list {
      optional group element {
        optional binary label (STRING);
        optional binary source (STRING);
      }
    }
  }
  required group symbolSynonyms (LIST) {
    repeated group list {
      optional group element {
        optional binary label (STRING);
        optional binary source (STRING);
      }
    }
  }
  required group nameSynonyms (LIST) {
    repeated group list {
      optional group element {
        optional binary label (STRING);
        optional binary source (STRING);
      }
    }
  }
  optional group functionDescriptions (LIST) {
    repeated group list {
      optional binary element (STRING);
    }
  }
  optional group subcellularLocations (LIST) {
    repeated group list {
      optional group element {
        optional binary location (STRING);
        optional binary source (STRING);
        optional binary termSL (STRING);
        optional binary labelSL (STRING);
      }
    }
  }
  optional group targetClass (LIST) {
    repeated group list {
      required group element {
        optional int64 id;
        optional binary label (STRING);
        required binary level (STRING);
      }
    }
  }
  required group obsoleteSymbols (LIST) {
    repeated group list {
      optional group element {
        optional binary label (STRING);
        optional binary source (STRING);
      }
    }
  }
  required group obsoleteNames (LIST) {
    repeated group list {
      optional group element {
        optional binary label (STRING);
        optional binary source (STRING);
      }
    }
  }
  optional group constraint (LIST) {
    repeated group list {
      required group element {
        required binary constraintType (STRING);
        optional float score;
        optional float exp;
        optional int32 obs;
        optional float oe;
        optional float oeLower;
        optional float oeUpper;
        optional int32 upperRank;
        optional int32 upperBin;
        optional int32 upperBin6;
      }
    }
  }
  optional group tep {
    optional binary targetFromSourceId (STRING);
    optional binary description (STRING);
    optional binary therapeuticArea (STRING);
    optional binary url (STRING);
  }
  optional group proteinIds (LIST) {
    repeated group list {
      required group element {
        optional binary id (STRING);
        optional binary source (STRING);
      }
    }
  }
  required group dbXrefs (LIST) {
    repeated group list {
      optional group element {
        optional binary id (STRING);
        optional binary source (STRING);
      }
    }
  }
  optional group chemicalProbes (LIST) {
    repeated group list {
      required group element {
        optional binary control (STRING);
        optional binary drugId (STRING);
        optional binary id (STRING);
        optional binary inchiKey (STRING);
        optional boolean isHighQuality;
        optional group mechanismOfAction (LIST) {
          repeated group list {
            optional binary element (STRING);
          }
        }
        optional group origin (LIST) {
          repeated group list {
            optional binary element (STRING);
          }
        }
        optional binary probeMinerScore (STRING);
        optional binary probesDrugsScore (STRING);
        optional binary scoreInCells (STRING);
        optional binary scoreInOrganisms (STRING);
        optional binary targetFromSourceId (STRING);
        optional group urls (LIST) {
          repeated group list {
            optional group element {
              optional binary niceName (STRING);
              optional binary url (STRING);
            }
          }
        }
      }
    }
  }
  optional group homologues (LIST) {
    repeated group list {
      required group element {
        optional binary speciesId (STRING);
        optional binary speciesName (STRING);
        optional binary homologyType (STRING);
        optional binary targetGeneId (STRING);
        optional binary isHighConfidence (STRING);
        optional binary targetGeneSymbol (STRING);
        optional double queryPercentageIdentity;
        optional double targetPercentageIdentity;
        optional int32 priority;
      }
    }
  }
  optional group tractability (LIST) {
    repeated group list {
      required group element {
        required binary modality (STRING);
        required binary id (STRING);
        required boolean value;
      }
    }
  }
  optional group safetyLiabilities (LIST) {
    repeated group list {
      required group element {
        optional binary event (STRING);
        optional binary eventId (STRING);
        optional group effects (LIST) {
          repeated group list {
            required group element {
              optional binary direction (STRING);
              optional binary dosing (STRING);
            }
          }
        }
        required group biosample (LIST) {
          repeated group list {
            required group element {
              optional binary tissueLabel (STRING);
              optional binary tissueId (STRING);
              optional binary cellLabel (STRING);
              optional binary cellFormat (STRING);
              optional binary cellId (STRING);
            }
          }
        }
        optional binary datasource (STRING);
        optional binary literature (STRING);
        optional binary url (STRING);
        required group study (LIST) {
          repeated group list {
            required group element {
              optional binary name (STRING);
              optional binary description (STRING);
              optional binary type (STRING);
            }
          }
        }
      }
    }
  }
  ^optional group pathways (LIST) {
    repeated group list {
      required group element {
        optional binary pathwayId (STRING);
        optional binary pathway (STRING);
        optional binary topLevelTerm (STRING);
      }
    }
  }
}

	 */

	public Target(Group data) {
		super(data.getString ("approvedName", 0));
		mSymbol = data.getString ("approvedSymbol", 0);
		mId = data.getString ("id", 0);
		
		List<Group> pathways = ParquetUtils.extractGroupList (data, "pathways");
		pathways.forEach (p -> mParquetPathways.add (new Pathway (p)));
	}

	@Override
	public String getId () {
		return mId;
	}

	@Override
	public final List<String> addCommands () {
		List<String> ret = new ArrayList<> ();
		ret.add ("CREATE (:Target" 
				+ " {" + "name:\'" + StringEscapeUtils.escapeEcmaScript (super.toString ()) + "\', "
				+ "targetId:\'" + StringEscapeUtils.escapeEcmaScript (mId) + "\', "
				+ "symbol:\'" + StringEscapeUtils.escapeEcmaScript (mSymbol) + "\'"
				+ "})");

		mParquetPathways.forEach (p -> ret.add (p.addCommands ().get (0)));
		
		return (ret);
	}
	
	
	public final EntityType getType () {
		return EntityType.Target;
	}

	@Override
	public boolean equals (Object otherObj) {
		if (otherObj instanceof Target) {
			return ((Target) otherObj).mId.equals (mId);
		} else {
			return (false);
		}
	}
	
	@Override
	public int hashCode () {
		return (mId.hashCode ());
	}

}
