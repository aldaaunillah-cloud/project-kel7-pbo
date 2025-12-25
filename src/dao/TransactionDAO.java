package dao;

import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class TransactionDAO {

    public boolean saveTransaction(int rentalId, double total) {
        String sql = "INSERT INTO transaction(rental_id, total_price) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, rentalId);
            ps.setDouble(2, total);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
