package com.powerbank.model;

import java.io.Serializable;

/**
 * Model representing a PowerBank unit in the inventory.
 * Stores battery levels, status, slot location, and usage statistics.
 */
public class PowerBank implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String STATUS_AVAILABLE = "AVAILABLE";
    public static final String STATUS_BORROWED = "BORROWED";
    public static final String STATUS_CHARGING = "CHARGING";
    public static final String STATUS_MAINTENANCE = "MAINTENANCE";

    private String id;
    private String modelName;
    private int capacityMah;
    private int batteryLevel; // 0 - 100
    private String status;    // AVAILABLE, BORROWED, CHARGING, MAINTENANCE
    private String slotLocation;
    private int totalBorrowCount;

    public PowerBank() {
        this.status = STATUS_AVAILABLE;
        this.batteryLevel = 100;
        this.totalBorrowCount = 0;
    }

    public PowerBank(String id, String modelName, int capacityMah, int batteryLevel, String status, String slotLocation, int totalBorrowCount) {
        this.id = id != null ? id.trim() : "";
        this.modelName = modelName != null ? modelName.trim() : "";
        this.capacityMah = capacityMah;
        this.batteryLevel = Math.max(0, Math.min(100, batteryLevel));
        this.status = (status != null && !status.trim().isEmpty()) ? status.trim().toUpperCase() : STATUS_AVAILABLE;
        this.slotLocation = slotLocation != null ? slotLocation.trim() : "DOCK-00";
        this.totalBorrowCount = Math.max(0, totalBorrowCount);
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id != null ? id.trim() : "";
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName != null ? modelName.trim() : "";
    }

    public int getCapacityMah() {
        return capacityMah;
    }

    public void setCapacityMah(int capacityMah) {
        this.capacityMah = capacityMah;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = Math.max(0, Math.min(100, batteryLevel));
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status != null ? status.trim().toUpperCase() : STATUS_AVAILABLE;
    }

    public String getSlotLocation() {
        return slotLocation;
    }

    public void setSlotLocation(String slotLocation) {
        this.slotLocation = slotLocation != null ? slotLocation.trim() : "";
    }

    public int getTotalBorrowCount() {
        return totalBorrowCount;
    }

    public void setTotalBorrowCount(int totalBorrowCount) {
        this.totalBorrowCount = totalBorrowCount;
    }

    public void incrementBorrowCount() {
        this.totalBorrowCount++;
    }

    /**
     * Checks if this powerbank is operational and available for lending.
     * Powerbank must be in AVAILABLE state and have at least 20% battery.
     */
    public boolean isAvailable() {
        return STATUS_AVAILABLE.equalsIgnoreCase(status) && batteryLevel >= 20;
    }

    /**
     * Checks if the battery level is critically low or below 20%.
     */
    public boolean isLowBattery() {
        return batteryLevel < 20;
    }

    /**
     * Checks if battery is critically low (< 10%).
     */
    public boolean isCriticalBattery() {
        return batteryLevel < 10;
    }

    /**
     * Serializes to CSV representation.
     */
    public String toCsvString() {
        return String.format("%s,%s,%d,%d,%s,%s,%d",
                escapeCsv(id),
                escapeCsv(modelName),
                capacityMah,
                batteryLevel,
                status,
                escapeCsv(slotLocation),
                totalBorrowCount);
    }

    /**
     * Parses from CSV line.
     */
    public static PowerBank fromCsvString(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        if (parts.length < 7) return null;
        try {
            String id = unescapeCsv(parts[0]);
            String model = unescapeCsv(parts[1]);
            int capacity = Integer.parseInt(parts[2].trim());
            int battery = Integer.parseInt(parts[3].trim());
            String status = parts[4].trim();
            String slot = unescapeCsv(parts[5]);
            int count = Integer.parseInt(parts[6].trim());
            return new PowerBank(id, model, capacity, battery, status, slot, count);
        } catch (Exception e) {
            System.err.println("Error parsing PowerBank CSV: " + line + " -> " + e.getMessage());
            return null;
        }
    }

    private static String escapeCsv(String val) {
        if (val == null) return "";
        if (val.contains(",") || val.contains("\"") || val.contains("\n")) {
            return "\"" + val.replace("\"", "\"\"") + "\"";
        }
        return val;
    }

    private static String unescapeCsv(String val) {
        if (val == null) return "";
        val = val.trim();
        if (val.startsWith("\"") && val.endsWith("\"") && val.length() >= 2) {
            val = val.substring(1, val.length() - 1).replace("\"\"", "\"");
        }
        return val;
    }

    @Override
    public String toString() {
        return String.format("%s (%s - %dmAh - %d%%)", id, modelName, capacityMah, batteryLevel);
    }
}
