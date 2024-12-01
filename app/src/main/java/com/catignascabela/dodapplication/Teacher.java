package com.catignascabela.dodapplication;

public class Teacher {
    private String fullName;
    private String department;
    private String uid; // Unique identifier for the teacher
    private String email;
    private String username;
    private String role;
    private boolean isApproved; // Indicates if the teacher is approved

    // Default constructor required for calls to DataSnapshot.getValue(Teacher.class)
    public Teacher() {
    }

    // Constructor with parameters
    public Teacher(String fullName, String department, String uid, String email, String username) {
        this.fullName = fullName;
        this.department = department;
        this.uid = uid; // Ensure this is set correctly
        this.email = email;
        this.username = username;
        this.role = "teacher"; // Default role
        this.isApproved = false; // Default is not approved
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
        this.uid = uid; // Ensure you are setting this when creating/updating a teacher
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

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved; // Setter for approval status
    }
}
