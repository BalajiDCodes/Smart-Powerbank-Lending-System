package com.powerbank.ui;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Custom table cell renderer displaying a visual battery progress bar
 * with color-coded alerts (<20% Red, 20-49% Amber, >=50% Green).
 */
public class BatteryBarRenderer extends JProgressBar implements TableCellRenderer {

    public BatteryBarRenderer() {
        super(0, 100);
        setStringPainted(true);
        setBorderPainted(false);
        setFont(UITheme.FONT_BODY_BOLD);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        int battery = 0;
        if (value instanceof Number) {
            battery = ((Number) value).intValue();
        } else if (value != null) {
            try {
                String str = value.toString().replace("%", "").trim();
                battery = Integer.parseInt(str);
            } catch (Exception ignored) {
            }
        }

        setValue(battery);
        setString(battery + "%");

        if (battery < 20) {
            setForeground(UITheme.DANGER_RED);
        } else if (battery < 50) {
            setForeground(UITheme.WARNING_AMBER);
        } else {
            setForeground(UITheme.SUCCESS_GREEN);
        }

        setBackground(new Color(241, 245, 249));
        return this;
    }
}
