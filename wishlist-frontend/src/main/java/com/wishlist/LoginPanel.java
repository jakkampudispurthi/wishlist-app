package com.wishlist;

import javax.swing.*;
import java.awt.*;
import org.json.JSONObject;

public class LoginPanel extends JPanel {
    private WishListApp app;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel messageLabel;

    private static final Color PRIMARY = new Color(99, 102, 241);
    private static final Color PRIMARY_DARK = new Color(79, 70, 229);
    private static final Color BG = new Color(248, 250, 252);
    private static final Color TEXT = new Color(30, 41, 59);
    private static final Color TEXT_LIGHT = new Color(100, 116, 139);
    private static final Color BORDER = new Color(226, 232, 240);

    public LoginPanel(WishListApp app) {
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
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;

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

        JLabel logo = new JLabel("🎁", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        c.gridx = 0; c.gridy = 0;
        card.add(logo, c);

        JLabel title = new JLabel("WishList", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(PRIMARY);
        c.gridy = 1;
        card.add(title, c);

        JLabel subtitle = new JLabel("Sign in to track your deals", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_LIGHT);
        c.gridy = 2;
        card.add(subtitle, c);

        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);
        c.gridy = 3;
        card.add(sep, c);

        JLabel emailLbl = new JLabel("Email address");
        emailLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailLbl.setForeground(TEXT_LIGHT);
        c.gridy = 4;
        card.add(emailLbl, c);

        emailField = createStyledField(20);
        c.gridy = 5;
        card.add(emailField, c);

        JLabel passLbl = new JLabel("Password");
        passLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLbl.setForeground(TEXT_LIGHT);
        c.gridy = 6;
        card.add(passLbl, c);

        passwordField = createStyledPassword();
        c.gridy = 7;
        card.add(passwordField, c);

        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        messageLabel.setForeground(Color.RED);
        c.gridy = 8;
        card.add(messageLabel, c);

        JButton loginBtn = new JButton("Sign In") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY, getWidth(), 0, PRIMARY_DARK);
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
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setContentAreaFilled(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setPreferredSize(new Dimension(280, 44));
        c.gridy = 9;
        card.add(loginBtn, c);

        JButton registerBtn = new JButton("Don't have an account? Create one →");
        registerBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        registerBtn.setForeground(PRIMARY);
        registerBtn.setBorderPainted(false);
        registerBtn.setContentAreaFilled(false);
        registerBtn.setFocusPainted(false);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        c.gridy = 10;
        card.add(registerBtn, c);

        gbc.gridx = 0; gbc.gridy = 0;
        add(card, gbc);

        loginBtn.addActionListener(e -> handleLogin());
        registerBtn.addActionListener(e -> app.showPanel("REGISTER"));
        passwordField.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (email.isEmpty() || password.isEmpty()) {
            messageLabel.setText("⚠️ Please fill in all fields!");
            return;
        }
        messageLabel.setForeground(new Color(59, 130, 246));
        messageLabel.setText("⏳ Signing in...");
        String body = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            protected String doInBackground() { return ApiClient.post("/auth/login", body); }
            protected void done() {
                try {
                    JSONObject json = new JSONObject(get());
                    if (json.has("access_token")) {
                        ApiClient.setToken(json.getString("access_token"));
                        messageLabel.setText("");
                        NotificationChecker.start();
                        app.showPanel("DASHBOARD");
                    } else {
                        messageLabel.setForeground(Color.RED);
                        messageLabel.setText("❌ Invalid email or password!");
                    }
                } catch (Exception e) {
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("❌ Connection error. Is the server running?");
                }
            }
        };
        worker.execute();
    }
}