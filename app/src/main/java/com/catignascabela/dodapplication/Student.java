package com.catignascabela.dodapplication;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private String studentId;
    private String email;
    private String fullName;
    private String gender;
    private String yearBlock;
    private String course;
    private String profileImageUri;
    private List<String> violations; // List to store violations
    private String role; // Added role attribute

    // Default constructor required for Firebase calls
    public Student() {
        this.violations = new ArrayList<>(); // Initialize the violations list
        this.role = "student"; // Set default role as student
    }

    // Constructor
    public Student(String studentId, String email, String fullName, String gender,
                   String yearBlock, String course, String profileImageUri) {
        this.studentId = studentId;
        this.email = email;
        this.fullName = fullName;
        this.gender = gender;
        this.yearBlock = yearBlock;
        this.course = course;
        this.profileImageUri = profileImageUri;
        this.violations = new ArrayList<>(); // Initialize the violations list
        this.role = "student"; // Set default role as student
    }

    // Getters and Setters for each field
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getYearBlock() {
        return yearBlock;
    }

    public void setYearBlock(String yearBlock) {
        this.yearBlock = yearBlock;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getProfileImageUri() {
        return profileImageUri;
    }

    public void setProfileImageUri(String profileImageUri) {
        this.profileImageUri = profileImageUri;
    }

    // Method to add a violation
    public void addViolation(String violation, String punishment) {
        violations.add(violation + " - " + punishment); // Store violations with punishment
    }

    // Getter for violations if needed
    public List<String> getViolations() {
        return violations;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
