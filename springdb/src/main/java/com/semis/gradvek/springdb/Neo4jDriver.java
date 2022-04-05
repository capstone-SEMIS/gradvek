package com.semis.gradvek.springdb;

import com.semis.gradvek.entity.Dataset;
import com.semis.gradvek.entity.Entity;
import com.semis.gradvek.entity.EntityType;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The abstraction of the access to the Neo4j database, delegating methods to
 * the Cypher queries
 *
 * @author ymachkasov, ychen
 */
public class Neo4jDriver implements DBDriver {
    private static final Logger mLogger = Logger.getLogger(Neo4jDriver.class.getName());

    private final Driver mDriver;
    private final Environment mEnv;
    private String mUri;

	private Neo4jDriver (Environment env, String uri) {
		mEnv = env;
		String user = env.getProperty ("neo4j.user");
		String password = env.getProperty ("neo4j.password");

        mUri = uri;
		mDriver = GraphDatabase.driver (mUri, AuthTokens.basic (user, password));
        mLogger.info("Neo4jDriver initialized with URL " + getUri());
	}

    @Override
    public String getUri() {
        return mUri;
    }

    /**
     * Map of singleton instances keyed by access URI
     */
    private static final Map<String, Neo4jDriver> mInstances = new HashMap<>();

    /**
     * Retrieves a singleton tied to the specified URI
     *
     * @param env app environment
     * @return the singleton driver instance
     */
    public static DBDriver instance(Environment env) {
        String uri = env.getProperty("NEO4JURL");

        Neo4jDriver ret = mInstances.getOrDefault(uri, null);
        if (ret == null) {
            ret = new Neo4jDriver(env, uri);
            mInstances.put(ret.getUri(), ret);
        }

        return (ret);
    }

    /**
     * Performs the command to add this entity to the database
     *
     * @param entity
     */
    @Override
    public <T extends Entity> void add(T entity) {
        entity.addCommands().forEach(c -> write(c));
    }

    /**
     * Utility to create chunks of commands of the configured size
     *
     * @param cmds
     * @return
     */
    private final Collection<List<String>> chunk(List<String> cmds) {
        int batchSize = mEnv.getProperty("neo4j.batch", Integer.class, 0);
        if (batchSize > 0) {
            final AtomicInteger counter = new AtomicInteger(); // because lambda requires effectively final
            return cmds.stream().collect(Collectors.groupingBy(e -> counter.getAndIncrement() / batchSize)).values();
        } else {
        	// no chunking
            return (Collections.singletonList(cmds));
        }
    }

    /**
     * Performs the commands to add this set of entities to the database
     */
    @Override
    public void add(List<Entity> entities, boolean canCombine) {
        List<String> cmds = entities.stream()
                .map(e -> e.addCommands()) // each entity can have several commands
                .flatMap(Collection::stream) // flatten them
                .collect(Collectors.toList());

        // separate into batches if needed
        final Collection<List<String>> batches = chunk(cmds);

        if (canCombine) {
            batches.forEach(b -> {
                // We can get the entire batch in one long command and execute it in one tx
                write(b.stream().collect(Collectors.joining("\n")));
            });
        } else {
            // all commands need to be run individually, but no reason to open/close
            // sessions for each
            try (final Session session = mDriver.session()) {
                batches.forEach(b -> {
                    try (Transaction tx = session.beginTransaction()) {
                        b.forEach(command -> tx.run(command));
                        tx.commit();
                    }
                });
            }
        }
    }

    /**
     * Clears the database
     */
    @Override
    public void clear() {
        write("MATCH (n) DETACH DELETE n");
    }

    /**
     * Executes the command in write mode
     *
     * @param command
     */
    private void write(String command) {
        mLogger.info(command);
        if (command != null && !command.isEmpty()) {
            try (Session session = mDriver.session()) {
                session.writeTransaction(tx -> {
                    tx.run(command);
                    return "";
                });
            }
        }
    }

    /**
     * Counts the entities of the given type
     *
     * @param type the entity type for the query
     * @return the number of entities of this type in the database
     */
    @Override
    public int count(EntityType type) {
        mLogger.info("Counting " + type);
        try (Session session = mDriver.session()) {
            return session.readTransaction(tx -> {
                Result result = tx.run(EntityType.toCountString(type));
                return (result.next().get(0).asInt());
            });
        }

    }

    /**
     * Creates an index on entries of the specified type, if this type supports
     * indexing and the index does not yet exist
     *
     * @param type the type of the entities to index
     */
    @Override
    public void index(EntityType type) {
        String indexField = type.getIndexField();
        if (indexField != null) {
            mLogger.info("Indexing " + type + " on " + indexField);
            try (Session session = mDriver.session()) {
                session.writeTransaction(tx -> {
                    tx.run("CREATE INDEX " + type + "Index IF NOT EXISTS FOR (n:" + type + ") ON (n." + indexField
                            + ")");
                    return ("");
                });
            }
        } else {
            mLogger.info("" + type + " does not support indexing");
        }
    }

    @Override
    public void unique(EntityType type) {
        String indexField = type.getIndexField();
        if (indexField != null) {
            mLogger.info("Uniquifying " + type + " on " + indexField);
            try (Session session = mDriver.session()) {
                session.writeTransaction(tx -> {
                    tx.run("MATCH (n:" + type + ")" + " WITH n." + indexField + " AS " + indexField
                            + " , collect(n) AS nodes WHERE size(nodes) > 1"
                            + " FOREACH (n in tail(nodes) | DELETE n)");
                    return ("");
                });
            }
        } else {
            mLogger.info("" + type + " does not support uniquifying");
        }
    }

    @Override
    public List<AdverseEventIntObj> getAEByTarget(String target) {
        mLogger.info("Getting adverse event by target " + target);
        try (Session session = mDriver.session()) {
            return session.readTransaction(tx -> {
                Result result = tx.run("MATCH ((Target{targetId:'" + target
                        + "'})-[:TARGETS]-(Drug)-[causes:\'ASSOCIATED_WITH\']-(AdverseEvent)) RETURN DISTINCT AdverseEvent.adverseEventId, AdverseEvent.meddraCode, causes.llr ORDER BY causes.llr DESC");
                List<AdverseEventIntObj> finalMap = new LinkedList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    String name = record.fields().get(0).value().asString();
                    String id = record.fields().get(0).value().asString().replace(' ', '_');
                    String code = record.fields().get(1).value().asString();
                    AdverseEventIntObj ae = new AdverseEventIntObj(name, id, code);
                    ae.setLlr(record.fields().get(2).value().asDouble());
                    finalMap.add(ae);
                }
                return finalMap;
            });
        }
    }

    @Override
    public void loadCsv(String url, List<String> columns) {
        long startTime = System.currentTimeMillis();

        if (columns.get(0).equalsIgnoreCase("Node")) {
            loadNodeCsv(url, columns);
        } else if (columns.get(0).equalsIgnoreCase("Relationship")) {
            loadRelationshipCsv(url, columns);
        }

        long stopTime = System.currentTimeMillis();
        mLogger.info("CSV loaded in " + (stopTime - startTime) / 1000.0 + " seconds");
    }

    private void loadRelationshipCsv(String url, List<String> columns) {

    }

    private void loadNodeCsv(String url, List<String> columns) {
        try (Session session = mDriver.session()) {
            session.writeTransaction(tx -> {
                tx.run("CREATE INDEX imported_label IF NOT EXISTS FOR (n:IMPORTED) ON (n.label)");
                return 1;
            });
        }

        StringBuilder properties = new StringBuilder("{label: line." + columns.get(0));
        for (int i = 1; i < columns.size(); ++i) {
            String prop = columns.get(i);
            properties.append(", " + prop + ": line." + prop);
        }
        properties.append("}");

        String command = String.format(
                "LOAD CSV WITH HEADERS FROM '%s' AS line "
                        + "  CALL { "
                        + "  WITH line "
                        + "  CREATE (:IMPORTED %s) "
                        + "} IN TRANSACTIONS",
                url, properties
        );
        mLogger.info(command);
        try (Session session = mDriver.session()) {
            session.run(command);
        }

        List<String> labels = new ArrayList<>();
        try (Session session = mDriver.session()) {
            session.readTransaction(tx -> {
                Result result = tx.run("MATCH (n:IMPORTED) RETURN DISTINCT n.label");
                while (result.hasNext()) {
                    labels.add(result.next().get(0).asString());
                }
                return labels.size();
            });
        }

        for (String label : labels) {
            String relabelCommand = "MATCH (n:IMPORTED {label:'" + label + "'}) "
                    + "SET n:" + label + " "
                    + "REMOVE n.label "
                    + "REMOVE n:IMPORTED";
            try (Session session = mDriver.session()) {
                session.writeTransaction(tx -> {
                    tx.run(relabelCommand);
                    return 1;
                });
            }
        }
    }

	@Override
	public List<Dataset> getDatasets () {
        try (Session session = mDriver.session()) {
            return session.readTransaction(tx -> {
                Result result = tx.run(
                		"MATCH (d:Dataset) RETURN d.dataset, d.description, d.source, d.timestamp, d.enabled ORDER BY d.identity DESC"
                		);
                List<Dataset> ret = new LinkedList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    Dataset d = new Dataset (
                    		record.get ("d.dataset").asString (),
                    		record.get ("d.description").asString (),
                    		record.get ("d.source").asString (),
                    		record.get ("d.timestamp").asLong ()
                    		);
                    d.setEnabled (record.get ("d.enabled", true));
                    ret.add (d);
                }
                return ret;
            });
        }
	}

//		return (List.of (
//				new Dataset (
//						"Target", "Core annotation for targets",
//						"ftp://ftp.ebi.ac.uk/pub/databases/opentargets/platform/latest/output/etl/parquet/targets",
//						1647831895L
//						),
//				new Dataset (
//						"Drug", "Core annotation for drugs",
//						"ftp://ftp.ebi.ac.uk/pub/databases/opentargets/platform/latest/output/etl/parquet/targets",
//						1647831895L
//						),
//				new Dataset (
//						"AdverseEvent", "Significant adverse events for drug molecules",
//						"ftp://ftp.ebi.ac.uk/pub/databases/opentargets/platform/latest/output/etl/parquet/targets",
//						1647831895L
//						)
//				)
//		);
//	}

	@Override
	public void enableDataset (String datasetName, boolean enable) {
        try (final Session session = mDriver.session()) {
            try (Transaction tx = session.beginTransaction()) {
                tx.run (
                		"MATCH (d:Dataset { dataset: \'"
                		+ datasetName
                		+ "\' }) SET d.enabled="
                		+ enable
                );
                tx.commit();
            }
        }
	}
}
