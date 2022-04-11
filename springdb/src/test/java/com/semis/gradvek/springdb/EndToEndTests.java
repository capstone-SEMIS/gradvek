package com.semis.gradvek.springdb;

import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = "neo4j.init=false")
class EndToEndTests {

    boolean setupDone = false;
    WebDriver driver;
    String baseUrl;
    ChromeOptions options;

    @Autowired
    private Environment environment;

    @BeforeEach
    void setupEach() {
        if (!setupDone) {
            String chromeDriverPath = environment.getProperty("CHROMEDRIVER_PATH");
            if (chromeDriverPath == null) {
                chromeDriverPath = "/usr/local/bin/chromedriver";
            }
            System.setProperty("webdriver.chrome.driver", chromeDriverPath);

            baseUrl = "http://localhost:3000";

            options = new ChromeOptions();
            options.addArguments("--no-sandbox");
            options.setHeadless(true);

            setupDone = true;
        }

        driver = new ChromeDriver(options);
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
}
