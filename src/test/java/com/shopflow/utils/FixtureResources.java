package com.shopflow.utils;

import java.net.URISyntaxException;
import java.net.URL;

public final class FixtureResources {
    private static final String ROOT = "advanced-fixtures/";

    private FixtureResources() {
    }

    public static String uri(String fileName) {
        URL resource = FixtureResources.class.getClassLoader().getResource(ROOT + fileName);
        if (resource == null) {
            throw new IllegalArgumentException("Fixture resource not found: " + fileName);
        }
        try {
            return resource.toURI().toString();
        } catch (URISyntaxException exception) {
            throw new IllegalStateException("Invalid fixture resource URI: " + fileName, exception);
        }
    }
}
