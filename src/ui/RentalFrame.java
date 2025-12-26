package ui;

import dao.PSDAO;
import model.PSItem;
import service.RentalService;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.io.File;
import java.util.List;

public class RentalFrame extends JFrame {

    private final RentalService rentalService = new RentalService();
    private final PSDAO psDAO = new PSDAO();

    private JTextField userIdField;
    private JTextField hoursField;
    private JComboBox<PSItem> psCombo;
    private JComboBox<String> gameCombo;
    private JLabel totalLabel;
    private JButton rentBtn;
    private JButton refreshBtn;
    private JButton printBtn;

    private static final int PRICE_PER_HOUR = 10000;

    // ===== COLOR THEME =====
    private static final Color BG = new Color(15, 23, 42);
    private static final Color PANEL = new Color(17, 24, 39);
    private static final Color NEON_PINK = new Color(236, 72, 153);
    private static final Color NEON_BLUE = new Color(34, 211, 238);
    private static final Color NEON_GREEN = new Color(16, 185, 129);
    private static final Color TEXT = new Color(229, 231, 235);

    public RentalFrame() {
        setTitle("Simplex Game Center - Rental");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(null);

        setContentPane(buildUI());
        loadAvailablePSAsync();
        setVisible(true);
    }

    // ================= UI =================
    private JPanel buildUI() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        root.setBackground(BG);

        JLabel title = new JLabel("FORM PENYEWAAN PLAYSTATION", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        title.setForeground(NEON_PINK);
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(PANEL);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;

        userIdField = new JTextField();
        hoursField = new JTextField();
        psCombo = new JComboBox<>();
        gameCombo = new JComboBox<>(new String[]{
                "FIFA 23", "PES 2021", "GTA V", "Tekken 7", "Call of Duty"
        });

        refreshBtn = new JButton("Refresh");
        rentBtn = new JButton("Sewa");
        printBtn = new JButton("Cetak Struk");
        printBtn.setEnabled(false);

        styleField(userIdField);
        styleField(hoursField);
        styleCombo(psCombo);
        styleCombo(gameCombo);

        styleButton(refreshBtn, NEON_BLUE);
        styleButton(rentBtn, NEON_PINK);
        styleButton(printBtn, NEON_GREEN);

        totalLabel = new JLabel("Total Bayar: Rp 0");
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 16f));
        totalLabel.setForeground(NEON_BLUE);

        setDigitsOnly(userIdField);
        setDigitsOnly(hoursField);

        hoursField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateTotal(); }
            public void removeUpdate(DocumentEvent e) { updateTotal(); }
            public void changedUpdate(DocumentEvent e) { updateTotal(); }
        });

        // ===== FORM ROWS =====
        addRow(form, gc, 0, "User ID", userIdField);
        addRow(form, gc, 1, "Pilih PS", psCombo, refreshBtn);
        addRow(form, gc, 2, "Pilih Game", gameCombo);
        addRow(form, gc, 3, "Durasi (jam)", hoursField);

        gc.gridx = 1; gc.gridy = 4;
        form.add(totalLabel, gc);

        root.add(form, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.setBackground(BG);
        bottom.add(printBtn);
        bottom.add(rentBtn);
        root.add(bottom, BorderLayout.SOUTH);

        // ===== EVENTS =====
        refreshBtn.addActionListener(e -> loadAvailablePSAsync());
        rentBtn.addActionListener(e -> handleRentAsync());

        printBtn.addActionListener(e -> {
            try {
                File f = new File("STRUK_RENTAL_" + userIdField.getText() + ".pdf");
                if (!f.exists()) {
                    JOptionPane.showMessageDialog(this, "Struk belum tersedia!");
                    return;
                }
                Desktop.getDesktop().open(f);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal membuka struk!");
            }
        });

        return root;
    }

    // ================= LOGIC =================
    private void updateTotal() {
        try {
            int jam = Integer.parseInt(hoursField.getText());
            totalLabel.setText("Total Bayar: Rp " + (jam * PRICE_PER_HOUR));
        } catch (Exception e) {
            totalLabel.setText("Total Bayar: Rp 0");
        }
    }

    private void loadAvailablePSAsync() {
        setLoading(true);

        new SwingWorker<List<PSItem>, Void>() {
            @Override
            protected List<PSItem> doInBackground() {
                return psDAO.getAvailablePSItems();
            }

            @Override
            protected void done() {
                try {
                    psCombo.removeAllItems();
                    for (PSItem it : get()) psCombo.addItem(it);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(RentalFrame.this, "Gagal load PS");
                } finally {
                    setLoading(false);
                }
            }
        }.execute();
    }

    private void handleRentAsync() {
        if (userIdField.getText().isEmpty() || hoursField.getText().isEmpty() || psCombo.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Lengkapi semua data!");
            return;
        }

        int userId = Integer.parseInt(userIdField.getText());
        int hours = Integer.parseInt(hoursField.getText());

        setLoading(true);

        new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                return rentalService.createRental(userId, ((PSItem) psCombo.getSelectedItem()).getId(), hours);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(RentalFrame.this,
                                "Rental berhasil!\n" + totalLabel.getText() +
                                "\nGame: " + gameCombo.getSelectedItem());
                        printBtn.setEnabled(true);
                        hoursField.setText("");
                        updateTotal();
                        loadAvailablePSAsync();
                    } else {
                        JOptionPane.showMessageDialog(RentalFrame.this, "Rental gagal!");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(RentalFrame.this, "Error transaksi");
                } finally {
                    setLoading(false);
                }
            }
        }.execute();
    }

    // ================= UTIL =================
    private void setLoading(boolean loading) {
        rentBtn.setEnabled(!loading);
        refreshBtn.setEnabled(!loading);
        setCursor(loading ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }

    private void addRow(JPanel panel, GridBagConstraints gc, int row, String label, JComponent field) {
        addRow(panel, gc, row, label, field, null);
    }

    private void addRow(JPanel panel, GridBagConstraints gc, int row, String label, JComponent field, JComponent extra) {
        JLabel l = new JLabel(label);
        l.setForeground(TEXT);
        gc.gridx = 0; gc.gridy = row;
        panel.add(l, gc);

        JPanel wrap = new JPanel(new BorderLayout(8, 0));
        wrap.setBackground(PANEL);
        wrap.add(field, BorderLayout.CENTER);
        if (extra != null) wrap.add(extra, BorderLayout.EAST);

        gc.gridx = 1;
        panel.add(wrap, gc);
    }

    private void styleField(JTextField f) {
        f.setBackground(new Color(30, 41, 59));
        f.setForeground(TEXT);
        f.setCaretColor(TEXT);
    }

    private void styleCombo(JComboBox<?> c) {
        c.setBackground(new Color(30, 41, 59));
        c.setForeground(TEXT);
    }

    private void styleButton(JButton b, Color bg) {
        b.setBackground(bg);
        b.setForeground(Color.BLACK);
        b.setFont(b.getFont().deriveFont(Font.BOLD));
    }

    private void setDigitsOnly(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                    throws BadLocationException {
                if (string != null && string.matches("\\d+"))
                    super.insertString(fb, offset, string, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                    throws BadLocationException {
                if (text == null || text.isEmpty() || text.matches("\\d+"))
                    super.replace(fb, offset, length, text, attrs);
            }
        });
    }
}
