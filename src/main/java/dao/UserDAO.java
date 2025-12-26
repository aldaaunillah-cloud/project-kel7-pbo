package dao;

import util.DBConnection;

import java.sql.*;

public class UserDAO {

    /** Login dan ambil userId */
    public Integer loginAndGetId(String username, String passwordHash) throws Exception {
        String sql = "SELECT id FROM users WHERE username=? AND password=? LIMIT 1";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, passwordHash);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("id") : null;
            }
        }
    }

    /** Register: return false kalau username sudah ada (UNIQUE). */
    public boolean register(String username, String passwordHash) throws Exception {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, passwordHash);
            return ps.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException dup) {
            // Duplicate username (UNIQUE)
            return false;

        } catch (SQLException e) {
            // Jaga-jaga kalau driver lempar SQLException umum untuk duplicate
            if ("23000".equals(e.getSQLState()) || e.getErrorCode() == 1062) {
                return false;
            }
            throw e;
        }
    }

    public String getUsernameById(int userId) throws Exception {
        String sql = "SELECT username FROM users WHERE id=? LIMIT 1";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("username") : null;
            }
        }
    }
}
