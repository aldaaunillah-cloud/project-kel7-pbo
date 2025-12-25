package ui.components;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TablePSPanel extends JPanel {

    public TablePSPanel() {
        String[] col = {"PS Name"};
        JTable table = new JTable(new DefaultTableModel(col, 0));
        add(new JScrollPane(table));
    }
}
