-- Insert sample users
INSERT INTO users (name, email, google_id, phone, role) VALUES
('John Doe', 'john.doe@gmail.com', 'google_123456789', '+91-9876543210', 'USER'),
('Jane Smith', 'jane.smith@gmail.com', 'google_987654321', '+91-9876543211', 'USER'),
('Admin User', 'admin@turfbooking.com', NULL, '+91-9876543212', 'ADMIN'),
('Mike Johnson', 'mike.johnson@gmail.com', 'google_456789123', '+91-9876543213', 'USER'),
('Sarah Wilson', 'sarah.wilson@gmail.com', 'google_789123456', '+91-9876543214', 'USER');

-- Insert sample turfs
INSERT INTO turfs (name, location, city, area, turf_type, price_per_hour, rating, total_reviews, image_url, description, facilities, latitude, longitude, contact_phone) VALUES
('Champions Football Arena', '123 Sports Complex, MG Road', 'Mumbai', 'Andheri West', 'FOOTBALL', 1200.00, 4.5, 45, 'https://example.com/images/football1.jpg', 'Premium football turf with FIFA standard artificial grass, floodlights, and changing rooms', '["Floodlights", "Changing Rooms", "Parking", "Refreshments"]', 19.1136, 72.8697, '+91-9876543215'),
('Elite Cricket Ground', '456 Cricket Stadium, Brigade Road', 'Bangalore', 'Koramangala', 'CRICKET', 2000.00, 4.8, 67, 'https://example.com/images/cricket1.jpg', 'Professional cricket ground with 22-yard pitch, pavilion, and practice nets', '["Practice Nets", "Pavilion", "Scoreboard", "Equipment Rental"]', 12.9352, 77.6245, '+91-9876543216'),
('Multi-Sport Arena', '789 Sports Hub, Cyber City', 'Gurgaon', 'DLF Phase 2', 'MULTI_SPORT', 800.00, 4.2, 32, 'https://example.com/images/multisport1.jpg', 'Versatile turf suitable for football, hockey, and other field sports', '["Multi-purpose", "Seating Area", "First Aid", "Parking"]', 28.4595, 77.0266, '+91-9876543217'),
('Premium Basketball Court', '321 Indoor Complex, Park Street', 'Kolkata', 'Park Street', 'BASKETBALL', 600.00, 4.6, 28, 'https://example.com/images/basketball1.jpg', 'Indoor basketball court with wooden flooring and air conditioning', '["Air Conditioning", "Sound System", "Locker Rooms", "Spectator Seating"]', 22.5726, 88.3639, '+91-9876543218'),
('Royal Tennis Academy', '654 Tennis Club, Anna Nagar', 'Chennai', 'Anna Nagar', 'TENNIS', 900.00, 4.7, 51, 'https://example.com/images/tennis1.jpg', 'Professional tennis court with clay surface and coaching facilities', '["Clay Court", "Coaching Available", "Equipment Rental", "Cafeteria"]', 13.0827, 80.2707, '+91-9876543219'),
('City Badminton Center', '987 Shuttle Complex, Banjara Hills', 'Hyderabad', 'Banjara Hills', 'BADMINTON', 400.00, 4.3, 39, 'https://example.com/images/badminton1.jpg', 'Modern badminton courts with proper lighting and ventilation', '["Multiple Courts", "Equipment Rental", "Coaching", "Parking"]', 17.4065, 78.4772, '+91-9876543220');

-- Insert sample bookings
INSERT INTO bookings (user_id, turf_id, booking_date, start_time, end_time, number_of_players, total_amount, status) VALUES
(1, 1, '2024-02-15', '16:00:00', '18:00:00', 10, 2400.00, 'CONFIRMED'),
(2, 2, '2024-02-16', '14:00:00', '17:00:00', 22, 6000.00, 'CONFIRMED'),
(4, 3, '2024-02-17', '10:00:00', '12:00:00', 8, 1600.00, 'PENDING'),
(5, 4, '2024-02-18', '19:00:00', '21:00:00', 5, 1200.00, 'CONFIRMED'),
(1, 5, '2024-02-19', '15:00:00', '17:00:00', 2, 1800.00, 'COMPLETED');

-- Insert sample transactions
INSERT INTO transactions (booking_id, transaction_id, amount, payment_method, payment_status) VALUES
(1, 'TXN_001_20240215', 2400.00, 'UPI', 'SUCCESS'),
(2, 'TXN_002_20240216', 6000.00, 'CREDIT_CARD', 'SUCCESS'),
(4, 'TXN_004_20240218', 1200.00, 'NET_BANKING', 'SUCCESS'),
(5, 'TXN_005_20240219', 1800.00, 'DEBIT_CARD', 'SUCCESS');

-- Insert sample reviews
INSERT INTO reviews (user_id, turf_id, booking_id, rating, review_text) VALUES
(1, 1, 1, 5, 'Excellent facility! The artificial grass quality is top-notch and the floodlights are perfect for evening games.'),
(2, 2, 2, 5, 'Amazing cricket ground with professional setup. The practice nets are very helpful for warm-up.'),
(5, 5, 5, 4, 'Good tennis court with proper clay surface. The coaching staff is knowledgeable and helpful.'),
(1, 3, NULL, 4, 'Great multi-sport facility. Perfect for casual games with friends. Good parking space available.'),
(4, 4, 4, 5, 'Best basketball court in the city! Air conditioning makes it comfortable even during summer.');

-- Insert sample tournaments
INSERT INTO tournaments (name, description, location, turf_type, tournament_date, start_time, end_time, entry_fee, prize_money, max_teams, registration_deadline, rules, contact_info, image_url) VALUES
('Mumbai Football Championship 2024', 'Annual football tournament featuring teams from across Mumbai', 'Champions Football Arena, Andheri West', 'FOOTBALL', '2024-03-15', '08:00:00', '20:00:00', 5000.00, 50000.00, 16, '2024-03-01', '11v11 format, 90-minute matches, knockout system', 'tournament@championsarena.com', 'https://example.com/images/football_tournament.jpg'),
('Bangalore Cricket Premier League', 'T20 cricket tournament with cash prizes and trophies', 'Elite Cricket Ground, Koramangala', 'CRICKET', '2024-03-22', '09:00:00', '18:00:00', 8000.00, 100000.00, 8, '2024-03-08', '20 overs per side, league + knockout format', 'cricket@eliteground.com', 'https://example.com/images/cricket_tournament.jpg'),
('Inter-City Basketball League', 'Basketball tournament featuring teams from different cities', 'Premium Basketball Court, Park Street', 'BASKETBALL', '2024-04-05', '10:00:00', '19:00:00', 3000.00, 25000.00, 12, '2024-03-20', '5v5 format, 4 quarters of 12 minutes each', 'basketball@premiumcourt.com', 'https://example.com/images/basketball_tournament.jpg');

-- Insert sample offers
INSERT INTO offers (title, description, discount_percentage, offer_code, valid_from, valid_until, max_usage_per_user, total_usage_limit, applicable_turf_types) VALUES
('New User Special', 'Get 20% off on your first booking', 20.00, 'NEWUSER20', '2024-01-01', '2024-12-31', 1, 1000, '["FOOTBALL", "CRICKET", "BASKETBALL", "TENNIS", "BADMINTON", "MULTI_SPORT"]'),
('Weekend Warriors', 'Special weekend discount for all sports', 15.00, 'WEEKEND15', '2024-01-01', '2024-06-30', 4, 500, '["FOOTBALL", "CRICKET", "BASKETBALL", "TENNIS", "BADMINTON", "MULTI_SPORT"]'),
('Cricket Fever', 'Exclusive offer for cricket enthusiasts', 25.00, 'CRICKET25', '2024-02-01', '2024-04-30', 2, 200, '["CRICKET"]');