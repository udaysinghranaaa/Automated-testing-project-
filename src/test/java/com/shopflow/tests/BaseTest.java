package com.shopflow.tests;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.shopflow.core.ConfigReader;
import com.shopflow.core.DriverFactory;
import com.shopflow.core.DriverManager;
import com.shopflow.pages.LoginPage;

public abstract class BaseTest {
    private static final Logger LOG = LoggerFactory.getLogger(BaseTest.class);
    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser", "headless"})
    public void setUpBrowser(@Optional("") String suiteBrowser, @Optional("") String suiteHeadless) {
        String browser = suiteBrowser == null || suiteBrowser.isBlank()
                ? ConfigReader.browser()
                : suiteBrowser.trim();
        boolean headless = ConfigReader.headless(suiteHeadless);
        MDC.put("testClass", getClass().getSimpleName());
        MDC.put("browser", browser);
        try {
            WebDriver createdDriver = DriverFactory.createDriver(browser, headless);
            DriverManager.setDriver(createdDriver);
            createdDriver.get(initialUrl());
            LOG.info("event=browser_initialized testClass={} browser={} headless={} threadId={} driverIdentity={}",
                    getClass().getSimpleName(), browser, headless, Thread.currentThread().getId(),
                    Integer.toHexString(System.identityHashCode(createdDriver)));
        } catch (RuntimeException | Error exception) {
            DriverManager.quitDriver();
            MDC.clear();
            throw exception;
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownBrowser() {
        try {
            if (DriverManager.getDriverOrNull() != null) {
                try {
                    DriverManager.quitDriver();
                } finally {
                    LOG.info("event=browser_closed testClass={} threadId={}",
                            getClass().getSimpleName(), Thread.currentThread().getId());
                }
            }
        } finally {
            MDC.clear();
        }
    }

    protected LoginPage loginPage() {
        return new LoginPage(DriverManager.getDriver()).waitUntilReady();
    }

    protected String initialUrl() {
        return ConfigReader.baseUrl();
    }

    public final WebDriver getDriver() {
        return DriverManager.getDriverOrNull();
    }
}
