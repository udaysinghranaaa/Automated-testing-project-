package com.shopflow.tests.advanced;

import java.util.Set;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.shopflow.pages.advanced.WindowsPage;

public class WindowsAdvancedCases extends AdvancedFixtureTest {
    private WindowsPage windows;

    @BeforeMethod(alwaysRun = true)
    public void openWindowsFixture() {
        openFixture("windows.html");
        windows = new WindowsPage(getDriver());
    }

    @Test(groups = "advanced", description = "TC_ADV_010 open and switch to a new tab")
    public void openAndSwitchToTab() {
        Set<String> before = windows.handles();
        windows.openTab();
        windows.switchToNewContext(before);
        Assert.assertEquals(windows.childKind(), "tab");
        Assert.assertEquals(windows.childHeading(), "Child browser context");
    }

    @Test(groups = "advanced", description = "TC_ADV_011 open a separate window")
    public void openSeparateWindow() {
        Set<String> before = windows.handles();
        windows.openWindow(2);
        windows.switchToNewContext(before);
        Assert.assertEquals(windows.childKind(), "window");
        Assert.assertEquals(windows.pageTitle(), "Advanced Child");
    }

    @Test(groups = "advanced", description = "TC_ADV_012 close child and return to parent")
    public void closeChildAndReturnToParent() {
        String parent = windows.currentHandle();
        Set<String> before = windows.handles();
        windows.openTab();
        windows.switchToNewContext(before);
        windows.closeCurrent();
        windows.switchTo(parent);
        Assert.assertEquals(windows.handles().size(), 1);
        Assert.assertEquals(windows.pageTitle(), "Advanced Windows");
    }

    @Test(groups = "advanced", description = "TC_ADV_013 manage multiple child handles")
    public void manageMultipleChildHandles() {
        String parent = windows.currentHandle();
        windows.openTab();
        windows.openWindow(3);
        Assert.assertEquals(windows.handles().size(), 3);
        for (String handle : windows.handles()) {
            if (!handle.equals(parent)) {
                windows.switchTo(handle);
                Assert.assertEquals(windows.childHeading(), "Child browser context");
                windows.closeCurrent();
            }
        }
        windows.switchTo(parent);
        Assert.assertEquals(windows.handles().size(), 1);
    }
}
