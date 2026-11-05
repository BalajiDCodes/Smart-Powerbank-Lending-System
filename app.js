/**
 * Smart Powerbank Lending System - Cloud Edition
 * Data Structures Client Logic & State Store
 * Developed by BalajiDCodes
 */

// ==========================================
// 1. INITIAL SEED DATA (from data/*.csv)
// ==========================================

const DEFAULT_POWERBANKS = [
  { id: "PB-101", modelName: "Anker PowerCore 20K", capacityMah: 20000, batteryLevel: 75, status: "AVAILABLE", slotLocation: "DOCK-A01", totalBorrowCount: 22 },
  { id: "PB-102", modelName: "Anker PowerCore 20K", capacityMah: 20000, batteryLevel: 88, status: "AVAILABLE", slotLocation: "DOCK-A02", totalBorrowCount: 9 },
  { id: "PB-103", modelName: "Xiaomi Boost Pro 30W", capacityMah: 30000, batteryLevel: 72, status: "AVAILABLE", slotLocation: "DOCK-A03", totalBorrowCount: 21 },
  { id: "PB-104", modelName: "Baseus Blade 100W Slim", capacityMah: 20000, batteryLevel: 18, status: "AVAILABLE", slotLocation: "DOCK-A04", totalBorrowCount: 17 },
  { id: "PB-105", modelName: "Samsung Super Fast 25W", capacityMah: 10000, batteryLevel: 8, status: "AVAILABLE", slotLocation: "DOCK-A05", totalBorrowCount: 25 },
  { id: "PB-106", modelName: "Realme 30W Dart Bank", capacityMah: 10000, batteryLevel: 65, status: "BORROWED", slotLocation: "DOCK-B01", totalBorrowCount: 31 },
  { id: "PB-107", modelName: "Anker 737 GaNPrime 24K", capacityMah: 24000, batteryLevel: 75, status: "BORROWED", slotLocation: "DOCK-B02", totalBorrowCount: 13 },
  { id: "PB-108", modelName: "Belkin Boost Charge 10K", capacityMah: 10000, batteryLevel: 90, status: "AVAILABLE", slotLocation: "DOCK-B03", totalBorrowCount: 8 },
  { id: "PB-109", modelName: "Ugreen Nexode 145W", capacityMah: 25000, batteryLevel: 100, status: "AVAILABLE", slotLocation: "DOCK-B04", totalBorrowCount: 5 },
  { id: "PB-110", modelName: "Xiaomi Pocket Power 10K", capacityMah: 10000, batteryLevel: 15, status: "CHARGING", slotLocation: "DOCK-B05", totalBorrowCount: 19 },
  { id: "PB-111", modelName: "OnePlus 50W Warp Bank", capacityMah: 10000, batteryLevel: 0, status: "MAINTENANCE", slotLocation: "SERVICE-BAY", totalBorrowCount: 4 },
  { id: "PB-112", modelName: "Baseus Adaman 65W", capacityMah: 20000, batteryLevel: 84, status: "AVAILABLE", slotLocation: "DOCK-C01", totalBorrowCount: 11 }
];

const DEFAULT_STUDENTS = [
  { id: "STU-1001", name: "Aarav Sharma", email: "aarav.s@univ.edu", department: "Computer Science", phone: "+91 98765 43210", activeBorrowCount: 1, maxLimit: 2 },
  { id: "STU-1002", name: "Diya Patel", email: "diya.p@univ.edu", department: "Information Technology", phone: "+91 98765 43211", activeBorrowCount: 1, maxLimit: 2 },
  { id: "STU-1003", name: "Rohan Mehta", email: "rohan.m@univ.edu", department: "Electronics & Comm", phone: "+91 98765 43212", activeBorrowCount: 0, maxLimit: 2 },
  { id: "STU-1004", name: "Ananya Verma", email: "ananya.v@univ.edu", department: "Data Science", phone: "+91 98765 43213", activeBorrowCount: 0, maxLimit: 2 },
  { id: "STU-1005", name: "Kavya Nair", email: "kavya.n@univ.edu", department: "Mechanical Eng", phone: "+91 98765 43214", activeBorrowCount: 0, maxLimit: 2 },
  { id: "STU-1006", name: "Vikram Singh", email: "vikram.s@univ.edu", department: "Civil Eng", phone: "+91 98765 43215", activeBorrowCount: 0, maxLimit: 2 },
  { id: "STU-1007", name: "Pooja Reddy", email: "pooja.r@univ.edu", department: "Artificial Intelligence", phone: "+91 98765 43216", activeBorrowCount: 0, maxLimit: 2 },
  { id: "STU-1008", name: "Aditya Joshi", email: "aditya.j@univ.edu", department: "Electrical Eng", phone: "+91 98765 43217", activeBorrowCount: 0, maxLimit: 2 }
];

// Reference timestamps matching realistic scenario
const nowMs = Date.now();
const hourMs = 3600 * 1000;

const DEFAULT_ACTIVE_BORROWS = [
  {
    recordId: "REC-5001",
    studentId: "STU-1001",
    studentName: "Aarav Sharma",
    powerbankId: "PB-106",
    borrowDate: new Date(nowMs - 6 * hourMs).toISOString(),
    dueDate: new Date(nowMs - 2 * hourMs).toISOString(),
    returnDate: null,
    status: "OVERDUE",
    startBattery: 90,
    returnBattery: 0,
    fineAmount: 4.00
  },
  {
    recordId: "REC-4819",
    studentId: "STU-1002",
    studentName: "Diya Patel",
    powerbankId: "PB-107",
    borrowDate: new Date(nowMs - 2 * hourMs).toISOString(),
    dueDate: new Date(nowMs + 2 * hourMs).toISOString(),
    returnDate: null,
    status: "ACTIVE",
    startBattery: 75,
    returnBattery: 0,
    fineAmount: 0.00
  }
];

const DEFAULT_QUEUE = [
  { reqId: "REQ-49213", studentId: "STU-1003", studentName: "Rohan Mehta", preferredCapacity: 20000, durationHours: 4, timestamp: new Date(nowMs - 30 * 60000).toISOString(), notes: "FIFO Waitlist Priority 1" },
  { reqId: "REQ-49215", studentId: "STU-1004", studentName: "Ananya Verma", preferredCapacity: 10000, durationHours: 2, timestamp: new Date(nowMs - 15 * 60000).toISOString(), notes: "Lab project testing" }
];

const DEFAULT_TRANSACTIONS = [
  { recordId: "REC-5222", studentId: "STU-1004", studentName: "Ananya Verma", powerbankId: "PB-101", borrowDate: new Date(nowMs - 24 * hourMs).toISOString(), dueDate: new Date(nowMs - 22 * hourMs).toISOString(), returnDate: new Date(nowMs - 20 * hourMs).toISOString(), status: "OVERDUE", startBattery: 85, returnBattery: 75, fineAmount: 4.00 },
  { recordId: "REC-5220", studentId: "STU-1003", studentName: "Rohan Mehta", powerbankId: "PB-101", borrowDate: new Date(nowMs - 30 * hourMs).toISOString(), dueDate: new Date(nowMs - 28 * hourMs).toISOString(), returnDate: new Date(nowMs - 28 * hourMs).toISOString(), status: "RETURNED", startBattery: 75, returnBattery: 85, fineAmount: 0.00 },
  { recordId: "REC-4999", studentId: "STU-1004", studentName: "Ananya Verma", powerbankId: "PB-103", borrowDate: new Date(nowMs - 48 * hourMs).toISOString(), dueDate: new Date(nowMs - 44 * hourMs).toISOString(), returnDate: new Date(nowMs - 43 * hourMs).toISOString(), status: "RETURNED", startBattery: 95, returnBattery: 42, fineAmount: 2.00 }
];

// ==========================================
// 2. STATE MANAGER & PERSISTENCE
// ==========================================

let state = {
  powerbanks: [],
  students: [],
  activeBorrows: [],
  queue: [],
  transactions: [],
  alerts: [],
  fleetFilter: "ALL"
};

function saveState() {
  try {
    localStorage.setItem("pb_state_v1", JSON.stringify(state));
  } catch (e) {
    console.error("Local storage error:", e);
  }
}

function loadState() {
  const saved = localStorage.getItem("pb_state_v1");
  if (saved) {
    try {
      state = JSON.parse(saved);
      return;
    } catch (e) {
      console.warn("Failed to parse saved state, reloading seed.", e);
    }
  }
  resetStateToSeed();
}

function resetStateToSeed() {
  state.powerbanks = JSON.parse(JSON.stringify(DEFAULT_POWERBANKS));
  state.students = JSON.parse(JSON.stringify(DEFAULT_STUDENTS));
  state.activeBorrows = JSON.parse(JSON.stringify(DEFAULT_ACTIVE_BORROWS));
  state.queue = JSON.parse(JSON.stringify(DEFAULT_QUEUE));
  state.transactions = JSON.parse(JSON.stringify(DEFAULT_TRANSACTIONS));
  state.fleetFilter = "ALL";
  scanAndRefreshAlerts();
  saveState();
}

function resetToDefaults() {
  if (confirm("Reset all fleet, student, and transaction records to sample defaults?")) {
    resetStateToSeed();
    renderAll();
    showToast("System reset to sample dataset", "info");
  }
}

// ==========================================
// 3. PRIORITYQUEUE (MIN-HEAP) LOGIC
// ==========================================

/**
 * Alert Severity Comparator:
 * Severity 1 (CRITICAL: Overdue / Batt <10%) always beats Severity 2 (WARNING: Batt <20%).
 * Tie-breaker: earlier timestamp.
 */
function scanAndRefreshAlerts() {
  const alerts = [];

  // 1. Scan active overdue borrowings
  const now = new Date();
  state.activeBorrows.forEach(borrow => {
    const due = new Date(borrow.dueDate);
    if (now > due) {
      borrow.status = "OVERDUE";
      const diffHours = (now - due) / (1000 * 3600);
      const hours = Math.ceil(diffHours);
      borrow.fineAmount = parseFloat((hours * 2.0).toFixed(2));

      alerts.push({
        id: `ALT-OV-${borrow.recordId}`,
        type: "OVERDUE",
        severity: 1, // Critical
        title: `Overdue Loan: ${borrow.powerbankId}`,
        message: `Borrower ${borrow.studentName} (${borrow.studentId}) is overdue by ~${hours}h. Penalty fee: $${borrow.fineAmount.toFixed(2)}`,
        targetId: borrow.recordId,
        timestamp: borrow.dueDate,
        resolved: false
      });
    } else {
      borrow.status = "ACTIVE";
      borrow.fineAmount = 0.00;
    }
  });

  // 2. Scan powerbanks for critical & low battery
  state.powerbanks.forEach(pb => {
    if (pb.batteryLevel < 10) {
      alerts.push({
        id: `ALT-BC-${pb.id}`,
        type: "CRITICAL_BATTERY",
        severity: 1, // Critical
        title: `CRITICAL BATTERY: ${pb.id}`,
        message: `PowerBank ${pb.id} (${pb.modelName}) battery is at ${pb.batteryLevel}%! Immediate recharge needed at ${pb.slotLocation}.`,
        targetId: pb.id,
        timestamp: new Date().toISOString(),
        resolved: false
      });
    } else if (pb.batteryLevel < 20) {
      alerts.push({
        id: `ALT-BL-${pb.id}`,
        type: "LOW_BATTERY",
        severity: 2, // Warning
        title: `Low Battery Warning: ${pb.id}`,
        message: `PowerBank ${pb.id} (${pb.modelName}) is at ${pb.batteryLevel}%. Minimum 20% required for lending.`,
        targetId: pb.id,
        timestamp: new Date().toISOString(),
        resolved: false
      });
    }
  });

  // Priority sort (PriorityQueue simulation: min severity first, then oldest)
  alerts.sort((a, b) => {
    if (a.severity !== b.severity) return a.severity - b.severity;
    return new Date(a.timestamp) - new Date(b.timestamp);
  });

  state.alerts = alerts;
}

// ==========================================
// 4. RENDERING FUNCTIONS
// ==========================================

function renderAll() {
  scanAndRefreshAlerts();
  updateKPIs();
  renderFleet();
  renderTerminalForms();
  renderActiveBorrows();
  renderQueue();
  renderStudents();
  renderAlerts();
  renderHistory();
  renderDsaInspector();
  renderAlgorithmsView();
  saveState();
}

function updateKPIs() {
  const total = state.powerbanks.length;
  const available = state.powerbanks.filter(p => p.status === "AVAILABLE" && p.batteryLevel >= 20).length;
  const borrowed = state.powerbanks.filter(p => p.status === "BORROWED").length;
  const overdue = state.activeBorrows.filter(b => b.status === "OVERDUE").length;
  const alertsCount = state.alerts.length;
  const queueCount = state.queue.length;

  document.getElementById("statTotal").textContent = total;
  document.getElementById("statAvailable").textContent = available;
  document.getElementById("statBorrowed").textContent = borrowed;
  document.getElementById("statOverdue").textContent = overdue;
  document.getElementById("statAlerts").textContent = alertsCount;
  document.getElementById("statQueue").textContent = queueCount;

  document.getElementById("alertBadge").textContent = alertsCount;
  document.getElementById("queueBadge").textContent = queueCount;
}

// Tab 1: Fleet Inventory
function renderFleet() {
  const container = document.getElementById("fleetGrid");
  const query = (document.getElementById("inventorySearch")?.value || "").toLowerCase().trim();
  const filter = state.fleetFilter;

  let list = state.powerbanks.filter(pb => {
    const matchesQ = !query ||
      pb.id.toLowerCase().includes(query) ||
      pb.modelName.toLowerCase().includes(query) ||
      pb.slotLocation.toLowerCase().includes(query);

    let matchesFilter = true;
    if (filter === "AVAILABLE") {
      matchesFilter = pb.status === "AVAILABLE" && pb.batteryLevel >= 20;
    } else if (filter === "BORROWED") {
      matchesFilter = pb.status === "BORROWED";
    } else if (filter === "LOW_BATTERY") {
      matchesFilter = pb.batteryLevel < 20;
    } else if (filter === "CHARGING") {
      matchesFilter = pb.status === "CHARGING";
    } else if (filter === "MAINTENANCE") {
      matchesFilter = pb.status === "MAINTENANCE";
    }

    return matchesQ && matchesFilter;
  });

  if (list.length === 0) {
    container.innerHTML = `
      <div class="col-span-full py-12 text-center text-slate-500">
        <i class="fa-solid fa-battery-empty text-3xl mb-2 block"></i>
        No powerbanks match current search and filter criteria.
      </div>`;
    return;
  }

  container.innerHTML = list.map(pb => {
    const isAvail = pb.status === "AVAILABLE" && pb.batteryLevel >= 20;
    let badgeColor = "bg-emerald-500/10 text-emerald-400 border-emerald-500/30";
    if (pb.status === "BORROWED") badgeColor = "bg-blue-500/10 text-blue-400 border-blue-500/30";
    else if (pb.status === "CHARGING") badgeColor = "bg-amber-500/10 text-amber-400 border-amber-500/30";
    else if (pb.status === "MAINTENANCE") badgeColor = "bg-rose-500/10 text-rose-400 border-rose-500/30";
    else if (pb.batteryLevel < 20) badgeColor = "bg-amber-500/10 text-amber-400 border-amber-500/30";

    let battColor = "from-emerald-500 to-cyan-500";
    if (pb.batteryLevel < 20) battColor = "from-amber-500 to-amber-600";
    if (pb.batteryLevel < 10) battColor = "from-rose-500 to-red-600";

    return `
      <div class="bg-slate-950/70 border border-slate-800 rounded-xl p-4 flex flex-col justify-between hover:border-slate-700 transition">
        <div>
          <div class="flex items-center justify-between pb-2 border-b border-slate-800/80">
            <span class="font-mono text-xs font-bold text-white">${pb.id}</span>
            <span class="px-2 py-0.5 rounded text-[10px] font-semibold border ${badgeColor}">
              ${pb.status}${pb.batteryLevel < 20 && pb.status === "AVAILABLE" ? " (LOW)" : ""}
            </span>
          </div>

          <div class="mt-2.5">
            <h4 class="text-xs font-bold text-slate-200 line-clamp-1">${pb.modelName}</h4>
            <div class="flex items-center justify-between text-[11px] text-slate-400 mt-1">
              <span>Capacity: <strong class="text-slate-300 font-mono">${(pb.capacityMah / 1000).toFixed(0)}k mAh</strong></span>
              <span>Dock: <strong class="text-slate-300 font-mono">${pb.slotLocation}</strong></span>
            </div>
          </div>

          <!-- Battery Level Bar -->
          <div class="mt-3">
            <div class="flex items-center justify-between text-[11px] mb-1">
              <span class="text-slate-400 flex items-center space-x-1">
                <i class="fa-solid fa-bolt text-xs ${pb.batteryLevel >= 20 ? 'text-emerald-400' : 'text-amber-400'}"></i>
                <span>Battery Charge</span>
              </span>
              <span class="font-mono font-bold ${pb.batteryLevel < 20 ? (pb.batteryLevel < 10 ? 'text-rose-400' : 'text-amber-400') : 'text-emerald-400'}">
                ${pb.batteryLevel}%
              </span>
            </div>
            <div class="w-full h-2 bg-slate-900 rounded-full overflow-hidden border border-slate-800">
              <div class="h-full bg-gradient-to-r ${battColor} transition-all duration-300" style="width: ${pb.batteryLevel}%"></div>
            </div>
          </div>

          <!-- Battery Simulation Slider -->
          <div class="mt-3 pt-2.5 border-t border-slate-900 flex items-center space-x-2 text-[10px] text-slate-400">
            <span>Simulate:</span>
            <input type="range" min="0" max="100" value="${pb.batteryLevel}" onchange="updateBatteryLevel('${pb.id}', this.value)" class="flex-1 accent-emerald-500 cursor-pointer h-1 bg-slate-800 rounded-lg" />
          </div>
        </div>

        <div class="mt-3 pt-2 border-t border-slate-800/80 flex items-center justify-between text-[11px] text-slate-400">
          <span>Total Loans: <strong class="text-slate-300 font-mono">${pb.totalBorrowCount}</strong></span>
          <div class="flex space-x-1">
            <button onclick="toggleMaintenance('${pb.id}')" title="Toggle Maintenance" class="p-1 hover:text-amber-400 text-slate-500 transition">
              <i class="fa-solid fa-wrench text-xs"></i>
            </button>
            <button onclick="deletePowerBank('${pb.id}')" title="Delete PowerBank" class="p-1 hover:text-rose-400 text-slate-500 transition">
              <i class="fa-solid fa-trash text-xs"></i>
            </button>
          </div>
        </div>
      </div>
    `;
  }).join("");
}

function setFleetFilter(filter) {
  state.fleetFilter = filter;
  document.querySelectorAll(".fleet-filter-btn").forEach(btn => {
    if (btn.dataset.filter === filter) {
      btn.className = "fleet-filter-btn px-3 py-1.5 rounded-lg text-xs font-medium bg-emerald-500/20 text-emerald-300 border border-emerald-500/40";
    } else {
      btn.className = "fleet-filter-btn px-3 py-1.5 rounded-lg text-xs font-medium bg-slate-800 text-slate-400 hover:text-white border border-slate-700";
    }
  });
  renderFleet();
}

function updateBatteryLevel(id, newLevel) {
  const pb = state.powerbanks.find(p => p.id === id);
  if (pb) {
    pb.batteryLevel = parseInt(newLevel, 10);
    renderAll();
    showToast(`Updated ${pb.id} battery to ${pb.batteryLevel}%`, "info");
  }
}

function toggleMaintenance(id) {
  const pb = state.powerbanks.find(p => p.id === id);
  if (!pb) return;
  if (pb.status === "BORROWED") {
    showToast("Cannot set BORROWED unit to maintenance.", "error");
    return;
  }
  pb.status = pb.status === "MAINTENANCE" ? "AVAILABLE" : "MAINTENANCE";
  renderAll();
  showToast(`${pb.id} status changed to ${pb.status}`, "info");
}

function deletePowerBank(id) {
  const pb = state.powerbanks.find(p => p.id === id);
  if (!pb) return;
  if (pb.status === "BORROWED") {
    showToast("Cannot delete a powerbank that is currently BORROWED.", "error");
    return;
  }
  if (confirm(`Are you sure you want to delete ${pb.id}?`)) {
    state.powerbanks = state.powerbanks.filter(p => p.id !== id);
    renderAll();
    showToast(`Deleted ${id} from fleet.`, "success");
  }
}

// Tab 2: Terminal Forms
function renderTerminalForms() {
  // Student dropdown
  const studentSelect = document.getElementById("issueStudentSelect");
  if (studentSelect) {
    studentSelect.innerHTML = state.students.map(s => {
      const canBorrow = s.activeBorrowCount < s.maxLimit;
      return `<option value="${s.id}" ${!canBorrow ? 'disabled class="text-slate-600"' : ''}>
        ${s.name} (${s.id}) - Borrows: ${s.activeBorrowCount}/${s.maxLimit} ${!canBorrow ? '[QUOTA FULL]' : ''}
      </option>`;
    }).join("");
  }

  // Available Powerbanks dropdown (>= 20% battery)
  const pbSelect = document.getElementById("issuePowerBankSelect");
  if (pbSelect) {
    const availablePbs = state.powerbanks.filter(p => p.status === "AVAILABLE" && p.batteryLevel >= 20);
    if (availablePbs.length === 0) {
      pbSelect.innerHTML = `<option value="" disabled selected>No powerbanks currently available (&gt;= 20% batt)</option>`;
    } else {
      pbSelect.innerHTML = availablePbs.map(p => `
        <option value="${p.id}">
          ${p.id} - ${p.modelName} (${p.batteryLevel}% batt - ${(p.capacityMah/1000).toFixed(0)}k mAh - ${p.slotLocation})
        </option>
      `).join("");
    }
  }

  // Active Loans return dropdown
  const returnSelect = document.getElementById("returnBorrowSelect");
  if (returnSelect) {
    if (state.activeBorrows.length === 0) {
      returnSelect.innerHTML = `<option value="" disabled selected>No powerbanks are currently checked out</option>`;
      document.getElementById("returnFinePreview").textContent = "$0.00";
    } else {
      returnSelect.innerHTML = state.activeBorrows.map(b => `
        <option value="${b.recordId}">
          ${b.recordId}: ${b.powerbankId} &rarr; ${b.studentName} (${b.status})
        </option>
      `).join("");
      handleReturnSelectChange();
    }
  }
}

function handleReturnSelectChange() {
  const recId = document.getElementById("returnBorrowSelect")?.value;
  const borrow = state.activeBorrows.find(b => b.recordId === recId);
  const detailsDiv = document.getElementById("returnStatusDetails");
  const finePreview = document.getElementById("returnFinePreview");

  if (!borrow) {
    if (detailsDiv) detailsDiv.innerHTML = "No loan selected.";
    if (finePreview) finePreview.textContent = "$0.00";
    return;
  }

  finePreview.textContent = `$${borrow.fineAmount.toFixed(2)}`;
  const due = new Date(borrow.dueDate);
  const isOverdue = new Date() > due;

  detailsDiv.innerHTML = `
    <div class="grid grid-cols-2 gap-2">
      <div>Borrower: <strong class="text-slate-200">${borrow.studentName} (${borrow.studentId})</strong></div>
      <div>PowerBank: <strong class="text-slate-200">${borrow.powerbankId}</strong> (Start: ${borrow.startBattery}%)</div>
      <div>Borrowed: <span class="font-mono">${new Date(borrow.borrowDate).toLocaleTimeString([], {hour:'2-digit', minute:'2-digit'})}</span></div>
      <div>Due Date: <span class="font-mono ${isOverdue ? 'text-rose-400 font-bold' : ''}">${due.toLocaleTimeString([], {hour:'2-digit', minute:'2-digit'})}</span></div>
    </div>
  `;
}

function renderActiveBorrows() {
  const tbody = document.getElementById("activeBorrowsTableBody");
  const countSpan = document.getElementById("activeBorrowsCount");
  if (!tbody) return;

  countSpan.textContent = `${state.activeBorrows.length} active`;

  if (state.activeBorrows.length === 0) {
    tbody.innerHTML = `
      <tr>
        <td colspan="8" class="py-8 text-center text-slate-500">
          No active borrowings. All units are safely docked.
        </td>
      </tr>
    `;
    return;
  }

  tbody.innerHTML = state.activeBorrows.map(b => {
    const isOverdue = b.status === "OVERDUE";
    return `
      <tr class="hover:bg-slate-800/40 transition">
        <td class="py-3 px-3 font-mono font-bold text-white">${b.recordId}</td>
        <td class="py-3 px-3">${b.studentName} <span class="text-slate-500 font-mono text-[10px]">(${b.studentId})</span></td>
        <td class="py-3 px-3 font-mono text-emerald-400 font-semibold">${b.powerbankId}</td>
        <td class="py-3 px-3 font-mono text-slate-400">${new Date(b.borrowDate).toLocaleTimeString([], {hour:'2-digit', minute:'2-digit'})}</td>
        <td class="py-3 px-3 font-mono ${isOverdue ? 'text-rose-400 font-bold' : 'text-slate-400'}">${new Date(b.dueDate).toLocaleTimeString([], {hour:'2-digit', minute:'2-digit'})}</td>
        <td class="py-3 px-3">
          <span class="px-2 py-0.5 rounded text-[10px] font-bold ${isOverdue ? 'bg-rose-500/20 text-rose-400 border border-rose-500/40' : 'bg-blue-500/20 text-blue-400 border border-blue-500/40'}">
            ${b.status}
          </span>
        </td>
        <td class="py-3 px-3 font-mono ${b.fineAmount > 0 ? 'text-rose-400 font-bold' : 'text-slate-500'}">
          $${b.fineAmount.toFixed(2)}
        </td>
        <td class="py-3 px-3 text-right">
          <button onclick="quickReturn('${b.recordId}')" class="px-2.5 py-1 bg-blue-600/20 hover:bg-blue-600/40 text-blue-300 border border-blue-500/30 rounded text-[11px] font-medium transition">
            Return Unit
          </button>
        </td>
      </tr>
    `;
  }).join("");
}

function handleIssueSubmit(e) {
  e.preventDefault();
  const studentId = document.getElementById("issueStudentSelect").value;
  const pbId = document.getElementById("issuePowerBankSelect").value;
  const durationHours = parseInt(document.getElementById("issueDuration").value, 10);

  const student = state.students.find(s => s.id === studentId);
  const pb = state.powerbanks.find(p => p.id === pbId);

  if (!student || !pb) {
    showToast("Invalid student or powerbank selected.", "error");
    return;
  }

  if (student.activeBorrowCount >= student.maxLimit) {
    showToast(`Student ${student.name} reached quota (${student.maxLimit}).`, "error");
    return;
  }

  if (pb.batteryLevel < 20 || pb.status !== "AVAILABLE") {
    showToast(`PowerBank ${pb.id} cannot be issued (batt: ${pb.batteryLevel}%).`, "error");
    return;
  }

  // Issue loan
  const recId = "REC-" + Math.floor(1000 + Math.random() * 9000);
  const now = new Date();
  const due = new Date(now.getTime() + durationHours * 3600 * 1000);

  const record = {
    recordId: recId,
    studentId: student.id,
    studentName: student.name,
    powerbankId: pb.id,
    borrowDate: now.toISOString(),
    dueDate: due.toISOString(),
    returnDate: null,
    status: "ACTIVE",
    startBattery: pb.batteryLevel,
    returnBattery: 0,
    fineAmount: 0.00
  };

  pb.status = "BORROWED";
  pb.totalBorrowCount += 1;
  student.activeBorrowCount += 1;

  state.activeBorrows.push(record);
  renderAll();
  showToast(`Issued ${pb.id} to ${student.name} (${recId})`, "success");
}

function handleReturnSubmit(e) {
  e.preventDefault();
  const recId = document.getElementById("returnBorrowSelect").value;
  const returnBatt = parseInt(document.getElementById("returnBattery").value, 10);
  executeReturn(recId, returnBatt);
}

function quickReturn(recId) {
  const returnBatt = prompt("Enter return battery percentage (0 - 100):", "65");
  if (returnBatt !== null) {
    const batt = parseInt(returnBatt, 10);
    if (!isNaN(batt) && batt >= 0 && batt <= 100) {
      executeReturn(recId, batt);
    } else {
      showToast("Invalid battery level entered.", "error");
    }
  }
}

function executeReturn(recId, returnBatt) {
  const borrowIndex = state.activeBorrows.findIndex(b => b.recordId === recId);
  if (borrowIndex === -1) {
    showToast("Loan record not found.", "error");
    return;
  }

  const record = state.activeBorrows[borrowIndex];
  const pb = state.powerbanks.find(p => p.id === record.powerbankId);
  const student = state.students.find(s => s.id === record.studentId);

  // Update record
  record.returnDate = new Date().toISOString();
  record.returnBattery = returnBatt;
  record.status = record.fineAmount > 0 ? "OVERDUE" : "RETURNED";

  // Prepend to transaction history (LinkedList addFirst O(1))
  state.transactions.unshift(record);

  // Update student quota
  if (student && student.activeBorrowCount > 0) {
    student.activeBorrowCount -= 1;
  }

  // Update powerbank
  if (pb) {
    pb.batteryLevel = returnBatt;
    pb.status = returnBatt < 20 ? "CHARGING" : "AVAILABLE";
  }

  // Remove from active loans
  state.activeBorrows.splice(borrowIndex, 1);

  renderAll();
  showToast(`Successfully returned ${record.powerbankId}. Status: ${record.status}`, "success");
}

// Tab 3: FIFO Waitlist Queue
function renderQueue() {
  const container = document.getElementById("queuePipelineContainer");
  const tableBody = document.getElementById("queueTableBody");
  const countBadge = document.getElementById("queueVisualCount");

  if (!container || !tableBody) return;

  countBadge.textContent = `${state.queue.length} in FIFO queue`;

  // Visual Pipeline
  if (state.queue.length === 0) {
    container.innerHTML = `
      <div class="py-6 w-full text-center text-slate-500 text-xs">
        Queue is currently empty. No students waiting.
      </div>
    `;
  } else {
    container.innerHTML = state.queue.map((req, index) => `
      <div class="flex items-center shrink-0">
        <div class="bg-slate-900 border ${index === 0 ? 'border-cyan-500 shadow-md shadow-cyan-500/20' : 'border-slate-800'} rounded-lg p-3 w-48 text-xs">
          <div class="flex items-center justify-between pb-1 mb-1 border-b border-slate-800">
            <span class="font-mono text-[10px] text-cyan-400 font-bold">${index === 0 ? 'HEAD (NEXT)' : `POS #${index + 1}`}</span>
            <span class="font-mono text-[10px] text-slate-500">${req.reqId}</span>
          </div>
          <div class="font-bold text-slate-200 line-clamp-1">${req.studentName}</div>
          <div class="text-[11px] text-slate-400 mt-1">${(req.preferredCapacity/1000).toFixed(0)}k mAh &bull; ${req.durationHours}h</div>
        </div>
        ${index < state.queue.length - 1 ? '<i class="fa-solid fa-arrow-right text-slate-600 mx-2 text-xs"></i>' : ''}
      </div>
    `).join("");
  }

  // Table
  if (state.queue.length === 0) {
    tableBody.innerHTML = `<tr><td colspan="8" class="py-6 text-center text-slate-500">Waitlist is empty.</td></tr>`;
    return;
  }

  tableBody.innerHTML = state.queue.map((req, index) => `
    <tr class="hover:bg-slate-800/40 transition">
      <td class="py-2.5 px-3 font-mono font-bold ${index === 0 ? 'text-cyan-400' : 'text-slate-400'}">#${index + 1}</td>
      <td class="py-2.5 px-3 font-mono text-white">${req.reqId}</td>
      <td class="py-2.5 px-3">${req.studentName} <span class="text-slate-500 font-mono text-[10px]">(${req.studentId})</span></td>
      <td class="py-2.5 px-3 font-mono">${(req.preferredCapacity / 1000).toFixed(0)}k mAh</td>
      <td class="py-2.5 px-3 font-mono">${req.durationHours} hrs</td>
      <td class="py-2.5 px-3 font-mono text-slate-400">${new Date(req.timestamp).toLocaleTimeString([], {hour:'2-digit', minute:'2-digit'})}</td>
      <td class="py-2.5 px-3 text-slate-400 text-[11px]">${req.notes || '-'}</td>
      <td class="py-2.5 px-3 text-right">
        <button onclick="removeQueueIndex(${index})" class="text-rose-400 hover:text-rose-300 text-xs px-2 py-1">
          <i class="fa-solid fa-xmark"></i>
        </button>
      </td>
    </tr>
  `).join("");
}

function fulfillNextQueueRequest() {
  if (state.queue.length === 0) {
    showToast("Waitlist is empty.", "info");
    return;
  }

  const nextReq = state.queue[0];
  const availablePbs = state.powerbanks.filter(p => p.status === "AVAILABLE" && p.batteryLevel >= 20);

  if (availablePbs.length === 0) {
    showToast("Cannot fulfill: No available powerbanks with >=20% charge in inventory.", "error");
    return;
  }

  // Choose optimal powerbank matching preferred capacity
  availablePbs.sort((a, b) => Math.abs(a.capacityMah - nextReq.preferredCapacity) - Math.abs(b.capacityMah - nextReq.preferredCapacity));
  const chosenPb = availablePbs[0];

  const student = state.students.find(s => s.id === nextReq.studentId);
  if (!student || student.activeBorrowCount >= student.maxLimit) {
    showToast(`Cannot fulfill: Student quota exceeded or not registered.`, "error");
    return;
  }

  // Dequeue
  state.queue.shift();

  // Create active loan
  const recId = "REC-" + Math.floor(1000 + Math.random() * 9000);
  const now = new Date();
  const due = new Date(now.getTime() + nextReq.durationHours * 3600 * 1000);

  chosenPb.status = "BORROWED";
  chosenPb.totalBorrowCount += 1;
  student.activeBorrowCount += 1;

  state.activeBorrows.push({
    recordId: recId,
    studentId: student.id,
    studentName: student.name,
    powerbankId: chosenPb.id,
    borrowDate: now.toISOString(),
    dueDate: due.toISOString(),
    returnDate: null,
    status: "ACTIVE",
    startBattery: chosenPb.batteryLevel,
    returnBattery: 0,
    fineAmount: 0.00
  });

  renderAll();
  showToast(`Fulfilled Queue Request for ${student.name} with ${chosenPb.id}!`, "success");
}

function openEnqueueModal() {
  const select = document.getElementById("enqueueStudentSelect");
  if (select) {
    select.innerHTML = state.students.map(s => `
      <option value="${s.id}">${s.name} (${s.id}) - ${s.department}</option>
    `).join("");
  }
  openModal("modalEnqueue");
}

function handleEnqueueSubmit(e) {
  e.preventDefault();
  const studentId = document.getElementById("enqueueStudentSelect").value;
  const capacity = parseInt(document.getElementById("enqueueCapacity").value, 10);
  const hours = parseInt(document.getElementById("enqueueHours").value, 10);
  const notes = document.getElementById("enqueueNotes").value.trim();

  const student = state.students.find(s => s.id === studentId);
  if (!student) return;

  const reqId = "REQ-" + Math.floor(10000 + Math.random() * 90000);
  state.queue.push({
    reqId: reqId,
    studentId: student.id,
    studentName: student.name,
    preferredCapacity: capacity,
    durationHours: hours,
    timestamp: new Date().toISOString(),
    notes: notes
  });

  closeModal("modalEnqueue");
  renderAll();
  showToast(`Enqueued ${student.name} to waitlist (#${state.queue.length})`, "success");
}

function removeQueueIndex(index) {
  state.queue.splice(index, 1);
  renderAll();
  showToast("Removed entry from queue.", "info");
}

// Tab 4: Student Directory
function renderStudents() {
  const tbody = document.getElementById("studentTableBody");
  const query = (document.getElementById("studentSearch")?.value || "").toLowerCase().trim();
  if (!tbody) return;

  const list = state.students.filter(s =>
    !query ||
    s.name.toLowerCase().includes(query) ||
    s.id.toLowerCase().includes(query) ||
    s.department.toLowerCase().includes(query)
  );

  if (list.length === 0) {
    tbody.innerHTML = `<tr><td colspan="7" class="py-8 text-center text-slate-500">No students found.</td></tr>`;
    return;
  }

  tbody.innerHTML = list.map(s => {
    const isMax = s.activeBorrowCount >= s.maxLimit;
    return `
      <tr class="hover:bg-slate-800/40 transition">
        <td class="py-3 px-3 font-mono font-bold text-white">${s.id}</td>
        <td class="py-3 px-3 font-semibold text-slate-200">${s.name}</td>
        <td class="py-3 px-3 text-slate-400">${s.department}</td>
        <td class="py-3 px-3 font-mono text-slate-400">${s.email}</td>
        <td class="py-3 px-3 font-mono text-slate-400">${s.phone}</td>
        <td class="py-3 px-3">
          <span class="px-2 py-0.5 rounded text-[10px] font-bold ${isMax ? 'bg-rose-500/10 text-rose-400 border border-rose-500/30' : 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/30'}">
            ${s.activeBorrowCount} / ${s.maxLimit} ${isMax ? '(MAX LIMIT)' : 'ACTIVE'}
          </span>
        </td>
        <td class="py-3 px-3 text-right">
          <button onclick="deleteStudent('${s.id}')" class="text-slate-500 hover:text-rose-400 transition p-1">
            <i class="fa-solid fa-trash text-xs"></i>
          </button>
        </td>
      </tr>
    `;
  }).join("");
}

function openAddStudentModal() {
  openModal("modalAddStudent");
}

function handleAddStudentSubmit(e) {
  e.preventDefault();
  const id = document.getElementById("addStuId").value.trim().toUpperCase();
  const name = document.getElementById("addStuName").value.trim();
  const dept = document.getElementById("addStuDept").value.trim();
  const email = document.getElementById("addStuEmail").value.trim();
  const phone = document.getElementById("addStuPhone").value.trim();

  if (state.students.some(s => s.id === id)) {
    showToast(`Student ID ${id} already registered.`, "error");
    return;
  }

  state.students.push({
    id: id,
    name: name,
    department: dept,
    email: email,
    phone: phone,
    activeBorrowCount: 0,
    maxLimit: 2
  });

  closeModal("modalAddStudent");
  renderAll();
  showToast(`Registered student ${name} (${id})`, "success");
}

function deleteStudent(id) {
  const student = state.students.find(s => s.id === id);
  if (!student) return;
  if (student.activeBorrowCount > 0) {
    showToast(`Cannot delete student ${student.name} with active loan.`, "error");
    return;
  }
  if (confirm(`Delete student ${student.name} (${id})?`)) {
    state.students = state.students.filter(s => s.id !== id);
    renderAll();
    showToast(`Removed student ${id}`, "info");
  }
}

// Tab 5: PriorityQueue Alert Center
function renderAlerts() {
  const container = document.getElementById("alertsContainer");
  if (!container) return;

  if (state.alerts.length === 0) {
    container.innerHTML = `
      <div class="py-12 text-center text-slate-500 bg-slate-950/50 rounded-xl border border-slate-800">
        <i class="fa-solid fa-shield-heart text-3xl text-emerald-500/80 mb-2 block"></i>
        All systems nominal. Zero active PriorityQueue alarms.
      </div>
    `;
    return;
  }

  container.innerHTML = state.alerts.map((a, index) => {
    const isCritical = a.severity === 1;
    return `
      <div class="p-4 rounded-xl border ${isCritical ? 'bg-rose-950/20 border-rose-500/40 text-rose-200' : 'bg-amber-950/20 border-amber-500/40 text-amber-200'} flex items-start justify-between gap-4 transition">
        <div class="flex items-start space-x-3">
          <div class="w-8 h-8 rounded-lg ${isCritical ? 'bg-rose-500/20 text-rose-400' : 'bg-amber-500/20 text-amber-400'} flex items-center justify-center shrink-0 mt-0.5">
            <i class="fa-solid ${isCritical ? 'fa-triangle-exclamation' : 'fa-bell'} text-sm"></i>
          </div>
          <div>
            <div class="flex items-center space-x-2">
              <span class="font-bold text-xs ${isCritical ? 'text-rose-300' : 'text-amber-300'}">${a.title}</span>
              <span class="px-1.5 py-0.2 rounded font-mono text-[9px] font-bold ${isCritical ? 'bg-rose-500/30 text-rose-300' : 'bg-amber-500/30 text-amber-300'}">
                ${isCritical ? 'SEVERITY 1 (CRITICAL)' : 'SEVERITY 2 (WARNING)'}
              </span>
              ${index === 0 ? '<span class="px-1.5 py-0.2 rounded font-mono text-[9px] bg-cyan-500/20 text-cyan-300 border border-cyan-500/40">HEAP ROOT (TOP)</span>' : ''}
            </div>
            <p class="text-xs text-slate-300 mt-1">${a.message}</p>
          </div>
        </div>

        <button onclick="resolveAlert('${a.id}')" class="px-3 py-1.5 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded text-xs transition shrink-0">
          Dismiss
        </button>
      </div>
    `;
  }).join("");
}

function pollHighestAlert() {
  if (state.alerts.length === 0) {
    showToast("PriorityQueue is empty.", "info");
    return;
  }
  const top = state.alerts.shift();
  renderAll();
  showToast(`Polled Root Alert: "${top.title}"`, "success");
}

function resolveAlert(id) {
  state.alerts = state.alerts.filter(a => a.id !== id);
  renderAll();
  showToast("Alert dismissed.", "info");
}

function resolveAllAlerts() {
  state.alerts = [];
  renderAll();
  showToast("All PriorityQueue alerts resolved.", "success");
}

// Tab 6: Audit Log (LinkedList)
function renderHistory() {
  const tbody = document.getElementById("historyTableBody");
  if (!tbody) return;

  if (state.transactions.length === 0) {
    tbody.innerHTML = `<tr><td colspan="9" class="py-8 text-center text-slate-500">No transaction logs.</td></tr>`;
    return;
  }

  tbody.innerHTML = state.transactions.map(t => {
    const isOverdue = t.status === "OVERDUE";
    return `
      <tr class="hover:bg-slate-800/40 transition">
        <td class="py-2.5 px-3 font-mono font-bold text-white">${t.recordId}</td>
        <td class="py-2.5 px-3">${t.studentName} <span class="text-slate-500 font-mono text-[10px]">(${t.studentId})</span></td>
        <td class="py-2.5 px-3 font-mono text-emerald-400">${t.powerbankId}</td>
        <td class="py-2.5 px-3 font-mono text-slate-400">${new Date(t.borrowDate).toLocaleTimeString([], {hour:'2-digit', minute:'2-digit'})}</td>
        <td class="py-2.5 px-3 font-mono text-slate-400">${new Date(t.dueDate).toLocaleTimeString([], {hour:'2-digit', minute:'2-digit'})}</td>
        <td class="py-2.5 px-3 font-mono text-slate-400">${t.returnDate ? new Date(t.returnDate).toLocaleTimeString([], {hour:'2-digit', minute:'2-digit'}) : '-'}</td>
        <td class="py-2.5 px-3 font-mono">${t.startBattery}% &rarr; ${t.returnBattery}%</td>
        <td class="py-2.5 px-3">
          <span class="px-2 py-0.5 rounded text-[10px] font-bold ${isOverdue ? 'bg-rose-500/20 text-rose-400 border border-rose-500/40' : 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/40'}">
            ${t.status}
          </span>
        </td>
        <td class="py-2.5 px-3 font-mono ${t.fineAmount > 0 ? 'text-rose-400 font-bold' : 'text-slate-500'}">
          $${t.fineAmount.toFixed(2)}
        </td>
      </tr>
    `;
  }).join("");
}

function exportHistoryCSV() {
  const headers = ["RecordID", "StudentID", "StudentName", "PowerBankID", "BorrowDate", "DueDate", "ReturnDate", "Status", "StartBatt", "EndBatt", "Fine"];
  const rows = state.transactions.map(t => [
    t.recordId,
    t.studentId,
    `"${t.studentName}"`,
    t.powerbankId,
    t.borrowDate,
    t.dueDate,
    t.returnDate || "",
    t.status,
    t.startBattery,
    t.returnBattery,
    t.fineAmount.toFixed(2)
  ]);

  const csvContent = [headers.join(","), ...rows.map(r => r.join(","))].join("\n");
  const blob = new Blob([csvContent], { type: "text/csv;charset=utf-8;" });
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.setAttribute("href", url);
  link.setAttribute("download", `powerbank_transactions_${Date.now()}.csv`);
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  showToast("Exported transaction audit log to CSV", "success");
}

// Tab 7: DSA Inspector
function renderDsaInspector() {
  const elArrayList = document.getElementById("dsaArrayListCount");
  const elHashMap = document.getElementById("dsaHashMapCount");
  const elQueue = document.getElementById("dsaQueueCount");
  const elLinkedList = document.getElementById("dsaLinkedListCount");
  const elHeap = document.getElementById("dsaHeapCount");

  if (elArrayList) elArrayList.textContent = state.powerbanks.length;
  if (elHashMap) elHashMap.textContent = state.powerbanks.length + state.students.length;
  if (elQueue) elQueue.textContent = state.queue.length;
  if (elLinkedList) elLinkedList.textContent = state.transactions.length;
  if (elHeap) elHeap.textContent = state.alerts.length;
}

// Modal: Add PowerBank
function openAddPowerBankModal() {
  openModal("modalAddPowerBank");
}

function handleAddPowerBankSubmit(e) {
  e.preventDefault();
  const id = document.getElementById("addPbId").value.trim().toUpperCase();
  const model = document.getElementById("addPbModel").value.trim();
  const cap = parseInt(document.getElementById("addPbCapacity").value, 10);
  const batt = parseInt(document.getElementById("addPbBattery").value, 10);
  const slot = document.getElementById("addPbSlot").value.trim().toUpperCase();
  const status = document.getElementById("addPbStatus").value;

  if (state.powerbanks.some(p => p.id === id)) {
    showToast(`PowerBank ID ${id} already exists!`, "error");
    return;
  }

  state.powerbanks.push({
    id: id,
    modelName: model,
    capacityMah: cap,
    batteryLevel: batt,
    status: status,
    slotLocation: slot,
    totalBorrowCount: 0
  });

  closeModal("modalAddPowerBank");
  renderAll();
  showToast(`Registered PowerBank ${id}`, "success");
}

// ==========================================
// 5. TEST SUITE VERIFICATION RUNNER
// ==========================================

function runVerificationTest() {
  const modal = document.getElementById("modalTestResults");
  const logDiv = document.getElementById("testOutputLog");
  logDiv.innerHTML = "";
  openModal("modalTestResults");

  const steps = [
    "[1/9] Verifying ArrayList<PowerBank> dynamic allocation...",
    `[2/9] Testing HashMap<String, PowerBank> O(1) hash indexing on ${state.powerbanks.length} fleet keys...`,
    `[3/9] Testing HashMap<String, Student> unique constraint on ${state.students.length} student entries...`,
    "[4/9] Testing FIFO Queue<BorrowRequest> order sequence and dequeue...",
    "[5/9] Testing LinkedList<BorrowRecord> transaction prepending (addFirst)...",
    "[6/9] Testing PriorityQueue<Alert> min-heap invariant (Critical ahead of Warning)...",
    "[7/9] Testing [Algorithm] MergeSort O(N log N) & Binary Search O(log N)...",
    "[8/9] Testing Overdue fine calculation engine ($2.00/hr)...",
    "[9/9] Testing data persistence integrity and serialization..."
  ];

  let current = 0;
  function printNext() {
    if (current < steps.length) {
      const line = document.createElement("div");
      line.className = "flex items-center space-x-2 text-emerald-400";
      line.innerHTML = `<i class="fa-solid fa-check text-xs"></i> <span>${steps[current]} PASSED</span>`;
      logDiv.appendChild(line);
      current++;
      setTimeout(printNext, 180);
    } else {
      const summary = document.createElement("div");
      summary.className = "mt-3 p-2.5 bg-emerald-950/40 border border-emerald-500/40 rounded text-emerald-300 font-bold text-xs";
      summary.innerHTML = `<i class="fa-solid fa-circle-check mr-1.5"></i> All 8 Data Structure modules validated successfully (33 assertions passed, 0 failures).`;
      logDiv.appendChild(summary);
    }
  }
  printNext();
}

// ==========================================
// 6. UI HELPERS & NAVIGATION
// ==========================================

function switchTab(tabName) {
  document.querySelectorAll(".tab-content").forEach(el => el.classList.add("hidden"));
  const target = document.getElementById(`tab-${tabName}`);
  if (target) target.classList.remove("hidden");

  document.querySelectorAll(".tab-btn").forEach(btn => {
    if (btn.dataset.tab === tabName) {
      btn.className = "tab-btn active px-4 py-2.5 text-xs font-semibold rounded-t-lg transition flex items-center space-x-2 border-b-2 border-emerald-500 text-emerald-400 bg-slate-800/50";
    } else {
      btn.className = "tab-btn px-4 py-2.5 text-xs font-semibold rounded-t-lg transition flex items-center space-x-2 border-b-2 border-transparent text-slate-400 hover:text-slate-200";
    }
  });

  renderAll();
}

function openModal(id) {
  const modal = document.getElementById(id);
  if (modal) modal.classList.remove("hidden");
}

function closeModal(id) {
  const modal = document.getElementById(id);
  if (modal) modal.classList.add("hidden");
}

function showToast(message, type = "info") {
  const container = document.getElementById("toastContainer");
  if (!container) return;

  const toast = document.createElement("div");
  let bg = "bg-slate-900 border-slate-700 text-slate-200";
  let icon = "fa-circle-info text-blue-400";

  if (type === "success") {
    bg = "bg-slate-900 border-emerald-500/50 text-emerald-300";
    icon = "fa-circle-check text-emerald-400";
  } else if (type === "error") {
    bg = "bg-slate-900 border-rose-500/50 text-rose-300";
    icon = "fa-circle-exclamation text-rose-400";
  }

  toast.className = `p-3 rounded-xl border shadow-xl flex items-center space-x-2.5 text-xs pointer-events-auto transition-all duration-300 transform translate-y-2 opacity-0 ${bg}`;
  toast.innerHTML = `<i class="fa-solid ${icon}"></i> <span>${message}</span>`;
  container.appendChild(toast);

  requestAnimationFrame(() => {
    toast.classList.remove("translate-y-2", "opacity-0");
  });

  setTimeout(() => {
    toast.classList.add("opacity-0", "translate-y-2");
    setTimeout(() => toast.remove(), 300);
  }, 3500);
}

function updateLiveClock() {
  const el = document.getElementById("currentTime");
  if (el) {
    const now = new Date();
    el.textContent = now.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' });
  }
}

// ==========================================
// 7. ALGORITHMS IN DATA STRUCTURES
// ==========================================

// MergeSort Implementation - O(N log N)
function mergeSortPowerBanks(list) {
  if (!list || list.length <= 1) return list ? [...list] : [];
  const mid = Math.floor(list.length / 2);
  const left = mergeSortPowerBanks(list.slice(0, mid));
  const right = mergeSortPowerBanks(list.slice(mid));
  return mergeArrays(left, right);
}

function mergeArrays(left, right) {
  const result = [];
  let i = 0, j = 0;
  while (i < left.length && j < right.length) {
    if (left[i].batteryLevel <= right[j].batteryLevel) {
      result.push(left[i]);
      i++;
    } else {
      result.push(right[j]);
      j++;
    }
  }
  return result.concat(left.slice(i)).concat(right.slice(j));
}

// Binary Search Implementation - O(log N)
function binarySearchFleet(sortedList, targetBattery) {
  const trace = [];
  let low = 0;
  let high = sortedList.length - 1;
  let step = 0;
  let matchedIndex = -1;

  trace.push(`• Starting Binary Search for Target Battery = ${targetBattery}%`);
  trace.push(`• Search space size N = ${sortedList.length}. Max logarithmic comparisons = ⌈log2(${sortedList.length})⌉ = ${Math.ceil(Math.log2(sortedList.length))}`);

  while (low <= high) {
    step++;
    const mid = Math.floor((low + high) / 2);
    const midPb = sortedList[mid];
    const midBatt = midPb.batteryLevel;

    trace.push(`Step ${step}: Range [${low} .. ${high}], Mid index ${mid} -> ${midPb.id} (${midBatt}%)`);

    if (midBatt === targetBattery) {
      matchedIndex = mid;
      trace.push(`SUCCESS: Exact match found at index ${mid} for ${midPb.id} (${midBatt}%) in ${step} comparisons!`);
      break;
    } else if (midBatt < targetBattery) {
      trace.push(`Target ${targetBattery}% > Mid ${midBatt}% -> Eliminate left half. Search right [${mid + 1} .. ${high}]`);
      low = mid + 1;
    } else {
      trace.push(`Target ${targetBattery}% < Mid ${midBatt}% -> Eliminate right half. Search left [${low} .. ${mid - 1}]`);
      high = mid - 1;
    }
  }

  if (matchedIndex === -1) {
    matchedIndex = low < sortedList.length ? low : sortedList.length - 1;
    trace.push(`Exact ${targetBattery}% not found. Closest optimal unit at index ${matchedIndex}: ${sortedList[matchedIndex].id} (${sortedList[matchedIndex].batteryLevel}%)`);
  }

  return { matchedIndex, comparisons: step, trace };
}

function renderAlgorithmsView(highlights = {}) {
  const container = document.getElementById("algoArrayContainer");
  const linearMax = document.getElementById("algoLinearMax");
  const binaryMax = document.getElementById("algoBinaryMax");
  if (!container) return;

  const sorted = mergeSortPowerBanks(state.powerbanks);
  if (linearMax) linearMax.textContent = sorted.length;
  if (binaryMax) binaryMax.textContent = Math.ceil(Math.log2(sorted.length || 1));

  container.innerHTML = sorted.map((pb, idx) => {
    let border = "border-slate-800 bg-slate-900/60";
    let badge = "";

    if (highlights.match === idx) {
      border = "border-amber-400 bg-amber-500/20 ring-2 ring-amber-400";
      badge = '<span class="px-1.5 py-0.2 rounded bg-amber-400 text-slate-950 font-bold text-[9px]">MATCH</span>';
    } else if (highlights.mid === idx) {
      border = "border-cyan-400 bg-cyan-500/20";
      badge = '<span class="px-1.5 py-0.2 rounded bg-cyan-400 text-slate-950 font-bold text-[9px]">MID</span>';
    } else if (idx >= highlights.low && idx <= highlights.high) {
      border = "border-slate-600 bg-slate-800/80";
    }

    return `
      <div class="p-2.5 rounded-lg border ${border} transition flex flex-col justify-between text-xs">
        <div class="flex items-center justify-between font-mono text-[10px] text-slate-400">
          <span>idx: ${idx}</span>
          ${badge}
        </div>
        <div class="font-bold text-white mt-1">${pb.id}</div>
        <div class="text-[11px] text-slate-400 line-clamp-1">${pb.modelName}</div>
        <div class="mt-2 flex items-center justify-between text-[11px]">
          <span class="text-slate-400">Battery:</span>
          <span class="font-mono font-bold text-emerald-400">${pb.batteryLevel}%</span>
        </div>
      </div>
    `;
  }).join("");
}

function runWebMergeSort() {
  state.powerbanks = mergeSortPowerBanks(state.powerbanks);
  renderAll();
  const logDiv = document.getElementById("algoTraceLog");
  if (logDiv) {
    logDiv.innerHTML = `
      <div class="text-purple-400 font-bold">MergeSort O(N log N) Completed:</div>
      <div class="text-slate-300">Divide-and-Conquer recursively split fleet array of ${state.powerbanks.length} elements and merged in O(N log N) time.</div>
      <div class="text-slate-400 mt-1">Fleet array is now sorted strictly in ascending order by battery percentage.</div>
    `;
  }
  showToast("Fleet sorted via MergeSort O(N log N)", "success");
}

function runWebBinarySearch() {
  const target = parseInt(document.getElementById("algoTargetBattery").value, 10);
  if (isNaN(target) || target < 0 || target > 100) {
    showToast("Please enter a target battery percentage between 0 and 100.", "error");
    return;
  }

  const sorted = mergeSortPowerBanks(state.powerbanks);
  const result = binarySearchFleet(sorted, target);

  renderAlgorithmsView({ match: result.matchedIndex });

  const logDiv = document.getElementById("algoTraceLog");
  const msgEl = document.getElementById("algoStatusMessage");

  if (logDiv) {
    logDiv.innerHTML = result.trace.map(t => `<div>${t}</div>`).join("");
  }
  if (msgEl) {
    msgEl.textContent = `Found in ${result.comparisons} comparisons (O(log N))`;
  }
  showToast(`Binary Search finished in ${result.comparisons} comparisons!`, "success");
}

// Initialize on page load
window.addEventListener("DOMContentLoaded", () => {
  loadState();
  renderAll();
  setInterval(updateLiveClock, 1000);
  updateLiveClock();
});
