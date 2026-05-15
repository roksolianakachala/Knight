package service;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static final Properties properties = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            System.err.println("Warning: config.properties not found, using default environment variables if available.");
        }
    }

    public static Connection connect() throws SQLException {
        String url = properties.getProperty("db.url", System.getenv("DB_URL"));
        String user = properties.getProperty("db.user", System.getenv("DB_USER"));
        String password = properties.getProperty("db.password", System.getenv("DB_PASSWORD"));

        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            LoggerService.logInfo("Successfully connected to Supabase database!");
            return connection;
        } catch (SQLException e) {
            LoggerService.logCriticalError("Failed to connect to Supabase: " + e.getMessage(), e);
            throw e;
        }
    }
}