package ui;

import app.AppContext;
import net.miginfocom.swing.MigLayout;
import service.AuthException;
import util.SwingAsync;
import util.UiDialogs;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {

    private final AppContext ctx;
    private final LoginFrame loginFrame;

    private final JTextField usernameField = new JTextField(22);
    private final JPasswordField passField = new JPasswordField(22);
    private final JPasswordField confirmField = new JPasswordField(22);

    private final JButton btnRegister = new JButton("Daftar");
    private final JButton btnBack = new JButton("Kembali");
    private final JLabel statusLabel = new JLabel(" ");

    public RegisterFrame(AppContext ctx, LoginFrame loginFrame) {
        this.ctx = ctx;
        this.loginFrame = loginFrame;

        setTitle("Buat Akun - Rental PS");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(520, 360);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(buildCard(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        hookActions();
    }

    private JComponent buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));

        JLabel title = new JLabel("Buat Akun");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        JLabel subtitle = new JLabel("Daftar untuk mulai rental PlayStation");
        subtitle.setForeground(new Color(140, 140, 140));

        JPanel text = new JPanel(new MigLayout("insets 0, fillx", "[grow]", "[]2[]"));
        text.setOpaque(false);
        text.add(title, "wrap");
        text.add(subtitle, "wrap");

        p.add(text, BorderLayout.CENTER);
        return p;
    }

    private JComponent buildCard() {
        JPanel card = new JPanel(new MigLayout("insets 18, fillx, wrap 2", "[120!][grow]", "[]12[]12[]12[]"));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 16, 0, 16),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220)),
                        BorderFactory.createEmptyBorder(16, 16, 16, 16)
                )
        ));

        card.add(new JLabel("Username"));
        card.add(usernameField, "growx");

        card.add(new JLabel("Password"));
        card.add(passField, "growx");

        card.add(new JLabel("Confirm"));
        card.add(confirmField, "growx");

        JCheckBox show = new JCheckBox("Tampilkan password");
        show.addActionListener(e -> {
            char echo = show.isSelected() ? 0 : '•';
            passField.setEchoChar(echo);
            confirmField.setEchoChar(echo);
        });

        card.add(show, "span 2, gaptop 6");
        return card;
    }

    private JComponent buildFooter() {
        JPanel p = new JPanel(new MigLayout("insets 12 16 16 16, fillx", "[grow][right]", "[]8[]"));
        p.add(statusLabel, "growx");

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.add(btnBack);
        btns.add(btnRegister);
        p.add(btns, "wrap");

        JLabel hint = new JLabel("Username: huruf/angka/_ (min 3). Password min 4.");
        hint.setForeground(new Color(140, 140, 140));
        p.add(hint, "span 2");

        return p;
    }

    private void hookActions() {
        btnBack.addActionListener(e -> {
            dispose();
            if (loginFrame != null) loginFrame.setVisible(true);
        });

        btnRegister.addActionListener(e -> doRegister());
        confirmField.addActionListener(e -> doRegister()); // enter = daftar
    }

    private void doRegister() {
    setBusy(true, "Membuat akun...");

    String username = usernameField.getText();
    String pw = new String(passField.getPassword());
    String cf = new String(confirmField.getPassword());

    SwingAsync.run(
            () -> {
                try {
                    ctx.authService.register(username, pw, cf);
                    return true;
                } catch (Exception ex) {
                    // penting: lempar apa adanya biar bisa dibaca di onError
                    throw new RuntimeException(ex);
                }
            },
            ok -> {
                setBusy(false, " ");
                UiDialogs.info(this, "Akun berhasil dibuat. Silakan login.");

                if (loginFrame != null) {
                    loginFrame.prefillUsername(username == null ? "" : username.trim());
                    loginFrame.setVisible(true);
                }
                dispose();
            },
            err -> {
                setBusy(false, " ");

                // ambil akar error (root cause)
                Throwable root = err;
                while (root.getCause() != null) root = root.getCause();

                // default message
                String msg = "Gagal membuat akun. Cek koneksi database.";

                // kalau auth error (username dipakai / validasi) tampilkan itu
                if (root instanceof AuthException) {
                    msg = root.getMessage();
                }

                UiDialogs.error(this, msg, root);
            }
    );
}

    private void setBusy(boolean busy, String text) {
        statusLabel.setText(text);
        btnRegister.setEnabled(!busy);
        btnBack.setEnabled(!busy);
        setCursor(busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }
}
