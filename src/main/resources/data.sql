INSERT INTO customers (name, mobile_number, address, notes, created_at)
VALUES ('Ravi Kumar', '7287924611', 'Shop Road, Sector 7', 'Loyal customer, prefers roses', NOW());

INSERT INTO bills (customer_id, total_amount, paid_amount, due_amount, bill_date, status, bill_image_url, notes, created_at)
VALUES (1, 2000.00, 500.00, 1500.00, '2026-05-10', 'PARTIAL', NULL, 'Mother\'s Day bouquet', NOW());

INSERT INTO payments (bill_id, customer_id, amount_paid, payment_date, payment_mode, notes, created_at)
VALUES (1, 1, 500.00, '2026-05-10', 'Cash', 'First installment', NOW());
