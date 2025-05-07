-- Roller tablosunu temizle ve yeniden doldur
DELETE FROM roles;

INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

-- İlk olarak eski kullanıcıları ve ilişkili verileri temizleyelim
DELETE FROM customer_roles;
DELETE FROM refresh_tokens;
DELETE FROM transactions;
DELETE FROM accounts;
DELETE FROM customers;

-- Admin kullanıcısı ekle (şifre: admin123)
INSERT INTO customers (first_name, last_name, email, password, phone_number, address, creation_date, last_modified_date, enabled)
VALUES ('Admin', 'Kullanıcı', 'admin@bankapp.com', '$2a$10$DdCw3qIE9RcIHE7H.JtPnOAczwrpOLqQvdHxPiXPaJ3F26Wlh44Oq', '555-123-4567', 'İstanbul, Kadıköy', NOW(), NOW(), true);

-- Örnek kullanıcı ekle (şifre: user123)
INSERT INTO customers (first_name, last_name, email, password, phone_number, address, creation_date, last_modified_date, enabled)
VALUES ('Test', 'Kullanıcı', 'user@bankapp.com', '$2a$10$M3D54qXE5Aj7ULhOQS8AQuBsNJGtvCYQpiwnSJzlCk4x/L3lw9Zjm', '555-987-6543', 'Ankara, Çankaya', NOW(), NOW(), true);

-- Ek kullanıcılar ekleyelim
INSERT INTO customers (first_name, last_name, email, password, phone_number, address, creation_date, last_modified_date, enabled)
VALUES ('Ali', 'Yılmaz', 'ali@example.com', '$2a$10$M3D54qXE5Aj7ULhOQS8AQuBsNJGtvCYQpiwnSJzlCk4x/L3lw9Zjm', '555-111-2222', 'İzmir, Karşıyaka', NOW(), NOW(), true);

INSERT INTO customers (first_name, last_name, email, password, phone_number, address, creation_date, last_modified_date, enabled)
VALUES ('Ayşe', 'Demir', 'ayse@example.com', '$2a$10$M3D54qXE5Aj7ULhOQS8AQuBsNJGtvCYQpiwnSJzlCk4x/L3lw9Zjm', '555-333-4444', 'Bursa, Nilüfer', NOW(), NOW(), true);

INSERT INTO customers (first_name, last_name, email, password, phone_number, address, creation_date, last_modified_date, enabled)
VALUES ('Mehmet', 'Kaya', 'mehmet@example.com', '$2a$10$M3D54qXE5Aj7ULhOQS8AQuBsNJGtvCYQpiwnSJzlCk4x/L3lw9Zjm', '555-555-6666', 'Antalya, Muratpaşa', NOW(), NOW(), true);

-- Kullanıcılara rol atamaları
-- Admin kullanıcısına ADMIN rolü ata
INSERT INTO customer_roles (customer_id, role_id)
VALUES (1, 2);

-- Admin kullanıcısına USER rolü de ata
INSERT INTO customer_roles (customer_id, role_id)
VALUES (1, 1);

-- Diğer kullanıcılara USER rolü ata
INSERT INTO customer_roles (customer_id, role_id)
VALUES (2, 1);

INSERT INTO customer_roles (customer_id, role_id)
VALUES (3, 1);

INSERT INTO customer_roles (customer_id, role_id)
VALUES (4, 1);

INSERT INTO customer_roles (customer_id, role_id)
VALUES (5, 1);

-- Hesaplar oluştur
-- Admin kullanıcısının hesapları
INSERT INTO accounts (account_number, account_type, balance, currency, created_at, updated_at, customer_id)
VALUES ('1001001', 'CHECKING', 28540.50, 'TL', NOW(), NOW(), 1);

INSERT INTO accounts (account_number, account_type, balance, currency, created_at, updated_at, customer_id)
VALUES ('1001002', 'SAVINGS', 14210.35, 'TL', NOW(), NOW(), 1);

INSERT INTO accounts (account_number, account_type, balance, currency, created_at, updated_at, customer_id)
VALUES ('1001003', 'CREDIT', 5000.00, 'USD', NOW(), NOW(), 1);

-- Test kullanıcısının hesapları
INSERT INTO accounts (account_number, account_type, balance, currency, created_at, updated_at, customer_id)
VALUES ('2001001', 'CHECKING', 5000.00, 'TL', NOW(), NOW(), 2);

INSERT INTO accounts (account_number, account_type, balance, currency, created_at, updated_at, customer_id)
VALUES ('2001002', 'SAVINGS', 12000.00, 'TL', NOW(), NOW(), 2);

-- Ali Yılmaz'ın hesapları
INSERT INTO accounts (account_number, account_type, balance, currency, created_at, updated_at, customer_id)
VALUES ('3001001', 'CHECKING', 7500.25, 'TL', NOW(), NOW(), 3);

INSERT INTO accounts (account_number, account_type, balance, currency, created_at, updated_at, customer_id)
VALUES ('3001002', 'SAVINGS', 35000.00, 'TL', NOW(), NOW(), 3);

-- Ayşe Demir'in hesapları
INSERT INTO accounts (account_number, account_type, balance, currency, created_at, updated_at, customer_id)
VALUES ('4001001', 'CHECKING', 3200.75, 'TL', NOW(), NOW(), 4);

-- Mehmet Kaya'nın hesapları
INSERT INTO accounts (account_number, account_type, balance, currency, created_at, updated_at, customer_id)
VALUES ('5001001', 'CHECKING', 9800.50, 'TL', NOW(), NOW(), 5);

INSERT INTO accounts (account_number, account_type, balance, currency, created_at, updated_at, customer_id)
VALUES ('5001002', 'SAVINGS', 45000.00, 'TL', NOW(), NOW(), 5);

INSERT INTO accounts (account_number, account_type, balance, currency, created_at, updated_at, customer_id)
VALUES ('5001003', 'CREDIT', 10000.00, 'EUR', NOW(), NOW(), 5);

-- İşlemler oluştur
-- Admin kullanıcısının işlemleri
INSERT INTO transactions (amount, description, transaction_date, type, source_account_id, target_account_id)
VALUES (245.50, 'Market Alışverişi', NOW() - INTERVAL '5 DAY', 'WITHDRAWAL', 1, NULL);

INSERT INTO transactions (amount, description, transaction_date, type, source_account_id, target_account_id)
VALUES (12500.00, 'Maaş Ödemesi', NOW() - INTERVAL '10 DAY', 'DEPOSIT', NULL, 1);

INSERT INTO transactions (amount, description, transaction_date, type, source_account_id, target_account_id)
VALUES (320.75, 'Elektrik Faturası', NOW() - INTERVAL '7 DAY', 'WITHDRAWAL', 1, NULL);

INSERT INTO transactions (amount, description, transaction_date, type, source_account_id, target_account_id)
VALUES (3500.00, 'Kira Ödemesi', NOW() - INTERVAL '9 DAY', 'WITHDRAWAL', 1, NULL);

INSERT INTO transactions (amount, description, transaction_date, type, source_account_id, target_account_id)
VALUES (890.25, 'Online Alışveriş', NOW() - INTERVAL '3 DAY', 'WITHDRAWAL', 1, NULL);

INSERT INTO transactions (amount, description, transaction_date, type, source_account_id, target_account_id)
VALUES (5000.00, 'Tasarruf Transferi', NOW() - INTERVAL '15 DAY', 'TRANSFER', 1, 2);

-- Test kullanıcısının işlemleri
INSERT INTO transactions (amount, description, transaction_date, type, source_account_id, target_account_id)
VALUES (1000.00, 'Arkadaştan Ödünç', NOW() - INTERVAL '8 DAY', 'DEPOSIT', NULL, 4);

INSERT INTO transactions (amount, description, transaction_date, type, source_account_id, target_account_id)
VALUES (750.50, 'Alışveriş', NOW() - INTERVAL '6 DAY', 'WITHDRAWAL', 4, NULL);

INSERT INTO transactions (amount, description, transaction_date, type, source_account_id, target_account_id)
VALUES (2500.00, 'Tasarruf Aktarımı', NOW() - INTERVAL '12 DAY', 'TRANSFER', 4, 5);

-- Ali Yılmaz'ın işlemleri  
INSERT INTO transactions (amount, description, transaction_date, type, source_account_id, target_account_id)
VALUES (8500.00, 'Maaş Yatırma', NOW() - INTERVAL '11 DAY', 'DEPOSIT', NULL, 6);

INSERT INTO transactions (amount, description, transaction_date, type, source_account_id, target_account_id)
VALUES (1200.00, 'Kira Ödemesi', NOW() - INTERVAL '5 DAY', 'WITHDRAWAL', 6, NULL);

INSERT INTO transactions (amount, description, transaction_date, type, source_account_id, target_account_id)
VALUES (10000.00, 'Birikim Aktarma', NOW() - INTERVAL '3 DAY', 'TRANSFER', 6, 7);

-- Ayşe Demir'in işlemleri
INSERT INTO transactions (amount, description, transaction_date, type, source_account_id, target_account_id)
VALUES (3500.00, 'Maaş Yatırma', NOW() - INTERVAL '9 DAY', 'DEPOSIT', NULL, 8);

INSERT INTO transactions (amount, description, transaction_date, type, source_account_id, target_account_id)
VALUES (950.75, 'Market Alışverişi', NOW() - INTERVAL '4 DAY', 'WITHDRAWAL', 8, NULL);

-- Mehmet Kaya'nın işlemleri
INSERT INTO transactions (amount, description, transaction_date, type, source_account_id, target_account_id)
VALUES (12000.00, 'Maaş Yatırma', NOW() - INTERVAL '14 DAY', 'DEPOSIT', NULL, 9);

INSERT INTO transactions (amount, description, transaction_date, type, source_account_id, target_account_id)
VALUES (2500.00, 'Kira Ödemesi', NOW() - INTERVAL '7 DAY', 'WITHDRAWAL', 9, NULL);

INSERT INTO transactions (amount, description, transaction_date, type, source_account_id, target_account_id)
VALUES (15000.00, 'Tasarruf Aktarımı', NOW() - INTERVAL '5 DAY', 'TRANSFER', 9, 10);

INSERT INTO transactions (amount, description, transaction_date, type, source_account_id, target_account_id)
VALUES (5000.00, 'Euro Transferi', NOW() - INTERVAL '2 DAY', 'TRANSFER', 9, 11); 