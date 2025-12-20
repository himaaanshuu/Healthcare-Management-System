package dao;

import model.Doctor;
import model.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {
    
    public boolean addDoctor(Doctor doctor) {
        String sql = "INSERT INTO doctors (doctor_id, name, specialization, phone, email, " +
                    "qualification, experience_years, consultation_fee) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, doctor.getDoctorId());
            pstmt.setString(2, doctor.getName());
            pstmt.setString(3, doctor.getSpecialization());
            pstmt.setString(4, doctor.getPhone());
            pstmt.setString(5, doctor.getEmail());
            pstmt.setString(6, doctor.getQualification());
            pstmt.setInt(7, doctor.getExperienceYears());
            pstmt.setDouble(8, doctor.getConsultationFee());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Doctor added successfully: " + doctor.getDoctorId());
                return true;
            } else {
                System.err.println("Failed to add doctor: No rows affected");
                return false;
            }
            
        } catch (SQLException e) {
            // Handle specific SQL errors
            if (e.getSQLState() != null && e.getSQLState().equals("23505")) { // Unique constraint violation
                System.err.println("Doctor ID already exists: " + doctor.getDoctorId());
                throw new RuntimeException("Doctor ID " + doctor.getDoctorId() + " already exists. Please use a different ID.");
            } else if (e.getSQLState() != null && e.getSQLState().equals("23514")) { // Check constraint violation
                System.err.println("Check constraint violation: " + e.getMessage());
                throw new RuntimeException("Invalid data: " + e.getMessage());
            } else {
                System.err.println("Error adding doctor: " + e.getMessage());
                throw new RuntimeException("Database error: " + e.getMessage(), e);
            }
        }
    }
    
    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Doctor doctor = extractDoctorFromResultSet(rs);
                doctors.add(doctor);
            }
            
            System.out.println("Retrieved " + doctors.size() + " doctors from database");
            
        } catch (SQLException e) {
            System.err.println("Error retrieving doctors: " + e.getMessage());
            e.printStackTrace();
        }
        
        return doctors;
    }
    
    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors WHERE specialization = ? AND available = TRUE ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, specialization);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Doctor doctor = extractDoctorFromResultSet(rs);
                    doctors.add(doctor);
                }
            }
            
            System.out.println("Found " + doctors.size() + " doctors for specialization: " + specialization);
            
        } catch (SQLException e) {
            System.err.println("Error retrieving doctors by specialization: " + e.getMessage());
            e.printStackTrace();
        }
        
        return doctors;
    }
    
    public List<String> getAllSpecializations() {
        List<String> specializations = new ArrayList<>();
        String sql = "SELECT DISTINCT specialization FROM doctors WHERE available = TRUE ORDER BY specialization";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                specializations.add(rs.getString("specialization"));
            }
            
            System.out.println("Found " + specializations.size() + " specializations");
            
        } catch (SQLException e) {
            System.err.println("Error retrieving specializations: " + e.getMessage());
            e.printStackTrace();
        }
        
        return specializations;
    }
    
    public boolean updateDoctor(Doctor doctor) {
        String sql = "UPDATE doctors SET name = ?, specialization = ?, phone = ?, email = ?, " +
                    "qualification = ?, experience_years = ?, consultation_fee = ?, available = ? " +
                    "WHERE doctor_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, doctor.getName());
            pstmt.setString(2, doctor.getSpecialization());
            pstmt.setString(3, doctor.getPhone());
            pstmt.setString(4, doctor.getEmail());
            pstmt.setString(5, doctor.getQualification());
            pstmt.setInt(6, doctor.getExperienceYears());
            pstmt.setDouble(7, doctor.getConsultationFee());
            pstmt.setBoolean(8, doctor.isAvailable());
            pstmt.setString(9, doctor.getDoctorId());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Doctor updated successfully: " + doctor.getDoctorId());
                return true;
            } else {
                System.err.println("Doctor not found for update: " + doctor.getDoctorId());
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating doctor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteDoctor(String doctorId) {
        // Check if doctor has appointments first
        if (hasAppointments(doctorId)) {
            System.err.println("Cannot delete doctor " + doctorId + " - has existing appointments");
            return false;
        }
        
        String sql = "DELETE FROM doctors WHERE doctor_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, doctorId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Doctor deleted successfully: " + doctorId);
                return true;
            } else {
                System.err.println("Doctor not found for deletion: " + doctorId);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting doctor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Doctor> searchDoctorsByName(String name) {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors WHERE name ILIKE ? ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + name + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Doctor doctor = extractDoctorFromResultSet(rs);
                    doctors.add(doctor);
                }
            }
            
            System.out.println("Found " + doctors.size() + " doctors matching: " + name);
            
        } catch (SQLException e) {
            System.err.println("Error searching doctors: " + e.getMessage());
            e.printStackTrace();
        }
        
        return doctors;
    }
    
    public List<Doctor> getDoctorsByExperience(int minYears) {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors WHERE experience_years >= ? AND available = TRUE ORDER BY experience_years DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, minYears);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Doctor doctor = extractDoctorFromResultSet(rs);
                    doctors.add(doctor);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting doctors by experience: " + e.getMessage());
            e.printStackTrace();
        }
        
        return doctors;
    }
    
    public List<Doctor> getDoctorsByFeeRange(double minFee, double maxFee) {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors WHERE consultation_fee BETWEEN ? AND ? AND available = TRUE ORDER BY consultation_fee";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, minFee);
            pstmt.setDouble(2, maxFee);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Doctor doctor = extractDoctorFromResultSet(rs);
                    doctors.add(doctor);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting doctors by fee range: " + e.getMessage());
            e.printStackTrace();
        }
        
        return doctors;
    }
    
    public DoctorStatistics getDoctorStatistics() {
        DoctorStatistics stats = new DoctorStatistics();
        String sql = "SELECT " +
                    "COUNT(*) as total, " +
                    "SUM(CASE WHEN available = TRUE THEN 1 ELSE 0 END) as available, " +
                    "AVG(experience_years) as avg_experience, " +
                    "AVG(consultation_fee) as avg_fee, " +
                    "MIN(consultation_fee) as min_fee, " +
                    "MAX(consultation_fee) as max_fee, " +
                    "(SELECT specialization FROM doctors GROUP BY specialization " +
                    "ORDER BY COUNT(*) DESC LIMIT 1) as most_common_specialization " +
                    "FROM doctors";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                stats.total = rs.getInt("total");
                stats.available = rs.getInt("available");
                stats.averageExperience = rs.getDouble("avg_experience");
                stats.averageFee = rs.getDouble("avg_fee");
                stats.minFee = rs.getDouble("min_fee");
                stats.maxFee = rs.getDouble("max_fee");
                stats.mostCommonSpecialization = rs.getString("most_common_specialization");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting doctor statistics: " + e.getMessage());
            e.printStackTrace();
        }
        
        return stats;
    }
    
    private Doctor extractDoctorFromResultSet(ResultSet rs) throws SQLException {
        Doctor doctor = new Doctor();
        doctor.setId(rs.getInt("id"));
        doctor.setDoctorId(rs.getString("doctor_id"));
        doctor.setName(rs.getString("name"));
        doctor.setSpecialization(rs.getString("specialization"));
        doctor.setPhone(rs.getString("phone"));
        doctor.setEmail(rs.getString("email"));
        doctor.setQualification(rs.getString("qualification"));
        doctor.setExperienceYears(rs.getInt("experience_years"));
        doctor.setConsultationFee(rs.getDouble("consultation_fee"));
        doctor.setAvailable(rs.getBoolean("available"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            doctor.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return doctor;
    }
    
    private boolean hasAppointments(String doctorId) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND status != 'Cancelled'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, doctorId);
            
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
    
    // Inner class for doctor statistics
    public static class DoctorStatistics {
        public int total;
        public int available;
        public double averageExperience;
        public double averageFee;
        public double minFee;
        public double maxFee;
        public String mostCommonSpecialization;
        
        @Override
        public String toString() {
            return String.format(
                "Doctor Statistics:\n" +
                "  Total Doctors: %d\n" +
                "  Available Doctors: %d (%.1f%%)\n" +
                "  Average Experience: %.1f years\n" +
                "  Consultation Fee:\n" +
                "    Average: ₹%.2f\n" +
                "    Range: ₹%.2f - ₹%.2f\n" +
                "  Most Common Specialization: %s",
                total,
                available, (total > 0 ? (available * 100.0 / total) : 0),
                averageExperience,
                averageFee,
                minFee, maxFee,
                mostCommonSpecialization != null ? mostCommonSpecialization : "N/A"
            );
        }
    }
}
