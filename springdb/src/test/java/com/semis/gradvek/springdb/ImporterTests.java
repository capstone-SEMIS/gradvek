package com.semis.gradvek.springdb;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.semis.gradvek.entity.Entity;
import com.semis.gradvek.entity.EntityType;
import com.semis.gradvek.parquet.Parquet;
import com.semis.gradvek.parquet.ParquetUtils;

public class ImporterTests {

	private static Parquet mParquet = null;
	
	@BeforeAll
	public static void readFile () throws IOException {	
		Resource r = new ClassPathResource ("targets/part-00000-93626c03-1c1d-49b0-a5d8-9973ee62d900-c000.snappy.parquet");
		mParquet = ParquetUtils.readResource (r);
	}
	@Test
	public void testImport () {
		List<Entity> imported = Importer.readEntities (mParquet, EntityType.Target);
		
		Assertions.assertEquals (imported.size (), 316);
	}
}
