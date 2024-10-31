package com.catignascabela.dodapplication;

public class Teacher {
    private String fullName;
    private String department;
    private String uid;
    private String email; // New attribute
    private String username; // New attribute for username
    private String role; // New attribute for role

    // Default constructor required for calls to DataSnapshot.getValue(Teacher.class)
    public Teacher() {
    }

    // Constructor with parameters
    public Teacher(String fullName, String department, String uid, String email, String username) {
        this.fullName = fullName;
        this.department = department;
        this.uid = uid;
        this.email = email;
        this.username = username;
        this.role = "teacher"; // Set role as "teacher"
    }

    // Getters and setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
