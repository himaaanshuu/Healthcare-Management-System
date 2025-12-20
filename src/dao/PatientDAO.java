package dao;

import model.Patient;
import model.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {
    
    public boolean addPatient(Patient patient) {
        String sql = "INSERT INTO patients (patient_id, name, age, gender, phone, email, " +
                    "address, blood_group, emergency_contact) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, patient.getPatientId());
            pstmt.setString(2, patient.getName());
            pstmt.setInt(3, patient.getAge());
            pstmt.setString(4, patient.getGender());
            pstmt.setString(5, patient.getPhone());
            pstmt.setString(6, patient.getEmail());
            pstmt.setString(7, patient.getAddress());
            pstmt.setString(8, patient.getBloodGroup());
            pstmt.setString(9, patient.getEmergencyContact());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Patient added successfully: " + patient.getPatientId());
                return true;
            } else {
                System.err.println("Failed to add patient: No rows affected");
                return false;
            }
            
        } catch (SQLException e) {
            // Handle specific SQL errors
            if (e.getSQLState() != null && e.getSQLState().equals("23505")) { // Unique constraint violation
                System.err.println("Patient ID already exists: " + patient.getPatientId());
                throw new RuntimeException("Patient ID " + patient.getPatientId() + " already exists. Please use a different ID.");
            } else if (e.getSQLState() != null && e.getSQLState().equals("23514")) { // Check constraint violation
                System.err.println("Check constraint violation: " + e.getMessage());
                throw new RuntimeException("Invalid data: " + e.getMessage());
            } else {
                System.err.println("Error adding patient: " + e.getMessage());
                throw new RuntimeException("Database error: " + e.getMessage(), e);
            }
        }
    }
    
    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Patient patient = extractPatientFromResultSet(rs);
                patients.add(patient);
            }
            
            System.out.println("Retrieved " + patients.size() + " patients from database");
            
        } catch (SQLException e) {
            System.err.println("Error retrieving patients: " + e.getMessage());
            e.printStackTrace();
        }
        
        return patients;
    }
    
    public Patient getPatientById(String patientId) {
        String sql = "SELECT * FROM patients WHERE patient_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, patientId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Patient patient = extractPatientFromResultSet(rs);
                    System.out.println("Found patient: " + patientId);
                    return patient;
                } else {
                    System.out.println("Patient not found: " + patientId);
                    return null;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving patient: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean updatePatient(Patient patient) {
        String sql = "UPDATE patients SET name = ?, age = ?, gender = ?, phone = ?, " +
                    "email = ?, address = ?, blood_group = ?, emergency_contact = ? " +
                    "WHERE patient_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, patient.getName());
            pstmt.setInt(2, patient.getAge());
            pstmt.setString(3, patient.getGender());
            pstmt.setString(4, patient.getPhone());
            pstmt.setString(5, patient.getEmail());
            pstmt.setString(6, patient.getAddress());
            pstmt.setString(7, patient.getBloodGroup());
            pstmt.setString(8, patient.getEmergencyContact());
            pstmt.setString(9, patient.getPatientId());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Patient updated successfully: " + patient.getPatientId());
                return true;
            } else {
                System.err.println("Patient not found for update: " + patient.getPatientId());
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating patient: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deletePatient(String patientId) {
        // Check if patient has appointments first
        if (hasAppointments(patientId)) {
            System.err.println("Cannot delete patient " + patientId + " - has existing appointments");
            return false;
        }
        
        String sql = "DELETE FROM patients WHERE patient_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, patientId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Patient deleted successfully: " + patientId);
                return true;
            } else {
                System.err.println("Patient not found for deletion: " + patientId);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting patient: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Patient> searchPatientsByName(String name) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE name ILIKE ? ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + name + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Patient patient = extractPatientFromResultSet(rs);
                    patients.add(patient);
                }
            }
            
            System.out.println("Found " + patients.size() + " patients matching: " + name);
            
        } catch (SQLException e) {
            System.err.println("Error searching patients: " + e.getMessage());
            e.printStackTrace();
        }
        
        return patients;
    }
    
    public List<Patient> getPatientsByAgeRange(int minAge, int maxAge) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE age BETWEEN ? AND ? ORDER BY age";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, minAge);
            pstmt.setInt(2, maxAge);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Patient patient = extractPatientFromResultSet(rs);
                    patients.add(patient);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting patients by age range: " + e.getMessage());
            e.printStackTrace();
        }
        
        return patients;
    }
    
    public List<Patient> getPatientsByBloodGroup(String bloodGroup) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE blood_group = ? ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, bloodGroup);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Patient patient = extractPatientFromResultSet(rs);
                    patients.add(patient);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting patients by blood group: " + e.getMessage());
            e.printStackTrace();
        }
        
        return patients;
    }
    
    public PatientStatistics getPatientStatistics() {
        PatientStatistics stats = new PatientStatistics();
        String sql = "SELECT " +
                    "COUNT(*) as total, " +
                    "AVG(age) as avg_age, " +
                    "SUM(CASE WHEN gender = 'Male' THEN 1 ELSE 0 END) as male_count, " +
                    "SUM(CASE WHEN gender = 'Female' THEN 1 ELSE 0 END) as female_count, " +
                    "SUM(CASE WHEN gender = 'Other' THEN 1 ELSE 0 END) as other_count, " +
                    "MIN(age) as min_age, " +
                    "MAX(age) as max_age " +
                    "FROM patients";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                stats.total = rs.getInt("total");
                stats.averageAge = rs.getDouble("avg_age");
                stats.maleCount = rs.getInt("male_count");
                stats.femaleCount = rs.getInt("female_count");
                stats.otherCount = rs.getInt("other_count");
                stats.minAge = rs.getInt("min_age");
                stats.maxAge = rs.getInt("max_age");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting patient statistics: " + e.getMessage());
            e.printStackTrace();
        }
        
        return stats;
    }
    
    private Patient extractPatientFromResultSet(ResultSet rs) throws SQLException {
        Patient patient = new Patient();
        patient.setId(rs.getInt("id"));
        patient.setPatientId(rs.getString("patient_id"));
        patient.setName(rs.getString("name"));
        patient.setAge(rs.getInt("age"));
        patient.setGender(rs.getString("gender"));
        patient.setPhone(rs.getString("phone"));
        patient.setEmail(rs.getString("email"));
        patient.setAddress(rs.getString("address"));
        patient.setBloodGroup(rs.getString("blood_group"));
        patient.setEmergencyContact(rs.getString("emergency_contact"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            patient.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return patient;
    }
    
    private boolean hasAppointments(String patientId) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE patient_id = ? AND status != 'Cancelled'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, patientId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking appointments: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    // Inner class for patient statistics
    public static class PatientStatistics {
        public int total;
        public double averageAge;
        public int maleCount;
        public int femaleCount;
        public int otherCount;
        public int minAge;
        public int maxAge;
        
        @Override
        public String toString() {
            return String.format(
                "Patient Statistics:\n" +
                "  Total Patients: %d\n" +
                "  Average Age: %.1f\n" +
                "  Gender Distribution:\n" +
                "    Male: %d (%.1f%%)\n" +
                "    Female: %d (%.1f%%)\n" +
                "    Other: %d (%.1f%%)\n" +
                "  Age Range: %d - %d years",
                total,
                averageAge,
                maleCount, (total > 0 ? (maleCount * 100.0 / total) : 0),
                femaleCount, (total > 0 ? (femaleCount * 100.0 / total) : 0),
                otherCount, (total > 0 ? (otherCount * 100.0 / total) : 0),
                minAge, maxAge
            );
        }
    }
}
