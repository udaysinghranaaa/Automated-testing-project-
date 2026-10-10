package com.shopflow.tests.advanced;

import java.util.List;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.shopflow.pages.advanced.DropdownsPage;

public class DropdownsAdvancedCases extends AdvancedFixtureTest {
    private DropdownsPage dropdowns;
    @BeforeMethod(alwaysRun = true)
    public void openDropdownFixture() { openFixture("dropdowns.html"); dropdowns = new DropdownsPage(getDriver()); }
    @Test(groups="advanced", description="TC_ADV_023 select native option by visible text")
    public void selectNativeOption() { dropdowns.selectNativeByVisibleText("Beta"); Assert.assertEquals(dropdowns.nativeValue(), "beta"); }
    @Test(groups="advanced", description="TC_ADV_024 select multiple native options")
    public void selectMultipleOptions() { dropdowns.selectMultiple("red", "blue"); Assert.assertEquals(dropdowns.selectedMultipleValues(), List.of("red", "blue")); }
    @Test(groups="advanced", description="TC_ADV_025 interact with a custom dropdown")
    public void selectCustomOption() { dropdowns.selectCustom("two"); Assert.assertEquals(dropdowns.customStatus(), "two"); Assert.assertEquals(dropdowns.customExpanded(), "false"); }
}
