package views;

import java.awt.*;
import javax.swing.*;
import models.User;

public class MainApp extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private User currentUser;

    public static final String LOGIN_VIEW = "LOGIN";
    public static final String REGISTER_VIEW = "REGISTER";
    public static final String ADMIN_VIEW = "ADMIN";
    public static final String CUSTOMER_VIEW = "CUSTOMER";

    public MainApp() {
        setTitle("EazyBus Mobile"); // Updated Window Title

        // --- IPHONE 14 PRO MAX DIMENSIONS ---
        // Width: 430px, Height: 932px (Standard logical resolution)
        setSize(430, 932); 
        setResizable(false); // Lock the size so it feels like a phone
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Set a white background for the main container
        mainPanel.setBackground(Color.WHITE); 

        // Initialize Views
        mainPanel.add(new AuthViews.LoginPanel(this), LOGIN_VIEW);
        mainPanel.add(new AuthViews.RegisterPanel(this), REGISTER_VIEW);

        add(mainPanel);
        showScreen(LOGIN_VIEW);
    }

    // ... (Keep the showScreen, loginSuccess, logout, getCurrentUser, and main methods exactly the same) ...
    public void showScreen(String screenName) {
        cardLayout.show(mainPanel, screenName);
    }

    public void loginSuccess(User user) {
        this.currentUser = user;
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            mainPanel.add(new AdminViews(this), ADMIN_VIEW);
            showScreen(ADMIN_VIEW);
        } else {
            mainPanel.add(new CustomerViews(this, user), CUSTOMER_VIEW);
            showScreen(CUSTOMER_VIEW);
        }
    }

    public void logout() {
        this.currentUser = null;
        showScreen(LOGIN_VIEW);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainApp().setVisible(true);
        });
    }
}