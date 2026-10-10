package com.shopflow.tests.advanced;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.shopflow.pages.advanced.ShadowDomPage;

public class ShadowDomAdvancedCases extends AdvancedFixtureTest {
    private ShadowDomPage shadow;
    @BeforeMethod(alwaysRun = true)
    public void openShadowFixture() { openFixture("shadow.html"); shadow = new ShadowDomPage(getDriver()); }
    @Test(groups="advanced", description="TC_ADV_031 use Selenium SearchContext inside open shadow DOM")
    public void interactInsideOpenShadowRoot() { shadow.clickShadowButton(); Assert.assertEquals(shadow.shadowStatus(), "Shadow clicked"); }
}
