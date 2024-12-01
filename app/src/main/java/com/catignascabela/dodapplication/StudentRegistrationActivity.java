package com.catignascabela.dodapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class StudentRegistrationActivity extends AppCompatActivity {

    private EditText studentId, surname, firstName, middleInitial, password, yearBlock;
    private Spinner genderSpinner, courseSpinner;
    private Button registerButton;
    private TextView loginLink;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private String selectedGender, selectedCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference(); // Initialize database reference

        // Initialize UI components
        studentId = findViewById(R.id.register_student_id);
        surname = findViewById(R.id.register_surname);
        firstName = findViewById(R.id.register_first_name);
        middleInitial = findViewById(R.id.register_middle_initial);
        password = findViewById(R.id.register_password);
        yearBlock = findViewById(R.id.register_year_block);
        genderSpinner = findViewById(R.id.spinner_gender);
        courseSpinner = findViewById(R.id.spinner_course);
        registerButton = findViewById(R.id.register_button);
        loginLink = findViewById(R.id.tv_already_registered);

        // Setup Spinners
        setupGenderSpinner();
        setupCourseSpinner();

        // Set click listener for register button
        registerButton.setOnClickListener(v -> registerStudent());

        // Set click listener for login link
        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(StudentRegistrationActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupGenderSpinner() {
        String[] genderOptions = new String[]{"Select Gender", "Male", "Female"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                genderOptions
        );
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedGender = genderOptions[position];
                } else {
                    selectedGender = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedGender = "";
            }
        });
    }

    private void setupCourseSpinner() {
        String[] courseOptions = new String[]{"Select Course", "BS-Computer Science", "BS-Information Technology"};
        ArrayAdapter<String> courseAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                courseOptions
        );
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(courseAdapter);

        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedCourse = courseOptions[position];
                } else {
                    selectedCourse = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCourse = "";
            }
        });
    }

    private void registerStudent() {
        String studentIdValue = studentId.getText().toString().trim();
        String surnameValue = surname.getText().toString().trim();
        String firstNameValue = firstName.getText().toString().trim();
        String middleInitialValue = middleInitial.getText().toString().trim();
        String passwordValue = password.getText().toString().trim();
        String yearBlockValue = yearBlock.getText().toString().trim();

        // Validate input fields
        if (TextUtils.isEmpty(studentIdValue) || TextUtils.isEmpty(surnameValue) ||
                TextUtils.isEmpty(firstNameValue) || TextUtils.isEmpty(passwordValue) ||
                TextUtils.isEmpty(yearBlockValue) || TextUtils.isEmpty(selectedGender) ||
                TextUtils.isEmpty(selectedCourse)) {

            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate student ID format (accepts alphanumeric and hyphen)
        if (!studentIdValue.matches("[a-zA-Z0-9-]+")) {
            Toast.makeText(this, "Invalid student ID format. Use alphanumeric characters and hyphens only.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate email from student ID and ensure it's lowercase for Firebase
        String emailValue = studentIdValue.toLowerCase() + "@lustudent.com";

        // Validate generated email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            Toast.makeText(this, "Generated email is invalid: " + emailValue, Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("StudentRegistration", "Generated Email: " + emailValue);

        // Register student in Firebase Authentication
        mAuth.createUserWithEmailAndPassword(emailValue, passwordValue)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Get the Firebase Authentication user ID (uid)
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            String userId = currentUser.getUid();  // Safely get UID

                            // Now, save student data in Realtime Database
                            saveStudentData(studentIdValue, surnameValue, firstNameValue, middleInitialValue,
                                    emailValue, selectedGender, selectedCourse, yearBlockValue, userId);
                        } else {
                            Log.e("StudentRegistration", "User is not authenticated.");
                            Toast.makeText(StudentRegistrationActivity.this, "User not authenticated.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (task.getException() != null) {
                            Log.e("StudentRegistration", "Registration failed", task.getException());
                            Toast.makeText(StudentRegistrationActivity.this,
                                    "Registration failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(StudentRegistrationActivity.this,
                                    "Unknown error occurred during registration.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveStudentData(String studentId, String surname, String firstName, String middleInitial,
                                 String email, String gender, String course, String yearBlock, String userId) {

        // Create a HashMap to store student data
        HashMap<String, Object> studentData = new HashMap<>();
        studentData.put("studentId", studentId);
        studentData.put("surname", surname);
        studentData.put("firstName", firstName);
        studentData.put("middleInitial", middleInitial);
        studentData.put("email", email);
        studentData.put("gender", gender);
        studentData.put("course", course);
        studentData.put("yearBlock", yearBlock);

        // Save student data under "students" node in Realtime Database
        databaseRef.child("students").child(userId).setValue(studentData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(StudentRegistrationActivity.this, "User profile created for " + studentId, Toast.LENGTH_SHORT).show();
                    navigateToHomepageActivity();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(StudentRegistrationActivity.this, "Error creating user profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToHomepageActivity() {
        Intent intent = new Intent(StudentRegistrationActivity.this, HomepageActivity.class);
        intent.putExtra("isTeacher", false);
        startActivity(intent);
        finish();
    }
}
