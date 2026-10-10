package com.shopflow.tests;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.shopflow.core.ConfigReader;
import com.shopflow.pages.InventoryPage;
import com.shopflow.pages.ProductDetailsPage;

@Test(groups = {"regression", "products"})
public class ProductTest extends BaseTest {
    private static final String INVENTORY_URL = ConfigReader.baseUrl() + "inventory.html";
    private static final String STANDARD_USERNAME = "standard_user";
    private static final String STANDARD_PASSWORD = "secret_sauce";
    private static final String REFERENCE_PRODUCT = "Sauce Labs Backpack";

    private InventoryPage inventory;

    @BeforeMethod(alwaysRun = true)
    public void loginBeforeProductTest() {
        inventory = loginPage().loginAs(STANDARD_USERNAME, STANDARD_PASSWORD);
    }

    @Test(description = "TC_PRODUCT_001 - Inventory page loads", groups = "smoke")
    public void inventoryPageShouldLoad() {
        Assert.assertEquals(inventory.currentUrl(), INVENTORY_URL,
                "Inventory URL should identify the products page.");
        Assert.assertEquals(inventory.getHeading(), "Products",
                "Products heading should identify the inventory page.");
        Assert.assertTrue(inventory.isInventoryDisplayed(),
                "Inventory container should be visible.");
    }

    @Test(description = "TC_PRODUCT_002 - Product list is not empty")
    public void productListShouldNotBeEmpty() {
        int productCardCount = inventory.getProductCardCount();

        Assert.assertFalse(productCardCount == 0, "Product list should not be empty.");
        Assert.assertTrue(productCardCount > 0,
                "Product-card count should be greater than zero.");
        for (boolean productCardDisplayed : inventory.getProductCardVisibilityStates()) {
            Assert.assertTrue(productCardDisplayed,
                    "Every returned product card should be visible.");
        }
    }

    @Test(description = "TC_PRODUCT_003 - Product names and descriptions are valid")
    public void productsShouldHaveNamesAndDescriptions() {
        for (String productName : inventory.getProductNames()) {
            String description = inventory.getProductDescription(productName);

            Assert.assertFalse(productName.isBlank(), "Product name should not be blank.");
            Assert.assertFalse(description.isBlank(),
                    "Description should not be blank for product: " + productName);
        }
    }

    @Test(description = "TC_PRODUCT_004 - Product prices and image sources are valid")
    public void productsShouldHaveNonNegativePricesAndUsableImageSources() {
        for (String productName : inventory.getProductNames()) {
            Assert.assertEquals(inventory.getProductPriceElementCount(productName), 1,
                    "Exactly one price should exist for product: " + productName);
            BigDecimal price = inventory.getProductPrice(productName);
            Assert.assertTrue(price.compareTo(BigDecimal.ZERO) >= 0,
                    "Price must not be negative for product: " + productName);

            Assert.assertEquals(inventory.getProductImageElementCount(productName), 1,
                    "Exactly one image should exist for product: " + productName);
            String imageSource = inventory.getProductImageSource(productName);
            Assert.assertNotNull(imageSource,
                    "Image source should exist for product: " + productName);
            Assert.assertFalse(imageSource.isBlank(),
                    "Image source should not be blank for product: " + productName);
        }
    }

    @Test(description = "TC_SORT_001 - Name A to Z")
    public void productsShouldSortByNameAscending() {
        inventory.selectSortOption("az");
        List<String> actualNames = inventory.getProductNames();
        List<String> expectedNames = new ArrayList<>(actualNames);
        Collections.sort(expectedNames, Comparator.naturalOrder());

        Assert.assertEquals(actualNames, expectedNames,
                "Displayed product names should be sorted A to Z.");
    }

    @Test(description = "TC_SORT_002 - Name Z to A")
    public void productsShouldSortByNameDescending() {
        inventory.selectSortOption("za");
        List<String> actualNames = inventory.getProductNames();
        List<String> expectedNames = new ArrayList<>(actualNames);
        Collections.sort(expectedNames, Comparator.reverseOrder());

        Assert.assertEquals(actualNames, expectedNames,
                "Displayed product names should be sorted Z to A.");
    }

    @Test(description = "TC_SORT_003 - Price low to high")
    public void productsShouldSortByPriceAscending() {
        inventory.selectSortOption("lohi");
        List<BigDecimal> actualPrices = inventory.getProductPrices();
        List<BigDecimal> expectedPrices = new ArrayList<>(actualPrices);
        Collections.sort(expectedPrices, Comparator.naturalOrder());

        Assert.assertEquals(actualPrices, expectedPrices,
                "Displayed prices should be sorted from low to high.");
    }

    @Test(description = "TC_SORT_004 - Price high to low")
    public void productsShouldSortByPriceDescending() {
        inventory.selectSortOption("hilo");
        List<BigDecimal> actualPrices = inventory.getProductPrices();
        List<BigDecimal> expectedPrices = new ArrayList<>(actualPrices);
        Collections.sort(expectedPrices, Comparator.reverseOrder());

        Assert.assertEquals(actualPrices, expectedPrices,
                "Displayed prices should be sorted from high to low.");
    }

    @Test(description = "TC_DETAIL_001 - Product details match inventory")
    public void productDetailsShouldMatchInventoryCard() {
        String expectedName = REFERENCE_PRODUCT;
        String expectedDescription = inventory.getProductDescription(REFERENCE_PRODUCT);
        BigDecimal expectedPrice = inventory.getProductPrice(REFERENCE_PRODUCT);

        ProductDetailsPage details = inventory.openProductDetails(REFERENCE_PRODUCT);

        Assert.assertEquals(details.getProductName(), expectedName,
                "Detail name should match the selected card.");
        Assert.assertEquals(details.getDescription(), expectedDescription,
                "Detail description should match the selected card.");
        Assert.assertEquals(details.getPrice().compareTo(expectedPrice), 0,
                "Detail price should match the selected card.");
    }

    @Test(description = "TC_DETAIL_002 - Back to Products navigation")
    public void backToProductsShouldReturnToInventory() {
        ProductDetailsPage details = inventory.openProductDetails(REFERENCE_PRODUCT);
        InventoryPage returnedInventory = details.backToProducts();

        Assert.assertEquals(returnedInventory.currentUrl(), INVENTORY_URL,
                "Back to products should return to the inventory URL.");
        Assert.assertTrue(returnedInventory.isInventoryDisplayed(),
                "Product listing should be visible after returning from details.");
    }
}
