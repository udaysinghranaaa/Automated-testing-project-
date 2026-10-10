# Selenium Live Coding — 20 Exercises

Each exercise has a problem, hint, complete core solution, explanation and follow-up. Imports/constructors are omitted only where obvious.

## 1. Start and close Chrome

**Problem:** Open SauceDemo and always end the session. **Hint:** `try/finally`.

```java
WebDriver driver = new ChromeDriver();
try {
    driver.get("https://www.saucedemo.com/");
    Assert.assertEquals(driver.getTitle(), "Swag Labs");
} finally {
    driver.quit();
}
```

**Explanation:** cleanup survives assertion failure. **Follow-up:** move lifecycle to TestNG `@BeforeMethod/@AfterMethod(alwaysRun=true)`.

## 2. Locate and submit login

**Problem:** Login with stable locators. **Hint:** SauceDemo inputs have IDs.

```java
driver.findElement(By.id("user-name")).sendKeys("standard_user");
driver.findElement(By.id("password")).sendKeys("secret_sauce");
driver.findElement(By.id("login-button")).click();
```

**Explanation:** IDs are concise and stable here. **Follow-up:** read demo data from configuration.

## 3. Wait for successful login

**Problem:** Assert navigation without sleep. **Hint:** URL and inventory state.

```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
wait.until(ExpectedConditions.urlContains("inventory.html"));
WebElement inventory = wait.until(ExpectedConditions.visibilityOfElementLocated(
        By.cssSelector("[data-test='inventory-container']")));
Assert.assertTrue(inventory.isDisplayed());
```

**Explanation:** the condition models actual readiness. **Follow-up:** return an `InventoryPage`.

## 4. Assert a negative login

**Problem:** Verify exact error and no inventory. **Hint:** use `findElements` for absence.

```java
driver.findElement(By.id("user-name")).sendKeys("invalid_user");
driver.findElement(By.id("password")).sendKeys("secret_sauce");
driver.findElement(By.id("login-button")).click();
String error = new WebDriverWait(driver, Duration.ofSeconds(10))
        .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-test='error']")))
        .getText();
Assert.assertEquals(error, "Epic sadface: Username and password do not match any user in this service");
Assert.assertTrue(driver.findElements(By.cssSelector("[data-test='inventory-container']")).isEmpty());
```

**Explanation:** positive error plus negative authorization state. **Follow-up:** parameterize invalid cases.

## 5. Build a Login Page Object

**Problem:** Hide locators and expose intent. **Hint:** return next page.

```java
final class LoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final By user = By.id("user-name"), pass = By.id("password"), submit = By.id("login-button");
    LoginPage(WebDriver d) { driver=d; wait=new WebDriverWait(d, Duration.ofSeconds(10)); }
    InventoryPage loginAs(String u, String p) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(user)).sendKeys(u);
        driver.findElement(pass).sendKeys(p);
        wait.until(ExpectedConditions.elementToBeClickable(submit)).click();
        return new InventoryPage(driver).waitUntilReady();
    }
}
```

**Explanation:** tests no longer know login DOM details. **Follow-up:** add invalid login without pretending it returns inventory.

## 6. Read all product names

**Problem:** Convert elements to trimmed names. **Hint:** stream the list.

```java
List<String> names = driver.findElements(By.cssSelector("[data-test='inventory-item-name']"))
        .stream().map(WebElement::getText).map(String::trim).toList();
Assert.assertFalse(names.isEmpty());
```

**Explanation:** `findElements` safely returns an empty list. **Follow-up:** assert uniqueness.

## 7. Verify A–Z sorting

**Problem:** Compare UI order independently. **Hint:** copy before sorting.

```java
List<String> actual = readNames();
List<String> expected = new ArrayList<>(actual);
expected.sort(String.CASE_INSENSITIVE_ORDER);
Assert.assertEquals(actual, expected);
```

**Explanation:** never sort `actual` itself or the assertion becomes meaningless. **Follow-up:** implement Z–A.

## 8. Parse product money safely

**Problem:** Convert `$29.99` to decimal. **Hint:** strip symbol, use `BigDecimal`.

```java
static BigDecimal money(String text) {
    String normalized = text.trim().replace("$", "");
    if (normalized.isBlank()) throw new IllegalArgumentException("Price is blank");
    return new BigDecimal(normalized);
}
```

**Explanation:** decimal arithmetic avoids `double` error. **Follow-up:** support a specified locale/currency contract.

## 9. Select native dropdown

**Problem:** Set price low-to-high. **Hint:** `Select` only for `<select>`.

```java
WebElement element = new WebDriverWait(driver, Duration.ofSeconds(10))
        .until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-test='product-sort-container']")));
new Select(element).selectByValue("lohi");
```

**Explanation:** Selenium dispatches native selection semantics. **Follow-up:** wait until prices are ordered.

## 10. Exact cart contents

**Problem:** Ensure selected names exactly equal cart names. **Hint:** sets detect extras/missing items.

```java
Set<String> expected = Set.of("Sauce Labs Backpack", "Sauce Labs Bike Light");
Set<String> actual = new HashSet<>(cart.productNames());
Assert.assertEquals(actual, expected);
Assert.assertEquals(cart.itemCount(), expected.size());
```

**Explanation:** exact collection comparison is stronger than badge-only checks. **Follow-up:** preserve duplicate quantities if the product supports them.

## 11. Wait for logout

**Problem:** Log out and prove unauthenticated readiness. **Hint:** wait before and after click.

```java
wait.until(ExpectedConditions.elementToBeClickable(By.id("react-burger-menu-btn"))).click();
wait.until(ExpectedConditions.elementToBeClickable(By.id("logout_sidebar_link"))).click();
wait.until(ExpectedConditions.urlToBe("https://www.saucedemo.com/"));
Assert.assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-button"))).isEnabled());
Assert.assertTrue(driver.findElements(By.cssSelector("[data-test='inventory-container']")).isEmpty());
```

**Explanation:** preserves business behavior without JS or retry. **Follow-up:** diagnose an intercepted click with evidence.

## 12. Native alert

**Problem:** accept an alert and assert text. **Hint:** `alertIsPresent`.

```java
driver.findElement(By.id("simple-alert")).click();
Alert alert = wait.until(ExpectedConditions.alertIsPresent());
Assert.assertEquals(alert.getText(), "Fixture alert");
alert.accept();
```

**Explanation:** DOM locators cannot address browser alerts. **Follow-up:** prompt input and dismiss.

## 13. Frame and default content

**Problem:** interact inside a frame, then return. **Hint:** switch context.

```java
wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("demo-frame")));
driver.findElement(By.id("inside-frame")).click();
driver.switchTo().defaultContent();
Assert.assertTrue(driver.findElement(By.id("outside-frame")).isDisplayed());
```

**Explanation:** element searches operate only in current context. **Follow-up:** use `parentFrame` for nesting.

## 14. New window safely

**Problem:** identify a new handle without assuming order. **Hint:** set difference.

```java
String parent = driver.getWindowHandle();
Set<String> before = driver.getWindowHandles();
driver.findElement(By.id("open-window")).click();
wait.until(d -> d.getWindowHandles().size() == before.size() + 1);
String child = driver.getWindowHandles().stream().filter(h -> !before.contains(h)).findFirst().orElseThrow();
driver.switchTo().window(child);
driver.close();
driver.switchTo().window(parent);
```

**Explanation:** handle set order is not a contract. **Follow-up:** manage multiple children.

## 15. File upload

**Problem:** upload without automating the OS dialog. **Hint:** input path.

```java
Path file = Files.createTempFile("shopflow-", ".txt");
try {
    Files.writeString(file, "fixture");
    driver.findElement(By.cssSelector("input[type='file']")).sendKeys(file.toAbsolutePath().toString());
    Assert.assertTrue(driver.findElement(By.id("selected-file")).getText().contains(file.getFileName().toString()));
} finally {
    Files.deleteIfExists(file);
}
```

**Explanation:** WebDriver sets the file input directly. **Follow-up:** CI path permissions.

## 16. Open shadow root

**Problem:** read an element inside open shadow DOM. **Hint:** `SearchContext`.

```java
WebElement host = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("shadow-host")));
SearchContext root = host.getShadowRoot();
String value = root.findElement(By.cssSelector("[data-testid='shadow-message']")).getText();
Assert.assertEquals(value, "Inside shadow DOM");
```

**Explanation:** ordinary document search does not cross a shadow boundary. **Follow-up:** closed roots are intentionally inaccessible.

## 17. FluentWait dynamic state

**Problem:** poll until a button becomes enabled. **Hint:** ignore stale during replacement.

```java
Wait<WebDriver> fluent = new FluentWait<>(driver)
        .withTimeout(Duration.ofSeconds(8)).pollingEvery(Duration.ofMillis(200))
        .ignoring(StaleElementReferenceException.class);
WebElement ready = fluent.until(d -> {
    WebElement e = d.findElement(By.id("delayed-button"));
    return e.isEnabled() ? e : null;
});
ready.click();
```

**Explanation:** returns the newly located ready element. **Follow-up:** why blanket exception ignoring is dangerous.

## 18. Thread-safe driver manager

**Problem:** isolate drivers per parallel worker. **Hint:** remove in `finally`.

```java
private static final ThreadLocal<WebDriver> LOCAL = new ThreadLocal<>();
static void set(WebDriver d) { LOCAL.set(Objects.requireNonNull(d)); }
static WebDriver get() { return Objects.requireNonNull(LOCAL.get(), "No driver"); }
static void quit() {
    try { if (LOCAL.get()!=null) LOCAL.get().quit(); }
    finally { LOCAL.remove(); }
}
```

**Explanation:** each TestNG worker sees only its own value. **Follow-up:** isolate report nodes too.

## 19. Failure screenshot listener

**Problem:** save a unique screenshot on failure. **Hint:** invocation identity plus timestamp/sequence.

```java
public void onTestFailure(ITestResult r) {
    WebDriver d = ((BaseTest) r.getInstance()).getDriver();
    if (d instanceof TakesScreenshot ts) {
        Path out = Path.of("target", "screenshots",
                r.getTestClass().getRealClass().getSimpleName() + "-" +
                r.getMethod().getMethodName() + "-" + System.nanoTime() + ".png");
        try { Files.createDirectories(out.getParent()); Files.write(out, ts.getScreenshotAs(OutputType.BYTES)); }
        catch (IOException e) { /* log; preserve original failure */ }
    }
}
```

**Explanation:** evidence failure cannot replace test failure. **Follow-up:** attach relative path to Extent.

## 20. Data-driven login

**Problem:** test success and rejection rows with one method. **Hint:** include expected outcome.

```java
@DataProvider(name="login")
Object[][] login() { return new Object[][] {
    {"standard_user", "secret_sauce", true},
    {"invalid_user", "secret_sauce", false}
}; }

@Test(dataProvider="login")
void loginOutcome(String user, String pass, boolean expectedSuccess) {
    LoginPage page = new LoginPage(driver).waitUntilReady();
    page.submit(user, pass);
    Assert.assertEquals(page.isInventoryPresent(), expectedSuccess);
}
```

**Explanation:** rows are invocations, not separate methods; each needs isolated lifecycle. **Follow-up:** include exact expected error per negative row.

## Live-coding habits

Narrate the observable condition, use stable locators, write the assertion before adding abstractions, and always include cleanup. If blocked by environment, explain the evidence and preserve correctness—never “make it pass” with sleeps or weak checks.
