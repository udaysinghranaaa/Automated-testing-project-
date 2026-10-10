package com.shopflow.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.shopflow.core.ConfigReader;

public final class CheckoutCompletePage extends BasePage {
    private static final String URL = ConfigReader.baseUrl() + "checkout-complete.html";
    private static final By PAGE_TITLE = By.cssSelector("[data-test='title']");
    private static final By COMPLETE = By.cssSelector("[data-test='checkout-complete-container']");
    private static final By COMPLETE_HEADER = By.cssSelector("[data-test='complete-header']");
    private static final By COMPLETE_TEXT = By.cssSelector("[data-test='complete-text']");
    private static final By BACK_HOME = By.cssSelector("[data-test='back-to-products']");

    public CheckoutCompletePage(WebDriver driver) { super(driver); }

    public CheckoutCompletePage waitUntilLoaded() {
        waits.urlToBe(URL);
        visible(COMPLETE);
        return this;
    }

    public String getHeading() { return text(PAGE_TITLE); }
    public String getConfirmationHeading() { return text(COMPLETE_HEADER); }
    public String getConfirmationMessage() { return text(COMPLETE_TEXT); }

    public InventoryPage backHome() {
        click(BACK_HOME);
        return new InventoryPage(driver).waitUntilLoaded();
    }
}
