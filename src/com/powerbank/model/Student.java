package com.powerbank.model;

import java.io.Serializable;

/**
 * Model representing a registered student in the lending system.
 */
public class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_MAX_BORROWS = 2;

    private String id;
    private String name;
    private String email;
    private String department;
    private String phone;
    private int activeBorrowCount;
    private int maxLimit;

    public Student() {
        this.maxLimit = DEFAULT_MAX_BORROWS;
        this.activeBorrowCount = 0;
    }

    public Student(String id, String name, String email, String department, String phone, int activeBorrowCount, int maxLimit) {
        this.id = id != null ? id.trim() : "";
        this.name = name != null ? name.trim() : "";
        this.email = email != null ? email.trim() : "";
        this.department = department != null ? department.trim() : "General";
        this.phone = phone != null ? phone.trim() : "";
        this.activeBorrowCount = Math.max(0, activeBorrowCount);
        this.maxLimit = maxLimit > 0 ? maxLimit : DEFAULT_MAX_BORROWS;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id != null ? id.trim() : "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name.trim() : "";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email.trim() : "";
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department != null ? department.trim() : "";
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone != null ? phone.trim() : "";
    }

    public int getActiveBorrowCount() {
        return activeBorrowCount;
    }

    public void setActiveBorrowCount(int activeBorrowCount) {
        this.activeBorrowCount = Math.max(0, activeBorrowCount);
    }

    public int getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(int maxLimit) {
        this.maxLimit = maxLimit > 0 ? maxLimit : DEFAULT_MAX_BORROWS;
    }

    public boolean canBorrow() {
        return activeBorrowCount < maxLimit;
    }

    public void incrementBorrowCount() {
        this.activeBorrowCount++;
    }

    public void decrementBorrowCount() {
        if (this.activeBorrowCount > 0) {
            this.activeBorrowCount--;
        }
    }

    public String toCsvString() {
        return String.format("%s,%s,%s,%s,%s,%d,%d",
                escapeCsv(id),
                escapeCsv(name),
                escapeCsv(email),
                escapeCsv(department),
                escapeCsv(phone),
                activeBorrowCount,
                maxLimit);
    }

    public static Student fromCsvString(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        if (parts.length < 7) return null;
        try {
            String id = unescapeCsv(parts[0]);
            String name = unescapeCsv(parts[1]);
            String email = unescapeCsv(parts[2]);
            String dept = unescapeCsv(parts[3]);
            String phone = unescapeCsv(parts[4]);
            int active = Integer.parseInt(parts[5].trim());
            int max = Integer.parseInt(parts[6].trim());
            return new Student(id, name, email, dept, phone, active, max);
        } catch (Exception e) {
            System.err.println("Error parsing Student CSV: " + line + " -> " + e.getMessage());
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
        return String.format("%s - %s (%s)", id, name, department);
    }
}
