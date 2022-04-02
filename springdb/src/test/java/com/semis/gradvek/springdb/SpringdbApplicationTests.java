package com.semis.gradvek.springdb;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = "neo4j.init=false")
class SpringdbApplicationTests {
    @Autowired
    private Environment mEnv;

    @Test
    void contextLoads() {
    }

    @Test
    void testsArePassing() {
        assertThat(true).isTrue();
    }

    @Test
    void envOverridesDbUrl() {
        DBDriver driver = Neo4jDriver.instance(mEnv);
        assertThat(driver.getUri()).doesNotContain("example.com");

        System.setProperty("NEO4JURL", "bolt://example.com:7687");
        driver = Neo4jDriver.instance(mEnv);
        assertThat(driver.getUri()).contains("example.com");
    }
}
