package com.semis.gradvek.springdb.E2ETesting;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.assertj.core.api.Assertions.assertThat;

public class TestPlan {
    private static WebDriver driver;

    @BeforeAll
    static void setup () {
        System.setProperty("webdriver.chrome.driver", "chromedriver");
        driver = new ChromeDriver();
    }

    @AfterAll
    static void destroy() {
        if (driver != null) {
            driver.close();
        }
    }

    @Test
    @Disabled
    void queryReturnsResults() {
        WebDriverWait wait = new WebDriverWait(driver, 5);

        driver.get(Utils.BASE_URL + "/app");
        DashboardPage page = new DashboardPage(driver);
        page.clickSearchIcon();
        page.enterSearchText();
        page.clickSearchBtn();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("results-table")));
        assertThat(driver.findElement(By.id("results-table")).getText()).contains("liver injury");
    }
}
