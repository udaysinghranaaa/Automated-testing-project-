# Manual Testing Interview — 50 Q&A

1. **What is testing?** Evaluating product behavior and risks against expectations to provide decision information.
2. **QA vs QC?** QA improves process/prevention; QC evaluates product outputs. Teams often perform both.
3. **Verification vs validation?** Building correctly against specification vs building the right user solution.
4. **Test scenario vs test case?** High-level behavior/risk vs detailed conditions, steps/data and expected result.
5. **Good test case fields?** ID, objective, preconditions, data, steps, expected result, priority and traceability.
6. **What is test basis?** Requirements, designs, risks, contracts and user knowledge used to derive tests.
7. **Positive testing?** Valid inputs and intended journeys.
8. **Negative testing?** Invalid, missing, conflicting or unauthorized conditions.
9. **Boundary value analysis?** Test at and just around limits because defects cluster there.
10. **Equivalence partitioning?** Divide inputs into groups expected to behave alike and sample each.
11. **Decision table?** Map combinations of conditions to actions for business-rule completeness.
12. **State transition testing?** Validate allowed/forbidden events and outputs across states.
13. **Exploratory testing?** Simultaneous learning, design and execution guided by a charter and notes.
14. **Use-case testing?** End-to-end actor goals including alternate and exception paths.
15. **Error guessing?** Experience-based probes for common failures; supplement, never replace systematic techniques.
16. **Severity vs priority?** Impact of defect vs urgency/order of fixing.
17. **Defect lifecycle?** New, triaged, assigned, fixed, retest, closed/reopened; exact states vary by team.
18. **Good bug report?** Clear title, environment/build, reproducible steps/data, actual vs expected, evidence and impact.
19. **Cannot reproduce response?** Match build/data/account/environment, inspect evidence/logs, ask precise questions, do not close blindly.
20. **Regression testing?** Check unchanged behavior after change.
21. **Retesting?** Confirm the specific defect fix.
22. **Smoke testing?** Fast broad check that critical functions/build are testable.
23. **Sanity testing?** Focused confidence check after a small change; terminology varies.
24. **Test plan contents?** Scope, risks, strategy, environments, data, schedule, roles, entry/exit and reporting.
25. **Entry criteria?** Conditions needed to start meaningful testing.
26. **Exit criteria?** Agreed evidence threshold; never “zero bugs” by default.
27. **Traceability matrix?** Maps requirements/risks to tests and results, revealing gaps.
28. **Risk-based testing?** Prioritize likelihood × impact and adapt depth to evidence.
29. **When stop testing?** When exit criteria and residual risk are understood and accepted by decision owners.
30. **Test oracle?** Source used to decide correctness: requirement, calculation, prior version, domain rule, independent system.
31. **Authentication vs authorization?** Identity proof vs allowed actions/resources.
32. **Session tests?** Expiry, logout invalidation, concurrent sessions, back navigation, cookie scope and unauthorized reuse.
33. **E-commerce cart tests?** add/remove, exact items, prices, totals, persistence, stock, concurrency and navigation.
34. **Checkout tests?** required fields, boundaries, address rules, exact summary/tax/total, cancel/back, idempotence and confirmation.
35. **Sorting tests?** Every option, full order, ties, case/numeric semantics, state after navigation and empty/single lists.
36. **Search tests?** Exact/partial/case, whitespace, special chars, no result, performance and injection—only if product has search.
37. **Compatibility testing?** Supported browsers/OS/devices, versions, viewport/input modes and graceful degradation.
38. **Accessibility basics?** Keyboard, focus, names/roles, contrast, zoom/reflow, errors and screen-reader semantics.
39. **Localization tests?** Translation expansion, encoding, date/time/number/currency, pluralization, RTL and locale fallback.
40. **Performance categories?** Load, stress, spike, endurance, volume and scalability; define workload and service objectives.
41. **Security testing boundary?** Authorized scope only; validate common controls without bypassing or attacking unapproved systems.
42. **API testing essentials?** Contract, status, schema, headers, auth, idempotence, boundaries, errors and data effects.
43. **Database testing essentials?** Constraints, CRUD integrity, transactions, migrations, precision, audit and access controls.
44. **Production testing?** Carefully approved low-impact checks with synthetic data, observability and rollback; never casual experimentation.
45. **Shift left/right?** Earlier prevention/feedback vs production observability and learning; both can coexist.
46. **What should be automated?** Stable, repeatable, high-value checks with deterministic oracle; not every exploratory/usability judgment.
47. **Why tests become flaky?** Async timing, shared state, unstable data/environment, brittle locators, resource pressure or product nondeterminism.
48. **Metric traps?** Raw case count/pass rate can reward shallow tests; pair metrics with risk, escaped defects and flake/lead time.
49. **How communicate release risk?** State verified scope, failures/blockers, evidence, affected users, workaround and explicit residual risk.
50. **SauceDemo limitation examples?** No search, quantity input, real payment/API/iframe in its business UI; do not invent coverage.

## Sample bug report

**Title:** Chrome automated session: password warning overlays checkout navigation after login  
**Environment:** Windows 11, Chrome 154, fresh Selenium-managed profile  
**Steps:** login with public demo standard user; add product; open cart; select checkout  
**Actual:** browser-level breached-password surface intercepts UI interaction  
**Expected:** automation profile reaches checkout information page  
**Evidence:** screenshot, console/DOM state, browser capabilities; Edge comparison  
**Classification:** browser automation-environment issue, not SauceDemo checkout defect  
**Resolution verification:** supported session-only Chrome preferences; targeted checkout/logout, 16-test checkout and full regression.

