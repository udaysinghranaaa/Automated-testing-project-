package com.shopflow.pages;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.shopflow.core.ConfigReader;
import com.shopflow.utils.MoneyUtils;

public final class InventoryPage extends BasePage {
    public static final By INVENTORY_CONTAINER =
            By.cssSelector("[data-test='inventory-container']");
    private static final String INVENTORY_URL = ConfigReader.baseUrl() + "inventory.html";
    private static final By MENU_BUTTON = By.id("react-burger-menu-btn");
    private static final By MENU_WRAPPER = By.cssSelector(".bm-menu-wrap");
    private static final By LOGOUT_LINK = By.id("logout_sidebar_link");
    private static final By PAGE_TITLE = By.cssSelector("[data-test='title']");
    private static final By PRODUCT_CARDS = By.cssSelector("[data-test='inventory-item']");
    private static final By PRODUCT_NAME = By.cssSelector("[data-test='inventory-item-name']");
    private static final By PRODUCT_DESCRIPTION =
            By.cssSelector("[data-test='inventory-item-desc']");
    private static final By PRODUCT_PRICE = By.cssSelector("[data-test='inventory-item-price']");
    private static final By PRODUCT_IMAGE = By.cssSelector("img[data-test$='-img']");
    private static final By SORT_DROPDOWN =
            By.cssSelector("[data-test='product-sort-container']");
    private static final By ADD_BUTTON = By.cssSelector("button[data-test^='add-to-cart']");
    private static final By REMOVE_BUTTON = By.cssSelector("button[data-test^='remove']");
    private static final By CART_LINK = By.cssSelector("[data-test='shopping-cart-link']");
    private static final By CART_BADGE = By.cssSelector("[data-test='shopping-cart-badge']");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public InventoryPage waitUntilLoaded() {
        waits.urlToBe(INVENTORY_URL);
        visible(INVENTORY_CONTAINER);
        return this;
    }

    public boolean isLoaded() {
        return currentUrl().equals(INVENTORY_URL)
                && visible(INVENTORY_CONTAINER).isDisplayed();
    }

    public boolean isInventoryDisplayed() {
        return visible(INVENTORY_CONTAINER).isDisplayed();
    }

    public String getHeading() {
        return text(PAGE_TITLE);
    }

    public int getProductCardCount() {
        return elements(PRODUCT_CARDS).size();
    }

    public List<Boolean> getProductCardVisibilityStates() {
        return elements(PRODUCT_CARDS).stream().map(WebElement::isDisplayed).toList();
    }

    public List<String> getProductNames() {
        return elements(PRODUCT_NAME).stream().map(element -> element.getText().trim()).toList();
    }

    public String getProductDescription(String productName) {
        return productContainer(productName).findElement(PRODUCT_DESCRIPTION).getText().trim();
    }

    public int getProductPriceElementCount(String productName) {
        return productContainer(productName).findElements(PRODUCT_PRICE).size();
    }

    public BigDecimal getProductPrice(String productName) {
        return MoneyUtils.parseDisplayedAmount(
                productContainer(productName).findElement(PRODUCT_PRICE).getText());
    }

    public List<BigDecimal> getProductPrices() {
        List<BigDecimal> prices = new ArrayList<>();
        for (WebElement price : elements(PRODUCT_PRICE)) {
            prices.add(MoneyUtils.parseDisplayedAmount(price.getText()));
        }
        return prices;
    }

    public Map<String, BigDecimal> getProductPriceMap() {
        Map<String, BigDecimal> prices = new LinkedHashMap<>();
        for (String productName : getProductNames()) {
            prices.put(productName, getProductPrice(productName));
        }
        return prices;
    }

    public int getProductImageElementCount(String productName) {
        return productContainer(productName).findElements(PRODUCT_IMAGE).size();
    }

    public String getProductImageSource(String productName) {
        return productContainer(productName).findElement(PRODUCT_IMAGE).getAttribute("src");
    }

    public InventoryPage selectSortOption(String value) {
        new Select(clickable(SORT_DROPDOWN)).selectByValue(value);
        waits.until(currentDriver -> value.equals(new Select(
                currentDriver.findElement(SORT_DROPDOWN))
                .getFirstSelectedOption().getAttribute("value")));
        return this;
    }

    public ProductDetailsPage openProductDetails(String productName) {
        productContainer(productName).findElement(PRODUCT_NAME).click();
        return new ProductDetailsPage(driver).waitUntilLoaded();
    }

    public InventoryPage addProductToCart(String productName) {
        productContainer(productName).findElement(ADD_BUTTON).click();
        waits.until(currentDriver -> !productContainer(productName)
                .findElements(REMOVE_BUTTON).isEmpty());
        return this;
    }

    public InventoryPage removeProductFromCart(String productName) {
        productContainer(productName).findElement(REMOVE_BUTTON).click();
        waits.invisible(CART_BADGE);
        waits.until(currentDriver -> !productContainer(productName)
                .findElements(ADD_BUTTON).isEmpty());
        return this;
    }

    public int waitForCartCount(int expectedCount) {
        return waits.until(currentDriver -> {
            List<WebElement> badges = currentDriver.findElements(CART_BADGE);
            if (badges.size() == 1
                    && badges.get(0).getText().equals(String.valueOf(expectedCount))) {
                return expectedCount;
            }
            return null;
        });
    }

    public boolean isCartBadgePresent() {
        return isPresent(CART_BADGE);
    }

    public boolean isAddButtonDisplayed(String productName) {
        return productContainer(productName).findElement(ADD_BUTTON).isDisplayed();
    }

    public boolean isRemoveButtonDisplayed(String productName) {
        return productContainer(productName).findElement(REMOVE_BUTTON).isDisplayed();
    }

    public CartPage openCart() {
        click(CART_LINK);
        return new CartPage(driver).waitUntilLoaded();
    }

    public LoginPage logout() {
        click(MENU_BUTTON);
        waits.until(currentDriver -> {
            org.openqa.selenium.WebElement menuWrapper = currentDriver.findElement(MENU_WRAPPER);
            return "false".equals(menuWrapper.getAttribute("aria-hidden"))
                    && "none".equals(menuWrapper.getCssValue("transform"));
        });
        click(LOGOUT_LINK);
        waits.urlToBe(ConfigReader.baseUrl());
        return new LoginPage(driver).waitUntilReady();
    }

    private WebElement productContainer(String productName) {
        return waits.visible(productContainerLocator(productName));
    }

    static By productContainerLocator(String productName) {
        return By.xpath("//div[@data-test='inventory-item']"
                + "[.//*[@data-test='inventory-item-name' and normalize-space()='"
                + productName + "']]");
    }
}
