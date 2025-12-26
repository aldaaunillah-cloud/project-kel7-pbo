package ui;

import app.AppContext;
import model.User;
import net.miginfocom.swing.MigLayout;
import util.SwingAsync;
import util.UiDialogs;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class LoginFrame extends JFrame {

    private final AppContext ctx;

    private final JTextField usernameField = new JTextField(22);
    private final JPasswordField passwordField = new JPasswordField(22);

    private final JButton loginBtn = new JButton("Login");
    private final JButton registerBtn = new JButton("Buat Akun");
    private final JLabel statusLabel = new JLabel(" ");

    public LoginFrame(AppContext ctx) {
        this.ctx = ctx;

        setTitle("Login - Rental PS");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(520, 330);
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

        JLabel title = new JLabel("Rental PlayStation");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        JLabel subtitle = new JLabel("Login untuk mengelola rental & melihat status PS real-time");
        subtitle.setForeground(new Color(140, 140, 140));

        JPanel text = new JPanel(new MigLayout("insets 0, fillx", "[grow]", "[]2[]"));
        text.setOpaque(false);
        text.add(title, "wrap");
        text.add(subtitle, "wrap");

        p.add(text, BorderLayout.CENTER);
        return p;
    }

    private JComponent buildCard() {
        JPanel card = new JPanel(new MigLayout("insets 18, fillx, wrap 2", "[120!][grow]", "[]12[]12[]"));
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
        card.add(passwordField, "growx");

        return card;
    }

    private JComponent buildFooter() {
        JPanel p = new JPanel(new MigLayout("insets 12 16 16 16, fillx", "[grow][right]", "[]8[]"));
        p.add(statusLabel, "growx");

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.add(registerBtn);
        btns.add(loginBtn);
        p.add(btns, "wrap");

        JLabel hint = new JLabel("Tip: Kalau belum punya akun, klik “Buat Akun”.");
        hint.setForeground(new Color(140, 140, 140));
        p.add(hint, "span 2");

        return p;
    }

    private void hookActions() {
        loginBtn.addActionListener(e -> doLogin());
        passwordField.addActionListener(e -> doLogin()); // enter = login

        registerBtn.addActionListener(e -> {
            setVisible(false);
            new RegisterFrame(ctx, this).setVisible(true);
        });
    }

    private void doLogin() {
        setLoading(true);

        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            setLoading(false);
            UiDialogs.error(this, "Username dan password wajib diisi.", null);
            return;
        }

        SwingAsync.run(
                () -> ctx.authService.login(username, password),
                (Optional<User> userOpt) -> {
                    setLoading(false);

                    if (userOpt.isEmpty()) {
                        UiDialogs.error(this, "Login gagal. Username / password salah.", null);
                        return;
                    }

                    User user = userOpt.get();
                    setVisible(false);
                    new DashboardFrame(ctx, user).setVisible(true);
                },
                err -> {
                    setLoading(false);
                    UiDialogs.error(this, "Terjadi error saat login (cek DB / server).", err);
                }
        );
    }

    private void setLoading(boolean loading) {
        loginBtn.setEnabled(!loading);
        registerBtn.setEnabled(!loading);
        statusLabel.setText(loading ? "Memproses login..." : " ");
        setCursor(loading ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }

    /** Dipanggil dari RegisterFrame setelah sukses daftar */
    public void prefillUsername(String username) {
        usernameField.setText(username);
        passwordField.setText("");
        usernameField.requestFocus();
    }
}
