package dao;

import model.Doctor;
import model.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDAO {
    
    public boolean addDoctor(Doctor doctor) {
        String sql = "INSERT INTO doctors (doctor_id, name, specialization, phone, email, qualification, experience_years, consultation_fee) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
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
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding doctor: " + e.getMessage());
            return false;
        }
    }
    
    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
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
                doctor.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                
                doctors.add(doctor);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving doctors: " + e.getMessage());
        }
        
        return doctors;
    }
    
    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors WHERE specialization = ? AND available = TRUE ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, specialization);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
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
                doctor.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                
                doctors.add(doctor);
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.err.println("Error retrieving doctors by specialization: " + e.getMessage());
        }
        
        return doctors;
    }
    
    public boolean updateDoctor(Doctor doctor) {
        String sql = "UPDATE doctors SET name = ?, specialization = ?, phone = ?, email = ?, qualification = ?, experience_years = ?, consultation_fee = ?, available = ? WHERE doctor_id = ?";
        
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
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating doctor: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteDoctor(String doctorId) {
        String sql = "DELETE FROM doctors WHERE doctor_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, doctorId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting doctor: " + e.getMessage());
            return false;
        }
    }
    
    // Get all specializations
    public List<String> getAllSpecializations() {
        List<String> specializations = new ArrayList<>();
        String sql = "SELECT DISTINCT specialization FROM doctors WHERE available = TRUE ORDER BY specialization";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                specializations.add(rs.getString("specialization"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving specializations: " + e.getMessage());
        }
        
        return specializations;
    }
}