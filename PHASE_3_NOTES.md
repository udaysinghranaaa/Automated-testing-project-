# Phase 3 Notes — Products, Sorting and Shopping Cart

## Chapter 1 — Project Introduction

Phase 3 ka objective SauceDemo ke inventory aur cart business flows ko real Chrome
browser mein validate karna hai. Login ke baad user products dekh sakta hai, unhe four
modes mein sort kar sakta hai, product details open kar sakta hai, products cart mein
add/remove kar sakta hai aur internal navigation ke baad cart state retain kar sakta hai.

Product testing ka purpose sirf elements count karna nahi hai. Hum product name,
description, monetary price aur image source validate karte hain. Cart testing mein badge,
selected names, product-specific prices, removal aur navigation state validate hoti hai.
Checkout is phase ka part nahi hai.

Phase 3 classes:

- `ProductTest.java`: 10 listing, sorting aur detail tests.
- `CartTest.java`: 9 add, remove, badge, price aur navigation tests.

## Chapter 2 — Java Collections

### Array vs ArrayList

Array ka size creation ke time fixed hota hai:

```java
String[] names = {"Backpack", "Bike Light"};
```

`ArrayList` dynamic hai; runtime par items add/remove ho sakte hain:

```java
List<String> names = new ArrayList<>();
names.add("Backpack");
names.add("Bike Light");
```

`List` interface ordered collection ka contract hai. `ArrayList` uski resizable-array
implementation hai. Variable ko interface type dena flexible design hai:

```java
List<String> productNames = new ArrayList<>();
```

### List<WebElement>

`findElements()` multiple matching DOM elements ko `List<WebElement>` mein return karta hai:

```java
List<WebElement> productCards = driver.findElements(
        By.cssSelector("[data-test='inventory-item']"));
```

No match par exception ke badle empty list milti hai. Phase 3 actual tests product cards,
names, prices aur cart badge lookup mein ye behavior use karte hain.

### Enhanced for-loop

Index ki zaroorat na ho toh enhanced loop readable hota hai:

```java
for (WebElement productCard : productCards) {
    String name = productCard.findElement(PRODUCT_NAME).getText();
    Assert.assertFalse(name.isBlank());
}
```

### Collections.sort and Comparator

Actual UI list ko mutate nahi karna chahiye. Independent expected copy banao:

```java
List<String> actual = readProductNames();
List<String> expected = new ArrayList<>(actual);
Collections.sort(expected, Comparator.naturalOrder());
Assert.assertEquals(actual, expected);
```

- `Comparator.naturalOrder()` ascending order deta hai.
- `Comparator.reverseOrder()` descending order deta hai.
- `List.equals()` size, values aur order tino compare karta hai.

### Map and HashMap

`Map` key-value association rakhta hai. Product name ko price se safely match karne ke
liye index ke badle map use hota hai:

```java
Map<String, BigDecimal> prices = new HashMap<>();
prices.put("Sauce Labs Backpack", new BigDecimal("29.99"));
BigDecimal backpackPrice = prices.get("Sauce Labs Backpack");
```

Project code deterministic insertion order ke liye `LinkedHashMap` use karta hai. Ye bhi
`Map` implementation hai; cart comparison correctness ordering par depend nahi karti.

## Chapter 3 — BigDecimal

Binary floating-point ki wajah se `double` money ke liye risky hai:

```java
System.out.println(0.1 + 0.2); // commonly 0.30000000000000004
```

`BigDecimal` decimal value exact form mein represent karta hai.

### Example 1 — String se creation

```java
BigDecimal price = new BigDecimal("29.99");
```

String constructor prefer karo; `new BigDecimal(29.99)` binary approximation capture kar sakta hai.

### Example 2 — UI price parsing

```java
String displayed = "$29.99";
BigDecimal price = new BigDecimal(displayed.replace("$", "").trim());
```

Ye exact pattern `ProductTest` aur `CartTest` ke `parsePrice()` helper mein use hua hai.

### Example 3 — Non-negative validation

```java
Assert.assertTrue(price.compareTo(BigDecimal.ZERO) >= 0);
```

### Example 4 — Numeric equality

```java
BigDecimal first = new BigDecimal("10.0");
BigDecimal second = new BigDecimal("10.00");
Assert.assertEquals(first.compareTo(second), 0);
```

### Example 5 — Sorting prices

```java
List<BigDecimal> prices = new ArrayList<>(
        List.of(new BigDecimal("29.99"), new BigDecimal("7.99")));
prices.sort(Comparator.naturalOrder());
```

`equals()` value ke saath scale bhi compare karta hai, isliye `10.0` aur `10.00` unequal
ho sakte hain. `compareTo()` numeric magnitude compare karta hai aur is case mein zero deta hai.

## Chapter 4 — Selenium Product Automation

### Actual project pattern: product cards

```java
private static final By PRODUCT_CARDS =
        By.cssSelector("[data-test='inventory-item']");

List<WebElement> productCards = driver.findElements(PRODUCT_CARDS);
```

Har card ke andar scoped lookup name-description association preserve karta hai:

```java
String name = productCard.findElement(PRODUCT_NAME).getText().trim();
String description = productCard.findElement(PRODUCT_DESCRIPTION).getText().trim();
```

Global name list aur global description list ko indexes se zip karna fragile ho sakta tha.

### Actual project pattern: product-specific relative XPath

```java
By productCard = By.xpath("//div[@data-test='inventory-item']"
        + "[.//*[@data-test='inventory-item-name' and normalize-space()='"
        + productName + "']]");
```

Ye fixed position use nahi karta. Named product ka containing card return karta hai.

### Actual project pattern: details

Inventory card se name, description aur price capture hote hain. Usi card ka name click
karne ke baad current detail container wait hota hai. Detail values captured values se compare
hoti hain. Isse test same product compare karta hai.

## Chapter 5 — Sorting Automation

Har sorting test ka flow:

1. `Select.selectByValue()` desired option choose karta hai.
2. Explicit wait selected option ke value ko confirm karta hai.
3. DOM ko dobara query karke actual displayed list banayi jati hai.
4. `new ArrayList<>(actual)` independent expected copy banata hai.
5. Expected copy comparator se sort hoti hai.
6. `Assert.assertEquals(actual, expected)` order validate karta hai.

Worked name example:

```text
Original UI: [Backpack, Bike Light, Onesie, Fleece Jacket]
Expected A-Z copy: [Backpack, Bike Light, Fleece Jacket, Onesie]
Actual UI after A-Z: [Backpack, Bike Light, Fleece Jacket, Onesie]
Result: actual.equals(expected) == true
```

Worked descending example:

```text
Expected Z-A: [Onesie, Fleece Jacket, Bike Light, Backpack]
```

Worked price example:

```text
Original: [29.99, 9.99, 15.99, 49.99]
Expected low-high: [9.99, 15.99, 29.99, 49.99]
Expected high-low: [49.99, 29.99, 15.99, 9.99]
```

Actual list ko sort karke usi se compare karna false-positive hota. Phase 3 mein sirf
expected copy sort hoti hai. Duplicate price values naturally equal compare hote hain; test
unsupported tie ordering invent nahi karta.

## Chapter 6 — Cart Automation

- **Add:** Named card locate karke card-scoped add button click hota hai.
- **Remove:** Inventory/cart card ke andar remove button use hota hai.
- **Badge:** Text ko integer mein parse karke actual actions ki collection size se compare hota hai.
- **Names:** Two selected names ko cart ke complete name set se compare kiya jata hai.
- **Prices:** `Map<String, BigDecimal>` inventory price ko same cart product se associate karta hai.
- **State:** Continue Shopping aur detail navigation ke baad badge plus cart contents verify hote hain.

Ordering business requirement na ho toh `Set` correct choice hai. TC_CART_005 mein exact
set comparison ke saath original list size bhi compare hoti hai, isliye unrelated aur duplicate
items detect ho sakte hain. Sorting tests mein ordering requirement hai, isliye `List` use hoti hai.

## Chapter 7 — Explicit Waits

Dynamic React UI event ke baad DOM update karta hai. Immediate lookup intermittent failure de
sakta hai. `WebDriverWait` condition ko timeout tak poll karta hai:

```java
WebElement badge = wait.until(driver -> {
    List<WebElement> badges = driver.findElements(CART_BADGE);
    return badges.size() == 1 && badges.get(0).getText().equals("2")
            ? badges.get(0) : null;
});
```

Used conditions:

- `visibilityOfElementLocated`: inventory/detail/cart visible.
- `elementToBeClickable`: dropdown, cart link, navigation button ready.
- `urlToBe` / `urlContains`: correct navigation completed.
- `invisibilityOfElementLocated`: empty-cart badge disappears.

Condition timeout par `TimeoutException` aata hai. Page re-render ke across old element reuse
karne par `StaleElementReferenceException` aa sakta hai. Sorting/removal ke baad tests locators
se elements re-read karte hain. No implicit wait aur no `Thread.sleep()` use hua hai.

## Chapter 8 — Real Test Cases

Precondition for all tests: Chrome available, SauceDemo reachable, and standard demo user valid.

| ID | Java method | Objective and main steps | Expected result | Targeted status |
| --- | --- | --- | --- | --- |
| TC_PRODUCT_001 | `inventoryPageShouldLoad` | Login; wait inventory; inspect URL/title/container | Products page identified and visible | Passed |
| TC_PRODUCT_002 | `productListShouldNotBeEmpty` | Read `List<WebElement>` cards; iterate | Nonempty and every card visible | Passed |
| TC_PRODUCT_003 | `productsShouldHaveNamesAndDescriptions` | Read each card-scoped name/description | Every value nonblank | Passed |
| TC_PRODUCT_004 | `productsShouldHaveNonNegativePricesAndUsableImageSources` | Parse each price; inspect image source | Price nonnegative; one nonblank image source | Passed |
| TC_SORT_001 | `productsShouldSortByNameAscending` | Select `az`; compare independent sorted copy | UI names A-Z | Passed |
| TC_SORT_002 | `productsShouldSortByNameDescending` | Select `za`; reverse comparator | UI names Z-A | Passed |
| TC_SORT_003 | `productsShouldSortByPriceAscending` | Select `lohi`; parse BigDecimal list | Prices nondecreasing | Passed |
| TC_SORT_004 | `productsShouldSortByPriceDescending` | Select `hilo`; reverse price comparator | Prices nonincreasing | Passed |
| TC_DETAIL_001 | `productDetailsShouldMatchInventoryCard` | Capture named card; open detail; compare | Same name, description and numeric price | Passed |
| TC_DETAIL_002 | `backToProductsShouldReturnToInventory` | Open detail; click Back to products | Inventory URL/list visible | Passed |
| TC_CART_001 | `addingOneProductShouldUpdateBadgeAndButton` | Add named product | Badge 1 and Remove state | Passed |
| TC_CART_002 | `addingTwoProductsShouldPlaceBothInCart` | Add two; open cart | Both exact names present | Passed |
| TC_CART_003 | `cartBadgeShouldMatchNumberOfAddedProducts` | Add selected list; parse badge | Badge equals actions count | Passed |
| TC_CART_004 | `removingProductOnInventoryShouldClearCartState` | Add then remove on inventory | Add state restored; empty badge absent | Passed |
| TC_CART_005 | `cartShouldContainOnlySelectedProducts` | Add two; exact set comparison | No unrelated/duplicate cart item | Passed |
| TC_CART_006 | `cartPricesShouldMatchInventoryPricesByProductName` | Map inventory prices; open cart | Each named price numerically equal | Passed |
| TC_CART_007 | `removingOneProductFromCartShouldKeepTheOther` | Add two; remove one in cart | Removed absent, other remains, badge 1 | Passed |
| TC_CART_008 | `continueShoppingShouldPreserveSelectedProduct` | Add; cart; continue shopping | Inventory shown and selection retained | Passed |
| TC_CART_009 | `cartStateShouldSurviveProductDetailNavigation` | Add; open detail; open cart | Badge and contents agree | Passed |

## Chapter 9 — Interview Questions

1. **`findElement` vs `findElements`?** First returns one or throws; second returns a list, empty when absent.
2. **`List` vs `ArrayList`?** `List` interface hai; `ArrayList` resizable-array implementation hai.
3. **Independent expected copy kyun?** Actual UI data mutate karke false-positive prevent karne ke liye.
4. **`Collections.sort()` kya karta hai?** Mutable list ko supplied/natural ordering se in-place sort karta hai.
5. **Comparator kya hai?** Do objects ka ordering rule define karta hai.
6. **Money ke liye `double` kyun avoid karein?** Binary representation decimal rounding error introduce kar sakti hai.
7. **`BigDecimal.equals` vs `compareTo`?** `equals` scale dekhta hai; `compareTo` numeric magnitude.
8. **CSS vs XPath?** CSS concise attributes ke liye; XPath ancestor/text relationships ke liye useful.
9. **Dynamic product locator kaise banaya?** Name match karne wale descendant ka containing product card locate kiya.
10. **Absolute XPath kyun brittle hai?** DOM hierarchy change hote hi full path break hota hai.
11. **Sorting UI kaise validate karte hain?** Actual list, separate sorted expected copy, ordered equality.
12. **Duplicate prices kaise handle hote hain?** BigDecimal value order compare hota hai; tie order invent nahi hota.
13. **Cart contents by index kyun compare nahi kiye?** Order business requirement nahi; name-based matching safer hai.
14. **Map cart-price testing mein kyun useful hai?** Product identity ko expected price se directly associate karta hai.
15. **Explicit wait kya hai?** Specific state ko bounded polling se wait karta hai.
16. **Stale element kya hai?** DOM replacement ke baad old WebElement reference invalid ho jata hai.
17. **Stale element prevent kaise kiya?** UI changes ke baad locator se elements re-fetch kiye.
18. **Tests independent kaise hain?** Every `@BeforeMethod` fresh browser/login aur teardown complete quit karta hai.
19. **Cart empty badge zero kyun assert nahi kiya?** Actual behavior badge ko remove karta hai; unsupported behavior invent nahi karte.
20. **Image validation ki limitation?** Nonblank `src` existence verify hoti hai, visual correctness nahi.
21. **`@AfterMethod(alwaysRun=true)` kyun?** Failed assertion ke baad bhi browser cleanup intent maintain hota hai.
22. **Product card scoped lookup kyun?** Name, description, price ki association same product ke saath preserve hoti hai.

## Chapter 10 — Coding Practice

Following snippets illustrative practice hain; production helpers ke exact replacements nahi.

### 1. Count all product elements

```java
int count = driver.findElements(By.cssSelector("[data-test='inventory-item']")).size();
Assert.assertTrue(count > 0);
```

`findElements()` se safe count milta hai.

### 2. Print all product names

```java
for (WebElement element : driver.findElements(
        By.cssSelector("[data-test='inventory-item-name']"))) {
    System.out.println(element.getText());
}
```

Enhanced loop each matching element visit karta hai.

### 3. Extract all product prices

```java
List<BigDecimal> prices = new ArrayList<>();
for (WebElement element : driver.findElements(
        By.cssSelector("[data-test='inventory-item-price']"))) {
    prices.add(new BigDecimal(element.getText().replace("$", "")));
}
```

Currency symbol remove karke exact decimal parse hota hai.

### 4. Sort names ascending

```java
List<String> expected = new ArrayList<>(names);
expected.sort(Comparator.naturalOrder());
```

Independent copy original UI order preserve karti hai.

### 5. Sort names descending

```java
List<String> expected = new ArrayList<>(names);
Collections.sort(expected, Comparator.reverseOrder());
```

Reverse comparator descending alphabetical order deta hai.

### 6. Sort prices ascending

```java
List<BigDecimal> expected = new ArrayList<>(prices);
expected.sort(BigDecimal::compareTo);
```

Method reference numeric compare use karta hai.

### 7. Sort prices descending

```java
List<BigDecimal> expected = new ArrayList<>(prices);
expected.sort(Comparator.reverseOrder());
```

### 8. Find cheapest product

```java
BigDecimal cheapest = prices.stream()
        .min(BigDecimal::compareTo)
        .orElseThrow();
```

Empty list unexpected ho toh `orElseThrow()` problem expose karta hai.

### 9. Find most expensive product

```java
BigDecimal mostExpensive = prices.stream()
        .max(BigDecimal::compareTo)
        .orElseThrow();
```

### 10. Verify cart item count

```java
int items = driver.findElements(By.cssSelector(
        "[data-test='cart-list'] [data-test='inventory-item']")).size();
int badge = Integer.parseInt(driver.findElement(
        By.cssSelector("[data-test='shopping-cart-badge']")).getText());
Assert.assertEquals(badge, items);
```

Badge aur actual cart DOM ka business consistency check hota hai.

## Chapter 11 — Debugging Practice

### Scenario 1 — Detail container timeout

- **Symptom:** Product click ke baad `TimeoutException`.
- **Likely causes:** Old `data-test` assumption, navigation failure, slow render.
- **Investigation:** URL, current `outerHTML`, current attributes inspect karo.
- **Solution:** Current rendered selector `.inventory_details_container[data-test='inventory-item']` use karo.
- **Prevention:** Public examples copy karne se pehle live DOM confirm karo.

### Scenario 2 — Sorting test always passes

- **Symptom:** Deliberately broken sorting bhi green.
- **Likely cause:** Actual list ko sort karke same list compare ki.
- **Investigation:** Object copies aur mutation trace karo.
- **Solution:** `new ArrayList<>(actual)` expected copy sort karo; actual untouched rakho.
- **Prevention:** Arrange actual and expected variables explicitly.

### Scenario 3 — Stale element after remove

- **Symptom:** `StaleElementReferenceException` after button click.
- **Likely cause:** React ne product card/button replace kar diya.
- **Investigation:** Stack trace mein old WebElement usage dekho.
- **Solution:** Locator se updated card/button re-find karo.
- **Prevention:** DOM-changing action ke across WebElement cache mat karo.

### Scenario 4 — Cart price mismatch

- **Symptom:** Wrong product ki price compare ho rahi hai.
- **Likely cause:** Inventory/cart arrays ko index se pair kiya.
- **Investigation:** Names aur prices log/map karke association inspect karo.
- **Solution:** `Map<String, BigDecimal>` and named cart item use karo.
- **Prevention:** Business identity key se data correlate karo.

### Scenario 5 — Browser processes remain

- **Symptom:** Tests ke baad ChromeDriver processes active.
- **Likely cause:** Assertion exception se teardown skip ya only `close()`.
- **Investigation:** Test lifecycle and process list check karo.
- **Solution:** Null-safe `@AfterMethod(alwaysRun = true)` with `driver.quit()`.
- **Prevention:** Cleanup ko every test class ka mandatory lifecycle part rakho.

### Scenario 6 — Empty cart badge assertion fails

- **Symptom:** Test zero text expect karta hai, badge missing milta hai.
- **Likely cause:** Invented requirement.
- **Investigation:** Remove ke baad live DOM inspect karo.
- **Solution:** Actual behavior ke according badge invisibility/absence assert karo.
- **Prevention:** Expected result implementation se pehle application behavior confirm karo.

## Chapter 12 — Final Revision

### Quick cheat sheet

- Multiple elements: `driver.findElements(locator)`
- Copy list: `new ArrayList<>(actual)`
- Ascending: `Comparator.naturalOrder()`
- Descending: `Comparator.reverseOrder()`
- Money parse: `new BigDecimal(text.replace("$", "").trim())`
- Numeric money equality: `first.compareTo(second) == 0`
- Named data: `Map<String, BigDecimal>`
- Wait: `wait.until(ExpectedConditions...)`
- Cleanup: `driver.quit()` in always-run teardown

### Key Java methods

`add`, `size`, `isEmpty`, `contains`, `equals`, `sort`, `compareTo`, `put`, `get`.

### Key Selenium methods

`findElement`, `findElements`, `getText`, `getAttribute`, `click`, `isDisplayed`,
`selectByValue`, `quit`.

### Most important assertions

- `assertEquals(actual, expected)` for exact ordered values/counts.
- `assertTrue(condition)` for visibility, presence and numeric rules.
- `assertFalse(condition)` for absence and nonblank checks.
- `assertNotNull(value)` before using required attributes.

### Five interview-ready answers

1. UI sorting ko independent expected copy se validate karta hoon; actual list mutate nahi karta.
2. Money ke liye String-based `BigDecimal` aur numeric equality ke liye `compareTo` use karta hoon.
3. Products ko fixed index ke badle name-based relative XPath se identify karta hoon.
4. Dynamic transitions ko explicit conditions se wait karta hoon; implicit wait mix nahi karta.
5. Har test fresh browser state se start aur always-run `quit()` se finish hota hai.

### Practice assignment

Without changing production tests, ek practice method design karo jo inventory ka cheapest
product name and price calculate kare, same product detail open kare, aur detail price compare
kare. Fixed index aur hardcoded expected price use mat karo.

### Completion checklist

- [x] 10 product/sort/detail scenarios implemented
- [x] 9 cart scenarios implemented
- [x] Java collections used meaningfully
- [x] BigDecimal used for currency
- [x] Stable IDs/data-test/relative XPath used
- [x] Explicit waits only
- [x] Independent browser lifecycle
- [x] Targeted ProductTest and CartTest runs passed
- [x] Full regression result and the later verify-only logout blocker are reported honestly
