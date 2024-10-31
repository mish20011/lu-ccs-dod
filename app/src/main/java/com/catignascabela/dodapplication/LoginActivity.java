package com.catignascabela.dodapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.catignascabela.dodapplication.databinding.ActivityLoginBinding;
import com.catignascabela.dodapplication.databinding.DialogRegistrationChoiceBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Set up TextWatchers to handle text input
        setupTextWatchers();

        binding.button.setOnClickListener(v -> handleLogin());
        binding.signupButton.setOnClickListener(v -> showRegistrationDialog());
    }

    private void setupTextWatchers() {
        binding.userid.addTextChangedListener(createTextWatcher(binding.useridUnderline));
        binding.password.addTextChangedListener(createTextWatcher(binding.passwordUnderline));
    }

    private TextWatcher createTextWatcher(View underlineView) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                underlineView.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
    }

    private void handleLogin() {
        String idOrUsername = binding.userid.getText().toString().trim();
        String password = binding.password.getText().toString().trim();

        // Reset underline visibility
        binding.useridUnderline.setVisibility(View.GONE);
        binding.passwordUnderline.setVisibility(View.GONE);

        if (idOrUsername.isEmpty() || password.isEmpty()) {
            binding.useridUnderline.setVisibility(idOrUsername.isEmpty() ? View.VISIBLE : View.GONE);
            binding.passwordUnderline.setVisibility(password.isEmpty() ? View.VISIBLE : View.GONE);
            Toast.makeText(LoginActivity.this, "Empty Login", Toast.LENGTH_SHORT).show();
        } else {
            loginUser(idOrUsername, password);
        }
    }

    private void loginUser(String idOrUsername, String password) {
        if (idOrUsername.contains("@")) {
            // If input is an email, proceed with Firebase Auth directly
            authenticateWithEmail(idOrUsername, password);
        } else {
            // Assume input is a student ID or teacher username and look up the associated email
            lookupEmailFromIdOrUsername(idOrUsername, password);
        }
    }

    private void lookupEmailFromIdOrUsername(String idOrUsername, String password) {
        DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference("students");
        studentsRef.orderByChild("studentId").equalTo(idOrUsername)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                                String email = studentSnapshot.child("email").getValue(String.class);
                                if (email != null) {
                                    authenticateWithEmail(email, password);
                                    return; // Exit after finding the first matching student
                                }
                            }
                        }
                        // If not found in students, check teachers by username
                        lookupEmailFromTeacherUsername(idOrUsername, password);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void lookupEmailFromTeacherUsername(String username, String password) {
        DatabaseReference teachersRef = FirebaseDatabase.getInstance().getReference("teachers");
        teachersRef.orderByChild("username").equalTo(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot teacherSnapshot : snapshot.getChildren()) {
                                String email = teacherSnapshot.child("email").getValue(String.class);
                                if (email != null) {
                                    authenticateWithEmail(email, password);
                                    return; // Exit after finding the first matching teacher
                                }
                            }
                        }
                        // If no matching email found, show invalid ID message
                        Toast.makeText(LoginActivity.this, "Invalid ID or username", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void authenticateWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        checkUserRoleAndNavigate(user);
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserRoleAndNavigate(FirebaseUser user) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference();

        // Check in the teachers reference first
        userRef.child("teachers").child(user.getUid()).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && "teacher".equals(snapshot.getValue(String.class))) {
                    // Navigate to HomepageActivity with isTeacher flag
                    navigateToHomepage(true);
                } else {
                    // Otherwise, check in the students reference
                    userRef.child("students").child(user.getUid()).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && "student".equals(snapshot.getValue(String.class))) {
                                // Navigate to HomepageActivity with isTeacher flag
                                navigateToHomepage(false);
                            } else {
                                Toast.makeText(LoginActivity.this, "User role not found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToHomepage(boolean isTeacher) {
        Intent intent = new Intent(LoginActivity.this, HomepageActivity.class);
        intent.putExtra("isTeacher", isTeacher); // Pass the user role as an extra
        startActivity(intent);
        finish(); // Close LoginActivity
    }

    private void showRegistrationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the dialog layout using ViewBinding
        DialogRegistrationChoiceBinding dialogBinding = DialogRegistrationChoiceBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());

        // Create the dialog
        AlertDialog dialog = builder.create();

        // Set button click listeners
        dialogBinding.radioStudent.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, StudentRegistrationActivity.class));
            dialog.dismiss(); // Close dialog after selection
        });

        dialogBinding.radioTeacher.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, TeacherRegistrationActivity.class));
            dialog.dismiss(); // Close dialog after selection
        });

        dialogBinding.buttonCancel.setOnClickListener(v -> dialog.dismiss()); // Close dialog on cancel

        // Show the dialog
        dialog.show();
    }
}
