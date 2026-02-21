package com.railway;

import javax.swing.*;
import java.awt.*;

public class SeatSelectionDialog extends JDialog {
    private String selectedSeat = null;
    private JButton[] seatButtons;

    public SeatSelectionDialog(JFrame parent, int rows, int cols) {
        super(parent, "Select Your Seat", true);
        setSize(400, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JPanel gridPanel = new JPanel(new GridLayout(rows, cols, 10, 10));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        seatButtons = new JButton[rows * cols];
        for (int i = 0; i < rows * cols; i++) {
            String seatName = (char)('A' + (i / cols)) + "" + (i % cols + 1);
            JButton btn = new JButton(seatName);
            btn.setBackground(Color.WHITE);
            btn.addActionListener(e -> {
                selectedSeat = seatName;
                dispose();
            });
            seatButtons[i] = btn;
            gridPanel.add(btn);
        }

        add(new JLabel("  Select a seat to proceed", SwingConstants.CENTER), BorderLayout.NORTH);
        add(gridPanel, BorderLayout.CENTER);
    }

    public String getSelectedSeat() {
        return selectedSeat;
    }
}
