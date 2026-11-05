package com.powerbank.service;

import com.powerbank.data.FileStorage;
import com.powerbank.model.Alert;
import com.powerbank.model.BorrowRecord;
import com.powerbank.model.PowerBank;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Manages system alerts with automated detection of overdue loans and low-battery powerbanks.
 * Demonstrates:
 * - PriorityQueue: Ensures critical alerts (Overdue and <10% Battery) always surface
 *   ahead of general warnings (10-20% battery) and info messages.
 */
public class AlertManager {

    // Mandatory Data Structure 5: PriorityQueue for prioritizing critical alerts
    private final PriorityQueue<Alert> alertPriorityQueue;

    // Persistent storage of all alerts
    private final List<Alert> allAlerts;

    private final InventoryManager inventoryManager;
    private final BorrowingManager borrowingManager;
    private final FileStorage fileStorage;

    public AlertManager(InventoryManager inventoryManager, BorrowingManager borrowingManager, FileStorage fileStorage) {
        this.inventoryManager = inventoryManager;
        this.borrowingManager = borrowingManager;
        this.fileStorage = fileStorage;

        this.alertPriorityQueue = new PriorityQueue<>();
        this.allAlerts = new ArrayList<>();

        loadAndRefreshAlerts();
    }

    public synchronized void loadAndRefreshAlerts() {
        alertPriorityQueue.clear();
        allAlerts.clear();

        List<Alert> saved = fileStorage.loadAlerts();
        for (Alert a : saved) {
            allAlerts.add(a);
            if (!a.isResolved()) {
                alertPriorityQueue.offer(a);
            }
        }

        // Run automated diagnostic scan across powerbanks and borrows
        scanAndGenerateAlerts();
    }

    /**
     * Diagnostic scan that checks:
     * 1. Overdue Borrowings (Severity 1 - Critical)
     * 2. Critical Battery Powerbanks < 10% (Severity 1 - Critical)
     * 3. Low Battery Powerbanks 10-19% (Severity 2 - Warning)
     */
    public synchronized void scanAndGenerateAlerts() {
        Set<String> activeTargetKeys = new HashSet<>();

        // 1. Scan for Overdue Loans
        List<BorrowRecord> overdueRecords = borrowingManager.getActiveOverdueBorrows();
        for (BorrowRecord rec : overdueRecords) {
            String key = "OVERDUE_" + rec.getRecordId();
            activeTargetKeys.add(key);

            if (!hasUnresolvedAlertFor(key)) {
                String title = "Overdue Loan: " + rec.getPowerbankId();
                String msg = String.format("Borrower %s (ID: %s) is overdue by %s. Penalty fee: $%.2f",
                        rec.getStudentName(), rec.getStudentId(), rec.getFormattedOverdueDuration(), rec.calculateOverdueFine());

                Alert alert = new Alert(
                        "ALT-OV-" + (System.currentTimeMillis() % 10000),
                        Alert.TYPE_OVERDUE,
                        Alert.SEVERITY_CRITICAL, // Priority 1
                        title,
                        msg,
                        key,
                        LocalDateTime.now(),
                        false
                );
                allAlerts.add(alert);
                alertPriorityQueue.offer(alert);
            }
        }

        // 2. Scan for Battery Alerts in Inventory
        List<PowerBank> allPbs = inventoryManager.getAllPowerBanks();
        for (PowerBank pb : allPbs) {
            if (pb.getBatteryLevel() < 10) {
                String key = "BATT_CRIT_" + pb.getId();
                activeTargetKeys.add(key);
                if (!hasUnresolvedAlertFor(key)) {
                    Alert alert = new Alert(
                            "ALT-BC-" + (System.currentTimeMillis() % 10000),
                            Alert.TYPE_CRITICAL_BATTERY,
                            Alert.SEVERITY_CRITICAL, // Priority 1
                            "CRITICAL BATTERY: " + pb.getId(),
                            String.format("PowerBank %s (%s) battery is at %d%%! Immediate recharge needed at %s.",
                                    pb.getId(), pb.getModelName(), pb.getBatteryLevel(), pb.getSlotLocation()),
                            key,
                            LocalDateTime.now(),
                            false
                    );
                    allAlerts.add(alert);
                    alertPriorityQueue.offer(alert);
                }
            } else if (pb.getBatteryLevel() < 20) {
                String key = "BATT_LOW_" + pb.getId();
                activeTargetKeys.add(key);
                if (!hasUnresolvedAlertFor(key)) {
                    Alert alert = new Alert(
                            "ALT-BL-" + (System.currentTimeMillis() % 10000),
                            Alert.TYPE_LOW_BATTERY,
                            Alert.SEVERITY_WARNING, // Priority 2
                            "Low Battery Warning: " + pb.getId(),
                            String.format("PowerBank %s (%s) battery is at %d%%. Minimum 20%% required for lending.",
                                    pb.getId(), pb.getModelName(), pb.getBatteryLevel()),
                            key,
                            LocalDateTime.now(),
                            false
                    );
                    allAlerts.add(alert);
                    alertPriorityQueue.offer(alert);
                }
            }
        }

        fileStorage.saveAlerts(allAlerts);
    }

    private boolean hasUnresolvedAlertFor(String targetKey) {
        for (Alert a : allAlerts) {
            if (!a.isResolved() && targetKey.equalsIgnoreCase(a.getTargetId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * PriorityQueue Operation: Retrieve and remove the highest-priority alert.
     */
    public synchronized Alert pollHighestPriorityAlert() {
        Alert top = alertPriorityQueue.poll();
        if (top != null) {
            top.setResolved(true);
            fileStorage.saveAlerts(allAlerts);
        }
        return top;
    }

    /**
     * PriorityQueue Operation: Inspect the highest-priority alert without removing.
     */
    public Alert peekHighestPriorityAlert() {
        return alertPriorityQueue.peek();
    }

    /**
     * Retrieves all unresolved alerts sorted in strict priority order (PriorityQueue order).
     */
    public synchronized List<Alert> getActiveAlertsSorted() {
        PriorityQueue<Alert> copy = new PriorityQueue<>(alertPriorityQueue);
        List<Alert> sorted = new ArrayList<>();
        while (!copy.isEmpty()) {
            sorted.add(copy.poll());
        }
        return sorted;
    }

    /**
     * Resolves a specific alert by ID.
     */
    public synchronized boolean resolveAlert(String alertId) {
        if (alertId == null) return false;
        boolean found = false;
        for (Alert a : allAlerts) {
            if (a.getAlertId().equalsIgnoreCase(alertId.trim())) {
                a.setResolved(true);
                found = true;
                break;
            }
        }
        if (found) {
            // Rebuild priority queue without the resolved alert
            alertPriorityQueue.clear();
            for (Alert a : allAlerts) {
                if (!a.isResolved()) {
                    alertPriorityQueue.offer(a);
                }
            }
            fileStorage.saveAlerts(allAlerts);
        }
        return found;
    }

    public synchronized void resolveAllAlerts() {
        for (Alert a : allAlerts) {
            a.setResolved(true);
        }
        alertPriorityQueue.clear();
        fileStorage.saveAlerts(allAlerts);
    }

    public int getUnresolvedCount() {
        return alertPriorityQueue.size();
    }

    public int getCriticalAlertCount() {
        int count = 0;
        for (Alert a : alertPriorityQueue) {
            if (a.getSeverity() == Alert.SEVERITY_CRITICAL) count++;
        }
        return count;
    }
}
