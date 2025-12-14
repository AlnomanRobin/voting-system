-- ============================================
-- Secure Online Voting System - Database Schema
-- ============================================

CREATE DATABASE IF NOT EXISTS voting_system;
USE voting_system;

-- ============================================
-- Users Table (Base Authentication)
-- ============================================
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    account_locked BOOLEAN NOT NULL DEFAULT FALSE,
    failed_login_attempts INT NOT NULL DEFAULT 0,
    last_login_at DATETIME,
    last_login_ip VARCHAR(50),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_role (role)
);

-- ============================================
-- Voters Table (Extends Users)
-- ============================================
CREATE TABLE voters (
    user_id BIGINT PRIMARY KEY,
    voter_id VARCHAR(20) UNIQUE NOT NULL,
    national_id VARCHAR(20) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(10) NOT NULL,
    address VARCHAR(200) NOT NULL,
    city VARCHAR(100),
    state VARCHAR(50),
    zip_code VARCHAR(10),
    phone_number VARCHAR(15),
    email VARCHAR(100),
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    eligible BOOLEAN NOT NULL DEFAULT TRUE,
    photo_path VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_voter_id (voter_id),
    INDEX idx_national_id (national_id),
    INDEX idx_status (status)
);

-- ============================================
-- Elections Table
-- ============================================
CREATE TABLE elections (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    type VARCHAR(50) NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    results_published BOOLEAN NOT NULL DEFAULT FALSE,
    results_published_at DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    FOREIGN KEY (created_by) REFERENCES users(id),
    INDEX idx_status (status),
    INDEX idx_dates (start_time, end_time),
    INDEX idx_type (type)
);

-- ============================================
-- Candidates Table
-- ============================================
CREATE TABLE candidates (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    party_name VARCHAR(100) NOT NULL,
    party_symbol VARCHAR(100),
    biography TEXT,
    manifesto TEXT,
    photo_path VARCHAR(255),
    symbol_image_path VARCHAR(255),
    education VARCHAR(100),
    experience TEXT,
    age INT,
    election_id BIGINT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    display_order INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (election_id) REFERENCES elections(id) ON DELETE CASCADE,
    INDEX idx_election (election_id),
    INDEX idx_active (active)
);

-- ============================================
-- Votes Table (Encrypted & Secure)
-- ============================================
CREATE TABLE votes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    voter_id BIGINT NOT NULL,
    election_id BIGINT NOT NULL,
    candidate_id BIGINT NOT NULL,
    voted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(50) NOT NULL,
    user_agent VARCHAR(200) NOT NULL,
    vote_hash VARCHAR(64) NOT NULL,
    verified BOOLEAN NOT NULL DEFAULT TRUE,
    session_id VARCHAR(255) NOT NULL,
    FOREIGN KEY (voter_id) REFERENCES voters(user_id) ON DELETE RESTRICT,
    FOREIGN KEY (election_id) REFERENCES elections(id) ON DELETE RESTRICT,
    FOREIGN KEY (candidate_id) REFERENCES candidates(id) ON DELETE RESTRICT,
    UNIQUE KEY unique_vote (voter_id, election_id),
    INDEX idx_election (election_id),
    INDEX idx_candidate (candidate_id),
    INDEX idx_voted_at (voted_at)
);

-- ============================================
-- Audit Logs Table (Security Tracking)
-- ============================================
CREATE TABLE audit_logs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    action_type VARCHAR(50) NOT NULL,
    action VARCHAR(200) NOT NULL,
    details TEXT,
    ip_address VARCHAR(50),
    user_agent VARCHAR(200),
    severity VARCHAR(20) NOT NULL,
    success BOOLEAN NOT NULL DEFAULT TRUE,
    error_message TEXT,
    timestamp DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_action_type (action_type),
    INDEX idx_severity (severity),
    INDEX idx_timestamp (timestamp),
    INDEX idx_user (user_id)
);

-- ============================================
-- Insert Default Admin User
-- Password: admin123 (BCrypt hashed)
-- ============================================
INSERT INTO users (username, password, role, active, account_locked)
VALUES ('admin', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5gyUI3yg1X9g2', 'ADMIN', TRUE, FALSE);

-- ============================================
-- Insert Sample Voter for Testing
-- Voter ID: V001, Password: voter123
-- ============================================
INSERT INTO users (username, password, role, active, account_locked)
VALUES ('voter001', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5gyUI3yg1X9g2', 'VOTER', TRUE, FALSE);

INSERT INTO voters (user_id, voter_id, national_id, full_name, date_of_birth, gender, address, verified, status)
VALUES (LAST_INSERT_ID(), 'V001', 'N12345678', 'John Doe Voter', '1990-01-15', 'Male', '123 Main Street, City', TRUE, 'VERIFIED');

-- ============================================
-- Insert Sample Election
-- ============================================
INSERT INTO elections (name, description, type, start_time, end_time, status, created_by)
VALUES (
    'Presidential Election 2024',
    'National Presidential Election for selecting the next President',
    'PRESIDENTIAL',
    '2024-12-15 08:00:00',
    '2024-12-15 20:00:00',
    'ACTIVE',
    1
);

-- ============================================
-- Insert Sample Candidates
-- ============================================
INSERT INTO candidates (name, party_name, party_symbol, biography, election_id, active, display_order)
VALUES 
    ('Alice Johnson', 'Progressive Party', 'Rising Sun', 'Experienced politician with 20 years in public service', 1, TRUE, 1),
    ('Bob Smith', 'Conservative Alliance', 'Mountain Peak', 'Business leader and former governor', 1, TRUE, 2),
    ('Carol Williams', 'People\'s Coalition', 'Green Tree', 'Human rights advocate and community organizer', 1, TRUE, 3);

-- ============================================
-- Useful Queries
-- ============================================

-- Get election results
/*
SELECT 
    c.name AS candidate_name,
    c.party_name,
    COUNT(v.id) AS vote_count,
    ROUND((COUNT(v.id) * 100.0 / (SELECT COUNT(*) FROM votes WHERE election_id = 1)), 2) AS percentage
FROM candidates c
LEFT JOIN votes v ON c.id = v.candidate_id
WHERE c.election_id = 1
GROUP BY c.id, c.name, c.party_name
ORDER BY vote_count DESC;
*/

-- Get voter participation rate
/*
SELECT 
    e.name AS election_name,
    COUNT(DISTINCT v.voter_id) AS voters_participated,
    (SELECT COUNT(*) FROM voters WHERE verified = TRUE) AS total_eligible_voters,
    ROUND((COUNT(DISTINCT v.voter_id) * 100.0 / (SELECT COUNT(*) FROM voters WHERE verified = TRUE)), 2) AS participation_rate
FROM elections e
LEFT JOIN votes v ON e.id = v.election_id
WHERE e.id = 1
GROUP BY e.id, e.name;
*/

-- Get recent audit logs
/*
SELECT 
    al.action,
    al.action_type,
    al.severity,
    u.username,
    al.ip_address,
    al.timestamp
FROM audit_logs al
LEFT JOIN users u ON al.user_id = u.id
ORDER BY al.timestamp DESC
LIMIT 50;
*/

-- Check for suspicious activities
/*
SELECT 
    user_id,
    COUNT(*) AS attempt_count,
    MAX(timestamp) AS last_attempt
FROM audit_logs
WHERE action_type = 'LOGIN' 
  AND success = FALSE
  AND timestamp >= DATE_SUB(NOW(), INTERVAL 1 HOUR)
GROUP BY user_id
HAVING attempt_count > 3;
*/
