package dao;

import model.PSItem;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PSDAO {

    // ✅ Dipakai PSService: ambil semua nama PS
    public List<String> getAllPS() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT name FROM playstation ORDER BY id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ✅ Dipakai PSService: ambil nama PS yang AVAILABLE
    public List<String> getAvailablePS() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT name FROM playstation WHERE status='AVAILABLE' ORDER BY id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            // fallback: kalau kolom status belum ada
            return getAllPS();
        }
        return list;
    }

    // ✅ Untuk dropdown: ambil id+name yang AVAILABLE
    public List<PSItem> getAvailablePSItems() {
        List<PSItem> list = new ArrayList<>();
        String sql = "SELECT id, name FROM playstation WHERE status='AVAILABLE' ORDER BY id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new PSItem(rs.getInt("id"), rs.getString("name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ✅ Cek available
    public boolean isAvailable(int psId) {
        String sql = "SELECT status FROM playstation WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, psId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false;
                return "AVAILABLE".equalsIgnoreCase(rs.getString("status"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ Update status
    public boolean updateStatus(int psId, String status) {
        String sql = "UPDATE playstation SET status=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, psId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
