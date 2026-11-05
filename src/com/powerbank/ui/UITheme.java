package com.powerbank.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Design system containing colors, typography, button stylers,
 * cards, tables, and badge renderers for the Smart Powerbank Lending System.
 */
public class UITheme {

    // Palette: Navy Blue, White, Teal, and Accents
    public static final Color NAVY_DARK = new Color(15, 23, 42);      // #0F172A
    public static final Color NAVY_SIDEBAR = new Color(30, 41, 59);   // #1E293B
    public static final Color NAVY_CARD = new Color(51, 65, 85);      // #334155
    public static final Color NAVY_HOVER = new Color(71, 85, 105);    // #475569

    public static final Color TEAL_PRIMARY = new Color(13, 148, 136);  // #0D9488
    public static final Color TEAL_LIGHT = new Color(20, 184, 166);    // #14B8A6
    public static final Color TEAL_MUTED = new Color(204, 251, 241);   // #CCFBF1
    public static final Color TEAL_DARK = new Color(15, 118, 110);     // #0F766E

    public static final Color BG_LIGHT = new Color(248, 250, 252);     // #F8FAFC
    public static final Color CARD_BG = Color.WHITE;
    public static final Color BORDER_COLOR = new Color(226, 232, 240); // #E2E8F0

    public static final Color TEXT_PRIMARY = new Color(15, 23, 42);    // #0F172A
    public static final Color TEXT_SECONDARY = new Color(100, 116, 139); // #64748B
    public static final Color TEXT_MUTED = new Color(148, 163, 184);   // #94A3B8

    public static final Color SUCCESS_GREEN = new Color(16, 185, 129); // #10B981
    public static final Color WARNING_AMBER = new Color(245, 158, 11); // #F59E0B
    public static final Color DANGER_RED = new Color(239, 68, 68);     // #EF4444
    public static final Color INFO_BLUE = new Color(59, 130, 246);     // #3B82F6

    // Fonts
    public static final Font FONT_HEADER_LARGE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER_MEDIUM = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_STAT_VALUE = new Font("Segoe UI", Font.BOLD, 28);

    /**
     * Creates a styled modern button with rounded aesthetics and hover feedback.
     */
    public static JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BODY_BOLD);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(9, 16, 9, 16));

        // Hover effect
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(darkenOrBrighten(bg));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) {
                    btn.setBackground(bg);
                }
            }
        });

        return btn;
    }

    public static JButton createPrimaryButton(String text) {
        return createButton(text, TEAL_PRIMARY, Color.WHITE);
    }

    public static JButton createDangerButton(String text) {
        return createButton(text, DANGER_RED, Color.WHITE);
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = createButton(text, new Color(241, 245, 249), TEXT_PRIMARY);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(8, 14, 8, 14)
        ));
        return btn;
    }

    private static Color darkenOrBrighten(Color c) {
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        float brightness = Math.max(0.1f, Math.min(1.0f, hsb[2] > 0.5f ? hsb[2] * 0.9f : hsb[2] * 1.15f));
        return Color.getHSBColor(hsb[0], hsb[1], brightness);
    }

    /**
     * Creates a styled card container.
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(16, 16, 16, 16)
        ));
        return panel;
    }

    /**
     * Creates a modern Dashboard metric/KPI card.
     */
    public static JPanel createStatCard(String title, String value, String subtext, Color accentColor, String iconSymbol) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, accentColor),
                BorderFactory.createCompoundBorder(
                        new LineBorder(BORDER_COLOR, 1),
                        new EmptyBorder(14, 16, 14, 16)
                )
        ));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(TEXT_SECONDARY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(FONT_STAT_VALUE);
        lblValue.setForeground(TEXT_PRIMARY);

        JLabel lblSub = new JLabel(subtext);
        lblSub.setFont(FONT_SMALL);
        lblSub.setForeground(TEXT_MUTED);

        textPanel.add(lblTitle);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(lblValue);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(lblSub);

        JLabel lblIcon = new JLabel(iconSymbol);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        lblIcon.setForeground(accentColor);
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(lblIcon, BorderLayout.EAST);
        return card;
    }

    /**
     * Styles a JTable with professional navy headers, alternating row colors,
     * proper row heights, and padding.
     */
    public static void styleTable(JTable table) {
        table.setRowHeight(36);
        table.setFont(FONT_BODY);
        table.setShowGrid(true);
        table.setGridColor(new Color(241, 245, 249));
        table.setSelectionBackground(new Color(204, 251, 241));
        table.setSelectionForeground(NAVY_DARK);
        table.setFillsViewportHeight(true);

        // Header Styling
        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_TITLE);
        header.setBackground(NAVY_SIDEBAR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 40));
        header.setReorderingAllowed(false);

        // Default cell renderer with padding and alternating row colors
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    c.setForeground(TEXT_PRIMARY);
                }
                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }
    }

    /**
     * Creates a styled text field.
     */
    public static JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(FONT_BODY);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(7, 10, 7, 10)
        ));
        return tf;
    }

    public static JPasswordField createPasswordField(int columns) {
        JPasswordField pf = new JPasswordField(columns);
        pf.setFont(FONT_BODY);
        pf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(7, 10, 7, 10)
        ));
        return pf;
    }

    /**
     * Creates a status pill / badge component.
     */
    public static JLabel createBadge(String text, Color bg, Color fg) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setOpaque(true);
        lbl.setBackground(bg);
        lbl.setForeground(fg);
        lbl.setBorder(new EmptyBorder(3, 8, 3, 8));
        return lbl;
    }
}
