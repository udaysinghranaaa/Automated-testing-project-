# ShopFlow QA — Java Selenium Hybrid Automation Framework

Portfolio-ready UI automation for the externally hosted [SauceDemo](https://www.saucedemo.com/) training application. The project demonstrates maintainable Page Objects, reliable synchronization, parallel and cross-browser execution, evidence-rich reporting, TestNG, a small JUnit learning module, and CI/CD definitions.

> SauceDemo is a public demo owned and operated by Sauce Labs. This repository is an independent learning project; it does not own, deploy, or control the application. Availability and test data can change outside this project's control.

## Verified snapshot

Audited on **9 October 2026** on Windows 11 with Java/Javac 26.0.1 compiling to Java 17, Maven 3.10.0, Chrome 154.0.8037.93 and Edge 154.0.4258.62.

| Scope | Honest count | Latest Phase 10 result |
|---|---:|---:|
| Original SauceDemo business regression | 49 test methods | 49 passed |
| Expanded regression | 49 methods + 7 DataProvider invocations | 56 passed |
| Advanced local-fixture suite | 33 test methods | 33 passed |
| JUnit learning module | 16 invocations | 16 passed |
| TestNG learning suite | 9 tests | 9 passed |
| Chrome + Edge critical journeys | 2 invocations | 2 passed |

The original 49 and advanced 33 are **82 test methods in separate scopes**. DataProvider and parameterized invocations are reported separately and are not presented as additional unique methods.

## What it tests

- Login: valid, invalid, blank and locked-out users; field interaction; logout.
- Products: inventory integrity, sorting, product details and navigation.
- Cart: add/remove, badge, price consistency and retained state.
- Checkout: validation, overview contents, money calculations, cancellation and completion.
- Advanced Selenium: alerts, frames, windows/tabs, Actions, keyboard, dropdowns, dynamic DOM, FluentWait, upload, shadow DOM and W3C scrolling.
- Framework behavior: lifecycle, assertions, dependencies, parameterization, parallel isolation, screenshots, logs and Extent HTML.

Advanced capabilities absent from SauceDemo run against deterministic HTML fixtures in `src/test/resources/advanced-fixtures`; the framework does not invent unsupported e-commerce features.

## Architecture

```text
Test / TestNG suite
        |
        v
BaseTest --> DriverFactory --> Selenium Manager --> Chrome / Edge
   |             |
   |        DriverManager (ThreadLocal<WebDriver>)
   v
Page Objects --> BasePage --> WaitUtils --> WebDriver
   |
   v
Assertions + listener --> logs / Extent report / failure screenshot / diagnostics
```

Key decisions:

- One fresh browser per test method for state isolation.
- `ThreadLocal<WebDriver>` for safe parallel workers, always removed after `quit()`.
- Stable `id`, `data-test` CSS and semantic state locators; no brittle index chains.
- Explicit waits only; no implicit wait and no `Thread.sleep()`.
- Plain Selenium clicks; no JavaScript-click escape hatch or blind retry.
- Page Objects expose business actions; tests own assertions.
- Browser credentials/password prompts disabled only inside Selenium-created temporary sessions.
- Unique per-run and per-invocation artifact names prevent parallel overwrite.

See [Architecture](docs/ARCHITECTURE.md), [test catalog](docs/TEST_CASES.md), and [framework interview guide](docs/FRAMEWORK_INTERVIEW.md).

## Repository map

```text
.
├── .github/workflows/selenium-tests.yml  # GitHub Actions definition
├── docs/                                 # architecture, catalog and interview pack
├── src/test/java/com/shopflow/
│   ├── core/                             # config, driver lifecycle, suite tuning
│   ├── learning/                         # TestNG and JUnit examples
│   ├── models/                           # immutable checkout data
│   ├── pages/                            # POM + advanced fixture pages
│   ├── reporting/                        # listener, Extent, screenshots, diagnostics
│   ├── tests/                            # business, data, browser and advanced tests
│   └── utils/                            # waits, money and fixture helpers
├── src/test/resources/
│   ├── advanced-fixtures/                # isolated local Selenium fixtures
│   ├── config.properties
│   └── logback-test.xml
├── Jenkinsfile
├── pom.xml
└── testng-*.xml                          # focused execution suites
```

## Prerequisites

- Windows PowerShell (commands below are Windows-friendly).
- JDK 17 or later. The project compiles with `maven.compiler.release=17`.
- Maven 3.9+ available as `mvn`.
- Current Chrome and/or Edge.
- Internet access for Maven dependencies, Selenium Manager driver resolution and SauceDemo tests.

Verify:

```powershell
java -version
javac -version
$env:JAVA_HOME
mvn -version
& 'C:\Program Files\Google\Chrome\Application\chrome.exe' --version
& 'C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe' --version
```

If Maven is not on `PATH`, assign its exact executable without changing global variables:

```powershell
$maven = 'C:\path\to\apache-maven\bin\mvn.cmd'
& $maven clean test
```

## Run commands

```powershell
# Original serial 49-test regression
mvn clean test
mvn verify

# Expanded 56-invocation regression
mvn '-Dtestng.suite=testng-regression.xml' `
  '-Dtestng.output=target/testng-reports/expanded' exec:java

# Advanced local fixtures (33)
mvn '-Dtestng.suite=testng-advanced.xml' `
  '-Dtestng.output=target/testng-reports/advanced' '-Dheadless=true' exec:java

# Parallel original / expanded
mvn '-Dtestng.suite=testng-parallel-original-regression.xml' `
  '-Dtestng.output=target/testng-reports/parallel-original' `
  '-Dparallel.threads=3' exec:java
mvn '-Dtestng.suite=testng-parallel-regression.xml' `
  '-Dtestng.output=target/testng-reports/parallel-expanded' `
  '-Dparallel.threads=3' exec:java

# Fast headless smoke
mvn '-Dtestng.suite=testng-parallel-smoke.xml' `
  '-Dtestng.output=target/testng-reports/smoke' `
  '-Dheadless=true' '-Dparallel.threads=3' exec:java

# Chrome + Edge
mvn '-Dtestng.suite=testng-cross-browser.xml' `
  '-Dtestng.output=target/testng-reports/cross-browser' `
  '-Dheadless=true' exec:java

# Seven login datasets
mvn '-Dtestng.suite=testng-parallel-dataprovider.xml' `
  '-Dtestng.output=target/testng-reports/data' `
  '-Dheadless=true' '-Dparallel.threads=3' exec:java

# Learning modules
mvn '-Dtest=PriceCalculatorJupiterSpec,InputValidatorJupiterSpec' test
mvn '-Dtestng.suite=testng-learning.xml' `
  '-Dtestng.output=target/testng-reports/learning' exec:java
```

Runtime overrides use Java system properties, for example `-Dbrowser=edge`, `-Dheadless=true`, `-Dexplicit.wait.seconds=10`, and `-Dparallel.threads=3`. Defaults live in `src/test/resources/config.properties`.

## Reports and evidence

After execution inspect:

- `target/surefire-reports/` — Maven XML/text results.
- `target/testng-reports/<run>/` — TestNG HTML/XML.
- `target/shopflow-reports/<run-id>/index.html` — Extent HTML.
- `target/shopflow-reports/<run-id>/screenshots/` — failure-only PNGs.
- `target/shopflow-reports/<run-id>/diagnostics/` — URL, title, browser, exception, screenshot reference and console logs.
- `target/shopflow-logs/shopflow-test.log` — structured run log.

The intentionally failing `testng-parallel-reporting-failure.xml` suite exists only to validate evidence capture. Do not place it in a normal green CI gate.

## CI/CD

- GitHub Actions: headless three-worker smoke, conditional original regression, optional advanced suite and always-uploaded reports.
- Jenkins declarative pipeline: compile, smoke, optional regression/advanced stages, JUnit publishing and artifact archival.
- Both definitions assume Linux agents with Java 17, Maven and Chrome. Jenkins tool names/agent labels must match the target controller.
- CI files were locally audited only; no remote GitHub workflow or Jenkins controller was triggered in Phase 10.

See [publishing checklist](docs/GITHUB_PUBLISHING_CHECKLIST.md) before creating a repository.

## Troubleshooting

| Symptom | Check |
|---|---|
| `mvn` not recognized | Use the full `mvn.cmd` path or add Maven to the current shell `PATH`. |
| Java release error | `mvn -version` must show JDK 17+; `JAVA_HOME` must reference a JDK, not a JRE. |
| Browser cannot start | Confirm Chrome/Edge installation; allow Selenium Manager network/cache access. |
| SauceDemo timeout | Check internet/site availability, then inspect URL/title/console evidence; do not add sleeps. |
| Password warning blocks UI | Confirm browser options are used; preferences affect only automation profiles. |
| Parallel-only failure | Check ThreadLocal driver removal, shared mutable state, unique artifacts and worker logs. |
| Headless-only mismatch | Reproduce headed, compare viewport/state, then fix the real condition. |
| Empty Extent report in JUnit run | Expected: the listener creates reports only for real TestNG test events. |

## Limitations

- SauceDemo is external; uptime and application changes are outside this repository.
- Demo credentials are public test data, not production secrets.
- Business regression currently targets Chromium browsers; Firefox is not declared as verified coverage.
- Local fixture tests prove WebDriver mechanics, not SauceDemo business behavior.
- No API, database, mobile, visual-diff, accessibility or performance testing is claimed.
- CI syntax is committed-ready but still needs authorized execution in real GitHub/Jenkins infrastructure.
- `DriverFactory` currently imports browser option factories from the `tests` package; functional but a future package-cleanup candidate.

## Interview/demo path

Start with [Interview Demo](docs/INTERVIEW_DEMO.md), then use [Resume Project](docs/RESUME_PROJECT.md), [Selenium mastery](docs/SELENIUM_INTERVIEW_MASTERY.md), [live coding](docs/SELENIUM_LIVE_CODING.md), [Core Java](docs/CORE_JAVA_INTERVIEW.md), [Java practice](docs/JAVA_CODING_PRACTICE.md), [SQL](docs/SQL_INTERVIEW.md), [manual testing](docs/MANUAL_TESTING.md), and [mock interviews](docs/MOCK_INTERVIEW.md).

## Responsible use

Use low, bounded concurrency against the demo site. Do not bypass security controls, automate real payments, or reuse this configuration against production. No commit, push, repository creation, or remote CI execution was performed during the final audit.
