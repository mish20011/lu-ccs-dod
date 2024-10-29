package com.catignascabela.dodapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.catignascabela.dodapplication.databinding.FragmentStudentProfilesBinding;

import java.util.ArrayList;
import java.util.List;

public class StudentProfilesFragment extends Fragment {

    private FragmentStudentProfilesBinding binding;
    private List<Student> studentList; // List to hold students
    private StudentAdapter studentAdapter; // Adapter for RecyclerView
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentStudentProfilesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Initialize SharedPreferences
        sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        // Retrieve all student information
        studentList = retrieveStudentProfiles();

        // Set up RecyclerView
        studentAdapter = new StudentAdapter(studentList, this::onStudentClick);
        binding.studentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.studentRecyclerView.setAdapter(studentAdapter);

        return view;
    }

    private List<Student> retrieveStudentProfiles() {
        List<Student> students = new ArrayList<>();

        // Assuming you have a naming convention for the student IDs
        int studentCount = sharedPreferences.getInt("studentCount", 0); // Total number of registered students

        for (int i = 0; i < studentCount; i++) {
            String studentId = sharedPreferences.getString("userId_" + i, null);
            String surname = sharedPreferences.getString("surname_" + i, null);
            String firstName = sharedPreferences.getString("firstName_" + i, null);
            String middleInitial = sharedPreferences.getString("middleInitial_" + i, null);
            String gender = sharedPreferences.getString("gender_" + i, null);
            String yearBlock = sharedPreferences.getString("yearBlock_" + i, null);
            String course = sharedPreferences.getString("course_" + i, null);

            // Construct full name
            String fullName = firstName + (middleInitial != null && !middleInitial.isEmpty() ? " " + middleInitial + "." : "") + " " + surname;

            // Add student only if ID is not null
            if (studentId != null) {
                Student student = new Student(fullName, studentId);
                student.setGender(gender);
                student.setYearBlock(yearBlock);
                student.setCourse(course);
                students.add(student);
            }
        }

        return students;
    }

    private void onStudentClick(Student student) {
        // Show a dialog to input violation details
        showViolationDialog(student);
    }

    private void showViolationDialog(Student student) {
        ViolationDialog dialog = new ViolationDialog(student, this::updateStudentViolations);
        dialog.show(getParentFragmentManager(), "ViolationDialog");
    }

    private void updateStudentViolations(Student student, String violation, String punishment) {
        // Update the student with the new violation and punishment
        student.addViolation(violation, punishment);
        studentAdapter.notifyDataSetChanged(); // Refresh the RecyclerView
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks
    }
}
