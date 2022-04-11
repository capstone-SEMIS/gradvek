package com.semis.gradvek.springdb;

import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder;
import org.springframework.web.context.WebApplicationContext;

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
    void setupEach(WebApplicationContext context) {
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

        try {
        	driver = new ChromeDriver(options);
        } catch (IllegalStateException isx) { // fallback - chrome driver not installed
            driver = MockMvcHtmlUnitDriverBuilder
                    .webAppContextSetup(context)
                    .build();
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
    void csvPost() {
        // TODO Michael
    }

    @Test
    void csvGet() {
        // TODO Michael
    }
}
