package com.railway;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPage() {
        setTitle("Railway Reservation - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        // Main Background Panel
        BackgroundPanel backgroundPanel = new BackgroundPanel("c:/Users/Admin/Desktop/RailwayReservation/Railway2.png");
        backgroundPanel.setLayout(new GridBagLayout());

        // Login Form Panel
        JPanel loginPanel = UIUtils.createRoundedPanel(30, new Color(255, 255, 255, 220));
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setPreferredSize(new Dimension(350, 400));
        loginPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Title
        JLabel titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(UIUtils.FONT_TITLE);
        titleLabel.setForeground(UIUtils.TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Fields
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        styleField(usernameField, "Username");
        styleField(passwordField, "Password");

        // Buttons
        JButton loginButton = new JButton("Login");
        UIUtils.styleButton(loginButton, UIUtils.PRIMARY_COLOR);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton registerButton = new JButton("New User? Register Here");
        registerButton.setContentAreaFilled(false);
        registerButton.setBorderPainted(false);
        registerButton.setForeground(UIUtils.PRIMARY_COLOR);
        registerButton.setFont(UIUtils.FONT_REGULAR);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Actions
        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> {
            new RegisterPage().setVisible(true);
            dispose();
        });

        // Add to panel
        loginPanel.add(titleLabel);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        loginPanel.add(new JLabel("Username"));
        loginPanel.add(usernameField);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        loginPanel.add(new JLabel("Password"));
        loginPanel.add(passwordField);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        loginPanel.add(loginButton);
        loginPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        loginPanel.add(registerButton);

        backgroundPanel.add(loginPanel);
        setContentPane(backgroundPanel);
    }

    private void styleField(JTextField field, String placeholder) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setFont(UIUtils.FONT_REGULAR);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Login Successful!");
                new MainDashboard(username).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Username or Password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}
