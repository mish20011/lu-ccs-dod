package com.catignascabela.dodapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.catignascabela.dodapplication.databinding.FragmentHomeStudentBinding;

public class StudentHomeFragment extends Fragment {

    private FragmentHomeStudentBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeStudentBinding.inflate(inflater, container, false);

        // Get the shared preferences
        sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);

        // Retrieve student details from SharedPreferences
        String studentId = sharedPreferences.getString("userId", "No ID");
        String gender = sharedPreferences.getString("gender", "No Gender");
        String yearBlock = sharedPreferences.getString("yearBlock", "No Year/Block");
        String course = sharedPreferences.getString("course", "No Course");

        // Retrieve surname, first name, and middle initial directly
        String surname = sharedPreferences.getString("surname", "No Surname");
        String firstName = sharedPreferences.getString("firstName", "No First Name");
        String middleInitial = sharedPreferences.getString("middleInitial", "").trim();

        // Debugging logs to verify correct data retrieval
        Log.d("StudentHomeFragment", "Retrieved Surname: " + surname);
        Log.d("StudentHomeFragment", "Retrieved First Name: " + firstName);
        Log.d("StudentHomeFragment", "Retrieved Middle Initial: " + middleInitial);

        // Construct the full name
        String fullName = sharedPreferences.getString("fullName", "No Full Name");

        // Debugging log for full name
        Log.d("StudentHomeFragment", "Retrieved Full Name: " + fullName);

        // Set the student details in the TextViews
        binding.studentIdTextView.setText("ID: " + studentId);
        binding.genderTextView.setText("Gender: " + gender);
        binding.collegeYearTextView.setText("Year/Block: " + yearBlock);
        binding.courseTextView.setText("Course: " + course);
        binding.fullNameTextView.setText("Full Name: " + fullName);

        return binding.getRoot();
    }
}
