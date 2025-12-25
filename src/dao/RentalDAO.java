package dao;

import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class RentalDAO {

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
}
