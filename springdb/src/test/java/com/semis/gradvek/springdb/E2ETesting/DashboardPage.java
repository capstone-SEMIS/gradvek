package com.semis.gradvek.springdb.E2ETesting;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class DashboardPage extends PageObject {

    private final String TARGET_SYMBOL = "TRPV1";

    @FindBy (id = "search-icon")
    private WebElement searchIcon;

    @FindBy (id = "search-input")
    private WebElement searchInput;

    @FindBy (id = "submit-btn")
    private WebElement searchBtn;

    @FindBy (id = "results-table")
    private WebElement resultsTable;

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    public void clickSearchIcon() {
        this.searchIcon.click();
    }

    public void enterSearchText() {
        this.searchInput.sendKeys(TARGET_SYMBOL);
    }

    public void clickSearchBtn() {
        this.searchBtn.click();
    }


}
