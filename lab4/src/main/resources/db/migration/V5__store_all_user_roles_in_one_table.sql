-- Keep every existing user ID while moving the remaining customer-specific fields.
ALTER TABLE users ADD COLUMN address VARCHAR(255), ADD COLUMN coordinates VARCHAR(255);
UPDATE users u SET address = c.address, coordinates = c.coordinates
FROM customers c WHERE u.id = c.id;

ALTER TABLE users
    ADD CONSTRAINT chk_customer_details CHECK
        (role <> 'CUSTOMER' OR (address IS NOT NULL AND coordinates IS NOT NULL)),
    ADD CONSTRAINT uk_users_id_role UNIQUE (id, role);

-- The fixed discriminator columns retain database-level subtype checks even though
-- every user now lives in one table. They are supplied by defaults for JPA inserts.
ALTER TABLE deliveries DROP CONSTRAINT deliveries_customer_fkey;
ALTER TABLE deliveries
    ADD COLUMN customer_role VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
    ADD CONSTRAINT chk_delivery_customer_role CHECK (customer_role = 'CUSTOMER'),
    ADD CONSTRAINT deliveries_customer_fkey FOREIGN KEY (customer, customer_role)
        REFERENCES users(id, role) ON DELETE RESTRICT;

ALTER TABLE vehicles DROP CONSTRAINT vehicles_driver_fkey;
ALTER TABLE vehicles
    ADD COLUMN driver_role VARCHAR(20) NOT NULL DEFAULT 'DRIVER',
    ADD CONSTRAINT chk_vehicle_driver_role CHECK (driver_role = 'DRIVER'),
    ADD CONSTRAINT vehicles_driver_fkey FOREIGN KEY (driver, driver_role)
        REFERENCES users(id, role) ON DELETE SET NULL (driver);

DROP TABLE customers;
DROP TABLE drivers;
