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
            
            // Ініціалізувати каталог амуніції якщо він порожній
            initializeAmmunitionCatalog(conn);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void initializeAmmunitionCatalog(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Перевірити кількість предметів в каталозі
            var rs = stmt.executeQuery("SELECT COUNT(*) as count FROM ammunition");
            int currentCount = 0;
            if (rs.next()) {
                currentCount = rs.getInt("count");
            }
            
            // Якщо каталог має правильну кількість предметів (25), не оновлювати
            if (currentCount == 25) {
                System.out.println("Каталог амуніції вже містить 25 предметів. Оновлення не потрібне.");
                return;
            }
            
            // ПОВНІСТЮ ОЧИСТИТИ старий каталог перед додаванням нового
            System.out.println("Очищення старого каталогу амуніції (було " + currentCount + " предметів)...");
            stmt.executeUpdate("DELETE FROM knight_equipment"); // Очистити зв'язки
            stmt.executeUpdate("DELETE FROM ammunition");
            
            System.out.println("Наповнення нового реалістичного каталогу амуніції (25 предметів)...");
            
            // ========== МЕЧІ (5 предметів) ==========
            stmt.executeUpdate("INSERT INTO ammunition (name, type, material, weight, price, protection_level, damage) VALUES " +
                "('Залізний короткий меч', 'Sword', 'Залізо', 2.8, 350, 0, 35)");
            stmt.executeUpdate("INSERT INTO ammunition (name, type, material, weight, price, protection_level, damage) VALUES " +
                "('Сталевий лицарський меч', 'Sword', 'Сталь', 4.2, 700, 0, 55)");
            stmt.executeUpdate("INSERT INTO ammunition (name, type, material, weight, price, protection_level, damage) VALUES " +
                "('Меч із дамаської сталі', 'Sword', 'Дамаська сталь', 3.8, 1200, 0, 75)");
            stmt.executeUpdate("INSERT INTO ammunition (name, type, material, weight, price, protection_level, damage) VALUES " +
                "('Міфриловий клинок', 'Sword', 'Міфрил', 2.1, 2500, 0, 100)");
            stmt.executeUpdate("INSERT INTO ammunition (name, type, material, weight, price, protection_level, damage) VALUES " +
                "('Меч із драконячої кістки', 'Sword', 'Драконяча кістка', 4.5, 5000, 0, 140)");
            
            // ========== БРОНЯ (5 предметів) ==========
            stmt.executeUpdate("INSERT INTO ammunition (name, type, material, weight, price, protection_level, damage) VALUES " +
                "('Шкіряна броня розвідника', 'Armor', 'Шкіра', 5.0, 300, 20, 0)");
            stmt.executeUpdate("INSERT INTO ammunition (name, type, material, weight, price, protection_level, damage) VALUES " +
                "('Кольчуга воїна', 'Armor', 'Кольчуга', 9.0, 800, 45, 0)");
            stmt.executeUpdate("INSERT INTO ammunition (name, type, material, weight, price, protection_level, damage) VALUES " +
                "('Сталева лицарська броня', 'Armor', 'Сталь', 16.0, 1800, 85, 0)");
            stmt.executeUpdate("INSERT INTO ammunition (name, type, material, weight, price, protection_level, damage) VALUES " +
                "('Міфрилова броня захисника', 'Armor', 'Міфрил', 8.0, 4500, 130, 0)");
            stmt.executeUpdate("INSERT INTO ammunition (name, type, material, weight, price, protection_level, damage) VALUES " +
                "('Драконяча броня імператора', 'Armor', 'Драконяча луска', 12.0, 9000, 200, 0)");
            
            // ========== ШОЛОМИ (5 предметів) ==========
            stmt.executeUpdate("INSERT INTO ammunition (name, type, material, weight, price, protection_level, damage) VALUES " +
                "('Шкіряний капюшон слідопита', 'Helmet', 'Шкіра', 1.0, 150, 10, 0)");
            stmt.executeUpdate("INSERT INTO ammunition (name, type, material, weight, price, protection_level, damage) VALUES " +
                "('Бронзовий шолом вартового', 'Helmet', 'Бронза', 2.5, 450, 25, 0)");
            stmt.executeUpdate("INSERT INTO ammunition (name, type, material, weight, price, protection_level, damage) VALUES " +
                "('Сталевий лицарський шолом', 'Helmet', 'Сталь', 4.0, 900, 45, 0)");
            stmt.executeUpdate("INSERT INTO ammunition (name, type, material, weight, price, protection_level, damage) VALUES " +
                "('Міфриловий шолом чемпіона', 'Helmet', 'Міфрил', 2.2, 2200, 70, 0)");
            stmt.executeUpdate("INSERT INTO ammunition (name, type, material, weight, price, protection_level, damage) VALUES " +
                "('Драконячий бойовий шолом', 'Helmet', 'Драконяча луска', 3.5, 4500, 100, 0)");
            
            // ========== ЩИТИ (5 предметів) ==========
            stmt.executeUpdate("INSERT INTO ammunition (name, type, material, weight, price, protection_level, damage) VALUES " +
                "('Дерев''яний щит новобранця', 'Shield', 'Дерево', 3.0, 120, 15, 0)");
            stmt.executeUpdate("INSERT INTO ammunition (name, type, material, weight, price, protection_level, damage) VALUES " +
                "('Посилений щит піхотинця', 'Shield', 'Дерево + залізо', 5.0, 500, 35, 0)");
            stmt.executeUpdate("INSERT INTO ammunition (name, type, material, weight, price, protection_level, damage) VALUES " +
                "('Сталевий щит лицаря', 'Shield', 'Сталь', 7.0, 1200, 65, 0)");
            stmt.executeUpdate("INSERT INTO ammunition (name, type, material, weight, price, protection_level, damage) VALUES " +
                "('Міфриловий щит охоронця', 'Shield', 'Міфрил', 4.5, 3000, 95, 0)");
            stmt.executeUpdate("INSERT INTO ammunition (name, type, material, weight, price, protection_level, damage) VALUES " +
                "('Драконячий щит володаря', 'Shield', 'Драконяча луска', 6.0, 6500, 140, 0)");
            
            // ========== ЧОБОТИ (5 предметів) ==========
            stmt.executeUpdate("INSERT INTO ammunition (name, type, material, weight, price, protection_level, damage) VALUES " +
                "('Шкіряні чоботи мандрівника', 'Boots', 'Шкіра', 1.4, 100, 8, 0)");
            stmt.executeUpdate("INSERT INTO ammunition (name, type, material, weight, price, protection_level, damage) VALUES " +
                "('Посилені чоботи воїна', 'Boots', 'Посилена шкіра', 2.2, 350, 18, 0)");
            stmt.executeUpdate("INSERT INTO ammunition (name, type, material, weight, price, protection_level, damage) VALUES " +
                "('Сталеві чоботи лицаря', 'Boots', 'Сталь', 4.0, 800, 35, 0)");
            stmt.executeUpdate("INSERT INTO ammunition (name, type, material, weight, price, protection_level, damage) VALUES " +
                "('Міфрилові чоботи захисника', 'Boots', 'Міфрил', 2.0, 1800, 55, 0)");
            stmt.executeUpdate("INSERT INTO ammunition (name, type, material, weight, price, protection_level, damage) VALUES " +
                "('Драконячі чоботи володаря', 'Boots', 'Драконяча шкіра', 3.0, 4000, 80, 0)");
            
            System.out.println("✓ Новий каталог амуніції успішно створено: 25 предметів!");
            LoggerService.logInfo("Каталог амуніції оновлено: 25 нових предметів додано до БД");
            
        } catch (Exception e) {
            System.err.println("Помилка при оновленні каталогу амуніції: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
