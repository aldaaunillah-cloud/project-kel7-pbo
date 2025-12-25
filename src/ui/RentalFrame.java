package ui;

import service.RentalService;

import javax.swing.*;
import java.awt.*;

public class RentalFrame extends JFrame {

    private final RentalService rentalService = new RentalService();

    public RentalFrame() {
        setTitle("Rental PS");
        setSize(350, 250);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        JTextField userIdField = new JTextField();
        JTextField psIdField = new JTextField();
        JTextField hoursField = new JTextField();

        formPanel.add(new JLabel("User ID"));
        formPanel.add(userIdField);
        formPanel.add(new JLabel("PS ID"));
        formPanel.add(psIdField);
        formPanel.add(new JLabel("Durasi (jam)"));
        formPanel.add(hoursField);

        JButton rentBtn = new JButton("Sewa");

        rentBtn.addActionListener(e -> {
            try {
                boolean success = rentalService.rentPS(
                        Integer.parseInt(userIdField.getText()),
                        Integer.parseInt(psIdField.getText()),
                        Integer.parseInt(hoursField.getText())
                );

                JOptionPane.showMessageDialog(this,
                        success ? "Rental berhasil" : "Rental gagal");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Input tidak valid");
            }
        });

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(rentBtn, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }
}
