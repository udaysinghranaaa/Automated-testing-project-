package com.shopflow.learning.junit;

public final class InputValidator {
    private InputValidator() {}

    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static String requireNonBlank(String fieldName, String value) {
        if (isBlank(value)) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return value;
    }
}
