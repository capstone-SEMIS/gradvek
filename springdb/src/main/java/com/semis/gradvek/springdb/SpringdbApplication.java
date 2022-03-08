package com.semis.gradvek.springdb;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import com.semis.gradvek.entity.Entity;

@SpringBootApplication
@RestController
public class SpringdbApplication {
	private static final Logger mLogger = Logger.getLogger(SpringApplication.class.getName());
	
	@Autowired
	private Environment mEnv;
	
	@PostMapping("/upload")
	@ResponseBody
	public ResponseEntity<Void> upload(
			@RequestBody JsonNode entityJson
	)
	{
		mLogger.info(entityJson.toString());
		Entity entity = Entity.parse(entityJson);
		Neo4jDriver driver = Neo4jDriver.instance(mEnv.getProperty("neo4j.url"));
		driver.add(entity);
		return new ResponseEntity<Void>(HttpStatus.CREATED);
	}
	
	@PostMapping("init")
	@ResponseBody
	public ResponseEntity<Void> init()
	{
		mLogger.info("Init");
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping("/info")
  	public String home() {
    	return "Hello Gradvec";
  	}


	public static void main(String[] args) {
		SpringApplication.run(SpringdbApplication.class, args);
	}

}
