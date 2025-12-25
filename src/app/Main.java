package app;

import ui.LoginFrame;
import ui.theme.ThemeConfig;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            ThemeConfig.apply();
            new LoginFrame();
        });
    }
}
