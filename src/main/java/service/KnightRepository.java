package service;

import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KnightRepository {

    public void createTables() {

    }

    public void saveKnight(Knight knight) {
        String sql;
        if (knight.getId() > 0) {
            sql = "UPDATE knights SET name=?, height=?, weight=?, strength=?, endurance=?, max_weight=? WHERE id=?";
        } else {
            sql = "INSERT INTO knights (name, height, weight, strength, endurance, max_weight) VALUES (?, ?, ?, ?, ?, ?)";
        }

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, knight.getName());
            pstmt.setDouble(2, knight.getHeight());
            pstmt.setDouble(3, knight.getWeight());
            pstmt.setInt(4, knight.getStrength());
            pstmt.setInt(5, knight.getEndurance());
            pstmt.setDouble(6, knight.getMaxWeight());

            int affectedRows;
            if (knight.getId() > 0) {
                pstmt.setInt(7, knight.getId());
                affectedRows = pstmt.executeUpdate();
            } else {
                affectedRows = pstmt.executeUpdate();
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    knight.setId(generatedKeys.getInt(1));
                }
            }
            
            if (affectedRows > 0) {
                saveEquipment(knight);
                LoggerService.logInfo("Лицар збережений успішно. Змінено рядків: " + affectedRows);
            } else {
                LoggerService.logWarning("Запит виконано, але жодного рядка не змінено (ID не знайдено?).");
            }
        } catch (SQLException e) {
            LoggerService.logCriticalError("Помилка при збереженні лицаря: " + e.getMessage(), e);
        }
    }

    private void saveEquipment(Knight knight) throws SQLException {
        try (Connection conn = DatabaseConnection.connect()) {
            // Delete old equipment links
            String deleteSql = "DELETE FROM knight_equipment WHERE knight_id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, knight.getId());
                deleteStmt.executeUpdate();
            }

            // Insert new links
            String insertSql = "INSERT INTO knight_equipment (knight_id, ammunition_id) VALUES (?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                for (Ammunition item : knight.getEquipment()) {
                    if (item.getId() == 0) {
                        saveAmmunition(item);
                    }
                    insertStmt.setInt(1, knight.getId());
                    insertStmt.setInt(2, item.getId());
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }
        }
    }

    public void saveAmmunition(Ammunition item) throws SQLException {
        String sql;
        if (item.getId() > 0) {
            sql = "UPDATE ammunition SET name=?, type=?, material=?, weight=?, price=?, protection_level=?, damage=? WHERE id=?";
        } else {
            sql = "INSERT INTO ammunition (name, type, material, weight, price, protection_level, damage) VALUES (?, ?, ?, ?, ?, ?, ?)";
        }

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, item.getName());
            pstmt.setString(2, item.getClass().getSimpleName());
            pstmt.setString(3, item.getMaterial());
            pstmt.setDouble(4, item.getWeight());
            pstmt.setDouble(5, item.getPrice());
            
            int prot = (item instanceof Armor) ? ((Armor) item).getDefense() : 0;
            int dmg = (item instanceof Weapon) ? ((Weapon) item).getDamage() : 0;
            
            pstmt.setInt(6, prot);
            pstmt.setInt(7, dmg);

            if (item.getId() > 0) {
                pstmt.setInt(8, item.getId());
                pstmt.executeUpdate();
            } else {
                pstmt.executeUpdate();
                ResultSet gk = pstmt.getGeneratedKeys();
                if (gk.next()) {
                    item.setId(gk.getInt(1));
                }
            }
        }
    }

    public List<Knight> getAllKnights() {
        List<Knight> knights = new ArrayList<>();
        String sql = "SELECT * FROM knights ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Knight knight = new Knight(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("height"),
                        rs.getDouble("weight"),
                        rs.getInt("strength"),
                        rs.getInt("endurance"),
                        rs.getDouble("max_weight")
                );
                loadEquipment(knight);
                knights.add(knight);
            }
        } catch (SQLException e) {
            LoggerService.logCriticalError("Помилка при отриманні лицарів", e);
        }
        return knights;
    }

    private void loadEquipment(Knight knight) throws SQLException {
        String sql = "SELECT a.* FROM ammunition a " +
                     "JOIN knight_equipment ke ON a.id = ke.ammunition_id " +
                     "WHERE ke.knight_id = ?";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, knight.getId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                knight.equip(mapAmmunition(rs));
            }
        }
    }

    public List<Ammunition> getAllAmmunition() {
        List<Ammunition> list = new ArrayList<>();
        String sql = "SELECT * FROM ammunition";
        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapAmmunition(rs));
            }
        } catch (SQLException e) {
            LoggerService.logCriticalError("Помилка при отриманні всієї амуніції", e);
        }
        return list;
    }

    private Ammunition mapAmmunition(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        String type = rs.getString("type");
        String material = rs.getString("material");
        double weight = rs.getDouble("weight");
        double price = rs.getDouble("price");
        int prot = rs.getInt("protection_level");
        int dmg = rs.getInt("damage");

        Ammunition item;
        switch (type) {
            case "Sword": item = new Sword(name, weight, price, material, dmg); break;
            case "Weapon": item = new Weapon(name, weight, price, material, dmg); break;
            case "Helmet": item = new Helmet(name, weight, price, material, prot); break;
            case "Boots": item = new Boots(name, weight, price, material, prot); break;
            case "Shield": item = new Shield(name, weight, price, material, prot); break;
            case "Armor": default: item = new Armor(name, weight, price, material, prot); break;
        }
        item.setId(id);
        return item;
    }
}
