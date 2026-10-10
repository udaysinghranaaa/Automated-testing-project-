package com.shopflow.pages.advanced;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import com.shopflow.pages.BasePage;

public final class DropdownsPage extends BasePage {
    private static final By NATIVE = By.id("native-select");
    private static final By MULTI = By.id("multi-select");
    private static final By CUSTOM_TRIGGER = By.id("custom-trigger");
    private static final By CUSTOM_STATUS = By.id("custom-status");

    public DropdownsPage(WebDriver driver) { super(driver); }
    public void selectNativeByVisibleText(String text) { new Select(visible(NATIVE)).selectByVisibleText(text); }
    public String nativeValue() { return new Select(visible(NATIVE)).getFirstSelectedOption().getAttribute("value"); }
    public void selectMultiple(String first, String second) { Select select = new Select(visible(MULTI)); select.selectByValue(first); select.selectByValue(second); }
    public List<String> selectedMultipleValues() { return new Select(visible(MULTI)).getAllSelectedOptions().stream().map(element -> element.getAttribute("value")).toList(); }
    public void selectCustom(String value) { click(CUSTOM_TRIGGER); click(By.cssSelector("#custom-options [data-value='" + value + "']")); }
    public String customStatus() { return text(CUSTOM_STATUS); }
    public String customExpanded() { return visible(CUSTOM_TRIGGER).getAttribute("aria-expanded"); }
}
