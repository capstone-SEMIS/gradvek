package com.semis.gradvek.springdb;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

import com.semis.gradvek.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class Neo4jDriver {
	private static final Logger mLogger = Logger.getLogger(Neo4jDriver.class.getName());
    
	private final Driver mDriver;

    private Neo4jDriver (String NeoURI, String user, String password) {
    	mDriver = GraphDatabase.driver( NeoURI, AuthTokens.basic( user, password ));
    }

    private static final Map<String, Neo4jDriver> mInstances = new HashMap<> ();
    public static Neo4jDriver instance (String uri, String user, String password) {
    	Neo4jDriver ret = mInstances.getOrDefault(uri, null);
    	if (ret == null) {
    		ret = new Neo4jDriver(uri, user, password);
    		mInstances.put(uri, ret);
    	}
    	
    	return (ret);
    }
    
    public void add (Entity entity) {
    	run ("CREATE " + entity.toCommand());
    }
    
    public void clear () {
    	run ("MATCH (n) DETACH DELETE n");
    }
    
    public void run (String command) {
    	mLogger.info(command);
    	try (Session session = mDriver.session()) {
    		session.writeTransaction (tx -> {
    			tx.run(command) ;
    			return "";
    		});
    	}
    }

}
