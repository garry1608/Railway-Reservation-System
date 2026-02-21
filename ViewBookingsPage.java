package com.railway;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.io.PrintWriter;

public class ViewBookingsPage extends JFrame {
    private String username;
    private DefaultTableModel model;
    private JTable table;

    public ViewBookingsPage(String username) {
        this.username = username;
        setTitle("My Active Bookings");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        model = new DefaultTableModel(new String[]{"PNR", "Train", "Fare", "Class/Seat", "Status"}, 0);
        table = new JTable(model); // Initialize the field
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Add the download button panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton downloadBtn = new JButton("Download Selected Ticket");
        // Assuming UIUtils exists and styleButton method is available.
        // If not, this line will cause a compilation error and needs to be removed or replaced.
        // For this change, I'll assume it exists as per the instruction.
        UIUtils.styleButton(downloadBtn, UIUtils.PRIMARY_COLOR); 
        downloadBtn.addActionListener(e -> downloadTicket(table));
        bottomPanel.add(downloadBtn);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH); // Add bottom panel to mainPanel

        loadActiveBookings();
        add(mainPanel);
    }

    private void downloadTicket(JTable table) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking first.");
            return;
        }
        String pnr = (String) model.getValueAt(row, 0);
        String train = (String) model.getValueAt(row, 1);
        String fare = String.valueOf(model.getValueAt(row, 2));

        try (PrintWriter out = new PrintWriter("Ticket_" + pnr + ".txt")) {
            out.println("--- RAILWAY E-TICKET ---");
            out.println("PNR: " + pnr);
            out.println("TRAIN: " + train);
            out.println("PASSENGER: " + username);
            out.println("FARE: RS. " + fare);
            out.println("------------------------");
            JOptionPane.showMessageDialog(this, "Ticket downloaded as 'Ticket_" + pnr + ".txt'");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving ticket: " + ex.getMessage());
        }
    }

    private void loadActiveBookings() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT b.pnr, t.name, b.amount, b.train_class, b.status " +
                           "FROM bookings b " +
                           "JOIN users u ON b.user_id = u.user_id " +
                           "JOIN trains t ON b.train_id = t.train_id " +
                           "WHERE u.username = ? AND b.status = 'Booked'";
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
