package com.shopflow.tests;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.shopflow.core.ConfigReader;
import com.shopflow.pages.InventoryPage;
import com.shopflow.pages.LoginPage;

public class DataDrivenLoginCases extends BaseTest {
    private static final String INVENTORY_URL = ConfigReader.baseUrl() + "inventory.html";

    @DataProvider(name = "loginData", parallel = true)
    public Object[][] loginData() {
        return new Object[][] {
                {"valid standard user", "standard_user", "secret_sauce", true, null},
                {"invalid username", "invalid_user", "secret_sauce", false,
                        "Epic sadface: Username and password do not match any user in this service"},
                {"invalid password", "standard_user", "invalid_password", false,
                        "Epic sadface: Username and password do not match any user in this service"},
                {"both credentials invalid", "invalid_user", "invalid_password", false,
                        "Epic sadface: Username and password do not match any user in this service"},
                {"empty username", "", "secret_sauce", false,
                        "Epic sadface: Username is required"},
                {"empty password", "standard_user", "", false,
                        "Epic sadface: Password is required"},
                {"locked-out account", "locked_out_user", "secret_sauce", false,
                        "Epic sadface: Sorry, this user has been locked out."}
        };
    }

    @Test(dataProvider = "loginData", groups = {"regression", "login"})
    public void loginShouldMatchDatasetExpectation(String caseName, String username,
            String password, boolean shouldSucceed, String expectedError) {
        LoginPage login = loginPage();

        if (shouldSucceed) {
            InventoryPage inventory = login.loginAs(username, password);
            Assert.assertEquals(inventory.currentUrl(), INVENTORY_URL,
                    caseName + ": valid login should reach inventory.");
            Assert.assertTrue(inventory.isInventoryDisplayed(),
                    caseName + ": inventory should be visible.");
            Assert.assertFalse(new LoginPage(getDriver()).isLoginErrorPresent(),
                    caseName + ": successful login should not show an error.");
            return;
        }

        login.submitInvalidLogin(username, password);
        Assert.assertEquals(login.getLoginError(), expectedError,
                caseName + ": exact login error should match.");
        Assert.assertFalse(login.currentUrl().equals(INVENTORY_URL),
                caseName + ": rejected login must not navigate to inventory.");
        Assert.assertFalse(login.isInventoryPresent(),
                caseName + ": rejected login must not expose inventory.");
    }
}
