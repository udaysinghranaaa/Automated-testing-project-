# Resume Project Description

## One line

Built a Java 17 Selenium/TestNG hybrid UI automation framework for SauceDemo with Page Objects, explicit waits, ThreadLocal parallelism, Chrome/Edge execution, data-driven testing, Extent evidence, structured logging, GitHub Actions and Jenkins pipelines.

## Resume bullets

- Designed 49 independent e-commerce regression tests across login, catalog, sorting, cart and checkout, plus 33 isolated advanced WebDriver tests using deterministic local HTML fixtures.
- Implemented reusable Page Objects, configuration-driven browser creation through Selenium Manager, explicit synchronization and reliable `alwaysRun` cleanup.
- Enabled safe three-worker execution with `ThreadLocal<WebDriver>`, parallel-safe report nodes and collision-free screenshots/diagnostics.
- Added exact price/state assertions using `BigDecimal`, positive/negative coverage, seven-row TestNG DataProvider coverage and a 16-invocation JUnit Jupiter learning module.
- Built Extent HTML, SLF4J/Logback logs, browser console capture and automatic failure screenshots/diagnostics without weakening original assertions.
- Authored headless smoke/regression CI definitions for GitHub Actions and Jenkins with report publishing and bounded concurrency.
- Audited all execution modes locally: original 49, expanded 56 invocations, advanced 33, Chrome/Edge, headless, parallel, JUnit and TestNG learning suites.

## STAR story

**Situation:** A logout/checkout regression became intermittent in Chrome while the same product/cart flows remained stable.  
**Task:** Find whether synchronization, application behavior, or the browser caused blocked clicks without adding sleeps or retries.  
**Action:** Captured screenshots, DOM/console state and cross-browser evidence; isolated a Chrome password-manager breached-password surface. Added supported Chrome preferences to fresh Selenium profiles only, preserved clicks and assertions, and ran targeted, suite and full-regression verification.  
**Result:** The popup disappeared from automation sessions and the complete regression returned green; user Chrome profile and global security settings were untouched.

## Honest scope statement

SauceDemo is externally hosted. Counts are 49 original business methods and 33 separate advanced fixture methods. Seven DataProvider and JUnit parameterized rows are invocations, not invented unique test cases. Remote CI files are prepared but were not executed against an external account during the local audit.

