package util;

import javax.swing.*;
import java.awt.*;

/** Helper dialog agar exception handling rapi & user-friendly. */
public final class UiDialogs {
    private UiDialogs(){}

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void warn(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Peringatan", JOptionPane.WARNING_MESSAGE);
    }

    public static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Terjadi Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void error(Component parent, String message, Throwable t) {
        // tampilkan pesan ramah, detail tetap ke console untuk debugging
        System.err.println("[ERROR] " + message);
        if (t != null) t.printStackTrace();

        JOptionPane.showMessageDialog(
                parent,
                message + "\n\nSilakan coba lagi. Jika tetap gagal, hubungi admin / cek koneksi DB.",
                "Terjadi Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
