package com.powerbank.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Model representing system alerts (Overdue, Low Battery, Critical Battery).
 * Implements Comparable for PriorityQueue prioritization.
 */
public class Alert implements Comparable<Alert>, Serializable {
    private static final long serialVersionUID = 1L;

    public static final String TYPE_OVERDUE = "OVERDUE";
    public static final String TYPE_CRITICAL_BATTERY = "CRITICAL_BATTERY";
    public static final String TYPE_LOW_BATTERY = "LOW_BATTERY";
    public static final String TYPE_SYSTEM = "SYSTEM";

    // Severity levels: 1 is highest priority (Critical), 2 is Warning, 3 is Info
    public static final int SEVERITY_CRITICAL = 1;
    public static final int SEVERITY_WARNING = 2;
    public static final int SEVERITY_INFO = 3;

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String alertId;
    private String type;
    private int severity; // 1 = Critical, 2 = Warning, 3 = Info
    private String title;
    private String message;
    private String targetId; // PowerBank ID or Student ID or Record ID
    private LocalDateTime timestamp;
    private boolean resolved;

    public Alert() {
        this.timestamp = LocalDateTime.now();
        this.severity = SEVERITY_INFO;
        this.resolved = false;
    }

    public Alert(String alertId, String type, int severity, String title, String message, String targetId, LocalDateTime timestamp, boolean resolved) {
        this.alertId = alertId != null ? alertId.trim() : "";
        this.type = type != null ? type.trim().toUpperCase() : TYPE_SYSTEM;
        this.severity = severity >= 1 && severity <= 3 ? severity : SEVERITY_INFO;
        this.title = title != null ? title.trim() : "";
        this.message = message != null ? message.trim() : "";
        this.targetId = targetId != null ? targetId.trim() : "";
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
        this.resolved = resolved;
    }

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId != null ? alertId.trim() : "";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type != null ? type.trim().toUpperCase() : TYPE_SYSTEM;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity >= 1 && severity <= 3 ? severity : SEVERITY_INFO;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title != null ? title.trim() : "";
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message != null ? message.trim() : "";
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId != null ? targetId.trim() : "";
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public String getSeverityName() {
        switch (severity) {
            case SEVERITY_CRITICAL: return "CRITICAL";
            case SEVERITY_WARNING: return "WARNING";
            case SEVERITY_INFO: default: return "INFO";
        }
    }

    /**
     * PriorityQueue order:
     * Lower severity integer (1 < 2 < 3) means higher priority.
     * If severities are equal, earlier timestamps take precedence.
     */
    @Override
    public int compareTo(Alert other) {
        if (other == null) return -1;
        // 1. Primary: Severity (1 = Critical, 2 = Warning, 3 = Info)
        int severityComp = Integer.compare(this.severity, other.severity);
        if (severityComp != 0) {
            return severityComp;
        }
        // 2. Secondary: Timestamp (earlier timestamps first)
        if (this.timestamp != null && other.timestamp != null) {
            return this.timestamp.compareTo(other.timestamp);
        }
        return 0;
    }

    public String toCsvString() {
        String timeStr = timestamp != null ? timestamp.format(DATE_FORMATTER) : "";
        return String.format("%s,%s,%d,%s,%s,%s,%s,%b",
                escapeCsv(alertId),
                escapeCsv(type),
                severity,
                escapeCsv(title),
                escapeCsv(message),
                escapeCsv(targetId),
                timeStr,
                resolved);
    }

    public static Alert fromCsvString(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        if (parts.length < 8) return null;
        try {
            String id = unescapeCsv(parts[0]);
            String type = unescapeCsv(parts[1]);
            int severity = Integer.parseInt(parts[2].trim());
            String title = unescapeCsv(parts[3]);
            String msg = unescapeCsv(parts[4]);
            String target = unescapeCsv(parts[5]);
            LocalDateTime time = parseDate(parts[6]);
            boolean res = Boolean.parseBoolean(parts[7].trim());
            return new Alert(id, type, severity, title, msg, target, time, res);
        } catch (Exception e) {
            System.err.println("Error parsing Alert CSV: " + line + " -> " + e.getMessage());
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
