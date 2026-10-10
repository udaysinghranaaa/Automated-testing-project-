# Framework Interview Guide

## Explain the framework

Start from the test: TestNG creates a class instance, `BaseTest` creates a configured browser, `DriverManager` binds it to the worker thread, a Page Object waits and performs business actions, the test asserts the observable result, and an independent listener records status/evidence. Teardown always quits and removes the driver.

## Design questions and project answers

1. **Why hybrid?** It combines POM, configuration, suites/groups, data-driven inputs, parallel execution, utilities and reporting without forcing unrelated abstractions.
2. **Why Page Objects?** Locators/actions have one owner; assertion intent stays visible in tests.
3. **Why are assertions not in pages?** Pages describe capability; tests decide expected behavior and failure meaning.
4. **Why `BasePage`?** It centralizes low-level visible/clickable/text behavior and avoids copy/paste.
5. **Why `BaseTest`?** One lifecycle policy and fresh session for every test method.
6. **Why factory plus manager?** Factory creates; manager owns thread-bound lifecycle. These are different responsibilities.
7. **Why Selenium Manager?** Avoid checked-in driver binaries and manual version coupling.
8. **Why explicit waits only?** They target a meaningful condition and avoid implicit/explicit timeout multiplication.
9. **How is parallel safe?** ThreadLocal driver, no shared page/test state, immutable data, atomic IDs and thread-local report nodes.
10. **Why three threads?** Bounded local load showed useful speedup without aggressively hitting the public demo.
11. **What is test isolation?** Every test creates its own session/data state and can run alone or in any class order.
12. **What belongs in config?** Environment/browser/headless/timeouts; not assertions or locators.
13. **Why `BigDecimal`?** Decimal money comparisons avoid floating-point representation error.
14. **How are failures diagnosed?** Original throwable plus URL, title, browser/version, console entries, screenshot and diagnostic text.
15. **Can evidence collection hide a failure?** No. It is best-effort and the TestNG result remains authoritative.
16. **Why local fixtures?** SauceDemo lacks advanced browser widgets; deterministic fixtures teach mechanics without false product claims.
17. **TestNG versus JUnit here?** TestNG is the UI orchestration framework; JUnit is an isolated learning/unit module.
18. **How does cross-browser work?** XML supplies browser parameters; factory returns a fresh driver with browser-specific options.
19. **How does CI differ from local?** Headless, clean ephemeral agents, artifact upload and no assumption about a developer profile.
20. **What would you change at scale?** External environments/secrets, remote Grid, test ownership/tags, API data setup and historical result storage.

## Trade-offs

Fresh browsers cost startup time but eliminate state leakage. Class-level parallelism reduces that time but still bounds load. Page methods return page types for readability, yet tests retain outcome assertions. Static utility entry points are simple here; dependency injection might be justified only when environments and factories become substantially more complex.

## Code-review checklist

- Locator stable and scoped?
- Wait tied to the post-action state?
- Assertion checks business outcome, not only visibility?
- Test independent and teardown guaranteed?
- No sleep, implicit wait, JS click or blind retry?
- Data immutable and no shared driver?
- Failure message says expected business behavior?
- New dependency/suite/report path justified and documented?

## Known limitation answer

Be direct: advanced fixture coverage is not product coverage, external SauceDemo uptime can block browser tests, CI awaits real infrastructure verification, and browser option factories currently live in the `tests` package. A senior answer separates verified facts from roadmap items.
