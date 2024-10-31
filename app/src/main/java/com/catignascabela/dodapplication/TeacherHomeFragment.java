package com.catignascabela.dodapplication;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.catignascabela.dodapplication.databinding.FragmentHomeTeacherBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

public class TeacherHomeFragment extends Fragment {

    private FragmentHomeTeacherBinding binding;
    private DatabaseReference databaseReference; // Firebase Database reference

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeTeacherBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Initialize Firebase Auth
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser(); // Get the current logged-in user

        // Check if the user is logged in
        if (currentUser != null) {
            String teacherId = currentUser.getUid(); // Get the unique ID of the logged-in user
            // Initialize Firebase Database reference
            databaseReference = FirebaseDatabase.getInstance().getReference("teachers");
            loadTeacherName(teacherId);
        } else {
            binding.teacherName.setText("Teacher's Name"); // Default name if not found
            Toast.makeText(getActivity(), "User not logged in.", Toast.LENGTH_SHORT).show();
        }

        // Set up button to view students' profiles
        binding.viewStudentsButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new StudentProfilesFragment())
                    .addToBackStack(null) // Allow back navigation
                    .commit();
        });

        // Set up button to manage violations (if needed)
        binding.manageViolationsButton.setOnClickListener(v -> {
            // Navigate to the violations management fragment
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ManageViolationsFragment()) // Assuming you create this fragment
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void loadTeacherName(String teacherId) {
        databaseReference.child(teacherId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String teacherName = dataSnapshot.child("fullName").getValue(String.class);
                    if (teacherName != null) {
                        binding.teacherName.setText(teacherName);
                    } else {
                        binding.teacherName.setText("Teacher's Name");
                    }
                } else {
                    binding.teacherName.setText("Teacher not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to load teacher's name.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks
    }
}
