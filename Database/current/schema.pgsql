CREATE TABLE customers (
  customer_id UUID PRIMARY KEY,
  customer_address VARCHAR(255) NOT NULL,
  customer_city VARCHAR(255) NOT NULL,
  customer_country VARCHAR(255) NOT NULL,
  customer_name VARCHAR(255) NOT NULL,
  customer_postal_code INT NOT NULL
);

CREATE TABLE products (
  product_id UUID PRIMARY KEY,
  product_description VARCHAR(255) NOT NULL,
  product_name VARCHAR(255) NOT NULL,
  price bigint NOT NULL CHECK (price > 0::bigint),
  units INT NOT NULL CHECK (units > 0)
);

CREATE TABLE users (
  user_id UUID PRIMARY KEY,
  username VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(255) NOT NULL,
  role_id UUID
);

CREATE TABLE sessions (
  session_id VARCHAR(255) NOT NULL,
  login VARCHAR(255) NOT NULL,
);

CREATE TABLE orders (
  order_id UUID PRIMARY KEY,
  delivery_address VARCHAR(255) NOT NULL,
  delivery_city VARCHAR(255) NOT NULL,
  delivery_country VARCHAR(255) NOT NULL,
  delivery_postal_code VARCHAR(255) NOT NULL,
  quantity INT NOT NULL CHECK (quantity > 0),
  order_status VARCHAR(255) NOT NULL,
  customer_id UUID references customers(customer_id),
  product_id INT references products(product_id)
);