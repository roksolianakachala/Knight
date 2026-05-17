package service;

import model.Ammunition;
import model.Armor;
import model.Boots;
import model.Helmet;
import model.Knight;
import model.Shield;
import model.Sword;
import model.Weapon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseAndRepositoryTest {

    @BeforeEach
    void resetDatabase() throws Exception {
        Files.deleteIfExists(Path.of("knight_database.db"));
        DatabaseInitializer.initialize();
    }

    @Test
    void databaseConnectionOpensSqliteConnection() throws Exception {
        assertDoesNotThrow(DatabaseConnection::new);

        try (Connection connection = DatabaseConnection.connect()) {
            assertFalse(connection.isClosed());
            assertTrue(connection.getMetaData().getURL().contains("knight_database.db"));
        }
    }

    @Test
    void initializerCreatesFullAmmunitionCatalogOnce() throws Exception {
        assertDoesNotThrow(DatabaseInitializer::new);
        DatabaseInitializer.initialize();
        DatabaseInitializer.initialize();

        try (Connection connection = DatabaseConnection.connect();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM ammunition")) {
            assertTrue(rs.next());
            assertEquals(25, rs.getInt(1));
        }
    }

    @Test
    void initializerRebuildsCatalogWhenItemCountIsWrong() throws Exception {
        try (Connection connection = DatabaseConnection.connect();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM knight_equipment");
            statement.executeUpdate("DELETE FROM ammunition WHERE id IN (SELECT id FROM ammunition LIMIT 1)");
        }

        DatabaseInitializer.initialize();

        try (Connection connection = DatabaseConnection.connect();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM ammunition")) {
            assertTrue(rs.next());
            assertEquals(25, rs.getInt(1));
        }
    }

    @Test
    void repositoryMapsAllAmmunitionTypesFromCatalog() {
        KnightRepository repository = new KnightRepository();
        assertDoesNotThrow(repository::createTables);

        List<Ammunition> ammunition = repository.getAllAmmunition();

        assertEquals(25, ammunition.size());
        assertTrue(ammunition.stream().anyMatch(Sword.class::isInstance));
        assertTrue(ammunition.stream().anyMatch(Armor.class::isInstance));
        assertTrue(ammunition.stream().anyMatch(Helmet.class::isInstance));
        assertTrue(ammunition.stream().anyMatch(Shield.class::isInstance));
        assertTrue(ammunition.stream().anyMatch(Boots.class::isInstance));
    }

    @Test
    void repositorySavesAndLoadsKnightWithEquipment() {
        KnightRepository repository = new KnightRepository();
        Knight knight = new Knight("Repository Knight", 181, 82, 55, 60);
        knight.equip(new Sword("Unit sword", 3.0, 450.0, "Steel", 50));
        knight.equip(new Armor("Unit armor", 8.0, 900.0, "Steel", 80));

        repository.saveKnight(knight);

        List<Knight> loaded = repository.getAllKnights();
        Knight result = loaded.stream()
                .filter(k -> k.getName().equals("Repository Knight"))
                .findFirst()
                .orElseThrow();

        assertTrue(result.getId() > 0);
        assertEquals(2, result.getEquipment().size());
        assertTrue(result.getEquipment().stream().anyMatch(Weapon.class::isInstance));
        assertTrue(result.getEquipment().stream().anyMatch(Armor.class::isInstance));
    }

    @Test
    void repositoryUpdatesExistingAmmunition() throws Exception {
        KnightRepository repository = new KnightRepository();
        Armor armor = new Armor("Old armor", 5.0, 100.0, "Leather", 10);

        repository.saveAmmunition(armor);
        armor.setName("Updated armor");
        armor.setDefense(25);
        repository.saveAmmunition(armor);

        try (Connection connection = DatabaseConnection.connect();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT name, protection_level FROM ammunition WHERE id = " + armor.getId())) {
            assertTrue(rs.next());
            assertEquals("Updated armor", rs.getString("name"));
            assertEquals(25, rs.getInt("protection_level"));
        }
    }

    @Test
    void repositoryUpdatesExistingKnightAndEquipmentLinks() {
        KnightRepository repository = new KnightRepository();
        Knight knight = new Knight("Before update", 180, 80, 50, 50);
        knight.equip(new Sword("First sword", 3.0, 300.0, "Iron", 30));

        repository.saveKnight(knight);
        knight.setName("After update");
        knight.setStrength(80);
        knight.unequip(knight.getEquipment().get(0));
        knight.equip(new Shield("Updated shield", 5.0, 450.0, "Steel", 40));
        repository.saveKnight(knight);

        Knight loaded = repository.getAllKnights().stream()
                .filter(item -> item.getId() == knight.getId())
                .findFirst()
                .orElseThrow();

        assertEquals("After update", loaded.getName());
        assertEquals(160, loaded.calculateAttack());
        assertEquals(1, loaded.getEquipment().size());
        assertTrue(loaded.getEquipment().get(0) instanceof Shield);
    }

    @Test
    void repositorySavesWeaponAndMapsUnknownTypeAsArmor() throws Exception {
        KnightRepository repository = new KnightRepository();
        Weapon weapon = new Weapon("Training axe", 4.0, 250.0, "Iron", 35);
        repository.saveAmmunition(weapon);

        try (Connection connection = DatabaseConnection.connect();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    INSERT INTO ammunition (name, type, material, weight, price, protection_level, damage)
                    VALUES ('Unknown defensive item', 'Unknown', 'Steel', 7.0, 500.0, 44, 0)
                    """);
        }

        List<Ammunition> ammunition = repository.getAllAmmunition();

        assertTrue(ammunition.stream().anyMatch(item ->
                item instanceof Weapon weaponItem
                        && weaponItem.getName().equals("Training axe")
                        && weaponItem.getDamage() == 35));
        assertTrue(ammunition.stream().anyMatch(item ->
                item instanceof Armor armor
                        && armor.getName().equals("Unknown defensive item")
                        && armor.getDefense() == 44));
    }
}
