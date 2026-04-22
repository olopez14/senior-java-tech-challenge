CREATE TABLE products (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(1000) NOT NULL
);

CREATE TABLE prices (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  product_id BIGINT NOT NULL,
  price_value DECIMAL(19,4) NOT NULL,
  init_date DATE NOT NULL,
  end_date DATE NULL,
  CONSTRAINT fk_prices_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE INDEX idx_prices_product ON prices(product_id);
CREATE INDEX idx_prices_product_init ON prices(product_id, init_date);
CREATE INDEX idx_prices_product_end ON prices(product_id, end_date);

