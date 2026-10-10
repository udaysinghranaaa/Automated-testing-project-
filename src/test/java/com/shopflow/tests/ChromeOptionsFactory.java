package com.shopflow.tests;

import java.util.Map;
import java.util.logging.Level;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;

public final class ChromeOptionsFactory {

    private ChromeOptionsFactory() {
    }

    public static ChromeOptions passwordPromptsDisabled() {
        return configured(false);
    }

    public static ChromeOptions configured(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_leak_detection", false));
        LoggingPreferences logging = new LoggingPreferences();
        logging.enable(LogType.BROWSER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logging);
        if (headless) {
            options.addArguments("--headless=new", "--window-size=1920,1080");
        }
        return options;
    }
}
