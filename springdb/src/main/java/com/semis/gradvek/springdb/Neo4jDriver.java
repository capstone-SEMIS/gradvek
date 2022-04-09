package com.semis.gradvek.springdb;

import com.semis.gradvek.csv.CsvFile;
import com.semis.gradvek.cytoscape.CytoscapeEntity;
import com.semis.gradvek.cytoscape.Node;
import com.semis.gradvek.cytoscape.Relationship;
import com.semis.gradvek.entity.*;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.springframework.core.env.Environment;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.neo4j.driver.types.Path;

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
                String cmd = "match n=(e:AdverseEvent)-[c:ASSOCIATED_WITH]-(:Drug)-[:TARGETS]-(:Target {symbol:'"
						+ target + "'}) return e, sum(toFloat(c.llr)) order by sum(toFloat(c.llr)) desc";
				Result result = tx.run (cmd);
                List<AdverseEventIntObj> finalMap = new LinkedList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    String name = record.fields().get(0).value().asEntity().get("adverseEventId").asString();
                    String id = record.fields().get(0).value().asEntity().get("adverseEventId").asString();
                    String code = record.fields().get(0).value().asEntity().get("meddraCode").asString();

                    AdverseEventIntObj ae = new AdverseEventIntObj(name, name, code);
                    ae.setLlr(record.fields().get(1).value().asDouble());
                    finalMap.add (ae);
                }
                return finalMap;
            });
        }
    }


	public List<CytoscapeEntity> getAEPathByTarget (String target) {
		mLogger.info ("Getting adverse event paths by target " + target);
		try (Session session = mDriver.session ()) {
			return session.readTransaction (tx -> {
				String cmd = "match n=(e:AdverseEvent)-[c:ASSOCIATED_WITH]-(:Drug)-[:TARGETS]-(:Target {symbol:'"
						+ target + "'}) return n, sum(toFloat(c.llr)) order by sum(toFloat(c.llr)) desc limit 10";
				Result result = tx.run (cmd);
				Map<Long, CytoscapeEntity> entitiesInvolved = new HashMap<>();
				while (result.hasNext ()) {
					Record record = result.next();
					Path path = record.fields().get(0).value().asPath();
					path.nodes().forEach( node -> {
						if (!entitiesInvolved.containsKey(node.id())) {
                            Map<String, String> dataMap = new HashMap<>();
							if (node.hasLabel("AdverseEvent")) {

                                dataMap.put("id", node.asMap().get("adverseEventId").toString());
                                dataMap.put("adverseEventId", node.asMap().get("adverseEventId").toString());
                                dataMap.put("meddraCode", node.asMap().get("meddraCode").toString());

								CytoscapeEntity entity = new Node(node.asMap().get("adverseEventId").toString(),"adverse event", dataMap);
								entitiesInvolved.put(node.id(), entity);
							} else if (node.hasLabel("Drug")) {

                                dataMap.put("id", node.asMap().get("drugId").toString());
                                dataMap.put("drugId", node.asMap().get("drugId").toString());
                                dataMap.put("chembl_code", node.asMap().get("chembl_code").toString());

                                CytoscapeEntity entity = new Node(node.asMap().get("drugId").toString(), "drug", dataMap);
								entitiesInvolved.put(node.id(), entity);
							} else if (node.hasLabel("Target")) {

                                dataMap.put("id", node.asMap().get("symbol").toString());
                                dataMap.put("targetId", node.asMap().get("targetId").toString());
                                dataMap.put("name", node.asMap().get("name").toString());
                                dataMap.put("symbol", node.asMap().get("symbol").toString());

                                CytoscapeEntity entity = new Node(node.asMap().get("symbol").toString(), "target", dataMap);
                                entitiesInvolved.put(node.id(), entity);
							} else if (node.hasLabel("Pathway")) {
                                dataMap.put("id", node.asMap().get("pathwayCode").toString());
                                dataMap.put("pathwayId", node.asMap().get("pathwayId").toString());
                                dataMap.put("name", node.asMap().get("pathwayCode").toString());
                                dataMap.put("term", node.asMap().get("topLevelTerm").toString());

                                CytoscapeEntity entity = new Node(node.asMap().get("pathwayCode").toString(), "pathway", dataMap);
								entitiesInvolved.put(node.id(), entity);
							}
						}
					});

					path.relationships().forEach(relationship -> {
                        if (!entitiesInvolved.containsKey(relationship.id())) {
                            Map<String, String> relationshipMap = new HashMap<>();
                            relationship.asMap().forEach((k,v) -> relationshipMap.put(k, v.toString())); // Change type of Value from Object to String
                            CytoscapeEntity entity = null;
                            if (relationship.hasType("ASSOCIATED_WITH")) {
                                Node drug = (Node) entitiesInvolved.get(relationship.startNodeId());
                                Node ae = (Node) entitiesInvolved.get(relationship.endNodeId());
                                ae.getData().put("llr", relationshipMap.get("llr"));
                                relationshipMap.put("id", String.valueOf(relationship.id()));
                                relationshipMap.put("source", drug.getId().toString());
                                relationshipMap.put("target", ae.getId().toString());
                                relationshipMap.put("arrow", "vee");
                                relationshipMap.put("action", relationship.type());

                                entity = new Relationship(String.valueOf(relationship.id()), relationshipMap);

                            } else if (relationship.hasType("TARGETS")) {
                                Node relatedDrug = (Node) entitiesInvolved.get(relationship.startNodeId());
                                Node relatedTarget = (Node) entitiesInvolved.get(relationship.endNodeId());

                                relationshipMap.put("id", String.valueOf(relationship.id()));
                                relationshipMap.put("source", relatedDrug.getId().toString());
                                relationshipMap.put("target", relatedTarget.getId().toString());
                                relationshipMap.put("arrow", "vee");
                                relationshipMap.put("action", relationship.type());

                                entity = new Relationship(String.valueOf(relationship.id()), "drug_target", relationshipMap);
                            }
                            entitiesInvolved.put(relationship.id(), entity);
                        }
					});
                }
                return new ArrayList<>(entitiesInvolved.values());
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

        // the id of this entity's dataset is the file name
        long stopTime = System.currentTimeMillis();
        add (new Dataset (url.substring (url.lastIndexOf ('/') + 1), csvFile.getColumns().get (0), url, stopTime));
        mLogger.info("CSV " + csvFile.getName() + " loaded in " + (stopTime - startTime) / 1000.0 + " seconds");
    }

    public static String loadCsvCommand(String url, CsvFile csvFile) {
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
        // add dataset reference
		propBuilder.append (", dataset: '" + url.substring (url.lastIndexOf ('/') + 1) + "'");
		
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
