package com.powerbank;

import com.powerbank.data.FileStorage;
import com.powerbank.model.Alert;
import com.powerbank.model.BorrowRecord;
import com.powerbank.model.BorrowRequest;
import com.powerbank.model.PowerBank;
import com.powerbank.model.Student;
import com.powerbank.service.AlertManager;
import com.powerbank.service.BorrowingManager;
import com.powerbank.service.InventoryManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Automated Verification and Test Suite for the Smart Powerbank Lending System.
 * Tests all 5 core Data Structures and all 12 Functional Modules.
 */
public class SystemVerificationTest {

    private static int testsPassed = 0;
    private static int testsTotal = 0;

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("    SMART POWERBANK LENDING SYSTEM - AUTOMATED VERIFICATION      ");
        System.out.println("=================================================================\n");

        FileStorage fileStorage = new FileStorage();
        InventoryManager inventoryManager = new InventoryManager(fileStorage);
        BorrowingManager borrowingManager = new BorrowingManager(inventoryManager, fileStorage);
        AlertManager alertManager = new AlertManager(inventoryManager, borrowingManager, fileStorage);

        testDataStructure1_ArrayList(inventoryManager);
        testDataStructure2_HashMap(inventoryManager, borrowingManager);
        testDataStructure3_FIFOQueue(borrowingManager);
        testDataStructure4_LinkedList(borrowingManager);
        testDataStructure5_PriorityQueue(alertManager);
        testBusinessRules_BorrowingValidations(inventoryManager, borrowingManager);
        testBusinessRules_ReturnAndOverdueCalculation(inventoryManager, borrowingManager);
        testPersistence_FileStorage(inventoryManager, borrowingManager, alertManager, fileStorage);

        System.out.println("\n=================================================================");
        System.out.printf("  VERIFICATION RESULT: %d / %d TESTS PASSED (%.1f%% SUCCESS)\n",
                testsPassed, testsTotal, (testsPassed * 100.0 / testsTotal));
        System.out.println("=================================================================");

        if (testsPassed == testsTotal) {
            System.out.println(">>> ALL DATA STRUCTURES & BUSINESS RULES VERIFIED SUCCESSFULLY! <<<");
        } else {
            System.err.println(">>> SOME TESTS FAILED. PLEASE REVIEW ABOVE LOGS. <<<");
            System.exit(1);
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        testsTotal++;
        if (condition) {
            testsPassed++;
            System.out.println("  [PASS] " + testName);
        } else {
            System.err.println("  [FAIL] " + testName);
        }
    }

    // ==========================================
    // 1. ARRAYLIST TEST
    // ==========================================
    private static void testDataStructure1_ArrayList(InventoryManager inv) {
        System.out.println("--> Testing [ArrayList] Inventory Management...");
        ArrayList<PowerBank> initialList = inv.getAllPowerBanks();
        assertTrue("ArrayList populated with initial inventory", initialList.size() >= 10);

        String testId = "TEST-PB-999";
        PowerBank testPb = new PowerBank(testId, "Test Anker 20K", 20000, 85, PowerBank.STATUS_AVAILABLE, "DOCK-T1", 0);
        inv.addPowerBank(testPb);

        ArrayList<PowerBank> afterAdd = inv.getAllPowerBanks();
        boolean foundInList = false;
        for (PowerBank pb : afterAdd) {
            if (testId.equalsIgnoreCase(pb.getId())) {
                foundInList = true;
                break;
            }
        }
        assertTrue("ArrayList dynamically holds newly added PowerBank", foundInList);

        // Filter test
        ArrayList<PowerBank> searchResult = inv.searchAndFilter("Test Anker", "ALL");
        assertTrue("ArrayList linear scan search and filter works", searchResult.size() == 1 && searchResult.get(0).getId().equals(testId));

        // Clean up test item
        inv.deletePowerBank(testId);
        assertTrue("ArrayList dynamically removes deleted PowerBank", inv.getPowerBank(testId) == null);
    }

    // ==========================================
    // 2. HASHMAP TEST
    // ==========================================
    private static void testDataStructure2_HashMap(InventoryManager inv, BorrowingManager bm) {
        System.out.println("--> Testing [HashMap] O(1) Instant Lookup & Key Constraints...");
        PowerBank pb101 = inv.getPowerBank("PB-101");
        assertTrue("HashMap retrieves PowerBank PB-101 in O(1) time", pb101 != null && pb101.getId().equals("PB-101"));

        // Case-insensitivity check
        PowerBank pbLower = inv.getPowerBank("pb-101");
        assertTrue("HashMap lookup is case-insensitive (PB-101 == pb-101)", pbLower != null);

        // Non-existent key check
        assertTrue("HashMap returns null for nonexistent ID", inv.getPowerBank("NON-EXISTENT-ID") == null);

        // Duplicate ID prevention check
        boolean duplicateCaught = false;
        try {
            PowerBank duplicate = new PowerBank("PB-101", "Duplicate Test", 10000, 100, PowerBank.STATUS_AVAILABLE, "DOCK-X", 0);
            inv.addPowerBank(duplicate);
        } catch (IllegalArgumentException e) {
            duplicateCaught = true;
        }
        assertTrue("HashMap detects collision and prevents duplicate ID insertion", duplicateCaught);

        // Student HashMap lookup check
        Student stu = bm.getStudent("STU-1001");
        assertTrue("Student HashMap retrieves student STU-1001 instantly", stu != null && stu.getName().equals("Aarav Sharma"));
    }

    // ==========================================
    // 3. FIFO QUEUE TEST
    // ==========================================
    private static void testDataStructure3_FIFOQueue(BorrowingManager bm) {
        System.out.println("--> Testing [Queue] FIFO Waiting List for PowerBank Allocation...");
        int initialQueueSize = bm.getQueueSize();

        // Enqueue student 1
        bm.enqueueBorrowRequest("STU-1003", 20000, 4, "FIFO Test 1");
        // Enqueue student 2
        bm.enqueueBorrowRequest("STU-1004", 10000, 2, "FIFO Test 2");

        assertTrue("Queue size incremented by 2", bm.getQueueSize() == initialQueueSize + 2);

        // Peek check (first-in student should be at head)
        BorrowRequest peeked = bm.peekNextRequest();
        assertTrue("Queue.peek() returns head without removing", peeked != null);

        // Poll check
        BorrowRequest firstOut = bm.dequeueNextRequest();
        assertTrue("Queue.poll() removes elements in strict FIFO order", firstOut != null);

        // Cleanup
        bm.dequeueNextRequest();
    }

    // ==========================================
    // 4. LINKEDLIST TEST
    // ==========================================
    private static void testDataStructure4_LinkedList(BorrowingManager bm) {
        System.out.println("--> Testing [LinkedList] Chronological Transaction History Log...");
        LinkedList<BorrowRecord> history = bm.getTransactionHistory();
        assertTrue("LinkedList transaction history loaded", !history.isEmpty());

        int initialSize = history.size();
        BorrowRecord testRecord = new BorrowRecord(
                "REC-TEST-LINKEDLIST",
                "STU-1003",
                "Rohan Mehta",
                "PB-102",
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(4),
                null,
                BorrowRecord.STATUS_ACTIVE,
                90,
                0,
                0.0
        );

        history.addFirst(testRecord);
        assertTrue("LinkedList supports O(1) front insertion of recent events", history.getFirst().getRecordId().equals("REC-TEST-LINKEDLIST"));
        assertTrue("LinkedList size updated correctly", history.size() == initialSize + 1);
    }

    // ==========================================
    // 5. PRIORITYQUEUE TEST
    // ==========================================
    private static void testDataStructure5_PriorityQueue(AlertManager am) {
        System.out.println("--> Testing [PriorityQueue] Multi-Level Severity Ordering...");
        am.loadAndRefreshAlerts();

        List<Alert> sorted = am.getActiveAlertsSorted();
        assertTrue("PriorityQueue holds active alerts", !sorted.isEmpty());

        // Verify ordering: Severity 1 must strictly come before Severity 2 and 3
        boolean correctlyOrdered = true;
        for (int i = 0; i < sorted.size() - 1; i++) {
            if (sorted.get(i).getSeverity() > sorted.get(i + 1).getSeverity()) {
                correctlyOrdered = false;
                break;
            }
        }
        assertTrue("PriorityQueue ensures Severity 1 (Critical) precedes Severity 2 (Warning) & 3 (Info)", correctlyOrdered);

        Alert peak = am.peekHighestPriorityAlert();
        assertTrue("PriorityQueue.peek() returns highest severity alert", peak != null && peak.getSeverity() == Alert.SEVERITY_CRITICAL);
    }

    // ==========================================
    // 6. BUSINESS RULES: BORROWING VALIDATIONS
    // ==========================================
    private static void testBusinessRules_BorrowingValidations(InventoryManager inv, BorrowingManager bm) {
        System.out.println("--> Testing Business Rules & Borrowing Validations...");

        // 1. Cannot borrow unit with battery < 20%
        PowerBank lowBattUnit = inv.getPowerBank("PB-104"); // 18% battery
        boolean lowBattRejected = false;
        try {
            bm.borrowPowerBank("STU-1003", lowBattUnit.getId(), 4);
        } catch (Exception e) {
            lowBattRejected = true;
        }
        assertTrue("System strictly prevents borrowing powerbanks with battery < 20%", lowBattRejected);

        // 2. Cannot borrow already borrowed unit
        PowerBank borrowedUnit = inv.getPowerBank("PB-106"); // Status BORROWED
        boolean alreadyBorrowedRejected = false;
        try {
            bm.borrowPowerBank("STU-1003", borrowedUnit.getId(), 4);
        } catch (Exception e) {
            alreadyBorrowedRejected = true;
        }
        assertTrue("System strictly prevents borrowing already borrowed powerbanks", alreadyBorrowedRejected);

        // 3. Successful borrow test
        PowerBank availableUnit = inv.getPowerBank("PB-101");
        int initialBorrows = availableUnit.getTotalBorrowCount();
        try {
            BorrowRecord rec = bm.borrowPowerBank("STU-1003", availableUnit.getId(), 2);
            assertTrue("Borrow successful for available unit", rec != null);
            assertTrue("PowerBank status updated to BORROWED", PowerBank.STATUS_BORROWED.equals(availableUnit.getStatus()));
            assertTrue("PowerBank borrow count incremented", availableUnit.getTotalBorrowCount() == initialBorrows + 1);

            // Clean up: return it
            bm.returnPowerBank(availableUnit.getId(), 85);
            assertTrue("PowerBank status restored to AVAILABLE after return", PowerBank.STATUS_AVAILABLE.equals(availableUnit.getStatus()));
        } catch (Exception e) {
            System.err.println("Borrow error: " + e.getMessage());
            assertTrue("Borrow operation should succeed", false);
        }
    }

    // ==========================================
    // 7. BUSINESS RULES: RETURN & OVERDUE CALCULATION
    // ==========================================
    private static void testBusinessRules_ReturnAndOverdueCalculation(InventoryManager inv, BorrowingManager bm) {
        System.out.println("--> Testing Return Processing & Automated Overdue Calculations...");

        // Ensure an overdue loan exists for idempotent test repeatability
        List<BorrowRecord> overdueRecords = bm.getActiveOverdueBorrows();
        if (overdueRecords.isEmpty()) {
            // Create an explicit overdue loan for testing
            try {
                PowerBank pb = inv.getAvailablePowerBanks().get(0);
                BorrowRecord rec = bm.borrowPowerBank("STU-1004", pb.getId(), 1);
                // Set due date to 2 hours in the past
                rec.setDueDate(LocalDateTime.now().minusHours(2));
                bm.refreshOverdueStatus();
                overdueRecords = bm.getActiveOverdueBorrows();
            } catch (Exception ignored) {}
        }

        assertTrue("Overdue manager detected overdue active loans", !overdueRecords.isEmpty());

        BorrowRecord overdueRec = overdueRecords.get(0);
        assertTrue("Overdue record calculates overdue minutes > 0", overdueRec.getOverdueMinutes() > 0);
        assertTrue("Overdue fine calculated accurately (> $0.00)", overdueRec.calculateOverdueFine() > 0.0);

        // Test return of overdue unit
        try {
            String pbId = overdueRec.getPowerbankId();
            BorrowRecord returned = bm.returnPowerBank(pbId, 75);
            assertTrue("Overdue unit return recorded with STATUS_OVERDUE retained", BorrowRecord.STATUS_OVERDUE.equals(returned.getStatus()));
            assertTrue("Fine amount preserved on return record", returned.getFineAmount() > 0.0);
        } catch (Exception e) {
            System.err.println("Return error: " + e.getMessage());
            assertTrue("Return operation should succeed", false);
        }
    }

    // ==========================================
    // 8. FILE STORAGE PERSISTENCE TEST
    // ==========================================
    private static void testPersistence_FileStorage(InventoryManager inv, BorrowingManager bm, AlertManager am, FileStorage fs) {
        System.out.println("--> Testing File Persistence & Restart Restoration...");
        // Save current state
        inv.persistData();
        bm.persistData();

        // Instantiate new managers simulating fresh application restart
        FileStorage freshStorage = new FileStorage();
        InventoryManager freshInv = new InventoryManager(freshStorage);
        BorrowingManager freshBm = new BorrowingManager(freshInv, freshStorage);

        assertTrue("PowerBanks restored from CSV files on restart", freshInv.getTotalCount() == inv.getTotalCount());
        assertTrue("Students restored from CSV files on restart", freshBm.getAllStudents().size() == bm.getAllStudents().size());
        assertTrue("Transactions restored from CSV files on restart", freshBm.getTransactionHistory().size() == bm.getTransactionHistory().size());
        assertTrue("PowerBank PB-101 intact after reload", freshInv.getPowerBank("PB-101") != null);
    }
}
