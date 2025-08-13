-- =============================================================================
-- YourCarYourWay Sample Data - PostgreSQL
-- =============================================================================

-- =============================================================================
-- VEHICLE CATEGORIES (ACRISS CODES)
-- =============================================================================

INSERT INTO vehicle_categories (acriss_code, name, description, base_price_per_day) VALUES
('ECAR', 'Economy', 'Petite voiture économique, parfaite pour la ville', 25.00),
('CCAR', 'Compact', 'Voiture compacte, bon compromis confort/prix', 35.00),
('ICAR', 'Intermediate', 'Berline intermédiaire, spacieuse et confortable', 45.00),
('SCAR', 'Standard', 'Berline standard, idéale pour les voyages', 55.00),
('FCAR', 'Full Size', 'Grande berline, maximum de confort', 70.00),
('PCAR', 'Premium', 'Véhicule haut de gamme', 90.00),
('LCAR', 'Luxury', 'Véhicule de luxe', 120.00),
('UUAV', 'Mini SUV', 'SUV compact urbain', 60.00),
('SUAV', 'SUV Standard', 'SUV familial spacieux', 80.00),
('FUAV', 'SUV Full Size', 'Grand SUV tout-terrain', 100.00);

-- =============================================================================
-- EQUIPMENTS
-- =============================================================================

INSERT INTO equipments (id, name, description, extra_cost, is_included_by_default) VALUES
(uuid_generate_v4(), 'GPS Navigation', 'Système de navigation GPS intégré', 8.00, false),
(uuid_generate_v4(), 'Siège bébé', 'Siège auto pour enfant (0-4 ans)', 12.00, false),
(uuid_generate_v4(), 'Climatisation', 'Système de climatisation', 0.00, true),
(uuid_generate_v4(), 'Bluetooth', 'Connectivité Bluetooth', 0.00, true),
(uuid_generate_v4(), 'Chaînes neige', 'Chaînes pour conduite hivernale', 15.00, false),
(uuid_generate_v4(), 'Toit ouvrant', 'Toit ouvrant électrique', 20.00, false),
(uuid_generate_v4(), 'Jantes alliage', 'Jantes en alliage léger', 10.00, false),
(uuid_generate_v4(), 'Radar de recul', 'Aide au stationnement', 5.00, false);

-- =============================================================================
-- VEHICLES
-- =============================================================================

INSERT INTO vehicles (id, brand, model, acriss_code, fuel_type, seats, transmission, is_active) VALUES
-- Economy Cars
(uuid_generate_v4(), 'Peugeot', '208', 'ECAR', 'gasoline', 5, 'manual', true),
(uuid_generate_v4(), 'Renault', 'Clio', 'ECAR', 'gasoline', 5, 'manual', true),
(uuid_generate_v4(), 'Citroën', 'C3', 'ECAR', 'gasoline', 5, 'manual', true),

-- Compact Cars
(uuid_generate_v4(), 'Volkswagen', 'Golf', 'CCAR', 'gasoline', 5, 'automatic', true),
(uuid_generate_v4(), 'Peugeot', '308', 'CCAR', 'diesel', 5, 'manual', true),
(uuid_generate_v4(), 'Renault', 'Mégane', 'CCAR', 'gasoline', 5, 'automatic', true),

-- Intermediate Cars
(uuid_generate_v4(), 'Peugeot', '508', 'ICAR', 'diesel', 5, 'automatic', true),
(uuid_generate_v4(), 'Renault', 'Talisman', 'ICAR', 'diesel', 5, 'automatic', true),
(uuid_generate_v4(), 'Citroën', 'C5 Aircross', 'ICAR', 'hybrid', 5, 'automatic', true),

-- Standard Cars
(uuid_generate_v4(), 'BMW', '3 Series', 'SCAR', 'diesel', 5, 'automatic', true),
(uuid_generate_v4(), 'Mercedes', 'C-Class', 'SCAR', 'diesel', 5, 'automatic', true),
(uuid_generate_v4(), 'Audi', 'A4', 'SCAR', 'gasoline', 5, 'automatic', true),

-- SUVs
(uuid_generate_v4(), 'Peugeot', '3008', 'UUAV', 'diesel', 5, 'automatic', true),
(uuid_generate_v4(), 'Renault', 'Kadjar', 'UUAV', 'gasoline', 5, 'automatic', true),
(uuid_generate_v4(), 'BMW', 'X3', 'SUAV', 'diesel', 5, 'automatic', true),
(uuid_generate_v4(), 'Mercedes', 'GLC', 'SUAV', 'gasoline', 5, 'automatic', true),

-- Electric
(uuid_generate_v4(), 'Tesla', 'Model 3', 'PCAR', 'electric', 5, 'automatic', true),
(uuid_generate_v4(), 'Renault', 'Zoe', 'CCAR', 'electric', 5, 'automatic', true);

-- =============================================================================
-- LOCATIONS
-- =============================================================================

INSERT INTO locations (id, name, code, street, city, postal_code, country, phone_number, is_active) VALUES
-- France
(uuid_generate_v4(), 'Paris Charles de Gaulle', 'CDG', 'Aéroport Roissy CDG Terminal 2', 'Roissy-en-France', '95700', 'France', '+33 1 48 62 22 80', true),
(uuid_generate_v4(), 'Paris Orly', 'ORY', 'Aéroport Paris-Orly Sud', 'Orly', '94390', 'France', '+33 1 49 75 15 15', true),
(uuid_generate_v4(), 'Paris Gare du Nord', 'GDN', '18 Rue de Dunkerque', 'Paris', '75010', 'France', '+33 1 55 31 58 40', true),
(uuid_generate_v4(), 'Lyon Part-Dieu', 'LPD', '3 Boulevard Vivier Merle', 'Lyon', '69003', 'France', '+33 4 78 63 20 20', true),
(uuid_generate_v4(), 'Marseille Saint-Charles', 'MSC', 'Square Narvik', 'Marseille', '13001', 'France', '+33 4 91 08 16 40', true),
(uuid_generate_v4(), 'Nice Côte d''Azur', 'NCE', 'Aéroport Nice Côte d''Azur', 'Nice', '06206', 'France', '+33 4 93 21 30 30', true),

-- International
(uuid_generate_v4(), 'London Heathrow', 'LHR', 'Heathrow Airport Terminal 5', 'London', 'TW6 2GA', 'United Kingdom', '+44 20 8759 4321', true),
(uuid_generate_v4(), 'Barcelona El Prat', 'BCN', 'Aeropuerto de Barcelona-El Prat', 'Barcelona', '08820', 'Spain', '+34 93 298 38 38', true),
(uuid_generate_v4(), 'Rome Fiumicino', 'FCO', 'Aeroporto Leonardo da Vinci', 'Rome', '00054', 'Italy', '+39 06 65951', true),
(uuid_generate_v4(), 'Amsterdam Schiphol', 'AMS', 'Schiphol Airport', 'Amsterdam', '1118 CP', 'Netherlands', '+31 20 794 0800', true);

-- =============================================================================
-- SAMPLE USERS
-- =============================================================================

INSERT INTO users (id, email, password_hash, first_name, last_name, birth_date, phone, street, city, postal_code, country, is_email_verified) VALUES
(uuid_generate_v4(), 'jean.dupont@email.com', crypt('password123', gen_salt('bf')), 'Jean', 'Dupont', '1985-03-15', '+33 6 12 34 56 78', '123 Rue de la Paix', 'Paris', '75001', 'France', true),
(uuid_generate_v4(), 'marie.martin@email.com', crypt('password123', gen_salt('bf')), 'Marie', 'Martin', '1990-07-22', '+33 6 98 76 54 32', '45 Avenue des Champs', 'Lyon', '69001', 'France', true),
(uuid_generate_v4(), 'pierre.bernard@email.com', crypt('password123', gen_salt('bf')), 'Pierre', 'Bernard', '1978-11-08', '+33 6 55 44 33 22', '78 Boulevard Saint-Michel', 'Marseille', '13001', 'France', true),
(uuid_generate_v4(), 'sophie.leroy@email.com', crypt('password123', gen_salt('bf')), 'Sophie', 'Leroy', '1992-01-30', '+33 6 11 22 33 44', '12 Rue Victor Hugo', 'Nice', '06000', 'France', true),
(uuid_generate_v4(), 'john.smith@email.com', crypt('password123', gen_salt('bf')), 'John', 'Smith', '1987-05-12', '+44 7911 123456', '10 Downing Street', 'London', 'SW1A 2AA', 'United Kingdom', true);

-- =============================================================================
-- SAMPLE BOOKINGS (COMPLETED FOR DEMO)
-- =============================================================================

-- Get some IDs for sample data
WITH sample_data AS (
    SELECT 
        (SELECT id FROM users WHERE email = 'jean.dupont@email.com') as user_id,
        (SELECT id FROM vehicles WHERE brand = 'Peugeot' AND model = '308' LIMIT 1) as vehicle_id,
        (SELECT id FROM locations WHERE code = 'CDG') as pickup_location,
        (SELECT id FROM locations WHERE code = 'LPD') as return_location
)
INSERT INTO bookings (id, customer_id, pickup_location_id, return_location_id, pickup_datetime, return_datetime, vehicle_id, status, total_amount, currency)
SELECT 
    uuid_generate_v4(),
    user_id,
    pickup_location,
    return_location,
    '2024-01-15 10:00:00'::timestamp,
    '2024-01-20 18:00:00'::timestamp,
    vehicle_id,
    'COMPLETED'::booking_status,
    275.00,
    'EUR'
FROM sample_data;

-- Add a payment for this booking
WITH sample_booking AS (
    SELECT id as booking_id FROM bookings WHERE status = 'COMPLETED' LIMIT 1
)
INSERT INTO payments (id, booking_id, amount, currency, status, payment_method, paid_at)
SELECT 
    uuid_generate_v4(),
    booking_id,
    275.00,
    'EUR',
    'COMPLETED'::payment_status,
    'card',
    '2024-01-14 15:30:00'::timestamp
FROM sample_booking;

-- Add a review for this booking
WITH sample_booking AS (
    SELECT id as booking_id, customer_id FROM bookings WHERE status = 'COMPLETED' LIMIT 1
)
INSERT INTO reviews (id, customer_id, booking_id, rating, comment)
SELECT 
    uuid_generate_v4(),
    customer_id,
    booking_id,
    5,
    'Excellent service, voiture en parfait état et personnel très accueillant!'
FROM sample_booking;

-- =============================================================================
-- SAMPLE SUPPORT TICKET
-- =============================================================================

WITH sample_user AS (
    SELECT id as user_id FROM users WHERE email = 'marie.martin@email.com'
)
INSERT INTO support_tickets (id, customer_id, subject, description, status, priority)
SELECT 
    uuid_generate_v4(),
    user_id,
    'Problème de réservation',
    'Je n''arrive pas à modifier ma réservation pour le week-end prochain. Le système affiche une erreur.',
    'OPEN'::ticket_status,
    'MEDIUM'::ticket_priority
FROM sample_user;