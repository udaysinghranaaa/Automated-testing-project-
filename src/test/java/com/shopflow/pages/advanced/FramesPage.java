package com.shopflow.pages.advanced;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import com.shopflow.pages.BasePage;

public final class FramesPage extends BasePage {
    private static final By MAIN_HEADING = By.id("main-heading");
    private static final By MAIN_BUTTON = By.id("main-button");
    private static final By MAIN_STATUS = By.id("main-status");
    private static final By PARENT_FRAME = By.id("parent-frame");
    private static final By PARENT_HEADING = By.id("parent-heading");
    private static final By PARENT_BUTTON = By.id("parent-button");
    private static final By PARENT_STATUS = By.id("parent-status");
    private static final By NESTED_FRAME = By.id("nested-frame");
    private static final By NESTED_HEADING = By.id("nested-heading");
    private static final By NESTED_BUTTON = By.id("nested-button");
    private static final By NESTED_STATUS = By.id("nested-status");

    public FramesPage(WebDriver driver) { super(driver); }
    public String mainHeading() { return text(MAIN_HEADING); }
    public String parentHeading() { return text(PARENT_HEADING); }
    public String nestedHeading() { return text(NESTED_HEADING); }
    public String mainStatus() { return text(MAIN_STATUS); }
    public String parentStatus() { return text(PARENT_STATUS); }
    public String nestedStatus() { return text(NESTED_STATUS); }
    public void clickMainAction() { click(MAIN_BUTTON); }
    public void clickParentAction() { click(PARENT_BUTTON); }
    public void clickNestedAction() { click(NESTED_BUTTON); }

    public void enterParentFrameByElement() {
        driver.switchTo().frame(visible(PARENT_FRAME));
        visible(PARENT_HEADING);
    }
    public void enterParentFrameWithWait() {
        waits.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(PARENT_FRAME));
        visible(PARENT_HEADING);
    }
    public void enterNestedFrameWithWait() {
        waits.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(NESTED_FRAME));
        visible(NESTED_HEADING);
    }
    public void returnToParentFrame() { driver.switchTo().parentFrame(); }
    public void returnToMainDocument() { driver.switchTo().defaultContent(); }
}
