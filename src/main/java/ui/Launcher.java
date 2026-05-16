package ui;

import service.DatabaseConnection;
import service.DatabaseInitializer;
import service.LoggerService;
import java.sql.Connection;

public class Launcher {

    public static void main(String[] args) {
        DatabaseInitializer.initialize();
        try {
            try (Connection conn = DatabaseConnection.connect()) {
                System.out.println("Database connection verified.");
            } catch (Exception dbEx) {
                System.err.println("Database connection failed: " + dbEx.getMessage());
            }

            KnightApp.main(args);
        } catch (Exception e) {
            LoggerService.logCriticalError("Unexpected application error", e);
        }
    }
}