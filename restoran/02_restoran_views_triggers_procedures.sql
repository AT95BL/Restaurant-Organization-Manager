-- ============================================================
-- RESTORAN - Pogledi, Trigeri i Uskladištene Procedure
-- ============================================================

USE `restoran`;

-- ============================================================
-- POGLEDI (VIEWS)
-- ============================================================

-- Svi zaposleni sa nazivom uloge
DROP VIEW IF EXISTS `allemployees`;
CREATE VIEW `allemployees` AS
  SELECT e.id, e.name, e.email, e.phone, e.salary, r.name AS role_name
  FROM employee e
  JOIN role r ON e.Role_id = r.id;

-- Svi aktivni korisnici (za login)
DROP VIEW IF EXISTS `allaccounts`;
CREATE VIEW `allaccounts` AS
  SELECT ua.id, ua.username, ua.password, ua.active,
         e.name AS employee_name, r.name AS role_name
  FROM user_account ua
  JOIN employee e ON ua.Employee_id = e.id
  JOIN role r ON e.Role_id = r.id
  WHERE ua.active = 1;

-- Sve kategorije
DROP VIEW IF EXISTS `allcategories`;
CREATE VIEW `allcategories` AS
  SELECT id, name FROM category;

-- Sve stavke menija (sa kategorijom)
DROP VIEW IF EXISTS `allitems`;
CREATE VIEW `allitems` AS
  SELECT i.id, i.name AS item_name, c.id AS category_id,
         c.name AS category_name, i.price, i.on_menu,
         i.description, i.picture
  FROM item i
  JOIN category c ON i.Category_id = c.id;

-- Samo stavke koje su na meniju
DROP VIEW IF EXISTS `allmenuitems`;
CREATE VIEW `allmenuitems` AS
  SELECT i.id, i.name AS item_name, c.id AS category_id,
         c.name AS category_name, i.price, i.description, i.picture
  FROM item i
  JOIN category c ON i.Category_id = c.id
  WHERE i.on_menu = 1;

-- Sve narudžbe sa detaljima
DROP VIEW IF EXISTS `allorders`;
CREATE VIEW `allorders` AS
  SELECT o.id, o.status, o.timestamp, o.note,
         e.name  AS employee_name,
         COALESCE(cu.name, 'Gost') AS customer_name,
         d.percentage AS discount_pct,
         p.amount AS paid_amount,
         pt.name AS payment_type,
         t.id AS table_id
  FROM `order` o
  JOIN employee e    ON o.Employee_id = e.id
  LEFT JOIN customer cu ON o.Customer_id = cu.id
  LEFT JOIN discount d  ON o.Discount_id = d.id
  JOIN payment p     ON o.Payment_id  = p.id
  JOIN paymenttype pt ON p.PaymentType_id = pt.id
  LEFT JOIN _table t ON o._Table_id = t.id;

-- Sve rezervacije sa detaljima
DROP VIEW IF EXISTS `allreservations`;
CREATE VIEW `allreservations` AS
  SELECT r.id, r.date, r.time, r.duration, r.note, r.status,
         t.id AS table_id, t.capacity, t.location,
         c.id AS customer_id, c.name AS customer_name,
         c.phone AS customer_phone
  FROM reservation r
  JOIN _table t   ON r._Table_id   = t.id
  JOIN customer c ON r.Customer_id = c.id;

-- Slobodni stolovi (nisu u aktivnoj rezervaciji danas)
DROP VIEW IF EXISTS `freetables`;
CREATE VIEW `freetables` AS
  SELECT t.id, t.capacity, t.location, t.status
  FROM _table t
  WHERE t.status = 'slobodan';

-- Tipovi plaćanja
DROP VIEW IF EXISTS `allpaymenttypes`;
CREATE VIEW `allpaymenttypes` AS
  SELECT id, name FROM paymenttype;

-- Svi dobavljači
DROP VIEW IF EXISTS `allsuppliers`;
CREATE VIEW `allsuppliers` AS
  SELECT id, name, email, phone, address FROM supplier;

-- Sve namirnice sa dobavljačem
DROP VIEW IF EXISTS `allingredients`;
CREATE VIEW `allingredients` AS
  SELECT i.id, i.name, i.unit, i.stock_qty,
         COALESCE(s.name, 'Bez dobavljača') AS supplier_name
  FROM ingredient i
  LEFT JOIN supplier s ON i.Supplier_id = s.id;


-- ============================================================
-- TRIGERI (TRIGGERS)
-- ============================================================

-- Triger: kada se kreira narudžba, postavi stol na 'zauzet'
DROP TRIGGER IF EXISTS `trg_order_set_table_occupied`;
DELIMITER ;;
CREATE TRIGGER `trg_order_set_table_occupied`
  AFTER INSERT ON `order`
  FOR EACH ROW
BEGIN
  IF NEW._Table_id IS NOT NULL THEN
    UPDATE _table SET status = 'zauzet' WHERE id = NEW._Table_id;
  END IF;
END;;
DELIMITER ;

-- Triger: kada se narudžba zatvori (status='plaćeno'), oslobodi stol
DROP TRIGGER IF EXISTS `trg_order_free_table`;
DELIMITER ;;
CREATE TRIGGER `trg_order_free_table`
  AFTER UPDATE ON `order`
  FOR EACH ROW
BEGIN
  IF NEW.status = 'plaćeno' AND OLD.status != 'plaćeno' THEN
    IF NEW._Table_id IS NOT NULL THEN
      UPDATE _table SET status = 'slobodan' WHERE id = NEW._Table_id;
    END IF;
  END IF;
END;;
DELIMITER ;

-- Triger: kada se kreira rezervacija, postavi stol na 'rezervisan'
DROP TRIGGER IF EXISTS `trg_reservation_set_table_reserved`;
DELIMITER ;;
CREATE TRIGGER `trg_reservation_set_table_reserved`
  AFTER INSERT ON `reservation`
  FOR EACH ROW
BEGIN
  IF NEW.status = 'aktivna' THEN
    UPDATE _table SET status = 'rezervisan' WHERE id = NEW._Table_id;
  END IF;
END;;
DELIMITER ;

-- Triger: pri otkazivanju rezervacije, oslobodi stol
DROP TRIGGER IF EXISTS `trg_reservation_cancel_free_table`;
DELIMITER ;;
CREATE TRIGGER `trg_reservation_cancel_free_table`
  AFTER UPDATE ON `reservation`
  FOR EACH ROW
BEGIN
  IF NEW.status = 'otkazana' AND OLD.status = 'aktivna' THEN
    UPDATE _table SET status = 'slobodan' WHERE id = NEW._Table_id;
  END IF;
END;;
DELIMITER ;

-- Triger: pri nabavci namirnica, povećaj stock_qty
DROP TRIGGER IF EXISTS `trg_purchase_update_stock`;
DELIMITER ;;
CREATE TRIGGER `trg_purchase_update_stock`
  AFTER INSERT ON `purchaseitemingredient`
  FOR EACH ROW
BEGIN
  IF NEW.Ingredient_id IS NOT NULL AND NEW.ingredient_quantity > 0 THEN
    UPDATE ingredient
    SET stock_qty = stock_qty + NEW.ingredient_quantity
    WHERE id = NEW.Ingredient_id;
  END IF;
END;;
DELIMITER ;

-- Triger: pri kreiranju narudžbene stavke, smanji stock_qty namirnica
DROP TRIGGER IF EXISTS `trg_ordereditem_decrease_stock`;
DELIMITER ;;
CREATE TRIGGER `trg_ordereditem_decrease_stock`
  AFTER INSERT ON `ordereditem`
  FOR EACH ROW
BEGIN
  UPDATE ingredient ing
  JOIN itemhasingredient ihi ON ihi.Ingredient_id = ing.id
  SET ing.stock_qty = ing.stock_qty - (ihi.quantity * NEW.quantity)
  WHERE ihi.Item_id = NEW.Item_id;
END;;
DELIMITER ;


-- ============================================================
-- USKLADIŠTENE PROCEDURE (STORED PROCEDURES)
-- ============================================================

-- --- AUTENTIFIKACIJA ---

DROP PROCEDURE IF EXISTS `login_user`;
DELIMITER ;;
CREATE PROCEDURE `login_user`(
  IN p_username VARCHAR(45),
  IN p_password VARCHAR(255)
)
BEGIN
  SELECT ua.id, ua.username, e.name AS employee_name,
         e.id AS employee_id, r.name AS role_name
  FROM user_account ua
  JOIN employee e ON ua.Employee_id = e.id
  JOIN role r     ON e.Role_id      = r.id
  WHERE ua.username = p_username
    AND ua.password = p_password
    AND ua.active   = 1;
END;;
DELIMITER ;

-- --- ZAPOSLENI ---

DROP PROCEDURE IF EXISTS `get_all_employees`;
DELIMITER ;;
CREATE PROCEDURE `get_all_employees`()
BEGIN
  SELECT * FROM allemployees ORDER BY name;
END;;
DELIMITER ;

DROP PROCEDURE IF EXISTS `add_employee`;
DELIMITER ;;
CREATE PROCEDURE `add_employee`(
  IN p_name   VARCHAR(45),
  IN p_email  VARCHAR(45),
  IN p_phone  VARCHAR(45),
  IN p_salary DOUBLE,
  IN p_role   INT
)
BEGIN
  INSERT INTO employee(name, email, phone, salary, Role_id)
  VALUES (p_name, p_email, p_phone, p_salary, p_role);
  SELECT LAST_INSERT_ID() AS new_id;
END;;
DELIMITER ;

DROP PROCEDURE IF EXISTS `update_employee`;
DELIMITER ;;
CREATE PROCEDURE `update_employee`(
  IN p_id     INT,
  IN p_name   VARCHAR(45),
  IN p_email  VARCHAR(45),
  IN p_phone  VARCHAR(45),
  IN p_salary DOUBLE,
  IN p_role   INT
)
BEGIN
  UPDATE employee
  SET name=p_name, email=p_email, phone=p_phone,
      salary=p_salary, Role_id=p_role
  WHERE id = p_id;
END;;
DELIMITER ;

DROP PROCEDURE IF EXISTS `delete_employee`;
DELIMITER ;;
CREATE PROCEDURE `delete_employee`(IN p_id INT)
BEGIN
  DELETE FROM employee WHERE id = p_id;
END;;
DELIMITER ;

-- --- KATEGORIJE ---

DROP PROCEDURE IF EXISTS `add_category`;
DELIMITER ;;
CREATE PROCEDURE `add_category`(IN p_name VARCHAR(45))
BEGIN
  INSERT INTO category(name) VALUES (p_name);
END;;
DELIMITER ;

DROP PROCEDURE IF EXISTS `delete_category`;
DELIMITER ;;
CREATE PROCEDURE `delete_category`(IN p_id INT)
BEGIN
  DELETE FROM category WHERE id = p_id;
END;;
DELIMITER ;

-- --- STAVKE MENIJA ---

DROP PROCEDURE IF EXISTS `get_all_items`;
DELIMITER ;;
CREATE PROCEDURE `get_all_items`()
BEGIN
  SELECT * FROM allitems ORDER BY category_name, item_name;
END;;
DELIMITER ;

DROP PROCEDURE IF EXISTS `add_item`;
DELIMITER ;;
CREATE PROCEDURE `add_item`(
  IN p_name     VARCHAR(45),
  IN p_price    DECIMAL(8,2),
  IN p_on_menu  TINYINT,
  IN p_desc     VARCHAR(200),
  IN p_cat_id   INT,
  IN p_picture  VARCHAR(500)
)
BEGIN
  INSERT INTO item(name, price, on_menu, description, Category_id, picture)
  VALUES (p_name, p_price, p_on_menu, p_desc, p_cat_id, p_picture);
  SELECT LAST_INSERT_ID() AS new_id;
END;;
DELIMITER ;

DROP PROCEDURE IF EXISTS `update_item`;
DELIMITER ;;
CREATE PROCEDURE `update_item`(
  IN p_id      INT,
  IN p_name    VARCHAR(45),
  IN p_price   DECIMAL(8,2),
  IN p_on_menu TINYINT,
  IN p_desc    VARCHAR(200),
  IN p_cat_id  INT,
  IN p_picture VARCHAR(500)
)
BEGIN
  UPDATE item
  SET name=p_name, price=p_price, on_menu=p_on_menu,
      description=p_desc, Category_id=p_cat_id, picture=p_picture
  WHERE id = p_id;
END;;
DELIMITER ;

DROP PROCEDURE IF EXISTS `delete_item`;
DELIMITER ;;
CREATE PROCEDURE `delete_item`(IN p_id INT)
BEGIN
  DELETE FROM item WHERE id = p_id;
END;;
DELIMITER ;

-- --- NARUDŽBE ---

DROP PROCEDURE IF EXISTS `get_all_orders`;
DELIMITER ;;
CREATE PROCEDURE `get_all_orders`()
BEGIN
  SELECT * FROM allorders ORDER BY timestamp DESC;
END;;
DELIMITER ;

DROP PROCEDURE IF EXISTS `get_open_orders`;
DELIMITER ;;
CREATE PROCEDURE `get_open_orders`()
BEGIN
  SELECT * FROM allorders WHERE status != 'plaćeno' ORDER BY timestamp DESC;
END;;
DELIMITER ;

DROP PROCEDURE IF EXISTS `create_order`;
DELIMITER ;;
CREATE PROCEDURE `create_order`(
  IN p_employee_id  INT,
  IN p_customer_id  INT,
  IN p_discount_id  INT,
  IN p_table_id     INT,
  IN p_paytype_id   INT,
  IN p_note         VARCHAR(200),
  OUT p_order_id    INT
)
BEGIN
  DECLARE v_pay_id INT;

  INSERT INTO payment(amount, timestamp, PaymentType_id)
  VALUES (0.00, NOW(6), p_paytype_id);
  SET v_pay_id = LAST_INSERT_ID();

  INSERT INTO `order`(status, timestamp, note,
                       Employee_id, Customer_id, Discount_id,
                       _Table_id, Payment_id)
  VALUES ('otvoren', NOW(6), p_note,
          p_employee_id, p_customer_id, p_discount_id,
          p_table_id, v_pay_id);

  SET p_order_id = LAST_INSERT_ID();
END;;
DELIMITER ;

DROP PROCEDURE IF EXISTS `add_item_to_order`;
DELIMITER ;;
CREATE PROCEDURE `add_item_to_order`(
  IN p_order_id INT,
  IN p_item_id  INT,
  IN p_qty      INT
)
BEGIN
  DECLARE v_price DECIMAL(8,2);
  SELECT price INTO v_price FROM item WHERE id = p_item_id;

  INSERT INTO ordereditem(quantity, price, Item_id, Order_id)
  VALUES (p_qty, v_price * p_qty, p_item_id, p_order_id);
END;;
DELIMITER ;

DROP PROCEDURE IF EXISTS `close_order`;
DELIMITER ;;
CREATE PROCEDURE `close_order`(IN p_order_id INT)
BEGIN
  DECLARE v_total   DECIMAL(10,2);
  DECLARE v_disc    DECIMAL(5,2);
  DECLARE v_pay_id  INT;
  DECLARE v_final   DECIMAL(10,2);

  -- ukupan iznos narudžbe
  SELECT COALESCE(SUM(oi.price), 0)
  INTO v_total
  FROM ordereditem oi
  WHERE oi.Order_id = p_order_id;

  -- popust (ako postoji)
  SELECT COALESCE(d.percentage, 0), o.Payment_id
  INTO v_disc, v_pay_id
  FROM `order` o
  LEFT JOIN discount d ON o.Discount_id = d.id
  WHERE o.id = p_order_id;

  SET v_final = v_total * (1 - v_disc / 100);

  -- ažuriraj plaćanje
  UPDATE payment SET amount = v_final WHERE id = v_pay_id;

  -- zatvori narudžbu
  UPDATE `order` SET status = 'plaćeno' WHERE id = p_order_id;
END;;
DELIMITER ;

DROP PROCEDURE IF EXISTS `ordered_items_by_order_id`;
DELIMITER ;;
CREATE PROCEDURE `ordered_items_by_order_id`(IN p_order_id INT)
BEGIN
  SELECT oi.id, oi.quantity, oi.price, i.name AS item_name
  FROM ordereditem oi
  JOIN item i ON oi.Item_id = i.id
  WHERE oi.Order_id = p_order_id;
END;;
DELIMITER ;

-- --- REZERVACIJE ---

DROP PROCEDURE IF EXISTS `get_all_reservations`;
DELIMITER ;;
CREATE PROCEDURE `get_all_reservations`()
BEGIN
  SELECT * FROM allreservations ORDER BY date DESC, time DESC;
END;;
DELIMITER ;

DROP PROCEDURE IF EXISTS `add_reservation`;
DELIMITER ;;
CREATE PROCEDURE `add_reservation`(
  IN p_date        DATE,
  IN p_time        TIME,
  IN p_duration    INT,
  IN p_note        VARCHAR(200),
  IN p_table_id    INT,
  IN p_customer_id INT
)
BEGIN
  INSERT INTO reservation(date, time, duration, note, status, _Table_id, Customer_id)
  VALUES (p_date, p_time, p_duration, p_note, 'aktivna', p_table_id, p_customer_id);
END;;
DELIMITER ;

DROP PROCEDURE IF EXISTS `cancel_reservation`;
DELIMITER ;;
CREATE PROCEDURE `cancel_reservation`(IN p_id INT)
BEGIN
  UPDATE reservation SET status = 'otkazana' WHERE id = p_id;
END;;
DELIMITER ;

-- --- KUPCI ---

DROP PROCEDURE IF EXISTS `add_customer`;
DELIMITER ;;
CREATE PROCEDURE `add_customer`(
  IN p_name  VARCHAR(45),
  IN p_email VARCHAR(45),
  IN p_phone VARCHAR(45),
  OUT p_id   INT
)
BEGIN
  INSERT INTO customer(name, email, phone)
  VALUES (p_name, p_email, p_phone);
  SET p_id = LAST_INSERT_ID();
END;;
DELIMITER ;

DROP PROCEDURE IF EXISTS `get_all_customers`;
DELIMITER ;;
CREATE PROCEDURE `get_all_customers`()
BEGIN
  SELECT id, name, email, phone FROM customer ORDER BY name;
END;;
DELIMITER ;

-- --- NAMIRNICE ---

DROP PROCEDURE IF EXISTS `add_ingredient`;
DELIMITER ;;
CREATE PROCEDURE `add_ingredient`(
  IN p_name        VARCHAR(45),
  IN p_unit        VARCHAR(20),
  IN p_supplier_id INT
)
BEGIN
  IF EXISTS (SELECT 1 FROM ingredient WHERE name = p_name) THEN
    SELECT 'Namirnica već postoji.' AS poruka;
  ELSE
    INSERT INTO ingredient(name, unit, stock_qty, Supplier_id)
    VALUES (p_name, p_unit, 0, p_supplier_id);
    SELECT 'Namirnica uspješno dodana.' AS poruka;
  END IF;
END;;
DELIMITER ;

DROP PROCEDURE IF EXISTS `add_ingredient_to_item`;
DELIMITER ;;
CREATE PROCEDURE `add_ingredient_to_item`(
  IN p_item_id       INT,
  IN p_ingredient_id INT,
  IN p_quantity      INT
)
BEGIN
  IF EXISTS (SELECT 1 FROM itemhasingredient
             WHERE Item_id=p_item_id AND Ingredient_id=p_ingredient_id) THEN
    UPDATE itemhasingredient
    SET quantity = p_quantity
    WHERE Item_id=p_item_id AND Ingredient_id=p_ingredient_id;
  ELSE
    INSERT INTO itemhasingredient(Item_id, Ingredient_id, quantity)
    VALUES (p_item_id, p_ingredient_id, p_quantity);
  END IF;
END;;
DELIMITER ;

-- --- ULOGE ---

DROP PROCEDURE IF EXISTS `get_all_roles`;
DELIMITER ;;
CREATE PROCEDURE `get_all_roles`()
BEGIN
  SELECT id, name FROM role ORDER BY name;
END;;
DELIMITER ;
