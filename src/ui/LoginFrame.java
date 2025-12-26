package ui;

import service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final AuthService authService = new AuthService();

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginBtn;

    public LoginFrame() {
        setTitle("Login - Rental PS");
        setMinimumSize(new Dimension(360, 220));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setContentPane(buildUI());
        setVisible(true);
    }

    // ================= UI =================
    private JPanel buildUI() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("LOGIN");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginBtn = new JButton("Login");

        // Row 0 - Username
        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0;
        form.add(new JLabel("Username"), gc);

        gc.gridx = 1; gc.gridy = 0; gc.weightx = 1;
        form.add(usernameField, gc);

        // Row 1 - Password
        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0;
        form.add(new JLabel("Password"), gc);

        gc.gridx = 1; gc.gridy = 1; gc.weightx = 1;
        form.add(passwordField, gc);

        root.add(form, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(loginBtn);
        root.add(bottom, BorderLayout.SOUTH);

        // event
        loginBtn.addActionListener(e -> loginAsync());
        getRootPane().setDefaultButton(loginBtn); // tekan Enter = login

        return root;
    }

    // ================= LOGIC =================
    private void loginAsync() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username dan password wajib diisi!",
                    "Validasi",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        setLoading(true);

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return authService.login(username, password);
            }

            @Override
            protected void done() {
                try {
                    boolean ok = get();
                    if (ok) {
                        new DashboardFrame();
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(
                                LoginFrame.this,
                                "Username atau password salah",
                                "Login gagal",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                            LoginFrame.this,
                            "Error: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                } finally {
                    setLoading(false);
                }
            }
        }.execute();
    }

    private void setLoading(boolean loading) {
        loginBtn.setEnabled(!loading);
        setCursor(loading
                ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
                : Cursor.getDefaultCursor());
    }
}
