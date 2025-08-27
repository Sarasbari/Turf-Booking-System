-- Turf Booking System Database Schema

-- Users table for storing user information
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    google_id VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    password_hash VARCHAR(255),
    profile_picture VARCHAR(500),
    role VARCHAR(10) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Turfs table for storing turf information
CREATE TABLE IF NOT EXISTS turfs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(200) NOT NULL,
    city VARCHAR(50) NOT NULL,
    area VARCHAR(100) NOT NULL,
    turf_type VARCHAR(20) NOT NULL,
    price_per_hour DECIMAL(10, 2) NOT NULL,
    rating DECIMAL(3, 2) DEFAULT 0.0,
    total_reviews INT DEFAULT 0,
    image_url VARCHAR(500),
    description TEXT,
    facilities VARCHAR(1000),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    contact_phone VARCHAR(20),
    opening_time TIME DEFAULT '06:00:00',
    closing_time TIME DEFAULT '23:00:00',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bookings table for storing booking information
CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    turf_id BIGINT NOT NULL,
    booking_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    number_of_players INT DEFAULT 1,
    total_amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    special_requests TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (turf_id) REFERENCES turfs(id) ON DELETE CASCADE,
    CONSTRAINT unique_booking UNIQUE (turf_id, booking_date, start_time, end_time)
);

-- Transactions table for storing payment information
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    transaction_id VARCHAR(100) UNIQUE NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    payment_status VARCHAR(20) DEFAULT 'PENDING',
    gateway_response VARCHAR(1000),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
);

-- Reviews table for storing user reviews and ratings
CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    turf_id BIGINT NOT NULL,
    booking_id BIGINT,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    review_text TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (turf_id) REFERENCES turfs(id) ON DELETE CASCADE,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE SET NULL,
    CONSTRAINT unique_user_turf_review UNIQUE (user_id, turf_id, booking_id)
);

-- Tournaments table for storing tournament information
CREATE TABLE IF NOT EXISTS tournaments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    location VARCHAR(200) NOT NULL,
    turf_type VARCHAR(20) NOT NULL,
    tournament_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    entry_fee DECIMAL(10, 2) NOT NULL,
    prize_money DECIMAL(10, 2),
    max_teams INT NOT NULL,
    registered_teams INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'UPCOMING',
    registration_deadline DATE,
    rules TEXT,
    contact_info VARCHAR(200),
    image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tournament registrations table
CREATE TABLE IF NOT EXISTS tournament_registrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tournament_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    team_name VARCHAR(100) NOT NULL,
    team_members VARCHAR(1000) NOT NULL,
    contact_phone VARCHAR(20),
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_status VARCHAR(20) DEFAULT 'PENDING',
    FOREIGN KEY (tournament_id) REFERENCES tournaments(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT unique_user_tournament UNIQUE (user_id, tournament_id)
);

-- Offers table for storing promotional offers
CREATE TABLE IF NOT EXISTS offers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    discount_percentage DECIMAL(5, 2),
    discount_amount DECIMAL(10, 2),
    min_booking_amount DECIMAL(10, 2),
    offer_code VARCHAR(50) UNIQUE,
    valid_from DATE NOT NULL,
    valid_until DATE NOT NULL,
    max_usage_per_user INT DEFAULT 1,
    total_usage_limit INT,
    current_usage INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    applicable_turf_types VARCHAR(1000),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User offer usage tracking
CREATE TABLE IF NOT EXISTS user_offer_usage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    offer_id BIGINT NOT NULL,
    booking_id BIGINT NOT NULL,
    usage_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (offer_id) REFERENCES offers(id) ON DELETE CASCADE,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE
);

-- Indexes for better performance
-- Note: H2 automatically creates indexes for UNIQUE constraints
-- Additional indexes for performance optimization
CREATE INDEX idx_turfs_location ON turfs(city, area);
CREATE INDEX idx_turfs_type ON turfs(turf_type);
CREATE INDEX idx_turfs_rating ON turfs(rating DESC);
CREATE INDEX idx_bookings_user ON bookings(user_id);
CREATE INDEX idx_bookings_turf ON bookings(turf_id);
CREATE INDEX idx_bookings_date ON bookings(booking_date);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_reviews_turf ON reviews(turf_id);
CREATE INDEX idx_reviews_rating ON reviews(rating DESC);
CREATE INDEX idx_tournaments_date ON tournaments(tournament_date);
CREATE INDEX idx_tournaments_type ON tournaments(turf_type);
