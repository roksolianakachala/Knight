package service;

import model.Knight;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KnightRepository {

    public void saveKnight(Knight knight) {
        String sql = "INSERT INTO knights (name, height, weight, strength, endurance) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, knight.getName());
            pstmt.setDouble(2, knight.getHeight());
            pstmt.setDouble(3, knight.getWeight());
            pstmt.setInt(4, knight.getStrength());
            pstmt.setInt(5, knight.getEndurance());
            
            pstmt.executeUpdate();
            LoggerService.logInfo("Knight saved to database: " + knight.getName());
        } catch (SQLException e) {
            LoggerService.logCriticalError("Error saving knight to database", e);
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
                        rs.getString("name"),
                        rs.getDouble("height"),
                        rs.getDouble("weight"),
                        rs.getInt("strength"),
                        rs.getInt("endurance")
                );
                knights.add(knight);
            }
        } catch (SQLException e) {
            LoggerService.logCriticalError("Error fetching knights from database", e);
        }
        return knights;
    }
}
