package edu.gradvek.springdb;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import static org.neo4j.driver.Values.parameters;

public class Neo4jDriver {
    private final Driver mDriver;

    public Neo4jDriver (String NeoURI) {
    	mDriver = GraphDatabase.driver(NeoURI);
    }
    
    public void add (Entity entity) {
    	try (Session session = mDriver.session()) {
    		String response = session.writeTransaction (tx -> {
    			return ("");
    		});
    	}
    }

}
