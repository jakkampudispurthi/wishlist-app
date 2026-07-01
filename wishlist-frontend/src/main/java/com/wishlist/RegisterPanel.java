package com.wishlist;

import javax.swing.*;
import java.awt.*;
import org.json.JSONObject;

public class RegisterPanel extends JPanel {
    private WishListApp app;
    private JTextField emailField, usernameField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    private static final Color PRIMARY = new Color(99, 102, 241);
    private static final Color PRIMARY_DARK = new Color(79, 70, 229);
    private static final Color SUCCESS = new Color(16, 185, 129);
    private static final Color BG = new Color(248, 250, 252);
    private static final Color TEXT_LIGHT = new Color(100, 116, 139);
    private static final Color BORDER = new Color(226, 232, 240);

    public RegisterPanel(WishListApp app) {
        this.app = app;
        setLayout(new GridBagLayout());
        setBackground(BG);
        buildUI();
    }

    private JTextField createStyledField(int cols) {
        JTextField field = new JTextField(cols);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setPreferredSize(new Dimension(280, 42));
        return field;
    }

    private JPasswordField createStyledPassword() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setPreferredSize(new Dimension(280, 42));
        return field;
    }

    private void buildUI() {
        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        // Logo
        JLabel logo = new JLabel("🎁", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        c.gridx = 0; c.gridy = 0;
        card.add(logo, c);

        JLabel title = new JLabel("Create Account", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(PRIMARY);
        c.gridy = 1;
        card.add(title, c);

        JLabel subtitle = new JLabel("Start tracking your wishlist today!", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_LIGHT);
        c.gridy = 2;
        card.add(subtitle, c);

        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);
        c.gridy = 3;
        card.add(sep, c);

        // Email
        JLabel emailLbl = new JLabel("Email address");
        emailLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailLbl.setForeground(TEXT_LIGHT);
        c.gridy = 4;
        card.add(emailLbl, c);
        emailField = createStyledField(20);
        c.gridy = 5;
        card.add(emailField, c);

        // Username
        JLabel userLbl = new JLabel("Username");
        userLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userLbl.setForeground(TEXT_LIGHT);
        c.gridy = 6;
        card.add(userLbl, c);
        usernameField = createStyledField(20);
        c.gridy = 7;
        card.add(usernameField, c);

        // Password
        JLabel passLbl = new JLabel("Password");
        passLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLbl.setForeground(TEXT_LIGHT);
        c.gridy = 8;
        card.add(passLbl, c);
        passwordField = createStyledPassword();
        c.gridy = 9;
        card.add(passwordField, c);

        // Message
        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        c.gridy = 10;
        card.add(messageLabel, c);

        // Register button — purple gradient
        JButton registerBtn = new JButton("Create My Account") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, SUCCESS, getWidth(), 0, new Color(5, 150, 105));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerBtn.setContentAreaFilled(false);
        registerBtn.setBorderPainted(false);
        registerBtn.setFocusPainted(false);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerBtn.setPreferredSize(new Dimension(280, 44));
        c.gridy = 11;
        card.add(registerBtn, c);

        // Login link
        JButton loginBtn = new JButton("Already have an account? Sign in →");
        loginBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        loginBtn.setForeground(PRIMARY);
        loginBtn.setBorderPainted(false);
        loginBtn.setContentAreaFilled(false);
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        c.gridy = 12;
        card.add(loginBtn, c);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        add(card, gbc);

        registerBtn.addActionListener(e -> handleRegister());
        loginBtn.addActionListener(e -> app.showPanel("LOGIN"));
        passwordField.addActionListener(e -> handleRegister());
    }

    private void handleRegister() {
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            messageLabel.setForeground(new Color(239, 68, 68));
            messageLabel.setText("⚠️ Please fill in all fields!");
            return;
        }

        messageLabel.setForeground(new Color(59, 130, 246));
        messageLabel.setText("⏳ Creating your account...");

        String body = "{\"email\":\"" + email + "\",\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            protected String doInBackground() { return ApiClient.post("/auth/register", body); }
            protected void done() {
                try {
                    JSONObject json = new JSONObject(get());
                    if (json.has("message")) {
                        messageLabel.setForeground(new Color(16, 185, 129));
                        messageLabel.setText("✅ Account created! Redirecting to login...");
                        Timer timer = new Timer(1500, ev -> app.showPanel("LOGIN"));
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        messageLabel.setForeground(new Color(239, 68, 68));
                        messageLabel.setText("❌ " + json.optString("detail", "Registration failed!"));
                    }
                } catch (Exception e) {
                    messageLabel.setForeground(new Color(239, 68, 68));
                    messageLabel.setText("❌ Connection error. Is the server running?");
                }
            }
        };
        worker.execute();
    }
}