-- Flyway migration: add last_ip_at timestamp to users table
ALTER TABLE users ADD COLUMN last_ip_at TIMESTAMP WITH TIME ZONE;
