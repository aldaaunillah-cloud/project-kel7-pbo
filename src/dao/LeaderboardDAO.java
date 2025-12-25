package dao;

import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class LeaderboardDAO {

    public Map<String, Integer> getTopUsers() {
        Map<String, Integer> data = new LinkedHashMap<>();
        String sql = """
            SELECT u.username, COUNT(r.id) AS total
            FROM users u
            JOIN rental r ON u.id = r.user_id
            GROUP BY u.username
            ORDER BY total DESC
            LIMIT 5
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                data.put(rs.getString("username"), rs.getInt("total"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
