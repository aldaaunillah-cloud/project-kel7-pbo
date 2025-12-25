package app;

import javax.swing.SwingUtilities;
import ui.LoginFrame;
import ui.theme.ThemeConfig;

public class Main {

    public static void main(String[] args) {

        // Jalankan UI di Event Dispatch Thread (best practice Swing)
        SwingUtilities.invokeLater(() -> {
            try {
                // Terapkan tema / look and feel
                ThemeConfig.apply();

                // Tampilkan halaman login
                new LoginFrame().setVisible(true);

                System.out.println("Aplikasi Rental PS berhasil dijalankan.");

            } catch (Exception e) {
                // Jika UI gagal dibuka
                e.printStackTrace();
                System.err.println("Gagal menjalankan aplikasi.");
            }
        });
    }
}
