package com.powerbank.data;

import com.powerbank.model.Alert;
import com.powerbank.model.BorrowRecord;
import com.powerbank.model.BorrowRequest;
import com.powerbank.model.PowerBank;
import com.powerbank.model.Student;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles permanent persistence of all system data using CSV text files.
 * Restores data at startup and auto-initializes with demo records if missing.
 */
public class FileStorage {

    private static final String DATA_DIR = "./data";
    private static final String POWERBANKS_FILE = DATA_DIR + "/powerbanks.csv";
    private static final String STUDENTS_FILE = DATA_DIR + "/students.csv";
    private static final String BORROWS_FILE = DATA_DIR + "/borrows.csv";
    private static final String TRANSACTIONS_FILE = DATA_DIR + "/transactions.csv";
    private static final String ALERTS_FILE = DATA_DIR + "/alerts.csv";
    private static final String QUEUE_FILE = DATA_DIR + "/waiting_queue.csv";

    public FileStorage() {
        ensureDataDirectoryExists();
    }

    private void ensureDataDirectoryExists() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // ==========================================
    // POWERBANK STORAGE
    // ==========================================

    public List<PowerBank> loadPowerBanks() {
        File file = new File(POWERBANKS_FILE);
        if (!file.exists() || file.length() == 0) {
            List<PowerBank> seeds = generateInitialPowerBanks();
            savePowerBanks(seeds);
            return seeds;
        }

        List<PowerBank> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                PowerBank pb = PowerBank.fromCsvString(line);
                if (pb != null) {
                    list.add(pb);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading powerbanks file: " + e.getMessage());
        }

        if (list.isEmpty()) {
            List<PowerBank> seeds = generateInitialPowerBanks();
            savePowerBanks(seeds);
            return seeds;
        }
        return list;
    }

    public synchronized void savePowerBanks(List<PowerBank> list) {
        ensureDataDirectoryExists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(POWERBANKS_FILE))) {
            for (PowerBank pb : list) {
                writer.write(pb.toCsvString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving powerbanks: " + e.getMessage());
        }
    }

    private List<PowerBank> generateInitialPowerBanks() {
        List<PowerBank> list = new ArrayList<>();
        list.add(new PowerBank("PB-101", "Anker PowerCore 20K", 20000, 95, PowerBank.STATUS_AVAILABLE, "DOCK-A01", 14));
        list.add(new PowerBank("PB-102", "Anker PowerCore 20K", 20000, 88, PowerBank.STATUS_AVAILABLE, "DOCK-A02", 9));
        list.add(new PowerBank("PB-103", "Xiaomi Boost Pro 30W", 30000, 72, PowerBank.STATUS_AVAILABLE, "DOCK-A03", 21));
        list.add(new PowerBank("PB-104", "Baseus Blade 100W Slim", 20000, 18, PowerBank.STATUS_AVAILABLE, "DOCK-A04", 17)); // Low battery warning (<20)
        list.add(new PowerBank("PB-105", "Samsung Super Fast 25W", 10000, 8, PowerBank.STATUS_AVAILABLE, "DOCK-A05", 25)); // Critical low battery (<10)
        list.add(new PowerBank("PB-106", "Realme 30W Dart Bank", 10000, 65, PowerBank.STATUS_BORROWED, "DOCK-B01", 31));
        list.add(new PowerBank("PB-107", "Anker 737 GaNPrime 24K", 24000, 45, PowerBank.STATUS_BORROWED, "DOCK-B02", 12));
        list.add(new PowerBank("PB-108", "Belkin Boost Charge 10K", 10000, 90, PowerBank.STATUS_AVAILABLE, "DOCK-B03", 8));
        list.add(new PowerBank("PB-109", "Ugreen Nexode 145W", 25000, 100, PowerBank.STATUS_AVAILABLE, "DOCK-B04", 5));
        list.add(new PowerBank("PB-110", "Xiaomi Pocket Power 10K", 10000, 15, PowerBank.STATUS_CHARGING, "DOCK-B05", 19));
        list.add(new PowerBank("PB-111", "OnePlus 50W Warp Bank", 10000, 0, PowerBank.STATUS_MAINTENANCE, "SERVICE-BAY", 4));
        list.add(new PowerBank("PB-112", "Baseus Adaman 65W", 20000, 84, PowerBank.STATUS_AVAILABLE, "DOCK-C01", 11));
        return list;
    }

    // ==========================================
    // STUDENT STORAGE
    // ==========================================

    public List<Student> loadStudents() {
        File file = new File(STUDENTS_FILE);
        if (!file.exists() || file.length() == 0) {
            List<Student> seeds = generateInitialStudents();
            saveStudents(seeds);
            return seeds;
        }

        List<Student> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                Student st = Student.fromCsvString(line);
                if (st != null) {
                    list.add(st);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading students file: " + e.getMessage());
        }

        if (list.isEmpty()) {
            List<Student> seeds = generateInitialStudents();
            saveStudents(seeds);
            return seeds;
        }
        return list;
    }

    public synchronized void saveStudents(List<Student> list) {
        ensureDataDirectoryExists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(STUDENTS_FILE))) {
            for (Student st : list) {
                writer.write(st.toCsvString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving students: " + e.getMessage());
        }
    }

    private List<Student> generateInitialStudents() {
        List<Student> list = new ArrayList<>();
        list.add(new Student("STU-1001", "Aarav Sharma", "aarav.s@univ.edu", "Computer Science", "+91 98765 43210", 1, 2));
        list.add(new Student("STU-1002", "Diya Patel", "diya.p@univ.edu", "Information Technology", "+91 98765 43211", 1, 2));
        list.add(new Student("STU-1003", "Rohan Mehta", "rohan.m@univ.edu", "Electronics & Comm", "+91 98765 43212", 0, 2));
        list.add(new Student("STU-1004", "Ananya Verma", "ananya.v@univ.edu", "Data Science", "+91 98765 43213", 0, 2));
        list.add(new Student("STU-1005", "Kavya Nair", "kavya.n@univ.edu", "Mechanical Eng", "+91 98765 43214", 0, 2));
        list.add(new Student("STU-1006", "Vikram Singh", "vikram.s@univ.edu", "Civil Eng", "+91 98765 43215", 0, 2));
        list.add(new Student("STU-1007", "Pooja Reddy", "pooja.r@univ.edu", "Artificial Intelligence", "+91 98765 43216", 0, 2));
        list.add(new Student("STU-1008", "Aditya Joshi", "aditya.j@univ.edu", "Electrical Eng", "+91 98765 43217", 0, 2));
        return list;
    }

    // ==========================================
    // BORROW RECORDS & TRANSACTIONS STORAGE
    // ==========================================

    public List<BorrowRecord> loadActiveBorrows() {
        File file = new File(BORROWS_FILE);
        if (!file.exists() || file.length() == 0) {
            List<BorrowRecord> seeds = generateInitialActiveBorrows();
            saveActiveBorrows(seeds);
            return seeds;
        }

        List<BorrowRecord> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                BorrowRecord rec = BorrowRecord.fromCsvString(line);
                if (rec != null) {
                    list.add(rec);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading borrows file: " + e.getMessage());
        }

        if (list.isEmpty()) {
            List<BorrowRecord> seeds = generateInitialActiveBorrows();
            saveActiveBorrows(seeds);
            return seeds;
        }
        return list;
    }

    public synchronized void saveActiveBorrows(List<BorrowRecord> list) {
        ensureDataDirectoryExists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BORROWS_FILE))) {
            for (BorrowRecord rec : list) {
                writer.write(rec.toCsvString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving borrows: " + e.getMessage());
        }
    }

    private List<BorrowRecord> generateInitialActiveBorrows() {
        List<BorrowRecord> list = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Active Borrow 1: Healthy active loan
        BorrowRecord rec1 = new BorrowRecord(
                "REC-5001",
                "STU-1001",
                "Aarav Sharma",
                "PB-106",
                now.minusHours(1),
                now.plusHours(3),
                null,
                BorrowRecord.STATUS_ACTIVE,
                90,
                0,
                0.0
        );

        // Active Borrow 2: OVERDUE loan (due 2 hours ago!) -> demonstrates Overdue module!
        BorrowRecord rec2 = new BorrowRecord(
                "REC-5002",
                "STU-1002",
                "Diya Patel",
                "PB-107",
                now.minusHours(6),
                now.minusHours(2), // Overdue by 2 hours
                null,
                BorrowRecord.STATUS_OVERDUE,
                80,
                0,
                4.0 // $4 fine
        );

        list.add(rec1);
        list.add(rec2);
        return list;
    }

    public LinkedList<BorrowRecord> loadTransactions() {
        File file = new File(TRANSACTIONS_FILE);
        LinkedList<BorrowRecord> list = new LinkedList<>();

        if (!file.exists() || file.length() == 0) {
            list = generateInitialTransactions();
            saveTransactions(list);
            return list;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                BorrowRecord rec = BorrowRecord.fromCsvString(line);
                if (rec != null) {
                    list.add(rec);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading transactions file: " + e.getMessage());
        }

        if (list.isEmpty()) {
            list = generateInitialTransactions();
            saveTransactions(list);
        }
        return list;
    }

    public synchronized void saveTransactions(LinkedList<BorrowRecord> list) {
        ensureDataDirectoryExists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTIONS_FILE))) {
            for (BorrowRecord rec : list) {
                writer.write(rec.toCsvString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving transactions: " + e.getMessage());
        }
    }

    private LinkedList<BorrowRecord> generateInitialTransactions() {
        LinkedList<BorrowRecord> list = new LinkedList<>();
        LocalDateTime now = LocalDateTime.now();

        // Past returned transaction 1
        list.add(new BorrowRecord(
                "REC-4998",
                "STU-1003",
                "Rohan Mehta",
                "PB-101",
                now.minusDays(2).minusHours(4),
                now.minusDays(2),
                now.minusDays(2).minusMinutes(10),
                BorrowRecord.STATUS_RETURNED,
                100,
                68,
                0.0
        ));

        // Past returned transaction 2 (was slightly overdue)
        list.add(new BorrowRecord(
                "REC-4999",
                "STU-1004",
                "Ananya Verma",
                "PB-103",
                now.minusDays(1).minusHours(5),
                now.minusDays(1).minusHours(1),
                now.minusDays(1),
                BorrowRecord.STATUS_RETURNED,
                95,
                42,
                2.0
        ));

        // Also add the active seed records to history
        list.add(new BorrowRecord(
                "REC-5001",
                "STU-1001",
                "Aarav Sharma",
                "PB-106",
                now.minusHours(1),
                now.plusHours(3),
                null,
                BorrowRecord.STATUS_ACTIVE,
                90,
                0,
                0.0
        ));

        list.add(new BorrowRecord(
                "REC-5002",
                "STU-1002",
                "Diya Patel",
                "PB-107",
                now.minusHours(6),
                now.minusHours(2),
                null,
                BorrowRecord.STATUS_OVERDUE,
                80,
                0,
                4.0
        ));

        return list;
    }

    // ==========================================
    // FIFO WAITING QUEUE STORAGE
    // ==========================================

    public List<BorrowRequest> loadQueue() {
        File file = new File(QUEUE_FILE);
        List<BorrowRequest> list = new ArrayList<>();
        if (!file.exists() || file.length() == 0) {
            list = generateInitialQueue();
            saveQueue(list);
            return list;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                BorrowRequest req = BorrowRequest.fromCsvString(line);
                if (req != null) {
                    list.add(req);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading queue file: " + e.getMessage());
        }

        if (list.isEmpty()) {
            list = generateInitialQueue();
            saveQueue(list);
        }
        return list;
    }

    public synchronized void saveQueue(List<BorrowRequest> list) {
        ensureDataDirectoryExists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(QUEUE_FILE))) {
            for (BorrowRequest req : list) {
                writer.write(req.toCsvString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving queue: " + e.getMessage());
        }
    }

    private List<BorrowRequest> generateInitialQueue() {
        List<BorrowRequest> list = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        list.add(new BorrowRequest("REQ-101", "STU-1005", "Kavya Nair", 20000, 3, now.minusMinutes(25), "Needs high capacity for campus hackathon"));
        list.add(new BorrowRequest("REQ-102", "STU-1006", "Vikram Singh", 10000, 2, now.minusMinutes(10), "Exam preparation library slot"));
        return list;
    }

    // ==========================================
    // ALERTS STORAGE
    // ==========================================

    public List<Alert> loadAlerts() {
        File file = new File(ALERTS_FILE);
        List<Alert> list = new ArrayList<>();
        if (!file.exists() || file.length() == 0) {
            // Fresh alerts are generated dynamically from current state
            return list;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                Alert alert = Alert.fromCsvString(line);
                if (alert != null) {
                    list.add(alert);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading alerts file: " + e.getMessage());
        }
        return list;
    }

    public synchronized void saveAlerts(List<Alert> list) {
        ensureDataDirectoryExists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ALERTS_FILE))) {
            for (Alert alert : list) {
                writer.write(alert.toCsvString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving alerts: " + e.getMessage());
        }
    }
}
