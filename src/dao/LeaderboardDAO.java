package dao;

import util.DBConnection;
import java.sql.*;
import java.util.*;

public class LeaderboardDAO {

    // ===============================
    // TAMBAH / UPDATE POINT USER
    // ===============================
    public void addPoint(int userId, int point) {
        String sql = """
            INSERT INTO leaderboard(user_id, point)
            VALUES (?, ?)
            ON DUPLICATE KEY UPDATE point = point + ?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, point);
            ps.setInt(3, point);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===============================
    // AMBIL DATA LEADERBOARD
    // ===============================
    public Map<String, Integer> getTopUsers() {
        Map<String, Integer> map = new LinkedHashMap<>();

        String sql = """
            SELECT u.username, l.point
            FROM leaderboard l
            JOIN users u ON l.user_id = u.id
            ORDER BY l.point DESC
            LIMIT 10
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                map.put(
                        rs.getString("username"),
                        rs.getInt("point")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }
}
