package com.shopflow.tests.advanced;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.shopflow.pages.advanced.KeyboardPage;

public class KeyboardAdvancedCases extends AdvancedFixtureTest {
    private KeyboardPage keyboard;
    @BeforeMethod(alwaysRun = true)
    public void openKeyboardFixture() { openFixture("keyboard.html"); keyboard = new KeyboardPage(getDriver()); }
    @Test(groups="advanced", description="TC_ADV_019 Ctrl+A replaces existing input")
    public void controlAReplacesText() { keyboard.replaceEditorText("Updated"); Assert.assertEquals(keyboard.editorValue(), "Updated"); }
    @Test(groups="advanced", description="TC_ADV_020 Tab changes active element")
    public void tabMovesFocus() { keyboard.tabFromFirst(); Assert.assertEquals(keyboard.activeElementId(), "second-field"); }
    @Test(groups="advanced", description="TC_ADV_021 Enter submits a form")
    public void enterSubmitsForm() { keyboard.submitWithEnter("selenium"); Assert.assertEquals(keyboard.status(), "Submitted: selenium"); }
    @Test(groups="advanced", description="TC_ADV_022 Shift modifier creates uppercase input")
    public void shiftModifierCreatesUppercase() { keyboard.typeUppercaseWithShift("abc"); Assert.assertEquals(keyboard.modifiedValue(), "ABC"); Assert.assertEquals(keyboard.status(), "ABC"); }
}
