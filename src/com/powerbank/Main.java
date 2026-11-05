package com.powerbank;

import com.powerbank.data.FileStorage;
import com.powerbank.service.AlertManager;
import com.powerbank.service.BorrowingManager;
import com.powerbank.service.InventoryManager;
import com.powerbank.ui.Dashboard;
import com.powerbank.ui.Login;

import javax.swing.*;
import java.awt.*;

/**
 * Main application entry point for the Smart Powerbank Lending System.
 * Configures modern rendering aesthetics, initializes services,
 * and boots the Admin Login portal.
 */
public class Main {

    public static void main(String[] args) {
        // Configure high-DPI scaling and font anti-aliasing
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        try {
            // Attempt to set Modern System Look and Feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fallback to default Swing Look and Feel
        }

        // Initialize Services & Core Data Structures
        FileStorage fileStorage = new FileStorage();
        InventoryManager inventoryManager = new InventoryManager(fileStorage);
        BorrowingManager borrowingManager = new BorrowingManager(inventoryManager, fileStorage);
        AlertManager alertManager = new AlertManager(inventoryManager, borrowingManager, fileStorage);

        // Check if headless or demo flag is passed
        if (args.length > 0 && "--skip-login".equalsIgnoreCase(args[0])) {
            SwingUtilities.invokeLater(() -> {
                Dashboard dashboard = new Dashboard(inventoryManager, borrowingManager, alertManager, fileStorage);
                dashboard.setVisible(true);
            });
        } else {
            SwingUtilities.invokeLater(() -> {
                Login login = new Login(inventoryManager, borrowingManager, alertManager, fileStorage);
                login.setVisible(true);
            });
        }
    }
}
