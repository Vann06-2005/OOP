package views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import models.Schedule;

public class BusCardPanel extends JPanel {
    
    public BusCardPanel(Schedule schedule, ActionListener onBookClicked) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(5, 10, 5, 10),
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true)
        ));
        
        // --- CENTER: Trip Info ---
        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. Time & Route
        JLabel routeLabel = new JLabel(schedule.getDepartureTime().toLocalTime() + " • " + 
                                       schedule.getRoute().getSourceCity() + " > " + 
                                       schedule.getRoute().getDestinationCity());
        routeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        // 2. Bus Details
        JLabel busLabel = new JLabel(schedule.getBus().getBusNumber() + " (" + schedule.getBus().getType() + ")");
        busLabel.setForeground(Color.GRAY);
        busLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // 3. Price & Seats
        JLabel priceLabel = new JLabel("$" + schedule.getTicketPrice() + " • " + schedule.getAvailableSeats() + " seats left");
        priceLabel.setForeground(new Color(0, 150, 0)); // Money Green
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 12));

        infoPanel.add(routeLabel);
        infoPanel.add(busLabel);
        infoPanel.add(priceLabel);
        add(infoPanel, BorderLayout.CENTER);

        // --- RIGHT: Book Button ---
        JButton bookBtn = new JButton("Book");
        bookBtn.setBackground(new Color(50, 150, 250)); // App Blue
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setFocusPainted(false);
        bookBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        bookBtn.addActionListener(onBookClicked);
        
        JPanel btnWrapper = new JPanel(new GridBagLayout());
        btnWrapper.setBackground(Color.WHITE);
        btnWrapper.add(bookBtn);
        btnWrapper.setBorder(new EmptyBorder(0, 0, 0, 10));
        
        add(btnWrapper, BorderLayout.EAST);
        
        // Fix height
        setPreferredSize(new Dimension(0, 100));
        setMaximumSize(new Dimension(9999, 100)); 
    }
}