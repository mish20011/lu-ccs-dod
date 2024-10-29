package com.catignascabela.dodapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.catignascabela.dodapplication.databinding.ActivityTeacherRegistrationBinding;

public class TeacherRegistrationActivity extends AppCompatActivity {

    private ActivityTeacherRegistrationBinding binding;
    private Bitmap profileImageBitmap; // To store the selected profile image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTeacherRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set the onClick listener for the register button
        binding.registerButton.setOnClickListener(v -> registerTeacher());
        binding.uploadPhotoButton.setOnClickListener(v -> openGallery());
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
                        binding.profileImage.setImageBitmap(profileImageBitmap); // Display the selected image
                    } catch (Exception e) {
                        Log.e("ImageError", "Error loading image", e);
                    }
                }
            });

    private void registerTeacher() {
        String username = binding.registerUsername.getText().toString().trim();
        String password = binding.registerPassword.getText().toString().trim();
        String fullName = binding.registerFullName.getText().toString().trim();

        // Get selected department
        int selectedDepartmentId = binding.radioGroupDepartment.getCheckedRadioButtonId();
        RadioButton selectedDepartmentButton = findViewById(selectedDepartmentId);
        String department = selectedDepartmentButton != null ? selectedDepartmentButton.getText().toString() : "";

        // Input validation
        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || department.isEmpty()) {
            Toast.makeText(TeacherRegistrationActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return; // Exit the method if any field is empty
        }

        // Save the details using SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("teacherUsername", username);
        editor.putString("teacherPassword", password); // Consider hashing the password for security
        editor.putString("teacherFullName", fullName);
        editor.putString("teacherDepartment", department); // Save selected department

        // Here, you can also convert the bitmap to a Base64 string or save it in the storage
        // editor.putString("teacherProfileImage", convertBitmapToBase64(profileImageBitmap)); // Uncomment if using Base64

        editor.apply();

        // Show success message
        Toast.makeText(TeacherRegistrationActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

        // Redirect to the login page or another activity
        startActivity(new Intent(TeacherRegistrationActivity.this, LoginActivity.class));
        finish(); // Close this activity
    }

    // Uncomment if you want to convert Bitmap to Base64 (for image storage)
    /*private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }*/
}
