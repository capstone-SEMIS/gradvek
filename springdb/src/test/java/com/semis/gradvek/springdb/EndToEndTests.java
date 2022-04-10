package com.semis.gradvek.springdb;

import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = "neo4j.init=false")
class EndToEndTests {

    WebDriver driver;
    String baseUrl;

    @Autowired
    private Environment environment;

    @BeforeEach
    void setupEach() {
        driver = new ChromeDriver();

        if (baseUrl == null) {
            baseUrl = environment.getProperty("BASE_URL");
            if (baseUrl == null || !baseUrl.startsWith("http")) {
                baseUrl = "http://localhost";
            }

            String chromeDriverPath = environment.getProperty("CHROMEDRIVER_PATH");
            if (chromeDriverPath == null) {
                chromeDriverPath = "/usr/local/bin/chromedriver";
            }
            System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        }
    }

    @AfterEach
    void destroy() {
        if (driver != null) {
            driver.close();
        }
    }

    @Test
    void TestsArePassing() {
        assertThat(true).isTrue();
    }

    @Test
    void InfoSaysHello() {
        driver.get(baseUrl + "/api/info");
        assertThat(driver.getPageSource()).contains("Hello Gradvek");
    }

    @Test
    void csvPost() {
        // TODO Michael
    }

    @Test
    void csvGet() {
        // TODO Michael
    }
}
