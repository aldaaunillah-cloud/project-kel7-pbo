package ui;

import dao.PSDAO;
import model.PSItem;
import service.RentalService;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.util.List;

public class RentalFrame extends JFrame {

    private final RentalService rentalService = new RentalService();
    private final PSDAO psDAO = new PSDAO();

    private JTextField userIdField;
    private JTextField hoursField;
    private JComboBox<PSItem> psCombo;

    public RentalFrame() {
        setTitle("Rental PS");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(520, 280);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;

        userIdField = new JTextField();
        hoursField = new JTextField();
        psCombo = new JComboBox<>();

        setDigitsOnly(userIdField);
        setDigitsOnly(hoursField);

        // Row 1: User ID
        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0;
        formPanel.add(new JLabel("User ID"), gc);
        gc.gridx = 1; gc.gridy = 0; gc.weightx = 1;
        formPanel.add(userIdField, gc);

        // Row 2: PS Dropdown
        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0;
        formPanel.add(new JLabel("Pilih PS (AVAILABLE)"), gc);
        gc.gridx = 1; gc.gridy = 1; gc.weightx = 1;
        formPanel.add(psCombo, gc);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadAvailablePS());
        gc.gridx = 2; gc.gridy = 1; gc.weightx = 0;
        formPanel.add(refreshBtn, gc);

        // Row 3: Durasi
        gc.gridx = 0; gc.gridy = 2; gc.weightx = 0;
        formPanel.add(new JLabel("Durasi (jam)"), gc);
        gc.gridx = 1; gc.gridy = 2; gc.weightx = 1;
        formPanel.add(hoursField, gc);

        JButton rentBtn = new JButton("Sewa");
        rentBtn.addActionListener(e -> handleRent());

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(rentBtn, BorderLayout.SOUTH);

        add(mainPanel);

        loadAvailablePS();
        setVisible(true);
    }

    private void loadAvailablePS() {
        psCombo.removeAllItems();
        List<PSItem> items = psDAO.getAvailablePSItems();
        for (PSItem it : items) psCombo.addItem(it);

        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Tidak ada PS AVAILABLE.\nCek tabel playstation, pastikan status='AVAILABLE'.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleRent() {
        String u = userIdField.getText().trim();
        String h = hoursField.getText().trim();
        PSItem selected = (PSItem) psCombo.getSelectedItem();

        if (u.isEmpty() || h.isEmpty()) {
            JOptionPane.showMessageDialog(this, "User ID dan Durasi wajib diisi!");
            return;
        }
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Pilih PS yang tersedia dulu!");
            return;
        }

        int userId, hours;
        try {
            userId = Integer.parseInt(u);
            hours = Integer.parseInt(h);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "User ID dan Durasi harus angka!");
            return;
        }

        if (userId <= 0 || hours <= 0) {
            JOptionPane.showMessageDialog(this, "Nilai harus > 0!");
            return;
        }

        int psId = selected.getId();

        boolean ok = rentalService.createRental(userId, psId, hours);
        JOptionPane.showMessageDialog(this, ok ? "Rental berhasil ✅" : "Rental gagal ❌");

        if (ok) {
            hoursField.setText("");
            loadAvailablePS(); // PS yang disewa jadi RENTED -> hilang dari dropdown
        }
    }

    private void setDigitsOnly(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DigitsOnlyFilter());
    }

    private static class DigitsOnlyFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, javax.swing.text.AttributeSet attr)
                throws BadLocationException {
            if (string != null && string.matches("\\d+")) super.insertString(fb, offset, string, attr);
            else Toolkit.getDefaultToolkit().beep();
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs)
                throws BadLocationException {
            if (text == null) return;
            if (text.isEmpty() || text.matches("\\d+")) super.replace(fb, offset, length, text, attrs);
            else Toolkit.getDefaultToolkit().beep();
        }
    }
}
