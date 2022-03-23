package com.semis.gradvek.springdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The main class for the Spring application providing access to the
 * Neo4j database
 * @author ymachkasov
 *
 */
@SpringBootApplication
public class SpringdbApplication {
	public static void main (String[] args) throws Exception {
		SpringApplication.run (SpringdbApplication.class, args);
	}

}
