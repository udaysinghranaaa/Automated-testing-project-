package com.shopflow.learning.junit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class InputValidatorJupiterSpec {
    @ParameterizedTest
    @ValueSource(strings = {"standard_user", "secret_sauce", "checkout value"})
    void acceptsNonBlankValues(String value) {
        assertFalse(InputValidator.isBlank(value));
        assertEquals(value, InputValidator.requireNonBlank("input", value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "   "})
    void identifiesBlankText(String value) {
        assertTrue(InputValidator.isBlank(value));
        assertThrows(IllegalArgumentException.class,
                () -> InputValidator.requireNonBlank("input", value));
    }

    @Test
    void handlesNullInput() {
        assertAll(
                () -> assertTrue(InputValidator.isBlank(null)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> InputValidator.requireNonBlank("input", null)));
    }
}
