package com.shopflow.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.shopflow.core.ConfigReader;
import com.shopflow.pages.InventoryPage;
import com.shopflow.pages.LoginPage;

@Test(groups = {"regression", "login"})
public class LoginTest extends BaseTest {
    private static final String STANDARD_USERNAME = "standard_user";
    private static final String LOCKED_OUT_USERNAME = "locked_out_user";
    private static final String VALID_PASSWORD = "secret_sauce";
    private static final String INVALID_USERNAME = "invalid_user";
    private static final String INVALID_PASSWORD = "invalid_password";

    private static final String CREDENTIAL_MISMATCH_ERROR =
            "Epic sadface: Username and password do not match any user in this service";
    private static final String USERNAME_REQUIRED_ERROR = "Epic sadface: Username is required";
    private static final String PASSWORD_REQUIRED_ERROR = "Epic sadface: Password is required";
    private static final String LOCKED_OUT_ERROR =
            "Epic sadface: Sorry, this user has been locked out.";

    @Test(description = "TC_LOGIN_001 - Valid username and password", groups = "smoke")
    public void validCredentialsShouldOpenInventory() {
        InventoryPage inventory = loginPage().loginAs(STANDARD_USERNAME, VALID_PASSWORD);

        Assert.assertTrue(inventory.isInventoryDisplayed(),
                "Inventory should be visible after valid login.");
        Assert.assertTrue(inventory.currentUrl().endsWith("/inventory.html"),
                "Valid login should navigate to the inventory URL.");
    }

    @Test(description = "TC_LOGIN_002 - Invalid username and valid password")
    public void invalidUsernameShouldShowCredentialMismatch() {
        assertLoginError(INVALID_USERNAME, VALID_PASSWORD, CREDENTIAL_MISMATCH_ERROR);
    }

    @Test(description = "TC_LOGIN_003 - Valid username and invalid password")
    public void invalidPasswordShouldShowCredentialMismatch() {
        assertLoginError(STANDARD_USERNAME, INVALID_PASSWORD, CREDENTIAL_MISMATCH_ERROR);
    }

    @Test(description = "TC_LOGIN_004 - Invalid username and password")
    public void invalidCredentialsShouldShowCredentialMismatch() {
        assertLoginError(INVALID_USERNAME, INVALID_PASSWORD, CREDENTIAL_MISMATCH_ERROR);
    }

    @Test(description = "TC_LOGIN_005 - Empty username")
    public void emptyUsernameShouldShowRequiredError() {
        assertLoginError("", VALID_PASSWORD, USERNAME_REQUIRED_ERROR);
    }

    @Test(description = "TC_LOGIN_006 - Empty password")
    public void emptyPasswordShouldShowRequiredError() {
        assertLoginError(STANDARD_USERNAME, "", PASSWORD_REQUIRED_ERROR);
    }

    @Test(description = "TC_LOGIN_007 - Empty username and password")
    public void emptyCredentialsShouldPrioritizeUsernameRequiredError() {
        assertLoginError("", "", USERNAME_REQUIRED_ERROR);
    }

    @Test(description = "TC_LOGIN_008 - Locked-out account")
    public void lockedOutUserShouldShowLockedOutError() {
        assertLoginError(LOCKED_OUT_USERNAME, VALID_PASSWORD, LOCKED_OUT_ERROR);
    }

    @Test(description = "TC_LOGIN_009 - Username and password field interaction")
    public void loginFieldsShouldAcceptAndClearInput() {
        LoginPage login = loginPage();

        Assert.assertTrue(login.isUsernameEnabled(), "Username input should be enabled.");
        Assert.assertTrue(login.isPasswordEnabled(), "Password input should be enabled.");

        login.enterUsername(STANDARD_USERNAME).enterPassword(VALID_PASSWORD);

        Assert.assertEquals(login.usernameValue(), STANDARD_USERNAME,
                "Username input should retain entered text.");
        Assert.assertEquals(login.passwordValue(), VALID_PASSWORD,
                "Password input should retain entered text.");
        Assert.assertEquals(login.passwordType(), "password",
                "Password input should mask its displayed value.");
        Assert.assertFalse(login.passwordValue().isEmpty(),
                "Password input should not be empty after typing.");

        login.clearCredentials();

        Assert.assertEquals(login.usernameValue(), "",
                "Username input should be empty after clear().");
        Assert.assertEquals(login.passwordValue(), "",
                "Password input should be empty after clear().");
    }

    @Test(description = "TC_LOGIN_010 - Logout after successful login")
    public void logoutShouldReturnToReadyLoginForm() {
        InventoryPage inventory = loginPage().loginAs(STANDARD_USERNAME, VALID_PASSWORD);
        LoginPage login = inventory.logout();

        Assert.assertEquals(login.currentUrl(), ConfigReader.baseUrl(),
                "Logout should return to the login URL.");
        Assert.assertTrue(login.isUsernameDisplayed(),
                "Login form should be visible after logout.");
        Assert.assertFalse(login.isInventoryPresent(),
                "Inventory should not remain in the DOM after logout.");
    }

    private void assertLoginError(String username, String password, String expectedMessage) {
        LoginPage login = loginPage().submitInvalidLogin(username, password);

        Assert.assertEquals(login.getLoginError(), expectedMessage,
                "Login validation message should match SauceDemo behavior.");
        Assert.assertTrue(login.isLoginErrorDisplayed(), "Login error should be visible.");
        Assert.assertFalse(login.isInventoryPresent(),
                "A rejected login must not display inventory.");
    }
}
