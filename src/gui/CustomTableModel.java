// package gui;

// import javax.swing.table.AbstractTableModel;
// import java.util.List;

// public class CustomTableModel extends AbstractTableModel {
//     private final List<?> data;
//     private final String[] columnNames;
//     private final String[] propertyNames;
    
//     public CustomTableModel(List<?> data, String[] columnNames, String[] propertyNames) {
//         this.data = data;
//         this.columnNames = columnNames;
//         this.propertyNames = propertyNames;
//     }
    
//     @Override
//     public int getRowCount() {
//         return data.size();
//     }
    
//     @Override
//     public int getColumnCount() {
//         return columnNames.length;
//     }
    
//     @Override
//     public String getColumnName(int column) {
//         return columnNames[column];
//     }
    
//     @Override
//     public Object getValueAt(int rowIndex, int columnIndex) {
//         try {
//             Object item = data.get(rowIndex);
//             String propertyName = propertyNames[columnIndex];
            
//             // Use reflection to get the property value
//             java.lang.reflect.Method method = item.getClass().getMethod("get" + 
//                 propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));
//             return method.invoke(item);
            
//         } catch (Exception e) {
//             e.printStackTrace();
//             return null;
//         }
//     }
    
//     public Object getItemAt(int rowIndex) {
//         return data.get(rowIndex);
//     }
    
//     public void updateData(List<?> newData) {
//         // Note: This would need to be handled differently in a real application
//         // as we're using a generic list. For simplicity, we'll just fire the event.
//         fireTableDataChanged();
//     }
// }
package gui;

import model.Patient;
import model.Doctor;
import model.Appointment;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class CustomTableModel extends AbstractTableModel {
    private final List<?> data;
    private final String[] columnNames;
    private final Class<?> dataType;
    
    public CustomTableModel(List<?> data, String[] columnNames, Class<?> dataType) {
        this.data = data;
        this.columnNames = columnNames;
        this.dataType = dataType;
    }
    
    @Override
    public int getRowCount() {
        return data.size();
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object item = data.get(rowIndex);
        
        if (dataType == Patient.class) {
            return getPatientValue((Patient) item, columnIndex);
        } else if (dataType == Doctor.class) {
            return getDoctorValue((Doctor) item, columnIndex);
        } else if (dataType == Appointment.class) {
            return getAppointmentValue((Appointment) item, columnIndex);
        }
        
        return null;
    }
    
    private Object getPatientValue(Patient patient, int columnIndex) {
        switch (columnIndex) {
            case 0: return patient.getPatientId();
            case 1: return patient.getName();
            case 2: return patient.getAge();
            case 3: return patient.getGender();
            case 4: return patient.getPhone();
            case 5: return patient.getEmail();
            case 6: return patient.getBloodGroup();
            default: return null;
        }
    }
    
    private Object getDoctorValue(Doctor doctor, int columnIndex) {
        switch (columnIndex) {
            case 0: return doctor.getDoctorId();
            case 1: return doctor.getName();
            case 2: return doctor.getSpecialization();
            case 3: return doctor.getPhone();
            case 4: return doctor.getEmail();
            case 5: return doctor.getQualification();
            case 6: return doctor.getExperienceYears();
            case 7: return doctor.getConsultationFee();
            case 8: return doctor.isAvailable() ? "Yes" : "No";
            default: return null;
        }
    }
    
    private Object getAppointmentValue(Appointment appointment, int columnIndex) {
        switch (columnIndex) {
            case 0: return appointment.getAppointmentId();
            case 1: return appointment.getPatientName();
            case 2: return appointment.getDoctorName();
            case 3: return appointment.getAppointmentDate();
            case 4: return appointment.getAppointmentTime();
            case 5: return appointment.getStatus();
            case 6: return appointment.getReason();
            default: return null;
        }
    }
    
    public Object getItemAt(int rowIndex) {
        return data.get(rowIndex);
    }
    
    public void updateData(List<?> newData) {
        // Clear current data and add new data
        ((List<Object>) data).clear();
        ((List<Object>) data).addAll(newData);
        fireTableDataChanged();
    }
}