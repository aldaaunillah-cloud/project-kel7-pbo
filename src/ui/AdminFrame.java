package ui;

import service.ReportService;

import javax.swing.*;
import java.util.Map;

public class AdminFrame extends JFrame {

    private final ReportService reportService = new ReportService();

    public AdminFrame() {
        setTitle("Admin");
        setSize(400, 300);
        setLocationRelativeTo(null);

        JTextArea area = new JTextArea();
        JButton loadBtn = new JButton("Load Leaderboard");

        loadBtn.addActionListener(e -> {
            Map<String, Integer> data = reportService.getLeaderboardData();
            area.setText("");

            data.forEach((user, total) ->
                    area.append(user + " : " + total + "\n"));
        });

        add(loadBtn, "North");
        add(new JScrollPane(area), "Center");
        setVisible(true);
    }
}
