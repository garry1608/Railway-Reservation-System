package com.railway;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class TrackTrainPage extends JFrame {
    private String username;
    private JTextField trainIdField;
    private JLabel statusLabel, locationLabel, timeLabel;

    public TrackTrainPage(String username) {
        this.username = username;
        setTitle("Track Train Live");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(new EmptyBorder(30, 50, 30, 50));

        trainIdField = new JTextField();
        JButton trackBtn = new JButton("Fetch Live Status");
        UIUtils.styleButton(trackBtn, UIUtils.PRIMARY_COLOR);

        statusLabel = new JLabel("Status: --");
        locationLabel = new JLabel("Current Location: --");
        timeLabel = new JLabel("Last Updated: --");

        statusLabel.setFont(UIUtils.FONT_BOLD);
        
        panel.add(new JLabel("Enter Train ID to Track:"));
        panel.add(trainIdField);
        panel.add(trackBtn);
        panel.add(new JSeparator());
        panel.add(statusLabel);
        panel.add(locationLabel);
        panel.add(timeLabel);

        add(panel);

        trackBtn.addActionListener(e -> trackLive());
    }

    private void trackLive() {
        String id = trainIdField.getText().trim();
        if (id.isEmpty()) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT ts.status, ts.location, ts.timestamp, t.name " +
                           "FROM train_status ts " +
                           "JOIN trains t ON ts.train_id = t.train_id " +
                           "WHERE ts.train_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, Integer.parseInt(id));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                statusLabel.setText("Status: " + rs.getString("status") + " (" + rs.getString("name") + ")");
                locationLabel.setText("Current Location: " + rs.getString("location"));
                timeLabel.setText("Last Updated: " + rs.getString("timestamp"));
            } else {
                JOptionPane.showMessageDialog(this, "No tracking data found for this train.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error tracking train: " + e.getMessage());
        }
    }
}
