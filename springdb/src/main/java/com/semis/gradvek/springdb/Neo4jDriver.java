package com.semis.gradvek.springdb;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;

import com.semis.gradvek.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class Neo4jDriver {
    private final Driver mDriver;

    private Neo4jDriver (String NeoURI) {
    	mDriver = GraphDatabase.driver(NeoURI);
    }

    private static final Map<String, Neo4jDriver> mInstances = new HashMap<> ();
    public static Neo4jDriver instance (String uri) {
    	Neo4jDriver ret = mInstances.getOrDefault(uri, null);
    	if (ret == null) {
    		ret = new Neo4jDriver(uri);
    		mInstances.put(uri, ret);
    	}
    	
    	return (ret);
    }
    
    public void add (Entity entity) {
    	try (Session session = mDriver.session()) {
    		session.writeTransaction (tx -> {
    			Result result = tx.run( "CREATE " + entity.toCommand() ) ;
    			return result.single().get( 0 ).asString();
    		});
    	}
    }

}
