package com.powerbank.service;

import com.powerbank.data.FileStorage;
import com.powerbank.model.BorrowRecord;
import com.powerbank.model.BorrowRequest;
import com.powerbank.model.PowerBank;
import com.powerbank.model.Student;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

/**
 * Manages PowerBank borrowing, returns, waiting requests, and transaction histories.
 * Demonstrates:
 * - Queue (FIFO): Manage student waiting requests when units are unavailable.
 * - LinkedList: High-performance sequential transaction history log.
 * - HashMap: Instant student lookup and active borrow lookup.
 */
public class BorrowingManager {

    // Mandatory Data Structure 3: Queue (FIFO) for waiting requests
    private final Queue<BorrowRequest> borrowQueue;

    // Mandatory Data Structure 4: LinkedList for chronological transaction audit logs
    private final LinkedList<BorrowRecord> transactionHistory;

    // Active borrows tracked in an ArrayList and HashMap for quick validation
    private final ArrayList<BorrowRecord> activeBorrows;
    private final HashMap<String, BorrowRecord> activeBorrowByPowerBankId;

    // Student Registry
    private final ArrayList<Student> studentList;
    private final HashMap<String, Student> studentMap;

    private final InventoryManager inventoryManager;
    private final FileStorage fileStorage;

    public BorrowingManager(InventoryManager inventoryManager, FileStorage fileStorage) {
        this.inventoryManager = inventoryManager;
        this.fileStorage = fileStorage;

        this.borrowQueue = new LinkedList<>();
        this.transactionHistory = new LinkedList<>();
        this.activeBorrows = new ArrayList<>();
        this.activeBorrowByPowerBankId = new HashMap<>();

        this.studentList = new ArrayList<>();
        this.studentMap = new HashMap<>();

        loadData();
    }

    private void loadData() {
        // 1. Load Students
        studentList.clear();
        studentMap.clear();
        List<Student> loadedStudents = fileStorage.loadStudents();
        for (Student st : loadedStudents) {
            studentList.add(st);
            studentMap.put(st.getId().toUpperCase(), st);
        }

        // 2. Load Active Borrows
        activeBorrows.clear();
        activeBorrowByPowerBankId.clear();
        List<BorrowRecord> loadedBorrows = fileStorage.loadActiveBorrows();
        for (BorrowRecord rec : loadedBorrows) {
            if (!BorrowRecord.STATUS_RETURNED.equalsIgnoreCase(rec.getStatus())) {
                // Check if currently overdue
                if (rec.isCurrentlyOverdue()) {
                    rec.setStatus(BorrowRecord.STATUS_OVERDUE);
                    rec.setFineAmount(rec.calculateOverdueFine());
                }
                activeBorrows.add(rec);
                activeBorrowByPowerBankId.put(rec.getPowerbankId().toUpperCase(), rec);
            }
        }

        // 3. Load Transaction History (LinkedList)
        transactionHistory.clear();
        LinkedList<BorrowRecord> loadedTransactions = fileStorage.loadTransactions();
        transactionHistory.addAll(loadedTransactions);

        // 4. Load FIFO Queue
        borrowQueue.clear();
        List<BorrowRequest> loadedQueue = fileStorage.loadQueue();
        borrowQueue.addAll(loadedQueue);
    }

    public synchronized void persistData() {
        fileStorage.saveStudents(studentList);
        fileStorage.saveActiveBorrows(activeBorrows);
        fileStorage.saveTransactions(transactionHistory);
        fileStorage.saveQueue(new ArrayList<>(borrowQueue));
    }

    // ==========================================
    // FIFO QUEUE OPERATIONS (Data Structure 3)
    // ==========================================

    /**
     * Enqueue a new student request in strict FIFO order.
     */
    public synchronized boolean enqueueBorrowRequest(String studentId, int preferredCapacity, int durationHours, String notes) {
        Student student = getStudent(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student ID '" + studentId + "' not found.");
        }
        if (!student.canBorrow()) {
            throw new IllegalStateException("Student " + student.getName() + " has reached the maximum borrowing limit (" + student.getMaxLimit() + ").");
        }

        String reqId = "REQ-" + (System.currentTimeMillis() % 100000);
        BorrowRequest request = new BorrowRequest(
                reqId,
                student.getId(),
                student.getName(),
                preferredCapacity,
                durationHours,
                LocalDateTime.now(),
                notes
        );

        borrowQueue.offer(request); // FIFO enqueue
        persistData();
        return true;
    }

    /**
     * Inspect next request in line without removing it.
     */
    public BorrowRequest peekNextRequest() {
        return borrowQueue.peek();
    }

    /**
     * Dequeue next request in line.
     */
    public synchronized BorrowRequest dequeueNextRequest() {
        BorrowRequest req = borrowQueue.poll();
        if (req != null) {
            persistData();
        }
        return req;
    }

    /**
     * Get snapshot of waiting requests in queue order for GUI display.
     */
    public List<BorrowRequest> getQueueSnapshot() {
        return new ArrayList<>(borrowQueue);
    }

    public int getQueueSize() {
        return borrowQueue.size();
    }

    // ==========================================
    // BORROW OPERATIONS
    // ==========================================

    /**
     * Borrows a powerbank.
     * Validates:
     * - Student exists and activeBorrowCount < maxLimit
     * - Powerbank exists, is AVAILABLE, and battery >= 20%
     * - Prevents duplicate borrowing
     */
    public synchronized BorrowRecord borrowPowerBank(String studentId, String powerbankId, int durationHours) throws Exception {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be empty.");
        }
        if (powerbankId == null || powerbankId.trim().isEmpty()) {
            throw new IllegalArgumentException("PowerBank ID cannot be empty.");
        }

        Student student = getStudent(studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student ID '" + studentId + "' does not exist.");
        }
        if (!student.canBorrow()) {
            throw new IllegalStateException("Student '" + student.getName() + "' already reached maximum borrow limit of " + student.getMaxLimit() + " units.");
        }

        PowerBank powerBank = inventoryManager.getPowerBank(powerbankId);
        if (powerBank == null) {
            throw new IllegalArgumentException("PowerBank ID '" + powerbankId + "' does not exist.");
        }

        // Prevent duplicate borrowing of an already borrowed unit
        if (activeBorrowByPowerBankId.containsKey(powerBank.getId().toUpperCase()) ||
            PowerBank.STATUS_BORROWED.equalsIgnoreCase(powerBank.getStatus())) {
            throw new IllegalStateException("PowerBank '" + powerBank.getId() + "' is already borrowed by another student.");
        }

        // Check availability & battery threshold
        if (!PowerBank.STATUS_AVAILABLE.equalsIgnoreCase(powerBank.getStatus())) {
            throw new IllegalStateException("PowerBank '" + powerBank.getId() + "' is not available (Current Status: " + powerBank.getStatus() + ").");
        }
        if (powerBank.getBatteryLevel() < 20) {
            throw new IllegalStateException("PowerBank '" + powerBank.getId() + "' has low battery (" + powerBank.getBatteryLevel() + "%). Minimum 20% required for lending.");
        }

        // Verify student doesn't already have an active loan on this powerbank
        for (BorrowRecord rec : activeBorrows) {
            if (rec.getStudentId().equalsIgnoreCase(student.getId()) &&
                rec.getPowerbankId().equalsIgnoreCase(powerBank.getId())) {
                throw new IllegalStateException("Student already has an active loan for this powerbank.");
            }
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime due = now.plusHours(Math.max(1, durationHours));
        String recId = "REC-" + (1000 + (System.currentTimeMillis() % 9000));

        BorrowRecord record = new BorrowRecord(
                recId,
                student.getId(),
                student.getName(),
                powerBank.getId(),
                now,
                due,
                null,
                BorrowRecord.STATUS_ACTIVE,
                powerBank.getBatteryLevel(),
                0,
                0.0
        );

        // Update state
        powerBank.setStatus(PowerBank.STATUS_BORROWED);
        powerBank.incrementBorrowCount();
        inventoryManager.persistData();

        student.incrementBorrowCount();

        activeBorrows.add(record);
        activeBorrowByPowerBankId.put(powerBank.getId().toUpperCase(), record);

        // Add to chronological LinkedList transaction history (newest at front or back)
        transactionHistory.addFirst(record);

        persistData();
        return record;
    }

    // ==========================================
    // RETURN OPERATIONS
    // ==========================================

    /**
     * Returns a borrowed powerbank.
     * Calculates overdue duration and overdue fine automatically.
     */
    public synchronized BorrowRecord returnPowerBank(String powerbankId, int returnBatteryLevel) throws Exception {
        if (powerbankId == null || powerbankId.trim().isEmpty()) {
            throw new IllegalArgumentException("PowerBank ID cannot be empty.");
        }
        String pbId = powerbankId.trim().toUpperCase();
        BorrowRecord record = activeBorrowByPowerBankId.get(pbId);
        if (record == null) {
            // Check if there is an active borrow record in the active list
            for (BorrowRecord r : activeBorrows) {
                if (r.getPowerbankId().equalsIgnoreCase(pbId)) {
                    record = r;
                    break;
                }
            }
        }

        if (record == null) {
            throw new IllegalArgumentException("No active borrowing record found for PowerBank '" + pbId + "'.");
        }

        LocalDateTime now = LocalDateTime.now();
        record.setReturnDate(now);
        record.setReturnBatteryLevel(Math.max(0, Math.min(100, returnBatteryLevel)));

        // Overdue calculation
        if (now.isAfter(record.getDueDate())) {
            record.setStatus(BorrowRecord.STATUS_OVERDUE);
            record.setFineAmount(record.calculateOverdueFine());
        } else {
            record.setStatus(BorrowRecord.STATUS_RETURNED);
            record.setFineAmount(0.0);
        }

        // Update PowerBank
        PowerBank powerBank = inventoryManager.getPowerBank(pbId);
        if (powerBank != null) {
            powerBank.setBatteryLevel(returnBatteryLevel);
            if (returnBatteryLevel < 20) {
                // If returned with low battery, place into charging dock
                powerBank.setStatus(PowerBank.STATUS_CHARGING);
            } else {
                powerBank.setStatus(PowerBank.STATUS_AVAILABLE);
            }
            inventoryManager.persistData();
        }

        // Update Student
        Student student = getStudent(record.getStudentId());
        if (student != null) {
            student.decrementBorrowCount();
        }

        // Remove from active list
        activeBorrows.remove(record);
        activeBorrowByPowerBankId.remove(pbId);

        // Record returned event in transaction history
        transactionHistory.addFirst(record);

        persistData();
        return record;
    }

    /**
     * Periodically updates statuses of active records to OVERDUE if deadline has passed.
     */
    public synchronized void refreshOverdueStatus() {
        LocalDateTime now = LocalDateTime.now();
        boolean changed = false;
        for (BorrowRecord rec : activeBorrows) {
            if (now.isAfter(rec.getDueDate())) {
                if (!BorrowRecord.STATUS_OVERDUE.equalsIgnoreCase(rec.getStatus())) {
                    rec.setStatus(BorrowRecord.STATUS_OVERDUE);
                    changed = true;
                }
                rec.setFineAmount(rec.calculateOverdueFine());
            }
        }
        if (changed) {
            persistData();
        }
    }

    public ArrayList<BorrowRecord> getActiveBorrows() {
        refreshOverdueStatus();
        return new ArrayList<>(activeBorrows);
    }

    public ArrayList<BorrowRecord> getActiveOverdueBorrows() {
        refreshOverdueStatus();
        ArrayList<BorrowRecord> list = new ArrayList<>();
        for (BorrowRecord rec : activeBorrows) {
            if (rec.isCurrentlyOverdue() || BorrowRecord.STATUS_OVERDUE.equalsIgnoreCase(rec.getStatus())) {
                list.add(rec);
            }
        }
        return list;
    }

    /**
     * Mandatory Data Structure 4: Returns the LinkedList maintaining full transaction history.
     */
    public LinkedList<BorrowRecord> getTransactionHistory() {
        return new LinkedList<>(transactionHistory);
    }

    public int getActiveBorrowCount() {
        return activeBorrows.size();
    }

    public int getOverdueCount() {
        int count = 0;
        for (BorrowRecord rec : activeBorrows) {
            if (rec.isCurrentlyOverdue()) count++;
        }
        return count;
    }

    // ==========================================
    // STUDENT MANAGEMENT
    // ==========================================

    public Student getStudent(String studentId) {
        if (studentId == null) return null;
        return studentMap.get(studentId.trim().toUpperCase());
    }

    public ArrayList<Student> getAllStudents() {
        return new ArrayList<>(studentList);
    }

    public synchronized boolean addStudent(Student student) {
        if (student == null) throw new IllegalArgumentException("Student cannot be null.");
        if (student.getId() == null || student.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("Student ID cannot be empty.");
        }
        String id = student.getId().trim().toUpperCase();
        if (studentMap.containsKey(id)) {
            throw new IllegalArgumentException("Student ID '" + id + "' already exists.");
        }

        student.setId(id);
        studentMap.put(id, student);
        studentList.add(student);
        persistData();
        return true;
    }

    public synchronized boolean updateStudent(Student updated) {
        if (updated == null || updated.getId() == null) return false;
        String id = updated.getId().trim().toUpperCase();
        Student existing = studentMap.get(id);
        if (existing == null) {
            throw new IllegalArgumentException("Student ID '" + id + "' not found.");
        }

        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setDepartment(updated.getDepartment());
        existing.setPhone(updated.getPhone());
        existing.setMaxLimit(updated.getMaxLimit());

        persistData();
        return true;
    }

    public synchronized boolean deleteStudent(String studentId) {
        if (studentId == null) return false;
        String id = studentId.trim().toUpperCase();
        Student student = studentMap.get(id);
        if (student == null) {
            throw new IllegalArgumentException("Student ID '" + id + "' not found.");
        }
        if (student.getActiveBorrowCount() > 0) {
            throw new IllegalStateException("Cannot delete student '" + student.getName() + "' with active powerbank borrowings.");
        }

        studentMap.remove(id);
        studentList.remove(student);
        persistData();
        return true;
    }
}
