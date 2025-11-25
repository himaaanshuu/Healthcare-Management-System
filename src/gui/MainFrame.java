package gui;

import model.Patient;
import model.Doctor;
import model.Appointment;
import dao.PatientDAO;
import dao.DoctorDAO;
import dao.AppointmentDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private JTable patientsTable, doctorsTable, appointmentsTable;
    private CustomTableModel patientsModel, doctorsModel, appointmentsModel;
    private JButton addPatientBtn, editPatientBtn, deletePatientBtn;
    private JButton addDoctorBtn, editDoctorBtn, deleteDoctorBtn;
    private JButton addAppointmentBtn, editAppointmentBtn, deleteAppointmentBtn, refreshBtn;
    
    private PatientDAO patientDAO;
    private DoctorDAO doctorDAO;
    private AppointmentDAO appointmentDAO;

    public MainFrame() {
        super("Hospital Management System");
        this.patientDAO = new PatientDAO();
        this.doctorDAO = new DoctorDAO();
        this.appointmentDAO = new AppointmentDAO();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        refreshAllTables();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeComponents() {
        tabbedPane = new JTabbedPane();
        
        // Initialize tables with empty data
        initializePatientsTab();
        initializeDoctorsTab();
        initializeAppointmentsTab();
        
        // Initialize buttons
        addPatientBtn = new JButton("Add Patient");
        editPatientBtn = new JButton("Edit Patient");
        deletePatientBtn = new JButton("Delete Patient");
        
        addDoctorBtn = new JButton("Add Doctor");
        editDoctorBtn = new JButton("Edit Doctor");
        deleteDoctorBtn = new JButton("Delete Doctor");
        
        addAppointmentBtn = new JButton("Schedule Appointment");
        editAppointmentBtn = new JButton("Edit Appointment");
        deleteAppointmentBtn = new JButton("Cancel Appointment");
        refreshBtn = new JButton("Refresh All");
    }

    private void initializePatientsTab() {
        String[] patientColumns = {"Patient ID", "Name", "Age", "Gender", "Phone", "Email", "Blood Group"};
        patientsModel = new CustomTableModel(patientDAO.getAllPatients(), patientColumns, Patient.class);
        patientsTable = new JTable(patientsModel);
        patientsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientsTable.setAutoCreateRowSorter(true);
    }

    private void initializeDoctorsTab() {
        String[] doctorColumns = {"Doctor ID", "Name", "Specialization", "Phone", "Email", "Qualification", "Experience", "Fee", "Available"};
        doctorsModel = new CustomTableModel(doctorDAO.getAllDoctors(), doctorColumns, Doctor.class);
        doctorsTable = new JTable(doctorsModel);
        doctorsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        doctorsTable.setAutoCreateRowSorter(true);
    }

    private void initializeAppointmentsTab() {
        String[] appointmentColumns = {"Appointment ID", "Patient", "Doctor", "Date", "Time", "Status", "Reason"};
        appointmentsModel = new CustomTableModel(appointmentDAO.getAllAppointments(), appointmentColumns, Appointment.class);
        appointmentsTable = new JTable(appointmentsModel);
        appointmentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentsTable.setAutoCreateRowSorter(true);
    }

    private void setupLayout() {
        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Header
        JLabel headerLabel = new JLabel("Hospital Management System", JLabel.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Setup tabs
        setupPatientsTab();
        setupDoctorsTab();
        setupAppointmentsTab();
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Global refresh button
        JPanel globalPanel = new JPanel();
        globalPanel.add(refreshBtn);
        mainPanel.add(globalPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }

    private void setupPatientsTab() {
        JPanel patientPanel = new JPanel(new BorderLayout());
        
        // Button panel for patients
        JPanel patientButtonPanel = new JPanel();
        patientButtonPanel.add(addPatientBtn);
        patientButtonPanel.add(editPatientBtn);
        patientButtonPanel.add(deletePatientBtn);
        
        patientPanel.add(patientButtonPanel, BorderLayout.NORTH);
        patientPanel.add(new JScrollPane(patientsTable), BorderLayout.CENTER);
        
        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("Total Patients: " + patientsTable.getRowCount()));
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
        
        doctorPanel.add(doctorButtonPanel, BorderLayout.NORTH);
        doctorPanel.add(new JScrollPane(doctorsTable), BorderLayout.CENTER);
        
        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("Total Doctors: " + doctorsTable.getRowCount()));
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
        
        appointmentPanel.add(appointmentButtonPanel, BorderLayout.NORTH);
        appointmentPanel.add(new JScrollPane(appointmentsTable), BorderLayout.CENTER);
        
        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("Total Appointments: " + appointmentsTable.getRowCount()));
        appointmentPanel.add(statusPanel, BorderLayout.SOUTH);
        
        tabbedPane.addTab("Appointments", appointmentPanel);
    }

    private void setupEventHandlers() {
        // Patient buttons
        addPatientBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPatientDialog(false);
            }
        });
        
        editPatientBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPatientDialog(true);
            }
        });
        
        deletePatientBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deletePatient();
            }
        });
        
        // Doctor buttons
        addDoctorBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDoctorDialog(false);
            }
        });
        
        editDoctorBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDoctorDialog(true);
            }
        });
        
        deleteDoctorBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteDoctor();
            }
        });
        
        // Appointment buttons
        addAppointmentBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAppointmentDialog(false);
            }
        });
        
        editAppointmentBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAppointmentDialog(true);
            }
        });
        
        deleteAppointmentBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteAppointment();
            }
        });
        
        // Refresh button
        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshAllTables();
            }
        });
        
        // Double-click listeners for tables
        patientsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showPatientDialog(true);
                }
            }
        });
        
        doctorsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showDoctorDialog(true);
                }
            }
        });
        
        appointmentsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showAppointmentDialog(true);
                }
            }
        });
    }

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
            "Are you sure you want to delete patient: " + patient.getName() + "?\n" +
            "This will also delete all associated appointments.", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (patientDAO.deletePatient(patient.getPatientId())) {
                JOptionPane.showMessageDialog(this, 
                    "Patient deleted successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                refreshAllTables();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to delete patient!\n" +
                    "Please check if the patient has existing appointments.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
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
            "Are you sure you want to delete doctor: " + doctor.getName() + "?\n" +
            "This will also delete all associated appointments.", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (doctorDAO.deleteDoctor(doctor.getDoctorId())) {
                JOptionPane.showMessageDialog(this, 
                    "Doctor deleted successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                refreshAllTables();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to delete doctor!\n" +
                    "Please check if the doctor has existing appointments.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
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
            if (appointmentDAO.deleteAppointment(appointment.getAppointmentId())) {
                JOptionPane.showMessageDialog(this, 
                    "Appointment cancelled successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                refreshAllTables();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to cancel appointment!", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void refreshAllTables() {
        try {
            // Refresh patients table
            List<Patient> patients = patientDAO.getAllPatients();
            patientsModel.updateData(patients);
            
            // Refresh doctors table
            List<Doctor> doctors = doctorDAO.getAllDoctors();
            doctorsModel.updateData(doctors);
            
            // Refresh appointments table
            List<Appointment> appointments = appointmentDAO.getAllAppointments();
            appointmentsModel.updateData(appointments);
            
            // Update status labels
            updateStatusLabels();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error refreshing data: " + e.getMessage(),
                "Refresh Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateStatusLabels() {
        // Update patient count
        Component patientTab = tabbedPane.getComponentAt(0);
        if (patientTab instanceof JPanel) {
            JPanel patientPanel = (JPanel) patientTab;
            Component[] components = patientPanel.getComponents();
            for (Component comp : components) {
                if (comp instanceof JPanel) {
                    JPanel southPanel = (JPanel) comp;
                    Component[] southComps = southPanel.getComponents();
                    for (Component southComp : southComps) {
                        if (southComp instanceof JLabel) {
                            JLabel label = (JLabel) southComp;
                            if (label.getText().startsWith("Total Patients:")) {
                                label.setText("Total Patients: " + patientsTable.getRowCount());
                            }
                        }
                    }
                }
            }
        }
        
        // Update doctor count
        Component doctorTab = tabbedPane.getComponentAt(1);
        if (doctorTab instanceof JPanel) {
            JPanel doctorPanel = (JPanel) doctorTab;
            Component[] components = doctorPanel.getComponents();
            for (Component comp : components) {
                if (comp instanceof JPanel) {
                    JPanel southPanel = (JPanel) comp;
                    Component[] southComps = southPanel.getComponents();
                    for (Component southComp : southComps) {
                        if (southComp instanceof JLabel) {
                            JLabel label = (JLabel) southComp;
                            if (label.getText().startsWith("Total Doctors:")) {
                                label.setText("Total Doctors: " + doctorsTable.getRowCount());
                            }
                        }
                    }
                }
            }
        }
        
        // Update appointment count
        Component appointmentTab = tabbedPane.getComponentAt(2);
        if (appointmentTab instanceof JPanel) {
            JPanel appointmentPanel = (JPanel) appointmentTab;
            Component[] components = appointmentPanel.getComponents();
            for (Component comp : components) {
                if (comp instanceof JPanel) {
                    JPanel southPanel = (JPanel) comp;
                    Component[] southComps = southPanel.getComponents();
                    for (Component southComp : southComps) {
                        if (southComp instanceof JLabel) {
                            JLabel label = (JLabel) southComp;
                            if (label.getText().startsWith("Total Appointments:")) {
                                label.setText("Total Appointments: " + appointmentsTable.getRowCount());
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Public method to refresh tables from other dialogs
    public void refreshPatientTable() {
        List<Patient> patients = patientDAO.getAllPatients();
        patientsModel.updateData(patients);
        updateStatusLabels();
    }
    
    public void refreshDoctorTable() {
        List<Doctor> doctors = doctorDAO.getAllDoctors();
        doctorsModel.updateData(doctors);
        updateStatusLabels();
    }
    
    public void refreshAppointmentTable() {
        List<Appointment> appointments = appointmentDAO.getAllAppointments();
        appointmentsModel.updateData(appointments);
        updateStatusLabels();
    }
}