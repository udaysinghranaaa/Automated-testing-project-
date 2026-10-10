package com.shopflow.learning.testng;

import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@Test(groups = "learning")
public class LifecycleLearningExamples {

    private final List<String> events = new ArrayList<>();
    private int methodSetupCount;

    @BeforeSuite
    public void beforeSuite() {
        System.out.println("TestNG lifecycle: @BeforeSuite");
    }

    @BeforeTest
    public void beforeTest() {
        System.out.println("TestNG lifecycle: @BeforeTest");
    }

    @BeforeClass
    public void beforeClass() {
        events.add("beforeClass");
        System.out.println("TestNG lifecycle: @BeforeClass");
    }

    @BeforeMethod
    public void beforeMethod() {
        methodSetupCount++;
        events.add("beforeMethod");
        System.out.println("TestNG lifecycle: @BeforeMethod #" + methodSetupCount);
    }

    @Test(priority = 1)
    public void firstLifecycleTest() {
        events.add("firstTest");
        Assert.assertEquals(events, List.of("beforeClass", "beforeMethod", "firstTest"));
    }

    @Test(priority = 2)
    public void secondLifecycleTest() {
        events.add("secondTest");
        Assert.assertEquals(methodSetupCount, 2,
                "@BeforeMethod must execute before every test method.");
        Assert.assertEquals(events.get(0), "beforeClass",
                "@BeforeClass must execute once before this class's tests.");
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        events.add("afterMethod");
        System.out.println("TestNG lifecycle: @AfterMethod");
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        Assert.assertEquals(methodSetupCount, 2,
                "Both test methods must receive method-level setup.");
        System.out.println("TestNG lifecycle: @AfterClass");
    }

    @AfterTest(alwaysRun = true)
    public void afterTest() {
        System.out.println("TestNG lifecycle: @AfterTest");
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        System.out.println("TestNG lifecycle: @AfterSuite");
    }
}
