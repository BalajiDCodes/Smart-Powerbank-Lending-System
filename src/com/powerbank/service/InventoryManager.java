package com.powerbank.service;

import com.powerbank.data.FileStorage;
import com.powerbank.model.PowerBank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Manages PowerBank inventory operations.
 * Demonstrates:
 * - ArrayList: Dynamic storage, list views, sorting, and linear filtering.
 * - HashMap: Instantaneous O(1) retrieval and unique ID collision detection.
 */
public class InventoryManager {

    // Mandatory Data Structure 1: ArrayList for ordered collection & display
    private final ArrayList<PowerBank> powerBankList;

    // Mandatory Data Structure 2: HashMap for O(1) instant key-value lookup by ID
    private final HashMap<String, PowerBank> powerBankMap;

    private final FileStorage fileStorage;

    public InventoryManager(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
        this.powerBankList = new ArrayList<>();
        this.powerBankMap = new HashMap<>();
        loadData();
    }

    private void loadData() {
        powerBankList.clear();
        powerBankMap.clear();

        List<PowerBank> loaded = fileStorage.loadPowerBanks();
        for (PowerBank pb : loaded) {
            powerBankList.add(pb);
            powerBankMap.put(pb.getId().toUpperCase(), pb);
        }
    }

    public synchronized void persistData() {
        fileStorage.savePowerBanks(powerBankList);
    }

    /**
     * HashMap Lookup: O(1) instant retrieval of a powerbank by its unique ID.
     */
    public PowerBank getPowerBank(String id) {
        if (id == null) return null;
        return powerBankMap.get(id.trim().toUpperCase());
    }

    /**
     * Checks if a powerbank with the specified ID already exists (O(1)).
     */
    public boolean exists(String id) {
        if (id == null) return false;
        return powerBankMap.containsKey(id.trim().toUpperCase());
    }

    /**
     * Adds a new powerbank to both ArrayList and HashMap.
     * Validates uniqueness and non-empty fields.
     */
    public synchronized boolean addPowerBank(PowerBank pb) throws IllegalArgumentException {
        if (pb == null) {
            throw new IllegalArgumentException("PowerBank object cannot be null.");
        }
        if (pb.getId() == null || pb.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("PowerBank ID cannot be blank.");
        }
        String normalizedId = pb.getId().trim().toUpperCase();
        if (powerBankMap.containsKey(normalizedId)) {
            throw new IllegalArgumentException("PowerBank ID '" + normalizedId + "' already exists in inventory.");
        }
        if (pb.getCapacityMah() <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0 mAh.");
        }
        if (pb.getBatteryLevel() < 0 || pb.getBatteryLevel() > 100) {
            throw new IllegalArgumentException("Battery level must be between 0% and 100%.");
        }

        pb.setId(normalizedId);
        powerBankMap.put(normalizedId, pb);
        powerBankList.add(pb);
        persistData();
        return true;
    }

    /**
     * Updates an existing powerbank in both structures.
     */
    public synchronized boolean updatePowerBank(PowerBank updated) throws IllegalArgumentException {
        if (updated == null || updated.getId() == null) {
            throw new IllegalArgumentException("Invalid PowerBank data for update.");
        }
        String id = updated.getId().trim().toUpperCase();
        PowerBank existing = powerBankMap.get(id);
        if (existing == null) {
            throw new IllegalArgumentException("PowerBank ID '" + id + "' not found.");
        }

        existing.setModelName(updated.getModelName());
        existing.setCapacityMah(updated.getCapacityMah());
        existing.setBatteryLevel(updated.getBatteryLevel());
        existing.setStatus(updated.getStatus());
        existing.setSlotLocation(updated.getSlotLocation());
        existing.setTotalBorrowCount(updated.getTotalBorrowCount());

        persistData();
        return true;
    }

    /**
     * Deletes a powerbank from both ArrayList and HashMap.
     * Prevents deletion if currently borrowed.
     */
    public synchronized boolean deletePowerBank(String id) throws IllegalStateException, IllegalArgumentException {
        if (id == null) return false;
        String normalizedId = id.trim().toUpperCase();
        PowerBank pb = powerBankMap.get(normalizedId);
        if (pb == null) {
            throw new IllegalArgumentException("PowerBank ID '" + normalizedId + "' not found.");
        }
        if (PowerBank.STATUS_BORROWED.equalsIgnoreCase(pb.getStatus())) {
            throw new IllegalStateException("Cannot delete PowerBank '" + normalizedId + "' because it is currently BORROWED.");
        }

        powerBankMap.remove(normalizedId);
        powerBankList.remove(pb);
        persistData();
        return true;
    }

    /**
     * Returns a copy of the ArrayList containing all powerbanks.
     */
    public ArrayList<PowerBank> getAllPowerBanks() {
        return new ArrayList<>(powerBankList);
    }

    /**
     * Filters powerbanks using linear scan across the ArrayList.
     */
    public ArrayList<PowerBank> searchAndFilter(String query, String statusFilter) {
        ArrayList<PowerBank> result = new ArrayList<>();
        String q = query != null ? query.trim().toLowerCase() : "";
        String status = statusFilter != null ? statusFilter.trim().toUpperCase() : "ALL";

        for (PowerBank pb : powerBankList) {
            boolean matchesQuery = q.isEmpty()
                    || pb.getId().toLowerCase().contains(q)
                    || pb.getModelName().toLowerCase().contains(q)
                    || pb.getSlotLocation().toLowerCase().contains(q);

            boolean matchesStatus;
            if ("ALL".equalsIgnoreCase(status)) {
                matchesStatus = true;
            } else if ("LOW_BATTERY".equalsIgnoreCase(status)) {
                matchesStatus = pb.isLowBattery();
            } else {
                matchesStatus = pb.getStatus().equalsIgnoreCase(status);
            }

            if (matchesQuery && matchesStatus) {
                result.add(pb);
            }
        }
        return result;
    }

    /**
     * Returns all powerbanks that are currently available and have at least 20% battery.
     */
    public ArrayList<PowerBank> getAvailablePowerBanks() {
        ArrayList<PowerBank> available = new ArrayList<>();
        for (PowerBank pb : powerBankList) {
            if (pb.isAvailable()) {
                available.add(pb);
            }
        }
        return available;
    }

    /**
     * Returns powerbanks with battery level < 20%.
     */
    public ArrayList<PowerBank> getLowBatteryPowerBanks() {
        ArrayList<PowerBank> low = new ArrayList<>();
        for (PowerBank pb : powerBankList) {
            if (pb.isLowBattery()) {
                low.add(pb);
            }
        }
        return low;
    }

    /**
     * Simulates battery drain or recharge for testing & battery monitoring module.
     */
    public synchronized void updateBatteryLevel(String id, int newLevel) {
        PowerBank pb = getPowerBank(id);
        if (pb != null) {
            pb.setBatteryLevel(newLevel);
            // If battery drops below 10%, set to charging or maintenance if available
            if (pb.isAvailable() && pb.getBatteryLevel() < 20) {
                // Keep status but availability check will fail
            }
            persistData();
        }
    }

    // Dashboard Statistics Metrics
    public int getTotalCount() {
        return powerBankList.size();
    }

    public int getAvailableCount() {
        int count = 0;
        for (PowerBank pb : powerBankList) {
            if (pb.isAvailable()) count++;
        }
        return count;
    }

    public int getBorrowedCount() {
        int count = 0;
        for (PowerBank pb : powerBankList) {
            if (PowerBank.STATUS_BORROWED.equalsIgnoreCase(pb.getStatus())) count++;
        }
        return count;
    }

    public int getLowBatteryCount() {
        int count = 0;
        for (PowerBank pb : powerBankList) {
            if (pb.isLowBattery()) count++;
        }
        return count;
    }

    public int getMaintenanceCount() {
        int count = 0;
        for (PowerBank pb : powerBankList) {
            if (PowerBank.STATUS_MAINTENANCE.equalsIgnoreCase(pb.getStatus()) ||
                PowerBank.STATUS_CHARGING.equalsIgnoreCase(pb.getStatus())) {
                count++;
            }
        }
        return count;
    }
}
