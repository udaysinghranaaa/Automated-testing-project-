# Java Coding Practice — 35 Solved Exercises

All snippets are method-level solutions for Java 17. Add standard `java.util` imports as needed.

## 1. Reverse a string

**Problem:** Reverse Unicode code units. **Approach:** `StringBuilder`.  
**Solution:** `static String reverse(String s){ return new StringBuilder(Objects.requireNonNull(s)).reverse().toString(); }`  
**Complexity:** O(n) time, O(n) space. **Sample:** `reverse("test") -> "tset"`. **Follow-up:** handle grapheme clusters.

## 2. Palindrome ignoring punctuation

**Problem:** Detect a phrase palindrome. **Approach:** two pointers skipping non-alphanumeric.

```java
static boolean palindrome(String s) {
    int i=0,j=s.length()-1;
    while(i<j){ while(i<j&&!Character.isLetterOrDigit(s.charAt(i)))i++;
        while(i<j&&!Character.isLetterOrDigit(s.charAt(j)))j--;
        if(Character.toLowerCase(s.charAt(i++))!=Character.toLowerCase(s.charAt(j--)))return false; }
    return true;
}
```

**Complexity:** O(n)/O(1). **Sample:** `"A man, a plan, a canal: Panama" -> true`. **Follow-up:** full Unicode code points.

## 3. Character frequency

**Problem:** Count each character. **Approach:** insertion-ordered map.  
**Solution:** `static Map<Character,Long> freq(String s){ return s.chars().mapToObj(c->(char)c).collect(Collectors.groupingBy(Function.identity(),LinkedHashMap::new,Collectors.counting())); }`  
**Complexity:** O(n)/O(k). **Sample:** `"aba" -> {a=2,b=1}`. **Follow-up:** code points.

## 4. First non-repeating character

**Problem:** Return the first unique char. **Approach:** count then scan.

```java
static Optional<Character> firstUnique(String s){
    Map<Character,Integer> m=new LinkedHashMap<>();
    for(char c:s.toCharArray())m.merge(c,1,Integer::sum);
    return m.entrySet().stream().filter(e->e.getValue()==1).map(Map.Entry::getKey).findFirst();
}
```

**Complexity:** O(n)/O(k). **Sample:** `"swiss" -> w`. **Follow-up:** return index.

## 5. Anagram

**Problem:** Compare normalized anagrams. **Approach:** sort lowercase letters.  
**Solution:** `static boolean anagram(String a,String b){ char[] x=a.replaceAll("\\s","").toLowerCase().toCharArray(),y=b.replaceAll("\\s","").toLowerCase().toCharArray(); Arrays.sort(x);Arrays.sort(y);return Arrays.equals(x,y); }`  
**Complexity:** O(n log n). **Sample:** `listen/silent -> true`. **Follow-up:** O(n) frequency map.

## 6. Remove duplicate list values

**Problem:** Preserve first occurrence. **Approach:** `LinkedHashSet`.  
**Solution:** `static <T> List<T> distinct(List<T> in){ return new ArrayList<>(new LinkedHashSet<>(in)); }`  
**Complexity:** O(n) average/O(n). **Sample:** `[2,1,2] -> [2,1]`. **Follow-up:** stream solution.

## 7. Find duplicate values

**Problem:** Return unique duplicates. **Approach:** seen and duplicate sets.

```java
static <T> Set<T> duplicates(List<T> values){
    Set<T> seen=new HashSet<>(), dup=new LinkedHashSet<>();
    for(T v:values)if(!seen.add(v))dup.add(v); return dup;
}
```

**Complexity:** O(n) average/O(n). **Sample:** `[1,2,1,1] -> [1]`. **Follow-up:** counts.

## 8. Maximum without sorting

**Problem:** Find max or reject empty input. **Approach:** single pass.  
**Solution:** `static int max(int[] a){ if(a.length==0)throw new IllegalArgumentException();int m=a[0];for(int v:a)m=Math.max(m,v);return m; }`  
**Complexity:** O(n)/O(1). **Sample:** `[3,-1,8] -> 8`. **Follow-up:** generic Comparator.

## 9. Second distinct maximum

**Problem:** Find second highest distinct integer. **Approach:** track two values.

```java
static int secondMax(int[] a){
    Integer first=null,second=null;
    for(int v:a){if(Objects.equals(first,v)||Objects.equals(second,v))continue;
        if(first==null||v>first){second=first;first=v;}else if(second==null||v>second)second=v;}
    if(second==null)throw new IllegalArgumentException("Need two distinct values"); return second;
}
```

**Complexity:** O(n)/O(1). **Sample:** `[5,1,5,4] -> 4`. **Follow-up:** kth distinct max.

## 10. Missing number 0..n

**Problem:** One value is missing. **Approach:** XOR indices and values.  
**Solution:** `static int missing(int[] a){int x=a.length;for(int i=0;i<a.length;i++)x^=i^a[i];return x;}`  
**Complexity:** O(n)/O(1). **Sample:** `[3,0,1] -> 2`. **Follow-up:** validate duplicates/range.

## 11. Two sum indices

**Problem:** Find indices totaling target. **Approach:** complement map.

```java
static int[] twoSum(int[] a,int target){Map<Integer,Integer> m=new HashMap<>();
 for(int i=0;i<a.length;i++){Integer j=m.get(target-a[i]);if(j!=null)return new int[]{j,i};m.put(a[i],i);}
 throw new IllegalArgumentException("No pair");}
```

**Complexity:** O(n)/O(n). **Sample:** `[2,7,11],9 -> [0,1]`. **Follow-up:** all pairs.

## 12. Move zeros to end

**Problem:** Stable in-place compaction. **Approach:** write pointer.

```java
static void moveZeros(int[] a){int w=0;for(int v:a)if(v!=0)a[w++]=v;while(w<a.length)a[w++]=0;}
```

**Complexity:** O(n)/O(1). **Sample:** `[0,1,0,3] -> [1,3,0,0]`. **Follow-up:** generic predicate.

## 13. Merge sorted arrays

**Problem:** Merge two sorted arrays. **Approach:** two pointers.

```java
static int[] merge(int[] a,int[] b){int[] r=new int[a.length+b.length];int i=0,j=0,k=0;
 while(i<a.length&&j<b.length)r[k++]=a[i]<=b[j]?a[i++]:b[j++];
 while(i<a.length)r[k++]=a[i++];while(j<b.length)r[k++]=b[j++];return r;}
```

**Complexity:** O(n+m)/O(n+m). **Sample:** `[1,3],[2,4] -> [1,2,3,4]`. **Follow-up:** merge in available tail space.

## 14. Binary search

**Problem:** Index in sorted array. **Approach:** halve range.

```java
static int binary(int[] a,int key){int l=0,r=a.length-1;while(l<=r){int m=l+(r-l)/2;
 if(a[m]==key)return m;if(a[m]<key)l=m+1;else r=m-1;}return -1;}
```

**Complexity:** O(log n)/O(1). **Sample:** `[1,4,9],4 -> 1`. **Follow-up:** first duplicate position.

## 15. Fibonacci iterative

**Problem:** Return nth number. **Approach:** rolling pair.  
**Solution:** `static long fib(int n){if(n<0)throw new IllegalArgumentException();long a=0,b=1;for(int i=0;i<n;i++){long t=Math.addExact(a,b);a=b;b=t;}return a;}`  
**Complexity:** O(n)/O(1). **Sample:** `fib(10)->55`. **Follow-up:** overflow/BigInteger.

## 16. Factorial

**Problem:** Exact non-negative factorial. **Approach:** `BigInteger` loop.  
**Solution:** `static BigInteger factorial(int n){if(n<0)throw new IllegalArgumentException();BigInteger r=BigInteger.ONE;for(int i=2;i<=n;i++)r=r.multiply(BigInteger.valueOf(i));return r;}`  
**Complexity:** O(n) multiplications. **Sample:** `5 -> 120`. **Follow-up:** recursion trade-off.

## 17. Prime check

**Problem:** Detect prime. **Approach:** trial division through sqrt.  
**Solution:** `static boolean prime(int n){if(n<2)return false;if(n%2==0)return n==2;for(int i=3;i<=n/i;i+=2)if(n%i==0)return false;return true;}`  
**Complexity:** O(sqrt n)/O(1). **Sample:** `29 -> true`. **Follow-up:** sieve for many queries.

## 18. GCD

**Problem:** Greatest common divisor. **Approach:** Euclid.  
**Solution:** `static int gcd(int a,int b){a=Math.abs(a);b=Math.abs(b);while(b!=0){int t=a%b;a=b;b=t;}return a;}`  
**Complexity:** O(log min(a,b))/O(1). **Sample:** `54,24 -> 6`. **Follow-up:** LCM overflow.

## 19. Balanced brackets

**Problem:** Validate `()[]{}`. **Approach:** stack.

```java
static boolean balanced(String s){Map<Character,Character> close=Map.of(')', '(',']','[','}','{');Deque<Character>d=new ArrayDeque<>();
 for(char c:s.toCharArray()){if(close.containsValue(c))d.push(c);else if(close.containsKey(c)&&(d.isEmpty()||d.pop()!=close.get(c)))return false;}return d.isEmpty();}
```

**Complexity:** O(n)/O(n). **Sample:** `"([])" -> true`. **Follow-up:** reject other chars.

## 20. Word frequency

**Problem:** Count normalized words. **Approach:** regex split and map merge.

```java
static Map<String,Integer> words(String s){Map<String,Integer> r=new LinkedHashMap<>();
 for(String w:s.toLowerCase().split("[^a-z0-9]+"))if(!w.isBlank())r.merge(w,1,Integer::sum);return r;}
```

**Complexity:** O(n)/O(k). **Sample:** `"Test test" -> {test=2}`. **Follow-up:** locale/Unicode tokenizer.

## 21. Group anagrams

**Problem:** Group words by letters. **Approach:** sorted-character key.

```java
static List<List<String>> group(List<String> xs){Map<String,List<String>> m=new LinkedHashMap<>();
 for(String s:xs){char[] c=s.toCharArray();Arrays.sort(c);m.computeIfAbsent(new String(c),k->new ArrayList<>()).add(s);}return new ArrayList<>(m.values());}
```

**Complexity:** O(n·k log k). **Sample:** `[eat,tea,bat] -> [[eat,tea],[bat]]`. **Follow-up:** frequency key.

## 22. Intersection preserving first list order

**Problem:** Unique common items. **Approach:** membership plus emitted set.  
**Solution:** `static <T> List<T> intersection(List<T>a,List<T>b){Set<T> right=new HashSet<>(b),out=new LinkedHashSet<>();for(T v:a)if(right.contains(v))out.add(v);return new ArrayList<>(out);}`  
**Complexity:** O(n+m)/O(m). **Sample:** `[3,1,3],[1,3] -> [3,1]`. **Follow-up:** multiset intersection.

## 23. Sort products by price then name

**Problem:** Deterministic ordering. **Approach:** comparator chain.  
**Solution:** `static List<Product> sort(List<Product>x){return x.stream().sorted(Comparator.comparing(Product::price).thenComparing(Product::name)).toList();}`  
**Complexity:** O(n log n)/O(n). **Sample:** equal prices alphabetize. **Follow-up:** null policy.

## 24. Parse dollar price

**Problem:** Validate `$12.50`. **Approach:** anchored pattern then decimal.

```java
static BigDecimal price(String s){Matcher m=Pattern.compile("^\\$?(\\d+(?:\\.\\d{1,2})?)$").matcher(s.trim());
 if(!m.matches())throw new IllegalArgumentException("Invalid price: "+s);return new BigDecimal(m.group(1));}
```

**Complexity:** O(n)/O(n). **Sample:** `$9.99 -> 9.99`. **Follow-up:** currency/locale service.

## 25. Cart subtotal

**Problem:** Sum immutable decimals. **Approach:** reduce from zero.  
**Solution:** `static BigDecimal subtotal(List<BigDecimal> p){return p.stream().map(Objects::requireNonNull).reduce(BigDecimal.ZERO,BigDecimal::add);}`  
**Complexity:** O(n)/O(1) excluding results. **Sample:** `10.00+5.50 -> 15.50`. **Follow-up:** quantities.

## 26. Partition pass/fail

**Problem:** Split scores at 60. **Approach:** `partitioningBy`.  
**Solution:** `static Map<Boolean,List<Integer>> partition(List<Integer>s){return s.stream().collect(Collectors.partitioningBy(x->x>=60));}`  
**Complexity:** O(n)/O(n). **Sample:** `[70,40] -> true:[70], false:[40]`. **Follow-up:** grade bands.

## 27. Top three frequent values

**Problem:** Rank by count descending. **Approach:** count then sort entries.  
**Solution:** `static List<String> top3(List<String>x){return x.stream().collect(Collectors.groupingBy(Function.identity(),Collectors.counting())).entrySet().stream().sorted(Map.Entry.<String,Long>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey())).limit(3).map(Map.Entry::getKey).toList();}`  
**Complexity:** O(n+k log k). **Sample:** deterministic ties. **Follow-up:** heap O(k log 3).

## 28. Longest common prefix

**Problem:** Common prefix of all strings. **Approach:** shrink prefix.

```java
static String prefix(List<String>x){if(x.isEmpty())return "";String p=x.get(0);
 for(String s:x)while(!s.startsWith(p)){if(p.isEmpty())return "";p=p.substring(0,p.length()-1);}return p;}
```

**Complexity:** O(total characters) typical/O(1) extra. **Sample:** `[flower,flow] -> flow`. **Follow-up:** trie.

## 29. Run-length encoding

**Problem:** Encode consecutive chars. **Approach:** scan runs.

```java
static String rle(String s){if(s.isEmpty())return s;StringBuilder b=new StringBuilder();int count=1;
 for(int i=1;i<=s.length();i++){if(i<s.length()&&s.charAt(i)==s.charAt(i-1))count++;else{b.append(s.charAt(i-1)).append(count);count=1;}}return b.toString();}
```

**Complexity:** O(n)/O(n). **Sample:** `aaabb -> a3b2`. **Follow-up:** decode and malformed input.

## 30. Rotate array right

**Problem:** Rotate k positions in place. **Approach:** three reversals.

```java
static void rotate(int[]a,int k){if(a.length==0)return;k=Math.floorMod(k,a.length);rev(a,0,a.length-1);rev(a,0,k-1);rev(a,k,a.length-1);}
static void rev(int[]a,int l,int r){while(l<r){int t=a[l];a[l++]=a[r];a[r--]=t;}}
```

**Complexity:** O(n)/O(1). **Sample:** `[1,2,3],1 -> [3,1,2]`. **Follow-up:** left rotation.

## 31. Merge overlapping intervals

**Problem:** Merge `[start,end]`. **Approach:** sort then extend.

```java
static List<int[]> intervals(List<int[]>x){x=x.stream().map(int[]::clone).sorted(Comparator.comparingInt(a->a[0])).toList();List<int[]>r=new ArrayList<>();
 for(int[]v:x){if(r.isEmpty()||r.get(r.size()-1)[1]<v[0])r.add(v);else r.get(r.size()-1)[1]=Math.max(r.get(r.size()-1)[1],v[1]);}return r;}
```

**Complexity:** O(n log n)/O(n). **Sample:** `[1,3],[2,4] -> [1,4]`. **Follow-up:** touching intervals policy.

## 32. LRU cache

**Problem:** Bounded least-recently-used map. **Approach:** access-ordered LinkedHashMap.

```java
final class Lru<K,V> extends LinkedHashMap<K,V>{private final int capacity;
 Lru(int c){super(c,0.75f,true);capacity=c;}@Override protected boolean removeEldestEntry(Map.Entry<K,V>e){return size()>capacity;}}
```

**Complexity:** O(1) average get/put, O(capacity) space. **Sample:** capacity 2 evicts least accessed. **Follow-up:** thread safety.

## 33. Retry-free polling helper

**Problem:** Poll a value until predicate/time limit (not rerun a failed test). **Approach:** monotonic deadline.

```java
static <T>T await(Supplier<T>s,Predicate<T>ok,Duration timeout){long end=System.nanoTime()+timeout.toNanos();T v;
 do{v=s.get();if(ok.test(v))return v;Thread.onSpinWait();}while(System.nanoTime()<end);throw new IllegalStateException("Condition timed out");}
```

**Complexity:** O(polls)/O(1). **Sample:** eventual state. **Follow-up:** scheduled backoff/interruption; Selenium uses WebDriverWait instead.

## 34. Thread-safe unique IDs

**Problem:** Generate unique increasing invocation IDs. **Approach:** AtomicLong.  
**Solution:** `private static final AtomicLong SEQ=new AtomicLong(); static String id(String name){return "%05d-%s".formatted(SEQ.incrementAndGet(),name);}`  
**Complexity:** O(1)/O(1). **Sample:** `00001-login`. **Follow-up:** uniqueness across processes.

## 35. Immutable checkout data

**Problem:** Validated data carrier. **Approach:** record compact constructor.

```java
record Checkout(String first,String last,String postal){Checkout{
 first=require("first",first);last=require("last",last);postal=require("postal",postal);}
 private static String require(String n,String v){if(v==null||v.isBlank())throw new IllegalArgumentException(n+" required");return v.trim();}}
```

**Complexity:** O(length of fields)/O(1). **Sample:** blanks rejected. **Follow-up:** whether trimming is product policy.
