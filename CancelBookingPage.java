package com.railway;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class CancelBookingPage extends JFrame {
    private String username;
    private JTextField pnrField;

    public CancelBookingPage(String username) {
        this.username = username;
        setTitle("Cancel Booking");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(new EmptyBorder(30, 50, 30, 50));

        pnrField = new JTextField();
        JButton cancelBtn = new JButton("Cancel Reservation");
        UIUtils.styleButton(cancelBtn, UIUtils.ACCENT_COLOR);

        panel.add(new JLabel("Enter PNR to Cancel:"));
        panel.add(pnrField);
        panel.add(new JLabel(""));
        panel.add(cancelBtn);

        add(panel);

        cancelBtn.addActionListener(e -> handleCancellation());
    }

    private void handleCancellation() {
        String pnr = pnrField.getText().trim();
        if (pnr.isEmpty()) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel " + pnr + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check ownership and status
            String checkSql = "SELECT b.booking_id FROM bookings b JOIN users u ON b.user_id = u.user_id WHERE b.pnr = ? AND u.username = ? AND b.status = 'Booked'";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, pnr);
            checkStmt.setString(2, username);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                String updateSql = "UPDATE bookings SET status = 'Cancelled' WHERE pnr = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, pnr);
                if (updateStmt.executeUpdate() > 0) {
                    JOptionPane.showMessageDialog(this, "Booking Cancelled Successfully.");
                    dispose();
                }
            } else {
                JOptionPane.showMessageDialog(this, "PNR not found or already cancelled.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
