package com.railway;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SearchTrainPage extends JFrame {
    private String username;
    private JTextField sourceField, destField;
    private DefaultTableModel model;

    public SearchTrainPage(String username) {
        this.username = username;
        setTitle("Search Trains");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Search Form
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        sourceField = new JTextField(15);
        destField = new JTextField(15);
        JButton searchBtn = new JButton("Search");
        UIUtils.styleButton(searchBtn, UIUtils.PRIMARY_COLOR);
        
        searchPanel.add(new JLabel("Source:")); searchPanel.add(sourceField);
        searchPanel.add(new JLabel("Destination:")); searchPanel.add(destField);
        searchPanel.add(searchBtn);
        
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(new String[]{"Train ID", "Name", "Source", "Destination", "Departure", "Arrival"}, 0);
        JTable table = new JTable(model);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        searchBtn.addActionListener(e -> performSearch());

        add(mainPanel);
    }

    private void performSearch() {
        model.setRowCount(0);
        String src = sourceField.getText().trim();
        String dest = destField.getText().trim();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM trains WHERE source LIKE ? AND destination LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + src + "%");
            stmt.setString(2, "%" + dest + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("train_id"), rs.getString("name"),
                    rs.getString("source"), rs.getString("destination"),
                    rs.getString("departure_time"), rs.getString("arrival_time")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Search Error: " + e.getMessage());
        }
    }
}
