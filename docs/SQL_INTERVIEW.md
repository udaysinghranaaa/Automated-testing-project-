# SQL Interview Practice — PostgreSQL

Use this disposable schema only in a local practice database. Queries are read-only unless explicitly labeled setup.

```sql
CREATE TABLE customers(customer_id bigint PRIMARY KEY, name text NOT NULL, email text UNIQUE, created_at timestamptz NOT NULL);
CREATE TABLE products(product_id bigint PRIMARY KEY, name text NOT NULL, category text NOT NULL, price numeric(12,2) NOT NULL CHECK(price>=0));
CREATE TABLE orders(order_id bigint PRIMARY KEY, customer_id bigint REFERENCES customers, status text NOT NULL, ordered_at timestamptz NOT NULL);
CREATE TABLE order_items(order_id bigint REFERENCES orders, product_id bigint REFERENCES products, quantity integer NOT NULL CHECK(quantity>0), unit_price numeric(12,2) NOT NULL, PRIMARY KEY(order_id,product_id));
CREATE TABLE payments(payment_id bigint PRIMARY KEY, order_id bigint REFERENCES orders, amount numeric(12,2) NOT NULL, status text NOT NULL, paid_at timestamptz);
CREATE TABLE test_runs(run_id bigint PRIMARY KEY, suite text NOT NULL, browser text NOT NULL, started_at timestamptz NOT NULL, finished_at timestamptz);
CREATE TABLE test_results(result_id bigint PRIMARY KEY, run_id bigint REFERENCES test_runs, test_name text NOT NULL, status text NOT NULL, duration_ms integer, error_type text);
```

## 40 practical queries

1. **All active catalog rows:** `SELECT product_id,name,price FROM products ORDER BY name;`
2. **Distinct categories:** `SELECT DISTINCT category FROM products ORDER BY category;`
3. **Products in price band:** `SELECT * FROM products WHERE price BETWEEN 10.00 AND 30.00 ORDER BY price;`
4. **Case-insensitive name search:** `SELECT * FROM products WHERE name ILIKE '%backpack%';`
5. **Recent orders:** `SELECT * FROM orders WHERE ordered_at >= CURRENT_TIMESTAMP - INTERVAL '30 days';`
6. **Status counts:** `SELECT status,COUNT(*) FROM orders GROUP BY status ORDER BY status;`
7. **Catalog aggregates:** `SELECT category,COUNT(*) item_count,MIN(price),MAX(price),ROUND(AVG(price),2) avg_price FROM products GROUP BY category;`
8. **Categories with 3+ products:** `SELECT category,COUNT(*) FROM products GROUP BY category HAVING COUNT(*)>=3;`
9. **Order with customer:** `SELECT o.order_id,c.name,o.status,o.ordered_at FROM orders o JOIN customers c ON c.customer_id=o.customer_id;`
10. **Customers including zero orders:** `SELECT c.customer_id,c.name,COUNT(o.order_id) orders FROM customers c LEFT JOIN orders o ON o.customer_id=c.customer_id GROUP BY c.customer_id,c.name;`
11. **Customers with no order:** `SELECT c.* FROM customers c WHERE NOT EXISTS(SELECT 1 FROM orders o WHERE o.customer_id=c.customer_id);`
12. **Order subtotal:** `SELECT order_id,SUM(quantity*unit_price) subtotal FROM order_items GROUP BY order_id;`
13. **Detailed order totals:** `SELECT o.order_id,c.name,SUM(oi.quantity*oi.unit_price) total FROM orders o JOIN customers c USING(customer_id) JOIN order_items oi USING(order_id) GROUP BY o.order_id,c.name;`
14. **Orders above 100:** `SELECT order_id,SUM(quantity*unit_price) total FROM order_items GROUP BY order_id HAVING SUM(quantity*unit_price)>100;`
15. **Top-selling products by units:** `SELECT p.product_id,p.name,SUM(oi.quantity) units FROM products p JOIN order_items oi USING(product_id) GROUP BY p.product_id,p.name ORDER BY units DESC,p.product_id LIMIT 5;`
16. **Top products by revenue:** `SELECT p.name,SUM(oi.quantity*oi.unit_price) revenue FROM products p JOIN order_items oi USING(product_id) GROUP BY p.product_id,p.name ORDER BY revenue DESC LIMIT 5;`
17. **Unsold products:** `SELECT p.* FROM products p WHERE NOT EXISTS(SELECT 1 FROM order_items oi WHERE oi.product_id=p.product_id);`
18. **Latest order per customer:** `SELECT * FROM (SELECT o.*,ROW_NUMBER() OVER(PARTITION BY customer_id ORDER BY ordered_at DESC,order_id DESC) rn FROM orders o) x WHERE rn=1;`
19. **Second-highest product price:** `SELECT DISTINCT price FROM products ORDER BY price DESC OFFSET 1 LIMIT 1;`
20. **Products at second-highest price:** `SELECT * FROM products WHERE price=(SELECT DISTINCT price FROM products ORDER BY price DESC OFFSET 1 LIMIT 1);`
21. **Rank product price by category:** `SELECT name,category,price,DENSE_RANK() OVER(PARTITION BY category ORDER BY price DESC) price_rank FROM products;`
22. **Running customer spend:** `SELECT o.customer_id,o.ordered_at,SUM(oi.quantity*oi.unit_price) order_total,SUM(SUM(oi.quantity*oi.unit_price)) OVER(PARTITION BY o.customer_id ORDER BY o.ordered_at,o.order_id) running_total FROM orders o JOIN order_items oi USING(order_id) GROUP BY o.customer_id,o.order_id,o.ordered_at;`
23. **Monthly revenue:** `SELECT date_trunc('month',o.ordered_at) month,SUM(oi.quantity*oi.unit_price) revenue FROM orders o JOIN order_items oi USING(order_id) WHERE o.status='COMPLETED' GROUP BY 1 ORDER BY 1;`
24. **Month-over-month revenue:** `WITH m AS (SELECT date_trunc('month',o.ordered_at) month,SUM(oi.quantity*oi.unit_price) revenue FROM orders o JOIN order_items oi USING(order_id) GROUP BY 1) SELECT month,revenue,LAG(revenue) OVER(ORDER BY month) previous,revenue-LAG(revenue) OVER(ORDER BY month) change FROM m;`
25. **Duplicate normalized emails:** `SELECT lower(trim(email)) normalized,COUNT(*) FROM customers GROUP BY lower(trim(email)) HAVING COUNT(*)>1;`
26. **Null-safe paid label:** `SELECT payment_id,COALESCE(paid_at::text,'not paid') paid FROM payments;`
27. **Payment mismatch:** `WITH totals AS (SELECT order_id,SUM(quantity*unit_price) total FROM order_items GROUP BY order_id), paid AS (SELECT order_id,SUM(amount) FILTER(WHERE status='SUCCESS') amount FROM payments GROUP BY order_id) SELECT t.order_id,t.total,COALESCE(p.amount,0) paid FROM totals t LEFT JOIN paid p USING(order_id) WHERE t.total<>COALESCE(p.amount,0);`
28. **Orders with failed payment only:** `SELECT o.order_id FROM orders o WHERE EXISTS(SELECT 1 FROM payments p WHERE p.order_id=o.order_id AND p.status='FAILED') AND NOT EXISTS(SELECT 1 FROM payments p WHERE p.order_id=o.order_id AND p.status='SUCCESS');`
29. **Customers spending above average customer spend:** `WITH s AS (SELECT o.customer_id,SUM(oi.quantity*oi.unit_price) spend FROM orders o JOIN order_items oi USING(order_id) GROUP BY o.customer_id) SELECT * FROM s WHERE spend>(SELECT AVG(spend) FROM s);`
30. **Safe percentage of successful tests:** `SELECT run_id,ROUND(100.0*COUNT(*) FILTER(WHERE status='PASS')/NULLIF(COUNT(*),0),2) pass_pct FROM test_results GROUP BY run_id;`
31. **Latest result per test/browser:** `SELECT * FROM (SELECT r.browser,t.*,ROW_NUMBER() OVER(PARTITION BY r.browser,t.test_name ORDER BY r.started_at DESC,r.run_id DESC) rn FROM test_runs r JOIN test_results t USING(run_id)) x WHERE rn=1;`
32. **Flaky tests (both pass and fail):** `SELECT test_name FROM test_results GROUP BY test_name HAVING COUNT(DISTINCT status) FILTER(WHERE status IN('PASS','FAIL'))=2;`
33. **Slowest tests:** `SELECT test_name,ROUND(AVG(duration_ms),1) avg_ms,MAX(duration_ms) max_ms FROM test_results WHERE duration_ms IS NOT NULL GROUP BY test_name ORDER BY avg_ms DESC LIMIT 10;`
34. **Failure type trend:** `SELECT date_trunc('day',r.started_at) day,t.error_type,COUNT(*) FROM test_runs r JOIN test_results t USING(run_id) WHERE t.status='FAIL' GROUP BY 1,2 ORDER BY 1,2;`
35. **Runs missing results:** `SELECT r.* FROM test_runs r LEFT JOIN test_results t USING(run_id) WHERE t.run_id IS NULL;`
36. **Suite duration:** `SELECT run_id,EXTRACT(EPOCH FROM(finished_at-started_at))*1000 duration_ms FROM test_runs WHERE finished_at IS NOT NULL;`
37. **Median test duration:** `SELECT test_name,percentile_cont(0.5) WITHIN GROUP(ORDER BY duration_ms) median_ms FROM test_results WHERE duration_ms IS NOT NULL GROUP BY test_name;`
38. **Consecutive failed runs:** `SELECT test_name,run_id,status,LAG(status) OVER(PARTITION BY test_name ORDER BY run_id) previous FROM test_results;` Filter this result where both statuses are `FAIL`.
39. **Parameterized browser counts:** `SELECT browser,status,COUNT(*) FROM test_runs JOIN test_results USING(run_id) GROUP BY browser,status ORDER BY browser,status;`
40. **Pagination with stable order:** `SELECT product_id,name,price FROM products ORDER BY name,product_id LIMIT 20 OFFSET 40;` For large tables prefer keyset: `WHERE (name,product_id)>(:last_name,:last_id)`.

## Interview edge cases

- `COUNT(column)` ignores null; `COUNT(*)` counts rows.
- `NOT IN` with a null can produce unknown; prefer `NOT EXISTS` for anti-joins.
- Put a right-table filter in `ON` if unmatched left rows must remain in a LEFT JOIN.
- `WHERE` filters rows before grouping; `HAVING` filters groups.
- Window functions retain detail rows; `GROUP BY` collapses them.
- Always add deterministic tie-breakers to ranking/pagination.
- Use `numeric`, not floating-point, for contractual money.
- Index foreign keys and frequent filter/join keys after measuring plans; indexes speed reads but cost writes/storage.
- Transactions provide atomicity; isolation level controls which concurrent anomalies are allowed.
- Practice data only: do not run destructive DDL/DML against a real shared database.

