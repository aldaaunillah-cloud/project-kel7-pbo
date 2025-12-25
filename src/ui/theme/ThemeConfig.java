package ui.theme;

import javax.swing.*;

public class ThemeConfig {

    public static void apply() {
        try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception ignored) {}
    }
}
