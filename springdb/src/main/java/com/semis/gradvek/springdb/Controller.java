package com.semis.gradvek.springdb;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.semis.gradvek.entity.EntityType;
import com.semis.gradvek.entity.Gene;
import com.semis.gradvek.parquet.ParquetUtils;

@RestController
public class Controller {
	private static final Logger mLogger = Logger.getLogger (Controller.class.getName ());

	@Autowired
	private Environment mEnv;

	@PostMapping ("/upload")
	@ResponseBody
	public ResponseEntity<Void> upload (@RequestBody JsonNode entityJson) {
		mLogger.info (entityJson.toString ());
		return new ResponseEntity<Void> (HttpStatus.CREATED);
	}

	@PostMapping ("/clear")
	@ResponseBody
	public ResponseEntity<Void> clear () {
		mLogger.info ("Clear");
		Neo4jDriver driver = Neo4jDriver.instance (mEnv.getProperty ("neo4j.url"), mEnv.getProperty ("neo4j.user"),
				mEnv.getProperty ("neo4j.password"));
		driver.clear ();
		return new ResponseEntity<Void> (HttpStatus.OK);
	}

	@PostMapping ("/init/targets")
	@ResponseBody
	public ResponseEntity<Void> initTargets () throws URISyntaxException, IOException {
		mLogger.info ("Clear");
		Neo4jDriver driver = Neo4jDriver.instance (mEnv.getProperty ("neo4j.url"), mEnv.getProperty ("neo4j.user"),
				mEnv.getProperty ("neo4j.password"));
		ParquetUtils.initEntities (driver, EntityType.Target);
		return new ResponseEntity<Void> (HttpStatus.OK);
	}

	@PostMapping ("/init/demo")
	@ResponseBody
	public ResponseEntity<Void> initDemo () {
		mLogger.info ("init with demo data");
		Neo4jDriver driver = Neo4jDriver.instance (mEnv.getProperty ("neo4j.url"), mEnv.getProperty ("neo4j.user"),
				mEnv.getProperty ("neo4j.password"));
		driver.run ("" + "CREATE (Acetaminophen:Drug {drugId:'Acetaminophen', chembl_code:'CHEMBL112'}) "
				+ "CREATE (AcuteHepaticFailure:AdverseEvent {adverseEventId:'Acute hepatic failure', meddraCode:'10000804'}) "
				+ "CREATE (ToxicityToVariousAgents:AdverseEvent {adverseEventId:'Toxicity to various agents', meddraCode:'10070863'}) "
				+ "CREATE "
				+ "(Acetaminophen)-[:CAUSES {count:1443, llr:4016.61, critval:522.61}]->(AcuteHepaticFailure), "
				+ "(Acetaminophen)-[:CAUSES {count:3002, llr:3957.48, critval:522.61}]->(ToxicityToVariousAgents) "
				+ "CREATE (VanilloidReceptor:Target {targetId:'Vanilloid receptor'}) "
				+ "CREATE (Cyclooxygenase:Target {targetId:'Cyclooxygenase'}) " + "CREATE "
				+ "(Acetaminophen)-[:TARGETS {action:'OPENER'}]->(VanilloidReceptor), "
				+ "(Acetaminophen)-[:TARGETS {action:'INHIBITOR'}]->(Cyclooxygenase) "
				+ "CREATE (TRPV1:Gene {geneId:'TRPV1'}) " + "CREATE (PTGS1:Gene {geneId:'PTGS1'}) "
				+ "CREATE (PTGS2:Gene {geneId:'PTGS2'}) " + "CREATE " + "(VanilloidReceptor)-[:INVOLVES]->(TRPV1), "
				+ "(Cyclooxygenase)-[:INVOLVES]->(PTGS1), " + "(Cyclooxygenase)-[:INVOLVES]->(PTGS2) "
				+ "CREATE (TRPChannels:Pathway {pathwayId:'TRP channels', pathwayCode:'R-HSA-3295583',topLevelTerm:'Transport of small molecules'}) "
				+ "CREATE (SynthesisOfPGAndTX:Pathway {pathwayId:'Synthesis of Prostaglandins (PG) and Thromboxanes (TX)', pathwayCode:'R-HSA-2162123', topLevelTerm:'Metabolism'}) "
				+ "CREATE " + "(TRPV1)-[:PARTICIPATES]->(TRPChannels), "
				+ "(PTGS1)-[:PARTICIPATES]->(SynthesisOfPGAndTX), " + "(PTGS2)-[:PARTICIPATES]->(SynthesisOfPGAndTX) ");

		return new ResponseEntity<Void> (HttpStatus.OK);
	}

	@PostMapping ("/gene/{id}")
	public ResponseEntity<Void> gene (@PathVariable (value = "id") final String id) {
		mLogger.info ("Init");
		Neo4jDriver driver = Neo4jDriver.instance (mEnv.getProperty ("neo4j.url"), mEnv.getProperty ("neo4j.user"),
				mEnv.getProperty ("neo4j.password"));
		driver.add (new Gene (id));
		return new ResponseEntity<Void> (HttpStatus.OK);
	}

	@RequestMapping ("/info")
	public String home () {
		return "Hello Gradvec";
	}
}
