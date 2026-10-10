# Architecture

## 1. Layered view

```mermaid
flowchart TB
  S[TestNG XML / Maven] --> T[Test classes]
  T --> P[Page objects]
  P --> B[BasePage + WaitUtils]
  B --> W[Selenium WebDriver]
  T --> BT[BaseTest]
  BT --> F[DriverFactory]
  F --> M[Selenium Manager]
  M --> C[Chrome / Edge]
  L[TestExecutionListener] --> R[Extent + logs + evidence]
  T -. events .-> L
```

Tests express intent and assertions. Page Objects own locators and browser interactions. Core classes own configuration and browser lifecycle. Reporting observes execution without changing pass/fail decisions.

## 2. Driver lifecycle

```mermaid
sequenceDiagram
  participant N as TestNG
  participant B as BaseTest
  participant F as DriverFactory
  participant D as DriverManager
  participant T as Test method
  N->>B: @BeforeMethod(browser, headless)
  B->>F: createDriver()
  F-->>B: ChromeDriver or EdgeDriver
  B->>D: setDriver(thread-local)
  B->>B: navigate initialUrl
  N->>T: execute
  T->>D: getDriver()
  N->>B: @AfterMethod(alwaysRun=true)
  B->>D: quitDriver()
  D->>D: driver.quit(); ThreadLocal.remove()
```

The guard rejects a second driver on the same thread. Cleanup runs even after failures and removes the reference to prevent cross-test leakage.

## 3. Page Object collaboration

```mermaid
classDiagram
  class BasePage {
    -WebDriver driver
    -WaitUtils waits
    +currentUrl()
    +pageTitle()
    #click(By)
    #enterText(By,String)
  }
  BasePage <|-- LoginPage
  BasePage <|-- InventoryPage
  BasePage <|-- ProductDetailsPage
  BasePage <|-- CartPage
  BasePage <|-- CheckoutInformationPage
  BasePage <|-- CheckoutOverviewPage
  BasePage <|-- CheckoutCompletePage
  LoginPage --> InventoryPage : valid login
  InventoryPage --> CartPage : open cart
  CartPage --> CheckoutInformationPage : checkout
  CheckoutInformationPage --> CheckoutOverviewPage : continue
  CheckoutOverviewPage --> CheckoutCompletePage : finish
```

Page methods return the next page type, making navigation readable while assertions stay in tests.

## 4. Parallel isolation

```mermaid
flowchart LR
  NG[TestNG scheduler] --> W1[Worker 1]
  NG --> W2[Worker 2]
  NG --> W3[Worker 3]
  W1 --> TL1[ThreadLocal driver A]
  W2 --> TL2[ThreadLocal driver B]
  W3 --> TL3[ThreadLocal driver C]
  TL1 --> A1[Unique invocation artifact]
  TL2 --> A2[Unique invocation artifact]
  TL3 --> A3[Unique invocation artifact]
  A1 --> ER[Single synchronized Extent run]
  A2 --> ER
  A3 --> ER
```

There is no static shared WebDriver. Immutable datasets, atomic invocation numbers, per-thread Extent nodes and unique filenames make three-worker execution safe.

## 5. Failure evidence flow

```mermaid
sequenceDiagram
  participant T as TestNG failure
  participant L as Listener
  participant S as ScreenshotManager
  participant E as FailureEvidenceCollector
  participant X as ExtentReportManager
  T->>L: onTestFailure(result)
  L->>S: capture(driver, invocationId)
  S-->>L: relative + absolute PNG path
  L->>E: collect result, browser, screenshot
  E-->>L: URL, title, exception, console, diagnostic path
  L->>X: attach throwable, metadata and PNG
  L->>L: structured error log
  Note over X: flush once in onExecutionFinish
```

## Package responsibilities

| Package | Responsibility |
|---|---|
| `core` | typed configuration, browser creation, ThreadLocal ownership, parallel suite tuning |
| `pages` | stable locators and user actions; `pages.advanced` wraps local fixtures |
| `tests` | scenarios and assertions; diagnostics are isolated under their own package |
| `reporting` | listener, Extent lifecycle, screenshots, browser logs and diagnostics |
| `utils` | explicit waits, money parsing and fixture URI/file helpers |
| `models` | immutable checkout input |
| `learning` | intentionally separate JUnit/TestNG examples |

## Reliability and design choices

- Explicit waits describe observable state. Mixing implicit waits is prohibited.
- Each method gets a fresh browser, trading some speed for deterministic state.
- Selenium Manager resolves drivers; binaries are not checked in.
- Chrome/Edge options are fresh per session. Password manager preferences do not modify a user's profile.
- SauceDemo business tests and advanced fixture tests are separate suites.
- Money uses `BigDecimal`, not binary floating-point.
- Failure capture is best-effort and must never replace the original assertion error.

## Known architectural debt

`DriverFactory` imports `ChromeOptionsFactory` and `EdgeOptionsFactory` from the `tests` package. It works and is covered by all browser runs, but moving these two factories into `core` would improve dependency direction in a future, separately reviewed change. It was not refactored during the final audit because there was no functional defect.

