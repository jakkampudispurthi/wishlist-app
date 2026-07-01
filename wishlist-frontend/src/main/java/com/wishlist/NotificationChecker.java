package com.wishlist;

import javax.swing.*;
import java.awt.*;
import java.awt.TrayIcon.MessageType;
import org.json.*;
import java.util.Set;
import java.util.HashSet;

public class NotificationChecker {
    private static javax.swing.Timer checkTimer;
    private static TrayIcon trayIcon;
    private static Set<Integer> seenNotificationIds = new HashSet<>();

    public static void start() {
        setupSystemTray();

        // Check every 60 seconds
        checkTimer = new javax.swing.Timer(60000, e -> checkNotifications());
        checkTimer.start();

        // Check immediately on start
        checkNotifications();
    }

    public static void stop() {
        if (checkTimer != null) {
            checkTimer.stop();
        }
    }

    private static void setupSystemTray() {
        if (!SystemTray.isSupported()) {
            System.out.println("System tray not supported!");
            return;
        }

        try {
            SystemTray tray = SystemTray.getSystemTray();

            // Create a simple icon
            Image image = createTrayImage();

            trayIcon = new TrayIcon(image, "WishList - Price Tracker");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("WishList - Tracking your deals!");

            tray.add(trayIcon);
        } catch (Exception e) {
            System.out.println("Could not create system tray icon: " + e.getMessage());
        }
    }

    private static Image createTrayImage() {
        int size = 16;
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(
            size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(99, 102, 241));
        g2.fillOval(0, 0, size, size);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 11));
        g2.drawString("W", 4, 12);
        g2.dispose();
        return img;
    }

    private static void checkNotifications() {
        if (!ApiClient.isLoggedIn()) return;

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            protected String doInBackground() {
                return ApiClient.getAuth("/wishlist/notifications");
            }
            protected void done() {
                try {
                    String response = get();
                    JSONArray notifications = new JSONArray(response);

                    for (int i = 0; i < notifications.length(); i++) {
                        JSONObject notif = notifications.getJSONObject(i);
                        int id = notif.getInt("id");

                        // Only show popup for notifications we haven't seen yet
                        if (!seenNotificationIds.contains(id)) {
                            seenNotificationIds.add(id);
                            String message = notif.getString("message");
                            showPopup("💰 Price Drop Alert!", message);
                        }
                    }
                } catch (Exception e) {
                    // Silently fail - server might be down temporarily
                }
            }
        };
        worker.execute();
    }

    private static void showPopup(String title, String message) {
        if (trayIcon != null) {
            trayIcon.displayMessage(title, message, MessageType.INFO);
        } else {
            // Fallback: show a small popup window
            SwingUtilities.invokeLater(() -> {
                JWindow popup = new JWindow();
                JPanel panel = new JPanel(new BorderLayout());
                panel.setBackground(new Color(99, 102, 241));
                panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

                JLabel titleLabel = new JLabel(title);
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                titleLabel.setForeground(Color.WHITE);

                JLabel msgLabel = new JLabel("<html><body style='width: 250px'>" + message + "</body></html>");
                msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                msgLabel.setForeground(Color.WHITE);
                msgLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

                panel.add(titleLabel, BorderLayout.NORTH);
                panel.add(msgLabel, BorderLayout.CENTER);

                popup.add(panel);
                popup.pack();

                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                popup.setLocation(screenSize.width - popup.getWidth() - 20,
                                   screenSize.height - popup.getHeight() - 60);
                popup.setVisible(true);

                javax.swing.Timer closeTimer = new javax.swing.Timer(5000, e -> popup.dispose());
                closeTimer.setRepeats(false);
                closeTimer.start();
            });
        }
    }

    // Force a manual check (called from refresh button)
    public static void checkNow() {
        checkNotifications();
    }
}