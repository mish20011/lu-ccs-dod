package com.catignascabela.dodapplication;

import java.util.ArrayList;
import java.util.List;

public class Student {
    private String fullName;
    private String studentId;
    private String gender;
    private String yearBlock;
    private String course;
    private List<String> violations;

    // Constructor
    public Student(String fullName, String studentId) {
        this.fullName = fullName;
        this.studentId = studentId;
        this.violations = new ArrayList<>();
    }

    // Getters
    public String getFullName() {
        return fullName;
    }

    public String getStudentId() {
        return studentId;
    }

    public List<String> getViolations() {
        return violations;
    }

    // Setters
    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setYearBlock(String yearBlock) {
        this.yearBlock = yearBlock;
    }

    public String getYearBlock() {
        return yearBlock;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getCourse() {
        return course;
    }

    public void addViolation(String violation, String punishment) {
        violations.add(violation + " - " + punishment); // Example of how you might store violations
    }
}
