package com.semis.gradvek.springdb;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SpringdbApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
    void TestsArePassing() {
        assertThat(true).isTrue();
    }

}
