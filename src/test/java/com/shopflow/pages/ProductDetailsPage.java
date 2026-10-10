package com.shopflow.pages;

import java.math.BigDecimal;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.shopflow.utils.MoneyUtils;

public final class ProductDetailsPage extends BasePage {
    private static final By DETAIL_CONTAINER =
            By.cssSelector(".inventory_details_container[data-test='inventory-item']");
    private static final By PRODUCT_NAME = By.cssSelector("[data-test='inventory-item-name']");
    private static final By PRODUCT_DESCRIPTION =
            By.cssSelector("[data-test='inventory-item-desc']");
    private static final By PRODUCT_PRICE =
            By.cssSelector("[data-test='inventory-item-price']");
    private static final By ADD_BUTTON = By.cssSelector("button[data-test^='add-to-cart']");
    private static final By REMOVE_BUTTON = By.cssSelector("button[data-test^='remove']");
    private static final By BACK_TO_PRODUCTS = By.cssSelector("[data-test='back-to-products']");
    private static final By CART_LINK = By.cssSelector("[data-test='shopping-cart-link']");
    private static final By CART_BADGE = By.cssSelector("[data-test='shopping-cart-badge']");

    public ProductDetailsPage(WebDriver driver) {
        super(driver);
    }

    public ProductDetailsPage waitUntilLoaded() {
        waits.urlContains("inventory-item.html");
        visible(DETAIL_CONTAINER);
        return this;
    }

    public String getProductName() {
        return text(PRODUCT_NAME);
    }

    public String getDescription() {
        return text(PRODUCT_DESCRIPTION);
    }

    public BigDecimal getPrice() {
        return MoneyUtils.parseDisplayedAmount(text(PRODUCT_PRICE));
    }

    public ProductDetailsPage addToCart() {
        click(ADD_BUTTON);
        visible(REMOVE_BUTTON);
        return this;
    }

    public ProductDetailsPage removeFromCart() {
        click(REMOVE_BUTTON);
        visible(ADD_BUTTON);
        return this;
    }

    public InventoryPage backToProducts() {
        click(BACK_TO_PRODUCTS);
        return new InventoryPage(driver).waitUntilLoaded();
    }

    public int waitForCartCount(int expectedCount) {
        return waits.until(currentDriver -> {
            List<org.openqa.selenium.WebElement> badges = currentDriver.findElements(CART_BADGE);
            if (badges.size() == 1
                    && badges.get(0).getText().equals(String.valueOf(expectedCount))) {
                return expectedCount;
            }
            return null;
        });
    }

    public CartPage openCart() {
        click(CART_LINK);
        return new CartPage(driver).waitUntilLoaded();
    }
}
