package com.shopflow.utils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Objects;

public final class MoneyUtils {
    private MoneyUtils() {
    }

    public static BigDecimal parseDisplayedAmount(String displayedValue) {
        Objects.requireNonNull(displayedValue, "displayedValue must not be null");
        String numericValue = displayedValue.replaceAll("[^0-9.-]", "");
        if (numericValue.isBlank()) {
            throw new IllegalArgumentException(
                    "Displayed value does not contain a monetary amount: " + displayedValue);
        }
        try {
            return new BigDecimal(numericValue);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Displayed value contains an invalid monetary amount: " + displayedValue,
                    exception);
        }
    }

    public static BigDecimal add(Collection<BigDecimal> amounts) {
        Objects.requireNonNull(amounts, "amounts must not be null");
        return amounts.stream().map(amount -> Objects.requireNonNull(amount,
                "amount must not be null")).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal multiply(BigDecimal amount, int quantity) {
        Objects.requireNonNull(amount, "amount must not be null");
        if (quantity < 0) {
            throw new IllegalArgumentException("quantity must not be negative");
        }
        return amount.multiply(BigDecimal.valueOf(quantity));
    }
}
