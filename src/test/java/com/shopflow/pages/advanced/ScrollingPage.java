package com.shopflow.pages.advanced;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.WheelInput.ScrollOrigin;
import com.shopflow.pages.BasePage;

public final class ScrollingPage extends BasePage {
    private static final By PAGE_BOTTOM = By.id("page-bottom");
    private static final By PAGE_STATUS = By.id("page-status");
    private static final By SCROLL_BOX = By.id("scroll-box");
    private static final By CONTAINER_BOTTOM = By.id("container-bottom");
    private static final By CONTAINER_STATUS = By.id("container-status");
    private final Actions actions;
    public ScrollingPage(WebDriver driver) { super(driver); this.actions = new Actions(driver); }
    public void scrollToAndClickPageBottom() { actions.scrollToElement(driver.findElement(PAGE_BOTTOM)).perform(); click(PAGE_BOTTOM); }
    public String pageStatus() { return text(PAGE_STATUS); }
    public void scrollContainerAndClickBottom() {
        actions.scrollFromOrigin(ScrollOrigin.fromElement(visible(SCROLL_BOX)), 0, 700).perform();
        click(CONTAINER_BOTTOM);
    }
    public String containerStatus() { return text(CONTAINER_STATUS); }
}
