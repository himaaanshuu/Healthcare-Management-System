-- PostgreSQL Schema for Hospital Management System
CREATE DATABASE hospital_management;

\c hospital_management;

-- Doctors table
CREATE TABLE IF NOT EXISTS doctors (
    id SERIAL PRIMARY KEY,
    doctor_id VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    phone VARCHAR(15),
    email VARCHAR(100),
    qualification VARCHAR(100),
    experience_years INTEGER,
    consultation_fee DECIMAL(10,2),
    available BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Patients table
CREATE TABLE IF NOT EXISTS patients (
    id SERIAL PRIMARY KEY,
    patient_id VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    age INTEGER NOT NULL,
    gender VARCHAR(10) CHECK (gender IN ('Male', 'Female', 'Other')),
    phone VARCHAR(15),
    email VARCHAR(100),
    address TEXT,
    blood_group VARCHAR(5),
    emergency_contact VARCHAR(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Appointments table
CREATE TABLE IF NOT EXISTS appointments (
    id SERIAL PRIMARY KEY,
    appointment_id VARCHAR(20) UNIQUE NOT NULL,
    patient_id VARCHAR(20) NOT NULL,
    doctor_id VARCHAR(20) NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status VARCHAR(20) CHECK (status IN ('Scheduled', 'Completed', 'Cancelled', 'No-Show')) DEFAULT 'Scheduled',
    reason TEXT,
    diagnosis TEXT,
    prescription TEXT,
    fee DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_appointments_date ON appointments(appointment_date);
CREATE INDEX IF NOT EXISTS idx_appointments_doctor ON appointments(doctor_id);
CREATE INDEX IF NOT EXISTS idx_appointments_patient ON appointments(patient_id);
CREATE INDEX IF NOT EXISTS idx_doctors_specialization ON doctors(specialization);
CREATE INDEX IF NOT EXISTS idx_patients_name ON patients(name);

-- Sample data
INSERT INTO doctors (doctor_id, name, specialization, phone, email, qualification, experience_years, consultation_fee) VALUES
('DOC001', 'Dr. Sarah Johnson', 'Cardiology', '123-456-7890', 'sarah.johnson@hospital.com', 'MD Cardiology', 15, 500.00),
('DOC002', 'Dr. Michael Chen', 'Neurology', '123-456-7891', 'michael.chen@hospital.com', 'DM Neurology', 12, 600.00),
('DOC003', 'Dr. Priya Sharma', 'Pediatrics', '123-456-7892', 'priya.sharma@hospital.com', 'MD Pediatrics', 8, 400.00)
ON CONFLICT (doctor_id) DO NOTHING;

INSERT INTO patients (patient_id, name, age, gender, phone, email, address, blood_group, emergency_contact) VALUES
('PAT001', 'John Smith', 45, 'Male', '987-654-3210', 'john.smith@email.com', '123 Main St, City', 'O+', '987-654-3211'),
('PAT002', 'Emma Wilson', 32, 'Female', '987-654-3212', 'emma.wilson@email.com', '456 Oak Ave, Town', 'A+', '987-654-3213'),
('PAT003', 'Robert Brown', 67, 'Male', '987-654-3214', 'robert.brown@email.com', '789 Pine Rd, Village', 'B+', '987-654-3215')
ON CONFLICT (patient_id) DO NOTHING;

-- Sample appointments
INSERT INTO appointments (appointment_id, patient_id, doctor_id, appointment_date, appointment_time, reason) VALUES
('APT001', 'PAT001', 'DOC001', '2024-01-15', '09:00:00', 'Routine heart checkup'),
('APT002', 'PAT002', 'DOC003', '2024-01-15', '10:30:00', 'Child vaccination'),
('APT003', 'PAT003', 'DOC002', '2024-01-16', '14:00:00', 'Neurological consultation')
ON CONFLICT (appointment_id) DO NOTHING;