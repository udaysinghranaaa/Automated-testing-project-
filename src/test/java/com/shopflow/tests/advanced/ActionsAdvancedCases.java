package com.shopflow.tests.advanced;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.shopflow.pages.advanced.ActionsPage;

public class ActionsAdvancedCases extends AdvancedFixtureTest {
    private ActionsPage actionsPage;
    @BeforeMethod(alwaysRun = true)
    public void openActionsFixture() { openFixture("actions.html"); actionsPage = new ActionsPage(getDriver()); }
    @Test(groups="advanced", description="TC_ADV_014 hover reveals tooltip")
    public void hoverRevealsTooltip() { actionsPage.hover(); Assert.assertTrue(actionsPage.tooltipDisplayed()); }
    @Test(groups="advanced", description="TC_ADV_015 double click triggers one event")
    public void doubleClickTriggersAction() { actionsPage.doubleClick(); Assert.assertEquals(actionsPage.doubleStatus(), "Count: 1"); }
    @Test(groups="advanced", description="TC_ADV_016 context click is handled")
    public void contextClickTriggersAction() { actionsPage.contextClick(); Assert.assertEquals(actionsPage.contextStatus(), "Context menu requested"); }
    @Test(groups="advanced", description="TC_ADV_017 drag source onto target")
    public void dragAndDropCompletes() { actionsPage.dragAndDrop(); Assert.assertEquals(actionsPage.dragStatus(), "Dropped successfully"); }
    @Test(groups="advanced", description="TC_ADV_018 click-and-hold then release")
    public void clickHoldAndRelease() { actionsPage.clickAndHoldThenRelease(); Assert.assertEquals(actionsPage.holdStatus(), "Released"); }
}
