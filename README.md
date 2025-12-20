# 🏥 Hospital Management System

A Java Swing application for managing hospital operations with PostgreSQL database, multithreading, and professional GUI.

## ✨ Features
- **Patient Management** - CRUD operations for patient records
- **Doctor Management** - Track doctors, specializations, availability
- **Appointment Scheduling** - Book, edit, cancel appointments with time validation
- **Multithreaded GUI** - SwingWorker for responsive interface
- **Data Export** - Export to CSV format
- **Search Functionality** - Quick patient search
- **Progress Feedback** - Visual progress bars for long operations

## 🚀 Quick Start

### 1. Prerequisites
. **Java 8+**
. **PostgreSQL** (running on localhost:5432)

### 2. Setup Database
```sql
CREATE DATABASE hospital_management;
```
The tables will be created automatically on first run.

### 3. Configure Connection
Edit `src/model/DatabaseConnection.java`:
```java
private static final String URL = "jdbc:postgresql://localhost:5432/hospital_management";
private static final String USERNAME = "postgres";
private static final String PASSWORD = "your_password";  // Update this
```

### 4. Run Application
**Using IDE:**
1. Import project
2. Add PostgreSQL JDBC driver to classpath
3. Run `build-and-run.bat` by double clicking 



## 📁 Project Structure
```
src/
├── main/Main.java           # Entry point with splash screen
├── gui/                     # All GUI components
├── model/                   # Entity classes & DatabaseConnection
├── dao/                     # Data Access Objects
└── utils/                   # Export & Validation utilities
```

## 🔧 Key Components
- **MainFrame.java** - Main application window with tabbed interface
- **CustomTableModel.java** - Generic table model for Patient/Doctor/Appointment data
- **DatabaseConnection.java** - Thread-safe connection management
- **AppointmentDAO.java** - Handles all appointment operations with time validation

## 🎯 Usage
1. **Add Records** - Use "Add" buttons in each tab
2. **Edit/Delete** - Select row → Edit/Delete button
3. **Schedule Appointments** - Select patient, doctor, date/time
4. **Search Patients** - Use search field in Patients tab
5. **Export Data** - Click "Export" buttons to save as CSV

## ⚠️ Troubleshooting
- **Database Connection Error**: Ensure PostgreSQL is running
- **Driver Not Found**: Add `postgresql-42.x.x.jar` to classpath
- **Authentication Failed**: Update password in DatabaseConnection.java

## 📊 Database Tables
- `patients` - Patient information
- `doctors` - Doctor profiles and availability
- `appointments` - Appointment details with status tracking

## 🎓 Academic Project
Demonstrates: OOP, MVC, Database Integration, Multithreading, GUI Design, Exception Handling

---



