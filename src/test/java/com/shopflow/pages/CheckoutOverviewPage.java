package com.shopflow.pages;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import com.shopflow.core.ConfigReader;
import com.shopflow.utils.MoneyUtils;

public final class CheckoutOverviewPage extends BasePage {
    private static final String URL = ConfigReader.baseUrl() + "checkout-step-two.html";
    private static final By PAGE_TITLE = By.cssSelector("[data-test='title']");
    private static final By SUMMARY = By.cssSelector("[data-test='checkout-summary-container']");
    private static final By PRODUCT_NAME = By.cssSelector("[data-test='inventory-item-name']");
    private static final By PRODUCT_PRICE = By.cssSelector("[data-test='inventory-item-price']");
    private static final By ITEM_QUANTITY = By.cssSelector("[data-test='item-quantity']");
    private static final By SUBTOTAL = By.cssSelector("[data-test='subtotal-label']");
    private static final By TAX = By.cssSelector("[data-test='tax-label']");
    private static final By TOTAL = By.cssSelector("[data-test='total-label']");
    private static final By FINISH = By.cssSelector("[data-test='finish']");
    private static final By CANCEL = By.cssSelector("[data-test='cancel']");

    public CheckoutOverviewPage(WebDriver driver) { super(driver); }

    public CheckoutOverviewPage waitUntilLoaded() {
        waits.urlToBe(URL);
        visible(SUMMARY);
        return this;
    }

    public String getHeading() { return text(PAGE_TITLE); }
    public boolean isSummaryDisplayed() { return visible(SUMMARY).isDisplayed(); }
    public List<String> getProductNames() {
        return elements(PRODUCT_NAME).stream().map(e -> e.getText().trim()).toList();
    }

    public Map<String, BigDecimal> getProductPrices() {
        Map<String, BigDecimal> prices = new LinkedHashMap<>();
        for (String name : getProductNames()) {
            WebElement item = visible(InventoryPage.productContainerLocator(name));
            prices.put(name, MoneyUtils.parseDisplayedAmount(item.findElement(PRODUCT_PRICE).getText()));
        }
        return prices;
    }

    public BigDecimal calculateItemsSubtotal() {
        List<BigDecimal> extendedPrices = new ArrayList<>();
        for (String name : getProductNames()) {
            WebElement item = visible(InventoryPage.productContainerLocator(name));
            BigDecimal price = MoneyUtils.parseDisplayedAmount(item.findElement(PRODUCT_PRICE).getText());
            int quantity = Integer.parseInt(item.findElement(ITEM_QUANTITY).getText().trim());
            extendedPrices.add(MoneyUtils.multiply(price, quantity));
        }
        return MoneyUtils.add(extendedPrices);
    }

    public BigDecimal getItemSubtotal() { return MoneyUtils.parseDisplayedAmount(text(SUBTOTAL)); }
    public BigDecimal getTax() { return MoneyUtils.parseDisplayedAmount(text(TAX)); }
    public BigDecimal getTotal() { return MoneyUtils.parseDisplayedAmount(text(TOTAL)); }

    public CheckoutCompletePage finish() {
        click(FINISH);
        return new CheckoutCompletePage(driver).waitUntilLoaded();
    }

    public InventoryPage cancel() {
        click(CANCEL);
        return new InventoryPage(driver).waitUntilLoaded();
    }
}
