# Core Java Interview — 70 Q&A

1. **JDK, JRE, JVM?** JDK builds/runs; JRE is runtime libraries plus JVM; JVM executes bytecode.
2. **Why Java is platform-independent?** Source compiles to bytecode interpreted/JIT-compiled by a platform JVM.
3. **Primitive vs reference?** Primitive holds a value; reference points to an object or null.
4. **Stack vs heap?** Frames/local variables are associated with thread stacks; objects generally live on managed heap.
5. **Class vs object?** Class defines type/behavior; object is its runtime instance.
6. **Constructor purpose?** Establish valid object state; it has no return type and can be overloaded.
7. **`this` vs `super`?** `this` addresses current instance/constructor; `super` addresses parent members/constructor.
8. **Encapsulation?** Hide mutable representation and expose controlled operations.
9. **Inheritance?** Derive behavior/state via `extends`; use for a true is-a relationship.
10. **Polymorphism?** The same interface call dispatches to an object's runtime implementation.
11. **Abstraction?** Expose essential contract and hide implementation details.
12. **Interface vs abstract class?** Interface defines capabilities/multiple type inheritance; abstract class can share instance state/construction.
13. **Overloading vs overriding?** Overload changes parameters at compile time; override replaces inherited runtime behavior.
14. **Can static methods override?** No; same signature hides the parent static method.
15. **`final` meanings?** Variable assigned once, method not overridable, class not extendable.
16. **Immutability?** State cannot change after construction; use final fields, validation and defensive copies.
17. **`String` immutable benefits?** Safe sharing, pooling, stable hashing and security-sensitive use.
18. **`StringBuilder` vs `StringBuffer`?** Builder is unsynchronized/faster locally; Buffer synchronizes operations.
19. **`==` vs `equals`?** `==` compares primitive values/reference identity; `equals` can compare logical value.
20. **`equals`/`hashCode` contract?** Equal objects must have equal hash codes; override both for value keys.
21. **Why `BigDecimal` for money?** Exact decimal representation and explicit scale/rounding.
22. **Checked vs unchecked exception?** Checked must be declared/handled; runtime exceptions usually signal programming/validation faults.
23. **`throw` vs `throws`?** `throw` raises one object; `throws` declares possible method outcomes.
24. **`finally` purpose?** Cleanup path after try/catch; prefer try-with-resources for closeable resources.
25. **Try-with-resources?** Automatically closes `AutoCloseable` resources, including suppressed exception handling.
26. **Custom exception?** Domain-specific type adding meaning/context; do not create one without benefit.
27. **List, Set, Map?** Ordered/duplicate sequence; unique values; key-value associations.
28. **ArrayList vs LinkedList?** ArrayList excels random access/cache locality; LinkedList rarely wins outside known-node operations.
29. **HashSet internals?** Hash-based buckets using equality/hashCode; no iteration-order guarantee.
30. **HashMap basics?** Hash narrows bucket, equality finds key; supports one null key and is not thread-safe.
31. **LinkedHashMap?** Preserves insertion/access order; useful for predictable iteration/LRU building.
32. **TreeMap?** Sorted keys via natural/comparator order, generally O(log n).
33. **Comparable vs Comparator?** Type's natural order vs external/multiple order strategies.
34. **Fail-fast iterator?** Best-effort concurrent modification detection, not a synchronization guarantee.
35. **Generics purpose?** Compile-time type safety and reusable algorithms without most casts.
36. **Type erasure?** Most generic type parameters are removed at runtime for compatibility.
37. **`? extends T` vs `? super T`?** Producer extends, consumer super (PECS).
38. **Lambda?** Concise implementation of a functional interface.
39. **Functional interface?** Exactly one abstract method; default/static methods allowed.
40. **Stream?** Lazy declarative pipeline over data; it does not store data.
41. **`map` vs `flatMap`?** One-to-one transformation vs flatten nested results.
42. **Intermediate vs terminal stream operation?** Intermediate builds lazy pipeline; terminal triggers processing.
43. **Why avoid side effects in streams?** They harm clarity, determinism and parallel safety.
44. **Optional purpose?** Explicit possibly-absent return; not a universal field/parameter replacement.
45. **Method reference?** Lambda shorthand such as `String::trim` when signatures match.
46. **Record?** Concise immutable data carrier with generated accessors/equality/hash/string.
47. **Enum benefit?** Type-safe finite values that may own fields and behavior.
48. **Access modifiers?** private, package-private, protected, public control visibility.
49. **Package purpose?** Namespace and package-level access boundary.
50. **Static member?** Belongs to class, not an instance; shared mutable static state needs care.
51. **Thread vs process?** Threads share process memory; processes have separate address spaces.
52. **Race condition?** Result depends on uncontrolled interleaving of shared mutable operations.
53. **`synchronized`?** Mutual exclusion plus happens-before visibility around the same monitor.
54. **`volatile`?** Visibility/order for a variable, not atomicity of compound actions.
55. **AtomicLong?** Lock-free atomic operations such as unique counters.
56. **ThreadLocal?** Per-thread value; remove pooled-thread values when finished.
57. **ExecutorService?** Manages reusable worker threads and task submission/shutdown.
58. **Future vs CompletableFuture?** Future represents eventual result; CompletableFuture composes async stages.
59. **Deadlock?** Threads cyclically wait for locks; prevent with fixed lock order/timeouts/less locking.
60. **Garbage collection?** Reclaims unreachable objects; it does not guarantee timely external-resource cleanup.
61. **Strong/weak reference?** Strong prevents collection; weak can be collected when only weakly reachable.
62. **Java memory model?** Defines visibility, ordering and happens-before rules between threads.
63. **Defensive copy?** Copy mutable input/output so callers cannot mutate internal state.
64. **Dependency injection?** Supply collaborators from outside, improving substitution and testability.
65. **SOLID?** Five design principles guiding cohesive, extensible and substitutable object design.
66. **DRY caution?** Remove true duplicated knowledge, not coincidentally similar code that changes for different reasons.
67. **Composition over inheritance?** Delegate capabilities to avoid rigid hierarchy and fragile base classes.
68. **How Java concepts appear in ShopFlow?** Interfaces (`WebDriver`), inheritance (`BasePage`), encapsulation (locators), polymorphism (Chrome/Edge), generics (collections), ThreadLocal and records/models.
69. **Why release 17 on JDK 26?** `--release 17` constrains language/API bytecode compatibility while a newer JDK runs Maven.
70. **Good exception message?** State operation, offending context and expected contract without leaking secrets.

## Project-focused follow-ups

```java
WebDriver driver = DriverFactory.createDriver(browser, headless);
```

This demonstrates interface-oriented polymorphism: the declared type is `WebDriver`; runtime object is ChromeDriver or EdgeDriver. `switch` centralizes supported implementations.

```java
try { if (DRIVER.get() != null) DRIVER.get().quit(); }
finally { DRIVER.remove(); }
```

`finally` guarantees reference cleanup even if `quit()` fails. A common mistake is treating garbage collection as browser cleanup; the driver process is an external resource.

```java
BigDecimal total = subtotal.add(tax);
```

`BigDecimal` is immutable, so `add` returns a new value. A common mistake is calling `subtotal.add(tax)` and ignoring the return value.
