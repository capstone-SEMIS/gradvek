package com.semis.gradvek.entity;

import java.util.Map;

import com.semis.gradvek.parquet.ParquetUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * The immutable class representing a connection between
 * targets and genes
 * @author ymachkasov
 *
 */
public class Involves extends Edges {

	public Involves (List<String> from, List<String> to, Map<String, String> params) {
		super (from, to, params);
		setDataset ("$" + DB_VERSION_PARAM);
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
			String cmd = "MATCH (from:Target), (to:Gene)\n"
					+ "WHERE from." + TARGET_ID_STRING + "=\'" + from + "\'\n"
					+ "AND to." + GENE_ID_STRING + "=\'" + to + "\'\n"
					+ "CREATE (from)-[:INVOLVES"
					+ " { " 
					+ getDatasetCommandString ()
					+ (jsonMap != null ? (", " + jsonMap) : "")
					+ "} " 
					+ "]->(to)";

			commands.add (cmd.toString());
		});

		return (commands);
	}

	
	public final EntityType getType () {
		return EntityType.Involves;
	}
}
