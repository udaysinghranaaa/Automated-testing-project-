package com.shopflow.pages.advanced;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import com.shopflow.pages.BasePage;

public final class DynamicPage extends BasePage {
    private static final By SHOW_LATER = By.id("show-later");
    private static final By LATE_CONTENT = By.id("late-content");
    private static final By LOAD = By.id("load-content");
    private static final By LOADING = By.id("loading");
    private static final By LOADED = By.id("loaded-content");
    private static final By REPLACE = By.id("replace-element");
    private static final By REPLACEABLE = By.id("replaceable");
    private static final By ENABLE = By.id("enable-later");
    private static final By DELAYED = By.id("delayed-button");

    public DynamicPage(WebDriver driver) { super(driver); }
    public void requestLateContent() { click(SHOW_LATER); }
    public String waitForLateContent() { return text(LATE_CONTENT); }
    public void startLoading() { click(LOAD); }
    public boolean waitForLoaderToDisappear() { visible(LOADING); return waits.invisible(LOADING); }
    public String loadedContent() { return text(LOADED); }
    public WebElement replaceableElement() { return visible(REPLACEABLE); }
    public void replaceElement() { click(REPLACE); }
    public boolean waitUntilStale(WebElement oldElement) { return waits.until(ExpectedConditions.stalenessOf(oldElement)); }
    public String waitForReplacementVersion() {
        return new FluentWait<>(driver).withTimeout(Duration.ofSeconds(5)).pollingEvery(Duration.ofMillis(100))
                .ignoring(StaleElementReferenceException.class)
                .until(webDriver -> {
                    String version = webDriver.findElement(REPLACEABLE).getAttribute("data-version");
                    return "2".equals(version) ? version : null;
                });
    }
    public void requestEnable() { click(ENABLE); }
    public boolean waitUntilEnabled() { return waits.until(webDriver -> webDriver.findElement(DELAYED).isEnabled()); }
}
