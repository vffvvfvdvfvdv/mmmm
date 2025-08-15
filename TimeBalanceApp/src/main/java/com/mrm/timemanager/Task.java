package com.mrm.timemanager;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

public class Task {
    private String title;
    private String category;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private int durationMinutes;

    public Task(String title, String category, DayOfWeek dayOfWeek, LocalTime startTime, int durationMinutes) {
        this.title = title == null ? "" : title.trim();
        this.category = category == null ? "Other" : category.trim();
        this.dayOfWeek = dayOfWeek == null ? DayOfWeek.MONDAY : dayOfWeek;
        this.startTime = startTime == null ? LocalTime.of(9, 0) : startTime;
        this.durationMinutes = Math.max(0, durationMinutes);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? "" : title.trim();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category == null ? "Other" : category.trim();
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek == null ? DayOfWeek.MONDAY : dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime == null ? LocalTime.of(9, 0) : startTime;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = Math.max(0, durationMinutes);
    }

    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", dayOfWeek=" + dayOfWeek +
                ", startTime=" + startTime +
                ", durationMinutes=" + durationMinutes +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return durationMinutes == task.durationMinutes &&
                Objects.equals(title, task.title) &&
                Objects.equals(category, task.category) &&
                dayOfWeek == task.dayOfWeek &&
                Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, category, dayOfWeek, startTime, durationMinutes);
    }
}