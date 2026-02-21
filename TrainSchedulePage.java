package com.railway;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class TrainSchedulePage extends JFrame {
    private DefaultTableModel model;

    public TrainSchedulePage(String username) {
        setTitle("Train Schedule");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        model = new DefaultTableModel(new String[]{"Train Name", "Source", "Destination", "Departure", "Arrival"}, 0);
        JTable table = new JTable(model);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        loadSchedule();
        add(mainPanel);
    }

    private void loadSchedule() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT name, source, destination, departure_time, arrival_time FROM trains ORDER BY departure_time";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5)
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
