package model;

import java.time.LocalDateTime;

public class Patient {
    private int id;
    private String patientId;
    private String name;
    private int age;
    private String gender;
    private String phone;
    private String email;
    private String address;
    private String bloodGroup;
    private String emergencyContact;
    private LocalDateTime createdAt;
    
    // Constructors
    public Patient() {}
    
    public Patient(String patientId, String name, int age, String gender, String phone, 
                   String email, String address, String bloodGroup, String emergencyContact) {
        this.patientId = patientId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.bloodGroup = bloodGroup;
        this.emergencyContact = emergencyContact;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }
    
    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    @Override
    public String toString() {
        return String.format("Patient[ID=%s, Name=%s, Age=%d]", patientId, name, age);
    }
}