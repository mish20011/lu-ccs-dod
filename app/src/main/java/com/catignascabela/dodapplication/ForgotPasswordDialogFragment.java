package com.catignascabela.dodapplication;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordDialogFragment extends DialogFragment {

    private FirebaseAuth mAuth;
    private EditText emailBox;

    @Nullable
    @Override
    public android.view.View onCreateView(@NonNull android.view.LayoutInflater inflater,
                                          @Nullable android.view.ViewGroup container,
                                          @Nullable Bundle savedInstanceState) {
        // Inflate the dialog layout
        return inflater.inflate(R.layout.fragment_forgot_password_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull android.view.View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Get the references to the UI elements
        emailBox = view.findViewById(R.id.emailBox);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnReset = view.findViewById(R.id.btnReset);

        // Set up click listeners for the buttons
        btnCancel.setOnClickListener(v -> dismiss()); // Close the dialog when "Cancel" is clicked

        btnReset.setOnClickListener(v -> {
            String email = emailBox.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getActivity(), "Please enter your email", Toast.LENGTH_SHORT).show();
            } else {
                resetPassword(email);
            }
        });
    }

    private void resetPassword(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Reset email sent. Check your inbox.", Toast.LENGTH_SHORT).show();
                        dismiss(); // Close the dialog once reset email is sent
                    } else {
                        Toast.makeText(getActivity(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
