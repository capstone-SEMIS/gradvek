package com.semis.gradvek.springdb;

import com.fasterxml.jackson.databind.JsonNode;
import com.semis.gradvek.csv.CsvFile;
import com.semis.gradvek.csv.CsvService;
import com.semis.gradvek.entity.AdverseEvent;
import com.semis.gradvek.entity.AssociatedWith;
import com.semis.gradvek.entity.Dataset;
import com.semis.gradvek.entity.Drug;
import com.semis.gradvek.entity.Gene;
import com.semis.gradvek.entity.Involves;
import com.semis.gradvek.entity.MechanismOfAction;
import com.semis.gradvek.entity.Participates;
import com.semis.gradvek.entity.Pathway;
import com.semis.gradvek.entity.Target;
import com.semis.gradvek.entity.*;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
		// This is the test environment - load the in-memory db driver
		if ("inmem".equals (mEnv.getProperty ("db.type"))) {
			mDriver = new TestDBDriver ();
			initDemo ();
			return;
		}
		
		try {
			/**
			 * Connect to the Neo4j database; will throw ServiceUnavailableException if can't
			 */
			mDriver = Neo4jDriver.instance (mEnv);

			// init these types of entities from OpenTarget
			// new InitThread ().start (); - if needed
			if (Boolean.TRUE.equals(mEnv.getProperty("neo4j.init", Boolean.class))){
				initFromOpenTarget();
			}
		} catch (org.neo4j.driver.exceptions.ServiceUnavailableException suax) {
			mLogger.warning ("Could not connect to neo4j database - will use inmem db");
			mDriver = new TestDBDriver ();
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
        List<String> fileIds = csvService.put(file);

		Map<String, String> body = new HashMap<>();
		for (String fileId : fileIds) {
			CsvFile currentFile = csvService.get(fileId);
			URI uri = URI.create(baseUrl + request.getRequestURI() + "/" + fileId);
			mDriver.loadCsv(uri.toString(), currentFile);
			body.put(fileId, currentFile.getName());
		}

        return ResponseEntity.ok(body);
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
		List<Entity> demoEntities = new ArrayList<> ();
		demoEntities.add (new Drug ("Acetaminophen", "CHEMBL112"));
		demoEntities.add (new AdverseEvent ("AcuteHepaticFailure", "Acute hepatic failure", "10000804"));
		demoEntities.add (new AdverseEvent ("ToxicityToVariousAgents", "Toxicity to various agents", "10070863"));
		demoEntities.add (new AssociatedWith ("CHEMBL112", "10000804", Map.of("llr", "4016.61", "critval", "522.61")));
		demoEntities.add (new AssociatedWith ("CHEMBL112", "10070863", Map.of("llr", "3957.48", "critval", "522.61")));
		demoEntities.add (new Target ("Vanilloid Receptor", "ENST00000310522", "VanilloidReceptor"));
		demoEntities.add (new Target ("Cyclooxygenase", "ENSG00000073756", "Cyclooxygenase"));
		demoEntities.add (new MechanismOfAction (
				Collections.singletonList ("CHEMBL112"),
				Collections.singletonList ("ENST00000310522"),
				Map.of ("action", "OPENER")));
		demoEntities.add (new MechanismOfAction (
				Collections.singletonList ("CHEMBL112"),
				Collections.singletonList ("ENSG00000073756"),
				Map.of ("action", "INHIBITOR")));
		demoEntities.add (new Gene ("TRPV1"));
		demoEntities.add (new Gene ("PTGS1"));
		demoEntities.add (new Gene ("PTGS2"));
		demoEntities.add ((new Involves (
				Collections.singletonList ("ENST00000310522"),
				Collections.singletonList ("TRPV1"), null)));
		demoEntities.add ((new Involves (
				Collections.singletonList ("ENSG00000073756"),
				List.of ("PTGS1", "PTGS2"), null)));
		demoEntities.add (new Pathway ("TRP channels", "R-HSA-3295583", "Transport of small molecules"));
		demoEntities.add (new Pathway ("Synthesis of Prostaglandins (PG) and Thromboxanes (TX)", "R-HSA-2162123", "Metabolism"));
		demoEntities.add (new Participates (
				Collections.singletonList ("ENST00000310522"),
				Collections.singletonList ("R-HSA-3295583"), null));
		demoEntities.add (new Participates (
				Collections.singletonList ("ENSG00000073756"),
				Collections.singletonList ("R-HSA-2162123"), null));

		mDriver.add (demoEntities, false);
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
		List<Dataset> datasets = mDriver.getDatasets ();
		String ret = datasets.stream ().map (d -> d.toJson ()).collect (Collectors.joining (", "));
		return (new ResponseEntity<String> ("[" + ret + "]", headers, HttpStatus.OK));

	}
	
	@PostMapping ("databases/{dataset}")
	public ResponseEntity<Void>  enableDataset (@PathVariable (value = "dataset") final String id) {
		return new ResponseEntity<Void> (HttpStatus.OK);		
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
