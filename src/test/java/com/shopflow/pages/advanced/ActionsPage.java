package com.shopflow.pages.advanced;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import com.shopflow.pages.BasePage;

public final class ActionsPage extends BasePage {
    private static final By HOVER_TARGET = By.id("hover-target");
    private static final By TOOLTIP = By.id("tooltip");
    private static final By DOUBLE_BUTTON = By.id("double-click");
    private static final By DOUBLE_STATUS = By.id("double-status");
    private static final By CONTEXT_TARGET = By.id("context-target");
    private static final By CONTEXT_STATUS = By.id("context-status");
    private static final By DRAG_SOURCE = By.id("drag-source");
    private static final By DROP_TARGET = By.id("drop-target");
    private static final By DRAG_STATUS = By.id("drag-status");
    private static final By HOLD_TARGET = By.id("hold-target");
    private static final By HOLD_STATUS = By.id("hold-status");
    private final Actions actions;

    public ActionsPage(WebDriver driver) { super(driver); this.actions = new Actions(driver); }
    public void hover() { actions.moveToElement(visible(HOVER_TARGET)).perform(); }
    public boolean tooltipDisplayed() { return visible(TOOLTIP).isDisplayed(); }
    public void doubleClick() { actions.doubleClick(clickable(DOUBLE_BUTTON)).perform(); }
    public String doubleStatus() { return text(DOUBLE_STATUS); }
    public void contextClick() { actions.contextClick(visible(CONTEXT_TARGET)).perform(); }
    public String contextStatus() { return text(CONTEXT_STATUS); }
    public void dragAndDrop() { actions.dragAndDrop(visible(DRAG_SOURCE), visible(DROP_TARGET)).perform(); }
    public String dragStatus() { return text(DRAG_STATUS); }
    public void clickAndHoldThenRelease() { actions.clickAndHold(clickable(HOLD_TARGET)).release().perform(); }
    public String holdStatus() { return text(HOLD_STATUS); }
}
