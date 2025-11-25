package gui;

import model.Appointment;
import model.Patient;
import model.Doctor;
import dao.AppointmentDAO;
import dao.PatientDAO;
import dao.DoctorDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class AppointmentDialog extends JDialog {
    private JTextField appointmentIdField, dateField, timeField;
    private JComboBox<String> patientComboBox, doctorComboBox, statusComboBox;
    private JTextArea reasonArea, diagnosisArea, prescriptionArea;
    private JTextField feeField;
    private JButton saveButton, cancelButton;
    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;
    private DoctorDAO doctorDAO;
    private MainFrame parentFrame;
    private boolean isEditMode = false;
    private Appointment existingAppointment;

    public AppointmentDialog(MainFrame parent) {
        super(parent, "Schedule New Appointment", true);
        this.parentFrame = parent;
        this.appointmentDAO = new AppointmentDAO();
        this.patientDAO = new PatientDAO();
        this.doctorDAO = new DoctorDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadComboBoxData();
        pack();
        setLocationRelativeTo(parent);
        setSize(500, 400);
    }

    public AppointmentDialog(MainFrame parent, Appointment appointment) {
        this(parent);
        this.isEditMode = true;
        this.existingAppointment = appointment;
        setTitle("Edit Appointment");
        populateFields(appointment);
    }

    private void initializeComponents() {
        appointmentIdField = new JTextField(15);
        dateField = new JTextField(15);
        timeField = new JTextField(15);
        patientComboBox = new JComboBox<>();
        doctorComboBox = new JComboBox<>();
        
        String[] statuses = {"Scheduled", "Completed", "Cancelled", "No-Show"};
        statusComboBox = new JComboBox<>(statuses);
        
        reasonArea = new JTextArea(3, 20);
        diagnosisArea = new JTextArea(3, 20);
        prescriptionArea = new JTextArea(3, 20);
        feeField = new JTextField(15);
        
        reasonArea.setLineWrap(true);
        diagnosisArea.setLineWrap(true);
        prescriptionArea.setLineWrap(true);
        
        saveButton = new JButton(isEditMode ? "Update" : "Schedule");
        cancelButton = new JButton("Cancel");

        if (!isEditMode) {
            appointmentIdField.setText(generateAppointmentId());
            dateField.setText(LocalDate.now().toString());
            timeField.setText("09:00");
        }
    }

    private void setupLayout() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Appointment ID
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Appointment ID:"), gbc);
        gbc.gridx = 1;
        panel.add(appointmentIdField, gbc);
        appointmentIdField.setEditable(false);
        row++;

        // Patient and Doctor
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Patient:*"), gbc);
        gbc.gridx = 1;
        panel.add(patientComboBox, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Doctor:*"), gbc);
        gbc.gridx = 3;
        panel.add(doctorComboBox, gbc);
        row++;

        // Date and Time
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Date (YYYY-MM-DD):*"), gbc);
        gbc.gridx = 1;
        panel.add(dateField, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Time (HH:MM):*"), gbc);
        gbc.gridx = 3;
        panel.add(timeField, gbc);
        row++;

        // Status
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        panel.add(statusComboBox, gbc);
        row++;

        // Reason
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Reason:*"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(reasonArea), gbc);
        row++;

        if (isEditMode) {
            // Diagnosis
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(new JLabel("Diagnosis:"), gbc);
            gbc.gridx = 1;
            gbc.gridwidth = 3;
            panel.add(new JScrollPane(diagnosisArea), gbc);
            row++;

            // Prescription
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(new JLabel("Prescription:"), gbc);
            gbc.gridx = 1;
            gbc.gridwidth = 3;
            panel.add(new JScrollPane(prescriptionArea), gbc);
            row++;

            // Fee
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(new JLabel("Fee:"), gbc);
            gbc.gridx = 1;
            gbc.gridwidth = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            panel.add(feeField, gbc);
            row++;
        }

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
                saveAppointment();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void loadComboBoxData() {
        // Load patients
        List<Patient> patients = patientDAO.getAllPatients();
        for (Patient patient : patients) {
            patientComboBox.addItem(patient.getPatientId() + " - " + patient.getName());
        }

        // Load doctors
        List<Doctor> doctors = doctorDAO.getAllDoctors();
        for (Doctor doctor : doctors) {
            if (doctor.isAvailable()) {
                doctorComboBox.addItem(doctor.getDoctorId() + " - " + doctor.getName() + " (" + doctor.getSpecialization() + ")");
            }
        }
    }

    private void populateFields(Appointment appointment) {
        appointmentIdField.setText(appointment.getAppointmentId());
        
        // Set patient
        for (int i = 0; i < patientComboBox.getItemCount(); i++) {
            if (patientComboBox.getItemAt(i).toString().startsWith(appointment.getPatientId())) {
                patientComboBox.setSelectedIndex(i);
                break;
            }
        }
        
        // Set doctor
        for (int i = 0; i < doctorComboBox.getItemCount(); i++) {
            if (doctorComboBox.getItemAt(i).toString().startsWith(appointment.getDoctorId())) {
                doctorComboBox.setSelectedIndex(i);
                break;
            }
        }
        
        dateField.setText(appointment.getAppointmentDate().toString());
        timeField.setText(appointment.getAppointmentTime().toString());
        statusComboBox.setSelectedItem(appointment.getStatus());
        reasonArea.setText(appointment.getReason());
        
        if (appointment.getDiagnosis() != null) {
            diagnosisArea.setText(appointment.getDiagnosis());
        }
        if (appointment.getPrescription() != null) {
            prescriptionArea.setText(appointment.getPrescription());
        }
        if (appointment.getFee() > 0) {
            feeField.setText(String.valueOf(appointment.getFee()));
        }
    }

    private String generateAppointmentId() {
        return "APT" + String.format("%03d", (int)(Math.random() * 1000));
    }

    private void saveAppointment() {
        try {
            // Validate inputs
            if (patientComboBox.getSelectedItem() == null ||
                doctorComboBox.getSelectedItem() == null ||
                dateField.getText().trim().isEmpty() ||
                timeField.getText().trim().isEmpty() ||
                reasonArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Extract IDs from combo boxes
            String patientId = patientComboBox.getSelectedItem().toString().split(" - ")[0];
            String doctorId = doctorComboBox.getSelectedItem().toString().split(" - ")[0];

            if (isEditMode) {
                // Update existing appointment
                existingAppointment.setDiagnosis(diagnosisArea.getText().trim());
                existingAppointment.setPrescription(prescriptionArea.getText().trim());
                existingAppointment.setStatus(statusComboBox.getSelectedItem().toString());
                
                if (!feeField.getText().trim().isEmpty()) {
                    existingAppointment.setFee(Double.parseDouble(feeField.getText().trim()));
                }

                boolean success = appointmentDAO.updateAppointmentDetails(existingAppointment);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Appointment updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    parentFrame.refreshAllTables();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update appointment!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Create new appointment
                Appointment appointment = new Appointment(
                    appointmentIdField.getText().trim(),
                    patientId,
                    doctorId,
                    LocalDate.parse(dateField.getText().trim()),
                    LocalTime.parse(timeField.getText().trim()),
                    reasonArea.getText().trim()
                );

                boolean success = appointmentDAO.addAppointment(appointment);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Appointment scheduled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    parentFrame.refreshAllTables();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to schedule appointment!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}