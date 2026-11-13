package com.securevault;

import com.securevault.ui.MainFrame;
import com.securevault.ui.ThemeManager;

import javax.swing.*;

/**
 * Entry point for SecureVault.
 * Initializes human-centred theme settings and launches the desktop application.
 */
public class Main {
    public static void main(String[] args) {
        // High-DPI and platform integration properties
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("apple.awt.application.name", "SecureVault");

        SwingUtilities.invokeLater(() -> {
            ThemeManager.initializeTheme();
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}

