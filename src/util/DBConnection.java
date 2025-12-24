package util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(
                Config.DB_URL,
                Config.DB_USER,
                Config.DB_PASS
        );
    }
}
