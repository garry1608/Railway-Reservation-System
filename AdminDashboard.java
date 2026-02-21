package com.railway;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminDashboard extends JFrame {
    private String adminUsername;
    private JTable trainTable, bookingTable;
    private DefaultTableModel trainModel, bookingModel;

    public AdminDashboard(String username) {
        this.adminUsername = username;
        setTitle("Railway Admin Panel - " + username);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIUtils.FONT_BOLD);

        tabbedPane.addTab("Manage Trains", createTrainTab());
        tabbedPane.addTab("All Bookings", createBookingTab());
        tabbedPane.addTab("Train Status Updates", createStatusTab());

        add(tabbedPane);
        
        loadTrains();
        loadAllBookings();
    }

    private JPanel createTrainTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        trainModel = new DefaultTableModel(new String[]{"ID", "Name", "Source", "Destination", "Departure", "Arrival"}, 0);
        trainTable = new JTable(trainModel);
        panel.add(new JScrollPane(trainTable), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add Train");
        JButton deleteButton = new JButton("Delete Train");
        UIUtils.styleButton(addButton, UIUtils.SECONDARY_COLOR);
        UIUtils.styleButton(deleteButton, UIUtils.ACCENT_COLOR);

        addButton.addActionListener(e -> openAddTrainDialog());
        deleteButton.addActionListener(e -> deleteSelectedTrain());

        actionPanel.add(addButton);
        actionPanel.add(deleteButton);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBookingTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        bookingModel = new DefaultTableModel(new String[]{"PNR", "User", "Train", "Status", "Amount", "Class"}, 0);
        bookingTable = new JTable(bookingModel);
        panel.add(new JScrollPane(bookingTable), BorderLayout.CENTER);

        return panel;
    }
    
    private JPanel createStatusTab() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(new EmptyBorder(50, 100, 50, 100));
        
        JTextField trainIdField = new JTextField();
        JTextField locationField = new JTextField();
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"On Time", "Delayed", "At Station", "In Transit", "Cancelled"});
        
        JButton updateButton = new JButton("Update Status");
        UIUtils.styleButton(updateButton, UIUtils.PRIMARY_COLOR);
        
        updateButton.addActionListener(e -> updateTrainStatus(trainIdField.getText(), statusCombo.getSelectedItem().toString(), locationField.getText()));
        
        panel.add(new JLabel("Train ID:")); panel.add(trainIdField);
        panel.add(new JLabel("Current Location:")); panel.add(locationField);
        panel.add(new JLabel("Status:")); panel.add(statusCombo);
        panel.add(new JLabel("")); panel.add(updateButton);
        
        return panel;
    }

    private void loadTrains() {
        trainModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM trains";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                trainModel.addRow(new Object[]{
                    rs.getInt("train_id"), rs.getString("name"),
                    rs.getString("source"), rs.getString("destination"),
                    rs.getString("departure_time"), rs.getString("arrival_time")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAllBookings() {
        bookingModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT b.pnr, u.username, t.name, b.status, b.amount, b.train_class " +
                           "FROM bookings b " +
                           "JOIN users u ON b.user_id = u.user_id " +
                           "JOIN trains t ON b.train_id = t.train_id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                bookingModel.addRow(new Object[]{
                    rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getDouble(5), rs.getString(6)
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openAddTrainDialog() {
        // Simplified add train dialog
        JTextField name = new JTextField();
        JTextField src = new JTextField();
        JTextField dest = new JTextField();
        JTextField dep = new JTextField("2026-02-25 10:00:00");
        JTextField arr = new JTextField("2026-02-25 18:00:00");
        
        Object[] message = {
            "Train Name:", name,
            "Source:", src,
            "Destination:", dest,
            "Departure (YYYY-MM-DD HH:MM:SS):", dep,
            "Arrival (YYYY-MM-DD HH:MM:SS):", arr
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Add New Train", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "INSERT INTO trains (name, source, destination, departure_time, arrival_time) VALUES (?, ?, ?, TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS'), TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS'))";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, name.getText());
                pstmt.setString(2, src.getText());
                pstmt.setString(3, dest.getText());
                pstmt.setString(4, dep.getText());
                pstmt.setString(5, arr.getText());
                pstmt.executeUpdate();
                loadTrains();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error adding train: " + ex.getMessage());
            }
        }
    }

    private void deleteSelectedTrain() {
        int row = trainTable.getSelectedRow();
        if (row != -1) {
            int id = (int) trainModel.getValueAt(row, 0);
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "DELETE FROM trains WHERE train_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
                loadTrains();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error deleting train. It might have bookings.");
            }
        }
    }
    
    private void updateTrainStatus(String id, String status, String loc) {
         try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "MERGE INTO train_status ts USING dual ON (ts.train_id = ?) " +
                         "WHEN MATCHED THEN UPDATE SET status = ?, location = ?, timestamp = SYSDATE " +
                         "WHEN NOT MATCHED THEN INSERT (train_id, status, location, timestamp) VALUES (?, ?, ?, SYSDATE)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(id));
            pstmt.setString(2, status);
            pstmt.setString(3, loc);
            pstmt.setInt(4, Integer.parseInt(id));
            pstmt.setString(5, status);
            pstmt.setString(6, loc);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Status updated successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
