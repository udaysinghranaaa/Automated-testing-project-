package com.shopflow.learning.junit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

class PriceCalculatorJupiterSpec {
    private BigDecimal basePrice;

    @BeforeAll
    static void beforeAll() { System.out.println("JUnit lifecycle: @BeforeAll"); }

    @BeforeEach
    void beforeEach() { basePrice = new BigDecimal("9.99"); }

    @AfterEach
    void afterEach() { basePrice = null; }

    @AfterAll
    static void afterAll() { System.out.println("JUnit lifecycle: @AfterAll"); }

    @Test
    void addsPricesAndHandlesEmptyCart() {
        assertAll(
                () -> assertEquals(new BigDecimal("15.50"), PriceCalculator.addPrices(
                        List.of(new BigDecimal("10.00"), new BigDecimal("5.50")))),
                () -> assertEquals(BigDecimal.ZERO, PriceCalculator.addPrices(List.of())),
                () -> assertNotNull(basePrice));
    }

    @Test
    void calculatesFinalTotal() {
        assertEquals(new BigDecimal("35.46"),
                PriceCalculator.calculateFinalTotal(new BigDecimal("32.97"), new BigDecimal("2.49")));
    }

    @Test
    void rejectsNullAndNegativeValues() {
        assertAll(
                () -> assertThrows(NullPointerException.class, () -> PriceCalculator.addPrices(null)),
                () -> assertThrows(NullPointerException.class, () -> PriceCalculator.multiply(null, 1)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> PriceCalculator.multiply(BigDecimal.ONE, -1)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> PriceCalculator.calculateFinalTotal(BigDecimal.ONE, new BigDecimal("-0.01"))));
    }

    @ParameterizedTest(name = "{0} x {1} = {2}")
    @CsvSource({"9.99, 2, 19.98", "0.00, 5, 0.00", "12.50, 0, 0.00"})
    void multipliesPriceByQuantity(String price, int quantity, String expected) {
        BigDecimal result = PriceCalculator.multiply(new BigDecimal(price), quantity);
        assertEquals(0, result.compareTo(new BigDecimal(expected)));
        assertTrue(result.signum() >= 0);
    }

    @ParameterizedTest
    @MethodSource("validPriceStrings")
    void parsesValidPriceStrings(String input, String expected) {
        BigDecimal parsed = PriceCalculator.parsePrice(input);
        assertEquals(0, parsed.compareTo(new BigDecimal(expected)));
        assertFalse(parsed.signum() < 0);
    }

    static Stream<Arguments> validPriceStrings() {
        return Stream.of(Arguments.of("$29.99", "29.99"),
                Arguments.of(" 7.50 ", "7.50"), Arguments.of("0", "0"));
    }
}
