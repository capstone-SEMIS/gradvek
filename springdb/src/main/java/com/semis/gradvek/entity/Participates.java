package com.semis.gradvek.entity;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.parquet.example.data.Group;

import com.semis.gradvek.parquet.ParquetUtils;
import com.semis.gradvek.springdb.Importer;

/**
 * The immutable class representing a connection between
 * targets and pathways
 * @author ymachkasov
 *
 */
public class Participates extends Edges {

	public Participates (List<String> from, List<String> to, Map<String, String> params) {
		super (from, to, params);
	}

	/* target Parquet schema, the fields we use are marked with ^:
message spark_schema {
  ^optional binary id (STRING); // ensemble ID
  ^optional binary approvedSymbol (STRING);
  optional binary biotype (STRING);
...
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

	/**
	 * Constructor from Parquet data
	 * 
	 * @param data the full Parquet entity for this event
	 */
	public Participates (Importer importer, Group data) {
		super (
				Collections.singletonList (data.getString ("id", 0)),
				ParquetUtils.extractGroupList (data, "pathways").stream ()
					.map (p -> p.getString ("pathwayId", 0))
					.collect (Collectors.toList ()),
				null
			);
		setDataset ("Participates");
	}

	/**
	 * The Cypher command to create this entity
	 */
	@Override
	public List<String> addCommands () {
		List<String> commands = new ArrayList<> ();
		String jsonMap = ParquetUtils.paramsAsJSON (getParams ());

		String from = getFrom ().get (0);
		getTo ().forEach (to -> {
			String cmd = "MATCH (from:Target), (to:Pathway)\n"
					+ "WHERE from." + TARGET_ID_STRING + "=\'" + from + "\'\n"
					+ "AND to." + PATHWAY_ID_STRING + "=\'" + to + "\'\n"
					+ "CREATE (from)-[:PARTICIPATES_IN"
					+ " { dataset: \'" + getDataset () + "\' "
					+ (jsonMap != null ? (", " + jsonMap) : "")
					+ "} " 
					+ "]->(to)";

			commands.add (cmd.toString());
		});

		return (commands);
	}

	
	public final EntityType getType () {
		return EntityType.Participates;
	}
}
