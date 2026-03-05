-- ============================================================
-- RESTORAN - Testni podaci
-- ============================================================

USE `restoran`;

SET FOREIGN_KEY_CHECKS = 0;

-- Uloge
INSERT INTO `role` (id, name) VALUES
  (1, 'Manager'),
  (2, 'Konobar'),
  (3, 'Kuvar'),
  (4, 'Blagajnik');

-- Zaposleni
INSERT INTO `employee` (id, name, email, phone, salary, Role_id) VALUES
  (1, 'Ana Marković',  'ana@restoran.ba',    '065111222', 2500, 1),
  (2, 'Marko Nikolić', 'marko@restoran.ba',  '065333444', 1500, 2),
  (3, 'Jelena Kovač',  'jelena@restoran.ba', '065555666', 1500, 2),
  (4, 'Petar Simić',   'petar@restoran.ba',  '065777888', 1600, 3),
  (5, 'Sara Đukić',    'sara@restoran.ba',   '065999000', 1400, 4);

-- Korisnički nalozi (lozinka je MD5 hash, ovdje plaintext radi testiranja)
INSERT INTO `user_account` (id, username, password, Employee_id, active) VALUES
  (1, 'admin',  'admin123',  1, 1),
  (2, 'marko',  'marko123',  2, 1),
  (3, 'jelena', 'jelena123', 3, 1);

-- Dobavljači
INSERT INTO `supplier` (id, name, email, phone, address) VALUES
  (1, 'Agrokor d.o.o.',      'agrokor@mail.ba',   '051100200', 'Banja Luka, Cara Lazara 5'),
  (2, 'Pivara BL d.o.o.',    'pivara@mail.ba',     '051200300', 'Banja Luka, Kralja Petra 12'),
  (3, 'EuroBakery d.o.o.',   'bakery@mail.ba',     '051300400', 'Banja Luka, Bulevar 22');

-- Namirnice
INSERT INTO `ingredient` (id, name, unit, stock_qty, Supplier_id) VALUES
  (1, 'Brašno',          'kg',  50.0, 1),
  (2, 'Jaja',            'kom', 120,  1),
  (3, 'Mlijeko',         'l',   30.0, 1),
  (4, 'Šećer',           'kg',  20.0, 1),
  (5, 'Sol',             'kg',  10.0, 1),
  (6, 'Maslinovo ulje',  'l',   15.0, 1),
  (7, 'Piletina',        'kg',  25.0, 1),
  (8, 'Govedina',        'kg',  20.0, 1),
  (9, 'Svinjetina',      'kg',  18.0, 1),
  (10,'Paradajz',        'kg',  15.0, 1),
  (11,'Luk',             'kg',  10.0, 1),
  (12,'Sir',             'kg',  12.0, 1),
  (13,'Pivo (limenka)',  'kom', 200,  2),
  (14,'Vino (boca)',     'kom', 50,   2),
  (15,'Hljeb',           'kom', 80,   3);

-- Kategorije menija
INSERT INTO `category` (id, name) VALUES
  (1, 'Predjela'),
  (2, 'Glavna jela'),
  (3, 'Deserti'),
  (4, 'Pića'),
  (5, 'Salate');

-- Stavke menija
INSERT INTO `item` (id, name, price, on_menu, description, Category_id) VALUES
  (1,  'Čorba od povrća',      3.50,  1, 'Domaća čorba sa svježim povrćem',         1),
  (2,  'Bruschetta',            4.00,  1, 'Tost sa paradajzom i bosiljkom',           1),
  (3,  'Pileći file na žaru',  12.00,  1, 'Pileći file sa prilogom',                 2),
  (4,  'Biftek',               18.00,  1, 'Goveđi biftek medium sa patatom',         2),
  (5,  'Svinjski kotlet',      10.00,  1, 'Svinjski kotlet sa povrćem',              2),
  (6,  'Pašta Bolognese',       9.00,  1, 'Tagliatelle sa goveđim ragúom',           2),
  (7,  'Palačinke',             4.50,  1, 'Sa slatkim filom po izboru',              3),
  (8,  'Čokoladni fondant',     5.00,  1, 'Topli čokoladni kolač sa sladoledom',     3),
  (9,  'Pivo (0.5l)',           2.50,  1, 'Domaće točeno pivo',                      4),
  (10, 'Vino (0.2l)',           3.00,  1, 'Crveno ili bijelo po izboru',             4),
  (11, 'Sok (0.33l)',           1.80,  1, 'Razne vrste sokova',                      4),
  (12, 'Voda (0.5l)',           1.00,  1, 'Negazirana voda',                         4),
  (13, 'Cezar salata',          6.00,  1, 'Romaine, pileće meso, cezar sos',         5),
  (14, 'Šopska salata',         4.50,  1, 'Paradajz, krastavac, sir',                5),
  (15, 'Sezonska salata',       3.50,  0, 'Sezonsko povrće (van sezone)',            5);

-- Veze stavki i namirnica
INSERT INTO `itemhasingredient` (Item_id, Ingredient_id, quantity) VALUES
  (3, 7,  1),  -- Piletina - piletina
  (4, 8,  1),  -- Biftek - govedina
  (5, 9,  1),  -- Kotlet - svinjetina
  (6, 8,  1),  -- Bolognese - govedina
  (6, 10, 1),  -- Bolognese - paradajz
  (7, 1,  1),  -- Palačinke - brašno
  (7, 2,  2),  -- Palačinke - jaja
  (7, 3,  1),  -- Palačinke - mlijeko
  (8, 4,  1),  -- Fondant - šećer
  (13,7,  1),  -- Cezar - piletina
  (14,10, 1),  -- Šopska - paradajz
  (14,12, 1);  -- Šopska - sir

-- Stolovi
INSERT INTO `_table` (id, capacity, location, status) VALUES
  (1, 2,  'Terasa',  'slobodan'),
  (2, 2,  'Terasa',  'slobodan'),
  (3, 4,  'Sala',    'slobodan'),
  (4, 4,  'Sala',    'slobodan'),
  (5, 4,  'Sala',    'slobodan'),
  (6, 6,  'Sala',    'slobodan'),
  (7, 6,  'Sala',    'slobodan'),
  (8, 8,  'VIP soba','slobodan'),
  (9, 10, 'VIP soba','slobodan');

-- Kupci
INSERT INTO `customer` (id, name, email, phone) VALUES
  (1, 'Jovan Petrović',  'jovan@mail.ba',  '066111111'),
  (2, 'Milica Savić',    'milica@mail.ba', '066222222'),
  (3, 'Dragan Ilić',     'dragan@mail.ba', '066333333');

-- Rezervacije
INSERT INTO `reservation` (id, date, time, duration, note, status, _Table_id, Customer_id) VALUES
  (1, CURDATE(), '19:00:00', 90, 'Godišnjica — cvjece na stol', 'aktivna', 8, 1),
  (2, CURDATE(), '20:00:00', 60, NULL,                           'aktivna', 3, 2),
  (3, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '12:30:00', 60, NULL, 'aktivna', 4, 3);

-- Popusti
INSERT INTO `discount` (id, code, percentage) VALUES
  (1, 'NEMA',     0.00),
  (2, '10POSTO', 10.00),
  (3, 'POLA',    50.00),
  (4, 'VIP20',   20.00);

-- Tipovi plaćanja
INSERT INTO `paymenttype` (id, name) VALUES
  (1, 'Gotovina'),
  (2, 'Kartica'),
  (3, 'Virman');

-- Smjene
INSERT INTO `shift` (date, start_time, end_time, Employee_id) VALUES
  (CURDATE(), '08:00:00', '16:00:00', 1),
  (CURDATE(), '10:00:00', '18:00:00', 2),
  (CURDATE(), '16:00:00', '00:00:00', 3);

-- Plaćanja (za testne narudžbe)
INSERT INTO `payment` (id, amount, timestamp, PaymentType_id) VALUES
  (1, 0.00,  NOW(6), 1),
  (2, 34.50, NOW(6), 2);

-- Testne narudžbe
INSERT INTO `order` (id, status, timestamp, note, Employee_id, Customer_id, Discount_id, _Table_id, Payment_id) VALUES
  (1, 'otvoren', NOW(6), NULL,          2, 1, 1, 3, 1),
  (2, 'plaćeno', NOW(6), 'Brza usluga', 3, 2, 2, 5, 2);

-- Stavke testnih narudžbi
INSERT INTO `ordereditem` (quantity, price, Item_id, Order_id) VALUES
  (2, 24.00, 3, 1),  -- 2x pileći file
  (2,  9.00, 9, 1),  -- 2x pivo
  (1, 18.00, 4, 2),  -- 1x biftek
  (1,  4.50, 14, 2); -- 1x šopska

SET FOREIGN_KEY_CHECKS = 1;
