# Phase 9 - Parallel Selenium, Cross-Browser and CI/CD Notes

This guide DevLabs interview preparation ke liye simple Hinglish mein hai. Jahan result
"verified" likha hai, wahan 9 October 2026 ko local machine par real command execute hui.
GitHub Actions aur Jenkins runtime external infrastructure na hone ke karan **PENDING** hain.

## Chapter 1 - Parallel Testing Introduction

Serial testing mein ek test ke baad next test chalta hai. Parallel testing mein independent
tests ek hi time par alag workers par chal sakte hain. QA teams iska use feedback time kam
karne ke liye karti hain. Benefit faster regression hai; challenge shared state, machine load,
report collisions aur flaky external application ho sakte hain. Is project ne correctness ko
speed se upar rakha aur maximum configurable workers ko 1-8 validate kiya.

Actual comparable headed Chrome measurements:

| Mode | Workers | Invocations | Result | Suite duration |
|---|---:|---:|---|---:|
| Serial | 1 | 49 | 49 passed | 92.395 s |
| Parallel classes | 2 | 49 | 49 passed | 67.581 s |
| Parallel classes | 3 | 49 | 49 passed | 61.299 s |

2 workers ka measured speedup 1.37x (26.9% duration reduction) aur 3 workers ka 1.51x
(33.7% reduction) tha. More threads always faster nahi hote; browser startup, CPU, RAM,
network aur longest test class critical path ban sakte hain.

## Chapter 2 - Java Threads

Thread ek process ke andar execution path hai. Common lifecycle: NEW, RUNNABLE, BLOCKED,
WAITING/TIMED_WAITING aur TERMINATED. TestNG worker ka name aur Java thread ID logs mein
record hua. Shared state sab workers dekh sakte hain, local state sirf owner use karta hai.
Race condition tab hoti hai jab two threads bina coordination same mutable value update karein.
Visibility ka matlab ek thread ka update doosre ko reliably dikhna bhi hai.

## Chapter 3 - ThreadLocal in Java

`ThreadLocal<T>` har worker thread ke liye separate value rakhta hai. `set()` owner value
assign karta hai, `get()` wahi thread ki value deta hai, aur `remove()` worker reuse se pehle
reference clear karta hai. Thread pool workers reuse hote hain, isliye `remove()` miss hone par
stale driver aur memory leak ho sakta hai.

```java
private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
DRIVER.set(driver);
WebDriver current = DRIVER.get();
DRIVER.remove();
```

## Chapter 4 - ThreadLocal WebDriver

Implemented owner `DriverManager` hai. `setDriver()` same thread par accidental overwrite
reject karta hai. `getDriver()` missing driver par clear exception deta hai.
`getDriverOrNull()` listener/configuration checks ke liye hai. `quitDriver()` `finally` mein
`remove()` karta hai, even when `quit()` throws. BaseTest mein second WebDriver store nahi hai.

Real flow:

```text
TestNG worker
  -> BaseTest @BeforeMethod
  -> DriverFactory.createDriver(browser, headless)
  -> DriverManager.setDriver(driver)
  -> Page Object receives that exact driver
  -> listener reads BaseTest.getDriver()
  -> BaseTest @AfterMethod
  -> DriverManager.quitDriver() -> quit + remove
```

Logs ne parallel runs mein distinct thread IDs aur `identityHashCode` values prove kiye. Test
ke baad OS process audit mein `chromedriver` aur `msedgedriver` zero the.

## Chapter 5 - TestNG Parallel Execution

- `parallel="methods"`: methods separate workers par; instance fields thread-safe hone chahiye.
- `parallel="classes"`: one class ke methods same worker par; different classes parallel.
- `parallel="tests"`: each XML `<test>` ek worker par; cross-browser parameters ke liye useful.
- `thread-count`: regular worker limit.
- `@DataProvider(parallel = true)`: datasets concurrent ho sakte hain.
- `data-provider-thread-count`: dataset pool ko bound karta hai.

Business tests mein mutable per-class Page Object fields hain, isliye class-level parallelism
safe aur smallest change tha. Cross-browser suite `parallel="tests"` use karti hai. Dedicated
DataProvider aur diagnostic suites method/data parallelism prove karti hain. Priority ko
parallel ordering guarantee nahi maana gaya.

## Chapter 6 - BaseTest in Parallel Framework

Superclass `@BeforeMethod(alwaysRun = true)` browser/headless parameters resolve karta hai,
driver create karke ThreadLocal mein set karta hai, then `initialUrl()` open karta hai.
Fixture tests `about:blank` preserve karte hain. Setup fail ho to driver immediately quit/remove
hota hai. `@AfterMethod(alwaysRun = true)` pass/fail dono cases mein cleanup aur MDC clear karta
hai. Ek invocation ko ek fresh browser milta hai.

## Chapter 7 - DriverFactory

Factory browser construction ka decision centralize karti hai:

```java
return switch (browser.toLowerCase(Locale.ROOT)) {
    case "chrome" -> new ChromeDriver(ChromeOptionsFactory.configured(headless));
    case "edge" -> new EdgeDriver(EdgeOptionsFactory.configured(headless));
    default -> throw new IllegalArgumentException(...);
};
```

Selenium Manager matching driver resolution karta hai. Unsupported browser silently Chrome
par fallback nahi hota. Factory driver ko globally cache nahi karti; ownership DriverManager
ke paas hai.

## Chapter 8 - Headless Chrome

Headed browser visible UI deta hai; headless browser display window ke bina render karta hai.
Implemented arguments `--headless=new` aur `--window-size=1920,1080` hain. Deterministic size
screenshot/layout differences kam karta hai. `--no-sandbox`, certificate bypass, security-off
flags add nahi kiye. Headless parallel smoke 5/5 aur advanced suite 33/33 pass hui.

Debugging steps: same test headed run karo, viewport compare karo, screenshot/title/URL dekho,
console logs inspect karo, then wait/locator problem fix karo. Headless failure ko retry se hide
mat karo.

## Chapter 9 - Cross-Browser Testing

Chrome aur Edge dono Chromium-based hain, lekin capabilities, driver binary aur browser policy
different ho sakte hain. `CrossBrowserSmokeCases` same Page Objects se login, product listing,
cart, checkout aur logout execute karta hai. XML ke two `<test>` blocks browser parameter dete
hain aur `parallel="tests"` se run karte hain.

Verified runtime:

- Chrome 154.0.8037.93: 1/1 journey passed.
- Microsoft Edge 154.0.4258.62: 1/1 journey passed.
- Actual `HasCapabilities` browser name/version log aur assertion se verify hua.

Browser start alone compatibility proof nahi; real controls and navigation assertions proof hain.

## Chapter 10 - Page Object Thread Safety

Page Objects constructor injection use karte hain: `new LoginPage(currentDriver)`. Unmein
`static WebDriver` nahi hai. Locator constants immutable `static final By` ho sakte hain because
they contain no session state. Per-test Page Objects apne invocation ka actual driver retain
karte hain. Correct existing POM rewrite nahi kiya gaya.

## Chapter 11 - Parallel Reporting

ExtentReports ek run-level object hai; each invocation ka `ExtentTest` thread-local hai.
Atomic sequence unique IDs deta hai. Report creation/initialization and final flush synchronized
boundaries par hain, browser execution par global lock nahi. Cross-browser metadata set both
Chrome and Edge versions record karta hai. DataProvider run mein seven distinct listener starts
aur report nodes observed hue.

`clearCurrentTest()` pass, failure, skip aur configuration failure paths mein `remove()` karta
hai. Isse worker reuse par next test previous node inherit nahi karta.

## Chapter 12 - Parallel Screenshots

Failure listener current invocation ka ThreadLocal driver leta hai. Filename atomic invocation
ID se banta hai. `Files.copy` overwrite option ke bina collision ko silently hide nahi karta.
Controlled diagnostic ne simultaneous red, green aur blue pages fail karaye:

- 3 distinct PNG files.
- 3 distinct SHA-256 prefixes and byte sizes.
- each diagnostic title matched its screenshot marker.
- Extent HTML contained all three markers and attachments.
- expected command exit code 1 preserved raha.

Diagnostic normal regression suite mein included nahi hai.

## Chapter 13 - Logging in Parallel Execution

SLF4J API plus Logback provider structured lifecycle events record karte hain. Pattern mein
timestamp, level, logger, thread, `testClass`, `testMethod` aur browser MDC fields hain. BaseTest
browser/test class context set karta hai; listener method context set/remove karta hai; teardown
`MDC.clear()` karta hai. Passwords logs mein nahi likhe jaate. Browser console collector bounded
50 entries rakhta aur sensitive key names redact karta hai.

## Chapter 14 - Maven Build Lifecycle

- `clean`: target delete karta hai.
- `compile`: main source compile karta hai (project mein main source absent hai).
- `test-compile`: tests/resources Java 17 target par compile karta hai.
- `test`: Surefire tests execute karta hai.
- `verify`: test ke baad packaging/verification lifecycle tak jata hai.
- Surefire XML: `target/surefire-reports/`.
- TestNG CLI XML: `target/testng-reports/<execution>/`.

`pom.xml` ka Exec plugin `-d ${testng.output}` pass karta hai, isliye suite artifacts predictable
target folders mein aate hain. `-Dtestng.suite`, `-Dtestng.output`, `-Dbrowser`, `-Dheadless` aur
`-Dparallel.threads` command-line overrides supported hain.

## Chapter 15 - Jenkins Introduction

Jenkins controller jobs/configuration coordinate karta hai; agents actual commands run karte
hain. Pipeline source-controlled stages ka flow hai. Declarative syntax `pipeline`, `agent`,
`stages`, `stage`, `steps`, `when` aur `post` blocks use karti hai. `Jenkinsfile` reproducible
pipeline definition hai. Is project ko Linux agent with Java 17, Maven 3 and Google Chrome
chahiye.

Required Jenkins capabilities/plugins: Pipeline, Pipeline: Declarative, Git, JUnit and Artifact
Manager support. Tool names `jdk17` and `maven3` Jenkins Global Tool Configuration mein define
hone chahiye.

## Chapter 16 - Jenkinsfile Walkthrough

Implemented stages:

1. **Checkout**: `checkout scm` workspace mein source lata hai.
2. **Environment Verification**: Java, Maven aur Chrome versions print karta hai.
3. **Test Compilation**: `mvn ... -DskipTests test-compile` fast compile gate hai.
4. **Headless Smoke**: three-worker headless smoke mandatory gate hai.
5. **Full Regression**: main branch ya `FULL_REGRESSION` parameter par 49-test verify.
6. **Advanced Selenium**: optional parameter par fixture suite.
7. **post always**: JUnit XML publish, bounded target artifacts archive, then workspace cleanup.

Test command par `|| true`/`returnStatus` masking nahi hai, so failure build ko fail karta hai.
`post` failure ke baad bhi evidence publish karta hai. Pipeline Linux `sh` only use karti hai;
local Windows PowerShell paths hardcode nahi hain. Jenkins installed nahi tha, isliye local
runtime result **PENDING external execution** hai.

## Chapter 17 - GitHub Actions Introduction

Workflow `.github/workflows/selenium-tests.yml` YAML file hai. Event workflow start karta hai,
job isolated runner VM par chalta hai, steps ordered commands/actions hain. Implemented events:
main push, pull request and manual dispatch. `permissions: contents: read` least privilege deta
hai. Concurrency same ref ke superseded run ko cancel karta hai.

## Chapter 18 - GitHub Actions Workflow

`headless-smoke` every event par runs: official checkout, Temurin Java 17 with Maven cache,
environment check, compile, then headless smoke. `full-regression` push ya selected manual run
par executes. `advanced-selenium` explicit manual input par executes.

Official maintained actions used:

- `actions/checkout@v6`
- `actions/setup-java@v4`
- `actions/upload-artifact@v4`

`if: always()` evidence upload ko failure ke baad chalata hai, but failed Maven step ka status
mask nahi karta. Artifacts 14 days retain hote hain. Workflow local file and Maven command
shape inspect hui; actual GitHub runner execution **PENDING** hai because repository push/run
authorized nahi tha.

## Chapter 19 - CI/CD Concepts

Continuous Integration har change ko frequently integrate karke automated checks chalata hai.
Continuous Delivery verified change ko deploy-ready rakhti hai but release decision manual ho
sakta hai. Continuous Deployment har passing change automatically release karta hai. Ye project
CI configuration implement karta hai; deployment implement nahi karta.

Practical gates:

- Pull request: small headless smoke.
- Main push: smoke then original 49-test regression.
- Manual dispatch: optional full and advanced suites.
- Failed tests: pipeline fails and evidence retained.

Unexecuted files ko "production-proven CI" nahi kaha gaya.

## Chapter 20 - CI Artifacts

Artifacts debugging ke liye run ke baad retained files hain:

- `target/surefire-reports/**`: Maven XML/text.
- `target/testng-reports/**`: TestNG XML/HTML/JUnit-style XML.
- `target/shopflow-reports/**`: Extent HTML, screenshots, diagnostics.
- `target/shopflow-logs/**`: bounded rolling framework log.

Baseline backups, personal browser profile, `.m2`, cookies and secrets upload paths mein nahi
hain. `mvn clean` target remove karta hai, so upload same job ke test ke baad hota hai.

## Chapter 21 - Performance Comparison

Comparable suite means same 49 business tests, same headed Chrome mode, same local environment.
Formula: `speedup = serial_duration / parallel_duration`.

```text
2 workers: 92.395 / 67.581 = 1.37x
3 workers: 92.395 / 61.299 = 1.51x
```

3 workers did improve this sample, but linear 3x improvement nahi mila. Browser startup,
CheckoutTest ka long class, network response and CPU contention limit karte hain. One run is
useful engineering evidence, formal benchmark nahi.

## Chapter 22 - Debugging Parallel Tests

| Scenario | Symptom | Investigation | Fix / prevention |
|---|---|---|---|
| Driver shared across threads | Wrong page/session | thread + driver identity logs | ThreadLocal owner |
| ThreadLocal not removed | Next test sees stale session | teardown and worker reuse logs | `remove()` in finally |
| Browser not closed | driver process remains | OS process check | alwaysRun teardown |
| Report entry overwritten | missing/mixed result | invocation count vs nodes | ThreadLocal ExtentTest + atomic ID |
| Screenshot mismatch | image belongs to another test | title/color diagnostic | capture current driver |
| Stale session | `NoSuchSessionException` | setup/teardown order | no lazy replacement driver |
| DataProvider collision | dataset result disappears | seven IDs and threads | immutable arrays + unique IDs |
| Slow parallel execution | little/no speedup | 1/2/3-worker timing | reduce threads; find long class |
| Chrome password popup | click intercepted | headed observation/options audit | automation-only prefs false |
| Headless navigation failure | headed passes, headless fails | URL/title/screenshot/viewport | explicit wait + fixed window |
| Edge driver mismatch | session creation failure | browser/driver capabilities | Selenium Manager + installed Edge |
| Maven provider issue | wrong tests discovered | Surefire provider logs/XML | explicit suite Exec commands |
| Jenkins artifact missing | no report after failure | stage order and glob | `post { always { ... } }` |
| Actions browser failure | Chrome command/session fails | runner version step/logs | verify runner image/browser |
| Configuration race | worker sees wrong browser | config/system property audit | immutable cached Properties |
| Static Page Object driver | cross-thread element errors | search `static WebDriver` | constructor injection |
| File collision | overwritten PNG/TXT | hashes/counts/CREATE_NEW error | atomic invocation filename |
| Setup failure becomes skip | many unexplained skipped tests | config-failure listener | fail setup clearly, no fallback |

## Chapter 23 - Interview Questions (50)

1. **Parallel testing kya hai?** Independent tests ko multiple workers par concurrently chalana.
2. **Main benefit?** Regression feedback time reduce hota hai.
3. **Main risk?** Shared mutable state race condition aur resource contention.
4. **Thread aur process difference?** Threads process memory share karte; processes isolated memory rakhte.
5. **ThreadLocal kya karta hai?** Same static access point par per-thread separate value deta hai.
6. **`remove()` kyun?** Pooled worker reuse par stale value/memory leak prevent karta hai.
7. **ThreadLocal thread safety ka complete solution hai?** Nahi; reports, files, test data sab audit chahiye.
8. **Static WebDriver unsafe kyun?** Workers same reference overwrite/use kar sakte hain.
9. **DriverFactory role?** Browser-specific construction logic centralize karna.
10. **DriverManager role?** Current thread ke driver lifecycle/ownership ko manage karna.
11. **Factory and manager difference?** Factory object banati; manager per-thread object store/cleanup karta.
12. **`parallel=methods` kab?** Jab test instance fields and dependencies fully thread-safe hon.
13. **`parallel=classes` kab?** Class methods same worker par rakhkar classes concurrent karni hon.
14. **`parallel=tests` kab?** XML test groups, especially browsers, separate workers par chalane hon.
15. **DataProvider parallel kaise?** `@DataProvider(parallel = true)` plus bounded pool.
16. **High thread count problem?** CPU/RAM/network saturation flaky timing create kar sakta hai.
17. **Fresh driver per test kyun?** Cookie/cart/navigation state isolation.
18. **`alwaysRun=true` kyun?** Failure/config paths mein cleanup attempt ensure karta hai.
19. **Setup fail ho to listener kya kare?** Configuration failure report kare, new hidden driver create na kare.
20. **Headless benefit?** CI display dependency kam aur often faster execution.
21. **Headless risk?** Viewport/render/timing headed browser se differ kar sakte hain.
22. **Fixed window size kyun?** Responsive layout deterministic rakhna.
23. **Selenium Manager kya hai?** Compatible local driver discovery/download automation.
24. **Cross-browser proof kya hai?** Same assertions with real capabilities and interactions, startup alone nahi.
25. **ChromeOptions prefs scope?** Sirf Selenium-created temporary automation profile.
26. **Password prefs kaun se?** `credentials_enable_service=false` and leak detection false.
27. **Page Object thread-safe kaise?** Constructor-injected non-static invocation driver.
28. **Static locators safe hain?** Immutable `By` constants generally safe hain.
29. **Explicit wait vs sleep?** Wait condition until timeout; sleep always fixed delay.
30. **Implicit + explicit mix kyun avoid?** Timeout behavior unpredictable/long ho sakta hai.
31. **Extent node isolation kaise?** `ThreadLocal<ExtentTest>` per worker invocation.
32. **Unique screenshot name kaise?** Atomic invocation sequence + class/method.
33. **`CREATE_NEW` benefit?** Collision overwrite silently nahi hota.
34. **MDC kya hai?** Per-thread logging key/value diagnostic context.
35. **MDC cleanup kyun?** Worker reuse par old test labels prevent karna.
36. **Surefire kya karta hai?** Maven test phase mein test provider execute/report karta hai.
37. **`mvn test` vs `verify`?** Verify later lifecycle phases tak jata; dono test execute karte.
38. **TestNG XML benefit?** Groups, classes, parameters and parallel mode declaratively select hote.
39. **CI smoke strategy?** PR par short critical flows for fast feedback.
40. **Full regression kab?** Main push/nightly/manual based cost and risk.
41. **Artifact upload on failure kaise?** `if: always()`/Jenkins `post always`.
42. **Failure status kaise preserve?** Test command ko `|| true` se mask na karo.
43. **Least privilege workflow?** Only required `contents: read` permissions.
44. **Jenkins controller/agent?** Controller schedules; agent workspace commands run karta.
45. **Declarative pipeline benefit?** Structured, reviewable, validated stage model.
46. **CI file local pass means CI passed?** Nahi; actual runner execution required.
47. **Speedup formula?** Serial duration divided by parallel duration.
48. **Why speedup non-linear?** Overhead, contention, uneven tests and external latency.
49. **Flaky parallel test debug first step?** Test/thread/driver IDs correlate karke shared state find karo.
50. **This design's key tradeoff?** Class parallel safety chosen over risky method-level maximum concurrency.

## Chapter 24 - Coding Practice (20 solved exercises)

### 1. Basic ThreadLocal

```java
ThreadLocal<String> value = new ThreadLocal<>();
value.set("worker-data");
System.out.println(value.get());
value.remove();
```

### 2. ThreadLocal WebDriver wrapper

```java
private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
static void set(WebDriver driver) { DRIVER.set(driver); }
static WebDriver get() { return DRIVER.get(); }
```

### 3. Safe quit and remove

```java
try { if (DRIVER.get() != null) DRIVER.get().quit(); }
finally { DRIVER.remove(); }
```

### 4. Parallel classes XML

```xml
<suite name="Parallel" parallel="classes" thread-count="3">
  <test name="UI"><classes><class name="example.LoginTest"/></classes></test>
</suite>
```

### 5. Parallel DataProvider

```java
@DataProvider(name="data", parallel=true)
public Object[][] data() { return new Object[][] {{"a"}, {"b"}, {"c"}}; }
```

### 6. Headless Chrome options

```java
ChromeOptions o = new ChromeOptions();
o.addArguments("--headless=new", "--window-size=1920,1080");
```

### 7. EdgeDriver setup

```java
EdgeOptions o = new EdgeOptions();
WebDriver driver = new EdgeDriver(o); // Selenium Manager resolves driver
```

### 8. Browser factory

```java
return switch (browser) {
  case "chrome" -> new ChromeDriver(chromeOptions);
  case "edge" -> new EdgeDriver(edgeOptions);
  default -> throw new IllegalArgumentException(browser);
};
```

### 9. System-property override

```java
String browser = System.getProperty("browser", properties.getProperty("browser"));
```

### 10. Parallel-safe screenshot name

```java
String name = sequence.incrementAndGet() + "-" + className + "-" + method + ".png";
```

### 11. Distinct ExtentTest association

```java
ThreadLocal<ExtentTest> node = new ThreadLocal<>();
node.set(report.createTest(uniqueName));
```

### 12. Logger thread context

```java
MDC.put("testMethod", methodName);
try { log.info("event=test_started"); } finally { MDC.remove("testMethod"); }
```

### 13. Serial execution command

```powershell
& $maven clean test
```

### 14. Parallel execution command

```powershell
& $maven '-Dtestng.suite=testng-parallel-original-regression.xml' `
  '-Dtestng.output=target/testng-reports/parallel' '-Dparallel.threads=3' exec:java
```

### 15. Maven headless command

```powershell
& $maven '-Dheadless=true' test
```

### 16. Jenkins stage

```groovy
stage('Compile') { steps { sh 'mvn -DskipTests test-compile' } }
```

### 17. GitHub Actions test step

```yaml
- name: Verify
  run: mvn --batch-mode -Dheadless=true verify
```

### 18. Upload test artifacts

```yaml
- uses: actions/upload-artifact@v4
  if: always()
  with:
    name: reports
    path: target/shopflow-reports/**
```

### 19. Calculate speedup

```java
double speedup = serialSeconds / parallelSeconds;
```

For measured values: `92.395 / 61.299 = 1.51x`.

### 20. Detect incorrect driver sharing

```java
log.info("thread={} driverIdentity={}", Thread.currentThread().getId(),
        Integer.toHexString(System.identityHashCode(driver)));
```

If different workers repeatedly show same live driver identity, ownership audit karo.

## Final Architecture Diagram

```text
CLI / CI property overrides
        |
        v
ConfigReader ---- ParallelSuiteConfigurer ---- TestNG worker pool (1..8)
        |                                      |       |       |
        v                                      v       v       v
DriverFactory ---------------------------- DriverManager ThreadLocal<WebDriver>
  | ChromeOptions                             | one session per invocation
  | EdgeOptions                               v
  | password-popup prefs                 BaseTest lifecycle
  | headless/window size                      |
  v                                           v
Selenium Manager -> Chrome / Edge        Page Objects -> SauceDemo / fixtures
                                                |
              +---------------------------------+-------------------+
              v                                                     v
 TestExecutionListener                                      SLF4J + MDC
   -> ThreadLocal<ExtentTest>                                correlated logs
   -> screenshot + diagnostic
   -> target/shopflow-reports/<run-id>
```

## Verified Windows Commands

```powershell
$maven = Join-Path $env:TEMP `
  'shopflow-maven-3.10.0\apache-maven-3.10.0\bin\mvn.cmd'

# Original serial regression (49)
& $maven clean test
& $maven verify

# Existing expanded regression (56)
& $maven '-Dtestng.suite=testng-regression.xml' `
  '-Dtestng.output=target/testng-reports/expanded-regression' exec:java

# Parallel suites
& $maven '-Dtestng.suite=testng-parallel-smoke.xml' `
  '-Dtestng.output=target/testng-reports/parallel-smoke' `
  '-Dheadless=true' '-Dparallel.threads=3' exec:java
& $maven '-Dtestng.suite=testng-parallel-original-regression.xml' `
  '-Dtestng.output=target/testng-reports/parallel-original' `
  '-Dparallel.threads=3' exec:java
& $maven '-Dtestng.suite=testng-parallel-regression.xml' `
  '-Dtestng.output=target/testng-reports/parallel-expanded' `
  '-Dparallel.threads=3' exec:java
& $maven '-Dtestng.suite=testng-parallel-dataprovider.xml' `
  '-Dtestng.output=target/testng-reports/parallel-dataprovider' `
  '-Dparallel.threads=3' exec:java

# Cross-browser and other preserved suites
& $maven '-Dtestng.suite=testng-cross-browser.xml' `
  '-Dtestng.output=target/testng-reports/cross-browser' '-Dheadless=true' exec:java
& $maven '-Dtestng.suite=testng-advanced.xml' `
  '-Dtestng.output=target/testng-reports/advanced' '-Dheadless=true' exec:java
& $maven '-Dtestng.suite=testng-smoke.xml' `
  '-Dtestng.output=target/testng-reports/smoke' exec:java
& $maven '-Dtestng.suite=testng-learning.xml' `
  '-Dtestng.output=target/testng-reports/learning' exec:java
& $maven '-Dtest=com.shopflow.learning.junit.*JupiterSpec' test
```

Controlled reporting command intentionally fails and must not be placed in normal CI gates:

```powershell
& $maven '-Dtestng.suite=testng-parallel-reporting-failure.xml' `
  '-Dtestng.output=target/testng-reports/parallel-reporting-failure' `
  '-Dheadless=true' exec:java
```

## Revision Cheat Sheet

- One invocation = one browser = one ThreadLocal owner.
- Factory creates; manager owns; BaseTest initializes/quits.
- Always `quit()` and `remove()` in `finally` paths.
- Business suite uses class parallelism; browsers use test parallelism; datasets use DP pool.
- Page Object driver is constructor-injected and non-static.
- Extent node and MDC context are per thread and cleared.
- Unique IDs prevent screenshot/diagnostic collisions.
- Headless uses 1920x1080 and keeps password-popup prefs.
- CI uploads evidence with `always`, but never masks Maven failure.
- Local YAML/Jenkinsfile is configuration evidence, not a successful external CI run.

## Phase 9 Checklist

- [x] Phase 8 baseline inspected; `clean test` and `verify` 49/49.
- [x] Backup created at `artifacts/phase9-baseline-20261009-154315`.
- [x] ThreadLocal WebDriver lifecycle implemented and cleaned.
- [x] Chrome, Edge and headless selection verified.
- [x] Parallel smoke 5/5 and original parallel regression 49/49.
- [x] Expanded parallel regression 56/56.
- [x] Parallel DataProvider 7/7 on three worker threads.
- [x] Cross-browser critical journeys 2/2.
- [x] Advanced Selenium 33/33 headless.
- [x] JUnit 16/16 and TestNG learning 9/9.
- [x] Three controlled failures generated correct unique evidence.
- [x] Password Manager protection remains in Chrome and Edge options.
- [x] No Thread.sleep, implicit wait, retry or JavaScript click added.
- [x] GitHub Actions workflow and Jenkinsfile created.
- [ ] GitHub-hosted runner execution (pending repository push/run authorization).
- [ ] Jenkins runtime validation (pending configured Jenkins Linux agent).
- [x] No Git initialization, commit, push, deploy or Phase 10 work.

## Backup and Restore

Baseline snapshot: `C:\devlabs\artifacts\phase9-baseline-20261009-154315` (96 files).
Safest restore leaves the working project untouched and creates a separate Phase 8 copy:

```powershell
$backup = 'C:\devlabs\artifacts\phase9-baseline-20261009-154315'
$restore = 'C:\devlabs-phase8-restored'
New-Item -ItemType Directory -Path $restore
Copy-Item -Path (Join-Path $backup '*') -Destination $restore -Recurse
```

Open `$restore` and run `mvn test` there after confirming its Maven/JDK environment. Copying
the backup over the live workspace is not recommended because Phase 9-created files would still
remain unless removed explicitly.
