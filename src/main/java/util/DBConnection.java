package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {

    private DBConnection() {}

    /**
     * Ambil koneksi DB. Bisa override lewat JVM args:
     * -DDB_URL=...
     * -DDB_USER=...
     * -DDB_PASS=...
     */
    public static Connection getConnection() throws SQLException {
        String url  = System.getProperty("DB_URL",  safeConfigUrl());
        String user = System.getProperty("DB_USER", safeConfigUser());
        String pass = System.getProperty("DB_PASS", safeConfigPass());

        if (url == null || url.isBlank()) {
            throw new SQLException("DB_URL kosong. Isi di util.Config.DB_URL atau set -DDB_URL=jdbc:mysql://...");
        }

        // Paksa load driver (biar tidak "No suitable driver found")
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver tidak ditemukan. Pastikan dependency mysql-connector-j ada di pom.xml.", e);
        }

        try {
            return DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            throw new SQLException(
                    "Gagal konek DB. url=" + url + ", user=" + user + ". Detail: " + e.getMessage(),
                    e
            );
        }
    }

    // ===============================
    // Fallback ke util.Config (kalau ada)
    // ===============================
    private static String safeConfigUrl() {
        try {
            return (String) Config.class.getField("DB_URL").get(null);
        } catch (Exception ignore) {
            // fallback default (biar tetap bisa jalan walau Config tidak ada)
            return "jdbc:mysql://localhost:3306/rental_ps?serverTimezone=UTC";
        }
    }

    private static String safeConfigUser() {
        try {
            return (String) Config.class.getField("DB_USER").get(null);
        } catch (Exception ignore) {
            return "root";
        }
    }

    private static String safeConfigPass() {
        try {
            return (String) Config.class.getField("DB_PASS").get(null);
        } catch (Exception ignore) {
            return "";
        }
    }

    // ===============================
    // MAIN untuk test koneksi DB
    // ===============================
    public static void main(String[] args) {
        try (Connection c = getConnection()) {
            System.out.println("✅ DB CONNECTED OK: " + c.getMetaData().getURL());
        } catch (Exception e) {
            System.out.println("❌ DB CONNECT FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
