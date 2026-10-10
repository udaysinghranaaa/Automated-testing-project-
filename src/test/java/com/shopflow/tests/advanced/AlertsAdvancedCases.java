package com.shopflow.tests.advanced;

import org.openqa.selenium.Alert;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.shopflow.pages.advanced.AlertsPage;

@Test(groups = {"advanced", "alerts"})
public class AlertsAdvancedCases extends AdvancedFixtureTest {
    private AlertsPage alerts;

    @BeforeMethod(alwaysRun = true)
    public void openAlertsFixture() {
        openFixture("alerts.html");
        alerts = new AlertsPage(getDriver()).waitUntilLoaded();
    }

    @Test(description = "TC_ADV_001 - Simple alert")
    public void simpleAlertShouldExposeTextAndAccept() {
        alerts.triggerSimpleAlert();
        Alert alert = alerts.waitForAlert();
        Assert.assertEquals(alert.getText(), "Operation completed");
        alert.accept();
        Assert.assertEquals(alerts.alertResult(), "Simple alert accepted");
    }

    @Test(description = "TC_ADV_002 - Accept confirmation")
    public void acceptedConfirmationShouldUpdateStatus() {
        alerts.triggerConfirmation();
        Alert alert = alerts.waitForAlert();
        Assert.assertEquals(alert.getText(), "Are you sure?");
        alert.accept();
        Assert.assertEquals(alerts.confirmationResult(), "Confirmed");
    }

    @Test(description = "TC_ADV_003 - Dismiss confirmation")
    public void dismissedConfirmationShouldUpdateStatus() {
        alerts.triggerConfirmation();
        alerts.waitForAlert().dismiss();
        Assert.assertEquals(alerts.confirmationResult(), "Cancelled");
    }

    @Test(description = "TC_ADV_004 - Prompt input")
    public void promptShouldDisplayEnteredValue() {
        alerts.triggerPrompt();
        Alert alert = alerts.waitForAlert();
        Assert.assertEquals(alert.getText(), "Enter your name:");
        alert.sendKeys("ShopFlow QA");
        alert.accept();
        Assert.assertEquals(alerts.promptResult(), "Hello, ShopFlow QA");
    }

    @Test(description = "TC_ADV_005 - Dismiss prompt")
    public void dismissedPromptShouldDisplayCancelledState() {
        alerts.triggerPrompt();
        alerts.waitForAlert().dismiss();
        Assert.assertEquals(alerts.promptResult(), "Prompt cancelled");
    }
}
