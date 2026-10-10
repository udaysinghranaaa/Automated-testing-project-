package com.shopflow.tests.diagnostics;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.shopflow.tests.advanced.AdvancedFixtureTest;

public class ReportingFailureDiagnosticCases extends AdvancedFixtureTest {
    @Test(groups = "reporting-diagnostic", description = "Intentional Phase 8 failure for evidence verification")
    public void intentionalFailureShouldProduceEvidence() {
        openFixture("alerts.html");
        Assert.assertEquals(getDriver().getTitle(), "Deliberately Incorrect Title",
                "Controlled Phase 8 diagnostic failure");
    }
}
