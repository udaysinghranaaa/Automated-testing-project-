package com.shopflow.pages.advanced;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import com.shopflow.pages.BasePage;

public final class ShadowDomPage extends BasePage {
    private static final By HOST = By.id("shadow-host");
    private static final By BUTTON = By.id("shadow-button");
    private static final By STATUS = By.id("shadow-status");
    public ShadowDomPage(WebDriver driver) { super(driver); }
    private SearchContext root() { return visible(HOST).getShadowRoot(); }
    public void clickShadowButton() { root().findElement(BUTTON).click(); }
    public String shadowStatus() { return root().findElement(STATUS).getText().trim(); }
}
