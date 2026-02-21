package com.railway;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class BookTrainPage extends JFrame {
    private String username;
    private JComboBox<String> trainCombo, classCombo;
    private JTextField dateField;
    private JLabel fareLabel;
    private String selectedSeat = null;

    public BookTrainPage(String username) {
        this.username = username;
        setTitle("Book A Ticket");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 20));
        panel.setBorder(new EmptyBorder(30, 50, 30, 50));

        trainCombo = new JComboBox<>();
        classCombo = new JComboBox<>(new String[]{"AC First Class", "AC 2-Tier", "Sleeper", "General"});
        dateField = new JTextField("2026-02-25");
        fareLabel = new JLabel("Fare: ₹ 0.00");
        fareLabel.setFont(UIUtils.FONT_BOLD);

        JButton seatBtn = new JButton("Select Seat");
        UIUtils.styleButton(seatBtn, Color.GRAY);
        
        JButton bookBtn = new JButton("Confirm Booking");
        UIUtils.styleButton(bookBtn, UIUtils.SECONDARY_COLOR);

        // Add
        panel.add(new JLabel("Select Train:")); panel.add(trainCombo);
        panel.add(new JLabel("Select Class:")); panel.add(classCombo);
        panel.add(new JLabel("Travel Date:")); panel.add(dateField);
        panel.add(new JLabel("Seat Selection:")); panel.add(seatBtn);
        panel.add(new JLabel("Total Fare:")); panel.add(fareLabel);
        panel.add(new JLabel("")); panel.add(bookBtn);

        add(panel);

        // Actions
        seatBtn.addActionListener(e -> {
            SeatSelectionDialog diag = new SeatSelectionDialog(this, 10, 4);
            diag.setVisible(true);
            selectedSeat = diag.getSelectedSeat();
            if (selectedSeat != null) {
                seatBtn.setText("Seat: " + selectedSeat);
                calculateFare();
            }
        });

        trainCombo.addActionListener(e -> calculateFare());
        classCombo.addActionListener(e -> calculateFare());
        bookBtn.addActionListener(e -> handleBooking());

        loadTrains();
    }

    private void loadTrains() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT name FROM trains";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                trainCombo.addItem(rs.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void calculateFare() {
        // Dummy logic for fare
        double base = 500.0;
        String cls = (String) classCombo.getSelectedItem();
        if (cls.contains("AC")) base += 700;
        else if (cls.equals("Sleeper")) base += 200;
        
        fareLabel.setText("Fare: ₹ " + base);
    }

    private void handleBooking() {
        if (selectedSeat == null) {
            JOptionPane.showMessageDialog(this, "Please select a seat first.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get user_id
            int userId = -1;
            PreparedStatement userStmt = conn.prepareStatement("SELECT user_id FROM users WHERE username = ?");
            userStmt.setString(1, username);
            ResultSet rsUser = userStmt.executeQuery();
            if (rsUser.next()) userId = rsUser.getInt(1);

            // Get train_id
            int trainId = -1;
            PreparedStatement trainStmt = conn.prepareStatement("SELECT train_id FROM trains WHERE name = ?");
            trainStmt.setString(1, (String) trainCombo.getSelectedItem());
            ResultSet rsTrain = trainStmt.executeQuery();
            if (rsTrain.next()) trainId = rsTrain.getInt(1);

            String pnr = "PNR" + System.currentTimeMillis() % 1000000;
            String fare = fareLabel.getText().replace("Fare: ₹ ", "");

            String sql = "INSERT INTO bookings (user_id, train_id, status, amount, pnr, train_class) VALUES (?, ?, 'Booked', ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, trainId);
            pstmt.setDouble(3, Double.parseDouble(fare));
            pstmt.setString(4, pnr);
            pstmt.setString(5, (String) classCombo.getSelectedItem() + " (" + selectedSeat + ")");
            
            if (pstmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Booking Successful! PNR: " + pnr);
                dispose();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Booking Error: " + e.getMessage());
        }
    }
}
