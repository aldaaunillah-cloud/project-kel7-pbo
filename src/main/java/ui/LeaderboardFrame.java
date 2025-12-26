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

public class LeaderboardFrame extends JFrame {

    private DefaultTableModel model;
    private JTable table;
    private JLabel statusLabel;

    public LeaderboardFrame() {
        setTitle("Leaderboard User");
        setMinimumSize(new Dimension(520, 360));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        buildUI();
        refreshAsync();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("LEADERBOARD USER");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        root.add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new Object[]{"Rank", "Username", "Point"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(26);

        root.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new MigLayout("fillx,insets 0", "[grow][]", "[]"));
        statusLabel = new JLabel(" ");
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshAsync());

        bottom.add(statusLabel, "grow");
        bottom.add(refreshBtn, "right");

        root.add(bottom, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private void refreshAsync() {
        setLoading(true, "Memuat leaderboard...");

        SwingAsync.run(
                () -> {
                    try {
                        return fetchTop();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                rows -> {
                    model.setRowCount(0);
                    int rank = 1;
                    for (Object[] r : rows) {
                        model.addRow(new Object[]{rank++, r[0], r[1]});
                    }
                    setLoading(false, "Terakhir update: " + java.time.LocalTime.now());
                },
                err -> {
                    setLoading(false, " ");
                    UiDialogs.error(this, "Gagal memuat leaderboard.", err);
                }
        );
    }

    private List<Object[]> fetchTop() throws Exception {
        List<Object[]> rows = new ArrayList<>();
        String sql =
                "SELECT username, total_point " +
                "FROM leaderboard " +
                "ORDER BY total_point DESC, last_update DESC " +
                "LIMIT 10";

        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rows.add(new Object[]{
                        rs.getString("username"),
                        rs.getInt("total_point")
                });
            }
        }
        return rows;
    }

    private void setLoading(boolean loading, String msg) {
        statusLabel.setText(msg);
        setCursor(loading ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }
}
