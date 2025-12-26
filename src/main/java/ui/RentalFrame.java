package ui;

import app.AppContext;
import dao.TransactionDAO;
import model.PSItem;
import model.User;
import util.DBConnection;
import util.SwingAsync;
import util.UiDialogs;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import static service.RentalService.PRICE_PER_HOUR;

public class RentalFrame extends JFrame {

    private final AppContext ctx;
    private final User user;

    private JComboBox<PSItem> psCombo;
    private JTextField hoursField;
    private JLabel totalLabel;
    private JButton rentBtn;
    private JLabel statusLabel;

    public RentalFrame(AppContext ctx, User user) {
        this.ctx = ctx;
        this.user = user;

        setTitle("Rental PS");
        setMinimumSize(new Dimension(520, 260));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        buildUI();
        loadAvailablePSAsync();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Form Rental PS");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new MigLayout("fillx,insets 14", "[right]10[grow,fill]", "[]10[]10[]14[]"));
        form.add(new JLabel("User"), "");
        form.add(new JLabel(user.getUsername() + " (ID: " + user.getId() + ")"), "wrap");

        psCombo = new JComboBox<>();
        form.add(new JLabel("Pilih PS"));
        form.add(psCombo, "wrap");

        hoursField = new JTextField();
        hoursField.getDocument().addDocumentListener((util.SimpleDocumentListener) e -> updateTotal());
        form.add(new JLabel("Durasi (jam)"));
        form.add(hoursField, "wrap");

        totalLabel = new JLabel("Total Bayar: Rp 0");
        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD));
        form.add(totalLabel, "span, wrap");

        statusLabel = new JLabel(" ");
        statusLabel.setForeground(new Color(0x666666));

        rentBtn = new JButton("Sewa Sekarang");
        rentBtn.addActionListener(e -> handleRentAsync());

        JPanel action = new JPanel(new MigLayout("fillx,insets 0", "[grow][]", "[]"));
        action.add(statusLabel, "grow");
        action.add(rentBtn, "right");

        root.add(form, BorderLayout.CENTER);
        root.add(action, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void updateTotal() {
        try {
            int jam = Integer.parseInt(hoursField.getText().trim());
            if (jam < 0) jam = 0;
            totalLabel.setText("Total Bayar: Rp " + (jam * PRICE_PER_HOUR));
        } catch (Exception e) {
            totalLabel.setText("Total Bayar: Rp 0");
        }
    }

    private void loadAvailablePSAsync() {
        setLoading(true, "Memuat PS tersedia...");

        SwingAsync.run(
                () -> ctx.psService.findAvailableItems(),
                (List<PSItem> items) -> {
                    psCombo.removeAllItems();
                    for (PSItem it : items) psCombo.addItem(it);

                    boolean hasItem = psCombo.getItemCount() > 0;
                    rentBtn.setEnabled(hasItem);
                    statusLabel.setText(hasItem ? " " : "Tidak ada PS tersedia saat ini.");

                    setLoading(false, " ");
                },
                err -> {
                    setLoading(false, " ");
                    UiDialogs.error(this, "Gagal memuat list PS tersedia.", err);
                }
        );
    }

    private void handleRentAsync() {
        PSItem selected = (PSItem) psCombo.getSelectedItem();
        if (selected == null) {
            UiDialogs.warn(this, "Pilih PS terlebih dahulu.");
            return;
        }

        int hours;
        try {
            hours = Integer.parseInt(hoursField.getText().trim());
        } catch (Exception e) {
            UiDialogs.warn(this, "Durasi harus angka (jam).");
            return;
        }

        if (hours <= 0) {
            UiDialogs.warn(this, "Durasi minimal 1 jam.");
            return;
        }

        int totalPrice = hours * PRICE_PER_HOUR;

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Konfirmasi sewa:\n" +
                        "- PS: " + selected.getName() + "\n" +
                        "- Durasi: " + hours + " jam\n" +
                        "- Total: Rp " + totalPrice + "\n\n" +
                        "Lanjutkan?",
                "Konfirmasi Rental",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        setLoading(true, "Memproses rental & leaderboard...");

        SwingAsync.run(
                () -> {
                    // 1) tetap pakai service kamu (biar flow existing + struk tetap jalan)
                    boolean okRental = ctx.rentalService.createRental(
                            user.getId(),
                            selected.getId(),
                            hours,
                            selected.getName()
                    );

                    if (!okRental) {
                        return RentResult.fail("Transaksi gagal. PS mungkin sudah disewa orang lain.");
                    }

                    // 2) ambil rentalId terbaru dari rental ATAU rentals
                    Integer rentalId = findLatestRentalId(user.getId(), selected.getId());
                    if (rentalId == null) {
                        return RentResult.fail("Rental berhasil, tapi gagal ambil rentalId (cek tabel rental/rentals).");
                    }

                    // 3) simpan transaksi + update leaderboard
                    TransactionDAO txDao = new TransactionDAO();
                    boolean okTx = txDao.saveTransaction(rentalId, totalPrice);
                    if (!okTx) {
                        return RentResult.fail("Rental berhasil, tapi transaksi/leaderboard gagal tersimpan. Cek console log.");
                    }

                    return RentResult.ok(rentalId);
                },
                result -> {
                    setLoading(false, " ");

                    if (!result.ok) {
                        UiDialogs.warn(this, result.message);
                        loadAvailablePSAsync();
                        return;
                    }

                    UiDialogs.info(this,
                            "Transaksi berhasil!\n" +
                                    "- rentalId: " + result.rentalId + "\n" +
                                    "- Leaderboard sudah terupdate.\n" +
                                    "Silakan buka Admin/Leaderboard lalu Refresh."
                    );

                    hoursField.setText("");
                    loadAvailablePSAsync();
                },
                err -> {
                    setLoading(false, " ");
                    UiDialogs.error(this, "Terjadi error saat transaksi rental.", err);
                }
        );
    }

    // Cari rentalId terakhir untuk userId + psId
    // Coba di tabel rental dulu, kalau tidak ada -> rentals
    private Integer findLatestRentalId(int userId, int psId) {
        String sql1 = "SELECT id FROM rental WHERE user_id=? AND ps_id=? ORDER BY id DESC LIMIT 1";
        String sql2 = "SELECT id FROM rentals WHERE user_id=? AND ps_id=? ORDER BY id DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection()) {

            // coba rental
            try (PreparedStatement ps = conn.prepareStatement(sql1)) {
                ps.setInt(1, userId);
                ps.setInt(2, psId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("id");
                }
            }

            // coba rentals
            try (PreparedStatement ps = conn.prepareStatement(sql2)) {
                ps.setInt(1, userId);
                ps.setInt(2, psId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("id");
                }
            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setLoading(boolean loading, String msg) {
        rentBtn.setEnabled(!loading);
        statusLabel.setText(msg);
        setCursor(loading ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }

    private static class RentResult {
        final boolean ok;
        final Integer rentalId;
        final String message;

        private RentResult(boolean ok, Integer rentalId, String message) {
            this.ok = ok;
            this.rentalId = rentalId;
            this.message = message;
        }

        static RentResult ok(int rentalId) {
            return new RentResult(true, rentalId, "OK");
        }

        static RentResult fail(String msg) {
            return new RentResult(false, null, msg);
        }
    }
}
