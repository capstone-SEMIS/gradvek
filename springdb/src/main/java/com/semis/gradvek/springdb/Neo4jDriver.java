package com.semis.gradvek.springdb;

import com.semis.gradvek.entity.AdverseEvent;

import com.semis.gradvek.entity.Entity;
import com.semis.gradvek.entity.EntityType;
import org.neo4j.driver.Record;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

/**
 * The abstraction of the access to the Neo4j database, delegating methods to the Cypher queries
 * @author ymachkasov, ychen
 *
 */
public class Neo4jDriver {
	private static final Logger mLogger = Logger.getLogger (Neo4jDriver.class.getName ());

	private final Driver mDriver;

	private Neo4jDriver (String NeoURI, String user, String password) {
		mDriver = GraphDatabase.driver (NeoURI, AuthTokens.basic (user, password));
	}

	/**
	 * Map of singleton instances keyed by access URI
	 */
	private static final Map<String, Neo4jDriver> mInstances = new HashMap<> ();

	/**
	 * Retrieves a singleton tied to the specified URI
	 * @param uri Neo4j URI
	 * @param user user name
	 * @param password user password
	 * @return the singleton driver instance
	 */
	public static Neo4jDriver instance (String uri, String user, String password) {
		String uriOverride = System.getenv("NEO4JURL");
		if (uriOverride != null) {
			uri = uriOverride;
		}
    	Neo4jDriver ret = mInstances.getOrDefault(uri, null);
    	if (ret == null) {
    		ret = new Neo4jDriver(uri, user, password);
    		mInstances.put(uri, ret);
    	}
    	
    	return (ret);
    }

	/**
	 * Performs the command to add this entity to the database
	 * @param entity
	 */
	public void add (Entity entity) {
		write (entity.addCommand ());
	}

	/**
	 * Performs the command to add this list of entities to the database
	 * @param entity
	 */
	public void add (List<Entity> entities) {
		String cmd = entities.stream ().map (e -> e.addCommand ()).collect (Collectors.joining ("\n "));
		write (cmd);
	}

	/**
	 * Clears the database
	 */
	public void clear () {
		write ("MATCH (n) DETACH DELETE n");
	}

	/**
	 * Executes the command in write mode
	 * @param command
	 */
	public void write (String command) {
		mLogger.info (command);
		if (command != null && !command.isEmpty ()) {
			try (Session session = mDriver.session ()) {
				session.writeTransaction (tx -> {
					tx.run (command);
					return "";
				});
			}
		}
	}

	/**
	 * Counts the entities of the given type
	 * @param type the entity type for the query
	 * @return the number of entities of this type in the database
	 */
	public int count (EntityType type) {
		mLogger.info ("Counting " + type);
		try (Session session = mDriver.session ()) {
			return session.readTransaction (tx -> {
				Result result = tx.run (EntityType.toCountString (type));
				return (result.next ().get (0).asInt ());
			});
		}

	}

	/**
	 * Creates an index on entries of the specified type, if this type supports indexing
	 * and the index does not yet exist
	 * @param type the type of the entities to index
	 */
	public void index (EntityType type) {
		String indexField = type.getIndexField ();
		if (indexField != null) {
			mLogger.info ("Indexing " + type + " on " + indexField);
			String typeString = type.toString ();
			try (Session session = mDriver.session ()) {
				session.writeTransaction (tx -> {
					tx.run (
						"CREATE INDEX " + typeString
						+ "Index IF NOT EXISTS FOR (n:" + typeString + ") ON (n." + indexField
						+ ")"
					);
					return ("");
				});
			}
		} else {
			mLogger.info ("" + type + " does not support indexing");
		}
	}

	public List<String> getAllByType (String command) {
		mLogger.info (command);
		try (Session session = mDriver.session ()) {
			return session.readTransaction (tx -> {
				List<String> names = new ArrayList<> ();
				Result result = tx.run (command);
				while (result.hasNext ()) {
					names.add (result.next ().get (0).asString ());
				}
				return names;
			});
		}
	}

	public List<AdverseEvent> getAEByTarget (String target) {
		mLogger.info("Getting adverse event by target " +target);
		try (Session session = mDriver.session()) {
			return session.readTransaction (tx -> {
				Result result = tx.run("MATCH ((Target{targetId:'" +target+ "'})-[:TARGETS]-(Drug)-[causes:CAUSES]-(AdverseEvent)) RETURN DISTINCT AdverseEvent.adverseEventId, AdverseEvent.meddraCode, causes.llr ORDER BY causes.llr DESC");
				List<AdverseEvent> finalMap = new LinkedList<>();
				while ( result.hasNext() ) {
					Record record = result.next();
					String name = record.fields().get(0).value().asString();
					String id = record.fields().get(0).value().asString().replace(' ', '_');
					String code = record.fields().get(1).value().asString();
					AdverseEvent ae = new AdverseEvent(name, id, code);
					ae.setLlr(record.fields().get(2).value().asDouble());
					finalMap.add(ae);
				}
				return finalMap;
			});
		}
	}

	public void loadCsv(String url) {
		// TODO use apoc to set labels
		// https://community.neo4j.com/t/get-node-label-name-from-csv-file/41994
		// https://neo4j.com/labs/apoc/4.0/installation/#docker
		// or set labels using Java enumeration
		// https://stackoverflow.com/questions/26536573/neo4j-how-to-set-label-with-property-value
		String command = String.format(
				"LOAD CSV FROM '%s' AS line "
						+ "  CALL { "
						+ "  WITH line "
						+ "  CREATE (:IMPORTED {label: line[0], id: line[1], name: line[2]}) "
						+ "} IN TRANSACTIONS",
				url
		);
		mLogger.info(command);
		try (Session session = mDriver.session()) {
			session.run(command);
		}
	}
}
