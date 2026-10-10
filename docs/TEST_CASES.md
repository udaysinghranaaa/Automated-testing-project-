# Test Case Catalog

Counts below describe the code as audited on 9 October 2026. Setup/configuration methods are excluded.

## SauceDemo business regression — 49 methods

### Foundation (4)

| Documentation ID | Test method | Expected result |
|---|---|---|
| BASE-001 | `shouldDisplayExpectedHomepageTitle` | title is `Swag Labs` |
| BASE-002 | `shouldDisplayLoginForm` | username, password and login controls visible |
| BASE-003 | `shouldEnableLoginButton` | login button enabled |
| BASE-004 | `shouldNavigateToInventoryAfterSuccessfulLogin` | stable inventory state and URL |

### Login (10)

| ID | Scenario | Expected result |
|---|---|---|
| TC_LOGIN_001 | valid username/password | inventory opens |
| TC_LOGIN_002 | invalid username | exact mismatch error; no inventory |
| TC_LOGIN_003 | invalid password | exact mismatch error; no inventory |
| TC_LOGIN_004 | both invalid | exact mismatch error; no inventory |
| TC_LOGIN_005 | empty username | username-required error |
| TC_LOGIN_006 | empty password | password-required error |
| TC_LOGIN_007 | both empty | username error has validation priority |
| TC_LOGIN_008 | locked-out account | exact locked-out error |
| TC_LOGIN_009 | type and clear fields | values accepted and cleared |
| TC_LOGIN_010 | logout after login | login form ready and inventory unavailable |

### Products, sorting and details (10)

| ID | Scenario | Expected result |
|---|---|---|
| TC_PRODUCT_001 | inventory loads | title/container visible |
| TC_PRODUCT_002 | list availability | at least one product |
| TC_PRODUCT_003 | names/descriptions | every card has meaningful text |
| TC_PRODUCT_004 | prices/images | non-negative prices and usable sources |
| TC_SORT_001 | name A–Z | actual list equals independently sorted list |
| TC_SORT_002 | name Z–A | reverse lexical order |
| TC_SORT_003 | price low–high | ascending numeric order |
| TC_SORT_004 | price high–low | descending numeric order |
| TC_DETAIL_001 | open product | detail name/description/price match card |
| TC_DETAIL_002 | back navigation | returns to inventory |

### Cart (9)

| ID | Scenario | Expected result |
|---|---|---|
| TC_CART_001 | add one | badge `1`; control changes to Remove |
| TC_CART_002 | add two | exactly both items in cart |
| TC_CART_003 | badge count | badge equals selected count |
| TC_CART_004 | remove on inventory | badge and state clear |
| TC_CART_005 | selected set | cart contains no extra/missing item |
| TC_CART_006 | price consistency | cart prices equal inventory by name |
| TC_CART_007 | remove in cart | selected item removed, other retained |
| TC_CART_008 | continue shopping | inventory returns and item persists |
| TC_CART_009 | internal navigation | cart survives product detail round-trip |

### Checkout (16)

| ID | Scenario | Expected result |
|---|---|---|
| TC_CHECKOUT_001 | cart to checkout | information page opens |
| TC_CHECKOUT_002 | form controls | three inputs plus continue/cancel available |
| TC_CHECKOUT_003 | empty first name | exact required error |
| TC_CHECKOUT_004 | empty last name | exact required error |
| TC_CHECKOUT_005 | empty postal code | exact required error |
| TC_CHECKOUT_006 | all empty | first-name validation appears first |
| TC_CHECKOUT_007 | valid information | overview opens |
| TC_CHECKOUT_008 | overview products | exact selected products shown |
| TC_CHECKOUT_009 | overview prices | prices match cart by product |
| TC_CHECKOUT_010 | subtotal | displayed subtotal equals item sum |
| TC_CHECKOUT_011 | tax and total | total equals subtotal plus tax |
| TC_CHECKOUT_012 | cancel information | returns to cart |
| TC_CHECKOUT_013 | cancel overview | returns to inventory |
| TC_CHECKOUT_014 | finish | confirmation text and state shown |
| TC_CHECKOUT_015 | Back Home | reset inventory and empty cart |
| TC_CHECKOUT_016 | full journey | data/items/money persist through completion |

## Advanced local-fixture suite — 33 methods

| IDs | Area | Covered behavior |
|---|---|---|
| TC_ADV_001–005 | alerts | simple, accept/dismiss confirmation, prompt input/dismiss |
| TC_ADV_006–009 | frames | WebElement switch, interaction, nesting/parent, default content |
| TC_ADV_010–013 | windows | tab, window, close/return, multiple handles |
| TC_ADV_014–018 | Actions | hover, double-click, context-click, drag/drop, hold/release |
| TC_ADV_019–022 | keyboard | Ctrl+A, Tab focus, Enter submit, Shift modifier |
| TC_ADV_023–025 | dropdowns | visible text, multi-select, custom dropdown |
| TC_ADV_026–029 | dynamic UI | visibility, invisibility, stale replacement, FluentWait |
| TC_ADV_030 | upload | send a temporary file path to file input |
| TC_ADV_031 | shadow DOM | Selenium `SearchContext` in open root |
| TC_ADV_032–033 | scrolling | W3C wheel viewport and overflow container |

These are deterministic WebDriver-learning tests. They are not SauceDemo feature claims.

## DataProvider — 7 invocations of one method

| Dataset | Expected |
|---|---|
| valid standard user | inventory success |
| invalid username | credential mismatch |
| invalid password | credential mismatch |
| both invalid | credential mismatch |
| empty username | username required |
| empty password | password required |
| locked-out account | locked-out error |

The provider is parallel. Every invocation still receives a fresh browser via `@BeforeMethod`.

## JUnit learning module — 16 invocations

| Spec | Invocations | Coverage |
|---|---:|---|
| `InputValidatorJupiterSpec` | 7 | 3 accepted strings, 3 blank strings, null behavior |
| `PriceCalculatorJupiterSpec` | 9 | add/empty, total, invalid inputs, 3 quantity rows, 3 parse rows |

## TestNG learning suite — 9 tests

Lifecycle ordering (2), hard/soft assertion behavior (4), priority/dependency behavior (3). Controlled exceptions are asserted inside green learning tests; they do not conceal failures.

## Smoke and browser matrices

- Smoke: title, valid login, inventory load, add-to-cart and order completion = 5.
- Cross-browser: the same critical commerce journey on Chrome and Edge = 2 invocations.
- Original parallel: 49 methods across three class workers.
- Expanded parallel: 49 methods plus 7 data rows = 56 invocations.

