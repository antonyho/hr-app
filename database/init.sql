-- HR Application Database Schema

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- User roles enum
CREATE TYPE user_role AS ENUM ('MANAGER', 'EMPLOYEE');

-- Absence status enum
CREATE TYPE absence_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED');

-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role user_role NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Employee profiles table
CREATE TABLE employee_profiles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    employee_id VARCHAR(20) UNIQUE NOT NULL,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    department VARCHAR(100),
    position VARCHAR(100),
    hire_date DATE,
    phone VARCHAR(20),
    address TEXT,
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    manager_id UUID REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Absence requests table
CREATE TABLE absence_requests (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    employee_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    reason TEXT,
    status absence_status DEFAULT 'PENDING',
    approved_by UUID REFERENCES users(id),
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP,
    comments TEXT
);

-- Feedback table for coworker feedback
CREATE TABLE feedback (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    profile_id UUID NOT NULL REFERENCES employee_profiles(id) ON DELETE CASCADE,
    feedback_by UUID NOT NULL REFERENCES users(id),
    feedback_text TEXT NOT NULL,
    polished_feedback TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for better performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_employee_profiles_user_id ON employee_profiles(user_id);
CREATE INDEX idx_employee_profiles_employee_id ON employee_profiles(employee_id);
CREATE INDEX idx_absence_requests_employee_id ON absence_requests(employee_id);
CREATE INDEX idx_absence_requests_status ON absence_requests(status);
CREATE INDEX idx_feedback_profile_id ON feedback(profile_id);

-- Insert some sample data (with explicit UUIDs for consistency)
INSERT INTO users (id, email, password_hash, role) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'manager@company.com', '$2a$10$v6mbuUn869h44yLZUuzuKOpT7LLvyJ9ffiT9hycaxWuO6Rv3pjT4a', 'MANAGER'),
('550e8400-e29b-41d4-a716-446655440002', 'john.doe@company.com', '$2a$10$v6mbuUn869h44yLZUuzuKOpT7LLvyJ9ffiT9hycaxWuO6Rv3pjT4a', 'EMPLOYEE'),
('550e8400-e29b-41d4-a716-446655440003', 'jane.smith@company.com', '$2a$10$v6mbuUn869h44yLZUuzuKOpT7LLvyJ9ffiT9hycaxWuO6Rv3pjT4a', 'EMPLOYEE');

INSERT INTO employee_profiles (user_id, employee_id, first_name, last_name, department, position, hire_date, phone, address, manager_id) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'MGR001', 'Alice', 'Manager', 'HR', 'HR Manager', '2020-01-15', '+1-555-0101', '123 Manager St', NULL),
('550e8400-e29b-41d4-a716-446655440002', 'EMP001', 'John', 'Doe', 'Engineering', 'Software Developer', '2021-03-01', '+1-555-0102', '456 Employee Ave', '550e8400-e29b-41d4-a716-446655440001'),
('550e8400-e29b-41d4-a716-446655440003', 'EMP002', 'Jane', 'Smith', 'Engineering', 'Senior Developer', '2020-06-15', '+1-555-0103', '789 Coworker Blvd', '550e8400-e29b-41d4-a716-446655440001');