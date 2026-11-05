package com.powerbank.ui;

import com.powerbank.data.FileStorage;
import com.powerbank.service.AlertManager;
import com.powerbank.service.BorrowingManager;
import com.powerbank.service.InventoryManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Modern Admin Login Window.
 * Navy blue, white, and teal design.
 */
public class Login extends JFrame {

    private final InventoryManager inventoryManager;
    private final BorrowingManager borrowingManager;
    private final AlertManager alertManager;
    private final FileStorage fileStorage;

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JLabel lblStatus;

    public Login(InventoryManager inventoryManager, BorrowingManager borrowingManager, AlertManager alertManager, FileStorage fileStorage) {
        this.inventoryManager = inventoryManager;
        this.borrowingManager = borrowingManager;
        this.alertManager = alertManager;
        this.fileStorage = fileStorage;

        initUI();
    }

    private void initUI() {
        setTitle("Admin Portal - Smart Powerbank Lending System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UITheme.BG_LIGHT);
        setLayout(new BorderLayout());

        // Header Panel with Navy Gradient
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, UITheme.NAVY_DARK, getWidth(), getHeight(), UITheme.NAVY_SIDEBAR);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(460, 130));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(25, 20, 20, 20));

        JLabel lblIcon = new JLabel("⚡ SMART POWERBANK");
        lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblIcon.setForeground(UITheme.TEAL_LIGHT);
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Lending Management & Battery Monitoring");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(203, 213, 225));
        lblSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTag = new JLabel("Data Structures & Algorithms Implementation");
        lblTag.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblTag.setForeground(UITheme.TEAL_MUTED);
        lblTag.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(lblIcon);
        headerPanel.add(Box.createVerticalStrut(6));
        headerPanel.add(lblSubtitle);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(lblTag);

        add(headerPanel, BorderLayout.NORTH);

        // Form Card Panel
        JPanel cardWrapper = new JPanel(new GridBagLayout());
        cardWrapper.setOpaque(false);
        cardWrapper.setBorder(new EmptyBorder(20, 25, 25, 25));

        JPanel formCard = UITheme.createCardPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setPreferredSize(new Dimension(390, 340));

        JLabel lblFormTitle = new JLabel("Admin Sign In");
        lblFormTitle.setFont(UITheme.FONT_HEADER_MEDIUM);
        lblFormTitle.setForeground(UITheme.NAVY_DARK);
        lblFormTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblInstruction = new JLabel("Enter your administrator credentials to proceed.");
        lblInstruction.setFont(UITheme.FONT_SMALL);
        lblInstruction.setForeground(UITheme.TEXT_SECONDARY);
        lblInstruction.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Username Field
        JLabel lblUser = new JLabel("Username / Admin ID");
        lblUser.setFont(UITheme.FONT_BODY_BOLD);
        lblUser.setForeground(UITheme.TEXT_PRIMARY);
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUsername = UITheme.createTextField(20);
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtUsername.setText("admin"); // Demo default

        // Password Field
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(UITheme.FONT_BODY_BOLD);
        lblPass.setForeground(UITheme.TEXT_PRIMARY);
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPassword = UITheme.createPasswordField(20);
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtPassword.setText("admin123"); // Demo default

        // Status Label for Error/Success Messages
        lblStatus = new JLabel(" ");
        lblStatus.setFont(UITheme.FONT_SMALL);
        lblStatus.setForeground(UITheme.DANGER_RED);
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Sign In Button
        JButton btnLogin = UITheme.createPrimaryButton("Sign In to System");
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.addActionListener(e -> performLogin());

        // Quick demo filler button
        JButton btnFillDemo = UITheme.createSecondaryButton("Auto-Fill Demo Credentials (admin / admin123)");
        btnFillDemo.setFont(UITheme.FONT_SMALL);
        btnFillDemo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        btnFillDemo.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnFillDemo.addActionListener(e -> {
            txtUsername.setText("admin");
            txtPassword.setText("admin123");
            lblStatus.setText("Demo credentials loaded.");
            lblStatus.setForeground(UITheme.INFO_BLUE);
        });

        // Add Enter Key Listener on password field
        txtPassword.addActionListener(e -> performLogin());
        txtUsername.addActionListener(e -> performLogin());

        formCard.add(lblFormTitle);
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(lblInstruction);
        formCard.add(Box.createVerticalStrut(15));
        formCard.add(lblUser);
        formCard.add(Box.createVerticalStrut(5));
        formCard.add(txtUsername);
        formCard.add(Box.createVerticalStrut(12));
        formCard.add(lblPass);
        formCard.add(Box.createVerticalStrut(5));
        formCard.add(txtPassword);
        formCard.add(Box.createVerticalStrut(6));
        formCard.add(lblStatus);
        formCard.add(Box.createVerticalStrut(12));
        formCard.add(btnLogin);
        formCard.add(Box.createVerticalStrut(8));
        formCard.add(btnFillDemo);

        cardWrapper.add(formCard);
        add(cardWrapper, BorderLayout.CENTER);
    }

    private void performLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("Please enter both username and password.");
            lblStatus.setForeground(UITheme.DANGER_RED);
            return;
        }

        // Validate credentials (admin / admin123)
        if ("admin".equalsIgnoreCase(username) && "admin123".equals(password)) {
            lblStatus.setText("Authentication successful! Loading dashboard...");
            lblStatus.setForeground(UITheme.SUCCESS_GREEN);

            Timer timer = new Timer(350, evt -> {
                dispose();
                SwingUtilities.invokeLater(() -> {
                    Dashboard dashboard = new Dashboard(inventoryManager, borrowingManager, alertManager, fileStorage);
                    dashboard.setVisible(true);
                });
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            lblStatus.setText("Invalid credentials. Try 'admin' / 'admin123'.");
            lblStatus.setForeground(UITheme.DANGER_RED);
        }
    }
}
