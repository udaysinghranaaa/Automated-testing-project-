package com.shopflow.learning.testng;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Test(groups = "learning")
public class AssertionDemo {

    @Test
    public void hardAssertionsShouldValidateImmediately() {
        String pageTitle = "Swag Labs";
        List<String> cart = List.of("Sauce Labs Backpack");
        Object missingBadge = null;

        Assert.assertEquals(pageTitle, "Swag Labs", "Actual title should equal expected title.");
        Assert.assertNotEquals(pageTitle, "Other Store", "Title should identify SauceDemo.");
        Assert.assertTrue(cart.contains("Sauce Labs Backpack"), "Cart should contain backpack.");
        Assert.assertFalse(cart.isEmpty(), "Cart should not be empty.");
        Assert.assertNotNull(cart, "Cart collection should exist.");
        Assert.assertNull(missingBadge, "Empty-cart badge representation should be null.");
    }

    @Test(expectedExceptions = AssertionError.class)
    public void controlledHardFailureShouldStopCurrentMethod() {
        Assert.fail("Controlled educational failure expected by TestNG.");
        Assert.fail("This line is unreachable because hard assertions stop the method.");
    }

    @Test
    public void softAssertionsShouldCollectChecksUntilAssertAll() {
        SoftAssert softAssert = new SoftAssert();

        softAssert.assertEquals("Products", "Products", "Inventory heading should match.");
        softAssert.assertTrue(new java.math.BigDecimal("39.98").signum() > 0,
                "Checkout subtotal should be positive.");
        softAssert.assertFalse(false, "Logout state should not expose inventory.");
        softAssert.assertAll();
    }

    @Test
    public void controlledSoftFailuresShouldSurfaceAtAssertAll() {
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(1, 2, "First controlled mismatch");
        softAssert.assertTrue(false, "Second controlled mismatch");

        boolean assertionErrorThrown = false;
        try {
            softAssert.assertAll();
        } catch (AssertionError expected) {
            assertionErrorThrown = true;
            Assert.assertTrue(expected.getMessage().contains("First controlled mismatch"),
                    "assertAll should report collected failure context.");
            Assert.assertTrue(expected.getMessage().contains("Second controlled mismatch"),
                    "assertAll should report every collected failure.");
        }
        Assert.assertTrue(assertionErrorThrown,
                "Soft assertion mismatches must fail when assertAll is called.");
    }
}
