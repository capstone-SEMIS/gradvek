package com.semis.gradvek.graphdb;

import com.semis.gradvek.cytoscape.CytoscapeEntity;
import org.neo4j.driver.Record;
import org.neo4j.driver.*;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.logging.Logger;
import java.util.stream.LongStream;

import static java.lang.Math.sqrt;

public class PerformanceTest {
    private final Environment env;
    private final DBDriver driver;
    private static final Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    public PerformanceTest() {
        this.env = new MockEnvironment()
                .withProperty("neo4j.user", "neo4j")
                .withProperty("neo4j.password", "gradvek")
                .withProperty("NEO4JURL", "bolt://localhost:7687");
        this.driver = Neo4jDriver.instance(this.env);
    }

    public List<Integer> run(List<String> targets) {
        List<Integer> results = new ArrayList<>();
        for (String target : targets) {
            List<CytoscapeEntity> entities = driver.getPathsTargetAeDrug(target, null, null, null);
            results.add(entities.hashCode());
        }
        return results;
    }

    public static double mean(List<Long> inputs) {
        LongStream s = inputs.stream().mapToLong(Long::longValue);
        OptionalDouble result = s.average();
        return result.isPresent() ? result.getAsDouble() : 0;
    }

    private static double stdev(List<Long> inputs, double mean) {
        double sum = 0;
        for (long input : inputs) {
            sum += ((double) input - mean) * ((double) input - mean);
        }
        return sqrt(sum / inputs.size());
    }

    public static void main(String[] args) {
        PerformanceTest test = new PerformanceTest();
        List<Long> times = new ArrayList<>();
        List<String> targets = test.getTestTargets();
        for (int i = 0; i < 40; ++i) {
            long begin = System.currentTimeMillis();
            test.run(targets);
            long end = System.currentTimeMillis();
            times.add(end - begin);
        }
        double mean = mean(times);
        double stdev = stdev(times, mean);
        System.out.println("Tests complete. Average = " + mean + " ms, stdev = " + stdev + " ms");
    }

    private List<String> getTestTargets() {
        Driver db = GraphDatabase.driver(driver.getUri(), AuthTokens.basic(
                env.getProperty("neo4j.user"), env.getProperty("neo4j.password")));
        List<String> targets = new ArrayList<>();
        try (Session session = db.session()) {
            session.readTransaction(tx -> {
                String cypher = "match p=(t:Target)-[:TARGETS]-(:Drug)-[:ASSOCIATED_WITH]-(:AdverseEvent)"
                        + " where not(t.symbol starts with 'QQ')"
                        + " return t.symbol, count(p) order by count(p) desc limit 40";
                Result result = tx.run(cypher);
                while (result.hasNext()) {
                    Record record = result.next();
                    targets.add(record.get(0).asString());
                }
                logger.info("Targets = " + String.join(", ", targets));
                return targets;
            });
        }
        return targets;
    }
}
