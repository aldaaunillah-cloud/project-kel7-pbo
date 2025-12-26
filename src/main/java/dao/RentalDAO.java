package dao;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class RentalDAO {

    // Versi lama (tetap dipertahankan biar kode lain tidak rusak)
    public boolean createRental(int userId, int psId, int hours) {
        String sql = "INSERT INTO rental(user_id, ps_id, hours) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, psId);
            ps.setInt(3, hours);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ NEW: bikin rental dan balikin rentalId (buat dipakai TransactionDAO.saveTransaction)
    public Integer createRentalReturnId(int userId, int psId, int hours) {
        String sql = "INSERT INTO rental(user_id, ps_id, hours) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, userId);
            ps.setInt(2, psId);
            ps.setInt(3, hours);

            int ok = ps.executeUpdate();
            if (ok <= 0) return null;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ✅ dipakai RentalService.getUserRentals()
    public String getUserRentals(int userId) {
        // coba pakai created_at (kalau ada), kalau tidak ada fallback
        String sqlWithCreatedAt =
                "SELECT id, ps_id, hours, created_at FROM rental WHERE user_id=? ORDER BY id DESC";
        String sqlBasic =
                "SELECT id, ps_id, hours FROM rental WHERE user_id=? ORDER BY id DESC";

        try (Connection conn = DBConnection.getConnection()) {
            try {
                return runQuery(conn, sqlWithCreatedAt, userId, true);
            } catch (Exception ignored) {
                return runQuery(conn, sqlBasic, userId, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }
    }

    private String runQuery(Connection conn, String sql, int userId, boolean hasCreatedAt) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                boolean first = true;
                while (rs.next()) {
                    if (!first) sb.append(",");
                    first = false;

                    int id = rs.getInt("id");
                    int psId = rs.getInt("ps_id");
                    int hours = rs.getInt("hours");

                    sb.append("{");
                    sb.append("\"id\":").append(id).append(",");
                    sb.append("\"psId\":").append(psId).append(",");
                    sb.append("\"hours\":").append(hours);

                    if (hasCreatedAt) {
                        Object createdAt = rs.getObject("created_at");
                        sb.append(",\"createdAt\":\"")
                                .append(createdAt == null ? "" : escape(createdAt.toString()))
                                .append("\"");
                    }

                    sb.append("}");
                }
            }
        }

        sb.append("]");
        return sb.toString();
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
