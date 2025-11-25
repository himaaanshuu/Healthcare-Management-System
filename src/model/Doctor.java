package model;

import java.time.LocalDateTime;

public class Doctor {
    private int id;
    private String doctorId;
    private String name;
    private String specialization;
    private String phone;
    private String email;
    private String qualification;
    private int experienceYears;
    private double consultationFee;
    private boolean available;
    private LocalDateTime createdAt;
    
    // Constructors
    public Doctor() {}
    
    public Doctor(String doctorId, String name, String specialization, String phone, 
                  String email, String qualification, int experienceYears, double consultationFee) {
        this.doctorId = doctorId;
        this.name = name;
        this.specialization = specialization;
        this.phone = phone;
        this.email = email;
        this.qualification = qualification;
        this.experienceYears = experienceYears;
        this.consultationFee = consultationFee;
        this.available = true;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }
    
    public int getExperienceYears() { return experienceYears; }
    public void setExperienceYears(int experienceYears) { this.experienceYears = experienceYears; }
    
    public double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }
    
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    @Override
    public String toString() {
        return String.format("Dr. %s (%s)", name, specialization);
    }
}