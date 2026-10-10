package com.shopflow.tests.advanced;

import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.shopflow.pages.advanced.DynamicPage;

public class DynamicAdvancedCases extends AdvancedFixtureTest {
    private DynamicPage dynamic;
    @BeforeMethod(alwaysRun = true)
    public void openDynamicFixture() { openFixture("dynamic.html"); dynamic = new DynamicPage(getDriver()); }
    @Test(groups="advanced", description="TC_ADV_026 wait for delayed visibility")
    public void waitForDelayedVisibility() { dynamic.requestLateContent(); Assert.assertEquals(dynamic.waitForLateContent(), "Now visible"); }
    @Test(groups="advanced", description="TC_ADV_027 wait for loader invisibility and result")
    public void waitForLoadingSequence() { dynamic.startLoading(); Assert.assertTrue(dynamic.waitForLoaderToDisappear()); Assert.assertEquals(dynamic.loadedContent(), "Content loaded"); }
    @Test(groups="advanced", description="TC_ADV_028 handle a stale element after replacement")
    public void waitForElementReplacement() { WebElement old = dynamic.replaceableElement(); dynamic.replaceElement(); Assert.assertTrue(dynamic.waitUntilStale(old)); Assert.assertEquals(dynamic.waitForReplacementVersion(), "2"); }
    @Test(groups="advanced", description="TC_ADV_029 use FluentWait polling for enabled state")
    public void waitUntilButtonEnabled() { dynamic.requestEnable(); Assert.assertTrue(dynamic.waitUntilEnabled()); }
}
