package com.mrm.timemanager;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JWindow;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.net.URL;

public class SplashScreenWindow extends JWindow {
    public SplashScreenWindow() {
        JLabel title = new JLabel("Time Balance", SwingConstants.CENTER);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        title.setForeground(new Color(30, 30, 30));
        title.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel subtitle = new JLabel("Weekly time planner for balanced life", SwingConstants.CENTER);
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        subtitle.setForeground(new Color(80, 80, 80));
        subtitle.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));

        JProgressBar progress = new JProgressBar();
        progress.setIndeterminate(true);
        progress.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(title, BorderLayout.NORTH);
        getContentPane().add(subtitle, BorderLayout.CENTER);
        getContentPane().add(progress, BorderLayout.SOUTH);
        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        setSize(new Dimension(420, 180));
        setLocationRelativeTo(null);
    }

    public void showForMillis(int millis, Runnable afterClose) {
        setVisible(true);
        Timer timer = new Timer(millis, e -> {
            setVisible(false);
            dispose();
            if (afterClose != null) afterClose.run();
        });
        timer.setRepeats(false);
        timer.start();
    }
}