package com.mrm.timemanager;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Map;

public class MainFrame extends JFrame {
    private final TaskTableModel taskTableModel = new TaskTableModel();
    private final CategoryTargetsPanel targetsPanel = new CategoryTargetsPanel();
    private final TaskEditorPanel taskEditorPanel = new TaskEditorPanel(taskTableModel, () -> targetsPanel.getCategoryNames());
    private final StatsPanel statsPanel = new StatsPanel(taskTableModel, targetsPanel);
    private final PieChartPanel pieChartPanel = new PieChartPanel();

    public MainFrame() {
        super("Time Balance – Weekly Planner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(1000, 640));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Timetable", taskEditorPanel);
        tabs.addTab("Targets", targetsPanel);

        JSplitPane reportsSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, statsPanel, pieChartPanel);
        reportsSplit.setResizeWeight(0.5);
        reportsSplit.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        tabs.addTab("Reports", reportsSplit);

        add(tabs, BorderLayout.CENTER);

        // Listeners to keep reports in sync
        TableModelListener syncListener = new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                refreshReports();
            }
        };
        taskTableModel.addTableModelListener(syncListener);
        targetsPanel.addTargetsChangedListener(this::refreshReports);

        pack();
        setLocationRelativeTo(null);

        // Initial report draw
        SwingUtilities.invokeLater(this::refreshReports);
    }

    private void refreshReports() {
        statsPanel.refresh();
        Map<String, Integer> actuals = taskTableModel.calculateActualMinutesByCategory();
        pieChartPanel.setData(actuals);
    }
}