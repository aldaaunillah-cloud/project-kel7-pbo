package ui.theme;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;

public class ThemeConfig {

    public static void apply() {
        FlatDarkLaf.setup();

        // ===== GLOBAL COLORS =====
        UIManager.put("Panel.background", new java.awt.Color(15, 23, 42));
        UIManager.put("Frame.background", new java.awt.Color(15, 23, 42));

        UIManager.put("Label.foreground", new java.awt.Color(229, 231, 235));
        UIManager.put("TextField.background", new java.awt.Color(17, 24, 39));
        UIManager.put("TextField.foreground", new java.awt.Color(229, 231, 235));
        UIManager.put("TextField.caretForeground", new java.awt.Color(34, 211, 238));

        UIManager.put("ComboBox.background", new java.awt.Color(17, 24, 39));
        UIManager.put("ComboBox.foreground", new java.awt.Color(229, 231, 235));

        UIManager.put("Button.background", new java.awt.Color(34, 211, 238));
        UIManager.put("Button.foreground", java.awt.Color.BLACK);
        UIManager.put("Button.arc", 12);

        UIManager.put("ScrollPane.background", new java.awt.Color(15, 23, 42));
        UIManager.put("Table.background", new java.awt.Color(17, 24, 39));
        UIManager.put("Table.foreground", new java.awt.Color(229, 231, 235));
        UIManager.put("Table.selectionBackground", new java.awt.Color(236, 72, 153));
    }
}
