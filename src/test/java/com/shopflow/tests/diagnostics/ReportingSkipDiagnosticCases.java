package com.shopflow.tests.diagnostics;

import org.testng.SkipException;
import org.testng.annotations.Test;

import com.shopflow.tests.advanced.AdvancedFixtureTest;

public class ReportingSkipDiagnosticCases extends AdvancedFixtureTest {
    @Test(groups = "reporting-diagnostic", description = "Intentional Phase 8 skip status verification")
    public void intentionalSkipShouldBeReported() {
        openFixture("alerts.html");
        throw new SkipException("Controlled Phase 8 skip demonstration");
    }
}
