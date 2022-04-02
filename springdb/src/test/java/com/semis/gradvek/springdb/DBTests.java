package com.semis.gradvek.springdb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

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
}
