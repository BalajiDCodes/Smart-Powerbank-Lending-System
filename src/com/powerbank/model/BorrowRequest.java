package com.powerbank.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Model representing a waiting request in the FIFO borrow queue.
 */
public class BorrowRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String requestId;
    private String studentId;
    private String studentName;
    private int preferredCapacityMah; // 0 for any
    private int requestedDurationHours;
    private LocalDateTime requestTime;
    private String notes;

    public BorrowRequest() {
        this.requestTime = LocalDateTime.now();
        this.requestedDurationHours = 4;
    }

    public BorrowRequest(String requestId, String studentId, String studentName, int preferredCapacityMah, int requestedDurationHours, LocalDateTime requestTime, String notes) {
        this.requestId = requestId != null ? requestId.trim() : "";
        this.studentId = studentId != null ? studentId.trim() : "";
        this.studentName = studentName != null ? studentName.trim() : "";
        this.preferredCapacityMah = preferredCapacityMah;
        this.requestedDurationHours = requestedDurationHours > 0 ? requestedDurationHours : 4;
        this.requestTime = requestTime != null ? requestTime : LocalDateTime.now();
        this.notes = notes != null ? notes.trim() : "";
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId != null ? requestId.trim() : "";
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

    public int getPreferredCapacityMah() {
        return preferredCapacityMah;
    }

    public void setPreferredCapacityMah(int preferredCapacityMah) {
        this.preferredCapacityMah = preferredCapacityMah;
    }

    public int getRequestedDurationHours() {
        return requestedDurationHours;
    }

    public void setRequestedDurationHours(int requestedDurationHours) {
        this.requestedDurationHours = requestedDurationHours;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime != null ? requestTime : LocalDateTime.now();
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes != null ? notes.trim() : "";
    }

    public String toCsvString() {
        String timeStr = requestTime != null ? requestTime.format(DATE_FORMATTER) : "";
        return String.format("%s,%s,%s,%d,%d,%s,%s",
                escapeCsv(requestId),
                escapeCsv(studentId),
                escapeCsv(studentName),
                preferredCapacityMah,
                requestedDurationHours,
                timeStr,
                escapeCsv(notes));
    }

    public static BorrowRequest fromCsvString(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        if (parts.length < 7) return null;
        try {
            String reqId = unescapeCsv(parts[0]);
            String stuId = unescapeCsv(parts[1]);
            String stuName = unescapeCsv(parts[2]);
            int cap = Integer.parseInt(parts[3].trim());
            int dur = Integer.parseInt(parts[4].trim());
            LocalDateTime time = parseDate(parts[5]);
            String notes = unescapeCsv(parts[6]);
            return new BorrowRequest(reqId, stuId, stuName, cap, dur, time, notes);
        } catch (Exception e) {
            System.err.println("Error parsing BorrowRequest CSV: " + line + " -> " + e.getMessage());
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
