package utils;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.util.regex.Pattern;
import java.time.LocalDate;
import java.time.LocalTime;

public class ValidationUtils {
    
    // Regular expressions for validation
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String PHONE_REGEX = "^[0-9]{10}$";
    private static final String NAME_REGEX = "^[a-zA-Z\\s.'-]{2,100}$";
    private static final String ID_REGEX = "^[A-Z]{3}[0-9]{3}$";
    private static final String BLOOD_GROUP_REGEX = "^(A|B|AB|O)[+-]$";
    
    // Compiled patterns for better performance
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_REGEX);
    private static final Pattern ID_PATTERN = Pattern.compile(ID_REGEX);
    private static final Pattern BLOOD_GROUP_PATTERN = Pattern.compile(BLOOD_GROUP_REGEX, Pattern.CASE_INSENSITIVE);
    
    // Validation methods with detailed error messages
    
    public static ValidationResult isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return new ValidationResult(true, ""); // Email is optional
        }
        
        boolean isValid = EMAIL_PATTERN.matcher(email.trim()).matches();
        String message = isValid ? "" : "Invalid email format. Please use format: user@domain.com";
        return new ValidationResult(isValid, message);
    }
    
    public static ValidationResult isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return new ValidationResult(false, "Phone number is required");
        }
        
        // Remove any non-digit characters
        String cleanPhone = phone.replaceAll("[^0-9]", "");
        
        if (cleanPhone.length() != 10) {
            return new ValidationResult(false, "Phone number must be 10 digits");
        }
        
        boolean isValid = PHONE_PATTERN.matcher(cleanPhone).matches();
        String message = isValid ? "" : "Invalid phone number. Must be 10 digits.";
        return new ValidationResult(isValid, message);
    }
    
    public static ValidationResult isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ValidationResult(false, "Name is required");
        }
        
        String trimmedName = name.trim();
        if (trimmedName.length() < 2 || trimmedName.length() > 100) {
            return new ValidationResult(false, "Name must be between 2 and 100 characters");
        }
        
        boolean isValid = NAME_PATTERN.matcher(trimmedName).matches();
        String message = isValid ? "" : "Invalid name format. Use only letters, spaces, and basic punctuation.";
        return new ValidationResult(isValid, message);
    }
    
    public static ValidationResult isValidAge(int age) {
        if (age <= 0) {
            return new ValidationResult(false, "Age must be greater than 0");
        }
        if (age > 150) {
            return new ValidationResult(false, "Age must be less than 150");
        }
        return new ValidationResult(true, "");
    }
    
    public static ValidationResult isValidId(String id, String type) {
        if (id == null || id.trim().isEmpty()) {
            return new ValidationResult(false, type + " ID is required");
        }
        
        boolean isValid = ID_PATTERN.matcher(id.trim()).matches();
        String message = isValid ? "" : "Invalid " + type + " ID format. Must be like: " + 
                           (type.equals("Patient") ? "PAT001" : type.equals("Doctor") ? "DOC001" : "APT001");
        return new ValidationResult(isValid, message);
    }
    
    public static ValidationResult isValidBloodGroup(String bloodGroup) {
        if (bloodGroup == null || bloodGroup.trim().isEmpty()) {
            return new ValidationResult(true, ""); // Optional field
        }
        
        boolean isValid = BLOOD_GROUP_PATTERN.matcher(bloodGroup.trim()).matches();
        String message = isValid ? "" : "Invalid blood group. Valid formats: A+, A-, B+, B-, O+, O-, AB+, AB-";
        return new ValidationResult(isValid, message);
    }
    
    public static ValidationResult isValidDate(LocalDate date) {
        if (date == null) {
            return new ValidationResult(false, "Date is required");
        }
        
        LocalDate today = LocalDate.now();
        if (date.isBefore(today)) {
            return new ValidationResult(false, "Cannot schedule appointments in the past");
        }
        
        if (date.isAfter(today.plusYears(1))) {
            return new ValidationResult(false, "Cannot schedule appointments more than 1 year in advance");
        }
        
        return new ValidationResult(true, "");
    }
    
    public static ValidationResult isValidTime(LocalTime time) {
        if (time == null) {
            return new ValidationResult(false, "Time is required");
        }
        
        // Business hours: 9 AM to 5 PM
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(17, 0);
        
        if (time.isBefore(startTime) || time.isAfter(endTime)) {
            return new ValidationResult(false, "Appointments only available between 9:00 AM and 5:00 PM");
        }
        
        // Check if time is in 30-minute intervals
        if (time.getMinute() % 30 != 0) {
            return new ValidationResult(false, "Appointments must be scheduled in 30-minute intervals");
        }
        
        return new ValidationResult(true, "");
    }
    
    public static ValidationResult isValidFee(double fee) {
        if (fee < 0) {
            return new ValidationResult(false, "Fee cannot be negative");
        }
        
        if (fee > 10000) {
            return new ValidationResult(false, "Fee cannot exceed 10,000");
        }
        
        return new ValidationResult(true, "");
    }
    
    public static ValidationResult isValidExperience(int experience) {
        if (experience < 0) {
            return new ValidationResult(false, "Experience cannot be negative");
        }
        
        if (experience > 50) {
            return new ValidationResult(false, "Experience cannot exceed 50 years");
        }
        
        return new ValidationResult(true, "");
    }
    
    // Helper method to validate text field
    public static boolean validateTextField(JTextComponent field, ValidationResult result) {
        if (!result.isValid()) {
            showValidationError(field, result.getMessage());
            field.requestFocus();
            if (field instanceof JTextField) {
                ((JTextField) field).selectAll();
            }
            return false;
        }
        return true;
    }
    
    // Show validation error
    public static void showValidationError(JComponent component, String message) {
        JOptionPane.showMessageDialog(component, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
    
    // Show validation success
    public static void showValidationSuccess(JComponent component, String message) {
        JOptionPane.showMessageDialog(component, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Validate all fields in a form
    public static boolean validateForm(JComponent parent, ValidationResult... results) {
        for (ValidationResult result : results) {
            if (!result.isValid()) {
                showValidationError(parent, result.getMessage());
                return false;
            }
        }
        return true;
    }
    
    // Sanitize input to prevent SQL injection
    public static String sanitizeInput(String input) {
        if (input == null) return "";
        
        // Remove SQL keywords and special characters
        String sanitized = input.trim()
            .replace("'", "''")
            .replace(";", "")
            .replace("--", "")
            .replace("/*", "")
            .replace("*/", "");
        
        // Limit length
        if (sanitized.length() > 1000) {
            sanitized = sanitized.substring(0, 1000);
        }
        
        return sanitized;
    }
    
    // Format phone number for display
    public static String formatPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) return "";
        
        String cleanPhone = phone.replaceAll("[^0-9]", "");
        if (cleanPhone.length() == 10) {
            return "(" + cleanPhone.substring(0, 3) + ") " + 
                   cleanPhone.substring(3, 6) + "-" + 
                   cleanPhone.substring(6);
        }
        return phone;
    }
    
    // Inner class for validation results
    public static class ValidationResult {
        private final boolean valid;
        private final String message;
        
        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getMessage() {
            return message;
        }
    }
}
