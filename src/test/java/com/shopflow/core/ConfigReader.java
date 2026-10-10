package com.shopflow.core;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Locale;
import java.util.Properties;

public final class ConfigReader {
    private static final String CONFIG_RESOURCE = "config.properties";
    private static final Properties PROPERTIES = loadProperties();

    private ConfigReader() {
    }

    public static String baseUrl() {
        return required("base.url");
    }

    public static String browser() {
        String browser = required("browser").toLowerCase(Locale.ROOT);
        if (!browser.equals("chrome") && !browser.equals("edge")) {
            throw new IllegalStateException(
                    "browser must be 'chrome' or 'edge', but was: " + browser);
        }
        return browser;
    }

    public static boolean headless() {
        return booleanValue("headless", required("headless"));
    }

    public static boolean headless(String override) {
        return override == null || override.isBlank()
                ? headless()
                : booleanValue("headless", override.trim());
    }

    public static int parallelThreads() {
        String configuredThreads = required("parallel.threads");
        try {
            int threads = Integer.parseInt(configuredThreads);
            if (threads < 1 || threads > 8) {
                throw new IllegalStateException("parallel.threads must be between 1 and 8");
            }
            return threads;
        } catch (NumberFormatException exception) {
            throw new IllegalStateException(
                    "parallel.threads must be a whole number: " + configuredThreads,
                    exception);
        }
    }

    public static Duration explicitWait() {
        String configuredSeconds = required("explicit.wait.seconds");
        try {
            long seconds = Long.parseLong(configuredSeconds);
            if (seconds <= 0) {
                throw new IllegalStateException("explicit.wait.seconds must be greater than zero");
            }
            return Duration.ofSeconds(seconds);
        } catch (NumberFormatException exception) {
            throw new IllegalStateException(
                    "explicit.wait.seconds must be a whole number: " + configuredSeconds,
                    exception);
        }
    }

    private static String required(String key) {
        String value = System.getProperty(key, PROPERTIES.getProperty(key));
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required configuration property: " + key);
        }
        return value.trim();
    }

    private static boolean booleanValue(String key, String value) {
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        throw new IllegalStateException(key + " must be true or false, but was: " + value);
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = ConfigReader.class.getClassLoader()
                .getResourceAsStream(CONFIG_RESOURCE)) {
            if (input == null) {
                throw new IllegalStateException(
                        "Configuration resource was not found on the classpath: " + CONFIG_RESOURCE);
            }
            properties.load(input);
            return properties;
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Unable to load configuration resource: " + CONFIG_RESOURCE, exception);
        }
    }
}
