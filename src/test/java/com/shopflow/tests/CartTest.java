package com.shopflow.tests;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.shopflow.pages.CartPage;
import com.shopflow.pages.InventoryPage;
import com.shopflow.pages.ProductDetailsPage;

@Test(groups = {"regression", "cart"})
public class CartTest extends BaseTest {
    private static final String STANDARD_USERNAME = "standard_user";
    private static final String STANDARD_PASSWORD = "secret_sauce";
    private static final String BACKPACK = "Sauce Labs Backpack";
    private static final String BIKE_LIGHT = "Sauce Labs Bike Light";
    private static final String BOLT_T_SHIRT = "Sauce Labs Bolt T-Shirt";

    private InventoryPage inventory;

    @BeforeMethod(alwaysRun = true)
    public void loginBeforeCartTest() {
        inventory = loginPage().loginAs(STANDARD_USERNAME, STANDARD_PASSWORD);
    }

    @Test(description = "TC_CART_001 - Add one product", groups = "smoke")
    public void addingOneProductShouldUpdateBadgeAndButton() {
        inventory.addProductToCart(BACKPACK);
        int badgeCount = inventory.waitForCartCount(1);

        Assert.assertEquals(String.valueOf(badgeCount), "1",
                "Cart badge should show one selected product.");
        Assert.assertTrue(inventory.isRemoveButtonDisplayed(BACKPACK),
                "Selected product should offer a Remove button on inventory.");
    }

    @Test(description = "TC_CART_002 - Add multiple products")
    public void addingTwoProductsShouldPlaceBothInCart() {
        inventory.addProductToCart(BACKPACK).addProductToCart(BIKE_LIGHT);
        inventory.waitForCartCount(2);

        CartPage cart = inventory.openCart();
        List<String> cartNames = cart.getProductNames();

        Assert.assertEquals(cartNames.size(), 2, "Cart should contain exactly two products.");
        Assert.assertTrue(cartNames.contains(BACKPACK), "Cart should contain the backpack.");
        Assert.assertTrue(cartNames.contains(BIKE_LIGHT), "Cart should contain the bike light.");
    }

    @Test(description = "TC_CART_003 - Cart badge count matches added products")
    public void cartBadgeShouldMatchNumberOfAddedProducts() {
        List<String> selectedProducts = List.of(BACKPACK, BIKE_LIGHT, BOLT_T_SHIRT);
        for (String productName : selectedProducts) {
            inventory.addProductToCart(productName);
        }

        int actualBadgeCount = inventory.waitForCartCount(selectedProducts.size());

        Assert.assertEquals(actualBadgeCount, selectedProducts.size(),
                "Cart badge count should match the number of distinct products added.");
    }

    @Test(description = "TC_CART_004 - Remove product on inventory page")
    public void removingProductOnInventoryShouldClearCartState() {
        inventory.addProductToCart(BACKPACK);
        inventory.waitForCartCount(1);
        inventory.removeProductFromCart(BACKPACK);

        Assert.assertTrue(inventory.isAddButtonDisplayed(BACKPACK),
                "Removed product should return to Add to cart state.");
        Assert.assertFalse(inventory.isCartBadgePresent(),
                "Cart badge should disappear when the cart becomes empty.");
    }

    @Test(description = "TC_CART_005 - Selected products appear on cart page")
    public void cartShouldContainOnlySelectedProducts() {
        List<String> selectedProducts = List.of(BACKPACK, BIKE_LIGHT);
        for (String productName : selectedProducts) {
            inventory.addProductToCart(productName);
        }

        List<String> actualCartNames = inventory.openCart().getProductNames();
        Set<String> expectedNames = new LinkedHashSet<>(selectedProducts);
        Set<String> actualNames = new HashSet<>(actualCartNames);

        Assert.assertEquals(actualCartNames.size(), selectedProducts.size(),
                "Cart should not contain unrelated or duplicate products.");
        Assert.assertEquals(actualNames, expectedNames,
                "Cart product set should exactly match the selected products.");
    }

    @Test(description = "TC_CART_006 - Product prices remain consistent in cart")
    public void cartPricesShouldMatchInventoryPricesByProductName() {
        Map<String, BigDecimal> inventoryPrices = new LinkedHashMap<>();
        for (String productName : List.of(BACKPACK, BIKE_LIGHT)) {
            inventoryPrices.put(productName, inventory.getProductPrice(productName));
            inventory.addProductToCart(productName);
        }

        CartPage cart = inventory.openCart();
        for (Map.Entry<String, BigDecimal> expected : inventoryPrices.entrySet()) {
            BigDecimal cartPrice = cart.getProductPrice(expected.getKey());

            Assert.assertEquals(cartPrice.compareTo(expected.getValue()), 0,
                    "Cart price should match inventory price for: " + expected.getKey());
        }
    }

    @Test(description = "TC_CART_007 - Remove product from cart page")
    public void removingOneProductFromCartShouldKeepTheOther() {
        inventory.addProductToCart(BACKPACK).addProductToCart(BIKE_LIGHT);
        CartPage cart = inventory.openCart();

        cart.removeProduct(BACKPACK);
        int badgeCount = cart.waitForCartCount(1);
        List<String> remainingProducts = cart.getProductNames();

        Assert.assertFalse(remainingProducts.contains(BACKPACK),
                "Removed product should be absent from the cart.");
        Assert.assertTrue(remainingProducts.contains(BIKE_LIGHT),
                "Unremoved product should remain in the cart.");
        Assert.assertEquals(String.valueOf(badgeCount), "1",
                "Cart badge should update after removing one of two products.");
    }

    @Test(description = "TC_CART_008 - Continue Shopping returns to inventory")
    public void continueShoppingShouldPreserveSelectedProduct() {
        inventory.addProductToCart(BACKPACK);
        InventoryPage returnedInventory = inventory.openCart().continueShopping();

        Assert.assertTrue(returnedInventory.isInventoryDisplayed(),
                "Continue Shopping should return to the visible inventory.");
        Assert.assertEquals(String.valueOf(returnedInventory.waitForCartCount(1)), "1",
                "Selected product should remain in the cart.");
        Assert.assertTrue(returnedInventory.isRemoveButtonDisplayed(BACKPACK),
                "Selected product should retain its Remove state.");
    }

    @Test(description = "TC_CART_009 - Cart state survives internal navigation")
    public void cartStateShouldSurviveProductDetailNavigation() {
        inventory.addProductToCart(BACKPACK);
        ProductDetailsPage details = inventory.openProductDetails(BACKPACK);

        Assert.assertEquals(String.valueOf(details.waitForCartCount(1)), "1",
                "Cart badge should remain one on product details.");

        CartPage cart = details.openCart();
        List<String> cartNames = cart.getProductNames();
        int badgeCount = cart.waitForCartCount(1);

        Assert.assertTrue(cartNames.contains(BACKPACK),
                "Selected product should remain in the cart after internal navigation.");
        Assert.assertEquals(badgeCount, cartNames.size(),
                "Cart badge and cart contents should agree.");
    }
}
