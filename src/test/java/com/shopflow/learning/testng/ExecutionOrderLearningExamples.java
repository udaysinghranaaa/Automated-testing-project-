package com.shopflow.learning.testng;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = "learning")
public class ExecutionOrderLearningExamples {

    private final List<String> execution = new ArrayList<>();

    @Test(priority = 1)
    public void firstByPriority() {
        execution.add("first");
        Assert.assertEquals(execution, List.of("first"));
    }

    @Test(priority = 2)
    public void secondByPriority() {
        execution.add("second");
        Assert.assertTrue(execution.indexOf("first") < execution.indexOf("second"),
                "Lower numeric priority should execute first in this demo.");
    }

    @Test(priority = 3, dependsOnMethods = "firstByPriority")
    public void dependentTestRunsAfterRequiredMethod() {
        Assert.assertTrue(execution.contains("first"),
                "Dependency should complete before dependent test execution.");
    }
}
