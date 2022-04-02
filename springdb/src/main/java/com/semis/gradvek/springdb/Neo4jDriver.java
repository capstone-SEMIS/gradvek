package com.semis.gradvek.springdb;

import com.semis.gradvek.csv.CsvFile;
import com.semis.gradvek.entity.Dataset;
import com.semis.gradvek.entity.Entity;
import com.semis.gradvek.entity.EntityType;
import org.neo4j.driver.Record;
import org.neo4j.driver.*;
import org.springframework.core.env.Environment;

import java.util.*;
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
        int batchSize = mEnv.getProperty("neoCommandBatchSize", Integer.class, 0);
        if (batchSize > 0) {
            final AtomicInteger counter = new AtomicInteger(); // because lambda requires effectively final
            return cmds.stream().collect(Collectors.groupingBy(e -> counter.getAndIncrement() / batchSize)).values();
        } else {
            return (Collections.singletonList(cmds));
        }
    }

    /**
     * Performs the command to add this set of entities to the database
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
    public void loadCsv(String url, CsvFile csvFile) {
        long startTime = System.currentTimeMillis();

        String command = loadCsvCommand(url, csvFile);
        try (Session session = mDriver.session()) {
            mLogger.info(command);
            session.run(command);
        }

        long stopTime = System.currentTimeMillis();
        mLogger.info("CSV " + csvFile.getName() + " loaded in " + (stopTime - startTime) / 1000.0 + " seconds");
    }

    private static String loadCsvCommand(String url, CsvFile csvFile) {
        List<String> columns = csvFile.getColumns();

        // Properties start at column 1 for nodes, 3 for relationships
        int propStartIdx = 1;
        if (csvFile.getType().equalsIgnoreCase("Relationship")) {
            propStartIdx = 3;
        }

        // Build the property string
        StringBuilder propBuilder = new StringBuilder();
        for (int i = propStartIdx; i < columns.size(); ++i) {
            if (propBuilder.length() == 0) {
                propBuilder.append("{");
            } else {
                propBuilder.append(", ");
            }
            String prop = columns.get(i);
            propBuilder.append(prop + ": line[" + i + "]");
        }
        propBuilder.append("}");
        String properties = columns.size() > propStartIdx ? propBuilder.toString() : "";

        // Build the command string
        String commandPattern = "LOAD CSV FROM '" + url + "' AS line CALL { WITH line %s } IN TRANSACTIONS";
        String commandCore = null;
        if (csvFile.getType().equalsIgnoreCase("Relationship")) {
            String fromIdProp = columns.get(1);
            String toIdProp = columns.get(2);
            String fromIdLabel = EntityType.fromIndex(fromIdProp).name();
            String toIdLabel = EntityType.fromIndex(toIdProp).name();
            commandCore =
                    "MATCH (fromNode:" + fromIdLabel + " {" + fromIdProp + ": line[1]}),"
                            + " (toNode:" + toIdLabel + " {" + toIdProp + ": line[2]})"
                            + " CREATE (fromNode)-[:" + csvFile.getLabel() + " " + properties + "]->(toNode)";
        } else if (csvFile.getType().equalsIgnoreCase("Node")) {
            commandCore = String.format("CREATE (:%s %s)", csvFile.getLabel(), properties);
        }
        String command = String.format(commandPattern, commandCore);

        return command;
    }

	@Override
	public List<Dataset> getDatasets () {
		return (List.of (
				new Dataset (
						"Targets", "Core annotation for targets",
						"ftp://ftp.ebi.ac.uk/pub/databases/opentargets/platform/latest/output/etl/parquet/targets",
						1647831895L, false
						),
				new Dataset (
						"Drugs", "Core annotation for drugs",
						"ftp://ftp.ebi.ac.uk/pub/databases/opentargets/platform/latest/output/etl/parquet/targets",
						1647831895L, true
						),
				new Dataset (
						"AdverseEvents", "Significant adverse events for drug molecules",
						"ftp://ftp.ebi.ac.uk/pub/databases/opentargets/platform/latest/output/etl/parquet/targets",
						1647831895L, true
						)
				)
		);
	}

	@Override
	public void enableDataset (String dataset, boolean enable) {
		// TODO Auto-generated method stub
		
	}
}
