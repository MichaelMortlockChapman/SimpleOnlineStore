CREATE TABLE products (
  product_id UUID PRIMARY KEY,
  product_name VARCHAR(255) UNIQUE NOT NULL,
  product_description TEXT,
  units INT NOT NULL CHECK (units > 0),
  price bigint NOT NULL CHECK (price > 0::bigint)
);

CREATE TABLE customers (
  customer_id UUID PRIMARY KEY,
  customer_name VARCHAR(255) NOT NULL,
  customer_email VARCHAR(255) UNIQUE NOT NULL,
  customer_address VARCHAR(255) NOT NULL,
  customer_city VARCHAR(255) NOT NULL,
  customer_postal_code INT NOT NULL,
  customer_country VARCHAR(255) NOT NULL
);

CREATE TABLE orders (
  order_id UUID PRIMARY KEY,
  product_id UUID REFERENCES products,
  customer_id UUID REFERENCES customers,
  quantity INTEGER NOT NULL CHECK (quantity > 0),
  delivery_address VARCHAR(255) NOT NULL,
  delivery_city VARCHAR(255) NOT NULL,
  delivery_postal_code INT NOT NULL,
  delivery_country VARCHAR(255) NOT NULL,
  order_status TEXT
);
