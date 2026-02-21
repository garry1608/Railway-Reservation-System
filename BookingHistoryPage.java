package com.railway;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class BookingHistoryPage extends JFrame {
    private String username;
    private DefaultTableModel model;

    public BookingHistoryPage(String username) {
        this.username = username;
        setTitle("Booking History - " + username);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        model = new DefaultTableModel(new String[]{"PNR", "Train Name", "Amount", "Class", "Status"}, 0);
        JTable table = new JTable(model);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        loadHistory();
        add(mainPanel);
    }

    private void loadHistory() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT b.pnr, t.name, b.amount, b.train_class, b.status " +
                           "FROM bookings b " +
                           "JOIN users u ON b.user_id = u.user_id " +
                           "JOIN trains t ON b.train_id = t.train_id " +
                           "WHERE u.username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString(1), rs.getString(2), rs.getDouble(3), 
                    rs.getString(4), rs.getString(5)
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
