package com.semis.gradvek.springdb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.semis.gradvek.entity.Dataset;
import com.semis.gradvek.entity.Entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

@SpringBootTest(properties = "db.type=inmem")
public class DBTests {
	@Autowired
	private Controller mController;
	
	@Test
	public void contextLoads() {
		assertThat(mController).isNotNull();
	}

	@Test
	public void testInit () throws IOException {
		assertThat (mController.count ("AdverseEvent").getBody () > 0);
	}
	
	@Test
	public void testAE () {
		assertThat (mController.getAdverseEvent ("ENST00000310522").getBody ().size () > 0);
	}
	
	@SuppressWarnings ("unchecked")
	@Test
	public void testDatasets () {
		// ask for all datasets - there should be exactly one
		String datasetJson = mController.datasets ().getBody ();
		Dataset [] datasets = Entity.fromJson(datasetJson, Dataset[].class);

		assertThat (datasets != null);
		assertThat (datasets.length == 1);
		assertThat (datasets[0].isEnabled ());
		
		// form the enable command
		List<Map<String, String>> toEnable = List.of (				
			Map.of("dataset", datasets[0].getDataset (), "include", "false")
		);
		// disable this dataset
		mController.enableDatasets (toEnable.stream ().toArray (Map[]::new));
		
		// get them again
		datasetJson = mController.datasets ().getBody ();
		datasets = Entity.fromJson(datasetJson, Dataset[].class);
		
		// should be disabled now
		assertThat (datasets != null);
		assertThat (datasets.length == 1);
		assertThat (!datasets[0].isEnabled ());
		// put it back
		toEnable = List.of (				
				Map.of("dataset", datasets[0].getDataset (), "include", "true")
			);
		mController.enableDatasets (toEnable.stream ().toArray (Map[]::new));
	}
	
	@SuppressWarnings ("unchecked")
	@Test
	public void testAEByTarget () {
		List<AdverseEventIntObj> ae = mController.getAdverseEvent ("ENST00000310522").getBody ();
		
		assertThat (ae.size () == 2);
		assertThat (ae.get (0).getMeddraCode ().equals ("10000804"));
		
		List<Map<String, String>> toEnable = List.of (				
			Map.of("dataset", "demo", "include", "false")
		);
		mController.enableDatasets (toEnable.stream ().toArray (Map[]::new));
		
		ae = mController.getAdverseEvent ("ENST00000310522").getBody ();
		assertThat (ae.size () == 0);
		
		toEnable = List.of (				
				Map.of("dataset", "demo", "include", "true")
			);
		mController.enableDatasets (toEnable.stream ().toArray (Map[]::new));
	}
}
