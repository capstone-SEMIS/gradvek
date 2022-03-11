package com.semis.gradvek.springdb;

import org.neo4j.driver.*;

import com.semis.gradvek.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
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

	public List<String> getAllByType (String command) {
		mLogger.info(command);
		try (Session session = mDriver.session()) {
			return session.readTransaction (tx -> {
				List<String> names = new ArrayList<>();
				Result result = tx.run( command);
				while ( result.hasNext() )
				{
					names.add( result.next().get( 0 ).asString() );
				}
				return names;
			});
		}
	}
}
