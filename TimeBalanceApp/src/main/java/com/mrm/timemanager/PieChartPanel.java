package com.mrm.timemanager;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PieChartPanel extends JPanel {
    private Map<String, Integer> categoryToMinutes = new LinkedHashMap<>();
    private final Map<String, Color> categoryToColor = new LinkedHashMap<>();

    public PieChartPanel() {
        setPreferredSize(new Dimension(600, 360));
        setBackground(Color.WHITE);
    }

    public void setData(Map<String, Integer> categoryToMinutes) {
        if (categoryToMinutes == null) {
            this.categoryToMinutes = new LinkedHashMap<>();
        } else {
            this.categoryToMinutes = new LinkedHashMap<>(categoryToMinutes);
        }
        ensureColors();
        repaint();
    }

    private void ensureColors() {
        for (String category : categoryToMinutes.keySet()) {
            if (!categoryToColor.containsKey(category)) {
                categoryToColor.put(category, generateColor(category));
            }
        }
        // Remove colors for categories no longer present
        List<String> toRemove = new ArrayList<>();
        for (String key : categoryToColor.keySet()) {
            if (!categoryToMinutes.containsKey(key)) {
                toRemove.add(key);
            }
        }
        for (String key : toRemove) {
            categoryToColor.remove(key);
        }
    }

    private Color generateColor(String key) {
        int hash = Objects.requireNonNullElse(key, "").hashCode();
        int r = 100 + Math.abs(hash % 156);
        int g = 100 + Math.abs((hash / 100) % 156);
        int b = 100 + Math.abs((hash / 10000) % 156);
        return new Color(r, g, b);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        int legendWidth = Math.min(240, Math.max(160, width / 3));
        int pieDiameter = Math.min(width - legendWidth - 40, height - 40);
        int pieX = 20;
        int pieY = (height - pieDiameter) / 2;

        int totalMinutes = categoryToMinutes.values().stream().mapToInt(Integer::intValue).sum();

        g2.setColor(new Color(245, 245, 245));
        g2.fillOval(pieX - 2, pieY - 2, pieDiameter + 4, pieDiameter + 4);

        if (totalMinutes <= 0) {
            g2.setColor(Color.DARK_GRAY);
            String msg = "No data yet";
            g2.setFont(getFont().deriveFont(Font.BOLD, 16f));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(msg, pieX + (pieDiameter - fm.stringWidth(msg)) / 2, pieY + pieDiameter / 2);
            g2.dispose();
            return;
        }

        double startAngle = 0.0;
        for (Map.Entry<String, Integer> entry : categoryToMinutes.entrySet()) {
            String category = entry.getKey();
            int minutes = entry.getValue();
            if (minutes <= 0) continue;
            double angle = 360.0 * minutes / totalMinutes;
            g2.setColor(categoryToColor.getOrDefault(category, Color.GRAY));
            g2.fill(new Arc2D.Double(pieX, pieY, pieDiameter, pieDiameter, startAngle, angle, Arc2D.PIE));
            startAngle += angle;
        }

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2f));
        g2.drawOval(pieX, pieY, pieDiameter, pieDiameter);

        drawLegend(g2, pieX + pieDiameter + 20, 30, legendWidth - 30, height - 60, totalMinutes);

        g2.dispose();
    }

    private void drawLegend(Graphics2D g2, int x, int y, int width, int height, int totalMinutes) {
        g2.setColor(new Color(250, 250, 250));
        g2.fillRoundRect(x - 10, y - 10, width + 20, height + 20, 12, 12);
        g2.setColor(new Color(220, 220, 220));
        g2.drawRoundRect(x - 10, y - 10, width + 20, height + 20, 12, 12);

        int lineHeight = 24;
        int boxSize = 14;
        int i = 0;
        g2.setFont(getFont().deriveFont(Font.PLAIN, 13f));
        for (Map.Entry<String, Integer> entry : categoryToMinutes.entrySet()) {
            String category = entry.getKey();
            int minutes = entry.getValue();
            if (minutes < 0) minutes = 0;
            double pct = totalMinutes > 0 ? (100.0 * minutes / totalMinutes) : 0.0;

            int yy = y + i * lineHeight;
            g2.setColor(categoryToColor.getOrDefault(category, Color.GRAY));
            g2.fillRect(x, yy, boxSize, boxSize);
            g2.setColor(Color.DARK_GRAY);
            g2.drawRect(x, yy, boxSize, boxSize);

            String label = String.format("%s — %d min (%.1f%%)", category, minutes, pct);
            g2.setColor(Color.DARK_GRAY);
            g2.drawString(label, x + boxSize + 8, yy + boxSize - 2);
            i++;
        }
    }
}