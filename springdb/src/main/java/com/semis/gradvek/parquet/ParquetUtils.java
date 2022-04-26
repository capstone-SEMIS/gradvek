package com.semis.gradvek.parquet;

import com.semis.gradvek.entity.Dataset;
import com.semis.gradvek.entity.EntityType;
import com.semis.gradvek.graphdb.DBDriver;
import com.semis.gradvek.springdb.Importer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.parquet.example.data.Group;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ParquetUtils {
	private static final Logger mLogger = Logger.getLogger (ParquetUtils.class.getName ());
	
	private static class OpenTargetsSource {
		private final String mPath;
		private final String mDescription;
		
		OpenTargetsSource (String path, String description) {
			mPath = path;
			mDescription = description;
		}
	}
	
	// maps the entity type to the name of the folder where the parquet files for it live
	private static final Map<EntityType, OpenTargetsSource> mEntityTypeToSource = Map.of (
			EntityType.Target, new OpenTargetsSource ("targets", "OpenTargets annotations for drug targets"),
			EntityType.AdverseEvent, new OpenTargetsSource ("fda/significantAdverseDrugReactions", "FAERS annotations for adverse events"),
			EntityType.Drug, new OpenTargetsSource ("molecule", "OpenTargets annotations for drug molecules"),
			EntityType.MechanismOfAction, new OpenTargetsSource ("mechanismOfAction", "OpenTargets annotations for mechanisms of action for drug molecules"),
			EntityType.Action, new OpenTargetsSource ("", "OpenTargets data on drug association with actions on targets"), // gets created with mechanisms
			EntityType.AssociatedWith, new OpenTargetsSource ("fda/significantAdverseDrugReactions", "FAERS data on association of adverse events with drug molecules"),
			EntityType.Pathway, new OpenTargetsSource ("", "OpenTargets annotations for pathways"), // gets created with targets
			EntityType.Participates, new OpenTargetsSource ("targets", "OpenTargets data on participation of drug targets in pathways")
	);

	/**
	 * Collects all fields with the specified keys from the supplied data into a map
	 * @param data Parquet data
	 * @param keys a vararg list of string keys
	 * @return the map
	 */
	public static Map<String, String> extractParams (Group data, String... keys) {
		Map<String, String> ret = new HashMap<> ();
		for (String key: keys) {
			String value = data.getValueToString (data.getType ().getFieldIndex (key), 0);
			if (value != null) {
				ret.put (key, value);
			}
		}
		
		return (ret);
	}
	
	public static List<String> extractStringList (Group data, String key) {
		try {
			Group list = data.getGroup (key, 0);
			int numInList = list.getFieldRepetitionCount (0);
			List<String> ret = new ArrayList<> (numInList);
			for (int iKey = 0; iKey < numInList; iKey ++) {
				ret.add (list.getGroup (0, iKey).getString (0, 0));
			}
			return (ret);
		} catch (RuntimeException rx) {
			return (Collections.emptyList ());
		}
	}
	
	public static List<Group> extractGroupList (Group data, String key) {
		try {
			Group list = data.getGroup (key, 0);
			int numInList = list.getFieldRepetitionCount (0);
			List<Group> ret = new ArrayList<> (numInList);
			for (int iKey = 0; iKey < numInList; iKey ++) {
				ret.add (list.getGroup (0, iKey).getGroup (0, 0));
			}
			return (ret);
		} catch (RuntimeException rx) {
			return (Collections.emptyList ());
		}
	}
	
	/**
	 * Constructs a single valid JSON string from all entries in the map
	 * The curly braces are not included
	 * @param params the map to be transformed 
	 * @return the JSON representation
	 */
	public static String paramsAsJSON (Map<String, String> params) {
		if (params == null) {
			return (null);
		}
		
		String ret = params.keySet ().stream ()
			.map (key -> key + ":\"" + StringEscapeUtils.escapeEcmaScript (params.get (key)) + "\"")
			.collect (Collectors.joining(", "));
		return (ret);
	}
	
	public static Parquet readResource (Resource r) throws IOException {
		Parquet parquet = null;
		File resourceFile = null;
		File tmpFile = null;
		
		// to use the Hadoop parquet standalone utils, we need to have a file, not a stream
		if (r.isFile ()) {
			// this is a filesystem file
			resourceFile = r.getFile ();
		} else if (r.isReadable ()) {
			// looks like we're inside the jar; need to suck out the resource into a temp file
			InputStream is = r.getInputStream ();
			tmpFile = File.createTempFile ("prq", null);
			FileUtils.copyInputStreamToFile (is, tmpFile);
			is.close ();
			resourceFile = tmpFile;
		}

		// do the reading
		try {
			parquet = Reader.read (Paths.get (resourceFile.toURI ()));
			mLogger.info ("Finished reading " + resourceFile.getName ());
		} catch (IOException iox) {
			mLogger.severe (iox.toString ());
		}

		if (tmpFile != null) {
			tmpFile.delete ();
		}

		return (parquet);
	}
	
	private static final void verifyCount (DBDriver driver, EntityType type, int expected) {
		if (expected > 0 && type != null) {
			int actual = driver.count (type);
			if (actual == expected) {
				mLogger.info ("Verified the expected number of " + expected + " entities of type " + type);
			} else {
				mLogger.severe ("Expected to find " + expected + " entities of type " + type + ", but found " + actual);
			}
		}
	}
	
	private static final String getVersionString () {
		String version = "";
		
		Resource[] resources = null;
		
		try {
			ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver ();
			resources = resourcePatternResolver.getResources ("conf/*platform_parquet.conf");
		} catch (IOException iox) {
			mLogger.warning ("OpenTarget version config not found in local environment");
		}
		
		
		if (resources != null && resources.length > 0 & resources[0].isReadable ()) {
			try (InputStream is = resources[0].getInputStream ()) {
				Properties props = new Properties (); 
				props.load (is); // it's not a real props file, but this will work
				version = props.getProperty ("data_version", "").replace ("\"", "");
			} catch (IOException iox) {
				mLogger.warning ("OpenTarget version config not readable in local environment");
			}
		}
		
		return ("OpenTargets." + version);
	}
	
	/**
	 * Uses the driver to connect to the database and initialize it with entities of the required type
	 * from the OpenTarget database
	 * @param env application environment
	 * @param driver the Neo4j driver abstraction
	 * @param type the entity type to read
	 * @return an empty HTTP response
	 * @throws IOException if cannot read file/resource
	 * @throws MalformedURLException
	 */
	public static ResponseEntity<Void> initEntities (Environment env, DBDriver driver, EntityType type)
			throws IOException, MalformedURLException {
		OpenTargetsSource source = mEntityTypeToSource.get (type);
		String path = source.mPath;
		if (path == null || path.isEmpty ()) {
			return new ResponseEntity<Void> (HttpStatus.CREATED);
		}
		
		Importer importer = new Importer (driver);
		
		Resource[] resources = null;
		
		String version = getVersionString ();
				
		try {
			ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver ();
			resources = resourcePatternResolver.getResources (path + "/*.parquet");
		} catch (FileNotFoundException fnfx) {
			mLogger.warning ("No files for type " + type + " found in local environment");
		}

		if ((resources == null || resources.length <= 0) && env.getProperty ("opentarget.server") != null) {
			// no local files - try to get from the website if configured
			FTPClient client = new FTPClient ();
			client.connect (env.getProperty ("opentarget.server"));
		    client.enterRemotePassiveMode();
			client.login("anonymous", "anonymous");
			Path entityPath = Paths.get (env.getProperty ("opentarget.path"), path);
			URL entityURL = entityPath.toUri ().toURL ();
			FTPFile[] files = client.listFiles (entityPath.toString (), file -> {
				return (file.getName ().endsWith ("parquet"));
			});
			// TODO: download
//			Arrays.stream (files).map(f -> new URL (entityURL, f.getName ()));
		}

		if (resources == null || resources.length <= 0) {
			mLogger.warning ("Could not load data for type " + type);
			return new ResponseEntity<Void> (HttpStatus.NO_CONTENT);
		}

		mLogger.info("Loading resources for type " + type.name());
		long startTypeTime = System.currentTimeMillis();
		int importedCount = 0;
		for (Resource r : resources) {
			mLogger.info("Loading " + type.name() + " from resource " + r.getFilename());
			long startResourceTime = System.currentTimeMillis();
			try {
				Parquet parquet = readResource (r);
				if (parquet != null) {
					importedCount += importer.importParquet (parquet, type, version);
				}
			} catch (IOException iox) {
				mLogger.severe (iox.toString ());
			}
			double resourceDuration = (System.currentTimeMillis() - startResourceTime) / 1000.0;
			mLogger.info("Done with " + r.getFilename() + " in " + resourceDuration + " seconds");
		}
		
		// If additional entities were read from this file, add them now
		EntityType addlType = importer.getAdditionalEntityType ();
		int addlImportedCount = importer.processAdditionalEntities(version);
		
		// check that the database has what we tried to put into it
		verifyCount (driver, type, importedCount);
		verifyCount (driver, addlType, addlImportedCount);
		
		// add the dataset(s); the references are already in the entities
		if (importedCount > 0) {
			driver.add (datasetFromType (type, version));
		}
		if (addlImportedCount > 0) {
			driver.add (datasetFromType (addlType, version));
		}
		
		long stopTypeTime = System.currentTimeMillis();
		mLogger.info("Done with " + type.name() + " in " + (stopTypeTime - startTypeTime) / 1000.0 + " seconds");

		return new ResponseEntity<Void> (HttpStatus.OK);
	}
	
	public final static Dataset datasetFromType (EntityType type, String dbVersion) {
		OpenTargetsSource source = mEntityTypeToSource.get (type);
		return new Dataset (dbVersion + "." + type.toString (), source.mDescription, source.mPath, System.currentTimeMillis ());
	}
}
