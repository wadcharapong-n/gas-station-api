-- Example Data for Gas Station Information & Management System
-- Passwords are 'password' hashed with BCrypt

-- Users
INSERT INTO users (id, name, email, password_hash, role, is_active, created_at) VALUES
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'Admin User', 'admin@station.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOn2', 'CORPORATE_ADMIN', true, CURRENT_TIMESTAMP),
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', 'Manager User', 'manager@station.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOn2', 'STATION_MANAGER', true, CURRENT_TIMESTAMP);

-- Stations
INSERT INTO stations (id, name, slug, address, latitude, longitude, phone, email, is_active, api_station_id, created_at, updated_at, social_links, services, hours) VALUES
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'Bangkok Central Station', 'bangkok-central', '123 Sukhumvit Rd, Bangkok, Thailand', 13.736717, 100.523186, '02-123-4567', 'central@bangkok-gas.com', true, 'ST001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '{}', '["Car Wash", "Convenience Store", "ATM"]', '{"monday": "00:00-23:59", "tuesday": "00:00-23:59", "wednesday": "00:00-23:59", "thursday": "00:00-23:59", "friday": "00:00-23:59", "saturday": "00:00-23:59", "sunday": "00:00-23:59"}'),
('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'Chiang Mai North Station', 'chiang-mai-north', '456 Nimman Rd, Chiang Mai, Thailand', 18.796143, 98.965611, '053-123-456', 'north@chiangmai-gas.com', true, 'ST002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, '{}', '["Convenience Store", "Coffee Shop"]', '{"monday": "06:00-22:00", "tuesday": "06:00-22:00", "wednesday": "06:00-22:00", "thursday": "06:00-22:00", "friday": "06:00-22:00", "saturday": "06:00-22:00", "sunday": "06:00-22:00"}');

-- Station-User Assignments
INSERT INTO station_users (id, station_id, user_id, assigned_at, assigned_by) VALUES
('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a31', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12', CURRENT_TIMESTAMP, 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11');

-- Fuel Types for Bangkok Central Station
INSERT INTO fuel_types (id, station_id, name, code, tank_capacity_litres, low_threshold_litres, is_active) VALUES
('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a41', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'Gasohol 95', 'G95', 20000.0, 4000.0, true),
('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a42', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'Diesel B7', 'DB7', 30000.0, 6000.0, true),
('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a43', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'E20', 'E20', 15000.0, 3000.0, true);

-- Fuel Types for Chiang Mai North Station
INSERT INTO fuel_types (id, station_id, name, code, tank_capacity_litres, low_threshold_litres, is_active) VALUES
('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'Gasohol 95', 'G95', 10000.0, 2000.0, true),
('d0eebc99-9c0b-4ef8-bb6d-6bb9bd380a45', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'Diesel B7', 'DB7', 15000.0, 3000.0, true);

-- Fuel Prices (Recent Data)
INSERT INTO fuel_prices (id, station_id, fuel_type_id, price_per_litre, source, override_active, recorded_at) VALUES
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a51', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a41', 38.50, 'API', false, CURRENT_TIMESTAMP),
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a52', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a42', 29.94, 'API', false, CURRENT_TIMESTAMP),
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a53', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a43', 36.40, 'API', false, CURRENT_TIMESTAMP),
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a54', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', 39.20, 'API', false, CURRENT_TIMESTAMP),
('e0eebc99-9c0b-4ef8-bb6d-6bb9bd380a55', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a45', 30.50, 'API', false, CURRENT_TIMESTAMP);

-- Fuel Levels (Recent Data)
INSERT INTO fuel_levels (id, station_id, fuel_type_id, volume_litres, source, override_active, recorded_at) VALUES
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a61', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a41', 12500.0, 'API', false, CURRENT_TIMESTAMP),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a62', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a42', 5000.0, 'API', false, CURRENT_TIMESTAMP), -- Below threshold
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a63', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a43', 8000.0, 'API', false, CURRENT_TIMESTAMP),
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a64', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a44', 1500.0, 'API', false, CURRENT_TIMESTAMP), -- Below threshold
('f0eebc99-9c0b-4ef8-bb6d-6bb9bd380a65', 'b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'd0eebc99-9c0b-4ef8-bb6d-6bb9bd380a45', 12000.0, 'API', false, CURRENT_TIMESTAMP);
