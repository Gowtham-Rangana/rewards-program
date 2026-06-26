-- Sample Customers
INSERT INTO customers (id, customer_name, email) VALUES
    (1, 'Gowtham Kishore', 'gowtham.kishore@email.com');

INSERT INTO customers (id, customer_name, email) VALUES
    (2, 'Kishore', 'kishore@email.com');

INSERT INTO customers (id, customer_name, email) VALUES
    (3, 'Carol Williams', 'carol.williams@email.com');


-- Transactions for Alice Johnson (Customer ID: 1)

-- Month 1 (3 months ago)
INSERT INTO transactions (customer_id, amount, transaction_date, description)
VALUES (1, 120.00, DATEADD('MONTH', -3, CURRENT_DATE) + 2, 'Electronics purchase');

INSERT INTO transactions (customer_id, amount, transaction_date, description)
VALUES (1, 75.50, DATEADD('MONTH', -3, CURRENT_DATE) + 8, 'Clothing purchase');

INSERT INTO transactions (customer_id, amount, transaction_date, description)
VALUES (1, 45.00, DATEADD('MONTH', -3, CURRENT_DATE) + 15, 'Grocery purchase');

-- Month 2 (2 months ago)
INSERT INTO transactions (customer_id, amount, transaction_date, description)
VALUES (1, 200.00, DATEADD('MONTH', -2, CURRENT_DATE) + 3, 'Furniture purchase');

INSERT INTO transactions (customer_id, amount, transaction_date, description)
VALUES (1, 55.00, DATEADD('MONTH', -2, CURRENT_DATE) + 12, 'Book purchase');

INSERT INTO transactions (customer_id, amount, transaction_date, description)
VALUES (1, 150.00, DATEADD('MONTH', -2, CURRENT_DATE) + 20, 'Appliance purchase');

-- Month 3 (1 month ago)
INSERT INTO transactions (customer_id, amount, transaction_date, description)
VALUES (1, 90.00, DATEADD('MONTH', -1, CURRENT_DATE) + 5, 'Sporting goods');

INSERT INTO transactions (customer_id, amount, transaction_date, description)
VALUES (1, 30.00, DATEADD('MONTH', -1, CURRENT_DATE) + 10, 'Pharmacy purchase');

INSERT INTO transactions (customer_id, amount, transaction_date, description)
VALUES (1, 110.00, DATEADD('MONTH', -1, CURRENT_DATE) + 18, 'Home improvement');


-- Transactions for Bob Martinez (Customer ID: 2)

-- Month 1 (3 months ago)
INSERT INTO transactions (customer_id, amount, transaction_date, description)
VALUES (2, 250.00, DATEADD('MONTH', -3, CURRENT_DATE) + 1, 'TV purchase');

INSERT INTO transactions (customer_id, amount, transaction_date, description)
VALUES (2, 40.00, DATEADD('MONTH', -3, CURRENT_DATE) + 14, 'Snack purchase');

-- Month 2 (2 months ago)
INSERT INTO transactions (customer_id, amount, transaction_date, description)
VALUES (2, 100.00, DATEADD('MONTH', -2, CURRENT_DATE) + 7, 'Clothing haul');

INSERT INTO transactions (customer_id, amount, transaction_date, description)
VALUES (2, 180.00, DATEADD('MONTH', -2, CURRENT_DATE) + 22, 'Laptop accessories');

-- Month 3 (1 month ago)
INSERT INTO transactions (customer_id, amount, transaction_date, description)
VALUES (2, 65.00, DATEADD('MONTH', -1, CURRENT_DATE) + 3, 'Office supplies');

INSERT INTO transactions (customer_id, amount, transaction_date, description)
VALUES (2, 320.00, DATEADD('MONTH', -1, CURRENT_DATE) + 15, 'Gaming console');


-- Transactions for Carol Williams (Customer ID: 3)

-- Month 1 (3 months ago)
INSERT INTO transactions (customer_id, amount, transaction_date, description)
VALUES (3, 50.00, DATEADD('MONTH', -3, CURRENT_DATE) + 4, 'Beauty products');

INSERT INTO transactions (customer_id, amount, transaction_date, description)
VALUES (3, 85.00, DATEADD('MONTH', -3, CURRENT_DATE) + 19, 'Pet supplies');

-- Month 2 (2 months ago)
INSERT INTO transactions (customer_id, amount, transaction_date, description)
VALUES (3, 130.00, DATEADD('MONTH', -2, CURRENT_DATE) + 6, 'Garden equipment');

INSERT INTO transactions (customer_id, amount, transaction_date, description)
VALUES (3, 25.00, DATEADD('MONTH', -2, CURRENT_DATE) + 11, 'Stationery');

INSERT INTO transactions (customer_id, amount, transaction_date, description)
VALUES (3, 95.00, DATEADD('MONTH', -2, CURRENT_DATE) + 25, 'Kitchen appliance');

-- Month 3 (1 month ago)
INSERT INTO transactions (customer_id, amount, transaction_date, description)
VALUES (3, 175.00, DATEADD('MONTH', -1, CURRENT_DATE) + 2, 'Smart home devices');

INSERT INTO transactions (customer_id, amount, transaction_date, description)
VALUES (3, 60.00, DATEADD('MONTH', -1, CURRENT_DATE) + 16, 'Fitness equipment');