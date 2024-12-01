package com.catignascabela.dodapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.catignascabela.dodapplication.databinding.ActivityLoginBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference studentDatabaseRef;
    private DatabaseReference teacherDatabaseRef;
    private DatabaseReference adminDatabaseRef; // Reference for admins

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        studentDatabaseRef = FirebaseDatabase.getInstance().getReference("students");
        teacherDatabaseRef = FirebaseDatabase.getInstance().getReference("teachers");
        adminDatabaseRef = FirebaseDatabase.getInstance().getReference("admin"); // Reference for admins

        setupTextWatchers();
        setupClickListeners();
    }

    private void setupTextWatchers() {
        binding.userid.addTextChangedListener(createTextWatcher(binding.textInputLayoutUserid));
        binding.password.addTextChangedListener(createTextWatcher(binding.textInputLayoutPassword));
    }

    private TextWatcher createTextWatcher(View underlineView) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (underlineView instanceof com.google.android.material.textfield.TextInputLayout) {
                    ((com.google.android.material.textfield.TextInputLayout) underlineView).setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
    }

    private void setupClickListeners() {
        binding.button.setOnClickListener(v -> handleLogin());
        binding.signupButton.setOnClickListener(v -> showRegistrationChoiceDialog());
        binding.cantRecover.setOnClickListener(v -> showSnackbar("Password recovery is currently disabled."));
    }

    private void handleLogin() {
        String userId = binding.userid.getText().toString().trim();
        String password = binding.password.getText().toString().trim();

        if (userId.isEmpty() || password.isEmpty()) {
            if (userId.isEmpty()) {
                binding.textInputLayoutUserid.setError("Required");
                binding.userid.requestFocus();
            } else {
                binding.textInputLayoutPassword.setError("Required");
                binding.password.requestFocus();
            }
            return;
        }

        Log.d("LoginActivity", "Querying database for userId: " + userId);

        // Try to authenticate as an admin first
        authenticateAdmin(userId, password);
    }

    private void authenticateAdmin(String userId, String password) {
        adminDatabaseRef.orderByChild("id").equalTo(userId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        // Found the admin, check the password
                        DataSnapshot adminSnapshot = snapshot.getChildren().iterator().next();
                        String storedPassword = adminSnapshot.child("password").getValue(String.class);
                        if (storedPassword != null && storedPassword.equals(password)) {
                            // Admin login successful, navigate to AdminFragment
                            navigateToAdminFragment();
                        } else {
                            showSnackbar("Invalid admin credentials.");
                        }
                    } else {
                        // If no admin found, check for student or teacher
                        authenticateUser(userId, password);
                    }
                })
                .addOnFailureListener(e -> showSnackbar("Error querying admin credentials: " + e.getMessage()));
    }

    private void authenticateUser(String userId, String password) {
        studentDatabaseRef.orderByChild("studentId").equalTo(userId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        // Found the student, get their email
                        DataSnapshot studentSnapshot = snapshot.getChildren().iterator().next();
                        String email = studentSnapshot.child("email").getValue(String.class);
                        if (email != null) {
                            signInWithEmail(email, password, false); // false indicates student
                        } else {
                            showSnackbar("No email linked to this student ID.");
                        }
                    } else {
                        // If student not found, try as a teacher
                        teacherDatabaseRef.orderByChild("username").equalTo(userId)
                                .get()
                                .addOnSuccessListener(teacherSnapshot -> {
                                    if (teacherSnapshot.exists()) {
                                        // Found the teacher, get their email
                                        DataSnapshot teacherData = teacherSnapshot.getChildren().iterator().next();
                                        String email = teacherData.child("email").getValue(String.class);
                                        if (email != null) {
                                            signInWithEmail(email, password, true); // true indicates teacher
                                        } else {
                                            showSnackbar("No email linked to this teacher username.");
                                        }
                                    } else {
                                        showSnackbar("Invalid credentials.");
                                    }
                                })
                                .addOnFailureListener(e -> showSnackbar("Error querying teachers: " + e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> showSnackbar("Error querying students: " + e.getMessage()));
    }

    private void signInWithEmail(String email, String password, boolean isTeacher) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("LoginActivity", "Sign-in successful.");
                        navigateToHomepage(isTeacher);
                    } else {
                        showSnackbar("Login failed. Check your credentials.");
                    }
                })
                .addOnFailureListener(e -> showSnackbar("Authentication failed: " + e.getMessage()));
    }

    private void navigateToHomepage(boolean isTeacher) {
        Intent intent = new Intent(LoginActivity.this, HomepageActivity.class);
        intent.putExtra("isTeacher", isTeacher); // Specify if the logged-in user is a teacher
        startActivity(intent);
        finish(); // Close this activity so it cannot be returned to
    }

    private void navigateToAdminFragment() {
        // Admin login successful, navigate to AdminFragment
        Intent intent = new Intent(LoginActivity.this, HomepageActivity.class);
        intent.putExtra("isAdmin", true); // Pass the admin flag
        startActivity(intent);
        finish();
    }

    private void showRegistrationChoiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_registration_choice, null);
        builder.setView(view);

        RadioGroup radioGroup = view.findViewById(R.id.radio_group);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnConfirm = view.findViewById(R.id.btnConfirm);

        AlertDialog dialog = builder.create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = view.findViewById(selectedId);
            if (selectedRadioButton != null) {
                String registrationType = selectedRadioButton.getText().toString();
                if ("Student".equals(registrationType)) {
                    navigateToStudentRegistration();
                } else if ("Teacher".equals(registrationType)) {
                    navigateToTeacherRegistration();
                }
                dialog.dismiss();
            } else {
                Toast.makeText(LoginActivity.this, "Please select a registration type", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void navigateToStudentRegistration() {
        Intent intent = new Intent(LoginActivity.this, StudentRegistrationActivity.class);
        startActivity(intent);
    }

    private void navigateToTeacherRegistration() {
        Intent intent = new Intent(LoginActivity.this, TeacherRegistrationActivity.class);
        startActivity(intent);
    }

    private void showSnackbar(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
    }
}
