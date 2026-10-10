package com.shopflow.reporting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;

import com.shopflow.reporting.ScreenshotManager.ScreenshotEvidence;

public final class FailureEvidenceCollector {
    private static final int MAX_STACK_FRAMES = 30;

    private FailureEvidenceCollector() {
    }

    public static FailureEvidence collect(
            ITestResult result,
            WebDriver driver,
            String invocationId,
            Optional<ScreenshotEvidence> screenshot) {
        String currentUrl = safeBrowserValue(driver, BrowserValue.URL);
        String pageTitle = safeBrowserValue(driver, BrowserValue.TITLE);
        String browserName = "unavailable";
        String browserVersion = "unavailable";
        if (driver instanceof HasCapabilities hasCapabilities) {
            try {
                Capabilities capabilities = hasCapabilities.getCapabilities();
                browserName = String.valueOf(capabilities.getBrowserName());
                browserVersion = String.valueOf(capabilities.getBrowserVersion());
            } catch (RuntimeException ignored) {
                // Best-effort evidence: keep unavailable values.
            }
        }

        List<String> browserLogs = BrowserLogCollector.collect(driver);
        String exceptionType = result.getThrowable() == null
                ? "unavailable"
                : result.getThrowable().getClass().getName();
        String exceptionMessage = result.getThrowable() == null
                ? "No exception message supplied"
                : bounded(result.getThrowable().getMessage(), 4_000);

        FailureEvidence evidence = new FailureEvidence(
                OffsetDateTime.now().toString(),
                result.getTestContext().getSuite().getName(),
                result.getTestContext().getName(),
                result.getTestClass().getName(),
                result.getMethod().getMethodName(),
                exceptionType,
                exceptionMessage,
                currentUrl,
                pageTitle,
                browserName,
                browserVersion,
                screenshot.map(ScreenshotEvidence::relativePath).orElse("not captured"),
                browserLogs,
                boundedStackTrace(result.getThrowable()));

        Path diagnostic = writeDiagnostic(invocationId, evidence);
        return evidence.withDiagnosticPath(diagnostic);
    }

    private static Path writeDiagnostic(String invocationId, FailureEvidence evidence) {
        Path file = ExtentReportManager.artifacts().diagnosticsDirectory()
                .resolve(ReportArtifactManager.safeFileName(invocationId) + ".txt").normalize();
        try {
            Files.writeString(file, evidence.asText(), StandardOpenOption.CREATE_NEW);
            return file;
        } catch (IOException exception) {
            return null;
        }
    }

    private static String safeBrowserValue(WebDriver driver, BrowserValue value) {
        if (driver == null) {
            return "unavailable";
        }
        try {
            return bounded(value == BrowserValue.URL ? driver.getCurrentUrl() : driver.getTitle(), 2_000);
        } catch (RuntimeException exception) {
            return "unavailable (" + exception.getClass().getSimpleName() + ")";
        }
    }

    private static String boundedStackTrace(Throwable throwable) {
        if (throwable == null) {
            return "No stack trace supplied";
        }
        StringBuilder builder = new StringBuilder(throwable.toString()).append(System.lineSeparator());
        StackTraceElement[] frames = throwable.getStackTrace();
        for (int index = 0; index < Math.min(frames.length, MAX_STACK_FRAMES); index++) {
            builder.append("  at ").append(frames[index]).append(System.lineSeparator());
        }
        if (frames.length > MAX_STACK_FRAMES) {
            builder.append("  ... stack trace truncated").append(System.lineSeparator());
        }
        return builder.toString();
    }

    private static String bounded(String value, int limit) {
        if (value == null || value.isBlank()) {
            return "unavailable";
        }
        return value.substring(0, Math.min(value.length(), limit));
    }

    private enum BrowserValue { URL, TITLE }

    public record FailureEvidence(
            String timestamp,
            String suiteName,
            String testName,
            String className,
            String methodName,
            String exceptionType,
            String exceptionMessage,
            String currentUrl,
            String pageTitle,
            String browserName,
            String browserVersion,
            String screenshotPath,
            List<String> browserLogs,
            String stackTrace,
            Path diagnosticPath) {

        private FailureEvidence(
                String timestamp, String suiteName, String testName, String className, String methodName,
                String exceptionType, String exceptionMessage, String currentUrl, String pageTitle,
                String browserName, String browserVersion, String screenshotPath,
                List<String> browserLogs, String stackTrace) {
            this(timestamp, suiteName, testName, className, methodName, exceptionType, exceptionMessage,
                    currentUrl, pageTitle, browserName, browserVersion, screenshotPath,
                    List.copyOf(browserLogs), stackTrace, null);
        }

        private FailureEvidence withDiagnosticPath(Path path) {
            return new FailureEvidence(timestamp, suiteName, testName, className, methodName,
                    exceptionType, exceptionMessage, currentUrl, pageTitle, browserName,
                    browserVersion, screenshotPath, browserLogs, stackTrace, path);
        }

        public String asText() {
            return "timestamp=" + timestamp + System.lineSeparator()
                    + "suite=" + suiteName + System.lineSeparator()
                    + "test=" + testName + System.lineSeparator()
                    + "class=" + className + System.lineSeparator()
                    + "method=" + methodName + System.lineSeparator()
                    + "exception.type=" + exceptionType + System.lineSeparator()
                    + "exception.message=" + exceptionMessage + System.lineSeparator()
                    + "current.url=" + currentUrl + System.lineSeparator()
                    + "page.title=" + pageTitle + System.lineSeparator()
                    + "browser.name=" + browserName + System.lineSeparator()
                    + "browser.version=" + browserVersion + System.lineSeparator()
                    + "screenshot=" + screenshotPath + System.lineSeparator()
                    + "browser.logs=" + System.lineSeparator() + String.join(System.lineSeparator(), browserLogs)
                    + System.lineSeparator() + "stack.trace=" + System.lineSeparator() + stackTrace;
        }
    }
}
