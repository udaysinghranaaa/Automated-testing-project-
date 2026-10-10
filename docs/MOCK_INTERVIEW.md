# Mock Interviews

## Simulation 1 — Junior-to-mid QA Automation (30 minutes)

**Questions:** Explain ShopFlow in 60 seconds; write a stable login; `findElement` vs `findElements`; explicit vs implicit wait; POM; positive/negative login; teardown; severity vs priority; basic SQL JOIN; reverse a string.

**Answer guidance:** Use principle + one project example. For login, assert URL and inventory state. For negative paths, exact error plus no inventory. Draw lifecycle. Write compilable code and mention cleanup.

**Follow-ups:** What if login button is visible but an overlay exists? Why not sleep? What if `findElements` is empty? How would you report a blocked environment?

**Rubric (20):** fundamentals 5, code correctness 5, debugging 4, testing thought process 4, honesty/communication 2. **Common mistakes:** memorized definitions, weak visibility-only assertion, no `quit`, claiming 82 SauceDemo tests. **Revision:** README, Q1–35 Selenium, SQL joins, Java exercises 1–12.

## Simulation 2 — SDET Framework Design (45 minutes)

**Questions:** Walk architecture; factory vs manager; prove parallel safety; design Page Object API; data-driven counts; cross-browser strategy; reporting lifecycle; retry policy; CI stages; diagnose intermittent logout; calculate checkout total; SQL flaky-test query.

**Answer guidance:** Draw layers and driver sequence. Show ThreadLocal set/get/quit/remove. Explain isolated mutable state and artifact IDs. Use the Chrome warning incident as an evidence-based debugging story. Distinguish methods from invocations.

**Follow-ups:** What if a worker reuses a thread? What if screenshot capture throws? How would Grid change creation? Why is three workers appropriate? What architectural debt remains?

**Rubric (30):** architecture 7, Selenium reliability 7, concurrency 5, test design 4, CI/reporting 4, trade-offs 3. **Common mistakes:** static driver, JS click, global retries, assertions in every page, external CI claim without run. **Revision:** Architecture diagrams, Framework guide, deep dives C–F, Phase 9/10 notes.

## Simulation 3 — Senior debugging and quality leadership (60 minutes)

**Scenario:** Chrome suite has click timeouts after login; Edge passes. Ask the candidate to lead investigation and release communication.

**Questions:** What evidence first? How isolate variables? Browser vs app vs automation classification? Smallest fix? Verification matrix? What remains unknown? How communicate risk? Which tests belong in PR vs nightly? What metrics matter? Review a proposed `Thread.sleep(5000)` and retry analyzer.

**Answer guidance:** Preserve failure state, run targeted fresh sessions, record capabilities/screenshot/DOM/console, compare same scenario in Edge, form a single hypothesis, use supported session-only options, rerun targeted repeatedly then feature/full suites. Reject sleep/retry because they hide cause. Separate local proof from remote CI.

**Follow-ups:** Both browsers now fail—what changes? Popup gone but failures remain—what next? Site is unavailable—what can still be verified? How prevent security preference from changing user profile? How would you measure flakiness?

**Rubric (40):** diagnostic discipline 10, technical depth 8, experiment design 6, risk communication 6, maintainability/security 5, leadership clarity 5. **Common mistakes:** random workaround stacking, confirmation bias, changing multiple variables, calling blocked tests passed, attacking public site. **Revision:** debugging deep dive, report artifacts, manual Q18/28/49, SQL queries 30–39.

## Self-scoring bands

- 85–100%: clear evidence, code and trade-offs; ready to lead the discussion.
- 70–84%: solid implementation; rehearse follow-ups and limitations.
- 50–69%: revisit waits, lifecycle, collections/SQL and defect communication.
- Below 50%: perform live exercises and explain each line aloud before retrying.
