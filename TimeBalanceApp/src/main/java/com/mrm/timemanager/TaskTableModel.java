package com.mrm.timemanager;

import javax.swing.table.AbstractTableModel;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskTableModel extends AbstractTableModel {
    private final String[] columnNames = new String[]{"Task", "Category", "Day", "Start", "Duration (min)"};
    private final Class<?>[] columnTypes = new Class<?>[]{String.class, String.class, DayOfWeek.class, LocalTime.class, Integer.class};
    private final List<Task> tasks = new ArrayList<>();

    @Override
    public int getRowCount() {
        return tasks.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnTypes[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Task task = tasks.get(rowIndex);
        switch (columnIndex) {
            case 0: return task.getTitle();
            case 1: return task.getCategory();
            case 2: return task.getDayOfWeek();
            case 3: return task.getStartTime();
            case 4: return task.getDurationMinutes();
            default: return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Task task = tasks.get(rowIndex);
        switch (columnIndex) {
            case 0:
                task.setTitle(aValue == null ? "" : aValue.toString());
                break;
            case 1:
                task.setCategory(aValue == null ? "Other" : aValue.toString());
                break;
            case 2:
                if (aValue instanceof DayOfWeek) {
                    task.setDayOfWeek((DayOfWeek) aValue);
                } else if (aValue != null) {
                    try {
                        task.setDayOfWeek(DayOfWeek.valueOf(aValue.toString()));
                    } catch (IllegalArgumentException ex) {
                        // ignore invalid values
                    }
                }
                break;
            case 3:
                if (aValue instanceof LocalTime) {
                    task.setStartTime((LocalTime) aValue);
                } else if (aValue != null) {
                    try {
                        task.setStartTime(LocalTime.parse(aValue.toString()));
                    } catch (Exception ex) {
                        // ignore invalid values
                    }
                }
                break;
            case 4:
                try {
                    int minutes = 0;
                    if (aValue instanceof Number) {
                        minutes = ((Number) aValue).intValue();
                    } else if (aValue != null) {
                        minutes = Integer.parseInt(aValue.toString());
                    }
                    task.setDurationMinutes(Math.max(0, minutes));
                } catch (NumberFormatException ex) {
                    // ignore invalid value
                }
                break;
        }
        fireTableRowsUpdated(rowIndex, rowIndex);
    }

    public void addTask(Task task) {
        tasks.add(task);
        int newIndex = tasks.size() - 1;
        fireTableRowsInserted(newIndex, newIndex);
    }

    public void removeTask(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < tasks.size()) {
            tasks.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }

    public Task getTaskAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < tasks.size()) {
            return tasks.get(rowIndex);
        }
        return null;
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    public Map<String, Integer> calculateActualMinutesByCategory() {
        Map<String, Integer> totals = new HashMap<>();
        for (Task task : tasks) {
            totals.merge(task.getCategory(), task.getDurationMinutes(), Integer::sum);
        }
        return totals;
    }
}