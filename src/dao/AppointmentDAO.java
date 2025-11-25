package dao;

import model.Appointment;
import model.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;  // Add this import
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    
    public boolean addAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointments (appointment_id, patient_id, doctor_id, appointment_date, appointment_time, status, reason) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, appointment.getAppointmentId());
            pstmt.setString(2, appointment.getPatientId());
            pstmt.setString(3, appointment.getDoctorId());
            pstmt.setDate(4, Date.valueOf(appointment.getAppointmentDate()));
            pstmt.setTime(5, Time.valueOf(appointment.getAppointmentTime()));
            pstmt.setString(6, appointment.getStatus());
            pstmt.setString(7, appointment.getReason());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding appointment: " + e.getMessage());
            return false;
        }
    }
    
    public List<Appointment> getAllAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.name as patient_name, d.name as doctor_name " +
                    "FROM appointments a " +
                    "JOIN patients p ON a.patient_id = p.patient_id " +
                    "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                    "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(rs.getInt("id"));
                appointment.setAppointmentId(rs.getString("appointment_id"));
                appointment.setPatientId(rs.getString("patient_id"));
                appointment.setDoctorId(rs.getString("doctor_id"));
                appointment.setAppointmentDate(rs.getDate("appointment_date").toLocalDate());
                appointment.setAppointmentTime(rs.getTime("appointment_time").toLocalTime());
                appointment.setStatus(rs.getString("status"));
                appointment.setReason(rs.getString("reason"));
                appointment.setDiagnosis(rs.getString("diagnosis"));
                appointment.setPrescription(rs.getString("prescription"));
                appointment.setFee(rs.getDouble("fee"));
                appointment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                appointment.setPatientName(rs.getString("patient_name"));
                appointment.setDoctorName(rs.getString("doctor_name"));
                
                appointments.add(appointment);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving appointments: " + e.getMessage());
        }
        
        return appointments;
    }
    
    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.name as patient_name, d.name as doctor_name " +
                    "FROM appointments a " +
                    "JOIN patients p ON a.patient_id = p.patient_id " +
                    "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                    "WHERE a.appointment_date = ? " +
                    "ORDER BY a.appointment_time";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDate(1, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(rs.getInt("id"));
                appointment.setAppointmentId(rs.getString("appointment_id"));
                appointment.setPatientId(rs.getString("patient_id"));
                appointment.setDoctorId(rs.getString("doctor_id"));
                appointment.setAppointmentDate(rs.getDate("appointment_date").toLocalDate());
                appointment.setAppointmentTime(rs.getTime("appointment_time").toLocalTime());
                appointment.setStatus(rs.getString("status"));
                appointment.setReason(rs.getString("reason"));
                appointment.setDiagnosis(rs.getString("diagnosis"));
                appointment.setPrescription(rs.getString("prescription"));
                appointment.setFee(rs.getDouble("fee"));
                appointment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                appointment.setPatientName(rs.getString("patient_name"));
                appointment.setDoctorName(rs.getString("doctor_name"));
                
                appointments.add(appointment);
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.err.println("Error retrieving appointments by date: " + e.getMessage());
        }
        
        return appointments;
    }
    
    public boolean updateAppointmentStatus(String appointmentId, String status) {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setString(2, appointmentId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating appointment status: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateAppointmentDetails(Appointment appointment) {
        String sql = "UPDATE appointments SET diagnosis = ?, prescription = ?, fee = ?, status = ? WHERE appointment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, appointment.getDiagnosis());
            pstmt.setString(2, appointment.getPrescription());
            pstmt.setDouble(3, appointment.getFee());
            pstmt.setString(4, appointment.getStatus());
            pstmt.setString(5, appointment.getAppointmentId());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating appointment details: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteAppointment(String appointmentId) {
        String sql = "DELETE FROM appointments WHERE appointment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, appointmentId);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting appointment: " + e.getMessage());
            return false;
        }
    }
    
    // Check if time slot is available for a doctor
    public boolean isTimeSlotAvailable(String doctorId, LocalDate date, LocalTime time) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ? AND appointment_time = ? AND status != 'Cancelled'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, doctorId);
            pstmt.setDate(2, Date.valueOf(date));
            pstmt.setTime(3, Time.valueOf(time));
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
            
            rs.close();
            
        } catch (SQLException e) {
            System.err.println("Error checking time slot availability: " + e.getMessage());
        }
        
        return false;
    }
}