package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DatabaseConnection {
    // PostgreSQL connection details
    private static final String URL = "jdbc:postgresql://localhost:5432/hospital_management";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "123456789";
    
    private static Connection connection;
    
    private DatabaseConnection() {}
    
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Load PostgreSQL JDBC Driver
                Class.forName("org.postgresql.Driver");
                
                // Establish connection
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Hospital Management Database (PostgreSQL) connected successfully!");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found!");
            e.printStackTrace();
            showErrorDialog("Database Driver Error", 
                "PostgreSQL JDBC Driver not found.\n" +
                "Please ensure PostgreSQL JDBC driver is in the classpath.\n" +
                "Download from: https://jdbc.postgresql.org/download.html");
        } catch (SQLException e) {
            System.err.println("Failed to connect to hospital database!");
            e.printStackTrace();
            showErrorDialog("Database Connection Error", 
                "Cannot connect to PostgreSQL database.\n" +
                "Please check:\n" +
                "1. PostgreSQL server is running\n" +
                "2. Database 'hospital_management' exists\n" +
                "3. Username and password are correct\n" +
                "4. Port 5432 is accessible");
        }
        return connection;
    }
    
    private static void showErrorDialog(String title, String message) {
        try {
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
                }
            });
        } catch (Exception e) {
            System.err.println(title + ": " + message);
        }
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed!");
            } catch (SQLException e) {
                System.err.println("Error closing database connection!");
                e.printStackTrace();
            }
        }
    }
    
    // Test database connection
    public static boolean testConnection() {
        try (Connection testConn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            return testConn != null && !testConn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    // Get database info for debugging
    public static String getConnectionInfo() {
        try {
            if (connection != null && !connection.isClosed()) {
                return "Connected to PostgreSQL: " + URL + " as " + USERNAME;
            } else {
                return "Not connected to database";
            }
        } catch (SQLException e) {
            return "Connection error: " + e.getMessage();
        }
    }
}