package com.semis.gradvek.springdb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.semis.gradvek.entity.Dataset;
import com.semis.gradvek.entity.Entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
		assertThat (mController.count ("AdverseEvent").getBody ()).isGreaterThan (0);
	}
	
	@Test
	public void testAE () {
		assertThat (mController.getAdverseEvent ("ENST00000310522", Optional.empty()).getBody ().size ()).isGreaterThan (0);
	}
	
	@SuppressWarnings ("unchecked")
	@Test
	public void testDatasets () {
		// ask for all datasets - there should be exactly one
		String datasetJson = mController.datasets ().getBody ();
		Dataset [] datasets = Entity.fromJson(datasetJson, Dataset[].class);

		assertThat (datasets).isNotNull ();
		assertThat (datasets.length).isEqualTo (1);
		assertThat (datasets[0].isEnabled ()).isTrue ();
		
		// form the enable command
		List<Map<String, String>> toEnable = List.of (				
			Map.of("dataset", datasets[0].getDataset (), "enabled", "false")
		);
		// disable this dataset
		mController.enableDatasets (toEnable.stream ().toArray (Map[]::new));
		
		// get them again
		datasetJson = mController.datasets ().getBody ();
		datasets = Entity.fromJson(datasetJson, Dataset[].class);
		
		// should be disabled now
		assertThat (datasets).isNotNull ();
		assertThat (datasets.length).isEqualTo (1);
		assertThat (datasets[0].isEnabled ()).isFalse ();
		// put it back
		toEnable = List.of (				
				Map.of("dataset", datasets[0].getDataset (), "enabled", "true")
			);
		mController.enableDatasets (toEnable.stream ().toArray (Map[]::new));
		// check that it's back
		datasetJson = mController.datasets ().getBody ();
		datasets = Entity.fromJson(datasetJson, Dataset[].class);
		assertThat (datasets[0].isEnabled ()).isTrue ();
	}
	
	@SuppressWarnings ("unchecked")
	@Test
	public void testAEByTarget () {
		
		// get the adverse events for the demo target
		List<AdverseEventIntObj> ae = mController.getAdverseEvent ("ENST00000310522", Optional.empty()).getBody ();
		
		// should be 2 of them
		assertThat (ae.size ()).isEqualTo (2);
		List<String> aeIds = List.of (ae.get (0).getMeddraId(), ae.get (1).getMeddraId());
		// find one by id
		assertThat (aeIds).contains ("10000804");
		
		// disable the dataset
		List<Map<String, String>> toEnable = List.of (				
			Map.of("dataset", "demo", "enabled", "false")
		);
		mController.enableDatasets (toEnable.stream ().toArray (Map[]::new));
		
		// now there are none
		ae = mController.getAdverseEvent ("ENST00000310522", Optional.empty()).getBody ();
		assertThat (ae.size ()).isEqualTo (0);
		
		// put the dataset back
		toEnable = List.of (				
				Map.of("dataset", "demo", "enabled", "true")
			);
		mController.enableDatasets (toEnable.stream ().toArray (Map[]::new));
		// should be 2 again
		ae = mController.getAdverseEvent ("ENST00000310522", Optional.empty()).getBody ();
		assertThat (ae.size ()).isEqualTo (2);
	}
}
