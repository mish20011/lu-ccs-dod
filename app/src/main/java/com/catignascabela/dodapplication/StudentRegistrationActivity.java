package com.catignascabela.dodapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.catignascabela.dodapplication.databinding.ActivityStudentRegistrationBinding;

public class StudentRegistrationActivity extends AppCompatActivity {

    private ActivityStudentRegistrationBinding binding;
    private Uri profileImageUri; // To store the URI of the uploaded image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set the onClick listener for the upload profile picture button
        binding.uploadProfilePictureButton.setOnClickListener(v -> openImagePicker());

        // Set the onClick listener for the register button
        binding.registerButton.setOnClickListener(v -> registerUser());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    // ActivityResultLauncher to handle image picking
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    profileImageUri = result.getData().getData(); // Store the URI of the selected image
                    Toast.makeText(this, "Profile picture selected", Toast.LENGTH_SHORT).show();
                }
            }
    );

    private void registerUser() {
        // Retrieve the data from input fields
        String studentId = binding.registerStudentId.getText().toString().trim();
        String password = binding.registerPassword.getText().toString().trim();
        String email = binding.registerEmail.getText().toString().trim();
        String surname = binding.registerSurname.getText().toString().trim();
        String firstName = binding.registerFirstName.getText().toString().trim();
        String middleInitial = binding.registerMiddleInitial.getText().toString().trim();
        String yearBlock = binding.registerYearBlock.getText().toString().trim();

        // Get selected gender
        int selectedGenderId = binding.radioGroupGender.getCheckedRadioButtonId();
        RadioButton selectedGenderButton = findViewById(selectedGenderId);
        String gender = selectedGenderButton != null ? selectedGenderButton.getText().toString() : "";

        // Get selected course
        int selectedCourseId = binding.radioGroupCourse.getCheckedRadioButtonId();
        RadioButton selectedCourseButton = findViewById(selectedCourseId);
        String course = selectedCourseButton != null ? selectedCourseButton.getText().toString() : "";

        // Input validation
        if (studentId.isEmpty() || password.isEmpty() || email.isEmpty() || surname.isEmpty() || firstName.isEmpty() ||
                middleInitial.isEmpty() || yearBlock.isEmpty() || gender.isEmpty() || course.isEmpty()) {
            Toast.makeText(StudentRegistrationActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return; // Exit if any field is empty
        }

        // Combine full name
        String fullName = surname +
                (firstName.isEmpty() ? "" : ", " + firstName) +
                (middleInitial.isEmpty() ? "" : " " + middleInitial.trim() + (middleInitial.endsWith(".") ? "" : "."));

        // Save the details using SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", studentId);
        editor.putString("password", password);
        editor.putString("email", email);
        editor.putString("surname", surname);
        editor.putString("firstName", firstName);
        editor.putString("middleInitial", middleInitial);
        editor.putString("fullName", fullName);
        editor.putString("gender", gender);
        editor.putString("yearBlock", yearBlock);
        editor.putString("course", course);
        if (profileImageUri != null) {
            editor.putString("profileImageUri", profileImageUri.toString());
        }
        editor.apply();

        // Registration successful
        Toast.makeText(StudentRegistrationActivity.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(StudentRegistrationActivity.this, LoginActivity.class));
        finish(); // Finish this activity to remove it from the back stack
    }
}
