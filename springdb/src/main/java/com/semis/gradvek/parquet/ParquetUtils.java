package com.semis.gradvek.parquet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

import com.semis.gradvek.entity.EntityType;
import com.semis.gradvek.springdb.DBDriver;
import com.semis.gradvek.springdb.Importer;

public class ParquetUtils {
	private static final Logger mLogger = Logger.getLogger (ParquetUtils.class.getName ());
	
	// maps the entity type to the name of the folder where the parquet files for it live
	private static final Map<EntityType, String> mEntityTypeToPath = Map.of (
			EntityType.Target, "targets",
			EntityType.AdverseEvent, "fda/significantAdverseDrugReactions",
			EntityType.Drug, "molecule",
			EntityType.MechanismOfAction, "mechanismOfAction",
			EntityType.AssociatedWith, "fda/significantAdverseDrugReactions",
			EntityType.Pathway, "" // gets created with targets
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
			return (Collections.<String>emptyList ());
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
			return (Collections.<Group>emptyList ());
		}
	}
	
	/**
	 * Constructs a single valid JSON string from all entries in the map
	 * The curly braces are not included
	 * @param params the map to be transformed 
	 * @return the JSON representation
	 */
	public static String paramsAsJSON (Map<String, String> params) {
		String ret = params.keySet ().stream ()
			.map (key -> key + ":" + StringEscapeUtils.escapeEcmaScript (params.get (key)))
			.collect (Collectors.joining(", "));
		return (ret);
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
		String path = mEntityTypeToPath.get (type);
		if (path == null || path.isEmpty ()) {
			return new ResponseEntity<Void> (HttpStatus.CREATED);
		}
		
		Importer importer = new Importer (driver);
		
		Resource[] resources = null;
		
		try {
			ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver ();
			resources = resourcePatternResolver.getResources (mEntityTypeToPath.get (type) + "/*.parquet");
		} catch (FileNotFoundException fnfx) {
			mLogger.warning ("No files for type " + type + " found in local environment");
		}

		if ((resources == null || resources.length <= 0) && env.getProperty ("opentarget.server") != null) {
			// no local files - try to get from the website if configured
			FTPClient client = new FTPClient ();
			client.connect (env.getProperty ("opentarget.server"));
		    client.enterRemotePassiveMode();
			client.login("anonymous", "anonymous");
			Path entityPath = Paths.get (env.getProperty ("opentarget.path"), mEntityTypeToPath.get (type));
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
		
		for (Resource r : resources) {
			// to use the Hadoop parquet standalone utils, we need to have a file, not a stream

			File resourceFile = null;
			File tmpFile = null;
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

			// do the import
			try {
				mLogger.info ("Processing " + resourceFile.getName ());
				Parquet parquet = Reader.read (Paths.get (resourceFile.toURI ()));
				importer.importParquet (parquet, type);
				mLogger.info ("Finished " + resourceFile.getName ());
			} catch (IOException iox) {
				mLogger.severe (iox.toString ());
			}

			if (tmpFile != null) {
				tmpFile.delete ();
			}
		}

		return new ResponseEntity<Void> (HttpStatus.OK);
	}
}
