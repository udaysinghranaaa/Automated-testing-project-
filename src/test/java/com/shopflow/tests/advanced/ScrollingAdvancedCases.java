package com.shopflow.tests.advanced;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.shopflow.pages.advanced.ScrollingPage;

public class ScrollingAdvancedCases extends AdvancedFixtureTest {
    private ScrollingPage scrolling;
    @BeforeMethod(alwaysRun = true)
    public void openScrollingFixture() { openFixture("scrolling.html"); scrolling = new ScrollingPage(getDriver()); }
    @Test(groups="advanced", description="TC_ADV_032 scroll viewport to an element with W3C wheel input")
    public void scrollPageToElement() { scrolling.scrollToAndClickPageBottom(); Assert.assertEquals(scrolling.pageStatus(), "Page bottom clicked"); }
    @Test(groups="advanced", description="TC_ADV_033 scroll an overflow container with W3C wheel input")
    public void scrollOverflowContainer() { scrolling.scrollContainerAndClickBottom(); Assert.assertEquals(scrolling.containerStatus(), "Container bottom clicked"); }
}
