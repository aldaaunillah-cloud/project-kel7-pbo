package dao;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;

public class TransactionDAO {

    /**
     * Simpan transaksi + update leaderboard.
     * Versi AMAN: tidak update status PS / rental closing (biar gak rollback gara-gara kolom beda).
     */
    public boolean saveTransaction(int rentalId, double total) {

        // rental utama (tabel rental)
        String qRental1 = "SELECT user_id, hours AS h FROM rental WHERE id=? LIMIT 1";
        // fallback kalau ternyata data ada di rentals (tabel plural)
        String qRental2 = "SELECT user_id, duration AS h FROM rentals WHERE id=? LIMIT 1";

        String qUsername = "SELECT username FROM users WHERE id=? LIMIT 1";

        // tabel kamu: transactions
        String qInsertTx = "INSERT INTO transactions(rental_id, total_price) VALUES (?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            int userId = -1;
            int hours = -1;

            // 1) ambil rental info dari rental
            try (PreparedStatement ps = conn.prepareStatement(qRental1)) {
                ps.setInt(1, rentalId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        userId = rs.getInt("user_id");
                        hours = rs.getInt("h");
                    }
                }
            }

            // 2) kalau tidak ketemu, coba dari rentals
            if (userId == -1) {
                try (PreparedStatement ps = conn.prepareStatement(qRental2)) {
                    ps.setInt(1, rentalId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            userId = rs.getInt("user_id");
                            hours = rs.getInt("h");
                        }
                    }
                }
            }

            if (userId == -1 || hours <= 0) {
                conn.rollback();
                System.out.println("[TX] Rental tidak ditemukan untuk rentalId=" + rentalId);
                return false;
            }

            // 3) ambil username
            String username;
            try (PreparedStatement ps = conn.prepareStatement(qUsername)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        System.out.println("[TX] User tidak ditemukan userId=" + userId);
                        return false;
                    }
                    username = rs.getString("username");
                }
            }

            // 4) insert transaksi (unik rental_id, anti dobel)
            try (PreparedStatement ps = conn.prepareStatement(qInsertTx)) {
                ps.setInt(1, rentalId);
                ps.setDouble(2, total);
                ps.executeUpdate();
            } catch (SQLIntegrityConstraintViolationException dup) {
                conn.rollback();
                System.out.println("[TX] Duplicate transaction untuk rentalId=" + rentalId);
                return false;
            }

            // 5) update leaderboard sesuai schema kamu
            int addPoint = calcPoint(hours, total);
            LeaderboardDAO lb = new LeaderboardDAO();
            boolean okLb = lb.upsert(userId, username, addPoint, hours);
            if (!okLb) {
                conn.rollback();
                System.out.println("[TX] Upsert leaderboard gagal userId=" + userId);
                return false;
            }

            conn.commit();
            System.out.println("[TX] SUCCESS rentalId=" + rentalId + " user=" + username + " +point=" + addPoint);
            return true;

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (Exception ignore) {}
            e.printStackTrace();
            return false;

        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception ignore) {}
        }
    }

    private int calcPoint(int hours, double total) {
        int p = 10 + (hours * 5);
        if (hours >= 3) p += 10;
        if (total >= 50000) p += 5;
        return p;
    }
}
