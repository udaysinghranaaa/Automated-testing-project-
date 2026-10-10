package com.shopflow.pages;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.shopflow.core.ConfigReader;
import com.shopflow.utils.MoneyUtils;

public final class CartPage extends BasePage {
    private static final String CART_URL = ConfigReader.baseUrl() + "cart.html";
    private static final By CART_LIST = By.cssSelector("[data-test='cart-list']");
    private static final By PRODUCT_NAME = By.cssSelector("[data-test='inventory-item-name']");
    private static final By PRODUCT_PRICE = By.cssSelector("[data-test='inventory-item-price']");
    private static final By REMOVE_BUTTON = By.cssSelector("button[data-test^='remove']");
    private static final By CART_BADGE = By.cssSelector("[data-test='shopping-cart-badge']");
    private static final By CONTINUE_SHOPPING = By.cssSelector("[data-test='continue-shopping']");
    private static final By CHECKOUT_BUTTON = By.cssSelector("[data-test='checkout']");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public CartPage waitUntilLoaded() {
        waits.urlToBe(CART_URL);
        visible(CART_LIST);
        return this;
    }

    public List<String> getProductNames() {
        return elements(PRODUCT_NAME).stream().map(element -> element.getText().trim()).toList();
    }

    public BigDecimal getProductPrice(String productName) {
        return MoneyUtils.parseDisplayedAmount(productContainer(productName)
                .findElement(PRODUCT_PRICE).getText());
    }

    public Map<String, BigDecimal> getProductPrices() {
        Map<String, BigDecimal> prices = new LinkedHashMap<>();
        for (String productName : getProductNames()) {
            prices.put(productName, getProductPrice(productName));
        }
        return prices;
    }

    public CartPage removeProduct(String productName) {
        productContainer(productName).findElement(REMOVE_BUTTON).click();
        waits.until(currentDriver -> currentDriver.findElements(
                InventoryPage.productContainerLocator(productName)).isEmpty());
        return this;
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

    public InventoryPage continueShopping() {
        click(CONTINUE_SHOPPING);
        return new InventoryPage(driver).waitUntilLoaded();
    }

    public CheckoutInformationPage checkout() {
        click(CHECKOUT_BUTTON);
        return new CheckoutInformationPage(driver).waitUntilLoaded();
    }

    private org.openqa.selenium.WebElement productContainer(String productName) {
        return waits.visible(InventoryPage.productContainerLocator(productName));
    }
}
