package com.shopflow.pages.advanced;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import com.shopflow.pages.BasePage;

public final class AlertsPage extends BasePage {
    private static final By HEADING = By.id("page-heading");
    private static final By SIMPLE = By.id("simple-alert");
    private static final By CONFIRM = By.id("confirm-alert");
    private static final By PROMPT = By.id("prompt-alert");
    private static final By ALERT_RESULT = By.id("alert-result");
    private static final By CONFIRM_RESULT = By.id("confirm-result");
    private static final By PROMPT_RESULT = By.id("prompt-result");

    public AlertsPage(WebDriver driver) { super(driver); }
    public AlertsPage waitUntilLoaded() { visible(HEADING); return this; }
    public void triggerSimpleAlert() { click(SIMPLE); }
    public void triggerConfirmation() { click(CONFIRM); }
    public void triggerPrompt() { click(PROMPT); }
    public Alert waitForAlert() { return waits.until(ExpectedConditions.alertIsPresent()); }
    public String alertResult() { return text(ALERT_RESULT); }
    public String confirmationResult() { return text(CONFIRM_RESULT); }
    public String promptResult() { return text(PROMPT_RESULT); }
}
