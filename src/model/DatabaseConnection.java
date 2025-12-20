package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // PostgreSQL connection details
    private static final String URL = "jdbc:postgresql://localhost:5432/hospital_management";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "123456789";
    
    // Static block to load driver
    static {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("PostgreSQL JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found!");
            throw new RuntimeException("PostgreSQL JDBC Driver not found. Please add the JDBC driver to your classpath.", e);
        }
    }
    
    // Private constructor to prevent instantiation
    private DatabaseConnection() {}
    
    /**
     * Get a new database connection for each operation.
     * This follows the best practice of opening and closing connections per operation.
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Database connection established.");
            return connection;
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            throw new SQLException("Database connection failed. Please check: \n" +
                                 "1. PostgreSQL server is running\n" +
                                 "2. Database 'hospital_management' exists\n" +
                                 "3. Username and password are correct\n" +
                                 "4. Port 5432 is accessible", e);
        }
    }
    
    /**
     * Get connection with retry mechanism for network issues
     */
    public static Connection getConnectionWithRetry(int maxRetries) throws SQLException {
        SQLException lastException = null;
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                Connection connection = getConnection();
                System.out.println("Connection established on attempt " + attempt);
                return connection;
            } catch (SQLException e) {
                lastException = e;
                System.err.println("Connection attempt " + attempt + " failed: " + e.getMessage());
                
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(2000); // Wait 2 seconds before retry
                        System.out.println("Retrying connection... (" + attempt + "/" + maxRetries + ")");
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new SQLException("Connection retry interrupted", ie);
                    }
                }
            }
        }
        
        throw new SQLException("Failed to establish connection after " + maxRetries + " attempts", lastException);
    }
    
    /**
     * Test database connection with proper resource management
     */
    public static boolean testConnection() {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            if (conn != null) {
                System.out.println("Database connection test: PASSED ✓");
                System.out.println("Connected to: " + conn.getMetaData().getDatabaseProductName() + 
                                  " " + conn.getMetaData().getDatabaseProductVersion());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Database connection test: FAILED ✗");
            System.err.println("Error: " + e.getMessage());
            System.err.println("URL: " + URL);
            System.err.println("Username: " + USERNAME);
        }
        return false;
    }
    
    /**
     * Test connection with detailed error information
     */
    public static String testConnectionWithDetails() {
        StringBuilder details = new StringBuilder();
        
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            if (conn != null && !conn.isClosed()) {
                details.append("✅ Database Connection Successful\n");
                details.append("   Database: ").append(conn.getMetaData().getDatabaseProductName()).append("\n");
                details.append("   Version: ").append(conn.getMetaData().getDatabaseProductVersion()).append("\n");
                details.append("   URL: ").append(URL).append("\n");
                details.append("   Username: ").append(USERNAME).append("\n");
                details.append("   Auto Commit: ").append(conn.getAutoCommit()).append("\n");
                return details.toString();
            }
        } catch (SQLException e) {
            details.append("❌ Database Connection Failed\n");
            details.append("   Error: ").append(e.getMessage()).append("\n");
            details.append("   SQL State: ").append(e.getSQLState()).append("\n");
            details.append("   Error Code: ").append(e.getErrorCode()).append("\n");
            details.append("   URL: ").append(URL).append("\n");
            details.append("   Username: ").append(USERNAME).append("\n");
            
            // Common error suggestions
            if (e.getMessage().contains("connection refused")) {
                details.append("\n💡 Suggestions:\n");
                details.append("   1. Check if PostgreSQL server is running\n");
                details.append("   2. Check if port 5432 is open\n");
                details.append("   3. Verify server is listening on localhost\n");
            } else if (e.getMessage().contains("authentication failed")) {
                details.append("\n💡 Suggestions:\n");
                details.append("   1. Check username and password\n");
                details.append("   2. Verify PostgreSQL authentication settings\n");
                details.append("   3. Check pg_hba.conf configuration\n");
            } else if (e.getMessage().contains("database") && e.getMessage().contains("not exist")) {
                details.append("\n💡 Suggestions:\n");
                details.append("   1. Create database: CREATE DATABASE hospital_management;\n");
                details.append("   2. Check database name spelling\n");
            }
        }
        
        return details.toString();
    }
    
    /**
     * Close connection quietly (helper method for manual connection management)
     */
    public static void closeQuietly(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("Database connection closed.");
                }
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Close resources quietly (for manual resource management)
     */
    public static void closeQuietly(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    System.err.println("Error closing resource: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Get database information for debugging
     */
    public static String getDatabaseInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Database Configuration:\n");
        info.append("  URL: ").append(URL).append("\n");
        info.append("  Username: ").append(USERNAME).append("\n");
        info.append("  Driver: PostgreSQL JDBC Driver\n");
        
        // Test connection to get live info
        try (Connection conn = getConnection()) {
            info.append("\nConnection Status: CONNECTED ✓\n");
            info.append("  Database: ").append(conn.getMetaData().getDatabaseProductName()).append("\n");
            info.append("  Version: ").append(conn.getMetaData().getDatabaseProductVersion()).append("\n");
            info.append("  Driver: ").append(conn.getMetaData().getDriverName()).append(" ").append(conn.getMetaData().getDriverVersion()).append("\n");
            info.append("  Transaction Isolation: ").append(getIsolationLevelName(conn.getTransactionIsolation())).append("\n");
        } catch (SQLException e) {
            info.append("\nConnection Status: DISCONNECTED ✗\n");
            info.append("  Error: ").append(e.getMessage()).append("\n");
        }
        
        return info.toString();
    }
    
    /**
     * Helper method to convert isolation level to string
     */
    private static String getIsolationLevelName(int level) {
        switch (level) {
            case Connection.TRANSACTION_NONE: return "NONE";
            case Connection.TRANSACTION_READ_UNCOMMITTED: return "READ_UNCOMMITTED";
            case Connection.TRANSACTION_READ_COMMITTED: return "READ_COMMITTED";
            case Connection.TRANSACTION_REPEATABLE_READ: return "REPEATABLE_READ";
            case Connection.TRANSACTION_SERIALIZABLE: return "SERIALIZABLE";
            default: return "UNKNOWN (" + level + ")";
        }
    }
    
    /**
     * Validate if connection parameters are set
     */
    @SuppressWarnings("unused")
    public static boolean validateConfiguration() {
        if (URL == null || URL.trim().isEmpty()) {
            System.err.println("Database URL is not configured");
            return false;
        }
        if (USERNAME == null || USERNAME.trim().isEmpty()) {
            System.err.println("Database username is not configured");
            return false;
        }
        if (PASSWORD == null) {
            System.err.println("Database password is not configured");
            return false;
        }
        return true;
    }
}
