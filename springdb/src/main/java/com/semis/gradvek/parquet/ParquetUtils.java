package com.semis.gradvek.parquet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.semis.gradvek.entity.EntityType;
import com.semis.gradvek.springdb.Controller;
import com.semis.gradvek.springdb.Importer;
import com.semis.gradvek.springdb.Neo4jDriver;

public class ParquetUtils {
	private static final PathMatcher mParquetMatcher = FileSystems.getDefault ().getPathMatcher ("glob:**.parquet");
	private static final Logger mLogger = Logger.getLogger (ParquetUtils.class.getName ());

	private static final Map<EntityType, String> mEntityTypeToPath = Map.of (EntityType.Target, "targets");

	public static ResponseEntity<Void> initEntities (Neo4jDriver driver, EntityType type) 
		throws IOException
	{
		Importer importer = new Importer (driver);
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = resourcePatternResolver.getResources(mEntityTypeToPath.get (type) + "/*.parquet");	
		
		for (Resource r: resources) {
			File resourceFile = null;
			File tmpFile = null;
			if (r.isFile ()) {
				resourceFile = r.getFile ();
			} else if (r.isReadable ()) {
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
	
	public static ResponseEntity<Void> initTargets (Neo4jDriver driver, EntityType type)
			throws URISyntaxException, IOException {
		Importer importer = new Importer (driver);
		ClassLoader cl = driver.getClass ().getClassLoader ();
		String folderName = mEntityTypeToPath.get (type);
		FileSystem fileSystem = null;
		DirectoryStream<Path> directoryStream = null;
		URI uri = cl.getResource (folderName).toURI ();
		if ("jar".equals (uri.getScheme ())) {
			fileSystem = FileSystems.newFileSystem (uri, Collections.emptyMap (), null);
	        directoryStream = Files.newDirectoryStream(fileSystem.getPath("BOOT-INF/classes/" + folderName));
		} else {
			directoryStream = Files.newDirectoryStream(Paths.get (uri));
		}

		directoryStream.forEach (path -> {
			if (mParquetMatcher.matches (path)) {
				try {
					Parquet parquet = Reader.read (path);
					importer.importParquet (parquet, type);
				} catch (IOException iox) {
					mLogger.severe (iox.toString ());
				}
			}
		});
		
		if (fileSystem != null) {
			fileSystem.close ();
		}

		return new ResponseEntity<Void> (HttpStatus.OK);
	}

}
