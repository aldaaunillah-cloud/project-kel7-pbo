package util;

import java.sql.Connection;

public class DBConnection {

    public static Connection getConnection() throws Exception {
        return java.sql.DriverManager.getConnection(
                Config.DB_URL,
                Config.DB_USER,
                Config.DB_PASS
        );
    }

    // ===============================
    // MAIN SEMENTARA (UNTUK TEST DB)
    // ===============================
  
       public static void main(String[] args) {
    System.out.println(util.PasswordUtil.hash("password123"));
}

}
