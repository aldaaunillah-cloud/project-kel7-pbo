package app;

import javax.swing.SwingUtilities;
import ui.LoginFrame;
import ui.theme.ThemeConfig;
import dao.PSDAO;

public class Main {

    public static void main(String[] args) {

        // Jalankan UI di Event Dispatch Thread (best practice Swing)
        SwingUtilities.invokeLater(() -> {
            try {
                // ===== THEME =====
                ThemeConfig.apply();

                // ===== OPSI 3: RESET STATUS PS =====
                // Biar setiap aplikasi dibuka, semua PS kembali AVAILABLE
                PSDAO psDAO = new PSDAO();
                psDAO.resetAllToAvailable();

                // ===== LOGIN =====
                new LoginFrame().setVisible(true);

                System.out.println("Aplikasi Rental PS berhasil dijalankan.");

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Gagal menjalankan aplikasi.");
            }
        });
    }
}
