package dao;

import util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PSDAO {

    public List<String> getAllPS() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT name FROM playstation";

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
}
