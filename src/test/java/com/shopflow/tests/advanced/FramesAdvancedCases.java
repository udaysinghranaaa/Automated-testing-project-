package com.shopflow.tests.advanced;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.shopflow.pages.advanced.FramesPage;

public class FramesAdvancedCases extends AdvancedFixtureTest {
    private FramesPage frames;

    @BeforeMethod(alwaysRun = true)
    public void openFramesFixture() {
        openFixture("frames.html");
        frames = new FramesPage(getDriver());
    }

    @Test(groups = "advanced", description = "TC_ADV_006 switch into a frame by WebElement")
    public void switchIntoFrameByElement() {
        frames.enterParentFrameByElement();
        Assert.assertEquals(frames.parentHeading(), "Parent frame");
    }

    @Test(groups = "advanced", description = "TC_ADV_007 interact inside a frame")
    public void interactInsideFrame() {
        frames.enterParentFrameWithWait();
        frames.clickParentAction();
        Assert.assertEquals(frames.parentStatus(), "Parent clicked");
    }

    @Test(groups = "advanced", description = "TC_ADV_008 navigate nested frames and parentFrame")
    public void navigateNestedFrames() {
        frames.enterParentFrameWithWait();
        frames.enterNestedFrameWithWait();
        frames.clickNestedAction();
        Assert.assertEquals(frames.nestedStatus(), "Nested clicked");
        frames.returnToParentFrame();
        Assert.assertEquals(frames.parentHeading(), "Parent frame");
    }

    @Test(groups = "advanced", description = "TC_ADV_009 restore default content")
    public void returnToDefaultContent() {
        frames.enterParentFrameWithWait();
        frames.enterNestedFrameWithWait();
        frames.returnToMainDocument();
        frames.clickMainAction();
        Assert.assertEquals(frames.mainStatus(), "Main clicked");
    }
}
