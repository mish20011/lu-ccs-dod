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
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeStudentBinding.inflate(inflater, container, false);

        // Initialize Firebase Authentication and Database Reference
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("students");  // Updated path to "students"

        // Fetch user data from Firebase
        fetchStudentData();

        return binding.getRoot();
    }

    private void fetchStudentData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid(); // Get the current user's ID

            // Reference to the user's data in the "students" node in the database
            databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Student student = snapshot.getValue(Student.class);

                        if (student != null) {
                            // Set the student details in the TextViews
                            binding.studentIdTextView.setText("ID: " + student.getStudentId());
                            binding.genderTextView.setText("Gender: " + student.getGender());
                            binding.collegeYearTextView.setText("Year/Block: " + student.getYearBlock());
                            binding.courseTextView.setText("Course: " + student.getCourse());
                            binding.fullNameTextView.setText("Full Name: " + student.getFullName());

                            // Debugging log to verify student data retrieval
                            Log.d("StudentHomeFragment", "Student Data Retrieved: " + student.toString());
                        } else {
                            Log.d("StudentHomeFragment", "Student data is null.");
                        }
                    } else {
                        Log.d("StudentHomeFragment", "No data found for user ID: " + userId);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("StudentHomeFragment", "Database error: " + error.getMessage());
                }
            });
        } else {
            Log.d("StudentHomeFragment", "No current user found.");
        }
    }
}
