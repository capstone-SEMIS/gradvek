package com.semis.gradvek.parquet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.parquet.example.data.simple.SimpleGroup;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.semis.gradvek.entity.EntityType;
import com.semis.gradvek.springdb.Importer;
import com.semis.gradvek.springdb.Neo4jDriver;

public class ParquetUtils {
	private static final Logger mLogger = Logger.getLogger (ParquetUtils.class.getName ());

	// maps the entity type to the name of the folder where the parquet files for it live
	private static final Map<EntityType, String> mEntityTypeToPath = Map.of (
			EntityType.Target, "targets",
			EntityType.Drug, "molecule",
			EntityType.Disease, "diseases",
			EntityType.Causes, "fda/significantAdverseDrugReactions");

	public static Map<String, String> extractParams (SimpleGroup data, String... keys) {
		Map<String, String> ret = new HashMap<> ();
		for (String key: keys) {
			String value = data.getValueToString (data.getType ().getFieldIndex (key), 0);
			if (value != null) {
				ret.put (key, value);
			}
		}
		
		return (ret);
	}
	
	public static String paramsAsJSON (Map<String, String> params) {
		String ret = params.keySet ().stream ()
			.map (key -> key + ":" + StringEscapeUtils.escapeEcmaScript (params.get (key)))
			.collect (Collectors.joining(", "));
		return (ret);
	}
	public static ResponseEntity<Void> initEntities (Environment env, Neo4jDriver driver, EntityType type)
			throws IOException, MalformedURLException {
		Importer importer = new Importer (driver);
		
		Resource[] resources = null;
		
		try {
			ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver ();
			resources = resourcePatternResolver.getResources (mEntityTypeToPath.get (type) + "/*.parquet");
		} catch (FileNotFoundException fnfx) {
			
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

			try {
				Parquet parquet = Reader.read (Paths.get (resourceFile.toURI ()));
				importer.importParquet (parquet, type);
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
