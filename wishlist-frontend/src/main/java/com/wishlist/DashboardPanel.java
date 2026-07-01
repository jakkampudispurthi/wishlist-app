package com.wishlist;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import org.json.*;

public class DashboardPanel extends JPanel {
    private WishListApp app;
    private JTable wishlistTable;
    private DefaultTableModel tableModel;
    private JTextField urlField;
    private JTextField nameField;
    private JTextField priceField;
    private JComboBox<String> categoryCombo;
    private JLabel statusLabel;
    private JLabel notificationLabel;

    public DashboardPanel(WishListApp app) {
        this.app = app;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        buildUI();
    }

    private void buildUI() {
        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(99, 102, 241));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("🎁 My WishList");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        topBar.add(title, BorderLayout.WEST);

        notificationLabel = new JLabel("🔔 0 notifications");
        notificationLabel.setForeground(Color.WHITE);
        notificationLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(Color.WHITE);
        logoutBtn.setForeground(new Color(99, 102, 241));
        logoutBtn.setOpaque(true);
        logoutBtn.setBorderPainted(false);
        logoutBtn.addActionListener(e -> {
            NotificationChecker.stop();
            ApiClient.logout();
            app.showPanel("LOGIN");
        });

        JPanel rightPanel = new JPanel(new FlowLayout());
        rightPanel.setOpaque(false);
        rightPanel.add(notificationLabel);
        rightPanel.add(logoutBtn);
        topBar.add(rightPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Add item form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(15, 15, 15, 15),
            BorderFactory.createTitledBorder("Add New Item")
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("URL:"), gbc);
        urlField = new JTextField(30);
        gbc.gridx = 1; gbc.gridwidth = 3;
        formPanel.add(urlField, gbc);

        JButton scrapeBtn = new JButton("🔍 Auto-fill");
        scrapeBtn.setBackground(new Color(16, 185, 129));
        scrapeBtn.setForeground(Color.WHITE);
        scrapeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        scrapeBtn.setOpaque(true);
        scrapeBtn.setBorderPainted(false);
        gbc.gridx = 4; gbc.gridwidth = 1;
        formPanel.add(scrapeBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Name:"), gbc);
        nameField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Price:"), gbc);
        priceField = new JTextField(10);
        gbc.gridx = 3;
        formPanel.add(priceField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Category:"), gbc);
        categoryCombo = new JComboBox<>(new String[]{"must-buy", "maybe-later", "need-next-time"});
        gbc.gridx = 1; gbc.gridwidth = 1;
        formPanel.add(categoryCombo, gbc);

        JButton addBtn = new JButton("➕ Add to WishList");
        addBtn.setBackground(new Color(99, 102, 241));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Arial", Font.BOLD, 13));
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.setOpaque(true);
        addBtn.setBorderPainted(false);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 5;
        formPanel.add(addBtn, gbc);

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(new Color(0, 150, 0));
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 5;
        formPanel.add(statusLabel, gbc);

        add(formPanel, BorderLayout.NORTH);

        // Wishlist table
        String[] columns = {"ID", "Product Name", "Price", "Category", "Store", "URL"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        wishlistTable = new JTable(tableModel);
        wishlistTable.setRowHeight(30);
        wishlistTable.setFont(new Font("Arial", Font.PLAIN, 13));
        wishlistTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        wishlistTable.getTableHeader().setBackground(new Color(99, 102, 241));
        wishlistTable.getTableHeader().setForeground(Color.WHITE);
        wishlistTable.setSelectionBackground(new Color(224, 231, 255));

        // Hide ID and URL columns
        wishlistTable.getColumnModel().getColumn(0).setMinWidth(0);
        wishlistTable.getColumnModel().getColumn(0).setMaxWidth(0);
        wishlistTable.getColumnModel().getColumn(5).setMinWidth(0);
        wishlistTable.getColumnModel().getColumn(5).setMaxWidth(0);

        JScrollPane scrollPane = new JScrollPane(wishlistTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Bottom buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(new Color(245, 247, 250));

        JButton deleteBtn = new JButton("🗑️ Delete Selected");
        deleteBtn.setBackground(new Color(239, 68, 68));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteBtn.setOpaque(true);
        deleteBtn.setBorderPainted(false);

        JButton refreshBtn = new JButton("🔄 Refresh");
        refreshBtn.setBackground(new Color(59, 130, 246));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.setOpaque(true);
        refreshBtn.setBorderPainted(false);

        bottomPanel.add(deleteBtn);
        bottomPanel.add(refreshBtn);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        // Actions
        scrapeBtn.addActionListener(e -> handleScrape());
        addBtn.addActionListener(e -> handleAddItem());
        deleteBtn.addActionListener(e -> handleDelete());
        refreshBtn.addActionListener(e -> refreshData());
    }

    private void handleScrape() {
        String url = urlField.getText().trim();
        if (url.isEmpty()) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Please enter a URL first!");
            return;
        }
        statusLabel.setForeground(Color.BLUE);
        statusLabel.setText("Scraping product info...");

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            protected String doInBackground() {
                return ApiClient.postAuth("/wishlist/scrape",
                    "{\"url\":\"" + url + "\"}");
            }
            protected void done() {
                try {
                    String response = get();
                    JSONObject json = new JSONObject(response);
                    if (json.optBoolean("success", false)) {
                        if (!json.isNull("product_name"))
                            nameField.setText(json.getString("product_name"));
                        if (!json.isNull("current_price"))
                            priceField.setText(String.valueOf(json.getDouble("current_price")));
                        statusLabel.setForeground(new Color(0, 150, 0));
                        statusLabel.setText("✅ Product info filled automatically!");
                    } else {
                        statusLabel.setForeground(Color.RED);
                        statusLabel.setText("Could not scrape. Fill in manually.");
                    }
                } catch (Exception e) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("Scraping failed. Fill in manually.");
                }
            }
        };
        worker.execute();
    }

    private void handleAddItem() {
        String url = urlField.getText().trim();
        String name = nameField.getText().trim();
        String priceText = priceField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();

        if (url.isEmpty()) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("URL is required!");
            return;
        }

        String priceJson = priceText.isEmpty() ? "null" : priceText;
        String nameJson = name.isEmpty() ? "null" : "\"" + name + "\"";
        String body = "{\"url\":\"" + url + "\",\"product_name\":" + nameJson +
            ",\"current_price\":" + priceJson + ",\"category\":\"" + category + "\"}";

        statusLabel.setForeground(Color.BLUE);
        statusLabel.setText("Adding item...");

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            protected String doInBackground() {
                return ApiClient.postAuth("/wishlist/", body);
            }
            protected void done() {
                try {
                    String response = get();
                    JSONObject json = new JSONObject(response);
                    if (json.has("item_id")) {
                        statusLabel.setForeground(new Color(0, 150, 0));
                        statusLabel.setText("✅ Item added successfully!");
                        urlField.setText("");
                        nameField.setText("");
                        priceField.setText("");
                        refreshData();
                    } else {
                        statusLabel.setForeground(Color.RED);
                        statusLabel.setText("Failed to add item!");
                    }
                } catch (Exception e) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("Error adding item!");
                }
            }
        };
        worker.execute();
    }

    private void handleDelete() {
        int selectedRow = wishlistTable.getSelectedRow();
        if (selectedRow == -1) {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Please select an item to delete!");
            return;
        }

        int itemId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete this item from your wishlist?", "Confirm Delete",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String response = ApiClient.deleteAuth("/wishlist/" + itemId);
            statusLabel.setForeground(new Color(0, 150, 0));
            statusLabel.setText("✅ Item deleted!");
            refreshData();
        }
    }

    public void refreshData() {
        SwingWorker<String, Void> worker = new SwingWorker<>() {
            protected String doInBackground() {
                return ApiClient.getAuth("/wishlist/");
            }
            protected void done() {
                try {
                    String response = get();
                    JSONArray items = new JSONArray(response);
                    tableModel.setRowCount(0);
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        tableModel.addRow(new Object[]{
                            item.getInt("id"),
                            item.optString("product_name", "Unknown"),
                            item.isNull("current_price") ? "N/A" : "$" + item.getDouble("current_price"),
                            item.optString("category", ""),
                            item.optString("store_name", ""),
                            item.optString("url", "")
                        });
                    }
                } catch (Exception e) {
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setText("Error loading wishlist!");
                }
            }
        };
        worker.execute();

        SwingWorker<String, Void> notifWorker = new SwingWorker<>() {
            protected String doInBackground() {
                return ApiClient.getAuth("/wishlist/notifications");
            }
            protected void done() {
                try {
                    String response = get();
                    JSONArray notifications = new JSONArray(response);
                    int count = notifications.length();
                    notificationLabel.setText("🔔 " + count + " notification" + (count != 1 ? "s" : ""));
                    if (count > 0) {
                        notificationLabel.setForeground(new Color(255, 220, 50));
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
        };
        notifWorker.execute();
    }
}