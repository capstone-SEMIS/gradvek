package com.semis.gradvek.springdb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.semis.gradvek.csv.CsvFile;
import com.semis.gradvek.csv.CsvService;
import com.semis.gradvek.cytoscape.CytoscapeEntity;
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
import com.semis.gradvek.graphdb.DBDriver;
import com.semis.gradvek.graphdb.Neo4jDriver;
import com.semis.gradvek.graphdb.TestDBDriver;
import com.semis.gradvek.parquet.ParquetUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

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
@CrossOrigin
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

		// Create indexes up front for fast merging
		for (EntityType type : toInit) {
			mDriver.index(type);
		}

		for (EntityType type: toInit) {
			try {
				String typeString = type.toString ();
				int alreadyThere = mDriver.count (type);
				if (alreadyThere <= 0) {
					mLogger.info ("Importing " + typeString + " data");
					ParquetUtils.initEntities (mEnv, mDriver, type);
					mDriver.unique (type);
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
	 * @param event indicates that the application has initialized
	 */
	@EventListener
	public void onApplicationReadyEvent (ApplicationReadyEvent event) {
		if ("inmem".equals (mEnv.getProperty ("db.type"))) {
			// This is the test environment - load the in-memory db driver and initialize with demo data
			mDriver = new TestDBDriver();
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

	@Operation(summary = "Upload one or more entities in a comma-separated file")
	@ApiResponses(value = {
			@ApiResponse(
					responseCode = "200",
					description = "Operation completed successfully"
			)
	})
    @PostMapping(value = "/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Map<String, String>> csvPost(@RequestParam MultipartFile file, @RequestParam String baseUrl, HttpServletRequest request) {
		return csvPostProcess(file, baseUrl, request.getRequestURI());
    }

	public ResponseEntity<Map<String, String>> csvPostProcess(MultipartFile file, String baseUrl, String requestURI) {
		CsvService csvService = CsvService.getInstance();
		List<String> fileIds = csvService.put(file);
		if (fileIds.isEmpty()) {
			return ResponseEntity.badRequest().build();
		}

		Map<String, String> body = new HashMap<>();
		for (String fileId : fileIds) {
			CsvFile currentFile = csvService.get(fileId);
			URI uri = URI.create(baseUrl + requestURI + "/" + fileId);
			mDriver.loadCsv(uri.toString(), currentFile);
			body.put(fileId, currentFile.getName());
		}

		return ResponseEntity.ok(body);
	}

	@Operation(summary = "Return the content of a previously uploaded comma-separated file")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Operation completed successfully")
	})
    @GetMapping(value = "/csv/{fileId}", produces = "text/csv")
    public ResponseEntity<InputStreamResource> csvGet(@PathVariable(value = "fileId") String fileId) {
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
	@Operation(summary = "Clear out the database")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Operation completed successfully")
	})
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
	@Operation(summary = "Initialize entities (all or of the specified type) from the OpenTargets store")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Operation completed successfully")
	})
	@PostMapping (value={"/init", "/init/{type}"})
	@ResponseBody
	public ResponseEntity<Void> initType (@PathVariable (value = "type", required = false) final String typeString) throws IOException {
		if (typeString == null) {
			initFromOpenTarget ();
		} else {
			EntityType type = EntityType.valueOf (typeString);
			mDriver.index(type);
			ParquetUtils.initEntities (mEnv, mDriver, type);
			mDriver.unique (type);
		}
		return new ResponseEntity<Void> (HttpStatus.OK);
	}
	
	/**
	 * Initialize the database with the demo nodes and relationships
	 * @return
	 */
	@Operation(summary = "Initialize the database with demo data")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Operation completed successfully")
	})
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
		
		Dataset demoDataset = new Dataset ("demo", "demo entities", "hardcoded", System.currentTimeMillis ());
		demoEntities.forEach (e -> e.setDataset ("demo"));
		demoEntities.add (demoDataset);

		mDriver.add (demoEntities, false, "");
		return new ResponseEntity<Void> (HttpStatus.OK);
	}

	/**
	 * Add a single gene; the name is in the path parameter
	 * @param id
	 * @return
	 */
	@Operation(summary = "Add a single gene entity to the database")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Operation completed successfully")
	})
	@PostMapping ("/gene/{id}")
	public ResponseEntity<Void> gene (@PathVariable (value = "id") final String id) {
		mLogger.info ("Init");
		mDriver.add (new Gene (id));
		return new ResponseEntity<Void> (HttpStatus.OK);
	}

	/**
	 * List of all loaded databases
	 */
	@Operation(summary = "Return an array of all known datasets (both active and inactive)")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Operation completed successfully")
	})
	@GetMapping ("/datasets")
	@ResponseBody
	public ResponseEntity<String> datasets () {
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		List<Dataset> datasets = mDriver.getDatasets ();
		String ret = datasets.stream ().map (d -> d.toJson ()).collect (Collectors.joining (", "));
		return (new ResponseEntity<String> ("[" + ret + "]", headers, HttpStatus.OK));

	}
	
	@Operation(summary = "Modify the active status of one or more datasets")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Operation completed successfully")
	})
	@PostMapping ("/datasets")
	public ResponseEntity<Void>  enableDatasets (@RequestBody Map<String, String>[] datasets) {
		for (Map<String, String> dataset: datasets) {
			mDriver.enableDataset (dataset.get ("dataset"), Boolean.valueOf (dataset.get ("enabled")));
		}
		return new ResponseEntity<Void> (HttpStatus.OK);		
	}

	@Operation(summary = "Return an array of adverse events associated with a specific target, optionally filtered by action")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Operation completed successfully")
	})
	@GetMapping("/weight/{target}")
	public ResponseEntity<List<AdverseEventIntObj>> getAdverseEvent(@PathVariable(value = "target") final String target,
																	@RequestParam Optional<List<String>> actions) {
		List<AdverseEventIntObj> adverseEvents = mDriver.getAEByTarget(target, actions.isPresent() ? actions.get() : null);
		return ResponseEntity.ok(adverseEvents);
	}

	@Operation(summary = "Return an array of weights of adverse events associated with a specific target, optionally filtered by action")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Operation completed successfully")
	})
	@GetMapping("/weight/{target}/{ae}")
	public ResponseEntity<List<Map<String, Object>>> getWeightsTargetAe(
			@PathVariable(value = "target") final String target,
			@PathVariable(value = "ae") final String ae, @RequestParam Optional<List<String>> actions) {
		List<Map<String, Object>> results = mDriver.getWeightsByDrug(target, actions.isPresent() ? actions.get() : null, ae);
		return ResponseEntity.ok(results);
	}

	@Operation(summary = "Return an array of Cytoscape entities representing paths from a target to one or all adverse events associated with it, optionally filtered by drug and action")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Operation completed successfully")
	})
	@GetMapping(value = {"/ae/path/{target}", "/ae/path/{target}/{ae}", "/ae/path/{target}/{ae}/{drugId}"})
	public ResponseEntity<String> getPathsTargetAeDrug(@PathVariable(value = "target") final String target,
												   @PathVariable(value = "ae", required = false) final String ae,
												   @PathVariable(value = "drugId", required = false) final String drugId,
												   @RequestParam Optional<List<String>> actions) {
		List<CytoscapeEntity> entities = mDriver.getPathsTargetAeDrug(target, actions.isPresent() ? actions.get() : null, ae, drugId);

		try {
			String json = new ObjectMapper().writeValueAsString(entities);
			return ResponseEntity.ok(json);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Operation(summary = "Return an array of Cytoscape entities representing paths from a target to one or all adverse events associated with it, optionally filtered by drug and action")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Operation completed successfully")
	})
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
	@Operation(summary = "Health check")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Application is alive")
	})
	@GetMapping ("/info")
	public String home () {
		return "Hello Gradvek";
	}

	@Operation(summary = "Return an array of suggested entities in response to a hint (beginning of the name)")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Operation completed successfully")
	})
	@GetMapping("suggest/{hint}")
	public ResponseEntity<List<Map<String, String>>> getTargetSuggestions(@PathVariable(value="hint") final String hint) {
		List<Map<String, String>> suggestions = mDriver.getTargetSuggestions(hint);
		return ResponseEntity.ok(suggestions);
	}

	@Operation(summary = "Return an array of all actions in the database")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Operation completed successfully")
	})	
	@GetMapping("actions")
	public ResponseEntity<List<Map<String, Object>>> getActions() {
		List<Map<String, Object>> actions = mDriver.getActions(null);
		return ResponseEntity.ok(actions);
	}

	@Operation(summary = "Return an array of actions for the specified target")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Operation completed successfully")
	})	
	@GetMapping("actions/{target}")
	public ResponseEntity<List<Map<String, Object>>> getActions(@PathVariable(required = false) final String target) {
		List<Map<String, Object>> actions = mDriver.getActions(target);
		return ResponseEntity.ok(actions);
	}
}
