package com.powerbank.ui;

import com.powerbank.algorithm.FleetSearchAlgorithm;
import com.powerbank.data.FileStorage;
import com.powerbank.model.Alert;
import com.powerbank.model.BorrowRecord;
import com.powerbank.model.BorrowRequest;
import com.powerbank.model.PowerBank;
import com.powerbank.model.Student;
import com.powerbank.service.AlertManager;
import com.powerbank.service.BorrowingManager;
import com.powerbank.service.InventoryManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Main Application Dashboard Frame.
 * Features sidebar navigation, clean dashboard cards, modern tables,
 * and comprehensive controls for all 12 modules.
 */
public class Dashboard extends JFrame {

    private final InventoryManager inventoryManager;
    private final BorrowingManager borrowingManager;
    private final AlertManager alertManager;
    private final FileStorage fileStorage;

    // Layout components
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private JLabel lblHeaderTitle;
    private JLabel lblHeaderSubtitle;
    private JLabel lblClock;
    private JLabel lblAlertBadge;

    // Sidebar navigation buttons
    private final List<JButton> navButtons = new ArrayList<>();

    // Dashboard View Components
    private JLabel lblStatTotal;
    private JLabel lblStatAvailable;
    private JLabel lblStatBorrowed;
    private JLabel lblStatLowBattery;
    private JLabel lblStatOverdue;
    private JTable tblDashAlerts;
    private DefaultTableModel modelDashAlerts;
    private JTable tblDashRecent;
    private DefaultTableModel modelDashRecent;
    private JLabel lblDsStatus;

    // Inventory View Components
    private JTable tblInventory;
    private DefaultTableModel modelInventory;
    private JTextField txtSearchInventory;
    private JComboBox<String> cmbFilterInventory;

    // Student View Components
    private JTable tblStudents;
    private DefaultTableModel modelStudents;
    private JTextField txtSearchStudents;

    // Borrow & Return View Components
    private JComboBox<String> cmbBorrowStudent;
    private JComboBox<String> cmbBorrowPowerBank;
    private JComboBox<String> cmbBorrowDuration;
    private JTable tblActiveBorrows;
    private DefaultTableModel modelActiveBorrows;
    private JTable tblWaitingQueue;
    private DefaultTableModel modelWaitingQueue;

    // Battery Monitor View Components
    private JTable tblBatteryMonitor;
    private DefaultTableModel modelBatteryMonitor;
    private JComboBox<String> cmbBatteryFilter;

    // Overdue Management View Components
    private JTable tblOverdue;
    private DefaultTableModel modelOverdue;

    // Alerts View Components
    private JTable tblAlerts;
    private DefaultTableModel modelAlerts;
    private JLabel lblPeakAlert;

    // History & Transactions View Components
    private JTable tblHistory;
    private DefaultTableModel modelHistory;
    private JTextField txtSearchHistory;
    private JComboBox<String> cmbHistoryStatus;

    // Reports View Components
    private JTextArea txtReportArea;

    public Dashboard(InventoryManager inventoryManager, BorrowingManager borrowingManager, AlertManager alertManager, FileStorage fileStorage) {
        this.inventoryManager = inventoryManager;
        this.borrowingManager = borrowingManager;
        this.alertManager = alertManager;
        this.fileStorage = fileStorage;

        initUI();
        startClockAndAutoRefreshTimer();
        refreshAllViews();
    }

    private void initUI() {
        setTitle("Smart Powerbank Lending System - Desktop Management Suite");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 820);
        setMinimumSize(new Dimension(1080, 700));
        setLocationRelativeTo(null);
        getContentPane().setBackground(UITheme.BG_LIGHT);
        setLayout(new BorderLayout());

        // Left Sidebar Navigation
        add(createSidebarPanel(), BorderLayout.WEST);

        // Right Main Container (Top Header + Center Cards)
        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.setBackground(UITheme.BG_LIGHT);

        rightContainer.add(createTopHeaderPanel(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(UITheme.BG_LIGHT);
        mainContentPanel.setBorder(new EmptyBorder(15, 20, 20, 20));

        // Add 9 module views to CardLayout
        mainContentPanel.add(createDashboardView(), "VIEW_DASHBOARD");
        mainContentPanel.add(createInventoryView(), "VIEW_INVENTORY");
        mainContentPanel.add(createBorrowReturnView(), "VIEW_BORROW_RETURN");
        mainContentPanel.add(createStudentView(), "VIEW_STUDENTS");
        mainContentPanel.add(createBatteryMonitorView(), "VIEW_BATTERY");
        mainContentPanel.add(createOverdueView(), "VIEW_OVERDUE");
        mainContentPanel.add(createAlertsQueueView(), "VIEW_ALERTS");
        mainContentPanel.add(createHistoryView(), "VIEW_HISTORY");
        mainContentPanel.add(createReportsView(), "VIEW_REPORTS");

        rightContainer.add(mainContentPanel, BorderLayout.CENTER);
        add(rightContainer, BorderLayout.CENTER);
    }

    // ==========================================
    // SIDEBAR NAVIGATION
    // ==========================================

    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UITheme.NAVY_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(new EmptyBorder(20, 12, 20, 12));

        // Brand Banner
        JLabel lblLogo = new JLabel("⚡ POWERBANK");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLogo.setForeground(UITheme.TEAL_LIGHT);
        lblLogo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Lending System DS");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(148, 163, 184));
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(lblLogo);
        sidebar.add(lblSub);
        sidebar.add(Box.createVerticalStrut(25));

        // Navigation Items
        String[][] navItems = {
                {"📊 Dashboard", "VIEW_DASHBOARD", "System Overview & Key Metrics"},
                {"🔋 Inventory", "VIEW_INVENTORY", "Powerbank Fleet & ArrayList/HashMap"},
                {"🔄 Borrow & Return", "VIEW_BORROW_RETURN", "Lending Desk & FIFO Queue"},
                {"👥 Students", "VIEW_STUDENTS", "Student Directory & Limit Verification"},
                {"⚡ Battery Monitor", "VIEW_BATTERY", "Real-Time Levels & Discharge Simulation"},
                {"⏳ Overdue Manager", "VIEW_OVERDUE", "Past Due Loans & Auto Fine Calculation"},
                {"🚨 Priority Alerts", "VIEW_ALERTS", "PriorityQueue Severity Management"},
                {"📜 History & Audits", "VIEW_HISTORY", "LinkedList Transaction Logs"},
                {"📈 Reports", "VIEW_REPORTS", "Analytics & CSV Export"}
        };

        for (int i = 0; i < navItems.length; i++) {
            String label = navItems[i][0];
            String viewKey = navItems[i][1];
            String subtitle = navItems[i][2];

            JButton btn = createNavButton(label, viewKey, subtitle, i == 0);
            navButtons.add(btn);
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(4));
        }

        sidebar.add(Box.createVerticalGlue());

        // Admin Profile Card & Logout
        JPanel profileCard = new JPanel(new BorderLayout(8, 0));
        profileCard.setBackground(new Color(15, 23, 42));
        profileCard.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(51, 65, 85), 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));
        profileCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JLabel lblAdmin = new JLabel("<html><b>Administrator</b><br><font color='#94A3B8' size='2'>System Operator</font></html>");
        lblAdmin.setForeground(Color.WHITE);

        JButton btnLogout = new JButton("🚪");
        btnLogout.setToolTipText("Sign Out");
        btnLogout.setBackground(new Color(30, 41, 59));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> logout());

        profileCard.add(lblAdmin, BorderLayout.CENTER);
        profileCard.add(btnLogout, BorderLayout.EAST);

        sidebar.add(profileCard);
        return sidebar;
    }

    private JButton createNavButton(String label, String viewKey, String subtitle, boolean active) {
        JButton btn = new JButton(label);
        btn.setFont(UITheme.FONT_BODY_BOLD);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 14, 8, 14));

        updateNavButtonStyle(btn, active);

        btn.addActionListener(e -> {
            for (JButton b : navButtons) {
                updateNavButtonStyle(b, b == btn);
            }
            cardLayout.show(mainContentPanel, viewKey);
            lblHeaderTitle.setText(label.substring(label.indexOf(" ") + 1));
            lblHeaderSubtitle.setText(subtitle);
            refreshAllViews();
        });

        return btn;
    }

    private void updateNavButtonStyle(JButton btn, boolean active) {
        if (active) {
            btn.setBackground(UITheme.TEAL_PRIMARY);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(UITheme.NAVY_SIDEBAR);
            btn.setForeground(new Color(203, 213, 225));
        }
    }

    // ==========================================
    // TOP HEADER
    // ==========================================

    private JPanel createTopHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_COLOR),
                new EmptyBorder(14, 24, 14, 24)
        ));

        // Title and Breadcrumbs
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        lblHeaderTitle = new JLabel("Dashboard Overview");
        lblHeaderTitle.setFont(UITheme.FONT_HEADER_LARGE);
        lblHeaderTitle.setForeground(UITheme.NAVY_DARK);

        lblHeaderSubtitle = new JLabel("System Overview & Key Metrics");
        lblHeaderSubtitle.setFont(UITheme.FONT_SMALL);
        lblHeaderSubtitle.setForeground(UITheme.TEXT_SECONDARY);

        titlePanel.add(lblHeaderTitle);
        titlePanel.add(Box.createVerticalStrut(2));
        titlePanel.add(lblHeaderSubtitle);

        // Right side: Clock, Quick Action Buttons, Alert Badge
        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightActions.setOpaque(false);

        lblClock = new JLabel();
        lblClock.setFont(UITheme.FONT_BODY_BOLD);
        lblClock.setForeground(UITheme.TEXT_SECONDARY);

        lblAlertBadge = UITheme.createBadge("0 ALERTS", UITheme.SUCCESS_GREEN, Color.WHITE);
        lblAlertBadge.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblAlertBadge.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                switchToView("VIEW_ALERTS", "Priority Alerts", "PriorityQueue Severity Management");
            }
        });

        JButton btnQuickRefresh = UITheme.createSecondaryButton("🔄 Refresh Data");
        btnQuickRefresh.addActionListener(e -> {
            refreshAllViews();
            JOptionPane.showMessageDialog(this, "All data structures and views synchronized successfully.", "Refreshed", JOptionPane.INFORMATION_MESSAGE);
        });

        rightActions.add(lblClock);
        rightActions.add(lblAlertBadge);
        rightActions.add(btnQuickRefresh);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(rightActions, BorderLayout.EAST);
        return header;
    }

    private void switchToView(String viewKey, String title, String subtitle) {
        for (int i = 0; i < navButtons.size(); i++) {
            JButton b = navButtons.get(i);
            boolean match = b.getText().contains(title.split(" ")[0]);
            updateNavButtonStyle(b, match);
        }
        cardLayout.show(mainContentPanel, viewKey);
        lblHeaderTitle.setText(title);
        lblHeaderSubtitle.setText(subtitle);
        refreshAllViews();
    }

    // ==========================================
    // 1. DASHBOARD VIEW
    // ==========================================

    private JPanel createDashboardView() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setOpaque(false);

        // Top 5 KPI Cards Grid
        JPanel kpiGrid = new JPanel(new GridLayout(1, 5, 12, 0));
        kpiGrid.setOpaque(false);

        lblStatTotal = new JLabel("0");
        lblStatAvailable = new JLabel("0");
        lblStatBorrowed = new JLabel("0");
        lblStatLowBattery = new JLabel("0");
        lblStatOverdue = new JLabel("0");

        JPanel card1 = UITheme.createStatCard("Total Units", "0", "Managed in ArrayList", UITheme.INFO_BLUE, "📦");
        lblStatTotal = (JLabel) ((JPanel) card1.getComponent(0)).getComponent(2);

        JPanel card2 = UITheme.createStatCard("Available", "0", "Ready to loan (>=20%)", UITheme.SUCCESS_GREEN, "✅");
        lblStatAvailable = (JLabel) ((JPanel) card2.getComponent(0)).getComponent(2);

        JPanel card3 = UITheme.createStatCard("Borrowed", "0", "Currently in circulation", UITheme.TEAL_PRIMARY, "🔄");
        lblStatBorrowed = (JLabel) ((JPanel) card3.getComponent(0)).getComponent(2);

        JPanel card4 = UITheme.createStatCard("Low Battery", "0", "Below 20% threshold", UITheme.WARNING_AMBER, "⚠️");
        lblStatLowBattery = (JLabel) ((JPanel) card4.getComponent(0)).getComponent(2);

        JPanel card5 = UITheme.createStatCard("Overdue", "0", "Past return deadline", UITheme.DANGER_RED, "🚨");
        lblStatOverdue = (JLabel) ((JPanel) card5.getComponent(0)).getComponent(2);

        kpiGrid.add(card1);
        kpiGrid.add(card2);
        kpiGrid.add(card3);
        kpiGrid.add(card4);
        kpiGrid.add(card5);

        panel.add(kpiGrid, BorderLayout.NORTH);

        // Center Split Panel: Left (PriorityQueue Alerts), Right (Recent Transactions LinkedList)
        JPanel centerSplit = new JPanel(new GridLayout(1, 2, 16, 0));
        centerSplit.setOpaque(false);

        // Left: PriorityQueue Alerts
        JPanel pnlAlerts = UITheme.createCardPanel();
        pnlAlerts.setLayout(new BorderLayout(0, 10));

        JPanel pnlAlertHeader = new JPanel(new BorderLayout());
        pnlAlertHeader.setOpaque(false);
        JLabel lblAlertTitle = new JLabel("Priority Alerts (PriorityQueue)");
        lblAlertTitle.setFont(UITheme.FONT_TITLE);
        lblAlertTitle.setForeground(UITheme.NAVY_DARK);

        JButton btnResolveTop = UITheme.createSecondaryButton("Resolve Top Alert");
        btnResolveTop.setFont(UITheme.FONT_SMALL);
        btnResolveTop.addActionListener(e -> resolveTopAlertAction());

        pnlAlertHeader.add(lblAlertTitle, BorderLayout.WEST);
        pnlAlertHeader.add(btnResolveTop, BorderLayout.EAST);

        String[] alertCols = {"Priority", "Type", "Target", "Message"};
        modelDashAlerts = new DefaultTableModel(alertCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblDashAlerts = new JTable(modelDashAlerts);
        UITheme.styleTable(tblDashAlerts);
        tblDashAlerts.getColumnModel().getColumn(0).setPreferredWidth(70);
        tblDashAlerts.getColumnModel().getColumn(1).setPreferredWidth(90);
        tblDashAlerts.getColumnModel().getColumn(2).setPreferredWidth(80);
        tblDashAlerts.getColumnModel().getColumn(3).setPreferredWidth(200);

        pnlAlerts.add(pnlAlertHeader, BorderLayout.NORTH);
        pnlAlerts.add(new JScrollPane(tblDashAlerts), BorderLayout.CENTER);

        // Right: Recent Activity from LinkedList
        JPanel pnlRecent = UITheme.createCardPanel();
        pnlRecent.setLayout(new BorderLayout(0, 10));

        JPanel pnlRecentHeader = new JPanel(new BorderLayout());
        pnlRecentHeader.setOpaque(false);
        JLabel lblRecentTitle = new JLabel("Recent Audit Trail (LinkedList)");
        lblRecentTitle.setFont(UITheme.FONT_TITLE);
        lblRecentTitle.setForeground(UITheme.NAVY_DARK);

        JButton btnViewAllHistory = UITheme.createSecondaryButton("Full History ➔");
        btnViewAllHistory.setFont(UITheme.FONT_SMALL);
        btnViewAllHistory.addActionListener(e -> switchToView("VIEW_HISTORY", "History & Audits", "LinkedList Transaction Logs"));

        pnlRecentHeader.add(lblRecentTitle, BorderLayout.WEST);
        pnlRecentHeader.add(btnViewAllHistory, BorderLayout.EAST);

        String[] recentCols = {"Record ID", "Student", "PowerBank", "Due / Status"};
        modelDashRecent = new DefaultTableModel(recentCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblDashRecent = new JTable(modelDashRecent);
        UITheme.styleTable(tblDashRecent);

        pnlRecent.add(pnlRecentHeader, BorderLayout.NORTH);
        pnlRecent.add(new JScrollPane(tblDashRecent), BorderLayout.CENTER);

        centerSplit.add(pnlAlerts);
        centerSplit.add(pnlRecent);

        panel.add(centerSplit, BorderLayout.CENTER);

        // Bottom: Data Structures Live Status Panel
        JPanel dsBanner = new JPanel(new BorderLayout());
        dsBanner.setBackground(UITheme.NAVY_DARK);
        dsBanner.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UITheme.NAVY_CARD, 1, true),
                new EmptyBorder(10, 16, 10, 16)
        ));

        lblDsStatus = new JLabel("⚡ Data Structures Active: ArrayList: 0 | HashMap: 0 keys | Queue: 0 waiting | LinkedList: 0 logs | PriorityQueue: 0 alerts");
        lblDsStatus.setFont(new Font("Consolas", Font.BOLD, 12));
        lblDsStatus.setForeground(UITheme.TEAL_LIGHT);

        dsBanner.add(lblDsStatus, BorderLayout.WEST);
        panel.add(dsBanner, BorderLayout.SOUTH);

        return panel;
    }

    // ==========================================
    // 2. INVENTORY VIEW (ArrayList + HashMap)
    // ==========================================

    private JPanel createInventoryView() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        // Filter & Action Toolbar
        JPanel toolbar = UITheme.createCardPanel();
        toolbar.setLayout(new BorderLayout(10, 0));

        JPanel leftFilters = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftFilters.setOpaque(false);

        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(UITheme.FONT_BODY_BOLD);

        txtSearchInventory = UITheme.createTextField(14);
        txtSearchInventory.addActionListener(e -> filterInventoryTable());

        JLabel lblStatus = new JLabel("Status Filter:");
        lblStatus.setFont(UITheme.FONT_BODY_BOLD);

        cmbFilterInventory = new JComboBox<>(new String[]{"ALL", "AVAILABLE", "BORROWED", "LOW_BATTERY", "CHARGING", "MAINTENANCE"});
        cmbFilterInventory.setFont(UITheme.FONT_BODY);
        cmbFilterInventory.addActionListener(e -> filterInventoryTable());

        JButton btnSearch = UITheme.createSecondaryButton("Filter");
        btnSearch.addActionListener(e -> filterInventoryTable());

        JButton btnReset = UITheme.createSecondaryButton("Reset");
        btnReset.addActionListener(e -> {
            txtSearchInventory.setText("");
            cmbFilterInventory.setSelectedIndex(0);
            filterInventoryTable();
        });

        leftFilters.add(lblSearch);
        leftFilters.add(txtSearchInventory);
        leftFilters.add(lblStatus);
        leftFilters.add(cmbFilterInventory);
        leftFilters.add(btnSearch);
        leftFilters.add(btnReset);

        JPanel rightActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightActions.setOpaque(false);

        JButton btnBinarySearch = UITheme.createSecondaryButton("🔍 Binary Search O(log N)");
        btnBinarySearch.addActionListener(e -> showBinarySearchDialog());

        JButton btnAdd = UITheme.createPrimaryButton("+ Add PowerBank");
        btnAdd.addActionListener(e -> showAddPowerBankDialog());

        JButton btnEdit = UITheme.createSecondaryButton("✏️ Edit Selected");
        btnEdit.addActionListener(e -> showEditPowerBankDialog());

        JButton btnDelete = UITheme.createDangerButton("🗑️ Delete Selected");
        btnDelete.addActionListener(e -> deleteSelectedPowerBank());

        rightActions.add(btnBinarySearch);
        rightActions.add(btnAdd);
        rightActions.add(btnEdit);
        rightActions.add(btnDelete);

        toolbar.add(leftFilters, BorderLayout.WEST);
        toolbar.add(rightActions, BorderLayout.EAST);
        panel.add(toolbar, BorderLayout.NORTH);

        // Table Card
        JPanel tableCard = UITheme.createCardPanel();
        tableCard.setLayout(new BorderLayout());

        String[] cols = {"Unit ID", "Model Name", "Capacity (mAh)", "Battery Level", "Status", "Slot Location", "Borrow Count"};
        modelInventory = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblInventory = new JTable(modelInventory);
        UITheme.styleTable(tblInventory);

        // Battery Level column gets visual BatteryBarRenderer!
        tblInventory.getColumnModel().getColumn(3).setCellRenderer(new BatteryBarRenderer());

        tableCard.add(new JScrollPane(tblInventory), BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.CENTER);

        return panel;
    }

    // ==========================================
    // 3. BORROW & RETURN VIEW (Queue + Overdue)
    // ==========================================

    private JPanel createBorrowReturnView() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 16, 0));
        panel.setOpaque(false);

        // LEFT COLUMN: Borrow PowerBank Form + FIFO Queue
        JPanel leftPanel = new JPanel(new BorderLayout(0, 14));
        leftPanel.setOpaque(false);

        // Borrow Form Card
        JPanel borrowCard = UITheme.createCardPanel();
        borrowCard.setLayout(new BoxLayout(borrowCard, BoxLayout.Y_AXIS));

        JLabel lblBorrowTitle = new JLabel("Lend PowerBank");
        lblBorrowTitle.setFont(UITheme.FONT_HEADER_MEDIUM);
        lblBorrowTitle.setForeground(UITheme.NAVY_DARK);

        JLabel lblBorrowSub = new JLabel("Issue an available unit (>=20% battery) to a registered student.");
        lblBorrowSub.setFont(UITheme.FONT_SMALL);
        lblBorrowSub.setForeground(UITheme.TEXT_SECONDARY);

        JLabel lblSelectStudent = new JLabel("Select Student:");
        lblSelectStudent.setFont(UITheme.FONT_BODY_BOLD);
        cmbBorrowStudent = new JComboBox<>();
        cmbBorrowStudent.setFont(UITheme.FONT_BODY);
        cmbBorrowStudent.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JLabel lblSelectPB = new JLabel("Select Available PowerBank:");
        lblSelectPB.setFont(UITheme.FONT_BODY_BOLD);
        cmbBorrowPowerBank = new JComboBox<>();
        cmbBorrowPowerBank.setFont(UITheme.FONT_BODY);
        cmbBorrowPowerBank.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JLabel lblDuration = new JLabel("Loan Duration:");
        lblDuration.setFont(UITheme.FONT_BODY_BOLD);
        cmbBorrowDuration = new JComboBox<>(new String[]{"1 Hour", "2 Hours", "4 Hours", "8 Hours", "24 Hours"});
        cmbBorrowDuration.setFont(UITheme.FONT_BODY);
        cmbBorrowDuration.setSelectedIndex(2); // 4 hours default
        cmbBorrowDuration.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        JButton btnIssue = UITheme.createPrimaryButton("Confirm & Issue PowerBank");
        btnIssue.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnIssue.addActionListener(e -> performBorrowAction());

        JButton btnAddToQueue = UITheme.createSecondaryButton("➕ Add Student to FIFO Waiting Queue");
        btnAddToQueue.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btnAddToQueue.addActionListener(e -> showAddToQueueDialog());

        borrowCard.add(lblBorrowTitle);
        borrowCard.add(Box.createVerticalStrut(3));
        borrowCard.add(lblBorrowSub);
        borrowCard.add(Box.createVerticalStrut(14));
        borrowCard.add(lblSelectStudent);
        borrowCard.add(Box.createVerticalStrut(4));
        borrowCard.add(cmbBorrowStudent);
        borrowCard.add(Box.createVerticalStrut(10));
        borrowCard.add(lblSelectPB);
        borrowCard.add(Box.createVerticalStrut(4));
        borrowCard.add(cmbBorrowPowerBank);
        borrowCard.add(Box.createVerticalStrut(10));
        borrowCard.add(lblDuration);
        borrowCard.add(Box.createVerticalStrut(4));
        borrowCard.add(cmbBorrowDuration);
        borrowCard.add(Box.createVerticalStrut(16));
        borrowCard.add(btnIssue);
        borrowCard.add(Box.createVerticalStrut(8));
        borrowCard.add(btnAddToQueue);

        leftPanel.add(borrowCard, BorderLayout.NORTH);

        // FIFO Waiting Queue Card
        JPanel queueCard = UITheme.createCardPanel();
        queueCard.setLayout(new BorderLayout(0, 10));

        JPanel queueHeader = new JPanel(new BorderLayout());
        queueHeader.setOpaque(false);
        JLabel lblQueueTitle = new JLabel("FIFO Waiting Queue (Queue<BorrowRequest>)");
        lblQueueTitle.setFont(UITheme.FONT_TITLE);
        lblQueueTitle.setForeground(UITheme.NAVY_DARK);

        JButton btnDispatch = UITheme.createPrimaryButton("Dispatch Next (FIFO)");
        btnDispatch.setFont(UITheme.FONT_SMALL);
        btnDispatch.addActionListener(e -> dispatchNextFromQueueAction());

        queueHeader.add(lblQueueTitle, BorderLayout.WEST);
        queueHeader.add(btnDispatch, BorderLayout.EAST);

        String[] queueCols = {"Queue #", "Request ID", "Student Name", "Capacity", "Time"};
        modelWaitingQueue = new DefaultTableModel(queueCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblWaitingQueue = new JTable(modelWaitingQueue);
        UITheme.styleTable(tblWaitingQueue);

        queueCard.add(queueHeader, BorderLayout.NORTH);
        queueCard.add(new JScrollPane(tblWaitingQueue), BorderLayout.CENTER);

        leftPanel.add(queueCard, BorderLayout.CENTER);
        panel.add(leftPanel);

        // RIGHT COLUMN: Return PowerBank & Active Borrows
        JPanel rightPanel = UITheme.createCardPanel();
        rightPanel.setLayout(new BorderLayout(0, 12));

        JPanel returnHeader = new JPanel(new BorderLayout());
        returnHeader.setOpaque(false);

        JPanel returnText = new JPanel();
        returnText.setLayout(new BoxLayout(returnText, BoxLayout.Y_AXIS));
        returnText.setOpaque(false);

        JLabel lblReturnTitle = new JLabel("Active Loans & Return Desk");
        lblReturnTitle.setFont(UITheme.FONT_HEADER_MEDIUM);
        lblReturnTitle.setForeground(UITheme.NAVY_DARK);

        JLabel lblReturnSub = new JLabel("Select an active loan below to record return, calculate fine, and recharge.");
        lblReturnSub.setFont(UITheme.FONT_SMALL);
        lblReturnSub.setForeground(UITheme.TEXT_SECONDARY);

        returnText.add(lblReturnTitle);
        returnText.add(lblReturnSub);

        JButton btnReturnSelected = UITheme.createPrimaryButton("Process Return Selected");
        btnReturnSelected.addActionListener(e -> performReturnAction());

        returnHeader.add(returnText, BorderLayout.WEST);
        returnHeader.add(btnReturnSelected, BorderLayout.EAST);

        String[] borrowCols = {"Record ID", "Student", "PowerBank", "Borrow Date", "Return Due", "Status"};
        modelActiveBorrows = new DefaultTableModel(borrowCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblActiveBorrows = new JTable(modelActiveBorrows);
        UITheme.styleTable(tblActiveBorrows);

        rightPanel.add(returnHeader, BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(tblActiveBorrows), BorderLayout.CENTER);

        panel.add(rightPanel);
        return panel;
    }

    // ==========================================
    // 4. STUDENT MANAGEMENT VIEW
    // ==========================================

    private JPanel createStudentView() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        // Toolbar
        JPanel toolbar = UITheme.createCardPanel();
        toolbar.setLayout(new BorderLayout(10, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JLabel lblSearch = new JLabel("Search Student:");
        lblSearch.setFont(UITheme.FONT_BODY_BOLD);

        txtSearchStudents = UITheme.createTextField(16);
        txtSearchStudents.addActionListener(e -> filterStudentsTable());

        JButton btnSearch = UITheme.createSecondaryButton("Search");
        btnSearch.addActionListener(e -> filterStudentsTable());

        JButton btnReset = UITheme.createSecondaryButton("Show All");
        btnReset.addActionListener(e -> {
            txtSearchStudents.setText("");
            filterStudentsTable();
        });

        left.add(lblSearch);
        left.add(txtSearchStudents);
        left.add(btnSearch);
        left.add(btnReset);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JButton btnAdd = UITheme.createPrimaryButton("+ Register Student");
        btnAdd.addActionListener(e -> showAddStudentDialog());

        JButton btnEdit = UITheme.createSecondaryButton("✏️ Edit");
        btnEdit.addActionListener(e -> showEditStudentDialog());

        JButton btnDelete = UITheme.createDangerButton("🗑️ Delete");
        btnDelete.addActionListener(e -> deleteSelectedStudent());

        right.add(btnAdd);
        right.add(btnEdit);
        right.add(btnDelete);

        toolbar.add(left, BorderLayout.WEST);
        toolbar.add(right, BorderLayout.EAST);
        panel.add(toolbar, BorderLayout.NORTH);

        // Table Card
        JPanel tableCard = UITheme.createCardPanel();
        tableCard.setLayout(new BorderLayout());

        String[] cols = {"Student ID", "Full Name", "Email Address", "Department", "Phone", "Active Borrows", "Max Limit"};
        modelStudents = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblStudents = new JTable(modelStudents);
        UITheme.styleTable(tblStudents);

        tableCard.add(new JScrollPane(tblStudents), BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.CENTER);

        return panel;
    }

    // ==========================================
    // 5. BATTERY MONITORING VIEW
    // ==========================================

    private JPanel createBatteryMonitorView() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        JPanel toolbar = UITheme.createCardPanel();
        toolbar.setLayout(new BorderLayout(10, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JLabel lblFilter = new JLabel("Battery Health Filter:");
        lblFilter.setFont(UITheme.FONT_BODY_BOLD);

        cmbBatteryFilter = new JComboBox<>(new String[]{"ALL UNITS", "HEALTHY (>=50%)", "WARNING (20-49%)", "CRITICAL LOW (<20%)"});
        cmbBatteryFilter.setFont(UITheme.FONT_BODY);
        cmbBatteryFilter.addActionListener(e -> filterBatteryTable());

        left.add(lblFilter);
        left.add(cmbBatteryFilter);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JButton btnDrain = UITheme.createDangerButton("⚡ Simulate Drain (-15%)");
        btnDrain.addActionListener(e -> simulateBatteryDrain());

        JButton btnRecharge = UITheme.createPrimaryButton("🔋 Quick Recharge to 100%");
        btnRecharge.addActionListener(e -> simulateBatteryRecharge());

        JButton btnScan = UITheme.createSecondaryButton("🔍 Run Diagnostic Scan");
        btnScan.addActionListener(e -> {
            alertManager.scanAndGenerateAlerts();
            refreshAllViews();
            JOptionPane.showMessageDialog(this, "Diagnostic scan complete. PriorityQueue updated.", "Diagnostics", JOptionPane.INFORMATION_MESSAGE);
        });

        right.add(btnDrain);
        right.add(btnRecharge);
        right.add(btnScan);

        toolbar.add(left, BorderLayout.WEST);
        toolbar.add(right, BorderLayout.EAST);
        panel.add(toolbar, BorderLayout.NORTH);

        // Table Card
        JPanel tableCard = UITheme.createCardPanel();
        tableCard.setLayout(new BorderLayout());

        String[] cols = {"Unit ID", "Model", "Slot Location", "Battery Level", "Health Status", "Available for Lending?"};
        modelBatteryMonitor = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblBatteryMonitor = new JTable(modelBatteryMonitor);
        UITheme.styleTable(tblBatteryMonitor);
        tblBatteryMonitor.getColumnModel().getColumn(3).setCellRenderer(new BatteryBarRenderer());

        tableCard.add(new JScrollPane(tblBatteryMonitor), BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.CENTER);

        return panel;
    }

    // ==========================================
    // 6. OVERDUE MANAGEMENT VIEW
    // ==========================================

    private JPanel createOverdueView() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        JPanel headerCard = UITheme.createCardPanel();
        headerCard.setLayout(new BorderLayout());

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);

        JLabel lblTitle = new JLabel("Overdue Lending Records");
        lblTitle.setFont(UITheme.FONT_HEADER_MEDIUM);
        lblTitle.setForeground(UITheme.DANGER_RED);

        JLabel lblSub = new JLabel("Loans exceeding agreed return deadline. Automated fee calculation at $2.00/hour.");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.TEXT_SECONDARY);

        text.add(lblTitle);
        text.add(lblSub);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        JButton btnReturnOverdue = UITheme.createPrimaryButton("Return Selected Overdue Unit");
        btnReturnOverdue.addActionListener(e -> performReturnFromOverdueTable());

        JButton btnAlertBorrower = UITheme.createSecondaryButton("📢 Send Alert Notice");
        btnAlertBorrower.addActionListener(e -> sendOverdueAlertNotice());

        actions.add(btnAlertBorrower);
        actions.add(btnReturnOverdue);

        headerCard.add(text, BorderLayout.WEST);
        headerCard.add(actions, BorderLayout.EAST);
        panel.add(headerCard, BorderLayout.NORTH);

        // Table Card
        JPanel tableCard = UITheme.createCardPanel();
        tableCard.setLayout(new BorderLayout());

        String[] cols = {"Record ID", "Student ID", "Student Name", "PowerBank ID", "Due Deadline", "Overdue Duration", "Calculated Fine ($)"};
        modelOverdue = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblOverdue = new JTable(modelOverdue);
        UITheme.styleTable(tblOverdue);

        tableCard.add(new JScrollPane(tblOverdue), BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.CENTER);

        return panel;
    }

    // ==========================================
    // 7. PRIORITY ALERTS VIEW (PriorityQueue)
    // ==========================================

    private JPanel createAlertsQueueView() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        // Top Banner highlighting the PEAK element of the PriorityQueue
        JPanel peakCard = new JPanel(new BorderLayout());
        peakCard.setBackground(new Color(254, 242, 242));
        peakCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, UITheme.DANGER_RED),
                BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(254, 202, 202), 1, true),
                        new EmptyBorder(14, 18, 14, 18)
                )
        ));

        JPanel peakText = new JPanel();
        peakText.setLayout(new BoxLayout(peakText, BoxLayout.Y_AXIS));
        peakText.setOpaque(false);

        JLabel lblPeakTitle = new JLabel("HIGHEST PRIORITY ALERT (PriorityQueue.peek())");
        lblPeakTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPeakTitle.setForeground(UITheme.DANGER_RED);

        lblPeakAlert = new JLabel("No active critical alerts in queue.");
        lblPeakAlert.setFont(UITheme.FONT_TITLE);
        lblPeakAlert.setForeground(UITheme.NAVY_DARK);

        peakText.add(lblPeakTitle);
        peakText.add(Box.createVerticalStrut(4));
        peakText.add(lblPeakAlert);

        JPanel peakActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        peakActions.setOpaque(false);

        JButton btnPoll = UITheme.createDangerButton("Process & Poll Top Alert");
        btnPoll.addActionListener(e -> resolveTopAlertAction());

        JButton btnResolveAll = UITheme.createSecondaryButton("Acknowledge All");
        btnResolveAll.addActionListener(e -> {
            alertManager.resolveAllAlerts();
            refreshAllViews();
            JOptionPane.showMessageDialog(this, "All alerts marked as acknowledged.", "Alerts Resolved", JOptionPane.INFORMATION_MESSAGE);
        });

        peakActions.add(btnPoll);
        peakActions.add(btnResolveAll);

        peakCard.add(peakText, BorderLayout.CENTER);
        peakCard.add(peakActions, BorderLayout.EAST);
        panel.add(peakCard, BorderLayout.NORTH);

        // Table Card
        JPanel tableCard = UITheme.createCardPanel();
        tableCard.setLayout(new BorderLayout(0, 8));

        JLabel lblTblTitle = new JLabel("Priority-Ordered Alert Queue (Severity 1: Overdue/<10% > Severity 2: 10-20% > Severity 3: Info)");
        lblTblTitle.setFont(UITheme.FONT_TITLE);
        lblTblTitle.setForeground(UITheme.NAVY_DARK);

        String[] cols = {"Severity Rank", "Alert ID", "Type", "Target", "Timestamp", "Alert Description"};
        modelAlerts = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblAlerts = new JTable(modelAlerts);
        UITheme.styleTable(tblAlerts);
        tblAlerts.getColumnModel().getColumn(0).setPreferredWidth(100);
        tblAlerts.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblAlerts.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblAlerts.getColumnModel().getColumn(3).setPreferredWidth(90);
        tblAlerts.getColumnModel().getColumn(4).setPreferredWidth(140);
        tblAlerts.getColumnModel().getColumn(5).setPreferredWidth(350);

        tableCard.add(lblTblTitle, BorderLayout.NORTH);
        tableCard.add(new JScrollPane(tblAlerts), BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.CENTER);

        return panel;
    }

    // ==========================================
    // 8. HISTORY & TRANSACTIONS VIEW (LinkedList)
    // ==========================================

    private JPanel createHistoryView() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        JPanel toolbar = UITheme.createCardPanel();
        toolbar.setLayout(new BorderLayout(10, 0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);

        JLabel lblSearch = new JLabel("Search History:");
        lblSearch.setFont(UITheme.FONT_BODY_BOLD);

        txtSearchHistory = UITheme.createTextField(14);
        txtSearchHistory.addActionListener(e -> filterHistoryTable());

        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setFont(UITheme.FONT_BODY_BOLD);

        cmbHistoryStatus = new JComboBox<>(new String[]{"ALL", "ACTIVE", "RETURNED", "OVERDUE"});
        cmbHistoryStatus.setFont(UITheme.FONT_BODY);
        cmbHistoryStatus.addActionListener(e -> filterHistoryTable());

        JButton btnFilter = UITheme.createSecondaryButton("Filter");
        btnFilter.addActionListener(e -> filterHistoryTable());

        left.add(lblSearch);
        left.add(txtSearchHistory);
        left.add(lblStatus);
        left.add(cmbHistoryStatus);
        left.add(btnFilter);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        JButton btnExport = UITheme.createPrimaryButton("💾 Export Audit Log (CSV)");
        btnExport.addActionListener(e -> exportTransactionAuditLog());

        right.add(btnExport);

        toolbar.add(left, BorderLayout.WEST);
        toolbar.add(right, BorderLayout.EAST);
        panel.add(toolbar, BorderLayout.NORTH);

        // Table Card
        JPanel tableCard = UITheme.createCardPanel();
        tableCard.setLayout(new BorderLayout());

        String[] cols = {"Record ID", "Student ID", "Student Name", "PowerBank ID", "Borrow Time", "Due Time", "Return Time", "Status", "Fine ($)"};
        modelHistory = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tblHistory = new JTable(modelHistory);
        UITheme.styleTable(tblHistory);

        tableCard.add(new JScrollPane(tblHistory), BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.CENTER);

        return panel;
    }

    // ==========================================
    // 9. REPORTS & ANALYTICS VIEW
    // ==========================================

    private JPanel createReportsView() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setOpaque(false);

        JPanel topCard = UITheme.createCardPanel();
        topCard.setLayout(new BorderLayout());

        JPanel text = new JPanel();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setOpaque(false);

        JLabel lblTitle = new JLabel("System Analytics & Operations Summary");
        lblTitle.setFont(UITheme.FONT_HEADER_MEDIUM);
        lblTitle.setForeground(UITheme.NAVY_DARK);

        JLabel lblSub = new JLabel("Real-time telemetry, fleet utilization, and comprehensive audit report.");
        lblSub.setFont(UITheme.FONT_SMALL);
        lblSub.setForeground(UITheme.TEXT_SECONDARY);

        text.add(lblTitle);
        text.add(lblSub);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        JButton btnGenerate = UITheme.createSecondaryButton("🔄 Regenerate Report");
        btnGenerate.addActionListener(e -> generateReportContent());

        JButton btnExportTxt = UITheme.createPrimaryButton("💾 Export Full Report (.txt)");
        btnExportTxt.addActionListener(e -> exportReportToFile());

        actions.add(btnGenerate);
        actions.add(btnExportTxt);

        topCard.add(text, BorderLayout.WEST);
        topCard.add(actions, BorderLayout.EAST);
        panel.add(topCard, BorderLayout.NORTH);

        // Report Text Card
        JPanel reportCard = UITheme.createCardPanel();
        reportCard.setLayout(new BorderLayout());

        txtReportArea = new JTextArea();
        txtReportArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtReportArea.setEditable(false);
        txtReportArea.setBackground(new Color(250, 250, 250));
        txtReportArea.setBorder(new EmptyBorder(16, 16, 16, 16));

        reportCard.add(new JScrollPane(txtReportArea), BorderLayout.CENTER);
        panel.add(reportCard, BorderLayout.CENTER);

        return panel;
    }

    // ==========================================
    // DATA REFRESHING & SYNCHRONIZATION
    // ==========================================

    public void refreshAllViews() {
        // 1. Refresh Overdues and Alerts
        borrowingManager.refreshOverdueStatus();
        alertManager.scanAndGenerateAlerts();

        // 2. Refresh Dashboard KPIs
        int total = inventoryManager.getTotalCount();
        int available = inventoryManager.getAvailableCount();
        int borrowed = inventoryManager.getBorrowedCount();
        int lowBatt = inventoryManager.getLowBatteryCount();
        int overdue = borrowingManager.getOverdueCount();

        lblStatTotal.setText(String.valueOf(total));
        lblStatAvailable.setText(String.valueOf(available));
        lblStatBorrowed.setText(String.valueOf(borrowed));
        lblStatLowBattery.setText(String.valueOf(lowBatt));
        lblStatOverdue.setText(String.valueOf(overdue));

        // Alert Badge
        int unresAlerts = alertManager.getUnresolvedCount();
        if (unresAlerts > 0) {
            lblAlertBadge.setText(unresAlerts + " ALERTS");
            lblAlertBadge.setBackground(UITheme.DANGER_RED);
        } else {
            lblAlertBadge.setText("0 ALERTS");
            lblAlertBadge.setBackground(UITheme.SUCCESS_GREEN);
        }

        // 3. Refresh Dashboard Mini-tables
        refreshDashboardMiniTables();

        // 4. Refresh Inventory Table
        filterInventoryTable();

        // 5. Refresh Borrow/Return combos and tables
        refreshBorrowReturnPanel();

        // 6. Refresh Students Table
        filterStudentsTable();

        // 7. Refresh Battery Table
        filterBatteryTable();

        // 8. Refresh Overdue Table
        refreshOverdueTable();

        // 9. Refresh Alerts Table & Peak Banner
        refreshAlertsTable();

        // 10. Refresh History Table
        filterHistoryTable();

        // 11. Refresh Report
        generateReportContent();

        // 12. Update Data Structure Status Banner
        lblDsStatus.setText(String.format("⚡ Data Structures Active: ArrayList: %d powerbanks | HashMap: %d keys | Queue: %d waiting | LinkedList: %d history logs | PriorityQueue: %d alerts",
                inventoryManager.getTotalCount(),
                inventoryManager.getTotalCount(),
                borrowingManager.getQueueSize(),
                borrowingManager.getTransactionHistory().size(),
                alertManager.getUnresolvedCount()));
    }

    private void refreshDashboardMiniTables() {
        // Alerts
        modelDashAlerts.setRowCount(0);
        List<Alert> alerts = alertManager.getActiveAlertsSorted();
        int limitAlerts = Math.min(8, alerts.size());
        for (int i = 0; i < limitAlerts; i++) {
            Alert a = alerts.get(i);
            modelDashAlerts.addRow(new Object[]{
                    "Rank " + a.getSeverity(),
                    a.getType(),
                    a.getTargetId(),
                    a.getTitle()
            });
        }

        // Recent Activity
        modelDashRecent.setRowCount(0);
        LinkedList<BorrowRecord> history = borrowingManager.getTransactionHistory();
        int limitHistory = Math.min(8, history.size());
        for (int i = 0; i < limitHistory; i++) {
            BorrowRecord r = history.get(i);
            modelDashRecent.addRow(new Object[]{
                    r.getRecordId(),
                    r.getStudentName(),
                    r.getPowerbankId(),
                    r.getStatus()
            });
        }
    }

    private void filterInventoryTable() {
        if (modelInventory == null) return;
        modelInventory.setRowCount(0);

        String query = txtSearchInventory != null ? txtSearchInventory.getText() : "";
        String status = cmbFilterInventory != null ? (String) cmbFilterInventory.getSelectedItem() : "ALL";

        ArrayList<PowerBank> list = inventoryManager.searchAndFilter(query, status);
        for (PowerBank pb : list) {
            modelInventory.addRow(new Object[]{
                    pb.getId(),
                    pb.getModelName(),
                    pb.getCapacityMah() + " mAh",
                    pb.getBatteryLevel(), // rendered via BatteryBarRenderer
                    pb.getStatus(),
                    pb.getSlotLocation(),
                    pb.getTotalBorrowCount()
            });
        }
    }

    private void refreshBorrowReturnPanel() {
        // 1. Populate Students Dropdown
        if (cmbBorrowStudent != null) {
            cmbBorrowStudent.removeAllItems();
            ArrayList<Student> students = borrowingManager.getAllStudents();
            for (Student s : students) {
                if (s.canBorrow()) {
                    cmbBorrowStudent.addItem(s.getId() + " - " + s.getName() + " (" + s.getDepartment() + ")");
                }
            }
        }

        // 2. Populate PowerBanks Dropdown (Only Available & >=20% Battery)
        if (cmbBorrowPowerBank != null) {
            cmbBorrowPowerBank.removeAllItems();
            ArrayList<PowerBank> availablePbs = inventoryManager.getAvailablePowerBanks();
            for (PowerBank pb : availablePbs) {
                cmbBorrowPowerBank.addItem(pb.getId() + " - " + pb.getModelName() + " (" + pb.getBatteryLevel() + "% | " + pb.getCapacityMah() + "mAh)");
            }
        }

        // 3. Refresh Active Borrows Table
        if (modelActiveBorrows != null) {
            modelActiveBorrows.setRowCount(0);
            ArrayList<BorrowRecord> active = borrowingManager.getActiveBorrows();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMM dd, HH:mm");
            for (BorrowRecord r : active) {
                String borrowStr = r.getBorrowDate() != null ? r.getBorrowDate().format(dtf) : "-";
                String dueStr = r.getDueDate() != null ? r.getDueDate().format(dtf) : "-";
                modelActiveBorrows.addRow(new Object[]{
                        r.getRecordId(),
                        r.getStudentName() + " (" + r.getStudentId() + ")",
                        r.getPowerbankId(),
                        borrowStr,
                        dueStr,
                        r.getStatus()
                });
            }
        }

        // 4. Refresh Waiting Queue Table
        if (modelWaitingQueue != null) {
            modelWaitingQueue.setRowCount(0);
            List<BorrowRequest> queue = borrowingManager.getQueueSnapshot();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
            int pos = 1;
            for (BorrowRequest req : queue) {
                String cap = req.getPreferredCapacityMah() > 0 ? req.getPreferredCapacityMah() + "mAh" : "Any";
                modelWaitingQueue.addRow(new Object[]{
                        "#" + (pos++),
                        req.getRequestId(),
                        req.getStudentName(),
                        cap,
                        req.getRequestTime() != null ? req.getRequestTime().format(dtf) : "-"
                });
            }
        }
    }

    private void filterStudentsTable() {
        if (modelStudents == null) return;
        modelStudents.setRowCount(0);

        String query = txtSearchStudents != null ? txtSearchStudents.getText().trim().toLowerCase() : "";
        ArrayList<Student> list = borrowingManager.getAllStudents();

        for (Student s : list) {
            boolean matches = query.isEmpty()
                    || s.getId().toLowerCase().contains(query)
                    || s.getName().toLowerCase().contains(query)
                    || s.getDepartment().toLowerCase().contains(query);

            if (matches) {
                modelStudents.addRow(new Object[]{
                        s.getId(),
                        s.getName(),
                        s.getEmail(),
                        s.getDepartment(),
                        s.getPhone(),
                        s.getActiveBorrowCount(),
                        s.getMaxLimit()
                });
            }
        }
    }

    private void filterBatteryTable() {
        if (modelBatteryMonitor == null) return;
        modelBatteryMonitor.setRowCount(0);

        String filter = cmbBatteryFilter != null ? (String) cmbBatteryFilter.getSelectedItem() : "ALL UNITS";
        ArrayList<PowerBank> list = inventoryManager.getAllPowerBanks();

        for (PowerBank pb : list) {
            boolean include = false;
            if ("ALL UNITS".equals(filter)) {
                include = true;
            } else if ("HEALTHY (>=50%)".equals(filter)) {
                include = pb.getBatteryLevel() >= 50;
            } else if ("WARNING (20-49%)".equals(filter)) {
                include = pb.getBatteryLevel() >= 20 && pb.getBatteryLevel() < 50;
            } else if ("CRITICAL LOW (<20%)".equals(filter)) {
                include = pb.getBatteryLevel() < 20;
            }

            if (include) {
                String health = pb.getBatteryLevel() >= 50 ? "Healthy" : (pb.getBatteryLevel() >= 20 ? "Fair" : "Critical Low");
                String avail = pb.isAvailable() ? "YES" : "NO (" + pb.getStatus() + ")";
                modelBatteryMonitor.addRow(new Object[]{
                        pb.getId(),
                        pb.getModelName(),
                        pb.getSlotLocation(),
                        pb.getBatteryLevel(),
                        health,
                        avail
                });
            }
        }
    }

    private void refreshOverdueTable() {
        if (modelOverdue == null) return;
        modelOverdue.setRowCount(0);

        ArrayList<BorrowRecord> overdueList = borrowingManager.getActiveOverdueBorrows();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (BorrowRecord r : overdueList) {
            String dueStr = r.getDueDate() != null ? r.getDueDate().format(dtf) : "-";
            modelOverdue.addRow(new Object[]{
                    r.getRecordId(),
                    r.getStudentId(),
                    r.getStudentName(),
                    r.getPowerbankId(),
                    dueStr,
                    r.getFormattedOverdueDuration(),
                    String.format("%.2f", r.calculateOverdueFine())
            });
        }
    }

    private void refreshAlertsTable() {
        if (modelAlerts == null) return;
        modelAlerts.setRowCount(0);

        List<Alert> sortedAlerts = alertManager.getActiveAlertsSorted();
        Alert peak = alertManager.peekHighestPriorityAlert();

        if (peak != null) {
            lblPeakAlert.setText(String.format("Priority %d [%s]: %s - %s",
                    peak.getSeverity(), peak.getType(), peak.getTitle(), peak.getMessage()));
        } else {
            lblPeakAlert.setText("No active critical alerts in queue. All systems normal.");
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd HH:mm");
        for (Alert a : sortedAlerts) {
            String timeStr = a.getTimestamp() != null ? a.getTimestamp().format(dtf) : "-";
            modelAlerts.addRow(new Object[]{
                    "Priority " + a.getSeverity() + " (" + a.getSeverityName() + ")",
                    a.getAlertId(),
                    a.getType(),
                    a.getTargetId(),
                    timeStr,
                    a.getMessage()
            });
        }
    }

    private void filterHistoryTable() {
        if (modelHistory == null) return;
        modelHistory.setRowCount(0);

        String query = txtSearchHistory != null ? txtSearchHistory.getText().trim().toLowerCase() : "";
        String status = cmbHistoryStatus != null ? (String) cmbHistoryStatus.getSelectedItem() : "ALL";

        LinkedList<BorrowRecord> list = borrowingManager.getTransactionHistory();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (BorrowRecord r : list) {
            boolean matchQuery = query.isEmpty()
                    || r.getRecordId().toLowerCase().contains(query)
                    || r.getStudentId().toLowerCase().contains(query)
                    || r.getStudentName().toLowerCase().contains(query)
                    || r.getPowerbankId().toLowerCase().contains(query);

            boolean matchStatus = "ALL".equalsIgnoreCase(status) || r.getStatus().equalsIgnoreCase(status);

            if (matchQuery && matchStatus) {
                String bStr = r.getBorrowDate() != null ? r.getBorrowDate().format(dtf) : "-";
                String dStr = r.getDueDate() != null ? r.getDueDate().format(dtf) : "-";
                String rStr = r.getReturnDate() != null ? r.getReturnDate().format(dtf) : "-";

                modelHistory.addRow(new Object[]{
                        r.getRecordId(),
                        r.getStudentId(),
                        r.getStudentName(),
                        r.getPowerbankId(),
                        bStr,
                        dStr,
                        rStr,
                        r.getStatus(),
                        String.format("%.2f", r.getFineAmount())
                });
            }
        }
    }

    private void generateReportContent() {
        if (txtReportArea == null) return;

        int totalPbs = inventoryManager.getTotalCount();
        int availPbs = inventoryManager.getAvailableCount();
        int borrowedPbs = inventoryManager.getBorrowedCount();
        int lowBattPbs = inventoryManager.getLowBatteryCount();
        int overdueCount = borrowingManager.getOverdueCount();
        int queueCount = borrowingManager.getQueueSize();
        int historyCount = borrowingManager.getTransactionHistory().size();

        double utilRate = totalPbs > 0 ? ((double) borrowedPbs / totalPbs) * 100.0 : 0.0;

        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================\n");
        sb.append("                       SMART POWERBANK LENDING SYSTEM - EXECUTIVE REPORT                \n");
        sb.append("========================================================================================\n");
        sb.append(String.format("Generated On: %s | Operator: System Admin\n\n",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

        sb.append("1. FLEET TELEMETRY & UTILIZATION\n");
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(String.format("  - Total PowerBanks Managed : %d\n", totalPbs));
        sb.append(String.format("  - Available for Lending    : %d (%.1f%%)\n", availPbs, totalPbs > 0 ? (availPbs * 100.0 / totalPbs) : 0));
        sb.append(String.format("  - Currently Borrowed       : %d (Utilization Rate: %.1f%%)\n", borrowedPbs, utilRate));
        sb.append(String.format("  - Low Battery Alerts (<20%%): %d\n", lowBattPbs));
        sb.append(String.format("  - Overdue Active Loans     : %d\n", overdueCount));
        sb.append(String.format("  - Waiting Queue Backlog    : %d students in FIFO queue\n\n", queueCount));

        sb.append("2. DATA STRUCTURE USAGE SUMMARY\n");
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(String.format("  - [ArrayList]     : Stores %d PowerBank objects with dynamic querying\n", totalPbs));
        sb.append(String.format("  - [HashMap]       : O(1) Instant retrieval index for %d powerbank & student IDs\n", totalPbs));
        sb.append(String.format("  - [Queue (FIFO)]  : Holding %d active student reservation requests\n", queueCount));
        sb.append(String.format("  - [LinkedList]    : Chronological audit trail containing %d transaction events\n", historyCount));
        sb.append(String.format("  - [PriorityQueue] : Prioritizing %d unresolved system & overdue alerts\n\n", alertManager.getUnresolvedCount()));

        sb.append("3. INVENTORY STATUS BREAKDOWN\n");
        sb.append("----------------------------------------------------------------------------------------\n");
        sb.append(String.format("%-10s %-26s %-12s %-10s %-12s %-10s\n", "ID", "Model", "Capacity", "Battery", "Status", "Slot"));
        for (PowerBank pb : inventoryManager.getAllPowerBanks()) {
            sb.append(String.format("%-10s %-26s %-12s %-10s %-12s %-10s\n",
                    pb.getId(),
                    pb.getModelName(),
                    pb.getCapacityMah() + "mAh",
                    pb.getBatteryLevel() + "%",
                    pb.getStatus(),
                    pb.getSlotLocation()));
        }

        txtReportArea.setText(sb.toString());
        txtReportArea.setCaretPosition(0);
    }

    // ==========================================
    // ACTION HANDLERS
    // ==========================================

    private void performBorrowAction() {
        if (cmbBorrowStudent.getSelectedItem() == null || cmbBorrowPowerBank.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select both a student and an available powerbank.", "Missing Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String studentItem = (String) cmbBorrowStudent.getSelectedItem();
        String studentId = studentItem.split(" - ")[0].trim();

        String pbItem = (String) cmbBorrowPowerBank.getSelectedItem();
        String powerbankId = pbItem.split(" - ")[0].trim();

        int durationHours = 4;
        String durStr = (String) cmbBorrowDuration.getSelectedItem();
        if (durStr != null) {
            try {
                durationHours = Integer.parseInt(durStr.split(" ")[0]);
            } catch (Exception ignored) {}
        }

        try {
            BorrowRecord record = borrowingManager.borrowPowerBank(studentId, powerbankId, durationHours);
            refreshAllViews();
            JOptionPane.showMessageDialog(this,
                    String.format("PowerBank '%s' successfully loaned to %s!\nDue deadline: %s\nRecord ID: %s",
                            powerbankId, record.getStudentName(),
                            record.getDueDate().format(DateTimeFormatter.ofPattern("MMM dd, HH:mm")),
                            record.getRecordId()),
                    "Loan Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Loan Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performReturnAction() {
        int selectedRow = tblActiveBorrows.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an active borrowing record from the table to process return.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String recordId = (String) tblActiveBorrows.getValueAt(selectedRow, 0);
        String pbId = (String) tblActiveBorrows.getValueAt(selectedRow, 2);

        PowerBank pb = inventoryManager.getPowerBank(pbId);
        int currentBatt = pb != null ? Math.max(10, pb.getBatteryLevel() - 20) : 60;

        String input = JOptionPane.showInputDialog(this,
                "Enter returned battery percentage (0 - 100%):",
                String.valueOf(currentBatt));

        if (input == null) return; // user cancelled

        int returnBatt;
        try {
            returnBatt = Integer.parseInt(input.trim());
            if (returnBatt < 0 || returnBatt > 100) throw new Exception();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid battery percentage between 0 and 100.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            BorrowRecord returned = borrowingManager.returnPowerBank(pbId, returnBatt);
            refreshAllViews();

            StringBuilder msg = new StringBuilder();
            msg.append("PowerBank '").append(pbId).append("' returned successfully!\n");
            msg.append("Updated Battery Level: ").append(returnBatt).append("%\n");

            if (returned.getOverdueMinutes() > 0) {
                msg.append("\n⚠️ OVERDUE NOTICE:\n");
                msg.append("Overdue Duration: ").append(returned.getFormattedOverdueDuration()).append("\n");
                msg.append("Penalty Fine: $").append(String.format("%.2f", returned.getFineAmount())).append("\n");
            }

            // Check if student in waiting queue can now be served!
            if (borrowingManager.getQueueSize() > 0 && returnBatt >= 20) {
                msg.append("\n💡 Notice: ").append(borrowingManager.getQueueSize())
                        .append(" student(s) waiting in FIFO Queue. You can dispatch now.");
            }

            JOptionPane.showMessageDialog(this, msg.toString(), "Return Processed", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Return Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performReturnFromOverdueTable() {
        int selectedRow = tblOverdue.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an overdue loan from the table.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String pbId = (String) tblOverdue.getValueAt(selectedRow, 3);
        String input = JOptionPane.showInputDialog(this, "Enter returned battery level for " + pbId + " (0-100%):", "50");
        if (input == null) return;

        try {
            int batt = Integer.parseInt(input.trim());
            borrowingManager.returnPowerBank(pbId, batt);
            refreshAllViews();
            JOptionPane.showMessageDialog(this, "Overdue unit returned and fines logged.", "Return Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendOverdueAlertNotice() {
        int selectedRow = tblOverdue.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select an overdue record to send notice.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String stuName = (String) tblOverdue.getValueAt(selectedRow, 2);
        String pbId = (String) tblOverdue.getValueAt(selectedRow, 3);
        String fine = (String) tblOverdue.getValueAt(selectedRow, 6);

        JOptionPane.showMessageDialog(this,
                String.format("Overdue alert notification dispatched to %s for PowerBank %s.\nCurrent accumulated penalty: $%s", stuName, pbId, fine),
                "Alert Sent", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAddToQueueDialog() {
        JComboBox<String> cmbQueueStu = new JComboBox<>();
        for (Student s : borrowingManager.getAllStudents()) {
            if (s.canBorrow()) {
                cmbQueueStu.addItem(s.getId() + " - " + s.getName());
            }
        }

        JComboBox<String> cmbCap = new JComboBox<>(new String[]{"Any Available Capacity", "10,000 mAh", "20,000 mAh", "25,000+ mAh"});
        JTextField txtNotes = UITheme.createTextField(15);
        txtNotes.setText("Standard library loan");

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.add(new JLabel("Student:"));
        form.add(cmbQueueStu);
        form.add(new JLabel("Preferred Capacity:"));
        form.add(cmbCap);
        form.add(new JLabel("Notes:"));
        form.add(txtNotes);

        int res = JOptionPane.showConfirmDialog(this, form, "Add Student to FIFO Waiting Queue", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION && cmbQueueStu.getSelectedItem() != null) {
            String stuId = ((String) cmbQueueStu.getSelectedItem()).split(" - ")[0].trim();
            int cap = 0;
            if (cmbCap.getSelectedIndex() == 1) cap = 10000;
            else if (cmbCap.getSelectedIndex() == 2) cap = 20000;
            else if (cmbCap.getSelectedIndex() == 3) cap = 25000;

            try {
                borrowingManager.enqueueBorrowRequest(stuId, cap, 4, txtNotes.getText().trim());
                refreshAllViews();
                JOptionPane.showMessageDialog(this, "Student successfully enqueued in FIFO request queue.", "Enqueued", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void dispatchNextFromQueueAction() {
        BorrowRequest req = borrowingManager.peekNextRequest();
        if (req == null) {
            JOptionPane.showMessageDialog(this, "The FIFO Waiting Queue is currently empty.", "Queue Empty", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Find matching available powerbank
        ArrayList<PowerBank> avail = inventoryManager.getAvailablePowerBanks();
        PowerBank selectedPb = null;
        for (PowerBank pb : avail) {
            if (req.getPreferredCapacityMah() == 0 || pb.getCapacityMah() >= req.getPreferredCapacityMah()) {
                selectedPb = pb;
                break;
            }
        }

        if (selectedPb == null) {
            if (!avail.isEmpty()) {
                selectedPb = avail.get(0); // fallback to any available
            } else {
                JOptionPane.showMessageDialog(this,
                        "Cannot dispatch next request: No powerbanks are currently available with sufficient battery (>=20%).",
                        "Inventory Constrained", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        int conf = JOptionPane.showConfirmDialog(this,
                String.format("Dispatch next student in FIFO order?\n\nStudent: %s (%s)\nAssigned PowerBank: %s (%s - %d%%)\nDuration: %d hours",
                        req.getStudentName(), req.getStudentId(), selectedPb.getId(), selectedPb.getModelName(),
                        selectedPb.getBatteryLevel(), req.getRequestedDurationHours()),
                "Confirm FIFO Dispatch", JOptionPane.YES_NO_OPTION);

        if (conf == JOptionPane.YES_OPTION) {
            try {
                borrowingManager.dequeueNextRequest();
                borrowingManager.borrowPowerBank(req.getStudentId(), selectedPb.getId(), req.getRequestedDurationHours());
                refreshAllViews();
                JOptionPane.showMessageDialog(this, "Successfully dispatched and loaned to " + req.getStudentName(), "Dispatch Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Dispatch Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resolveTopAlertAction() {
        Alert top = alertManager.pollHighestPriorityAlert();
        if (top == null) {
            JOptionPane.showMessageDialog(this, "No unresolved alerts in the PriorityQueue.", "All Clear", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        refreshAllViews();
        JOptionPane.showMessageDialog(this,
                String.format("Processed highest priority alert:\n\nID: %s\nSeverity: %d (%s)\nTitle: %s\nTarget: %s",
                        top.getAlertId(), top.getSeverity(), top.getSeverityName(), top.getTitle(), top.getTargetId()),
                "Alert Resolved", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showBinarySearchDialog() {
        String input = JOptionPane.showInputDialog(this,
                "Enter Target Battery Percentage to Search (0 - 100):\n(Demonstrates MergeSort O(N log N) + Binary Search O(log N))",
                "75");
        if (input != null && !input.trim().isEmpty()) {
            try {
                int targetBattery = Integer.parseInt(input.trim());
                if (targetBattery < 0 || targetBattery > 100) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid battery level between 0 and 100.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                FleetSearchAlgorithm.SearchResult result = inventoryManager.binarySearchByBattery(targetBattery);
                StringBuilder sb = new StringBuilder();
                sb.append("=== BINARY SEARCH ALGORITHM EXECUTION TRACE ===\n\n");
                for (String line : result.getExecutionTrace()) {
                    sb.append("• ").append(line).append("\n");
                }
                sb.append("\n================================================\n");
                sb.append(String.format("Time Complexity: O(log N)\nTotal Comparisons: %d\nMatched PowerBank: %s",
                        result.getComparisons(),
                        result.getMatchedUnit() != null ? result.getMatchedUnit().toString() : "None"));

                JTextArea textArea = new JTextArea(sb.toString(), 16, 45);
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                JScrollPane scrollPane = new JScrollPane(textArea);

                JOptionPane.showMessageDialog(this, scrollPane, "Binary Search Algorithm Result - O(log N)", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddPowerBankDialog() {
        JTextField txtId = UITheme.createTextField(10);
        JTextField txtModel = UITheme.createTextField(15);
        JComboBox<String> cmbCap = new JComboBox<>(new String[]{"10000", "20000", "25000", "30000"});
        JTextField txtBatt = UITheme.createTextField(5);
        txtBatt.setText("100");
        JTextField txtSlot = UITheme.createTextField(8);
        txtSlot.setText("DOCK-C02");

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.add(new JLabel("Unit ID (e.g. PB-113):"));
        form.add(txtId);
        form.add(new JLabel("Model Name:"));
        form.add(txtModel);
        form.add(new JLabel("Capacity (mAh):"));
        form.add(cmbCap);
        form.add(new JLabel("Initial Battery (%):"));
        form.add(txtBatt);
        form.add(new JLabel("Slot Location:"));
        form.add(txtSlot);

        int res = JOptionPane.showConfirmDialog(this, form, "Add New PowerBank", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                int cap = Integer.parseInt((String) cmbCap.getSelectedItem());
                int batt = Integer.parseInt(txtBatt.getText().trim());
                PowerBank pb = new PowerBank(txtId.getText().trim(), txtModel.getText().trim(), cap, batt, PowerBank.STATUS_AVAILABLE, txtSlot.getText().trim(), 0);
                inventoryManager.addPowerBank(pb);
                refreshAllViews();
                JOptionPane.showMessageDialog(this, "PowerBank '" + pb.getId() + "' added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditPowerBankDialog() {
        int sel = tblInventory.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Please select a powerbank to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tblInventory.getValueAt(sel, 0);
        PowerBank pb = inventoryManager.getPowerBank(id);
        if (pb == null) return;

        JTextField txtModel = UITheme.createTextField(15);
        txtModel.setText(pb.getModelName());
        JTextField txtCap = UITheme.createTextField(8);
        txtCap.setText(String.valueOf(pb.getCapacityMah()));
        JTextField txtBatt = UITheme.createTextField(5);
        txtBatt.setText(String.valueOf(pb.getBatteryLevel()));
        JComboBox<String> cmbStat = new JComboBox<>(new String[]{PowerBank.STATUS_AVAILABLE, PowerBank.STATUS_BORROWED, PowerBank.STATUS_CHARGING, PowerBank.STATUS_MAINTENANCE});
        cmbStat.setSelectedItem(pb.getStatus());
        JTextField txtSlot = UITheme.createTextField(8);
        txtSlot.setText(pb.getSlotLocation());

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.add(new JLabel("Model Name:"));
        form.add(txtModel);
        form.add(new JLabel("Capacity (mAh):"));
        form.add(txtCap);
        form.add(new JLabel("Battery (%):"));
        form.add(txtBatt);
        form.add(new JLabel("Status:"));
        form.add(cmbStat);
        form.add(new JLabel("Slot Location:"));
        form.add(txtSlot);

        int res = JOptionPane.showConfirmDialog(this, form, "Edit PowerBank: " + id, JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                pb.setModelName(txtModel.getText().trim());
                pb.setCapacityMah(Integer.parseInt(txtCap.getText().trim()));
                pb.setBatteryLevel(Integer.parseInt(txtBatt.getText().trim()));
                pb.setStatus((String) cmbStat.getSelectedItem());
                pb.setSlotLocation(txtSlot.getText().trim());

                inventoryManager.updatePowerBank(pb);
                refreshAllViews();
                JOptionPane.showMessageDialog(this, "PowerBank updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedPowerBank() {
        int sel = tblInventory.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Please select a powerbank to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tblInventory.getValueAt(sel, 0);
        int conf = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete PowerBank " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            try {
                inventoryManager.deletePowerBank(id);
                refreshAllViews();
                JOptionPane.showMessageDialog(this, "PowerBank deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot Delete", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddStudentDialog() {
        JTextField txtId = UITheme.createTextField(10);
        JTextField txtName = UITheme.createTextField(15);
        JTextField txtEmail = UITheme.createTextField(15);
        JTextField txtDept = UITheme.createTextField(12);
        txtDept.setText("Computer Science");
        JTextField txtPhone = UITheme.createTextField(12);
        txtPhone.setText("+91 98765 ");

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.add(new JLabel("Student ID (e.g. STU-1009):"));
        form.add(txtId);
        form.add(new JLabel("Full Name:"));
        form.add(txtName);
        form.add(new JLabel("Email Address:"));
        form.add(txtEmail);
        form.add(new JLabel("Department:"));
        form.add(txtDept);
        form.add(new JLabel("Phone:"));
        form.add(txtPhone);

        int res = JOptionPane.showConfirmDialog(this, form, "Register New Student", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                Student s = new Student(txtId.getText().trim(), txtName.getText().trim(), txtEmail.getText().trim(),
                        txtDept.getText().trim(), txtPhone.getText().trim(), 0, Student.DEFAULT_MAX_BORROWS);
                borrowingManager.addStudent(s);
                refreshAllViews();
                JOptionPane.showMessageDialog(this, "Student registered successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditStudentDialog() {
        int sel = tblStudents.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tblStudents.getValueAt(sel, 0);
        Student s = borrowingManager.getStudent(id);
        if (s == null) return;

        JTextField txtName = UITheme.createTextField(15);
        txtName.setText(s.getName());
        JTextField txtEmail = UITheme.createTextField(15);
        txtEmail.setText(s.getEmail());
        JTextField txtDept = UITheme.createTextField(12);
        txtDept.setText(s.getDepartment());
        JTextField txtPhone = UITheme.createTextField(12);
        txtPhone.setText(s.getPhone());

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.add(new JLabel("Full Name:"));
        form.add(txtName);
        form.add(new JLabel("Email Address:"));
        form.add(txtEmail);
        form.add(new JLabel("Department:"));
        form.add(txtDept);
        form.add(new JLabel("Phone:"));
        form.add(txtPhone);

        int res = JOptionPane.showConfirmDialog(this, form, "Edit Student: " + id, JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                s.setName(txtName.getText().trim());
                s.setEmail(txtEmail.getText().trim());
                s.setDepartment(txtDept.getText().trim());
                s.setPhone(txtPhone.getText().trim());

                borrowingManager.updateStudent(s);
                refreshAllViews();
                JOptionPane.showMessageDialog(this, "Student details updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedStudent() {
        int sel = tblStudents.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tblStudents.getValueAt(sel, 0);
        int conf = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete student " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            try {
                borrowingManager.deleteStudent(id);
                refreshAllViews();
                JOptionPane.showMessageDialog(this, "Student deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot Delete", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void simulateBatteryDrain() {
        int sel = tblBatteryMonitor.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Select a powerbank from the table to simulate drain.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tblBatteryMonitor.getValueAt(sel, 0);
        PowerBank pb = inventoryManager.getPowerBank(id);
        if (pb != null) {
            int newBatt = Math.max(0, pb.getBatteryLevel() - 15);
            inventoryManager.updateBatteryLevel(id, newBatt);
            alertManager.scanAndGenerateAlerts();
            refreshAllViews();
            JOptionPane.showMessageDialog(this,
                    String.format("PowerBank %s battery drained by 15%%.\nNew level: %d%%.\n%s",
                            id, newBatt, newBatt < 20 ? "🚨 LOW BATTERY ALERT TRIGGERED & ENQUEUED IN PRIORITYQUEUE!" : "Battery level within safe threshold."),
                    "Drain Simulation", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void simulateBatteryRecharge() {
        int sel = tblBatteryMonitor.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Select a powerbank to recharge.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) tblBatteryMonitor.getValueAt(sel, 0);
        inventoryManager.updateBatteryLevel(id, 100);
        alertManager.scanAndGenerateAlerts();
        refreshAllViews();
        JOptionPane.showMessageDialog(this, "PowerBank " + id + " fully recharged to 100%.", "Recharge Complete", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportTransactionAuditLog() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("powerbank_audit_log.csv"));
        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File dest = chooser.getSelectedFile();
            try (FileWriter fw = new FileWriter(dest)) {
                fw.write("RecordID,StudentID,StudentName,PowerBankID,BorrowDate,DueDate,ReturnDate,Status,InitialBatt,ReturnBatt,Fine\n");
                for (BorrowRecord r : borrowingManager.getTransactionHistory()) {
                    fw.write(r.toCsvString() + "\n");
                }
                JOptionPane.showMessageDialog(this, "Audit log exported to:\n" + dest.getAbsolutePath(), "Export Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportReportToFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("powerbank_executive_report.txt"));
        int res = chooser.showSaveDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File dest = chooser.getSelectedFile();
            try (FileWriter fw = new FileWriter(dest)) {
                fw.write(txtReportArea.getText());
                JOptionPane.showMessageDialog(this, "Report exported to:\n" + dest.getAbsolutePath(), "Report Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void logout() {
        int conf = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out of the admin portal?", "Logout Confirmation", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                Login login = new Login(inventoryManager, borrowingManager, alertManager, fileStorage);
                login.setVisible(true);
            });
        }
    }

    private void startClockAndAutoRefreshTimer() {
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("MMM dd, yyyy  HH:mm:ss");
        Timer clockTimer = new Timer(1000, e -> {
            lblClock.setText(LocalDateTime.now().format(timeFmt));
        });
        clockTimer.start();
    }
}
