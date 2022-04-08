package com.semis.gradvek.parquet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.parquet.example.data.Group;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class ParquetTests {

	private static Parquet mParquet = null;
	
	@BeforeAll
	public static void readFile () throws IOException {	
		Resource r = new ClassPathResource ("targets/part-00000-93626c03-1c1d-49b0-a5d8-9973ee62d900-c000.snappy.parquet");
		mParquet = ParquetUtils.readResource (r);
	}
	
	@Test
	public void readFileTest () {
		Assertions.assertNotNull (mParquet);
	}
	
	@Test
	public void extractParamsTest () {
		Group firstEntry = mParquet.getData ().get (0);
		Map<String, String> params = ParquetUtils.extractParams (firstEntry, "id", "approvedSymbol", "approvedName");
		
		Assertions.assertEquals (params.getOrDefault ("id", null), "ENSG00000020219");
		Assertions.assertEquals (params.getOrDefault ("approvedName", null), "chaperonin containing TCP1 subunit 8 like 1, pseudogene");
	
		String json = ParquetUtils.paramsAsJSON (params);
		Assertions.assertTrue (json.contains ("approvedSymbol"));
	}

	@Test
	public void extractStringListTest () {
		Group firstEntry = mParquet.getData ().get (0);
		List<String> params = ParquetUtils.extractStringList (firstEntry, "transcriptIds");
		
		Assertions.assertEquals (params.size (), 1);
		Assertions.assertEquals (params.get (0), "ENST00000465400");
	}

	@Test
	public void extractGroupListTest () {
		Group firstEntry = mParquet.getData ().get (0);
		List<Group> params = ParquetUtils.extractGroupList (firstEntry, "synonyms");
		
		Assertions.assertEquals (params.size (), 6);
	}

}
