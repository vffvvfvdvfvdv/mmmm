package com.mrm.timemanager;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                // ignore and keep default
            }

            SplashScreenWindow splash = new SplashScreenWindow();
            splash.showForMillis(1200, () -> {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            });
        });
    }
}