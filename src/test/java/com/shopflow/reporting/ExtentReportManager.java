package com.shopflow.reporting;

import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.openqa.selenium.BuildInfo;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.shopflow.core.ConfigReader;
import com.shopflow.reporting.ReportArtifactManager.RunArtifacts;

public final class ExtentReportManager {
    private static final ThreadLocal<ExtentTest> CURRENT_TEST = new ThreadLocal<>();
    private static final Set<String> RUNTIME_BROWSERS = ConcurrentHashMap.newKeySet();
    private static volatile ExtentReports report;
    private static volatile RunArtifacts artifacts;

    private ExtentReportManager() {
    }

    public static synchronized void initialize(String suiteName) {
        if (report != null) {
            return;
        }
        artifacts = ReportArtifactManager.initializeRun();
        ExtentSparkReporter spark = new ExtentSparkReporter(artifacts.reportFile().toString());
        spark.config().setDocumentTitle("ShopFlow Automation Report");
        spark.config().setReportName(suiteName);
        spark.config().setTheme(Theme.STANDARD);
        spark.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss XXX");

        report = new ExtentReports();
        report.attachReporter(spark);
        report.setSystemInfo("Project", "ShopFlow QA Automation Framework");
        report.setSystemInfo("Suite", suiteName);
        report.setSystemInfo("Java", System.getProperty("java.version", "unavailable"));
        report.setSystemInfo("Operating System", System.getProperty("os.name", "unavailable")
                + " " + System.getProperty("os.version", ""));
        report.setSystemInfo("Browser", ConfigReader.browser());
        report.setSystemInfo("Selenium", new BuildInfo().getReleaseLabel());
        report.setSystemInfo("Run timestamp", OffsetDateTime.now().toString());
    }

    public static synchronized ExtentTest startTest(String name, String description, String className, String[] groups) {
        initialize("TestNG execution");
        ExtentTest test = report.createTest(name, description == null ? "" : description).assignCategory(className);
        if (groups != null && groups.length > 0) {
            test.assignCategory(groups);
        }
        CURRENT_TEST.set(test);
        return test;
    }

    public static ExtentTest currentTest() {
        return CURRENT_TEST.get();
    }

    public static void clearCurrentTest() {
        CURRENT_TEST.remove();
    }

    public static RunArtifacts artifacts() {
        return artifacts == null ? ReportArtifactManager.currentRun() : artifacts;
    }

    public static Path reportPath() {
        return artifacts().reportFile();
    }

    public static synchronized boolean isInitialized() {
        return report != null;
    }

    public static synchronized void recordRuntimeBrowser(WebDriver driver) {
        if (report == null || !(driver instanceof HasCapabilities capable)) {
            return;
        }
        try {
            String browserName = capable.getCapabilities().getBrowserName();
            String browserVersion = capable.getCapabilities().getBrowserVersion();
            String browser = browserName + " " + browserVersion;
            if (RUNTIME_BROWSERS.add(browser)) {
                report.setSystemInfo("Browser runtime: " + browserName, browserVersion);
            }
        } catch (RuntimeException ignored) {
            // Environment metadata is best-effort and must never affect the test.
        }
    }

    public static synchronized void flush() {
        if (report != null) {
            report.flush();
        }
        CURRENT_TEST.remove();
    }
}
