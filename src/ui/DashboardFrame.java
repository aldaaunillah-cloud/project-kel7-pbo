package ui;

import javax.swing.*;
import java.awt.*;

public class DashboardFrame extends JFrame {

    public DashboardFrame() {
        setTitle("Dashboard - Rental PS");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JButton rentalBtn = new JButton("Rental PS");
        JButton adminBtn = new JButton("Admin / Laporan");

        rentalBtn.addActionListener(e -> new RentalFrame());
        adminBtn.addActionListener(e -> new AdminFrame());

        panel.add(rentalBtn);
        panel.add(adminBtn);

        add(panel);
        setVisible(true);
    }
}
