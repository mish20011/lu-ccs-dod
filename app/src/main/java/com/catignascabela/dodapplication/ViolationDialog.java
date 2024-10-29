package com.catignascabela.dodapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ViolationDialog extends DialogFragment {

    private Student student;
    private OnViolationAddedListener listener;

    public interface OnViolationAddedListener {
        void onViolationAdded(Student student, String violation, String punishment);
    }

    public ViolationDialog(Student student, OnViolationAddedListener listener) {
        this.student = student;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_violation, container, false);

        EditText violationInput = view.findViewById(R.id.violation_input);
        EditText punishmentInput = view.findViewById(R.id.punishment_input);
        Button addButton = view.findViewById(R.id.add_violation_button);

        addButton.setOnClickListener(v -> {
            String violation = violationInput.getText().toString();
            String punishment = punishmentInput.getText().toString();

            listener.onViolationAdded(student, violation, punishment);
            dismiss(); // Close the dialog
        });

        return view;
    }
}
