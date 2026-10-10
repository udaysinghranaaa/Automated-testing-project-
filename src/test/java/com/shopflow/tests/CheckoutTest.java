package com.shopflow.tests;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.shopflow.core.ConfigReader;
import com.shopflow.models.CheckoutInformation;
import com.shopflow.pages.CartPage;
import com.shopflow.pages.CheckoutCompletePage;
import com.shopflow.pages.CheckoutInformationPage;
import com.shopflow.pages.CheckoutOverviewPage;
import com.shopflow.pages.InventoryPage;

@Test(groups = {"regression", "checkout"})
public class CheckoutTest extends BaseTest {
    private static final String INVENTORY_URL = ConfigReader.baseUrl() + "inventory.html";
    private static final String CART_URL = ConfigReader.baseUrl() + "cart.html";
    private static final String CHECKOUT_INFORMATION_URL = ConfigReader.baseUrl() + "checkout-step-one.html";
    private static final String CHECKOUT_OVERVIEW_URL = ConfigReader.baseUrl() + "checkout-step-two.html";
    private static final String CHECKOUT_COMPLETE_URL = ConfigReader.baseUrl() + "checkout-complete.html";
    private static final String STANDARD_USERNAME = "standard_user";
    private static final String STANDARD_PASSWORD = "secret_sauce";
    private static final String BACKPACK = "Sauce Labs Backpack";
    private static final String BIKE_LIGHT = "Sauce Labs Bike Light";
    private static final CheckoutInformation VALID_INFORMATION =
            new CheckoutInformation("Test", "User", "201310");
    private static final String FIRST_NAME_REQUIRED = "Error: First Name is required";
    private static final String LAST_NAME_REQUIRED = "Error: Last Name is required";
    private static final String POSTAL_CODE_REQUIRED = "Error: Postal Code is required";
    private static final String COMPLETE_HEADER_TEXT = "Thank you for your order!";
    private static final String COMPLETE_DETAIL_TEXT =
            "Your order has been dispatched, and will arrive just as fast as the pony can get there!";

    private InventoryPage inventory;

    @BeforeMethod(alwaysRun = true)
    public void loginBeforeCheckoutTest() {
        inventory = loginPage().loginAs(STANDARD_USERNAME, STANDARD_PASSWORD);
    }

    @Test(description = "TC_CHECKOUT_001 - Navigate from cart to checkout")
    public void cartCheckoutShouldOpenCheckoutInformation() {
        CartPage cart = prepareCart(List.of(BACKPACK)).openCart();
        Assert.assertEquals(cart.getProductNames(), List.of(BACKPACK),
                "Cart should contain the selected product before checkout.");

        CheckoutInformationPage information = cart.checkout();
        Assert.assertEquals(information.currentUrl(), CHECKOUT_INFORMATION_URL,
                "Checkout should navigate to the information URL.");
        Assert.assertEquals(information.getHeading(), "Checkout: Your Information",
                "Checkout information heading should be displayed.");
        Assert.assertTrue(information.isFormDisplayed(),
                "Checkout information container should be visible.");
    }

    @Test(description = "TC_CHECKOUT_002 - Verify checkout information form")
    public void checkoutInformationFormShouldExposeRequiredControls() {
        CheckoutInformationPage information = reachCheckoutInformation(List.of(BACKPACK));
        Assert.assertTrue(information.isFirstNameDisplayed(), "First-name input should be visible.");
        Assert.assertTrue(information.isLastNameDisplayed(), "Last-name input should be visible.");
        Assert.assertTrue(information.isPostalCodeDisplayed(), "Postal-code input should be visible.");
        Assert.assertTrue(information.isContinueEnabled(), "Continue control should be enabled.");
        Assert.assertTrue(information.isCancelEnabled(), "Cancel control should be enabled.");
    }

    @Test(description = "TC_CHECKOUT_003 - Empty first name")
    public void emptyFirstNameShouldShowRequiredError() {
        CheckoutInformationPage information = reachCheckoutInformation(List.of(BACKPACK));
        information.submitForValidation(new CheckoutInformation("", "User", "201310"));
        assertFormError(information, FIRST_NAME_REQUIRED);
    }

    @Test(description = "TC_CHECKOUT_004 - Empty last name")
    public void emptyLastNameShouldShowRequiredError() {
        CheckoutInformationPage information = reachCheckoutInformation(List.of(BACKPACK));
        information.submitForValidation(new CheckoutInformation("Test", "", "201310"));
        assertFormError(information, LAST_NAME_REQUIRED);
    }

    @Test(description = "TC_CHECKOUT_005 - Empty postal code")
    public void emptyPostalCodeShouldShowRequiredError() {
        CheckoutInformationPage information = reachCheckoutInformation(List.of(BACKPACK));
        information.submitForValidation(new CheckoutInformation("Test", "User", ""));
        assertFormError(information, POSTAL_CODE_REQUIRED);
    }

    @Test(description = "TC_CHECKOUT_006 - All checkout fields empty")
    public void emptyCheckoutFormShouldReportFirstNameFirst() {
        CheckoutInformationPage information = reachCheckoutInformation(List.of(BACKPACK));
        information.submitForValidation(new CheckoutInformation("", "", ""));
        assertFormError(information, FIRST_NAME_REQUIRED);
    }

    @Test(description = "TC_CHECKOUT_007 - Valid checkout information")
    public void validCheckoutInformationShouldOpenOverview() {
        CheckoutOverviewPage overview = reachCheckoutInformation(List.of(BACKPACK))
                .continueWithValidData(VALID_INFORMATION);
        Assert.assertEquals(overview.currentUrl(), CHECKOUT_OVERVIEW_URL,
                "Valid information should navigate to checkout overview.");
        Assert.assertEquals(overview.getHeading(), "Checkout: Overview",
                "Overview heading should identify the page.");
        Assert.assertTrue(overview.isSummaryDisplayed(), "Checkout summary should be visible.");
    }

    @Test(description = "TC_CHECKOUT_008 - Correct products in overview")
    public void overviewShouldContainExactlySelectedProducts() {
        List<String> selectedProducts = List.of(BACKPACK, BIKE_LIGHT);
        CheckoutOverviewPage overview = reachCheckoutOverview(selectedProducts);
        List<String> actualProductList = overview.getProductNames();
        Set<String> actualProducts = new LinkedHashSet<>(actualProductList);
        Set<String> expectedProducts = new LinkedHashSet<>(selectedProducts);

        Assert.assertEquals(actualProductList.size(), selectedProducts.size(),
                "Overview should not contain unrelated or duplicate products.");
        Assert.assertEquals(actualProducts, expectedProducts,
                "Overview product set should exactly match selected products.");
    }

    @Test(description = "TC_CHECKOUT_009 - Product prices match cart")
    public void overviewPricesShouldMatchCartPricesByProduct() {
        CartPage cart = prepareCart(List.of(BACKPACK, BIKE_LIGHT)).openCart();
        Map<String, BigDecimal> cartPrices = cart.getProductPrices();
        CheckoutOverviewPage overview = cart.checkout().continueWithValidData(VALID_INFORMATION);
        Map<String, BigDecimal> overviewPrices = overview.getProductPrices();

        Assert.assertEquals(overviewPrices.keySet(), cartPrices.keySet(),
                "Overview and cart should contain the same product identities.");
        assertPriceMapsEqual(overviewPrices, cartPrices,
                "Overview price should match cart price for: ");
    }

    @Test(description = "TC_CHECKOUT_010 - Item subtotal calculation")
    public void displayedSubtotalShouldEqualQuantityTimesUnitPrice() {
        CheckoutOverviewPage overview = reachCheckoutOverview(List.of(BACKPACK, BIKE_LIGHT));
        assertMoneyEquals(overview.getItemSubtotal(), overview.calculateItemsSubtotal(),
                "Displayed subtotal should equal the sum of quantity multiplied by unit price.");
    }

    @Test(description = "TC_CHECKOUT_011 - Total and tax consistency")
    public void displayedTotalShouldEqualSubtotalPlusTax() {
        CheckoutOverviewPage overview = reachCheckoutOverview(List.of(BACKPACK, BIKE_LIGHT));
        assertMoneyEquals(overview.getTotal(), overview.getItemSubtotal().add(overview.getTax()),
                "Displayed total should equal displayed subtotal plus displayed tax.");
    }

    @Test(description = "TC_CHECKOUT_012 - Cancel checkout information")
    public void cancelingCheckoutInformationShouldReturnToCart() {
        CartPage cart = reachCheckoutInformation(List.of(BACKPACK)).cancel();
        Assert.assertEquals(cart.currentUrl(), CART_URL,
                "Information-page Cancel should return to the cart.");
        Assert.assertEquals(cart.getProductNames(), List.of(BACKPACK),
                "Canceling information should preserve the cart product.");
        Assert.assertEquals(String.valueOf(cart.waitForCartCount(1)), "1",
                "Cart badge should remain one after canceling checkout information.");
    }

    @Test(description = "TC_CHECKOUT_013 - Cancel checkout overview")
    public void cancelingCheckoutOverviewShouldReturnToInventory() {
        InventoryPage returnedInventory = reachCheckoutOverview(List.of(BACKPACK)).cancel();
        Assert.assertEquals(returnedInventory.currentUrl(), INVENTORY_URL,
                "Overview Cancel should return to inventory.");
        Assert.assertEquals(String.valueOf(returnedInventory.waitForCartCount(1)), "1",
                "Selected cart state should remain after canceling overview.");
        Assert.assertTrue(returnedInventory.isRemoveButtonDisplayed(BACKPACK),
                "Selected product should remain in its Remove state.");
    }

    @Test(description = "TC_CHECKOUT_014 - Successful order confirmation", groups = "smoke")
    public void finishingCheckoutShouldShowOrderConfirmation() {
        CheckoutCompletePage complete = reachCheckoutOverview(List.of(BACKPACK)).finish();
        Assert.assertEquals(complete.currentUrl(), CHECKOUT_COMPLETE_URL,
                "Finish should navigate to checkout complete.");
        Assert.assertEquals(complete.getHeading(), "Checkout: Complete!",
                "Complete-page heading should identify the page.");
        Assert.assertEquals(complete.getConfirmationHeading(), COMPLETE_HEADER_TEXT,
                "Order confirmation heading should match live behavior.");
        Assert.assertEquals(complete.getConfirmationMessage(), COMPLETE_DETAIL_TEXT,
                "Order confirmation detail should match live behavior.");
    }

    @Test(description = "TC_CHECKOUT_015 - Back Home after completion")
    public void backHomeAfterCompletionShouldReturnToResetInventory() {
        InventoryPage returnedInventory = reachCheckoutOverview(List.of(BACKPACK)).finish().backHome();
        Assert.assertEquals(returnedInventory.currentUrl(), INVENTORY_URL,
                "Back Home should return to inventory.");
        Assert.assertEquals(returnedInventory.getHeading(), "Products",
                "Products heading should be visible after returning home.");
        Assert.assertFalse(returnedInventory.isCartBadgePresent(),
                "Completed checkout should leave no cart badge.");
        Assert.assertTrue(returnedInventory.isAddButtonDisplayed(BACKPACK),
                "Purchased product should return to Add to cart state.");
    }

    @Test(description = "TC_CHECKOUT_016 - Full end-to-end purchase journey", groups = "e2e")
    public void completePurchaseJourneyShouldPreserveDataAndFinishOrder() {
        List<String> selectedProducts = List.of(BACKPACK, BIKE_LIGHT);
        Map<String, BigDecimal> inventoryPrices = new LinkedHashMap<>();
        Assert.assertEquals(inventory.currentUrl(), INVENTORY_URL,
                "E2E journey should begin on inventory after login.");
        Assert.assertTrue(inventory.isInventoryDisplayed(),
                "Inventory should be visible before product selection.");

        for (int index = 0; index < selectedProducts.size(); index++) {
            String productName = selectedProducts.get(index);
            inventoryPrices.put(productName, inventory.getProductPrice(productName));
            inventory.addProductToCart(productName).waitForCartCount(index + 1);
        }
        Assert.assertEquals(String.valueOf(inventory.waitForCartCount(2)), "2",
                "Cart badge should reflect both selected products.");

        CartPage cart = inventory.openCart();
        List<String> cartNames = cart.getProductNames();
        Map<String, BigDecimal> cartPrices = cart.getProductPrices();
        Assert.assertEquals(new LinkedHashSet<>(cartNames), new LinkedHashSet<>(selectedProducts),
                "Cart should contain both selected products.");
        assertPriceMapsEqual(cartPrices, inventoryPrices,
                "Cart price should match inventory price for: ");

        CheckoutOverviewPage overview = cart.checkout().continueWithValidData(VALID_INFORMATION);
        Assert.assertEquals(new LinkedHashSet<>(overview.getProductNames()),
                new LinkedHashSet<>(selectedProducts),
                "Overview should preserve selected product identities.");
        assertPriceMapsEqual(overview.getProductPrices(), cartPrices,
                "Overview price should match cart price for: ");
        assertMoneyEquals(overview.getItemSubtotal(), overview.calculateItemsSubtotal(),
                "E2E subtotal should match item calculations.");
        assertMoneyEquals(overview.getTotal(), overview.getItemSubtotal().add(overview.getTax()),
                "E2E total should equal subtotal plus tax.");

        CheckoutCompletePage complete = overview.finish();
        Assert.assertEquals(complete.getConfirmationHeading(), COMPLETE_HEADER_TEXT,
                "E2E journey should finish with order confirmation.");
        InventoryPage returnedInventory = complete.backHome();
        Assert.assertEquals(returnedInventory.currentUrl(), INVENTORY_URL,
                "E2E journey should return to inventory.");
        Assert.assertFalse(returnedInventory.isCartBadgePresent(),
                "E2E completed order should clear the cart badge.");
    }

    private InventoryPage prepareCart(List<String> productNames) {
        for (int index = 0; index < productNames.size(); index++) {
            inventory.addProductToCart(productNames.get(index)).waitForCartCount(index + 1);
        }
        return inventory;
    }

    private CheckoutInformationPage reachCheckoutInformation(List<String> productNames) {
        return prepareCart(productNames).openCart().checkout();
    }

    private CheckoutOverviewPage reachCheckoutOverview(List<String> productNames) {
        return reachCheckoutInformation(productNames).continueWithValidData(VALID_INFORMATION);
    }

    private void assertFormError(CheckoutInformationPage information, String expectedError) {
        Assert.assertEquals(information.getErrorMessage(), expectedError,
                "Checkout validation message should match live behavior.");
        Assert.assertEquals(information.currentUrl(), CHECKOUT_INFORMATION_URL,
                "Invalid checkout information should remain on the form.");
        Assert.assertFalse(information.isOverviewPresent(),
                "Invalid checkout information must not display an overview.");
    }

    private void assertPriceMapsEqual(Map<String, BigDecimal> actual,
            Map<String, BigDecimal> expected, String messagePrefix) {
        Assert.assertEquals(actual.keySet(), expected.keySet(),
                "Product identities should match before comparing prices.");
        for (Map.Entry<String, BigDecimal> expectedEntry : expected.entrySet()) {
            assertMoneyEquals(actual.get(expectedEntry.getKey()), expectedEntry.getValue(),
                    messagePrefix + expectedEntry.getKey());
        }
    }

    private void assertMoneyEquals(BigDecimal actual, BigDecimal expected, String message) {
        Assert.assertEquals(actual.compareTo(expected), 0, message);
    }
}
