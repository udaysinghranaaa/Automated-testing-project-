package com.shopflow.core;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;

import com.shopflow.tests.ChromeOptionsFactory;
import com.shopflow.tests.EdgeOptionsFactory;

public final class DriverFactory {
    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        return createDriver(ConfigReader.browser(), ConfigReader.headless());
    }

    public static WebDriver createDriver(String browser, boolean headless) {
        if (browser == null || browser.isBlank()) {
            throw new IllegalArgumentException("Browser name must not be blank");
        }
        return switch (browser.trim().toLowerCase(java.util.Locale.ROOT)) {
            case "chrome" -> new ChromeDriver(ChromeOptionsFactory.configured(headless));
            case "edge" -> new EdgeDriver(EdgeOptionsFactory.configured(headless));
            default -> throw new IllegalArgumentException(
                    "Unsupported browser '" + browser + "'. Supported browsers: chrome, edge.");
        };
    }
}
