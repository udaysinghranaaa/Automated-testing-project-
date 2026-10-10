package com.shopflow.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.shopflow.pages.InventoryPage;
import com.shopflow.pages.LoginPage;

@Test(groups = {"regression", "login"})
public class FirstLoginTest extends BaseTest {
    private static final String STANDARD_USERNAME = "standard_user";
    private static final String STANDARD_PASSWORD = "secret_sauce";

    @Test(groups = "smoke")
    public void shouldDisplayExpectedHomepageTitle() {
        Assert.assertEquals(loginPage().pageTitle(), "Swag Labs",
                "The SauceDemo homepage title should match.");
    }

    @Test
    public void shouldDisplayLoginForm() {
        LoginPage loginPage = loginPage();

        Assert.assertTrue(loginPage.isUsernameDisplayed(),
                "Username input should be visible.");
        Assert.assertTrue(loginPage.isPasswordDisplayed(),
                "Password input should be visible.");
        Assert.assertTrue(loginPage.isLoginButtonDisplayed(),
                "Login button should be visible.");
    }

    @Test
    public void shouldEnableLoginButton() {
        Assert.assertTrue(loginPage().isLoginButtonEnabled(),
                "Login button should be enabled.");
    }

    @Test
    public void shouldNavigateToInventoryAfterSuccessfulLogin() {
        InventoryPage inventory = loginPage().loginAs(STANDARD_USERNAME, STANDARD_PASSWORD);

        Assert.assertTrue(inventory.isInventoryDisplayed(),
                "Inventory should be visible after login.");
        Assert.assertTrue(inventory.currentUrl().endsWith("/inventory.html"),
                "A successful login should navigate to the inventory page.");
    }
}
