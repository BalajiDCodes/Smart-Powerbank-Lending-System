package com.powerbank.model;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Model representing an issuance/borrowing event and return record.
 */
public class BorrowRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_RETURNED = "RETURNED";
    public static final String STATUS_OVERDUE = "OVERDUE";

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final double FINE_RATE_PER_HOUR = 2.0; // $2.00 or ₹20 per hour overdue

    private String recordId;
    private String studentId;
    private String studentName;
    private String powerbankId;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private String status; // ACTIVE, RETURNED, OVERDUE
    private int initialBatteryLevel;
    private int returnBatteryLevel;
    private double fineAmount;

    public BorrowRecord() {
        this.borrowDate = LocalDateTime.now();
        this.dueDate = this.borrowDate.plusHours(4); // default 4-hour loan
        this.status = STATUS_ACTIVE;
    }

    public BorrowRecord(String recordId, String studentId, String studentName, String powerbankId,
                        LocalDateTime borrowDate, LocalDateTime dueDate, LocalDateTime returnDate,
                        String status, int initialBatteryLevel, int returnBatteryLevel, double fineAmount) {
        this.recordId = recordId != null ? recordId.trim() : "";
        this.studentId = studentId != null ? studentId.trim() : "";
        this.studentName = studentName != null ? studentName.trim() : "";
        this.powerbankId = powerbankId != null ? powerbankId.trim() : "";
        this.borrowDate = borrowDate != null ? borrowDate : LocalDateTime.now();
        this.dueDate = dueDate != null ? dueDate : this.borrowDate.plusHours(4);
        this.returnDate = returnDate;
        this.status = status != null ? status.trim().toUpperCase() : STATUS_ACTIVE;
        this.initialBatteryLevel = initialBatteryLevel;
        this.returnBatteryLevel = returnBatteryLevel;
        this.fineAmount = fineAmount;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId != null ? recordId.trim() : "";
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId != null ? studentId.trim() : "";
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName != null ? studentName.trim() : "";
    }

    public String getPowerbankId() {
        return powerbankId;
    }

    public void setPowerbankId(String powerbankId) {
        this.powerbankId = powerbankId != null ? powerbankId.trim() : "";
    }

    public LocalDateTime getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDateTime borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status != null ? status.trim().toUpperCase() : STATUS_ACTIVE;
    }

    public int getInitialBatteryLevel() {
        return initialBatteryLevel;
    }

    public void setInitialBatteryLevel(int initialBatteryLevel) {
        this.initialBatteryLevel = initialBatteryLevel;
    }

    public int getReturnBatteryLevel() {
        return returnBatteryLevel;
    }

    public void setReturnBatteryLevel(int returnBatteryLevel) {
        this.returnBatteryLevel = returnBatteryLevel;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(double fineAmount) {
        this.fineAmount = fineAmount;
    }

    /**
     * Checks if this record is currently overdue.
     */
    public boolean isCurrentlyOverdue() {
        if (STATUS_RETURNED.equalsIgnoreCase(status)) return false;
        return LocalDateTime.now().isAfter(dueDate);
    }

    /**
     * Calculates the overdue duration in minutes based on current time or return time.
     */
    public long getOverdueMinutes() {
        LocalDateTime comparisonPoint = (returnDate != null) ? returnDate : LocalDateTime.now();
        if (comparisonPoint.isAfter(dueDate)) {
            return Duration.between(dueDate, comparisonPoint).toMinutes();
        }
        return 0;
    }

    /**
     * Formats overdue duration into a human-readable string (e.g. "2h 15m" or "45m").
     */
    public String getFormattedOverdueDuration() {
        long mins = getOverdueMinutes();
        if (mins <= 0) return "0 mins";
        long days = mins / (24 * 60);
        long hours = (mins % (24 * 60)) / 60;
        long remMins = mins % 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (remMins > 0 || (days == 0 && hours == 0)) sb.append(remMins).append("m");
        return sb.toString().trim();
    }

    /**
     * Calculates the overdue penalty fine.
     */
    public double calculateOverdueFine() {
        long mins = getOverdueMinutes();
        if (mins <= 0) return 0.0;
        // Fractional hours rounded to 2 decimal places
        double hours = mins / 60.0;
        return Math.round(hours * FINE_RATE_PER_HOUR * 100.0) / 100.0;
    }

    public String toCsvString() {
        String borrowStr = borrowDate != null ? borrowDate.format(DATE_FORMATTER) : "";
        String dueStr = dueDate != null ? dueDate.format(DATE_FORMATTER) : "";
        String returnStr = returnDate != null ? returnDate.format(DATE_FORMATTER) : "";

        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%d,%d,%.2f",
                escapeCsv(recordId),
                escapeCsv(studentId),
                escapeCsv(studentName),
                escapeCsv(powerbankId),
                borrowStr,
                dueStr,
                returnStr,
                status,
                initialBatteryLevel,
                returnBatteryLevel,
                fineAmount);
    }

    public static BorrowRecord fromCsvString(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        if (parts.length < 11) return null;
        try {
            String recId = unescapeCsv(parts[0]);
            String stuId = unescapeCsv(parts[1]);
            String stuName = unescapeCsv(parts[2]);
            String pbId = unescapeCsv(parts[3]);
            LocalDateTime bDate = parseDate(parts[4]);
            LocalDateTime dDate = parseDate(parts[5]);
            LocalDateTime rDate = parseDate(parts[6]);
            String st = parts[7].trim();
            int initBatt = Integer.parseInt(parts[8].trim());
            int retBatt = Integer.parseInt(parts[9].trim());
            double fine = Double.parseDouble(parts[10].trim());

            return new BorrowRecord(recId, stuId, stuName, pbId, bDate, dDate, rDate, st, initBatt, retBatt, fine);
        } catch (Exception e) {
            System.err.println("Error parsing BorrowRecord CSV: " + line + " -> " + e.getMessage());
            return null;
        }
    }

    private static LocalDateTime parseDate(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try {
            return LocalDateTime.parse(s.trim(), DATE_FORMATTER);
        } catch (Exception e) {
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
}
