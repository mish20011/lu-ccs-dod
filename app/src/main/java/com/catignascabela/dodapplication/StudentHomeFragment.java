package com.catignascabela.dodapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.catignascabela.dodapplication.databinding.FragmentHomeStudentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudentHomeFragment extends Fragment {
    private FragmentHomeStudentBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Firebase Authentication and Realtime Database
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeStudentBinding.inflate(inflater, container, false);
        // Fetch student data based on their authenticated UID
        fetchStudentData();
        return binding.getRoot();
    }

    private void fetchStudentData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid(); // UID from Firebase Authentication
            DatabaseReference studentRef = databaseRef.child("students").child(userId);

            studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Student student = snapshot.getValue(Student.class);
                        if (student != null) {
                            populateStudentDetails(student);
                            fetchStudentViolations(student.getStudentId());
                        }
                    } else {
                        Log.e("StudentHomeFragment", "No student data found for UID: " + userId);
                        binding.studentIdTextView.setText("No student data found.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("StudentHomeFragment", "Failed to fetch student data.", error.toException());
                    binding.studentIdTextView.setText("Error fetching data.");
                }
            });
        } else {
            Log.e("StudentHomeFragment", "No authenticated user found.");
            binding.studentIdTextView.setText("User not authenticated.");
        }
    }

    private void populateStudentDetails(Student student) {
        binding.studentIdTextView.setText("ID: " + student.getStudentId());
        binding.genderTextView.setText("Gender: " + student.getGender());
        binding.collegeYearTextView.setText("Year/Block: " + student.getYearBlock());
        binding.courseTextView.setText("Course: " + student.getCourse());
        binding.fullNameTextView.setText("Full Name: " + student.getFullName());
    }

    private void displayViolation(DataSnapshot snapshot) {
        // Iterate through the violationRecords (each snapshot here is a violation)
        for (DataSnapshot violationSnapshot : snapshot.getChildren()) {
            // Fetch the violation details from the current violationSnapshot
            String description = violationSnapshot.child("description").getValue(String.class);
            String punishment = violationSnapshot.child("punishment").getValue(String.class);

            // Check if both description and punishment exist
            if (description != null && punishment != null) {
                binding.violationStatusTextView.setText(
                        "You have a violation! Comply and settle your violation!\n\n" +
                                "Violation Description: " + description + "\n" +
                                "Punishment: " + punishment
                );
            } else {
                Log.e("StudentHomeFragment", "Incomplete violation details.");
                binding.violationStatusTextView.setText("Violation details are incomplete.");
            }
        }
    }

    private void fetchStudentViolations(String studentId) {
        DatabaseReference violationsRef = FirebaseDatabase.getInstance()
                .getReference("violations")
                .child(studentId);

        // First, check if there are any active violations
        violationsRef.child("violationRecords").orderByChild("timestamp").limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Show current violation
                            displayViolation(snapshot);
                        } else {
                            // No active violation, check settled violations
                            checkSettledViolations(studentId);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("StudentHomeFragment", "Error fetching violations: " + error.getMessage());
                    }
                });
    }

    private void checkSettledViolations(String studentId) {
        DatabaseReference settledViolationsRef = FirebaseDatabase.getInstance()
                .getReference("violations")
                .child(studentId)
                .child("settledViolations");

        settledViolationsRef.orderByChild("timestamp").limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            binding.violationStatusTextView.setText("You are clear of violations!");
                        } else {
                            binding.violationStatusTextView.setText("All violations settled.");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("StudentHomeFragment", "Error fetching settled violations: " + error.getMessage());
                    }
                });
    }
}
