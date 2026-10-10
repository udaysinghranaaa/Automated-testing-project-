package com.shopflow.pages.advanced;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import com.shopflow.pages.BasePage;

public final class KeyboardPage extends BasePage {
    private static final By EDITOR = By.id("editor");
    private static final By FIRST = By.id("first-field");
    private static final By SECOND = By.id("second-field");
    private static final By SUBMIT = By.id("submit-field");
    private static final By MODIFIED = By.id("modified-input");
    private static final By STATUS = By.id("keyboard-status");
    private final Actions actions;

    public KeyboardPage(WebDriver driver) { super(driver); this.actions = new Actions(driver); }
    public void replaceEditorText(String value) { visible(EDITOR).sendKeys(Keys.chord(Keys.CONTROL, "a"), value); }
    public String editorValue() { return visible(EDITOR).getAttribute("value"); }
    public void tabFromFirst() { visible(FIRST).click(); actions.sendKeys(Keys.TAB).perform(); }
    public String activeElementId() { return driver.switchTo().activeElement().getAttribute("id"); }
    public void submitWithEnter(String value) { visible(SUBMIT).sendKeys(value, Keys.ENTER); }
    public void typeUppercaseWithShift(String value) {
        WebElement input = visible(MODIFIED);
        actions.click(input).keyDown(Keys.SHIFT).sendKeys(value).keyUp(Keys.SHIFT).perform();
    }
    public String modifiedValue() { return visible(MODIFIED).getAttribute("value"); }
    public String status() { return text(STATUS); }
}
