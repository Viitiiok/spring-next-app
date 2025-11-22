-- Flyway migration: add registered_ip and last_ip columns to users table
ALTER TABLE users ADD COLUMN registered_ip VARCHAR(255);
ALTER TABLE users ADD COLUMN last_ip VARCHAR(255);
