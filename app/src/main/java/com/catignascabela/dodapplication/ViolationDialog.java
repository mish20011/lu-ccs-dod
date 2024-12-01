package com.catignascabela.dodapplication;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class ViolationDialog {

    public static void showViolationDialog(@NonNull Context context, List<Violation> violations, ViolationSelectionListener listener) {
        // Inflate the custom layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_violation, null);

        // Get references to the RecyclerView, title TextView, and custom punishment input
        RecyclerView violationRecyclerView = dialogView.findViewById(R.id.violation_recycler_view);
        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        dialogTitle.setText("Select Violation");

        EditText customPunishmentInput = dialogView.findViewById(R.id.custom_punishment_input);
        customPunishmentInput.setVisibility(View.GONE); // Initially hidden

        // Initialize RecyclerView with ViolationAdapter
        ViolationAdapter adapter = new ViolationAdapter(violations, violation -> {
            // Show the custom punishment input field when a violation is selected
            customPunishmentInput.setVisibility(View.VISIBLE);
            customPunishmentInput.requestFocus(); // Focus on input field

            // Set up the confirm button action when a violation is selected
            Button confirmButton = dialogView.findViewById(R.id.confirm_button);
            confirmButton.setOnClickListener(v -> {
                String customPunishment = customPunishmentInput.getText().toString().trim();
                long timestamp = System.currentTimeMillis(); // Add timestamp to the violation
                listener.onViolationSelected(violation.getDescription(), timestamp, customPunishment);
                Dialog alertDialog = null;
                alertDialog.dismiss(); // Dismiss the dialog when the confirm button is clicked
            });
        });

        // Set the RecyclerView's layout manager and adapter
        violationRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        violationRecyclerView.setAdapter(adapter);

        // Create the dialog
        AlertDialog alertDialog = new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .create();

        // Show the dialog
        alertDialog.show();

        // Set up close button action
        Button closeButton = dialogView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> alertDialog.dismiss()); // Dismiss on close button click
    }

    public interface ViolationSelectionListener {
        void onViolationSelected(String violation, long timestamp, String customPunishment); // Include custom punishment
    }
}
