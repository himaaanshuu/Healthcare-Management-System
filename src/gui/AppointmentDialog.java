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
import java.util.concurrent.ExecutionException;

public class AppointmentDialog extends JDialog {
    private JTextField appointmentIdField, dateField, timeField;
    private JComboBox<String> patientComboBox, doctorComboBox, statusComboBox;
    private JTextArea reasonArea, diagnosisArea, prescriptionArea;
    private JTextField feeField;
    private JButton saveButton, cancelButton;
    private JProgressBar progressBar;
    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;
    private DoctorDAO doctorDAO;
    private MainFrame parentFrame;
    private boolean isEditMode = false;
    // @SuppressWarnings("unused")
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
        loadComboBoxDataInBackground();
        pack();
        setLocationRelativeTo(parent);
        setSize(500, 450);
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
        
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setStringPainted(true);

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

        // Progress bar
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(progressBar, gbc);
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
                saveAppointmentInBackground();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void loadComboBoxDataInBackground() {
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                publish("Loading patients...");
                List<Patient> patients = patientDAO.getAllPatients();
                SwingUtilities.invokeLater(() -> {
                    for (Patient patient : patients) {
                        patientComboBox.addItem(patient.getPatientId() + " - " + patient.getName());
                    }
                });
                
                publish("Loading doctors...");
                List<Doctor> doctors = doctorDAO.getAllDoctors();
                SwingUtilities.invokeLater(() -> {
                    for (Doctor doctor : doctors) {
                        if (doctor.isAvailable()) {
                            doctorComboBox.addItem(doctor.getDoctorId() + " - " + doctor.getName() + 
                                                  " (" + doctor.getSpecialization() + ")");
                        }
                    }
                });
                
                return null;
            }
            
            @Override
            protected void process(List<String> chunks) {
                if (!chunks.isEmpty()) {
                    progressBar.setVisible(true);
                    progressBar.setString(chunks.get(chunks.size() - 1));
                }
            }
            
            @Override
            protected void done() {
                progressBar.setVisible(false);
                progressBar.setString("");
                saveButton.setEnabled(true);
                patientComboBox.setEnabled(true);
                doctorComboBox.setEnabled(true);
            }
        };
        
        progressBar.setVisible(true);
        progressBar.setString("Loading data...");
        saveButton.setEnabled(false);
        patientComboBox.setEnabled(false);
        doctorComboBox.setEnabled(false);
        worker.execute();
    }

    private void populateFields(Appointment appointment) {
        // Debug: Show what date/time is loaded
        System.out.println("DEBUG: Loading appointment - ID: " + appointment.getAppointmentId() + 
                         ", Date: " + appointment.getAppointmentDate() + 
                         ", Time: " + appointment.getAppointmentTime());
        
        appointmentIdField.setText(appointment.getAppointmentId());
        
        // Set patient - needs to wait for combo box data to load
        SwingWorker<Void, Void> patientWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Wait for combo box to be populated
                while (patientComboBox.getItemCount() == 0) {
                    Thread.sleep(100);
                }
                
                SwingUtilities.invokeLater(() -> {
                    for (int i = 0; i < patientComboBox.getItemCount(); i++) {
                        if (patientComboBox.getItemAt(i).toString().startsWith(appointment.getPatientId())) {
                            patientComboBox.setSelectedIndex(i);
                            break;
                        }
                    }
                });
                return null;
            }
        };
        
        // Set doctor - needs to wait for combo box data to load
        SwingWorker<Void, Void> doctorWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Wait for combo box to be populated
                while (doctorComboBox.getItemCount() == 0) {
                    Thread.sleep(100);
                }
                
                SwingUtilities.invokeLater(() -> {
                    for (int i = 0; i < doctorComboBox.getItemCount(); i++) {
                        if (doctorComboBox.getItemAt(i).toString().startsWith(appointment.getDoctorId())) {
                            doctorComboBox.setSelectedIndex(i);
                            break;
                        }
                    }
                });
                return null;
            }
        };
        
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
        
        patientWorker.execute();
        doctorWorker.execute();
    }

    private String generateAppointmentId() {
        return "APT" + String.format("%03d", (int)(Math.random() * 1000));
    }

    private void saveAppointmentInBackground() {
        // Validate inputs first (this is quick, can be on EDT)
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
            // Update existing appointment in background
            SwingWorker<Boolean, String> worker = new SwingWorker<Boolean, String>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    publish("Updating appointment...");
                    
                    // Debug: Show what values we're trying to set
                    System.out.println("DEBUG: Setting date to: " + dateField.getText().trim());
                    System.out.println("DEBUG: Setting time to: " + timeField.getText().trim());
                    
                    // CRITICAL FIX: Update ALL fields on the existing appointment
                    existingAppointment.setPatientId(patientId);
                    existingAppointment.setDoctorId(doctorId);
                    
                    // Parse and set new date and time (THIS WAS MISSING!)
                    try {
                        LocalDate newDate = LocalDate.parse(dateField.getText().trim());
                        LocalTime newTime = LocalTime.parse(timeField.getText().trim());
                        existingAppointment.setAppointmentDate(newDate);
                        existingAppointment.setAppointmentTime(newTime);
                    } catch (Exception e) {
                        throw new RuntimeException("Invalid date/time format. Please use YYYY-MM-DD for date and HH:MM for time.");
                    }
                    
                    existingAppointment.setReason(reasonArea.getText().trim());
                    existingAppointment.setStatus(statusComboBox.getSelectedItem().toString());
                    existingAppointment.setDiagnosis(diagnosisArea.getText().trim());
                    existingAppointment.setPrescription(prescriptionArea.getText().trim());
                    
                    if (!feeField.getText().trim().isEmpty()) {
                        try {
                            existingAppointment.setFee(Double.parseDouble(feeField.getText().trim()));
                        } catch (NumberFormatException e) {
                            throw new RuntimeException("Invalid fee format. Please enter a valid number.");
                        }
                    }
                    
                    // Debug print before update
                    System.out.println("DEBUG: Before update - Date: " + existingAppointment.getAppointmentDate() + 
                                      ", Time: " + existingAppointment.getAppointmentTime());
                    
                    return appointmentDAO.updateAppointmentDetails(existingAppointment);
                }
                
                @Override
                protected void process(List<String> chunks) {
                    if (!chunks.isEmpty()) {
                        progressBar.setVisible(true);
                        progressBar.setString(chunks.get(chunks.size() - 1));
                    }
                }
                
                @Override
                protected void done() {
                    progressBar.setVisible(false);
                    progressBar.setString("");
                    saveButton.setEnabled(true);
                    
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(AppointmentDialog.this, 
                                "Appointment updated successfully!", 
                                "Success", 
                                JOptionPane.INFORMATION_MESSAGE);
                            parentFrame.refreshAppointmentTable();
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(AppointmentDialog.this, 
                                "Failed to update appointment!", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        Throwable cause = e.getCause();
                        String errorMessage = cause != null ? cause.getMessage() : e.getMessage();
                        JOptionPane.showMessageDialog(AppointmentDialog.this, 
                            "Error: " + errorMessage, 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            
            progressBar.setVisible(true);
            progressBar.setString("Starting update...");
            saveButton.setEnabled(false);
            worker.execute();
            
        } else {
            // Create new appointment in background
            SwingWorker<Boolean, String> worker = new SwingWorker<Boolean, String>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    publish("Creating appointment...");
                    
                    // Validate date and time
                    LocalDate appointmentDate;
                    LocalTime appointmentTime;
                    try {
                        appointmentDate = LocalDate.parse(dateField.getText().trim());
                    } catch (Exception e) {
                        throw new RuntimeException("Invalid date format. Please use YYYY-MM-DD.");
                    }
                    
                    try {
                        appointmentTime = LocalTime.parse(timeField.getText().trim());
                    } catch (Exception e) {
                        throw new RuntimeException("Invalid time format. Please use HH:MM.");
                    }
                    
                    // Check if time slot is available
                    if (!appointmentDAO.isTimeSlotAvailable(doctorId, appointmentDate, appointmentTime)) {
                        throw new RuntimeException("Time slot not available. Please choose a different time.");
                    }
                    
                    Appointment appointment = new Appointment(
                        appointmentIdField.getText().trim(),
                        patientId,
                        doctorId,
                        appointmentDate,
                        appointmentTime,
                        reasonArea.getText().trim()
                    );
                    
                    return appointmentDAO.addAppointment(appointment);
                }
                
                @Override
                protected void process(List<String> chunks) {
                    if (!chunks.isEmpty()) {
                        progressBar.setVisible(true);
                        progressBar.setString(chunks.get(chunks.size() - 1));
                    }
                }
                
                @Override
                protected void done() {
                    progressBar.setVisible(false);
                    progressBar.setString("");
                    saveButton.setEnabled(true);
                    
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(AppointmentDialog.this, 
                                "Appointment scheduled successfully!", 
                                "Success", 
                                JOptionPane.INFORMATION_MESSAGE);
                            parentFrame.refreshAppointmentTable();
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(AppointmentDialog.this, 
                                "Failed to schedule appointment!", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        Throwable cause = e.getCause();
                        String errorMessage = cause != null ? cause.getMessage() : e.getMessage();
                        JOptionPane.showMessageDialog(AppointmentDialog.this, 
                            "Error: " + errorMessage, 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            
            progressBar.setVisible(true);
            progressBar.setString("Starting schedule...");
            saveButton.setEnabled(false);
            worker.execute();
        }
    }
}
