package com.shopflow.reporting;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.testng.IConfigurationListener;
import org.testng.IExecutionListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentTest;
import com.shopflow.reporting.FailureEvidenceCollector.FailureEvidence;
import com.shopflow.reporting.ScreenshotManager.ScreenshotEvidence;
import com.shopflow.tests.BaseTest;

public final class TestExecutionListener
        implements ITestListener, IExecutionListener, IConfigurationListener {
    private static final Logger LOG = LoggerFactory.getLogger(TestExecutionListener.class);
    private static final AtomicLong INVOCATION_SEQUENCE = new AtomicLong();
    private static final String INVOCATION_ATTRIBUTE = "shopflow.reporting.invocation-id";
    private static final String DISCOVERY_ATTRIBUTE = "shopflow.reporting.discovery-callback";

    @Override
    public void onExecutionStart() {
        LOG.info("event=testng_execution_started");
    }

    @Override
    public void onStart(ITestContext context) {
        LOG.info("event=test_context_started suite={} test={}",
                context.getSuite().getName(), context.getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        if (isDriverlessDiscoveryCallback(result)) {
            result.setAttribute(DISCOVERY_ATTRIBUTE, Boolean.TRUE);
            return;
        }
        ExtentReportManager.initialize(result.getTestContext().getSuite().getName());
        MDC.put("testMethod", result.getMethod().getMethodName());
        ExtentReportManager.recordRuntimeBrowser(driverFrom(result));
        long sequence = INVOCATION_SEQUENCE.incrementAndGet();
        String invocationId = String.format("%05d-%s-%s", sequence,
                result.getTestClass().getRealClass().getSimpleName(), result.getMethod().getMethodName());
        result.setAttribute(INVOCATION_ATTRIBUTE, invocationId);
        String displayName = result.getTestClass().getRealClass().getSimpleName() + "."
                + result.getMethod().getMethodName() + " [invocation " + String.format("%05d", sequence) + "]";
        ExtentReportManager.startTest(displayName, result.getMethod().getDescription(),
                result.getTestClass().getName(), result.getMethod().getGroups());
        LOG.info("event=test_started id={} suite={} class={} method={}", invocationId,
                result.getTestContext().getSuite().getName(), result.getTestClass().getName(),
                result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        if (isDiscoveryCallback(result)) {
            return;
        }
        long duration = durationMillis(result);
        currentOrCreate(result).pass("PASS | duration=" + duration + " ms");
        LOG.info("event=test_passed id={} durationMs={}", invocationId(result), duration);
        ExtentReportManager.clearCurrentTest();
        MDC.remove("testMethod");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        if (isDiscoveryCallback(result)) {
            return;
        }
        String invocationId = invocationId(result);
        WebDriver driver = driverFrom(result);
        Optional<ScreenshotEvidence> screenshot = ScreenshotManager.capture(driver, invocationId);
        if (screenshot.isEmpty()) {
            LOG.warn("event=screenshot_capture_unavailable id={}", invocationId);
        } else {
            LOG.info("event=screenshot_captured id={} path={}", invocationId,
                    screenshot.get().absolutePath());
        }
        FailureEvidence evidence = FailureEvidenceCollector.collect(result, driver, invocationId, screenshot);

        ExtentTest test = currentOrCreate(result);
        if (result.getThrowable() != null) {
            test.fail(result.getThrowable());
        } else {
            test.fail("Test failed without an attached throwable");
        }
        test.info("Duration: " + durationMillis(result) + " ms");
        test.info("Current URL: " + html(evidence.currentUrl()));
        test.info("Page title: " + html(evidence.pageTitle()));
        test.info("Browser: " + html(evidence.browserName()) + " " + html(evidence.browserVersion()));
        test.info("Browser console entries captured: " + evidence.browserLogs().size());
        screenshot.ifPresent(item -> {
            try {
                test.addScreenCaptureFromPath(item.relativePath(), "Failure screenshot");
            } catch (RuntimeException exception) {
                LOG.warn("event=screenshot_attachment_failed id={} reason={}", invocationId,
                        exception.getClass().getSimpleName());
            }
        });
        if (evidence.diagnosticPath() != null) {
            Path relative = ExtentReportManager.artifacts().runDirectory().relativize(evidence.diagnosticPath());
            test.info("Diagnostic file: " + html(relative.toString().replace('\\', '/')));
        }
        LOG.error("event=test_failed id={} durationMs={} exception={} screenshot={} diagnostic={}",
                invocationId, durationMillis(result), evidence.exceptionType(),
                screenshot.map(item -> item.absolutePath().toString()).orElse("unavailable"),
                evidence.diagnosticPath(), result.getThrowable());
        ExtentReportManager.clearCurrentTest();
        MDC.remove("testMethod");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (isDiscoveryCallback(result)) {
            return;
        }
        String reason = result.getThrowable() == null
                ? "Skipped without an exception reason"
                : result.getThrowable().getClass().getSimpleName() + ": " + result.getThrowable().getMessage();
        currentOrCreate(result).skip(html(reason));
        LOG.warn("event=test_skipped id={} reason={}", invocationId(result), reason);
        ExtentReportManager.clearCurrentTest();
        MDC.remove("testMethod");
    }

    @Override
    public void onConfigurationFailure(ITestResult result) {
        ExtentReportManager.initialize(result.getTestContext().getSuite().getName());
        String name = "Configuration." + result.getMethod().getMethodName() + " [failure]";
        ExtentTest test = ExtentReportManager.startTest(name, "TestNG configuration failure",
                result.getTestClass().getName(), result.getMethod().getGroups());
        if (result.getThrowable() != null) {
            test.fail(result.getThrowable());
        } else {
            test.fail("Configuration failed without an attached throwable");
        }
        LOG.error("event=configuration_failed class={} method={}", result.getTestClass().getName(),
                result.getMethod().getMethodName(), result.getThrowable());
        ExtentReportManager.clearCurrentTest();
        MDC.remove("testMethod");
    }

    @Override
    public void onFinish(ITestContext context) {
        LOG.info("event=test_context_finished suite={} test={} passed={} failed={} skipped={}",
                context.getSuite().getName(), context.getName(), context.getPassedTests().size(),
                context.getFailedTests().size(), context.getSkippedTests().size());
    }

    @Override
    public void onExecutionFinish() {
        if (ExtentReportManager.isInitialized()) {
            ExtentReportManager.flush();
            LOG.info("event=report_finalized report={}", ExtentReportManager.reportPath());
        } else {
            LOG.info("event=report_not_created reason=no_real_testng_test_events");
        }
    }

    private static ExtentTest currentOrCreate(ITestResult result) {
        ExtentTest current = ExtentReportManager.currentTest();
        if (current != null) {
            return current;
        }
        return ExtentReportManager.startTest(
                result.getTestClass().getRealClass().getSimpleName() + "." + result.getMethod().getMethodName()
                        + " [" + invocationId(result) + "]",
                result.getMethod().getDescription(), result.getTestClass().getName(), result.getMethod().getGroups());
    }

    private static WebDriver driverFrom(ITestResult result) {
        Object instance = result.getInstance();
        return instance instanceof BaseTest baseTest ? baseTest.getDriver() : null;
    }

    private static boolean isDriverlessDiscoveryCallback(ITestResult result) {
        Object instance = result.getInstance();
        return instance instanceof BaseTest baseTest && baseTest.getDriver() == null;
    }

    private static boolean isDiscoveryCallback(ITestResult result) {
        return Boolean.TRUE.equals(result.getAttribute(DISCOVERY_ATTRIBUTE))
                || isDriverlessDiscoveryCallback(result);
    }

    private static String invocationId(ITestResult result) {
        Object id = result.getAttribute(INVOCATION_ATTRIBUTE);
        return id == null ? "untracked-" + ReportArtifactManager.safeFileName(result.getMethod().getMethodName()) : id.toString();
    }

    private static long durationMillis(ITestResult result) {
        long end = result.getEndMillis() > 0 ? result.getEndMillis() : System.currentTimeMillis();
        return Math.max(0, end - result.getStartMillis());
    }

    private static String html(String value) {
        if (value == null) {
            return "unavailable";
        }
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
