package gui;

import model.Patient;
import model.Doctor;
import model.Appointment;
import dao.PatientDAO;
import dao.DoctorDAO;
import dao.AppointmentDAO;
import utils.ExportUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private JTable patientsTable, doctorsTable, appointmentsTable;
    private CustomTableModel patientsModel, doctorsModel, appointmentsModel;
    private JButton addPatientBtn, editPatientBtn, deletePatientBtn, exportPatientsBtn, searchPatientBtn;
    private JButton addDoctorBtn, editDoctorBtn, deleteDoctorBtn, exportDoctorsBtn;
    private JButton addAppointmentBtn, editAppointmentBtn, deleteAppointmentBtn, exportAppointmentsBtn, refreshBtn;
    private JTextField searchField;
    
    // Status labels as instance variables
    private JLabel patientStatusLabel;
    private JLabel doctorStatusLabel;
    private JLabel appointmentStatusLabel;
    
    // Progress bar for long operations
    private JProgressBar progressBar;
    
    private PatientDAO patientDAO;
    private DoctorDAO doctorDAO;
    private AppointmentDAO appointmentDAO;

    public MainFrame() {
        super("HealthCare Management System");
        this.patientDAO = new PatientDAO();
        this.doctorDAO = new DoctorDAO();
        this.appointmentDAO = new AppointmentDAO();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        // Initial load in background thread
        SwingWorker<Void, Void> initialLoader = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                refreshAllTablesBackground();
                return null;
            }
        };
        initialLoader.execute();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
        
        // Initialize tables with EMPTY data (not null) to prevent NullPointerException
        String[] patientColumns = {"Patient ID", "Name", "Age", "Gender", "Phone", "Email", "Blood Group"};
        String[] doctorColumns = {"Doctor ID", "Name", "Specialization", "Phone", "Email", "Qualification", "Experience", "Fee", "Available"};
        String[] appointmentColumns = {"Appointment ID", "Patient", "Doctor", "Date", "Time", "Status", "Reason"};
        
        // Use empty ArrayList instead of null to prevent NullPointerException
        patientsModel = new CustomTableModel(new ArrayList<>(), patientColumns, Patient.class);
        doctorsModel = new CustomTableModel(new ArrayList<>(), doctorColumns, Doctor.class);
        appointmentsModel = new CustomTableModel(new ArrayList<>(), appointmentColumns, Appointment.class);
        
        patientsTable = new JTable(patientsModel);
        doctorsTable = new JTable(doctorsModel);
        appointmentsTable = new JTable(appointmentsModel);
        
        patientsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        doctorsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        patientsTable.setAutoCreateRowSorter(true);
        doctorsTable.setAutoCreateRowSorter(true);
        appointmentsTable.setAutoCreateRowSorter(true);
        
        patientsTable.setFillsViewportHeight(true);
        doctorsTable.setFillsViewportHeight(true);
        appointmentsTable.setFillsViewportHeight(true);
        
        // Initialize buttons
        addPatientBtn = new JButton("Add Patient");
        editPatientBtn = new JButton("Edit Patient");
        deletePatientBtn = new JButton("Delete Patient");
        exportPatientsBtn = new JButton("Export Patients");
        searchPatientBtn = new JButton("Search");
        
        addDoctorBtn = new JButton("Add Doctor");
        editDoctorBtn = new JButton("Edit Doctor");
        deleteDoctorBtn = new JButton("Delete Doctor");
        exportDoctorsBtn = new JButton("Export Doctors");
        
        addAppointmentBtn = new JButton("Schedule Appointment");
        editAppointmentBtn = new JButton("Edit Appointment");
        deleteAppointmentBtn = new JButton("Cancel Appointment");
        exportAppointmentsBtn = new JButton("Export Appointments");
        refreshBtn = new JButton("Refresh All");
        
        searchField = new JTextField(20);
        
        // Progress bar
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setStringPainted(true);
    }

    private void setupLayout() {
        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Header
        JLabel headerLabel = new JLabel("HealthCare Management System", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 28));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Setup tabs
        setupPatientsTab();
        setupDoctorsTab();
        setupAppointmentsTab();
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Bottom panel with progress bar and refresh button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        // Left side: Status message
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel statusLabel = new JLabel("Ready");
        statusPanel.add(statusLabel);
        
        // Center: Progress bar
        JPanel progressPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        progressPanel.add(progressBar);
        
        // Right side: Refresh button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshBtn);
        
        bottomPanel.add(statusPanel, BorderLayout.WEST);
        bottomPanel.add(progressPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }

    private void setupPatientsTab() {
        JPanel patientPanel = new JPanel(new BorderLayout());
        
        // Top panel with buttons and search
        JPanel patientTopPanel = new JPanel(new BorderLayout());
        
        // Button panel
        JPanel patientButtonPanel = new JPanel();
        patientButtonPanel.add(addPatientBtn);
        patientButtonPanel.add(editPatientBtn);
        patientButtonPanel.add(deletePatientBtn);
        patientButtonPanel.add(exportPatientsBtn);
        
        patientTopPanel.add(patientButtonPanel, BorderLayout.WEST);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchPatientBtn);
        
        JButton clearSearchBtn = new JButton("Clear");
        clearSearchBtn.addActionListener(e -> {
            searchField.setText("");
            refreshPatientTableBackground();
        });
        searchPanel.add(clearSearchBtn);
        
        patientTopPanel.add(searchPanel, BorderLayout.EAST);
        patientPanel.add(patientTopPanel, BorderLayout.NORTH);
        
        // Table in scroll pane
        patientPanel.add(new JScrollPane(patientsTable), BorderLayout.CENTER);
        
        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        patientStatusLabel = new JLabel("Patients: Loading...");
        statusPanel.add(patientStatusLabel);
        patientPanel.add(statusPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Patients", patientPanel);
    }

    private void setupDoctorsTab() {
        JPanel doctorPanel = new JPanel(new BorderLayout());
        
        // Button panel for doctors
        JPanel doctorButtonPanel = new JPanel();
        doctorButtonPanel.add(addDoctorBtn);
        doctorButtonPanel.add(editDoctorBtn);
        doctorButtonPanel.add(deleteDoctorBtn);
        doctorButtonPanel.add(exportDoctorsBtn);
        
        doctorPanel.add(doctorButtonPanel, BorderLayout.NORTH);
        doctorPanel.add(new JScrollPane(doctorsTable), BorderLayout.CENTER);
        
        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        doctorStatusLabel = new JLabel("Doctors: Loading...");
        statusPanel.add(doctorStatusLabel);
        doctorPanel.add(statusPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Doctors", doctorPanel);
    }

    private void setupAppointmentsTab() {
        JPanel appointmentPanel = new JPanel(new BorderLayout());
        
        // Button panel for appointments
        JPanel appointmentButtonPanel = new JPanel();
        appointmentButtonPanel.add(addAppointmentBtn);
        appointmentButtonPanel.add(editAppointmentBtn);
        appointmentButtonPanel.add(deleteAppointmentBtn);
        appointmentButtonPanel.add(exportAppointmentsBtn);
        
        appointmentPanel.add(appointmentButtonPanel, BorderLayout.NORTH);
        appointmentPanel.add(new JScrollPane(appointmentsTable), BorderLayout.CENTER);
        
        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        appointmentStatusLabel = new JLabel("Appointments: Loading...");
        statusPanel.add(appointmentStatusLabel);
        appointmentPanel.add(statusPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Appointments", appointmentPanel);
    }

    private void setupEventHandlers() {
        // Patient buttons
        addPatientBtn.addActionListener(e -> showPatientDialog(false));
        editPatientBtn.addActionListener(e -> showPatientDialog(true));
        deletePatientBtn.addActionListener(e -> deletePatient());
        exportPatientsBtn.addActionListener(e -> exportPatients());
        searchPatientBtn.addActionListener(e -> searchPatientsBackground());
        
        // Doctor buttons
        addDoctorBtn.addActionListener(e -> showDoctorDialog(false));
        editDoctorBtn.addActionListener(e -> showDoctorDialog(true));
        deleteDoctorBtn.addActionListener(e -> deleteDoctor());
        exportDoctorsBtn.addActionListener(e -> exportDoctors());
        
        // Appointment buttons
        addAppointmentBtn.addActionListener(e -> showAppointmentDialog(false));
        editAppointmentBtn.addActionListener(e -> showAppointmentDialog(true));
        deleteAppointmentBtn.addActionListener(e -> deleteAppointment());
        exportAppointmentsBtn.addActionListener(e -> exportAppointments());
        
        // Refresh button
        refreshBtn.addActionListener(e -> refreshAllTablesBackground());
        
        // Double-click listeners for tables
        patientsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showPatientDialog(true);
                }
            }
        });
        
        doctorsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showDoctorDialog(true);
                }
            }
        });
        
        appointmentsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showAppointmentDialog(true);
                }
            }
        });
        
        // Enter key for search
        searchField.addActionListener(e -> searchPatientsBackground());
    }

    // ============== MULTITHREADING METHODS ==============
    
    private void refreshAllTablesBackground() {
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                publish("Loading patients...");
                List<Patient> patients = patientDAO.getAllPatients();
                SwingUtilities.invokeLater(() -> {
                    patientsModel.updateData(patients);
                    updatePatientStatus(patients.size());
                });
                
                publish("Loading doctors...");
                List<Doctor> doctors = doctorDAO.getAllDoctors();
                SwingUtilities.invokeLater(() -> {
                    doctorsModel.updateData(doctors);
                    updateDoctorStatus(doctors);
                });
                
                publish("Loading appointments...");
                List<Appointment> appointments = appointmentDAO.getAllAppointments();
                SwingUtilities.invokeLater(() -> {
                    appointmentsModel.updateData(appointments);
                    updateAppointmentStatus(appointments);
                });
                
                publish("Data loaded successfully");
                return null;
            }
            
            @Override
            protected void process(List<String> chunks) {
                // Update progress bar messages
                if (!chunks.isEmpty()) {
                    progressBar.setString(chunks.get(chunks.size() - 1));
                }
            }
            
            @Override
            protected void done() {
                progressBar.setVisible(false);
                progressBar.setString("");
                refreshBtn.setEnabled(true);
            }
        };
        
        progressBar.setVisible(true);
        progressBar.setString("Starting refresh...");
        refreshBtn.setEnabled(false);
        worker.execute();
    }
    
    private void refreshPatientTableBackground() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                List<Patient> patients = patientDAO.getAllPatients();
                SwingUtilities.invokeLater(() -> {
                    patientsModel.updateData(patients);
                    updatePatientStatus(patients.size());
                });
                return null;
            }
        };
        worker.execute();
    }
    
    private void refreshDoctorTableBackground() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                List<Doctor> doctors = doctorDAO.getAllDoctors();
                SwingUtilities.invokeLater(() -> {
                    doctorsModel.updateData(doctors);
                    updateDoctorStatus(doctors);
                });
                return null;
            }
        };
        worker.execute();
    }
    
    private void refreshAppointmentTableBackground() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                List<Appointment> appointments = appointmentDAO.getAllAppointments();
                SwingUtilities.invokeLater(() -> {
                    appointmentsModel.updateData(appointments);
                    updateAppointmentStatus(appointments);
                });
                return null;
            }
        };
        worker.execute();
    }
    
    private void searchPatientsBackground() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            refreshPatientTableBackground();
            return;
        }
        
        SwingWorker<List<Patient>, Void> worker = new SwingWorker<List<Patient>, Void>() {
            @Override
            protected List<Patient> doInBackground() throws Exception {
                return patientDAO.searchPatientsByName(query);
            }
            
            @Override
            protected void done() {
                try {
                    List<Patient> patients = get();
                    patientsModel.updateData(patients);
                    updatePatientStatus(patients.size());
                } catch (InterruptedException | ExecutionException e) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                        "Error searching patients: " + e.getMessage(),
                        "Search Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void deletePatient() {
        int selectedRow = patientsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a patient to delete!", 
                "Warning", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int modelRow = patientsTable.convertRowIndexToModel(selectedRow);
        Patient patient = (Patient) patientsModel.getItemAt(modelRow);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete patient: " + patient.getName() + "?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return patientDAO.deletePatient(patient.getPatientId());
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(MainFrame.this, 
                                "Patient deleted successfully!", 
                                "Success", 
                                JOptionPane.INFORMATION_MESSAGE);
                            refreshPatientTableBackground();
                        } else {
                            JOptionPane.showMessageDialog(MainFrame.this, 
                                "Failed to delete patient!", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        JOptionPane.showMessageDialog(MainFrame.this,
                            "Error deleting patient: " + e.getMessage(),
                            "Delete Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }
    
    private void deleteDoctor() {
        int selectedRow = doctorsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a doctor to delete!", 
                "Warning", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int modelRow = doctorsTable.convertRowIndexToModel(selectedRow);
        Doctor doctor = (Doctor) doctorsModel.getItemAt(modelRow);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete doctor: " + doctor.getName() + "?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return doctorDAO.deleteDoctor(doctor.getDoctorId());
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(MainFrame.this, 
                                "Doctor deleted successfully!", 
                                "Success", 
                                JOptionPane.INFORMATION_MESSAGE);
                            refreshDoctorTableBackground();
                        } else {
                            JOptionPane.showMessageDialog(MainFrame.this, 
                                "Failed to delete doctor!", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        JOptionPane.showMessageDialog(MainFrame.this,
                            "Error deleting doctor: " + e.getMessage(),
                            "Delete Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }
    
    private void deleteAppointment() {
        int selectedRow = appointmentsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an appointment to cancel!", 
                "Warning", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int modelRow = appointmentsTable.convertRowIndexToModel(selectedRow);
        Appointment appointment = (Appointment) appointmentsModel.getItemAt(modelRow);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to cancel appointment: " + appointment.getAppointmentId() + "?\n" +
            "Patient: " + appointment.getPatientName() + "\n" +
            "Doctor: " + appointment.getDoctorName(), 
            "Confirm Cancel", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return appointmentDAO.deleteAppointment(appointment.getAppointmentId());
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(MainFrame.this, 
                                "Appointment cancelled successfully!", 
                                "Success", 
                                JOptionPane.INFORMATION_MESSAGE);
                            refreshAppointmentTableBackground();
                        } else {
                            JOptionPane.showMessageDialog(MainFrame.this, 
                                "Failed to cancel appointment!", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        JOptionPane.showMessageDialog(MainFrame.this,
                            "Error cancelling appointment: " + e.getMessage(),
                            "Cancel Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }

    // ============== DIALOG METHODS ==============
    
    private void showPatientDialog(boolean isEdit) {
        if (isEdit) {
            int selectedRow = patientsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a patient to edit!", 
                    "Warning", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = patientsTable.convertRowIndexToModel(selectedRow);
            Patient patient = (Patient) patientsModel.getItemAt(modelRow);
            PatientManagementDialog dialog = new PatientManagementDialog(this, patient);
            dialog.setVisible(true);
        } else {
            PatientManagementDialog dialog = new PatientManagementDialog(this);
            dialog.setVisible(true);
        }
    }

    private void showDoctorDialog(boolean isEdit) {
        if (isEdit) {
            int selectedRow = doctorsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Please select a doctor to edit!", 
                    "Warning", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = doctorsTable.convertRowIndexToModel(selectedRow);
            Doctor doctor = (Doctor) doctorsModel.getItemAt(modelRow);
            DoctorManagementDialog dialog = new DoctorManagementDialog(this, doctor);
            dialog.setVisible(true);
        } else {
            DoctorManagementDialog dialog = new DoctorManagementDialog(this);
            dialog.setVisible(true);
        }
    }

    private void showAppointmentDialog(boolean isEdit) {
        if (isEdit) {
            int selectedRow = appointmentsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Please select an appointment to edit!", 
                    "Warning", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = appointmentsTable.convertRowIndexToModel(selectedRow);
            Appointment appointment = (Appointment) appointmentsModel.getItemAt(modelRow);
            AppointmentDialog dialog = new AppointmentDialog(this, appointment);
            dialog.setVisible(true);
        } else {
            AppointmentDialog dialog = new AppointmentDialog(this);
            dialog.setVisible(true);
        }
    }

    // ============== EXPORT METHODS ==============
    
    private void exportPatients() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                ExportUtils.exportTableToCSV(patientsTable, "Patients");
                return null;
            }
        };
        worker.execute();
    }

    private void exportDoctors() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                ExportUtils.exportTableToCSV(doctorsTable, "Doctors");
                return null;
            }
        };
        worker.execute();
    }

    private void exportAppointments() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                ExportUtils.exportTableToCSV(appointmentsTable, "Appointments");
                return null;
            }
        };
        worker.execute();
    }

    // ============== STATUS UPDATE METHODS ==============
    
    private void updatePatientStatus(int count) {
        if (patientStatusLabel != null) {
            patientStatusLabel.setText("Patients: " + count);
        }
    }
    
    private void updateDoctorStatus(List<Doctor> doctors) {
        if (doctorStatusLabel != null && doctors != null) {
            long availableCount = doctors.stream().filter(Doctor::isAvailable).count();
            doctorStatusLabel.setText("Doctors: " + doctors.size() + " (Available: " + availableCount + ")");
        }
    }
    
    private void updateAppointmentStatus(List<Appointment> appointments) {
        if (appointmentStatusLabel != null && appointments != null) {
            try {
                long todayCount = appointmentDAO.getAppointmentsByDate(java.time.LocalDate.now()).size();
                appointmentStatusLabel.setText("Appointments: " + appointments.size() + " (Today: " + todayCount + ")");
            } catch (Exception e) {
                appointmentStatusLabel.setText("Appointments: " + appointments.size());
            }
        }
    }
    
    // ============== PUBLIC METHODS FOR DIALOGS ==============
    
    // For backward compatibility with dialogs
    public void refreshAllTables() {
        refreshAllTablesBackground();
    }
    
    public void refreshPatientTable() {
        refreshPatientTableBackground();
    }
    
    public void refreshDoctorTable() {
        refreshDoctorTableBackground();
    }
    
    public void refreshAppointmentTable() {
        refreshAppointmentTableBackground();
    }
    
    // Get DAO instances for dialogs
    public PatientDAO getPatientDAO() {
        return patientDAO;
    }
    
    public DoctorDAO getDoctorDAO() {
        return doctorDAO;
    }
    
    public AppointmentDAO getAppointmentDAO() {
        return appointmentDAO;
    }
}
