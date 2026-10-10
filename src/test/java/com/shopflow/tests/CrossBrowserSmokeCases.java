package com.shopflow.tests;

import org.openqa.selenium.HasCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.shopflow.models.CheckoutInformation;
import com.shopflow.pages.CartPage;
import com.shopflow.pages.CheckoutCompletePage;
import com.shopflow.pages.CheckoutOverviewPage;
import com.shopflow.pages.InventoryPage;
import com.shopflow.pages.LoginPage;

public class CrossBrowserSmokeCases extends BaseTest {
    private static final Logger LOG = LoggerFactory.getLogger(CrossBrowserSmokeCases.class);
    private static final String BACKPACK = "Sauce Labs Backpack";

    @Test(groups = "cross-browser", description = "Chrome/Edge critical commerce journey")
    @Parameters("browser")
    public void criticalCommerceJourneyShouldWork(@Optional("chrome") String requestedBrowser) {
        String runtimeBrowser = ((HasCapabilities) getDriver()).getCapabilities().getBrowserName();
        String runtimeVersion = ((HasCapabilities) getDriver()).getCapabilities().getBrowserVersion();
        LOG.info("event=cross_browser_runtime requested={} actual={} version={}",
                requestedBrowser, runtimeBrowser, runtimeVersion);
        Assert.assertTrue(matchesRequestedBrowser(requestedBrowser, runtimeBrowser),
                "Requested browser must match the real WebDriver capabilities.");

        InventoryPage inventory = loginPage().loginAs("standard_user", "secret_sauce");
        Assert.assertTrue(inventory.isInventoryDisplayed(), "Inventory should load after login.");
        Assert.assertTrue(inventory.getProductCardCount() > 0, "Product listing should not be empty.");

        CartPage cart = inventory.addProductToCart(BACKPACK).openCart();
        Assert.assertTrue(cart.getProductNames().contains(BACKPACK),
                "Selected product should appear in the cart.");

        CheckoutOverviewPage overview = cart.checkout().continueWithValidData(
                new CheckoutInformation("Cross", "Browser", "201310"));
        CheckoutCompletePage complete = overview.finish();
        Assert.assertEquals(complete.getConfirmationHeading(), "Thank you for your order!",
                "Checkout should complete successfully.");

        LoginPage login = complete.backHome().logout();
        Assert.assertTrue(login.isUsernameDisplayed(), "Logout should restore the login form.");
        Assert.assertFalse(login.isInventoryPresent(), "Inventory must not remain after logout.");
    }

    private static boolean matchesRequestedBrowser(String requested, String actual) {
        if ("edge".equalsIgnoreCase(requested)) {
            return "MicrosoftEdge".equalsIgnoreCase(actual) || "msedge".equalsIgnoreCase(actual);
        }
        return requested.equalsIgnoreCase(actual);
    }
}
