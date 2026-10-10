package com.shopflow.learning.junit;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public final class PriceCalculator {
    private PriceCalculator() {}

    public static BigDecimal addPrices(List<BigDecimal> prices) {
        Objects.requireNonNull(prices, "prices must not be null");
        return prices.stream().map(price -> requireNonNegative(price, "price"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public static BigDecimal multiply(BigDecimal price, int quantity) {
        requireNonNegative(price, "price");
        if (quantity < 0) {
            throw new IllegalArgumentException("quantity must not be negative");
        }
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    public static BigDecimal calculateFinalTotal(BigDecimal subtotal, BigDecimal tax) {
        return requireNonNegative(subtotal, "subtotal").add(requireNonNegative(tax, "tax"));
    }

    public static BigDecimal parsePrice(String value) {
        InputValidator.requireNonBlank("price", value);
        String normalized = value.trim().replaceFirst("^\\$", "");
        try {
            return requireNonNegative(new BigDecimal(normalized), "price");
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("price must be numeric", exception);
        }
    }

    private static BigDecimal requireNonNegative(BigDecimal value, String field) {
        Objects.requireNonNull(value, field + " must not be null");
        if (value.signum() < 0) {
            throw new IllegalArgumentException(field + " must not be negative");
        }
        return value;
    }
}
