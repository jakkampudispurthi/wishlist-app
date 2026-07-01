package com.wishlist;

import javax.swing.*;
import java.awt.*;

public class WishListApp extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                // Make buttons colorful globally
                UIManager.put("Button.arc", 10);
                UIManager.put("Component.arc", 10);
            } catch (Exception e) {
                e.printStackTrace();
            }
            new WishListApp().setVisible(true);
        });
    }

    public WishListApp() {
        setTitle("🎁 WishList — Track Deals & Price Drops");
        setSize(1000, 700);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set taskbar icon color via title bar
        try {
            setIconImage(createAppIcon());
        } catch (Exception e) {}

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(new LoginPanel(this), "LOGIN");
        mainPanel.add(new RegisterPanel(this), "REGISTER");
        mainPanel.add(new DashboardPanel(this), "DASHBOARD");

        add(mainPanel);
        showPanel("LOGIN");
    }

    private Image createAppIcon() {
        // Create a simple colored icon
        JPanel iconPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(99, 102, 241));
                g2.fillOval(0, 0, 32, 32);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 20));
                g2.drawString("W", 8, 24);
            }
        };
        iconPanel.setSize(32, 32);
        Image img = iconPanel.createImage(32, 32);
        return img;
    }

    public void showPanel(String name) {
        cardLayout.show(mainPanel, name);
        if (name.equals("DASHBOARD")) {
            for (Component comp : mainPanel.getComponents()) {
                if (comp instanceof DashboardPanel) {
                    ((DashboardPanel) comp).refreshData();
                }
            }
        }
    }
}