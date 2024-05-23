-- product sales info
SELECT product_name, price AS product_price, SUM (quantity) AS order_count, SUM (quantity * products.price) AS order_gross_profit
FROM orders
INNER JOIN products ON products.product_id = orders.product_id
GROUP BY product_name, price
ORDER BY order_gross_profit DESC;

-- num of orders per customer
SELECT customer_name, COUNT(order_id) as num_orders
FROM customers
INNER JOIN orders ON customers.customer_id = orders.customer_id
GROUP BY customer_name
ORDER BY num_orders DESC;

-- random customer stats/info
SELECT COUNT (customer_id)
FROM customers;
SELECT DISTINCT customer_country AS unique_customer_countries
FROM customers;
SELECT DISTINCT customer_city AS unique_customer_cities
FROM customers;
SELECT DISTINCT customer_postal_code AS unique_customer_postal_codes
FROM customers;

-- num of customers using @example.com
SELECT COUNT (customer_email)
FROM customers
WHERE customer_email LIKE '%@example.com';