package ui;

import app.AppContext;
import model.PS;
import model.User;
import realtime.RealtimeEvent;
import util.SwingAsync;
import util.UiDialogs;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardFrame extends JFrame {

    private final AppContext ctx;
    private final User user;

    private DefaultTableModel psModel;
    private JTable psTable;
    private JLabel statusLabel;

    public DashboardFrame(AppContext ctx, User user) {
        this.ctx = ctx;
        this.user = user;

        setTitle("Aplikasi Rental PS");
        setMinimumSize(new Dimension(760, 420));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        buildUI();
        wireRealtime();
        refreshPSAsync();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Aplikasi Rental PS | Login: " + user.getUsername());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        root.add(title, BorderLayout.NORTH);

        psModel = new DefaultTableModel(new Object[]{"ID", "Nama PS", "Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        psTable = new JTable(psModel);
        psTable.setRowHeight(26);
        root.add(new JScrollPane(psTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new MigLayout("fillx,insets 0", "[grow][]10[]10[]10[]", "[]8[]"));
        statusLabel = new JLabel(" ");
        bottom.add(statusLabel, "span, wrap");

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshPSAsync());

        JButton rentalBtn = new JButton("Rental PS");
        rentalBtn.addActionListener(e -> new RentalFrame(ctx, user).setVisible(true));

        JButton leaderboardBtn = new JButton("Leaderboard");
        leaderboardBtn.addActionListener(e -> new LeaderboardFrame().setVisible(true));

        JButton adminBtn = new JButton("Admin");
        adminBtn.addActionListener(e -> new AdminFrame().setVisible(true));

        bottom.add(refreshBtn, "right");
        bottom.add(rentalBtn, "right");
        bottom.add(leaderboardBtn, "right");
        bottom.add(adminBtn, "right, wrap");

        root.add(bottom, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private void wireRealtime() {
        ctx.realtimeClient.addListener(this::onRealtimeEvent);
    }

    private void onRealtimeEvent(RealtimeEvent event) {
        if (event == null || event.type == null) return;

        switch (event.type) {
            case "PS_STATUS_UPDATED" -> refreshPSAsync();
            case "NEW_RENTAL" -> {
                String msg =
                        "Transaksi baru!\n" +
                        "UserId: " + safeInt(event, "userId") + "\n" +
                        "PS: " + safeInt(event, "psId") + "\n" +
                        "Durasi: " + safeInt(event, "duration") + " jam";

                JOptionPane.showMessageDialog(
                        this,
                        msg,
                        "Notifikasi Rental",
                        JOptionPane.INFORMATION_MESSAGE
                );
                refreshPSAsync();
            }
            default -> { /* ignore */ }
        }
    }

    private int safeInt(RealtimeEvent ev, String key) {
        try { return ev.payload.get(key).getAsInt(); }
        catch (Exception e) { return 0; }
    }

    private void refreshPSAsync() {
        setLoading(true, "Memuat status PS...");

        SwingAsync.run(
                () -> ctx.psService.findAll(),
                (List<PS> list) -> {
                    psModel.setRowCount(0);
                    for (PS ps : list) {
                        psModel.addRow(new Object[]{ps.getId(), ps.getName(), ps.getStatus()});
                    }
                    setLoading(false, "Terakhir update: " + java.time.LocalTime.now());
                },
                err -> {
                    setLoading(false, " ");
                    UiDialogs.error(this, "Gagal memuat status PS.", err);
                }
        );
    }

    private void setLoading(boolean loading, String msg) {
        statusLabel.setText(msg);
        setCursor(loading ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }
}
