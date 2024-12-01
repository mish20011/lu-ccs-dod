package com.catignascabela.dodapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.catignascabela.dodapplication.databinding.FragmentHomeTeacherBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TeacherHomeFragment extends Fragment {
    private FragmentHomeTeacherBinding binding;
    private List<Student> studentList;
    private StudentAdapter studentAdapter;
    private DatabaseReference studentsRef; // Reference for students

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeTeacherBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Initialize Firebase Auth and get the current user
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        // Initialize Realtime Database reference for students
        studentsRef = FirebaseDatabase.getInstance().getReference("students");

        // Set up the RecyclerView for displaying students
        binding.studentRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        studentList = new ArrayList<>();
        studentAdapter = new StudentAdapter(studentList, student -> openManageViolationsFragment(student));
        binding.studentRecyclerView.setAdapter(studentAdapter);

        // Load all students initially
        loadStudentsByDepartment("All");

        // Set up department filter spinner
        String[] departmentOptions = {"All", "BS-Computer Science", "BS-Information Technology"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, departmentOptions);
        binding.departmentSpinner.setAdapter(adapter);

        // Set listener to reload list based on department selection
        binding.departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDepartment = departmentOptions[position];
                loadStudentsByDepartment(selectedDepartment);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                loadStudentsByDepartment("All");
            }
        });

        // Load teacher's name if the user is logged in
        if (currentUser != null) {
            String teacherId = currentUser.getUid();
            loadTeacherName(teacherId);
        } else {
            binding.teacherName.setText("Teacher's Name");
            Toast.makeText(requireActivity(), "User not logged in.", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void loadStudentsByDepartment(String department) {
        studentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentList.clear();
                for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                    Student student = studentSnapshot.getValue(Student.class);
                    if (student != null && (department.equals("All") || student.getCourse().equals(department))) {
                        studentList.add(student);
                    }
                }
                studentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Failed to load students: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTeacherName(String teacherId) {
        // Load teacher's name from the database (if needed)
        // Assuming you have a teachers node in your database
        DatabaseReference teacherRef = FirebaseDatabase.getInstance().getReference("teachers").child(teacherId);

        teacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot taskSnapshot) {
                if (taskSnapshot.exists()) {
                    String teacherName = taskSnapshot.child("fullName").getValue(String.class);
                    binding.teacherName.setText(teacherName != null ? teacherName : "Teacher's Name");
                } else {
                    binding.teacherName.setText("Teacher not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                binding.teacherName.setText("Error loading teacher name");
            }
        });
    }

    private void openManageViolationsFragment(Student student) {
        ManageViolationsFragment manageViolationsFragment = new ManageViolationsFragment();
        Bundle args = new Bundle();
        args.putString("studentId", student.getStudentId());
        args.putString("fullName", student.getFullName());
        manageViolationsFragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, manageViolationsFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks
    }
}
