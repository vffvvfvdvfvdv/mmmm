package com.mrm.timemanager;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.function.Supplier;

public class TaskEditorPanel extends JPanel {
    private final TaskTableModel taskTableModel;
    private final JTable table;
    private final Supplier<List<String>> categoriesSupplier;

    public TaskEditorPanel(TaskTableModel taskTableModel, Supplier<List<String>> categoriesSupplier) {
        this.taskTableModel = taskTableModel;
        this.categoriesSupplier = categoriesSupplier;

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        table = new JTable(taskTableModel);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        // Editors
        JComboBox<DayOfWeek> dayCombo = new JComboBox<>(DayOfWeek.values());
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(dayCombo));
        table.getColumnModel().getColumn(3).setCellRenderer(new LocalTimeRenderer());
        table.getColumnModel().getColumn(3).setCellEditor(new LocalTimeEditor());
        table.getColumnModel().getColumn(1).setCellEditor(new CategoryCellEditor(() -> categoriesSupplier.get()));

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add Task");
        JButton removeBtn = new JButton("Remove Task");
        top.add(new JLabel("Weekly timetable"));
        top.add(addBtn);
        top.add(removeBtn);
        add(top, BorderLayout.NORTH);

        addBtn.addActionListener(e -> addDefaultTask());
        removeBtn.addActionListener(e -> removeSelectedTask());

        // Ensure table repaints when data changes
        taskTableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                SwingUtilities.invokeLater(() -> table.repaint());
            }
        });
    }

    private void addDefaultTask() {
        String defaultCategory = categoriesSupplier.get().isEmpty() ? "Other" : categoriesSupplier.get().get(0);
        Task task = new Task("New Task", defaultCategory, DayOfWeek.MONDAY, LocalTime.of(9, 0), 60);
        taskTableModel.addTask(task);
        int row = taskTableModel.getRowCount() - 1;
        if (row >= 0) {
            table.getSelectionModel().setSelectionInterval(row, row);
            table.scrollRectToVisible(table.getCellRect(row, 0, true));
        }
    }

    private void removeSelectedTask() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            taskTableModel.removeTask(row);
        }
    }

    public JTable getTable() {
        return table;
    }

    private static class LocalTimeRenderer extends DefaultTableCellRenderer {
        private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        @Override
        protected void setValue(Object value) {
            if (value instanceof LocalTime) {
                setText(((LocalTime) value).format(fmt));
            } else {
                setText(value == null ? "" : value.toString());
            }
        }
    }

    private static class LocalTimeEditor extends DefaultCellEditor {
        private final JTextField field;
        private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");

        public LocalTimeEditor() {
            super(new JTextField());
            this.field = (JTextField) getComponent();
        }

        @Override
        public boolean stopCellEditing() {
            String text = field.getText().trim();
            try {
                LocalTime.parse(text, fmt);
            } catch (DateTimeParseException ex) {
                field.setText("09:00");
            }
            return super.stopCellEditing();
        }

        @Override
        public Object getCellEditorValue() {
            String text = field.getText().trim();
            try {
                return LocalTime.parse(text, fmt);
            } catch (DateTimeParseException ex) {
                return LocalTime.of(9, 0);
            }
        }
    }

    private static class CategoryCellEditor extends DefaultCellEditor {
        private final JComboBox<String> comboBox;
        private final Supplier<List<String>> categoriesSupplier;

        public CategoryCellEditor(Supplier<List<String>> categoriesSupplier) {
            super(new JComboBox<>());
            this.comboBox = (JComboBox<String>) getComponent();
            this.categoriesSupplier = categoriesSupplier;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            comboBox.removeAllItems();
            for (String c : categoriesSupplier.get()) comboBox.addItem(c);
            if (value != null) comboBox.setSelectedItem(value.toString());
            return comboBox;
        }

        @Override
        public Object getCellEditorValue() {
            Object v = comboBox.getSelectedItem();
            return v == null ? "Other" : v.toString();
        }
    }
}