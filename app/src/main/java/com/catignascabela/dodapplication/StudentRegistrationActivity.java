package com.catignascabela.dodapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.catignascabela.dodapplication.databinding.ActivityStudentRegistrationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StudentRegistrationActivity extends AppCompatActivity {

    private ActivityStudentRegistrationBinding binding;
    private Bitmap profileImageBitmap;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        // Initialize Database Reference to "students"
        databaseReference = FirebaseDatabase.getInstance().getReference("students");

        // Set the onClick listener for the register button
        binding.registerButton.setOnClickListener(v -> registerStudent());
        binding.uploadProfilePictureButton.setOnClickListener(v -> openGallery());
    }

    private void openGallery() {
        // Launch the gallery to pick an image
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoResultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> photoResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Uri imageUri = result.getData().getData();
                    try {
                        profileImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        // Removed the line that sets the image to an ImageView
                    } catch (Exception e) {
                        Log.e("ImageError", "Error loading image", e);
                    }
                }
            });

    private void registerStudent() {
        String email = binding.registerEmail.getText().toString().trim();
        String password = binding.registerPassword.getText().toString().trim();
        String fullName = binding.registerFirstName.getText().toString().trim() + " " + binding.registerSurname.getText().toString().trim();
        String studentId = binding.registerStudentId.getText().toString().trim(); // Student ID field
        String yearBlock = binding.registerYearBlock.getText().toString().trim(); // Year/Block field
        String gender = binding.radioGroupGender.getCheckedRadioButtonId() == R.id.radio_male ? "Male" : "Female"; // Gender field
        String course = binding.radioGroupCourse.getCheckedRadioButtonId() == R.id.radio_bs_computer_science ? "BS-Computer Science" : "BS-Information Technology"; // Course field

        // Input validation
        if (email.isEmpty() || password.isEmpty() || fullName.isEmpty() || studentId.isEmpty() || yearBlock.isEmpty() || gender.isEmpty() || course.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return; // Exit the method if any field is empty
        }

        // Firebase Authentication to create user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registration successful
                        FirebaseUser user = mAuth.getCurrentUser();
                        // Create a Student object with the role
                        Student newStudent = new Student(studentId, email, fullName, gender, yearBlock, course, "student");

                        // Save user data to Firebase Realtime Database
                        if (user != null) {
                            databaseReference.child(user.getUid()).setValue(newStudent)
                                    .addOnCompleteListener(saveTask -> {
                                        if (saveTask.isSuccessful()) {
                                            Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(this, LoginActivity.class));
                                            finish(); // Finish this activity to remove it from the back stack
                                        } else {
                                            Toast.makeText(this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        // If registration fails, display a message to the user.
                        Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
