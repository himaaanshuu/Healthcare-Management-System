package gui;

import model.Doctor;
import dao.DoctorDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DoctorManagementDialog extends JDialog {
    private JTextField doctorIdField, nameField, specializationField, phoneField, 
                      emailField, qualificationField, experienceField, feeField;
    private JCheckBox availableCheckBox;
    private JButton saveButton, cancelButton;
    private DoctorDAO doctorDAO;
    private MainFrame parentFrame;
    private boolean isEditMode = false;
    private Doctor existingDoctor;

    public DoctorManagementDialog(MainFrame parent) {
        super(parent, "Add New Doctor", true);
        this.parentFrame = parent;
        this.doctorDAO = new DoctorDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    public DoctorManagementDialog(MainFrame parent, Doctor doctor) {
        this(parent);
        this.isEditMode = true;
        this.existingDoctor = doctor;
        setTitle("Edit Doctor");
        populateFields(doctor);
    }

    private void initializeComponents() {
        doctorIdField = new JTextField(20);
        nameField = new JTextField(20);
        specializationField = new JTextField(20);
        phoneField = new JTextField(20);
        emailField = new JTextField(20);
        qualificationField = new JTextField(20);
        experienceField = new JTextField(20);
        feeField = new JTextField(20);
        
        availableCheckBox = new JCheckBox("Available for appointments");
        availableCheckBox.setSelected(true);
        
        saveButton = new JButton(isEditMode ? "Update" : "Save");
        cancelButton = new JButton("Cancel");

        if (!isEditMode) {
            doctorIdField.setText(generateDoctorId());
        }
    }

    private void setupLayout() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Doctor ID
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Doctor ID:"), gbc);
        gbc.gridx = 1;
        panel.add(doctorIdField, gbc);
        doctorIdField.setEditable(false);
        row++;

        // Name
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Name:*"), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);
        row++;

        // Specialization
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Specialization:*"), gbc);
        gbc.gridx = 1;
        panel.add(specializationField, gbc);
        row++;

        // Phone and Email
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Phone:*"), gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 3;
        panel.add(emailField, gbc);
        row++;

        // Qualification
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Qualification:*"), gbc);
        gbc.gridx = 1;
        panel.add(qualificationField, gbc);
        row++;

        // Experience and Fee
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Experience (years):*"), gbc);
        gbc.gridx = 1;
        panel.add(experienceField, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Consultation Fee:*"), gbc);
        gbc.gridx = 3;
        panel.add(feeField, gbc);
        row++;

        // Available Checkbox
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(availableCheckBox, gbc);
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
                saveDoctor();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void populateFields(Doctor doctor) {
        doctorIdField.setText(doctor.getDoctorId());
        nameField.setText(doctor.getName());
        specializationField.setText(doctor.getSpecialization());
        phoneField.setText(doctor.getPhone());
        emailField.setText(doctor.getEmail());
        qualificationField.setText(doctor.getQualification());
        experienceField.setText(String.valueOf(doctor.getExperienceYears()));
        feeField.setText(String.valueOf(doctor.getConsultationFee()));
        availableCheckBox.setSelected(doctor.isAvailable());
    }

    private String generateDoctorId() {
        return "DOC" + String.format("%03d", (int)(Math.random() * 1000));
    }

    private void saveDoctor() {
        try {
            // Validate inputs
            if (nameField.getText().trim().isEmpty() ||
                specializationField.getText().trim().isEmpty() ||
                phoneField.getText().trim().isEmpty() ||
                qualificationField.getText().trim().isEmpty() ||
                experienceField.getText().trim().isEmpty() ||
                feeField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Create doctor object
            Doctor doctor = new Doctor(
                doctorIdField.getText().trim(),
                nameField.getText().trim(),
                specializationField.getText().trim(),
                phoneField.getText().trim(),
                emailField.getText().trim(),
                qualificationField.getText().trim(),
                Integer.parseInt(experienceField.getText().trim()),
                Double.parseDouble(feeField.getText().trim())
            );
            doctor.setAvailable(availableCheckBox.isSelected());

            boolean success;
            if (isEditMode) {
                success = doctorDAO.updateDoctor(doctor);
            } else {
                success = doctorDAO.addDoctor(doctor);
            }

            if (success) {
                JOptionPane.showMessageDialog(this, 
                    isEditMode ? "Doctor updated successfully!" : "Doctor added successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                parentFrame.refreshAllTables();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    isEditMode ? "Failed to update doctor!" : "Failed to add doctor!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for experience and fee!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}