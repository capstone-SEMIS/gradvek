package edu.gradvek.springdb;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EndToEndTests {

    WebDriver driver;

    @BeforeEach
    void setup(WebApplicationContext context) {
        driver = MockMvcHtmlUnitDriverBuilder
                .webAppContextSetup(context)
                .build();
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
    void TestsAreFailing() {
        assertThat(1).isEqualTo(2);
    }

    @Test
    void InfoSaysHello() {
        driver.get("http://localhost/info");
        assertThat(driver.getPageSource()).contains("Hello Gradvec");
    }

    @Test
    void InfoSaysGoodbye() {
        driver.get("http://localhost/info");
        assertThat(driver.getPageSource()).contains("Goodbye Gradvec");
    }
}
