package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseInitializer {

    private static final String URL = "jdbc:sqlite:knight_database.db";

    public static void initialize() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS knights (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    height REAL,
                    weight REAL,
                    strength INTEGER,
                    endurance INTEGER,
                    max_weight REAL,
                    created_at TEXT DEFAULT CURRENT_TIMESTAMP
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS ammunition (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    type TEXT,
                    material TEXT,
                    weight REAL,
                    price REAL,
                    protection_level INTEGER,
                    damage INTEGER,
                    created_at TEXT DEFAULT CURRENT_TIMESTAMP
                );
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS knight_equipment (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    knight_id INTEGER,
                    ammunition_id INTEGER,
                    FOREIGN KEY (knight_id) REFERENCES knights(id),
                    FOREIGN KEY (ammunition_id) REFERENCES ammunition(id)
                );
            """);

            System.out.println("SQLite database initialized successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
