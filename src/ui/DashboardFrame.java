package ui;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {

    public DashboardFrame() {
        setTitle("Dashboard - Rental PS");
        setSize(420, 260);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(2, 1, 12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        JButton rentalBtn = new JButton("Rental PS");
        JButton adminBtn = new JButton("Admin / Leaderboard");

        rentalBtn.setFont(rentalBtn.getFont().deriveFont(Font.BOLD, 14f));
        adminBtn.setFont(adminBtn.getFont().deriveFont(Font.BOLD, 14f));

        rentalBtn.addActionListener(e -> new RentalFrame());
        adminBtn.addActionListener(e -> new AdminFrame());

        panel.add(rentalBtn);
        panel.add(adminBtn);

        add(panel);
        setVisible(true);
    }
}
