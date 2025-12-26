package dao;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class LeaderboardDAO {

    // =========================
    // ✅ API BARU (sesuai schema leaderboard kamu)
    // =========================
    public boolean upsert(int userId, String username, int addPoint, int addHours) {
        String sql = """
            INSERT INTO leaderboard(user_id, username, total_point, total_transaction, total_hours)
            VALUES (?, ?, ?, 1, ?)
            ON DUPLICATE KEY UPDATE
                username = VALUES(username),
                total_point = total_point + VALUES(total_point),
                total_transaction = total_transaction + 1,
                total_hours = total_hours + VALUES(total_hours),
                last_update = CURRENT_TIMESTAMP
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, username);
            ps.setInt(3, addPoint);
            ps.setInt(4, addHours);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================
    // ✅ ADAPTER BUAT KODE LAMA
    // JdbcLeaderboardRepository masih manggil addPoint(userId, point)
    // Karena schema leaderboard butuh username, kita ambil username dari tabel users.
    // =========================
    public boolean addPoint(int userId, int point) {
        String qUser = "SELECT username FROM users WHERE id=? LIMIT 1";

        try (Connection c = DBConnection.getConnection()) {

            String username = null;
            try (PreparedStatement ps = c.prepareStatement(qUser)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) username = rs.getString("username");
                }
            }

            if (username == null) username = "user-" + userId;

            // addHours = 0 karena method lama tidak bawa durasi
            return upsert(userId, username, point, 0);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================
    // ✅ UNTUK UI LEADERBOARD
    // =========================
    public Map<String, Integer> getTopUsers(int limit) {
        Map<String, Integer> map = new LinkedHashMap<>();

        String sql = """
            SELECT username, total_point
            FROM leaderboard
            ORDER BY total_point DESC
            LIMIT ?
        """;

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, Math.max(1, limit));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString("username"), rs.getInt("total_point"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    public Map<String, Integer> getTopUsers() {
        return getTopUsers(10);
    }
}
