# Phase 2 Notes — Complete Login Automation

## Test cases explained in easy Hinglish

1. **TC_LOGIN_001 — Valid credentials:** `standard_user` aur correct password se login karke inventory page aur URL verify hote hain.
2. **TC_LOGIN_002 — Invalid username:** Galat username aur correct password par exact credential-mismatch error verify hota hai.
3. **TC_LOGIN_003 — Invalid password:** Correct username aur galat password par wahi secure generic error expected hai.
4. **TC_LOGIN_004 — Both invalid:** Dono values galat hone par login reject aur inventory absent honi chahiye.
5. **TC_LOGIN_005 — Empty username:** Password filled hai, lekin username blank hone par `Username is required` aata hai.
6. **TC_LOGIN_006 — Empty password:** Username filled hai, lekin password blank hone par `Password is required` aata hai.
7. **TC_LOGIN_007 — Both empty:** Dono blank hone par application pehle username validation report karti hai.
8. **TC_LOGIN_008 — Locked user:** Public demo account `locked_out_user` ko exact locked-out message milta hai.
9. **TC_LOGIN_009 — Field interaction:** Fields enabled hain, text accept/retain karte hain, password type masked hai, aur `clear()` kaam karta hai.
10. **TC_LOGIN_010 — Logout:** Valid login ke baad menu se logout karke ready login form, base URL, aur inventory absence verify hoti hai.

Har test ke pehle `@BeforeMethod` fresh Chrome open karta hai. Har test ke baad
`@AfterMethod(alwaysRun = true)` `quit()` call karta hai, isliye tests independent hain.

## Important Java and Selenium code

- `private static final` constants duplicate URLs, credentials, messages aur locators ko centralize karte hain.
- `WebDriver` browser-control interface hai; `ChromeDriver` uski Chrome implementation hai.
- `WebDriverWait` maximum wait duration rakhta hai. Condition jaldi true ho toh wait turant finish hota hai.
- `ExpectedConditions.visibilityOfElementLocated(...)` element present aur visible hone tak wait karta hai.
- `ExpectedConditions.elementToBeClickable(...)` visible aur enabled element return karta hai.
- `submitLogin()` repeated typing/clicking logic ko ek reusable method mein rakhta hai.
- `loginSuccessfully()` login ke saath inventory readiness bhi return karta hai.
- `assertLoginError()` exact message aur unsuccessful navigation dono validate karta hai.
- `isElementPresent()` `findElements()` use karta hai, isliye missing element exception nahi throw karta.

## Locator examples from the SauceDemo DOM

Production tests stable IDs aur `data-test` attribute prefer karte hain. Neeche ke baaki
examples locator learning ke liye hain; har style ko production mein use karna zaroori nahi.

```java
By.id("user-name");                         // username input
By.name("password");                       // password input
By.className("login_logo");                // one CSS class only
By.cssSelector("[data-test='login-button']");
By.xpath("//input[@id='login-button']");
By.xpath("//div[@class='login-box']//input[@name='password']"); // relative XPath
```

`By.className()` mein spaces wali complete class value pass nahi kar sakte. Relative XPath
`//` se current document mein matching descendant search karta hai; absolute XPath
`/html/body/...` layout change se easily break hota hai.

## CSS Selector vs XPath

| CSS Selector | XPath |
| --- | --- |
| Usually short aur readable | Complex DOM relationships express kar sakta hai |
| IDs, classes, attributes ke liye strong choice | Parent/ancestor aur text matching support karta hai |
| Parent ki taraf traverse nahi karta | `ancestor`, `parent`, `following-sibling` axes available hain |
| Example: `[data-test='error']` | Example: `//h3[@data-test='error']` |

Best locator woh hai jo unique, readable aur UI changes ke against stable ho. SauceDemo par
unique `id` aur dedicated `data-test` attributes preferred hain.

## findElement vs findElements

```java
WebElement button = driver.findElement(By.id("login-button"));
List<WebElement> errors = driver.findElements(By.cssSelector("[data-test='error']"));
```

- `findElement()` first matching element deta hai; match na mile toh `NoSuchElementException`.
- `findElements()` all matches ki list deta hai; match na mile toh empty list.
- Presence/absence checks ke liye `findElements().isEmpty()` useful hai.

## Explicit vs implicit waits

- **Explicit wait:** Chosen condition aur chosen element ke liye wait. Clear intent aur controlled timeout deta hai.
- **Implicit wait:** Har element lookup par global polling delay apply karta hai.
- Dono ko mix karne se unpredictable total wait ho sakta hai. Framework Phase 2 mein sirf explicit waits use karta hai.
- `Thread.sleep()` fixed delay hai: condition early complete ho tab bhi full wait karta hai aur slow/flaky tests create kar sakta hai.

## Positive vs negative testing

- **Positive test** valid input dekar expected successful journey validate karta hai, jaise TC_LOGIN_001.
- **Negative test** invalid, blank ya restricted input dekar validation and access protection check karta hai, jaise TC_LOGIN_002–008.
- Negative test sirf error text nahi, successful page absent hai ye bhi assert kare toh stronger hota hai.

## Ten Selenium interview questions with answers

1. **WebDriver kya hai?** Browser automation commands define karne wala interface.
2. **`close()` aur `quit()` mein difference?** `close()` current window close karta hai; `quit()` complete session aur all windows end karta hai.
3. **Stable locator kaise choose karte hain?** Unique ID ya dedicated test attribute prefer karke dynamic classes/layout paths avoid karte hain.
4. **Explicit wait ka benefit?** Specific UI state ka condition-based wait deta hai without fixed sleep.
5. **Implicit aur explicit wait mix kyun nahi karne chahiye?** Combined polling se timeout duration unpredictable ho sakti hai.
6. **`findElement()` missing element par kya karta hai?** `NoSuchElementException` throw karta hai.
7. **`findElements()` missing elements par kya karta hai?** Empty list return karta hai.
8. **Har test ke liye fresh driver kyun?** Cookies, login aur page state leak ko prevent karke isolation deta hai.
9. **`alwaysRun=true` ka fayda?** Test failure ke baad bhi teardown execute karne ka intent deta hai.
10. **Selenium Manager kya karta hai?** Installed browser ke compatible driver ko automatically resolve/manage karta hai.

## Five practical Java coding exercises with solutions

### 1. Empty string check

```java
static boolean isBlank(String value) {
    return value == null || value.trim().isEmpty();
}
```

### 2. Count passed results

```java
static long countPassed(List<Boolean> results) {
    return results.stream().filter(Boolean::booleanValue).count();
}
```

### 3. Reusable expected-error comparison

```java
static boolean errorMatches(String actual, String expected) {
    return expected.equals(actual);
}
```

### 4. Remove duplicate test data while preserving order

```java
static List<String> uniqueUsers(List<String> users) {
    return new ArrayList<>(new LinkedHashSet<>(users));
}
```

### 5. Create immutable credentials

```java
record Credentials(String username, String password) {
}

Credentials standardUser = new Credentials("standard_user", "secret_sauce");
```

Exercises 2 and 4 ke liye `java.util.List`, aur exercise 4 ke liye
`java.util.ArrayList` plus `java.util.LinkedHashSet` imports chahiye. `record` Java 17
mein concise immutable data carrier provide karta hai.
