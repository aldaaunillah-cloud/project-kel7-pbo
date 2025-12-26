package ui;

import service.ReportService;
import service.RentalObserver;
import service.RentalService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Map;

public class AdminFrame extends JFrame implements RentalObserver {

    private final ReportService reportService = new ReportService();
    private JTable table;
    private DefaultTableModel model;

    public AdminFrame() {
        setTitle("Admin - Leaderboard");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // ===== REGISTER OBSERVER =====
        RentalService.addObserver(this);

        JLabel title = new JLabel("LEADERBOARD USER", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new Object[]{"Rank", "Username", "Point"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(26);
        table.getTableHeader().setFont(
                table.getTableHeader().getFont().deriveFont(Font.BOLD)
        );

        add(new JScrollPane(table), BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh Leaderboard");
        refreshBtn.addActionListener(e -> loadData());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(refreshBtn);
        add(bottom, BorderLayout.SOUTH);

        loadData();
        setVisible(true);
    }

    // ===== AUTO UPDATE DARI RENTAL =====
    @Override
    public void onRentalCreated() {
        SwingUtilities.invokeLater(this::loadData);
    }

    private void loadData() {
        model.setRowCount(0);
        Map<String, Integer> data = reportService.getLeaderboard();

        int rank = 1;
        for (var e : data.entrySet()) {
            model.addRow(new Object[]{rank++, e.getKey(), e.getValue()});
        }
    }
}
