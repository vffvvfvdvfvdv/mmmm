package com.mrm.timemanager;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CategoryTargetsPanel extends JPanel {
    private final DefaultTableModel tableModel;
    private final JTable table;
    private final List<Runnable> listeners = new ArrayList<>();

    public CategoryTargetsPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        tableModel = new DefaultTableModel(new Object[][]{}, new String[]{"Category", "Target (min)"}) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 1 ? Integer.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {
                if (column == 1) {
                    int val = 0;
                    if (aValue instanceof Number) {
                        val = ((Number) aValue).intValue();
                    } else if (aValue != null) {
                        try { val = Integer.parseInt(aValue.toString()); } catch (Exception ignore) {}
                    }
                    super.setValueAt(Math.max(0, val), row, column);
                } else {
                    super.setValueAt(aValue, row, column);
                }
                notifyListeners();
            }
        };

        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add Category");
        JButton removeBtn = new JButton("Remove");
        JButton defaultsBtn = new JButton("Load Defaults");
        controls.add(addBtn);
        controls.add(removeBtn);
        controls.add(defaultsBtn);
        add(controls, BorderLayout.NORTH);

        addBtn.addActionListener(e -> onAddCategory());
        removeBtn.addActionListener(e -> onRemoveCategory());
        defaultsBtn.addActionListener(e -> loadDefaultCategories());

        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                notifyListeners();
            }
        });

        loadDefaultCategories();
    }

    private void onAddCategory() {
        JTextField nameField = new JTextField();
        JTextField targetField = new JTextField("0");
        JPanel panel = new JPanel(new BorderLayout(4, 4));
        JPanel labels = new JPanel(new BorderLayout(4, 4));
        labels.add(new JLabel("Name:"), BorderLayout.NORTH);
        labels.add(new JLabel("Target (min):"), BorderLayout.CENTER);
        JPanel fields = new JPanel(new BorderLayout(4, 4));
        fields.add(nameField, BorderLayout.NORTH);
        fields.add(targetField, BorderLayout.CENTER);
        panel.add(labels, BorderLayout.WEST);
        panel.add(fields, BorderLayout.CENTER);

        int res = JOptionPane.showConfirmDialog(this, panel, "Add Category", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            if (name.isEmpty()) return;
            if (findCategoryRow(name) >= 0) {
                JOptionPane.showMessageDialog(this, "Category already exists.", "Duplicate", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int minutes = 0;
            try { minutes = Integer.parseInt(targetField.getText().trim()); } catch (Exception ignore) {}
            tableModel.addRow(new Object[]{name, Math.max(0, minutes)});
            notifyListeners();
        }
    }

    private void onRemoveCategory() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            tableModel.removeRow(row);
            notifyListeners();
        }
    }

    private int findCategoryRow(String name) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object v = tableModel.getValueAt(i, 0);
            if (v != null && v.toString().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    public Map<String, Integer> getTargetsMap() {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String name = String.valueOf(tableModel.getValueAt(i, 0));
            Object v = tableModel.getValueAt(i, 1);
            int minutes = 0;
            if (v instanceof Number) minutes = ((Number) v).intValue();
            else if (v != null) { try { minutes = Integer.parseInt(v.toString()); } catch (Exception ignore) {} }
            map.put(name, Math.max(0, minutes));
        }
        return map;
    }

    public List<String> getCategoryNames() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object v = tableModel.getValueAt(i, 0);
            if (v != null) list.add(v.toString());
        }
        return list;
    }

    public void setTargetsMap(Map<String, Integer> targets) {
        tableModel.setRowCount(0);
        for (Map.Entry<String, Integer> e : targets.entrySet()) {
            tableModel.addRow(new Object[]{e.getKey(), Math.max(0, e.getValue())});
        }
        notifyListeners();
    }

    public void addTargetsChangedListener(Runnable listener) {
        if (listener != null) listeners.add(listener);
    }

    private void notifyListeners() {
        for (Runnable r : listeners) r.run();
    }

    private void loadDefaultCategories() {
        if (tableModel.getRowCount() > 0) return;
        tableModel.addRow(new Object[]{"Family", 0});
        tableModel.addRow(new Object[]{"Work", 0});
        tableModel.addRow(new Object[]{"Sports", 0});
        tableModel.addRow(new Object[]{"Friends", 0});
        tableModel.addRow(new Object[]{"Other", 0});
        notifyListeners();
    }
}