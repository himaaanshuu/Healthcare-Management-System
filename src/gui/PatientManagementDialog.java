package gui;

import model.Patient;
import dao.PatientDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PatientManagementDialog extends JDialog {
    private JTextField patientIdField, nameField, ageField, phoneField, emailField, 
                      bloodGroupField, emergencyContactField;
    private JComboBox<String> genderComboBox;
    private JTextArea addressArea;
    private JButton saveButton, cancelButton;
    private PatientDAO patientDAO;
    private MainFrame parentFrame;
    private boolean isEditMode = false;
    private Patient existingPatient;
    
    public PatientManagementDialog(MainFrame parent) {
        super(parent, "Add New Patient", true);
        this.parentFrame = parent;
        this.patientDAO = new PatientDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    public PatientManagementDialog(MainFrame parent, Patient patient) {
        this(parent);
        this.isEditMode = true;
        this.existingPatient = patient;
        setTitle("Edit Patient");
        populateFields(patient);
    }
    
    private void initializeComponents() {
        patientIdField = new JTextField(20);
        nameField = new JTextField(20);
        ageField = new JTextField(20);
        phoneField = new JTextField(20);
        emailField = new JTextField(20);
        bloodGroupField = new JTextField(20);
        emergencyContactField = new JTextField(20);
        
        String[] genders = {"Male", "Female", "Other"};
        genderComboBox = new JComboBox<>(genders);
        
        addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        
        saveButton = new JButton(isEditMode ? "Update" : "Save");
        cancelButton = new JButton("Cancel");
        
        if (!isEditMode) {
            patientIdField.setText(generatePatientId());
        }
    }
    
    private void setupLayout() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Patient ID
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Patient ID:"), gbc);
        gbc.gridx = 1;
        panel.add(patientIdField, gbc);
        patientIdField.setEditable(false);
        row++;
        
        // Name
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Name:*"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        row++;
        
        // Age and Gender
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Age:*"), gbc);
        gbc.gridx = 1;
        panel.add(ageField, gbc);
        
        gbc.gridx = 2; 
        panel.add(new JLabel("Gender:*"), gbc);
        gbc.gridx = 3;
        panel.add(genderComboBox, gbc);
        row++;
        
        // Phone
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Phone:*"), gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);
        row++;
        
        // Email
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(emailField, gbc);
        row++;
        
                // Blood Group
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Blood Group:"), gbc);
        gbc.gridx = 1;
        panel.add(bloodGroupField, gbc);
        row++;

        // Emergency Contact
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Emergency Contact:"), gbc);
        gbc.gridx = 1;
        panel.add(emergencyContactField, gbc);
        row++;

        // Address
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(addressArea), gbc);
        row++;

        // Buttons
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);

        add(panel);
    }

    private void setupEventHandlers() {
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePatient();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void populateFields(Patient patient) {
        patientIdField.setText(patient.getPatientId());
        nameField.setText(patient.getName());
        ageField.setText(String.valueOf(patient.getAge()));
        genderComboBox.setSelectedItem(patient.getGender());
        phoneField.setText(patient.getPhone());
        emailField.setText(patient.getEmail());
        addressArea.setText(patient.getAddress());
        bloodGroupField.setText(patient.getBloodGroup());
        emergencyContactField.setText(patient.getEmergencyContact());
    }

    private String generatePatientId() {
        return "PAT" + String.format("%03d", (int)(Math.random() * 1000));
    }

    private void savePatient() {
        try {
            // Validate inputs
            if (nameField.getText().trim().isEmpty() ||
                ageField.getText().trim().isEmpty() ||
                phoneField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create patient object
            Patient patient = new Patient(
                patientIdField.getText().trim(),
                nameField.getText().trim(),
                Integer.parseInt(ageField.getText().trim()),
                (String) genderComboBox.getSelectedItem(),
                phoneField.getText().trim(),
                emailField.getText().trim(),
                addressArea.getText().trim(),
                bloodGroupField.getText().trim(),
                emergencyContactField.getText().trim()
            );

            boolean success;
            if (isEditMode) {
                success = patientDAO.updatePatient(patient);
            } else {
                success = patientDAO.addPatient(patient);
            }

            if (success) {
                JOptionPane.showMessageDialog(this, 
                    isEditMode ? "Patient updated successfully!" : "Patient added successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                parentFrame.refreshAllTables();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    isEditMode ? "Failed to update patient!" : "Failed to add patient!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for age!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
       