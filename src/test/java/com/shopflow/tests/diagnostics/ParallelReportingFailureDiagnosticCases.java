package com.shopflow.tests.diagnostics;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.shopflow.tests.BaseTest;
import com.shopflow.utils.FixtureResources;

public class ParallelReportingFailureDiagnosticCases extends BaseTest {
    @DataProvider(name = "parallelFailurePages", parallel = true)
    public Object[][] parallelFailurePages() {
        return new Object[][] {
                {"red-worker", "#b91c1c"},
                {"green-worker", "#047857"},
                {"blue-worker", "#1d4ed8"}
        };
    }

    @Test(dataProvider = "parallelFailurePages", groups = "reporting-diagnostic")
    public void eachParallelFailureShouldCaptureItsOwnPage(String marker, String color) {
        getDriver().get(FixtureResources.uri("parallel-diagnostic.html")
                + "?marker=" + marker + "&color=" + color.substring(1));
        Assert.assertEquals(getDriver().getTitle(), "Intentional mismatch for " + marker,
                "Intentional Phase 9 failure: screenshot and diagnostic must identify " + marker);
    }
}
