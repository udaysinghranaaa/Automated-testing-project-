package com.shopflow.pages.advanced;

import java.util.HashSet;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import com.shopflow.pages.BasePage;

public final class WindowsPage extends BasePage {
    private static final By OPEN_TAB = By.id("open-tab");
    private static final By OPEN_WINDOW = By.id("open-window");
    private static final By CHILD_HEADING = By.id("child-heading");
    private static final By CHILD_KIND = By.id("child-kind");

    public WindowsPage(WebDriver driver) { super(driver); }
    public String currentHandle() { return driver.getWindowHandle(); }
    public Set<String> handles() { return driver.getWindowHandles(); }
    public void openTab() { click(OPEN_TAB); waits.until(ExpectedConditions.numberOfWindowsToBe(2)); }
    public void openWindow(int expectedCount) { click(OPEN_WINDOW); waits.until(ExpectedConditions.numberOfWindowsToBe(expectedCount)); }
    public String switchToNewContext(Set<String> originalHandles) {
        Set<String> newHandles = new HashSet<>(driver.getWindowHandles());
        newHandles.removeAll(originalHandles);
        String newHandle = newHandles.iterator().next();
        driver.switchTo().window(newHandle);
        visible(CHILD_HEADING);
        return newHandle;
    }
    public String childHeading() { return text(CHILD_HEADING); }
    public String childKind() { return text(CHILD_KIND); }
    public void switchTo(String handle) { driver.switchTo().window(handle); }
    public void closeCurrent() { driver.close(); }
}
