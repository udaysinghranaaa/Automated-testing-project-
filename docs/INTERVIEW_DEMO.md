# Interview Demo Runbook

## Before the call

Run `mvn -Dheadless=true clean test` once if network is available. Keep this README, `LoginPage`, `BaseTest`, `DriverManager`, `CheckoutTest`, an Extent `index.html`, and the GitHub workflow open. Never depend on a live website during an interview.

## 60-second version

“ShopFlow QA is my Java 17 Selenium hybrid framework for SauceDemo. The original regression has 49 independent business tests for login, products, cart and checkout. Page Objects isolate locators/actions, `BaseTest` owns a fresh browser per method, and `DriverManager` uses `ThreadLocal` so three TestNG workers do not share sessions. It runs Chrome and Edge, headed or headless, uses explicit waits only, and creates Extent, structured logs, screenshots and diagnostics. Advanced WebDriver topics absent from SauceDemo use local fixtures. GitHub Actions and Jenkins definitions run headless smoke and optional deeper suites. In the final audit all required local matrices passed.”

## 3-minute version

1. Show `README.md` verified counts; distinguish 82 methods from parameterized invocations.
2. Open `LoginPage`: stable locators, intent methods, explicit readiness.
3. Open `CheckoutTest`: tests assert business outcomes, exact item sets and `BigDecimal` totals.
4. Open `DriverManager` and `BaseTest`: one driver per thread; quit and remove in `alwaysRun` teardown.
5. Open `TestExecutionListener`: unique invocation ID, failure evidence, Extent node, structured log.
6. Show `testng-parallel-regression.xml` and explain three bounded workers.
7. End with CI workflow and one report folder.

## 5-minute version

Add a live headless smoke command:

```powershell
mvn '-Dtestng.suite=testng-parallel-smoke.xml' `
  '-Dtestng.output=target/testng-reports/demo' `
  '-Dheadless=true' '-Dparallel.threads=3' exec:java
```

While it runs, explain synchronization: visibility/clickability/URL/state waits replace time guesses. Explain the resolved flaky logout story: evidence pointed to a Chrome password-manager breach warning; preferences were applied only to Selenium sessions, business assertions remained unchanged, then Chrome and Edge were verified. Show report after completion.

## Offline fallback

- Show saved Extent HTML under `target/shopflow-reports/<run-id>/index.html`.
- Show `docs/TEST_CASES.md`, architecture diagrams and Surefire XML from the verification archive.
- Run JUnit locally; it needs no browser/site: `mvn '-Dtest=PriceCalculatorJupiterSpec,InputValidatorJupiterSpec' test`.
- If browser launch fails, explain the prerequisite rather than editing assertions or adding sleeps.

## Browser/site failure fallback

Check browser version, internet, SauceDemo status, URL/title/console evidence, then run the local advanced fixture suite. State clearly that a blocked external dependency is not a passed product test.

## Likely questions

- Why POM? Locator/action change is centralized and tests stay business-readable.
- Why fresh driver per method? Isolation and reliable parallelism; startup cost is accepted.
- Why ThreadLocal? A static driver would be overwritten across workers.
- Why no retry? Retry can hide deterministic defects; add it only for classified infrastructure faults with evidence.
- What would you add? Remote Grid/container execution, API setup, accessibility, ownership tags and trend storage—only when product scope needs them.

