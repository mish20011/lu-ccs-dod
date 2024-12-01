package com.catignascabela.dodapplication;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private String studentId; // Document ID (Student ID)
    private String email; // Email address
    private String firstName; // First name
    private String middleInitial; // Middle initial
    private String surname; // Last name (surname)
    private String gender; // Gender
    private String yearBlock; // Year and block (e.g., 3b)
    private String course; // Course enrolled
    private List<Violation> violations; // List of Violation objects
    private String role; // User role (default: student)

    // Default constructor required for Firestore
    public Student() {
        this.violations = new ArrayList<>(); // Initialize violations list
        this.role = "student"; // Default role
    }

    // Full constructor
    public Student(String studentId, String email, String firstName, String middleInitial,
                   String surname, String gender, String yearBlock, String course) {
        this.studentId = studentId; // Document ID as Student ID
        this.email = email;
        this.firstName = firstName;
        this.middleInitial = middleInitial;
        this.surname = surname;
        this.gender = gender;
        this.yearBlock = yearBlock;
        this.course = course;
        this.violations = new ArrayList<>(); // Initialize violations list
        this.role = "student"; // Default role
    }

    public String getFullName() {
        StringBuilder fullName = new StringBuilder();
        fullName.append(surname).append(", ").append(firstName);
        if (middleInitial != null && !middleInitial.isEmpty()) {
            fullName.append(" ").append(middleInitial);
        }
        return fullName.toString();
    }

    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleInitial() { return middleInitial; }
    public void setMiddleInitial(String middleInitial) { this.middleInitial = middleInitial; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getYearBlock() { return yearBlock; }
    public void setYearBlock(String yearBlock) { this.yearBlock = yearBlock; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public List<Violation> getViolations() { return violations; }
    public void setViolations(List<Violation> violations) { this.violations = violations; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}