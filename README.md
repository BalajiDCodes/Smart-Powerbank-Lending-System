# ⚡ Smart Powerbank Lending System (DSA Desktop & Vercel Cloud Suite)

[![Deploy with Vercel](https://vercel.com/button)](https://vercel.com/new/clone?repository-url=https://github.com/BalajiDCodes/Smart-Powerbank-Lending-System)
[![Java Version](https://img.shields.io/badge/Java-11%2B%20%7C%2021%20%7C%2025-orange.svg)](https://openjdk.org)
[![Vercel Deployment](https://img.shields.io/badge/Vercel-Deployed%20Ready-black.svg?logo=vercel)](https://vercel.com)
[![License: MIT](https://img.shields.io/badge/License-MIT-emerald.svg)](LICENSE)
[![GitHub BalajiDCodes](https://img.shields.io/badge/GitHub-BalajiDCodes-blue.svg?logo=github)](https://github.com/BalajiDCodes/Smart-Powerbank-Lending-System)

A complete, production-ready **Smart Powerbank Lending System** available both as a high-performance **Java Swing Desktop Suite** and an interactive, real-time **Vercel Cloud Web Dashboard**. Designed for high-reliability campus powerbank rental operations, fleet battery health monitoring, automated overdue penalty management, and strict data persistence without any external database dependencies.

Built as a comprehensive demonstration of core **Computer Science Data Structures** applied to solve real-world problems.

---

## 🌐 Deploy to Vercel (1-Click & CLI)

This repository includes a native, modern, responsive cloud dashboard ready for instant zero-config deployment on Vercel:

### 1-Click Deployment via Vercel Web:
1. Log in to [Vercel Dashboard](https://vercel.com/dashboard).
2. Click **Add New...** &rarr; **Project**.
3. Import your GitHub repository: `BalajiDCodes/Smart-Powerbank-Lending-System`.
4. Leave Framework preset as **Other** (Root contains `index.html` and `vercel.json`).
5. Click **Deploy**! Your cloud dashboard is live in seconds.

### Deploy via Vercel CLI:
```bash
# Deploy instantly to Vercel Preview
npx vercel

# Deploy directly to Production
npx vercel --prod
```

---

## 🚀 Quick Execution Guide

### Pre-requisites
- **Java 11 or higher** (Tested on OpenJDK 17, 21, and 25).

### Option 1: Run with Executable JAR (Instant)
```bash
java -jar SmartPowerbankSystem.jar
```

### Option 2: Run with Shell Script (macOS / Linux)
```bash
chmod +x run.sh
./run.sh
```

### Option 3: Run on Windows
Double-click `run.bat` or run in Command Prompt / PowerShell:
```cmd
run.bat
```

### Option 4: Manual Compile & Run from Source
```bash
# 1. Compile all Java source files
javac -d bin $(find src -name "*.java")

# 2. Run Main application
java -cp bin com.powerbank.Main
```

### Option 5: Run Automated 33-Step Test Suite
```bash
chmod +x test.sh
./test.sh
```

---

## 🔐 Default Administrator Credentials

| Field | Value |
|---|---|
| **Portal** | Admin Sign In |
| **Username** | `admin` |
| **Password** | `admin123` |
| **Quick Action** | Click *"Auto-Fill Demo Credentials"* on the login screen for 1-click login during demos! |

---

## 🏛️ Mandatory Data Structures Implementation

Every data structure performs a critical and meaningful algorithmic role in the application:

| Data Structure | Implementation Class | Real-World Operational Role in System |
|---|---|---|
| **`ArrayList`** | `java.util.ArrayList<PowerBank>` in `InventoryManager` | Stores dynamic powerbank inventory for rendering table views, multi-criteria filtering (by status, battery health), and full-fleet linear scans. |
| **`HashMap`** | `java.util.HashMap<String, PowerBank>` in `InventoryManager`<br>`HashMap<String, Student>` in `BorrowingManager` | Provides $O(1)$ instantaneous retrieval of powerbanks and students by unique ID (`PB-101`, `STU-1001`), and enforces ID collision detection. |
| **`Queue` (FIFO)** | `java.util.Queue<BorrowRequest>` (LinkedList) in `BorrowingManager` | Manages student borrowing requests in strict First-In-First-Out (FIFO) sequence when inventory is constrained. The *"Dispatch Next"* button automatically allocates units in queue order. |
| **`LinkedList`** | `java.util.LinkedList<BorrowRecord>` in `BorrowingManager` | Maintains the complete chronological audit trail of all borrowing and return events. Provides $O(1)$ front insertion (`addFirst`) as new events occur. |
| **`PriorityQueue`** | `java.util.PriorityQueue<Alert>` in `AlertManager` | Prioritizes operational alerts based on multi-level severity: **Severity 1 (Critical)**: Overdue loans and Battery < 10%; **Severity 2 (Warning)**: Low battery 10%–20%; **Severity 3 (Info)**: General notices. |

---

## ⚡ Algorithms in Data Structures Implementation

In addition to core data structure storage, the system implements two fundamental algorithms:

| Algorithm | Implementation File | Time Complexity | Space Complexity | Practical Role in System |
|---|---|---|---|---|
| **Binary Search** | `FleetSearchAlgorithm.java` & `app.js` | $O(\log N)$ | $O(1)$ | Logarithmically searches the fleet array to locate an exact or optimal powerbank matching a requested target battery percentage (or capacity) in $\lceil \log_2(N) \rceil$ comparisons instead of linear $O(N)$ scan. |
| **MergeSort** | `FleetSearchAlgorithm.java` & `app.js` | $O(N \log N)$ | $O(N)$ | Custom Divide-and-Conquer sorting algorithm that recursively partitions the powerbank fleet and merges them into strict ascending order by battery level or capacity. Pre-requisite for logarithmic binary search. |

### How It Works:
1. **Desktop Suite (`Dashboard.java`)**: Click **"🔍 Binary Search O(log N)"** in the Inventory view. Enter a target battery percentage (e.g. `75%`), and the system runs the algorithm, displaying the complete step-by-step diagnostic trace ($[low .. high]$ range, $mid$ index, eliminated half, and total comparisons).
2. **Vercel Cloud Edition (`index.html` & `app.js`)**: Open the **"Algorithms (Binary Search & MergeSort)"** tab to interactively visualize pointer movements (`LOW`, `MID`, `HIGH`, `MATCH`) and view live divide-and-conquer trace logs.

---

## 📦 Application Modules Walkthrough

The suite comprises all 12 requested modules accessible through the modern Navy-Teal sidebar:

1. **Admin Login Portal (`Login.java`)**:
   - Modern navy-gradient authentication card.
   - Credential validation and instant demo filler.

2. **Dashboard Overview (`Dashboard.java`)**:
   - 5 KPI summary cards: Total Units, Available Units, Borrowed Units, Low Battery Units (<20%), Overdue Units.
   - Real-time Priority Alerts mini-table (powered by `PriorityQueue`).
   - Recent Transactions activity log (powered by `LinkedList`).
   - Live Data Structures telemetry monitor banner.

3. **Student Management (`Student.java` & `Dashboard.java`)**:
   - Register new students, update details, or delete inactive profiles.
   - Enforces student borrowing limits (maximum 2 concurrent units).

4. **Powerbank Inventory (`PowerBank.java` & `InventoryManager.java`)**:
   - Add, edit, delete, and search powerbanks.
   - Enforces unique unit IDs and validates capacity and slot locations.
   - Blocks deletion of currently borrowed units.

5. **Borrow Powerbank (`BorrowingManager.java`)**:
   - Form for issuing powerbanks to registered students.
   - Validates availability: status must be `AVAILABLE` and battery must be $\ge 20\%$.
   - Prevents duplicate loans.
   - Calculates return due date.

6. **Return Powerbank (`BorrowingManager.java`)**:
   - Process returns with recorded battery levels.
   - Automatically determines if powerbank needs charging dock assignment ($< 20\%$).
   - Automatically calculates overdue duration and fine penalties.

7. **Battery Monitoring & Health Simulator**:
   - Color-coded battery level indicators:
     - 🟢 **Green ($\ge 50\%$)**: Healthy
     - 🟡 **Amber ($20\% - 49\%$)**: Fair
     - 🔴 **Red ($< 20\%$)**: Low Battery Alert
   - One-click simulation buttons:
     - *Simulate Battery Drain (-15%)*
     - *Quick Recharge to 100%*
   - Instantly regenerates alerts and updates the `PriorityQueue`.

8. **Overdue Management**:
   - Dedicated table displaying all active loans that have passed their return deadline.
   - Real-time overdue duration calculator (formatted as `Xd Yh Zm`).
   - Automated fine calculator at $\$2.00 / \text{hour}$.
   - One-click *Send Alert Notice* and *Return Overdue Unit* actions.

9. **Low Battery Alerts & Priority Queue Center**:
   - Real-time inspection of the `PriorityQueue`.
   - Prominently displays the Peak Element (`peek()`).
   - *Process & Poll Top Alert* (`poll()`) removes the highest-priority critical item.

10. **Borrowing History**:
    - Complete sequential audit trail of all historical and active loans.
    - Searchable by Student ID, Powerbank ID, or Student Name.

11. **Transaction Management**:
    - Filter by status (`ACTIVE`, `RETURNED`, `OVERDUE`).
    - One-click *Export Audit Log (CSV)* for external spreadsheets.

12. **Executive Reports & Analytics**:
    - Real-time fleet telemetry: Fleet Utilization Rate, Availability Percentage.
    - Breakdown of data structures element counts.
    - Complete tabulated inventory snapshot.
    - One-click *Export Full Report (.txt)* to disk.

---

## 💾 File Persistence Architecture (`./data/`)

All system records are permanently saved to clean, human-readable CSV text files in the `./data/` folder upon every action. Existing data is completely restored upon restart:

```
./data/
  ├── powerbanks.csv     # PowerBank inventory units & battery percentages
  ├── students.csv       # Registered students & borrowing quota limits
  ├── borrows.csv        # Currently active powerbank loans
  ├── transactions.csv   # Historical transaction audit trail (LinkedList)
  ├── alerts.csv         # Active & resolved operational alerts
  └── waiting_queue.csv  # Pending FIFO request queue
```

*Note: If data files are missing upon launch, `FileStorage.java` automatically seeds realistic sample records (healthy units, low-battery units, active loans, and overdue loans) so the system is immediately ready for evaluation.*

---

## 🧪 Automated Verification Suite (`SystemVerificationTest.java`)

A dedicated automated test suite validates all data structures and business logic headless:

```
=================================================================
    SMART POWERBANK LENDING SYSTEM - AUTOMATED VERIFICATION      
=================================================================

--> Testing [ArrayList] Inventory Management...
  [PASS] ArrayList populated with initial inventory
  [PASS] ArrayList dynamically holds newly added PowerBank
  [PASS] ArrayList linear scan search and filter works
  [PASS] ArrayList dynamically removes deleted PowerBank
--> Testing [HashMap] O(1) Instant Lookup & Key Constraints...
  [PASS] HashMap retrieves PowerBank PB-101 in O(1) time
  [PASS] HashMap lookup is case-insensitive (PB-101 == pb-101)
  [PASS] HashMap returns null for nonexistent ID
  [PASS] HashMap detects collision and prevents duplicate ID insertion
  [PASS] Student HashMap retrieves student STU-1001 instantly
--> Testing [Queue] FIFO Waiting List for PowerBank Allocation...
  [PASS] Queue size incremented by 2
  [PASS] Queue.peek() returns head without removing
  [PASS] Queue.poll() removes elements in strict FIFO order
--> Testing [LinkedList] Chronological Transaction History Log...
  [PASS] LinkedList transaction history loaded
  [PASS] LinkedList supports O(1) front insertion of recent events
  [PASS] LinkedList size updated correctly
--> Testing [PriorityQueue] Multi-Level Severity Ordering...
  [PASS] PriorityQueue holds active alerts
  [PASS] PriorityQueue ensures Severity 1 (Critical) precedes Severity 2 (Warning) & 3 (Info)
  [PASS] PriorityQueue.peek() returns highest severity alert
--> Testing Business Rules & Borrowing Validations...
  [PASS] System strictly prevents borrowing powerbanks with battery < 20%
  [PASS] System strictly prevents borrowing already borrowed powerbanks
  [PASS] Borrow successful for available unit
  [PASS] PowerBank status updated to BORROWED
  [PASS] PowerBank borrow count incremented
  [PASS] PowerBank status restored to AVAILABLE after return
--> Testing Return Processing & Automated Overdue Calculations...
  [PASS] Overdue manager detected overdue active loans
  [PASS] Overdue record calculates overdue minutes > 0
  [PASS] Overdue fine calculated accurately (> $0.00)
  [PASS] Overdue unit return recorded with STATUS_OVERDUE retained
  [PASS] Fine amount preserved on return record
--> Testing File Persistence & Restart Restoration...
  [PASS] PowerBanks restored from CSV files on restart
  [PASS] Students restored from CSV files on restart
  [PASS] Transactions restored from CSV files on restart
  [PASS] PowerBank PB-101 intact after reload

=================================================================
  VERIFICATION RESULT: 33 / 33 TESTS PASSED (100.0% SUCCESS)
=================================================================
>>> ALL DATA STRUCTURES & BUSINESS RULES VERIFIED SUCCESSFULLY! <<<
```

---

## 🎨 UI Design System & Theme Specs

- **Palette**: Navy Blue (`#0F172A`, `#1E293B`), Teal Accent (`#0D9488`, `#14B8A6`), Surface Off-White (`#F8FAFC`, `#FFFFFF`), Slate Gray (`#64748B`, `#E2E8F0`).
- **Typography**: Clean Segoe UI / San Francisco system fonts with clear hierarchies.
- **Components**: Rounded cards, elevated stat widgets, status badges, custom table headers, alternating row renderers, and custom graphical battery bars.
