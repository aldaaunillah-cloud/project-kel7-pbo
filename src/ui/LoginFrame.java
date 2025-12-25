package ui;

import service.AuthService;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final AuthService authService = new AuthService();

    public LoginFrame() {
        setTitle("Login - Rental PS");
        setSize(350, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel utama
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel form
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        formPanel.add(new JLabel("Username"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password"));
        formPanel.add(passwordField);

        // Tombol login
        JButton loginBtn = new JButton("Login");
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (authService.login(username, password)) {
                new DashboardFrame();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Login gagal");
            }
        });

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(loginBtn, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }
}
