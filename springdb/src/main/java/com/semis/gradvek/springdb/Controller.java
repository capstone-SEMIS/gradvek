package com.semis.gradvek.springdb;

import com.fasterxml.jackson.databind.JsonNode;
import com.semis.gradvek.csv.CsvFile;
import com.semis.gradvek.csv.CsvService;
import com.semis.gradvek.entity.EntityType;
import com.semis.gradvek.entity.Gene;
import com.semis.gradvek.parquet.ParquetUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import static com.semis.gradvek.entity.EntityType.*;

/**
 * The Spring controller representing the REST API to the driver abstraction over Neo4j database
 *
 * @author ymachkasov, ychen
 */
@RestController
public class Controller {
	private static final Logger mLogger = Logger.getLogger (Controller.class.getName ());

	@Autowired
	private Environment mEnv;

	private DBDriver mDriver;
	
	private final class InitThread extends Thread {
		@Override
		public void run () {
			initFromOpenTarget ();
		}
	}

	private final void initFromOpenTarget () {
		EntityType[] toInit = {
				Target,
				Pathway,
				Drug,
				AdverseEvent,
				AssociatedWith,
				MechanismOfAction,
				Participates
		};
		
		for (EntityType type: toInit) {
			try {
				String typeString = type.toString ();
				int alreadyThere = mDriver.count (type);
				if (alreadyThere <= 0) {
					mLogger.info ("Importing " + typeString + " data");
					ParquetUtils.initEntities (mEnv, mDriver, type);
					mDriver.unique (type);
					mDriver.index (type);
					mLogger.info ("Imported " +  mDriver.count (type) + " entities of type " + typeString);
				} else {
					mLogger.info ("Database contains " + alreadyThere + " entries of type " + typeString + ", skipping import");

				}
			} catch (IOException iox) {
			}
		}
	}

	/**
	 * Initialization; invoked when the application has completed startup
	 * @param event
	 */
	@EventListener
	public void onApplicationReadyEvent (ApplicationReadyEvent event) {
		try {
			/**
			 * Connect to the Neo4j database; will throw ServiceUnavailableException if can't
			 */
			mDriver = Neo4jDriver.instance (mEnv);

			// init these types of entities from OpenTarget
			// new InitThread ().start (); - if needed
			initFromOpenTarget ();
		} catch (org.neo4j.driver.exceptions.ServiceUnavailableException suax) {
			mLogger.warning ("Could not connect to neo4j database - is this testing mode?");
		}

	}

	/**
	 * Uploading a single entity in JSON format from the body of the request
	 * @param entityJson
	 * @return
	 */
	@PostMapping ("/upload")
	@ResponseBody
	public ResponseEntity<Void> upload (@RequestBody JsonNode entityJson) {
		mLogger.info (entityJson.toString ());
		// TODO
		return new ResponseEntity<Void> (HttpStatus.CREATED);
	}

    @PostMapping(value = "/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Map<String, String>> csvPost(@RequestParam MultipartFile file, @RequestParam String baseUrl, HttpServletRequest request) {
		CsvService csvService = CsvService.getInstance();
        String fileId = csvService.put(file);

		List<String> columns = csvService.get(fileId).getColumns();
        URI uri = URI.create(baseUrl + request.getRequestURI() + "/" + fileId);
        mDriver.loadCsv(uri.toString(), columns);

		Map<String, String> body = new HashMap<>();
		body.put("name", file.getOriginalFilename());

        return ResponseEntity.created(uri).body(body);
    }

    @GetMapping(value = "/csv/{fileId}", produces = "text/csv")
    public ResponseEntity<InputStreamResource> csvGet(@PathVariable(value = "fileId") String fileId) throws IOException {
        CsvFile file = CsvService.getInstance().get(fileId);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", file.getName()));
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .body(new InputStreamResource(file.getInputStream()));
    }

	/**
	 * Clean out the entire database
	 * @return
	 */
	@PostMapping ("/clear")
	@ResponseBody
	public ResponseEntity<Void> clear () {
		mLogger.info ("Clear");
		mDriver.clear ();
		return new ResponseEntity<Void> (HttpStatus.OK);
	}

	/**
	 * Initialize the database with the entities or relationships of the specified type
	 * @return
	 */
	@PostMapping ("/init/{type}")
	@ResponseBody
	public ResponseEntity<Void> initType (@PathVariable (value = "type") final String typeString) throws IOException {
		EntityType type = EntityType.valueOf (typeString);
		ParquetUtils.initEntities (mEnv, mDriver, type);
		mDriver.unique (type);
		mDriver.index (type);
		return new ResponseEntity<Void> (HttpStatus.OK);
	}
	
	/**
	 * Initialize the database with the demo nodes and relationships
	 * @return
	 */
	@PostMapping ("/init/demo")
	@ResponseBody
	public ResponseEntity<Void> initDemo () {
		mLogger.info ("init with demo data");
		mDriver.write ("" + "CREATE (Acetaminophen:Drug {drugId:'Acetaminophen', chembl_code:'CHEMBL112'}) "
				+ "CREATE (AcuteHepaticFailure:AdverseEvent {adverseEventId:'Acute hepatic failure', meddraCode:'10000804'}) "
				+ "CREATE (ToxicityToVariousAgents:AdverseEvent {adverseEventId:'Toxicity to various agents', meddraCode:'10070863'}) "
				+ "CREATE (Acetaminophen)-[:ASSOCIATED_WITH {count:1443, llr:4016.61, critval:522.61}]->(AcuteHepaticFailure), "
				+ "(Acetaminophen)-[:ASSOCIATED_WITH {count:3002, llr:3957.48, critval:522.61}]->(ToxicityToVariousAgents) "
				+ "CREATE (VanilloidReceptor:Target {targetId:'Vanilloid receptor'}) "
				+ "CREATE (Cyclooxygenase:Target {targetId:'Cyclooxygenase'}) "
				+ "CREATE (Acetaminophen)-[:TARGETS {action:'OPENER'}]->(VanilloidReceptor), "
				+ "(Acetaminophen)-[:TARGETS {action:'INHIBITOR'}]->(Cyclooxygenase) "
				+ "CREATE (TRPV1:Gene {geneId:'TRPV1'}) " + "CREATE (PTGS1:Gene {geneId:'PTGS1'}) "
				+ "CREATE (PTGS2:Gene {geneId:'PTGS2'}) " + "CREATE (VanilloidReceptor)-[:INVOLVES]->(TRPV1), "
				+ "(Cyclooxygenase)-[:INVOLVES]->(PTGS1), " + "(Cyclooxygenase)-[:INVOLVES]->(PTGS2) "
				+ "CREATE (TRPChannels:Pathway {pathwayId:'TRP channels', pathwayCode:'R-HSA-3295583',topLevelTerm:'Transport of small molecules'}) "
				+ "CREATE (SynthesisOfPGAndTX:Pathway {pathwayId:'Synthesis of Prostaglandins (PG) and Thromboxanes (TX)', pathwayCode:'R-HSA-2162123', topLevelTerm:'Metabolism'}) "
				+ "CREATE (TRPV1)-[:PARTICIPATES]->(TRPChannels), " + "(PTGS1)-[:PARTICIPATES]->(SynthesisOfPGAndTX), "
				+ "(PTGS2)-[:PARTICIPATES]->(SynthesisOfPGAndTX) ");

		return new ResponseEntity<Void> (HttpStatus.OK);
	}

	/**
	 * Add a single gene; the name is in the path parameter
	 * @param id
	 * @return
	 */
	@PostMapping ("/gene/{id}")
	public ResponseEntity<Void> gene (@PathVariable (value = "id") final String id) {
		mLogger.info ("Init");
		mDriver.add (new Gene (id));
		return new ResponseEntity<Void> (HttpStatus.OK);
	}

	/**
	 * List of all loaded databases
	 * TODO
	 */
	@GetMapping ("/databases")
	@ResponseBody
	public ResponseEntity<String> databases () {
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return (new ResponseEntity<String> (
				"{datasets:["
				+ "{\"dataset\":\"Targets\","
				+ " \"description\":\"Core annotation for targets\","
				+ " \"source\":\"ftp://ftp.ebi.ac.uk/pub/databases/opentargets/platform/latest/output/etl/parquet/targets\","
				+ " \"timestamp\":1647831895},"
				+ "{\"dataset\":\"Drugs\","
				+ " \"description\":\"Core annotation for drugs\","
				+ " \"source\":\"ftp://ftp.ebi.ac.uk/pub/databases/opentargets/platform/latest/output/etl/parquet/molecule\","
				+ " timestamp:1647831895},"
				+ "{\"dataset\":\"Adverse Events\","
				+ " \"description\":\"Significant adverse events for drug molecules\","
				+ " \"source\":\"ftp://ftp.ebi.ac.uk/pub/databases/opentargets/platform/latest/output/etl/parquet/fda/significantAdverseDrugReactions\","
				+ " \"timestamp\":1647831895}"
				+ "]}",
				headers, HttpStatus.OK
				)
		);

	}

	@GetMapping("/ae/{target}")
	public ResponseEntity<List<AdverseEventIntObj>> getAdverseEvent(@PathVariable(value="target") final String target) {
		List<AdverseEventIntObj> adverseEvents = mDriver.getAEByTarget(target);
		return ResponseEntity.ok(adverseEvents);
	}

	@GetMapping("count/{type}")
	@ResponseBody
	public ResponseEntity<Integer> count (@PathVariable (value = "type") final String typeString) throws IOException {
		EntityType type = EntityType.valueOf (typeString);
		int numEntities = mDriver.count (type);
		return ResponseEntity.ok(numEntities);
	}
	
	
	/**
	 * Health check
	 * @return
	 */
	@GetMapping ("/info")
	public String home () {
		return "Hello Gradvek";
	}
}
