package app;

import ui.LoginFrame;
import ui.theme.ThemeConfig;
import util.SwingAsync;
import util.UiDialogs;

import javax.swing.*;

/**
 * Entry point (client Swing).
 * Rubrik:
 * - Inisialisasi UI di EDT
 * - Operasi DB (reset) di background agar tidak freeze
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                ThemeConfig.apply();

                AppContext ctx = AppContext.create();

                LoginFrame login = new LoginFrame(ctx);
                login.setVisible(true);

                // Reset status PS (2-tier saja), tapi jangan di EDT.
                if (!AppConfig.IS_3_TIER) {
                    SwingAsync.run(
                            () -> { ctx.psRepo.resetAllToAvailable(); return true; },
                            ok -> {},
                            err -> UiDialogs.error(login, "Gagal reset status PS di database.", err)
                    );
                }

                System.out.println("Aplikasi Rental PS berjalan. Mode: " + (AppConfig.IS_3_TIER ? "3-Tier" : "2-Tier"));
            } catch (Exception e) {
                e.printStackTrace();
                UiDialogs.error(null, "Gagal menjalankan aplikasi.", e);
            }
        });
    }
}
