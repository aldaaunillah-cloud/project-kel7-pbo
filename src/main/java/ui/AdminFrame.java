package ui;

import util.DBConnection;
import util.SwingAsync;
import util.UiDialogs;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AdminFrame extends JFrame {

    // PS
    private DefaultTableModel psModel;
    private JTable psTable;
    private JTextField psNameField;

    // Users
    private DefaultTableModel userModel;
    private JTable userTable;

    private JLabel statusLabel;

    public AdminFrame() {
        setTitle("Admin - Kelola Data");
        setMinimumSize(new Dimension(820, 520));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        buildUI();
        refreshAllAsync();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("ADMIN PANEL");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        root.add(title, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Kelola Unit PS", buildPsPanel());
        tabs.addTab("Kelola User", buildUserPanel());

        root.add(tabs, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new MigLayout("fillx,insets 0", "[grow][]", "[]"));
        statusLabel = new JLabel(" ");
        JButton refreshBtn = new JButton("Refresh Semua");
        refreshBtn.addActionListener(e -> refreshAllAsync());
        bottom.add(statusLabel, "grow");
        bottom.add(refreshBtn, "right");

        root.add(bottom, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel buildPsPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        psModel = new DefaultTableModel(new Object[]{"ID", "Nama PS", "Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        psTable = new JTable(psModel);
        psTable.setRowHeight(26);

        p.add(new JScrollPane(psTable), BorderLayout.CENTER);

        JPanel actions = new JPanel(new MigLayout("fillx,insets 0", "[grow][]10[]10[]", "[]8[]"));

        psNameField = new JTextField();
        JButton addBtn = new JButton("Tambah Unit");
        addBtn.addActionListener(e -> addPsAsync());

        JButton deleteBtn = new JButton("Hapus Unit");
        deleteBtn.addActionListener(e -> deletePsAsync());

        JButton resetBtn = new JButton("Reset Semua AVAILABLE");
        resetBtn.addActionListener(e -> resetPsAsync());

        actions.add(new JLabel("Nama Unit PS:"), "span, wrap");
        actions.add(psNameField, "grow");
        actions.add(addBtn, "right");
        actions.add(deleteBtn, "right");
        actions.add(resetBtn, "right, wrap");

        p.add(actions, BorderLayout.SOUTH);
        return p;
    }

    private JPanel buildUserPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        userModel = new DefaultTableModel(new Object[]{"ID", "Username"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        userTable = new JTable(userModel);
        userTable.setRowHeight(26);

        p.add(new JScrollPane(userTable), BorderLayout.CENTER);

        JPanel actions = new JPanel(new MigLayout("fillx,insets 0", "[grow][]", "[]"));
        JButton deleteUserBtn = new JButton("Hapus User Terpilih");
        deleteUserBtn.addActionListener(e -> deleteUserAsync());

        actions.add(new JLabel("Catatan: hapus user bisa gagal jika terikat foreign key rental/transaksi."), "grow");
        actions.add(deleteUserBtn, "right");

        p.add(actions, BorderLayout.SOUTH);
        return p;
    }

    private void refreshAllAsync() {
        setLoading(true, "Memuat data...");

        SwingAsync.run(
                () -> {
                    try {
                        List<Object[]> psRows = fetchAllPS();
                        List<Object[]> userRows = fetchAllUsers();
                        return new Object[]{psRows, userRows};
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                res -> {
                    @SuppressWarnings("unchecked")
                    List<Object[]> psRows = (List<Object[]>) ((Object[]) res)[0];
                    @SuppressWarnings("unchecked")
                    List<Object[]> userRows = (List<Object[]>) ((Object[]) res)[1];

                    psModel.setRowCount(0);
                    for (Object[] r : psRows) psModel.addRow(r);

                    userModel.setRowCount(0);
                    for (Object[] r : userRows) userModel.addRow(r);

                    setLoading(false, "Terakhir update: " + java.time.LocalTime.now());
                },
                err -> {
                    setLoading(false, " ");
                    UiDialogs.error(this, "Gagal memuat data admin.", err);
                }
        );
    }

    private void addPsAsync() {
        String name = psNameField.getText().trim();
        if (name.isEmpty()) {
            UiDialogs.warn(this, "Nama unit PS tidak boleh kosong.");
            return;
        }

        setLoading(true, "Menambah unit PS...");
        SwingAsync.run(
                () -> {
                    try {
                        return insertPS(name);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                ok -> {
                    setLoading(false, " ");
                    if (!(Boolean) ok) {
                        UiDialogs.warn(this, "Gagal menambah unit PS.");
                        return;
                    }
                    psNameField.setText("");
                    refreshAllAsync();
                    UiDialogs.info(this, "Unit PS berhasil ditambahkan.");
                },
                err -> {
                    setLoading(false, " ");
                    UiDialogs.error(this, "Error saat menambah unit PS.", err);
                }
        );
    }

    private void deletePsAsync() {
        int row = psTable.getSelectedRow();
        if (row < 0) {
            UiDialogs.warn(this, "Pilih 1 unit PS di tabel dulu.");
            return;
        }

        int id = (int) psModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Hapus unit PS ID " + id + "?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        setLoading(true, "Menghapus unit PS...");
        SwingAsync.run(
                () -> {
                    try {
                        return deletePS(id);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                ok -> {
                    setLoading(false, " ");
                    if (!(Boolean) ok) {
                        UiDialogs.warn(this, "Gagal menghapus unit PS (mungkin sedang dipakai / terikat FK).");
                        return;
                    }
                    refreshAllAsync();
                    UiDialogs.info(this, "Unit PS berhasil dihapus.");
                },
                err -> {
                    setLoading(false, " ");
                    UiDialogs.error(this, "Error saat menghapus unit PS.", err);
                }
        );
    }

    private void resetPsAsync() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Reset semua PS menjadi AVAILABLE?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        setLoading(true, "Reset status PS...");
        SwingAsync.run(
                () -> {
                    try {
                        resetAllPS();
                        return true;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                ok -> {
                    setLoading(false, " ");
                    refreshAllAsync();
                    UiDialogs.info(this, "Semua PS sudah di-reset ke AVAILABLE.");
                },
                err -> {
                    setLoading(false, " ");
                    UiDialogs.error(this, "Error saat reset PS.", err);
                }
        );
    }

    private void deleteUserAsync() {
        int row = userTable.getSelectedRow();
        if (row < 0) {
            UiDialogs.warn(this, "Pilih 1 user di tabel dulu.");
            return;
        }

        int id = (int) userModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Hapus user ID " + id + "?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) return;

        setLoading(true, "Menghapus user...");
        SwingAsync.run(
                () -> {
                    try {
                        return deleteUser(id);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                ok -> {
                    setLoading(false, " ");
                    if (!(Boolean) ok) {
                        UiDialogs.warn(this, "Gagal menghapus user (mungkin terikat rental/transaksi).");
                        return;
                    }
                    refreshAllAsync();
                    UiDialogs.info(this, "User berhasil dihapus.");
                },
                err -> {
                    setLoading(false, " ");
                    UiDialogs.error(this, "Error saat menghapus user.", err);
                }
        );
    }

    // =========================
    // DB helpers (throw Exception)
    // =========================

    private List<Object[]> fetchAllPS() throws Exception {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT id, name, status FROM playstation ORDER BY id";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rows.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("status")
                });
            }
        }
        return rows;
    }

    private boolean insertPS(String name) throws Exception {
        String sql = "INSERT INTO playstation(name, status) VALUES (?, 'AVAILABLE')";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, name);
            return ps.executeUpdate() > 0;
        }
    }

    private boolean deletePS(int id) throws Exception {
        String sql = "DELETE FROM playstation WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private void resetAllPS() throws Exception {
        String sql = "UPDATE playstation SET status='AVAILABLE'";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }

    private List<Object[]> fetchAllUsers() throws Exception {
        List<Object[]> rows = new ArrayList<>();
        String sql = "SELECT id, username FROM users ORDER BY id";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rows.add(new Object[]{
                        rs.getInt("id"),
                        rs.getString("username")
                });
            }
        }
        return rows;
    }

    private boolean deleteUser(int id) throws Exception {
        String sql = "DELETE FROM users WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private void setLoading(boolean loading, String msg) {
        statusLabel.setText(msg);
        setCursor(loading ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }
}
