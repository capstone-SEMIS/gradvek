package com.semis.gradvek.entity;

import com.semis.gradvek.parquet.ParquetUtils;
import com.semis.gradvek.springdb.Importer;
import org.apache.parquet.example.data.Group;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The immutable class representing a connection between
 * drug(s) and target(s) through an action mechanism
 * @author ymachkasov
 *
 */
public class MechanismOfAction extends Edges {

	public MechanismOfAction (List<String> from, List<String> to, Map<String, String> params) {
		super (from, to, params);
	}

	/*
	 * Schema: 
{
  optional binary actionType (STRING); // ex.: "ANTAGONIST"
  optional binary mechanismOfAction (STRING); // human-readable descriptor, ex: "Histamine H1 receptor antagonist"
  optional group chemblIds (LIST) {
    repeated group list {
      optional binary element (STRING); // Chembl ids of drugs, ex: "CHEMBL1200638"
    }
  }
  optional binary targetName (STRING); // Human-readable name, ex: "Histamine H1 receptor"
  optional binary targetType (STRING); // ex: "single protein"
  optional group targets (LIST) {
    repeated group list {
      required binary element (STRING); // ensemble ids, ex: "ENSG00000196639"
    }
  }
  required group references (LIST) {
    repeated group list {
      required group element {
        optional binary source (STRING);
        required group ids (LIST) {
          repeated group list {
            required binary element (STRING);
          }
        }
        required group urls (LIST) {
          repeated group list {
            required binary element (STRING);
          }
        }
      }
    }
  }
}
	 */
	/**
	 * Constructor from Parquet data
	 * 
	 * @param data the full Parquet entity for this event
	 */
	public MechanismOfAction (Importer importer, Group data) {
		super (
				ParquetUtils.extractStringList (data, "chemblIds"),
				ParquetUtils.extractStringList (data, "targets"),
				Map.of (
//						"mechanismOfAction", data.getString ("mechanismOfAction", 0),
						"actionType", data.getString ("actionType", 0)
				)
			);
		setDataset ("$" + DB_VERSION_PARAM);

		Map<String, String> params = getParams ();
		getFrom().forEach(from -> getTo().forEach(to -> importer.additionalEntity(new Action(from, to, params))));
	}

	/**
	 * The Cypher command to create this entity
	 */
	@Override
	public List<String> addCommands () {
		List<String> commands = new ArrayList<> ();
		String jsonMap = ParquetUtils.paramsAsJSON (getParams ());

		getFrom ().forEach (from -> {
			getTo ().forEach (to -> {
				String cmd = "MATCH (from:Drug), (to:Target)\n"
						+ "WHERE from." + DRUG_ID_STRING + "=\'" + from + "\'\n"
						+ "AND to." + TARGET_ID_STRING + "=\'" + to + "\'\n"
						+ "CREATE (from)-[:TARGETS"
						+ " { " 
						+ getDatasetCommandString ()
						+ (jsonMap != null ? (", " + jsonMap) : "")
						+ "} " 
						+ "]->(to)";

				commands.add (cmd.toString());
			});
		});
		return (commands);
	}

	
	public final EntityType getType () {
		return EntityType.MechanismOfAction;
	}

	@Override
	public boolean filter (Group data) {
		return (false);
	}
}
