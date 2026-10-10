package com.shopflow.reporting;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;

public final class BrowserLogCollector {
    private static final int MAX_ENTRIES = 50;

    private BrowserLogCollector() {
    }

    public static List<String> collect(WebDriver driver) {
        List<String> result = new ArrayList<>();
        if (driver == null) {
            return result;
        }
        try {
            Set<String> availableTypes = driver.manage().logs().getAvailableLogTypes();
            if (!availableTypes.contains(LogType.BROWSER)) {
                result.add("Browser console log type is not supported by this driver session.");
                return result;
            }
            int count = 0;
            for (LogEntry entry : driver.manage().logs().get(LogType.BROWSER)) {
                if (count++ >= MAX_ENTRIES) {
                    result.add("Browser console output truncated after " + MAX_ENTRIES + " entries.");
                    break;
                }
                result.add(Instant.ofEpochMilli(entry.getTimestamp()) + " " + entry.getLevel()
                        + " " + redact(entry.getMessage()));
            }
            if (result.isEmpty()) {
                result.add("No browser console messages were available at failure time.");
            }
        } catch (RuntimeException exception) {
            result.add("Browser console unavailable: " + exception.getClass().getSimpleName());
        }
        return result;
    }

    private static String redact(String message) {
        if (message == null) {
            return "";
        }
        String bounded = message.substring(0, Math.min(message.length(), 2_000));
        return bounded.replaceAll("(?i)(password|token|authorization|cookie|secret)([=:]\\s*)([^\\s&]+)", "$1$2[REDACTED]");
    }
}
