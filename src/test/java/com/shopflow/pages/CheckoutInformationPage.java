package com.shopflow.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.shopflow.core.ConfigReader;

public final class CheckoutInformationPage extends BasePage {
    private static final String CHECKOUT_INFORMATION_URL =
            ConfigReader.baseUrl() + "checkout-step-one.html";
    private static final By CHECKOUT_INFORMATION =
            By.cssSelector("[data-test='checkout-info-container']");
    private static final By PAGE_TITLE = By.cssSelector("[data-test='title']");
    private static final By FIRST_NAME_INPUT = By.id("first-name");
    private static final By LAST_NAME_INPUT = By.id("last-name");
    private static final By POSTAL_CODE_INPUT = By.id("postal-code");
    private static final By CONTINUE_BUTTON = By.cssSelector("[data-test='continue']");
    private static final By CANCEL_BUTTON = By.cssSelector("[data-test='cancel']");
    private static final By FORM_ERROR = By.cssSelector("[data-test='error']");
    private static final By CHECKOUT_SUMMARY =
            By.cssSelector("[data-test='checkout-summary-container']");

    public CheckoutInformationPage(WebDriver driver) {
        super(driver);
    }

    public CheckoutInformationPage waitUntilLoaded() {
        waits.urlToBe(CHECKOUT_INFORMATION_URL);
        visible(CHECKOUT_INFORMATION);
        return this;
    }

    public String getHeading() { return text(PAGE_TITLE); }
    public boolean isFormDisplayed() { return visible(CHECKOUT_INFORMATION).isDisplayed(); }
    public boolean isFirstNameDisplayed() { return visible(FIRST_NAME_INPUT).isDisplayed(); }
    public boolean isLastNameDisplayed() { return visible(LAST_NAME_INPUT).isDisplayed(); }
    public boolean isPostalCodeDisplayed() { return visible(POSTAL_CODE_INPUT).isDisplayed(); }
    public boolean isContinueEnabled() { return visible(CONTINUE_BUTTON).isEnabled(); }
    public boolean isCancelEnabled() { return visible(CANCEL_BUTTON).isEnabled(); }

    public CheckoutInformationPage submitForValidation(
            com.shopflow.models.CheckoutInformation information) {
        fill(information);
        click(CONTINUE_BUTTON);
        return this;
    }

    public CheckoutOverviewPage continueWithValidData(
            com.shopflow.models.CheckoutInformation information) {
        fill(information);
        click(CONTINUE_BUTTON);
        return new CheckoutOverviewPage(driver).waitUntilLoaded();
    }

    public String getErrorMessage() { return text(FORM_ERROR); }
    public boolean isOverviewPresent() { return isPresent(CHECKOUT_SUMMARY); }

    public CartPage cancel() {
        click(CANCEL_BUTTON);
        return new CartPage(driver).waitUntilLoaded();
    }

    private void fill(com.shopflow.models.CheckoutInformation information) {
        if (!information.firstName().isEmpty()) { enterText(FIRST_NAME_INPUT, information.firstName()); }
        if (!information.lastName().isEmpty()) { enterText(LAST_NAME_INPUT, information.lastName()); }
        if (!information.postalCode().isEmpty()) { enterText(POSTAL_CODE_INPUT, information.postalCode()); }
    }
}
