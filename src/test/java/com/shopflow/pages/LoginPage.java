package com.shopflow.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public final class LoginPage extends BasePage {
    private static final By USERNAME_INPUT = By.id("user-name");
    private static final By PASSWORD_INPUT = By.id("password");
    private static final By LOGIN_BUTTON = By.id("login-button");
    private static final By LOGIN_ERROR = By.cssSelector("[data-test='error']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage waitUntilReady() {
        visible(USERNAME_INPUT);
        visible(PASSWORD_INPUT);
        clickable(LOGIN_BUTTON);
        return this;
    }

    public boolean isUsernameDisplayed() {
        return visible(USERNAME_INPUT).isDisplayed();
    }

    public boolean isPasswordDisplayed() {
        return visible(PASSWORD_INPUT).isDisplayed();
    }

    public boolean isLoginButtonDisplayed() {
        return visible(LOGIN_BUTTON).isDisplayed();
    }

    public boolean isLoginButtonEnabled() {
        return visible(LOGIN_BUTTON).isEnabled();
    }

    public boolean isUsernameEnabled() {
        return visible(USERNAME_INPUT).isEnabled();
    }

    public boolean isPasswordEnabled() {
        return visible(PASSWORD_INPUT).isEnabled();
    }

    public LoginPage enterUsername(String username) {
        enterText(USERNAME_INPUT, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        enterText(PASSWORD_INPUT, password);
        return this;
    }

    public void submit() {
        click(LOGIN_BUTTON);
    }

    public InventoryPage loginAs(String username, String password) {
        enterUsername(username).enterPassword(password).submit();
        return new InventoryPage(driver).waitUntilLoaded();
    }

    public LoginPage submitInvalidLogin(String username, String password) {
        enterUsername(username).enterPassword(password).submit();
        visible(LOGIN_ERROR);
        return this;
    }

    public String getLoginError() {
        return text(LOGIN_ERROR);
    }

    public boolean isLoginErrorDisplayed() {
        return visible(LOGIN_ERROR).isDisplayed();
    }

    public boolean isInventoryPresent() {
        return isPresent(InventoryPage.INVENTORY_CONTAINER);
    }

    public boolean isLoginErrorPresent() {
        return isPresent(LOGIN_ERROR);
    }

    public String usernameValue() {
        return visible(USERNAME_INPUT).getAttribute("value");
    }

    public String passwordValue() {
        return visible(PASSWORD_INPUT).getAttribute("value");
    }

    public String passwordType() {
        return visible(PASSWORD_INPUT).getAttribute("type");
    }

    public LoginPage clearCredentials() {
        visible(USERNAME_INPUT).clear();
        visible(PASSWORD_INPUT).clear();
        return this;
    }
}
