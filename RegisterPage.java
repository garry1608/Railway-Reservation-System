package com.railway;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterPage extends JFrame {
    private JTextField usernameField, fullNameField, emailField, phoneField;
    private JPasswordField passwordField, confirmPasswordField;

    public RegisterPage() {
        setTitle("Railway Reservation - Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 600);
        setLocationRelativeTo(null);

        BackgroundPanel backgroundPanel = new BackgroundPanel("c:/Users/Admin/Desktop/RailwayReservation/Railway.png");
        backgroundPanel.setLayout(new GridBagLayout());

        JPanel registerPanel = UIUtils.createRoundedPanel(30, new Color(255, 255, 255, 220));
        registerPanel.setLayout(new GridLayout(0, 2, 20, 15));
        registerPanel.setPreferredSize(new Dimension(600, 500));
        registerPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Title across two columns
        JLabel titleLabel = new JLabel("Join Us", SwingConstants.CENTER);
        titleLabel.setFont(UIUtils.FONT_TITLE);
        titleLabel.setForeground(UIUtils.TEXT_COLOR);

        usernameField = new JTextField();
        fullNameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();

        JButton registerButton = new JButton("Register");
        UIUtils.styleButton(registerButton, UIUtils.SECONDARY_COLOR);

        JButton backButton = new JButton("Back to Login");
        UIUtils.styleButton(backButton, UIUtils.PRIMARY_COLOR);

        // Add components
        registerPanel.add(titleLabel);
        registerPanel.add(new JLabel("")); // Spacer

        registerPanel.add(new JLabel("Username:"));
        registerPanel.add(usernameField);
        
        registerPanel.add(new JLabel("Full Name:"));
        registerPanel.add(fullNameField);

        registerPanel.add(new JLabel("Email:"));
        registerPanel.add(emailField);

        registerPanel.add(new JLabel("Phone Number:"));
        registerPanel.add(phoneField);

        registerPanel.add(new JLabel("Password:"));
        registerPanel.add(passwordField);

        registerPanel.add(new JLabel("Confirm Password:"));
        registerPanel.add(confirmPasswordField);

        registerPanel.add(backButton);
        registerPanel.add(registerButton);

        // Actions
        registerButton.addActionListener(e -> handleRegister());
        backButton.addActionListener(e -> {
            new LoginPage().setVisible(true);
            dispose();
        });

        backgroundPanel.add(registerPanel);
        setContentPane(backgroundPanel);
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty() || !password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Please check your inputs and ensure passwords match.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if user exists
            String checkQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Insert
            String query = "INSERT INTO users (username, password, email, full_name, phone_number) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email.isEmpty() ? "none@example.com" : email);
            stmt.setString(4, fullName.isEmpty() ? "New User" : fullName);
            stmt.setString(5, phone.isEmpty() ? "0000000000" : phone);
            
            if (stmt.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Registration Successful!");
                new LoginPage().setVisible(true);
                dispose();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
