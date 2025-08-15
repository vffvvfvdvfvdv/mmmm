package com.mrm.timemanager;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StatsPanel extends JPanel {
    private final TaskTableModel taskTableModel;
    private final CategoryTargetsPanel targetsPanel;
    private final DefaultTableModel statsModel;
    private final JTable statsTable;
    private final DecimalFormat pctFmt = new DecimalFormat("+#,##0.0%;-#,##0.0%");

    public StatsPanel(TaskTableModel taskTableModel, CategoryTargetsPanel targetsPanel) {
        this.taskTableModel = taskTableModel;
        this.targetsPanel = targetsPanel;

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        statsModel = new DefaultTableModel(new Object[][]{}, new String[]{"Category", "Target (min)", "Actual (min)", "Deviation (%)"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        statsTable = new JTable(statsModel);
        statsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        statsTable.setFillsViewportHeight(true);
        add(new JScrollPane(statsTable), BorderLayout.CENTER);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshBtn = new JButton("Refresh Stats");
        JButton checkBtn = new JButton("Load check");
        top.add(new JLabel("Weekly category load"));
        top.add(refreshBtn);
        top.add(checkBtn);
        add(top, BorderLayout.NORTH);

        refreshBtn.addActionListener(e -> refresh());
        checkBtn.addActionListener(e -> doLoadCheck());
    }

    public void refresh() {
        Map<String, Integer> targets = targetsPanel.getTargetsMap();
        Map<String, Integer> actuals = taskTableModel.calculateActualMinutesByCategory();
        List<String> categories = new ArrayList<>(targets.keySet());
        for (String cat : actuals.keySet()) {
            if (!targets.containsKey(cat)) categories.add(cat);
        }

        statsModel.setRowCount(0);
        for (String cat : categories) {
            int target = targets.getOrDefault(cat, 0);
            int actual = actuals.getOrDefault(cat, 0);
            String deviationText;
            if (target <= 0) {
                deviationText = actual > 0 ? "+∞" : "0.0%";
            } else {
                double deviation = (actual - target) / (double) target;
                deviationText = pctFmt.format(deviation);
            }
            statsModel.addRow(new Object[]{cat, target, actual, deviationText});
        }
    }

    private void doLoadCheck() {
        Map<String, Integer> targets = targetsPanel.getTargetsMap();
        Map<String, Integer> actuals = taskTableModel.calculateActualMinutesByCategory();

        Map<String, Integer> overloaded = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> e : actuals.entrySet()) {
            int target = targets.getOrDefault(e.getKey(), 0);
            int actual = e.getValue();
            if (actual > target) {
                overloaded.put(e.getKey(), actual - target);
            }
        }

        Toolkit.getDefaultToolkit().beep();
        if (!overloaded.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Overload detected in: \n");
            for (Map.Entry<String, Integer> e : overloaded.entrySet()) {
                int target = targets.getOrDefault(e.getKey(), 0);
                int actual = actuals.getOrDefault(e.getKey(), 0);
                String dev;
                if (target <= 0) dev = "+∞%";
                else dev = pctFmt.format((actual - target) / (double) target);
                sb.append(String.format("- %s: +%d min (%s over target)\n", e.getKey(), e.getValue(), dev));
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Overload alert", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Timetable is balanced. No category exceeds its target.", "Balanced", JOptionPane.INFORMATION_MESSAGE);
        }
        refresh();
    }
}