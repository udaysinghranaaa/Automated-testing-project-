package com.shopflow.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.shopflow.core.ConfigReader;
import com.shopflow.utils.WaitUtils;

public abstract class BasePage {
    protected final WebDriver driver;
    protected final WaitUtils waits;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.waits = new WaitUtils(driver, ConfigReader.explicitWait());
    }

    protected WebElement visible(By locator) {
        return waits.visible(locator);
    }

    protected WebElement clickable(By locator) {
        return waits.clickable(locator);
    }

    protected void click(By locator) {
        clickable(locator).click();
    }

    protected void enterText(By locator, String value) {
        visible(locator).sendKeys(value);
    }

    protected String text(By locator) {
        return visible(locator).getText().trim();
    }

    protected List<WebElement> elements(By locator) {
        return driver.findElements(locator);
    }

    protected boolean isPresent(By locator) {
        return !elements(locator).isEmpty();
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    public String pageTitle() {
        return driver.getTitle();
    }
}
