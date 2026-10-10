# Selenium Interview Mastery — 110 Q&A

Answers are concise first; starred deep dives below add code, ShopFlow usage, follow-ups and common mistakes.

## WebDriver fundamentals

1. **What is Selenium WebDriver?** A W3C browser-automation API that sends commands to a browser-specific driver.
2. **Why WebDriver over Selenium IDE?** Code offers maintainability, assertions, CI, data and architecture; IDE is mainly record/playback.
3. **What is the W3C WebDriver protocol?** A standardized HTTP command/response contract between client bindings and the remote end.
4. **What is Selenium Manager?** Selenium's driver/browser discovery and management tool invoked by bindings when no path is supplied.
5. **`WebDriver` vs `ChromeDriver`?** `WebDriver` is the interface; `ChromeDriver` is a Chromium implementation.
6. **Why code to an interface?** Tests can accept different implementations and reduce browser coupling.
7. **`get()` vs `navigate().to()`?** Both navigate; `navigate()` also exposes back, forward and refresh.
8. **`close()` vs `quit()`?** `close` closes one window; `quit` ends the whole session. Teardown normally uses `quit`.
9. **What is a WebElement?** A remote reference to a DOM element in the current browsing context.
10. **Why can an element become stale?** The DOM node/context was replaced or detached after it was located.

## Locators

11. **Preferred locator order?** Unique stable `id`, stable product-owned attribute/CSS, then accessible/semantic locator; XPath when relationships/text demand it.
12. **Why avoid absolute XPath?** It couples to DOM depth and breaks on harmless layout changes.
13. **CSS vs XPath?** CSS is compact for attributes/classes; XPath supports ancestor/sibling/text relationships.
14. **`By.id` example?** `By.id("user-name")`.
15. **Stable SauceDemo attribute?** `data-test`, e.g. `By.cssSelector("[data-test='login-button']")`.
16. **Why is className limited?** It accepts one class token, not a space-separated class attribute.
17. **Relative XPath example?** `//div[@data-test='inventory-item'][.//div[text()='Sauce Labs Backpack']]`.
18. **`findElement` behavior?** Returns first match or throws `NoSuchElementException`.
19. **`findElements` behavior?** Returns all matches or an empty list; useful for presence checks.
20. **Why avoid indexes?** Order changes silently point at the wrong element.

## Waits and synchronization

21. **Why does UI automation need waits?** Browser, network and JavaScript state changes are asynchronous.
22. **Implicit wait?** A global element-lookup polling timeout; it can make timing opaque.
23. **Explicit wait?** Polls a named condition until success or timeout.
24. **FluentWait?** Configurable timeout, polling interval and ignored exceptions behind explicit waits.
25. **Why not mix waits?** Nested timing can create long, unpredictable timeouts.
26. **Why not `Thread.sleep()`?** It waits a fixed time regardless of readiness and either slows or flakes.
27. **Visibility vs presence?** Presence means in DOM; visibility also requires display and usable size.
28. **Visibility vs clickability?** Clickability checks visible and enabled, but overlays can still intercept.
29. **Best wait after login?** A stable authenticated state: expected URL plus visible inventory container.
30. **Best wait after logout?** Login URL/form readiness and authenticated inventory absence.
31. **Wait for an error?** Wait for the error element visible, then assert exact message.
32. **Wait for page load?** Prefer a page-specific ready condition over only `document.readyState`.
33. **Wait for loader?** Wait for invisibility, then for the result state.
34. **Handle stale replacement?** Re-locate inside a condition after known DOM replacement; do not keep the old element.
35. **What should timeout messages contain?** Action, locator/state, expected result and useful current context.

## Interactions and contexts

36. **How do you type?** Wait visible, optionally clear, then `sendKeys`.
37. **Native dropdown?** Selenium `Select` for a real `<select>` element.
38. **Custom dropdown?** Click trigger and choose an option with normal locators/waits.
39. **Hover/double/right click?** W3C `Actions` API.
40. **Drag and drop?** `Actions.dragAndDrop` or explicit click-hold/move/release when the widget requires it.
41. **Keyboard shortcuts?** `Keys.chord` or `Actions.keyDown/keyUp`; use platform-appropriate modifier.
42. **File upload?** Send the absolute local path to `<input type=file>`; do not automate OS dialogs.
43. **Alert handling?** Wait for alert, then read/send text and accept/dismiss.
44. **Frame handling?** Wait and switch to frame; restore with `parentFrame` or `defaultContent`.
45. **Window handling?** Save parent, trigger, wait for handle count, identify/switch, close child, restore parent.
46. **Shadow DOM?** Obtain `getShadowRoot()` and search within its `SearchContext` for an open root.
47. **Scrolling?** W3C wheel actions or element interaction; JavaScript only with a justified browser limitation.
48. **Why avoid JavaScript click?** It bypasses user-like interactability and can mask overlays or UI defects.
49. **What is an intercepted click?** Another element/overlay receives the click; inspect evidence and wait for real UI state.
50. **How handle cookies?** WebDriver cookie API; clear only within the controlled test session.

## Test architecture

51. **What is POM?** A class models page controls/actions so tests describe business intent.
52. **Should pages assert?** Usually no; return state/data and let tests define expectation.
53. **What is PageFactory?** Annotation/proxy-based element initialization; optional, not required for POM.
54. **Why avoid cached elements?** Modern DOM nodes change; caching increases stale references.
55. **What belongs in BasePage?** Repeated low-level waits/interactions, not application business assertions.
56. **What belongs in BaseTest?** Driver setup/teardown and test-level shared navigation helpers.
57. **Factory vs manager?** Factory creates a driver; manager owns/accesses its lifecycle.
58. **Why configuration abstraction?** One validated precedence policy for defaults and runtime overrides.
59. **What is a hybrid framework?** A pragmatic combination of POM, data, suites, utilities, reporting and CI.
60. **How avoid over-engineering?** Add abstractions only after a repeated responsibility or real extension point exists.

## TestNG, data and assertions

61. **`@BeforeMethod` use?** Establish isolated preconditions before every test invocation.
62. **Why `alwaysRun` teardown?** Cleanup must run after test/configuration failures.
63. **Hard vs soft assertions?** Hard stops at failure; soft collects checks until `assertAll`.
64. **When use soft assertions?** Multiple independent observations where all failures add diagnostic value.
65. **What is DataProvider?** Supplies parameter rows to one TestNG method.
66. **Is each data row a method?** No; each is a separate invocation of one method.
67. **Groups?** Metadata to select smoke, regression, feature or other execution scopes.
68. **Dependency risk?** Tests become order-coupled; prefer independent setup for product regression.
69. **Priority risk?** Execution order is not state isolation and can hide coupling.
70. **Precise assertion?** Compare the actual business state and include meaningful failure context.

## Parallel and cross-browser

71. **Why static WebDriver fails in parallel?** Workers overwrite one shared reference and control the wrong session.
72. **What does ThreadLocal do?** Stores a separate value for each current worker thread.
73. **Why call `remove()`?** Prevent value retention when thread-pool threads are reused.
74. **Is ThreadLocal alone sufficient?** No; datasets, reports, files and mutable helpers must also be isolated.
75. **Parallel modes?** Suites, tests, classes, methods or instances; select based on lifecycle and isolation.
76. **Why bounded thread count?** Protect local resources and avoid overloading a public application.
77. **How prove parallelism?** Log thread ID/name, driver identity and overlapping timestamps.
78. **Cross-browser strategy?** Parameterize browser, centralize options, run the same journey and record actual capabilities.
79. **Headless limitation?** Rendering/viewport can differ; keep a headed reproduction path.
80. **Grid benefit?** Remote, distributed browser/OS capacity; it does not fix unsafe test state.

## Failures, reporting and quality

81. **What evidence on failure?** Throwable, screenshot, URL, title, browser/version, console logs and invocation identity.
82. **Why unique filenames?** Parallel failures otherwise overwrite each other.
83. **Listener role?** Observe lifecycle and attach evidence without changing business behavior.
84. **Should listener catch everything?** Evidence capture should be best-effort, but original test failure must survive.
85. **Retry analyzer risk?** It can turn real flaky/product failures green and hide instability.
86. **When retry may be valid?** A classified, measured infrastructure transient with strict cap and first-failure reporting.
87. **Automation defect vs product defect?** Reproduce, inspect DOM/network/browser evidence and compare independent paths/browsers.
88. **How debug flaky tests?** Preserve failure evidence, repeat targeted fresh sessions, form a hypothesis, change one cause, regress.
89. **Why controlled failure suite?** Proves the failure pipeline itself captures correct, collision-free evidence.
90. **Why keep it outside CI green gates?** Its intended result is non-zero.

## CI, security and scenarios

91. **What runs on PR?** Fast, deterministic headless smoke; deeper suites based on cost/risk policy.
92. **Why upload artifacts on failure?** The evidence is most valuable when the job fails.
93. **What should not enter Git?** Secrets, profiles, cookies, reports, screenshots, logs, caches and backups.
94. **How handle credentials?** Public demo data may be config; real credentials belong in secret stores/environment injection.
95. **What makes CI reproducible?** Pinned runtime/dependency strategy, clean checkout, declared commands and isolated state.
96. **Login test minimum assertions?** Authenticated page state and URL; negative cases also assert no inventory.
97. **Sorting test approach?** Capture actual values, independently sort a copy, compare complete ordered lists.
98. **Cart integrity approach?** Compare exact name sets and prices keyed by product, not only badge count.
99. **Checkout money approach?** Parse into `BigDecimal`; assert item sum, tax and total relationships.
100. **Logout test approach?** Open menu, wait/click real logout link, assert ready login form and no authenticated content.
101. **Why not automate a real payment?** Safety, legal and data impact; use sandbox/test contracts only.
102. **Why local advanced fixtures?** Determinism and honest separation when the AUT lacks a feature.
103. **How test responsive UI?** Set explicit viewport sizes and assert meaningful layout/interaction behavior.
104. **What is stale-safe collection reading?** Locate the collection after the state change and map values immediately.
105. **How validate images?** Source presence is UI-level; HTTP/content validation may belong to a separate client test.
106. **What is test pyramid relevance?** Keep most logic checks below UI; reserve UI for critical integrated journeys.
107. **What is flakiness rate?** Unexplained outcome variation across identical runs; track it rather than retrying it away.
108. **What is observability?** Enough structured context to reconstruct who ran what, where, when and why it failed.
109. **How choose smoke tests?** Critical, fast, independent journeys that detect deployment-blocking failures.
110. **Strong senior answer pattern?** State principle, trade-off, concrete project evidence, limitation and next diagnostic.

## Deep dive A — explicit wait for live login

```java
private final By username = By.id("user-name");
private final By password = By.id("password");
private final By login = By.id("login-button");
private final By inventory = By.cssSelector("[data-test='inventory-container']");

public InventoryPage loginAs(String user, String pass) {
    waits.visible(username).sendKeys(user);
    waits.visible(password).sendKeys(pass);
    waits.clickable(login).click();
    waits.urlContains("inventory.html");
    waits.visible(inventory);
    return new InventoryPage(driver);
}
```

**Why:** success is a URL plus stable authenticated DOM state, not merely “click did not throw.” **Project evidence:** every valid-login flow uses Page Object readiness. **Follow-up:** for negative login, wait for the error and assert inventory absent. **Common mistake:** waiting for the button before click but never waiting for the resulting state.

## Deep dive B — wait selection table

| Transition | Condition | Avoid |
|---|---|---|
| page opens | unique page container visible + expected URL | title alone |
| async result | loader invisible then result visible | fixed sleep |
| dropdown option | trigger clickable, option visible/clickable | stale cached option |
| new window | number of handles then identify new handle | assuming list index |
| DOM replacement | re-locate new node in condition | reusing old WebElement |
| logout | login form ready + authenticated content absent | URL check alone |

**Follow-up:** clickability cannot prove no overlay will intercept at the exact instant; collect screenshot/DOM and wait on the overlay's disappearance if evidence shows one. **Mistake:** catching `TimeoutException` and retrying the same action without understanding state.

## Deep dive C — ThreadLocal lifecycle

```java
private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
public static void setDriver(WebDriver d) { DRIVER.set(Objects.requireNonNull(d)); }
public static WebDriver getDriver() {
    WebDriver d = DRIVER.get();
    if (d == null) throw new IllegalStateException("No driver for current thread");
    return d;
}
public static void quitDriver() {
    try { if (DRIVER.get() != null) DRIVER.get().quit(); }
    finally { DRIVER.remove(); }
}
```

**Why:** worker A cannot control worker B's browser. **Project evidence:** three workers log different driver identities and 49/56 parallel runs passed. **Follow-up:** `InheritableThreadLocal` is not needed because TestNG worker tasks own their sessions. **Mistake:** making page objects static or forgetting `remove()`.

## Deep dive D — flaky click investigation

1. Re-run only the method with a fresh session.
2. Record browser/version, current URL, screenshot, console and target DOM state.
3. Compare another browser without changing business assertions.
4. Classify: locator/wait/session defect, browser surface, application defect or infrastructure.
5. Make the smallest supported change and rerun targeted repeatedly, then suite/regression.

ShopFlow's Chrome popup incident followed this process. Supported preferences disabled password saving/leak warnings only for automation profiles. No sleep, JavaScript click, assertion weakening or user-profile change was used. **Follow-up:** if both browsers failed with the same DOM state, application/automation hypotheses would be stronger. **Mistake:** stacking Actions, Enter, JS click and retries until one passes.

## Deep dive E — exact cart/checkout assertions

```java
Map<String, BigDecimal> expected = inventory.selectedProductPrices();
Map<String, BigDecimal> actual = cart.productPricesByName();
Assert.assertEquals(actual, expected, "Cart products and prices must match selection");
Assert.assertEquals(total, subtotal.add(tax), "Total must equal subtotal plus tax");
```

**Why:** badge/visibility checks can pass while the wrong item or price is shown. **Project evidence:** cart and overview compare maps by product identity. **Follow-up:** use currency scale/rounding rules supplied by the product contract. **Mistake:** using `double` and exact equality for money.

## Deep dive F — evidence listener

The listener assigns an atomic invocation ID, retrieves the current thread's driver, captures a PNG, writes diagnostic text, attaches metadata/throwable to the current thread's Extent node, and logs a structured error. Report flushing happens once at execution end. **Follow-up:** evidence failure is logged but must not replace the application assertion. **Mistake:** a shared static `ExtentTest` causing one worker to write into another test.
