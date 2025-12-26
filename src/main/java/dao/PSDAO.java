package dao;

import util.DBConnection;
import java.sql.*;
import java.util.*;
import model.PSItem;

public class PSDAO {

    // ===============================
    // RESET OTOMATIS (OPSI 3)
    // ===============================
    public void resetAllToAvailable() {
        String sql = "UPDATE playstation SET status='AVAILABLE'";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===============================
    // DROPDOWN PS (AVAILABLE)
    // ===============================
    public List<PSItem> getAvailablePSItems() {
        List<PSItem> list = new ArrayList<>();
        String sql = "SELECT id, name FROM playstation WHERE status='AVAILABLE'";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new PSItem(
                        rs.getInt("id"),
                        rs.getString("name")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===============================
    // CEK STATUS
    // ===============================
    public boolean isAvailable(int psId) {
        String sql = "SELECT status FROM playstation WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, psId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && "AVAILABLE".equals(rs.getString("status"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ===============================
    // UPDATE STATUS
    // ===============================
    public void updateStatus(int psId, String status) {
        String sql = "UPDATE playstation SET status=? WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, psId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===============================
    // AMBIL NAMA PS
    // ===============================
    public String getPSNameById(int psId) {
        String sql = "SELECT name FROM playstation WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, psId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("name");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown PS";
    }

    // ===============================
    // LEGACY (BIAR GA ERROR)
    // ===============================
    public List<String> getAllPS() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT name FROM playstation";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(rs.getString("name"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> getAvailablePS() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT name FROM playstation WHERE status='AVAILABLE'";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(rs.getString("name"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===============================
    // LIST SEMUA PS + STATUS (untuk Dashboard)
    // ===============================
    public List<model.PS> getAllPSWithStatus() {
        List<model.PS> list = new ArrayList<>();
        String sql = "SELECT id, name, status, 10000 as price_per_hour FROM playstation ORDER BY id";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new model.PS(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("status"),
                        rs.getDouble("price_per_hour")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
