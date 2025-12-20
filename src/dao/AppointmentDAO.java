package dao;

import model.Appointment;
import model.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDAO {
    
    public boolean addAppointment(Appointment appointment) {
        // Check time slot availability first
        if (!isTimeSlotAvailable(appointment.getDoctorId(), 
                                 appointment.getAppointmentDate(), 
                                 appointment.getAppointmentTime())) {
            System.err.println("Time slot not available for doctor " + appointment.getDoctorId() + 
                              " on " + appointment.getAppointmentDate() + " at " + appointment.getAppointmentTime());
            return false;
        }
        
        String sql = "INSERT INTO appointments (appointment_id, patient_id, doctor_id, " +
                    "appointment_date, appointment_time, status, reason) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, appointment.getAppointmentId());
            pstmt.setString(2, appointment.getPatientId());
            pstmt.setString(3, appointment.getDoctorId());
            pstmt.setDate(4, Date.valueOf(appointment.getAppointmentDate()));
            pstmt.setTime(5, Time.valueOf(appointment.getAppointmentTime()));
            pstmt.setString(6, appointment.getStatus());
            pstmt.setString(7, appointment.getReason());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Appointment added successfully: " + appointment.getAppointmentId());
                return true;
            } else {
                System.err.println("Failed to add appointment: No rows affected");
                return false;
            }
            
        } catch (SQLException e) {
            // Handle specific SQL errors
            if (e.getSQLState() != null && e.getSQLState().equals("23505")) { // Unique constraint violation
                System.err.println("Appointment ID already exists: " + appointment.getAppointmentId());
                throw new RuntimeException("Appointment ID " + appointment.getAppointmentId() + " already exists.");
            } else if (e.getSQLState() != null && e.getSQLState().equals("23503")) { // Foreign key violation
                System.err.println("Foreign key violation: " + e.getMessage());
                throw new RuntimeException("Invalid patient or doctor ID");
            } else {
                System.err.println("Error adding appointment: " + e.getMessage());
                throw new RuntimeException("Database error: " + e.getMessage(), e);
            }
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
                Appointment appointment = extractAppointmentFromResultSet(rs);
                appointments.add(appointment);
            }
            
            System.out.println("Retrieved " + appointments.size() + " appointments from database");
            
        } catch (SQLException e) {
            System.err.println("Error retrieving appointments: " + e.getMessage());
            e.printStackTrace();
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
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Appointment appointment = extractAppointmentFromResultSet(rs);
                    appointments.add(appointment);
                }
            }
            
            System.out.println("Found " + appointments.size() + " appointments for date: " + date);
            
        } catch (SQLException e) {
            System.err.println("Error retrieving appointments by date: " + e.getMessage());
            e.printStackTrace();
        }
        
        return appointments;
    }
    
    public List<Appointment> getAppointmentsByDoctor(String doctorId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.name as patient_name, d.name as doctor_name " +
                    "FROM appointments a " +
                    "JOIN patients p ON a.patient_id = p.patient_id " +
                    "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                    "WHERE a.doctor_id = ? " +
                    "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, doctorId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Appointment appointment = extractAppointmentFromResultSet(rs);
                    appointments.add(appointment);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving appointments by doctor: " + e.getMessage());
            e.printStackTrace();
        }
        
        return appointments;
    }
    
    public List<Appointment> getAppointmentsByPatient(String patientId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.name as patient_name, d.name as doctor_name " +
                    "FROM appointments a " +
                    "JOIN patients p ON a.patient_id = p.patient_id " +
                    "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                    "WHERE a.patient_id = ? " +
                    "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, patientId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Appointment appointment = extractAppointmentFromResultSet(rs);
                    appointments.add(appointment);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving appointments by patient: " + e.getMessage());
            e.printStackTrace();
        }
        
        return appointments;
    }
    
    public List<Appointment> getAppointmentsByStatus(String status) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.name as patient_name, d.name as doctor_name " +
                    "FROM appointments a " +
                    "JOIN patients p ON a.patient_id = p.patient_id " +
                    "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                    "WHERE a.status = ? " +
                    "ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Appointment appointment = extractAppointmentFromResultSet(rs);
                    appointments.add(appointment);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving appointments by status: " + e.getMessage());
            e.printStackTrace();
        }
        
        return appointments;
    }
    
    public Appointment getAppointmentById(String appointmentId) {
        String sql = "SELECT a.*, p.name as patient_name, d.name as doctor_name " +
                    "FROM appointments a " +
                    "JOIN patients p ON a.patient_id = p.patient_id " +
                    "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                    "WHERE a.appointment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, appointmentId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Appointment appointment = extractAppointmentFromResultSet(rs);
                    System.out.println("Found appointment: " + appointmentId);
                    return appointment;
                } else {
                    System.out.println("Appointment not found: " + appointmentId);
                    return null;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving appointment: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean updateAppointmentStatus(String appointmentId, String status) {
        String sql = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setString(2, appointmentId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Appointment status updated successfully: " + appointmentId);
                return true;
            } else {
                System.err.println("Appointment not found for status update: " + appointmentId);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating appointment status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateAppointmentDetails(Appointment appointment) {
        // FIXED: Now updates ALL fields including date and time
        String sql = "UPDATE appointments SET " +
                    "patient_id = ?, " +
                    "doctor_id = ?, " +
                    "appointment_date = ?, " +
                    "appointment_time = ?, " +
                    "reason = ?, " +
                    "status = ?, " +
                    "diagnosis = ?, " +
                    "prescription = ?, " +
                    "fee = ? " +
                    "WHERE appointment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, appointment.getPatientId());
            pstmt.setString(2, appointment.getDoctorId());
            pstmt.setDate(3, Date.valueOf(appointment.getAppointmentDate()));
            pstmt.setTime(4, Time.valueOf(appointment.getAppointmentTime()));
            pstmt.setString(5, appointment.getReason());
            pstmt.setString(6, appointment.getStatus());
            pstmt.setString(7, appointment.getDiagnosis());
            pstmt.setString(8, appointment.getPrescription());
            pstmt.setDouble(9, appointment.getFee());
            pstmt.setString(10, appointment.getAppointmentId());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Appointment FULL details updated successfully: " + appointment.getAppointmentId());
                System.out.println("Updated date to: " + appointment.getAppointmentDate());
                System.out.println("Updated time to: " + appointment.getAppointmentTime());
                return true;
            } else {
                System.err.println("Appointment not found for details update: " + appointment.getAppointmentId());
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating appointment details: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteAppointment(String appointmentId) {
        String sql = "DELETE FROM appointments WHERE appointment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, appointmentId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Appointment deleted successfully: " + appointmentId);
                return true;
            } else {
                System.err.println("Appointment not found for deletion: " + appointmentId);
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting appointment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean isTimeSlotAvailable(String doctorId, LocalDate date, LocalTime time) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ? AND appointment_time = ? AND status != 'Cancelled'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, doctorId);
            pstmt.setDate(2, Date.valueOf(date));
            pstmt.setTime(3, Time.valueOf(time));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    boolean available = count == 0;
                    System.out.println("Time slot available for doctor " + doctorId + " on " + date + " at " + time + ": " + available);
                    return available;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking time slot availability: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    public List<LocalTime> getAvailableTimeSlots(String doctorId, LocalDate date) {
        List<LocalTime> availableSlots = new ArrayList<>();
        
        // Business hours: 9 AM to 5 PM in 30-minute intervals
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(17, 0);
        
        LocalTime currentTime = startTime;
        while (!currentTime.isAfter(endTime.minusMinutes(30))) {
            if (isTimeSlotAvailable(doctorId, date, currentTime)) {
                availableSlots.add(currentTime);
            }
            currentTime = currentTime.plusMinutes(30);
        }
        
        System.out.println("Found " + availableSlots.size() + " available time slots for doctor " + doctorId + " on " + date);
        return availableSlots;
    }
    
    // Validate patient-doctor relationship
    public boolean validateAppointment(String patientId, String doctorId) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE patient_id = ? AND doctor_id = ? " +
                    "AND appointment_date = CURRENT_DATE AND status IN ('Scheduled', 'Completed')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, patientId);
            pstmt.setString(2, doctorId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    boolean valid = count == 0; // No existing appointment today
                    System.out.println("Appointment validation for patient " + patientId + " with doctor " + doctorId + ": " + valid);
                    return valid;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Validation error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    // Get appointment statistics
    public AppointmentStatistics getAppointmentStatistics() {
        AppointmentStatistics stats = new AppointmentStatistics();
        String sql = "SELECT " +
                    "COUNT(*) as total, " +
                    "SUM(CASE WHEN status = 'Scheduled' THEN 1 ELSE 0 END) as scheduled, " +
                    "SUM(CASE WHEN status = 'Completed' THEN 1 ELSE 0 END) as completed, " +
                    "SUM(CASE WHEN status = 'Cancelled' THEN 1 ELSE 0 END) as cancelled, " +
                    "SUM(CASE WHEN status = 'No-Show' THEN 1 ELSE 0 END) as no_show, " +
                    "AVG(fee) as avg_fee, " +
                    "SUM(fee) as total_fee " +
                    "FROM appointments WHERE fee > 0";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                stats.total = rs.getInt("total");
                stats.scheduled = rs.getInt("scheduled");
                stats.completed = rs.getInt("completed");
                stats.cancelled = rs.getInt("cancelled");
                stats.noShow = rs.getInt("no_show");
                stats.averageFee = rs.getDouble("avg_fee");
                stats.totalFee = rs.getDouble("total_fee");
            }
            
            System.out.println("Appointment statistics retrieved: " + stats.total + " total appointments");
            
        } catch (SQLException e) {
            System.err.println("Error getting statistics: " + e.getMessage());
            e.printStackTrace();
        }
        
        return stats;
    }
    
    // Get today's appointments count
    public int getTodaysAppointmentsCount() {
        String sql = "SELECT COUNT(*) FROM appointments WHERE appointment_date = CURRENT_DATE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting today's appointments count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    // Get upcoming appointments (next 7 days)
    public List<Appointment> getUpcomingAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT a.*, p.name as patient_name, d.name as doctor_name " +
                    "FROM appointments a " +
                    "JOIN patients p ON a.patient_id = p.patient_id " +
                    "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                    "WHERE a.appointment_date >= CURRENT_DATE AND a.appointment_date <= CURRENT_DATE + INTERVAL '7 days' " +
                    "AND a.status = 'Scheduled' " +
                    "ORDER BY a.appointment_date, a.appointment_time";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Appointment appointment = extractAppointmentFromResultSet(rs);
                appointments.add(appointment);
            }
            
            System.out.println("Found " + appointments.size() + " upcoming appointments");
            
        } catch (SQLException e) {
            System.err.println("Error retrieving upcoming appointments: " + e.getMessage());
            e.printStackTrace();
        }
        
        return appointments;
    }
    
    private Appointment extractAppointmentFromResultSet(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        
        // Set all appointment fields - REMOVED setId() if your Appointment doesn't have id field
        appointment.setAppointmentId(rs.getString("appointment_id"));
        appointment.setPatientId(rs.getString("patient_id"));
        appointment.setDoctorId(rs.getString("doctor_id"));
        appointment.setAppointmentDate(rs.getDate("appointment_date").toLocalDate());
        appointment.setAppointmentTime(rs.getTime("appointment_time").toLocalTime());
        appointment.setStatus(rs.getString("status"));
        appointment.setReason(rs.getString("reason"));
        
        // Optional fields (may be null)
        String diagnosis = rs.getString("diagnosis");
        if (diagnosis != null) {
            appointment.setDiagnosis(diagnosis);
        }
        
        String prescription = rs.getString("prescription");
        if (prescription != null) {
            appointment.setPrescription(prescription);
        }
        
        appointment.setFee(rs.getDouble("fee"));
        
        // Set patient and doctor names if available
        try {
            appointment.setPatientName(rs.getString("patient_name"));
            appointment.setDoctorName(rs.getString("doctor_name"));
        } catch (SQLException e) {
            // These columns might not exist in all queries, ignore if not present
        }
        
        return appointment;
    }
    
    // Inner class for appointment statistics
    public static class AppointmentStatistics {
        public int total;
        public int scheduled;
        public int completed;
        public int cancelled;
        public int noShow;
        public double averageFee;
        public double totalFee;
        
        @Override
        public String toString() {
            double scheduledPercent = total > 0 ? (scheduled * 100.0 / total) : 0;
            double completedPercent = total > 0 ? (completed * 100.0 / total) : 0;
            double cancelledPercent = total > 0 ? (cancelled * 100.0 / total) : 0;
            double noShowPercent = total > 0 ? (noShow * 100.0 / total) : 0;
            
            return String.format(
                "Appointment Statistics:\n" +
                "  Total Appointments: %d\n" +
                "  Status Distribution:\n" +
                "    Scheduled: %d (%.1f%%)\n" +
                "    Completed: %d (%.1f%%)\n" +
                "    Cancelled: %d (%.1f%%)\n" +
                "    No-Show: %d (%.1f%%)\n" +
                "  Financial:\n" +
                "    Average Fee: ₹%.2f\n" +
                "    Total Revenue: ₹%.2f",
                total,
                scheduled, scheduledPercent,
                completed, completedPercent,
                cancelled, cancelledPercent,
                noShow, noShowPercent,
                averageFee,
                totalFee
            );
        }
    }
}
