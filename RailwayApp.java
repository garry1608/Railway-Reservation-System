package com.railway;

import javax.swing.*;

public class RailwayApp {
    public static void main(String[] args) {
        // Set Look and Feel to System if possible
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignore
        }

        SwingUtilities.invokeLater(() -> {
            new LoginPage().setVisible(true);
        });
    }
}
