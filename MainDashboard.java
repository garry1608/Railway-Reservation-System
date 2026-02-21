package com.railway;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainDashboard extends JFrame {
    private String username;

    public MainDashboard(String username) {
        this.username = username;
        setTitle("Railway System - Dashboard (" + username + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        BackgroundPanel backgroundPanel = new BackgroundPanel("c:/Users/Admin/Desktop/RailwayReservation/Railway.png");
        backgroundPanel.setLayout(new BorderLayout());

        // Sidebar or Header
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UIUtils.PRIMARY_COLOR);
        topPanel.setPreferredSize(new Dimension(0, 80));
        topPanel.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel welcomeLabel = new JLabel("Railway Ticket Reservation System");
        welcomeLabel.setFont(UIUtils.FONT_TITLE);
        welcomeLabel.setForeground(Color.WHITE);
        topPanel.add(welcomeLabel, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        JLabel nameLabel = new JLabel("Welcome, " + username);
        nameLabel.setFont(UIUtils.FONT_BOLD);
        nameLabel.setForeground(Color.WHITE);
        JButton logoutButton = new JButton("Logout");
        UIUtils.styleButton(logoutButton, UIUtils.ACCENT_COLOR);
        logoutButton.addActionListener(e -> {
            new LoginPage().setVisible(true);
            dispose();
        });
        userPanel.add(nameLabel);
        userPanel.add(logoutButton);
        topPanel.add(userPanel, BorderLayout.EAST);

        // Content Area with Grid of Buttons
        JPanel contentPanel = new JPanel(new GridLayout(2, 4, 30, 30));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(50, 50, 50, 50));

        addDashboardButton(contentPanel, "Search Trains", "c:/Users/Admin/Desktop/RailwayReservation/Railway2.png", e -> new SearchTrainPage(username).setVisible(true));
        addDashboardButton(contentPanel, "Book Ticket", "c:/Users/Admin/Desktop/RailwayReservation/Railway2.png", e -> new BookTrainPage(username).setVisible(true));
        addDashboardButton(contentPanel, "Booking History", "c:/Users/Admin/Desktop/RailwayReservation/Railway2.png", e -> new BookingHistoryPage(username).setVisible(true));
        addDashboardButton(contentPanel, "Track Train", "c:/Users/Admin/Desktop/RailwayReservation/Railway2.png", e -> new TrackTrainPage(username).setVisible(true));
        addDashboardButton(contentPanel, "Train Schedule", "c:/Users/Admin/Desktop/RailwayReservation/Railway2.png", e -> new TrainSchedulePage(username).setVisible(true));
        addDashboardButton(contentPanel, "View Bookings", "c:/Users/Admin/Desktop/RailwayReservation/Railway2.png", e -> new ViewBookingsPage(username).setVisible(true));
        addDashboardButton(contentPanel, "Cancel Ticket", "c:/Users/Admin/Desktop/RailwayReservation/Railway2.png", e -> new CancelBookingPage(username).setVisible(true));
        addDashboardButton(contentPanel, "Edit Profile", "c:/Users/Admin/Desktop/RailwayReservation/Railway2.png", e -> openProfileDialog());
        
        // Special button for Admin (check if admin)
        if (isAdmin(username)) {
            addDashboardButton(contentPanel, "Admin Panel", "c:/Users/Admin/Desktop/RailwayReservation/Railway2.png", e -> new AdminDashboard(username).setVisible(true));
        }

        backgroundPanel.add(topPanel, BorderLayout.NORTH);
        backgroundPanel.add(contentPanel, BorderLayout.CENTER);
        setContentPane(backgroundPanel);
    }

    private void openProfileDialog() {
        JTextField name = new JTextField();
        JTextField email = new JTextField();
        JTextField phone = new JTextField();
        
        try (java.sql.Connection conn = DatabaseConnection.getConnection()) {
            java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT full_name, email, phone_number FROM users WHERE username = ?");
            stmt.setString(1, username);
            java.sql.ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                name.setText(rs.getString(1));
                email.setText(rs.getString(2));
                phone.setText(rs.getString(3));
            }
        } catch (Exception e) { e.printStackTrace(); }

        Object[] message = { "Full Name:", name, "Email:", email, "Phone:", phone };
        int option = JOptionPane.showConfirmDialog(this, message, "Update Profile", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (java.sql.Connection conn = DatabaseConnection.getConnection()) {
                java.sql.PreparedStatement stmt = conn.prepareStatement("UPDATE users SET full_name = ?, email = ?, phone_number = ? WHERE username = ?");
                stmt.setString(1, name.getText());
                stmt.setString(2, email.getText());
                stmt.setString(3, phone.getText());
                stmt.setString(4, username);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Profile Updated!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage()); }
        }
    }

    private boolean isAdmin(String username) {
        // Simple check: 'admin' is always admin, or check 'admin' table
        try (java.sql.Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM admin WHERE username = ?";
            java.sql.PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            java.sql.ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (java.sql.SQLException e) {
            return username.equalsIgnoreCase("admin"); // Fallback
        }
    }

    private void addDashboardButton(JPanel parent, String text, String iconPath, java.awt.event.ActionListener action) {
        JPanel btnPanel = UIUtils.createRoundedPanel(20, new Color(255, 255, 255, 200));
        btnPanel.setLayout(new BorderLayout());
        
        JButton button = new JButton(text);
        button.setFont(UIUtils.FONT_BOLD);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(action);
        
        btnPanel.add(button, BorderLayout.CENTER);
        parent.add(btnPanel);
    }
}
