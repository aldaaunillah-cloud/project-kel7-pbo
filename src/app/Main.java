package app;

import ui.LoginFrame;

public class Main {
    public static void main(String[] args) {
        // Entry point desktop app
        javax.swing.SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
