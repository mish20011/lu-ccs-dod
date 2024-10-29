package com.catignascabela.dodapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.catignascabela.dodapplication.databinding.ActivityLoginBinding;
import com.catignascabela.dodapplication.databinding.DialogRegistrationChoiceBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // TextWatcher to hide the underline when user starts typing
        binding.userid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.useridUnderline.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.passwordUnderline.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.button.setOnClickListener(v -> {
            String username = binding.userid.getText().toString().trim();
            String password = binding.password.getText().toString().trim();

            binding.useridUnderline.setVisibility(View.GONE);
            binding.passwordUnderline.setVisibility(View.GONE);

            if (username.isEmpty() && password.isEmpty()) {
                binding.useridUnderline.setVisibility(View.VISIBLE);
                binding.passwordUnderline.setVisibility(View.VISIBLE);
                Toast.makeText(LoginActivity.this, "Empty Login", Toast.LENGTH_SHORT).show();
            } else {
                binding.useridUnderline.setVisibility(username.isEmpty() ? View.VISIBLE : View.GONE);
                binding.passwordUnderline.setVisibility(password.isEmpty() ? View.VISIBLE : View.GONE);

                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                String registeredTeacherUsername = prefs.getString("teacherUsername", null);
                String registeredTeacherPassword = prefs.getString("teacherPassword", null);
                String registeredStudentId = prefs.getString("userId", null);
                String registeredStudentPassword = prefs.getString("password", null);

                if (username.equals(registeredTeacherUsername) && password.equals(registeredTeacherPassword)) {
                    // Redirect to Teacher Home Fragment
                    Intent intent = new Intent(LoginActivity.this, HomepageActivity.class);
                    intent.putExtra("isTeacher", true); // Add extra to indicate teacher login
                    startActivity(intent);
                    finish();
                } else if (username.equals(registeredStudentId) && password.equals(registeredStudentPassword)) {
                    // Redirect to Student Home Fragment
                    Intent intent = new Intent(LoginActivity.this, HomepageActivity.class);
                    intent.putExtra("isTeacher", false); // Add extra to indicate student login
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Invalid Login", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.signupButton.setOnClickListener(v -> showRegistrationDialog());
    }

    private void showRegistrationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the dialog layout using ViewBinding
        DialogRegistrationChoiceBinding dialogBinding = DialogRegistrationChoiceBinding.inflate(getLayoutInflater());
        builder.setView(dialogBinding.getRoot());

        // Create the dialog
        AlertDialog dialog = builder.create();

        // Set button click listeners using ViewBinding
        dialogBinding.radioStudent.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, StudentRegistrationActivity.class);
            startActivity(intent);
            dialog.dismiss(); // Close dialog after selection
        });

        dialogBinding.radioTeacher.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, TeacherRegistrationActivity.class);
            startActivity(intent);
            dialog.dismiss(); // Close dialog after selection
        });

        dialogBinding.buttonCancel.setOnClickListener(v -> dialog.dismiss()); // Close dialog on cancel

        // Show the dialog
        dialog.show();
    }
}
