package com.semis.gradvek.springdb;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class SpringdbApplication {
	private static final Logger mLogger = Logger.getLogger (SpringApplication.class.getName ());
	public static void main (String[] args) throws Exception {
		SpringApplication.run (SpringdbApplication.class, args);
	}

}
