package com.catignascabela.dodapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.catignascabela.dodapplication.databinding.FragmentManageViolationsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageViolationsFragment extends Fragment {
    private FragmentManageViolationsBinding binding;
    private List<Violation> violationList;
    private ViolationAdapter violationAdapter;
    private String studentId;
    private String studentName;
    private FirebaseDatabase database;
    private DatabaseReference violationsRef;
    private List<Violation> currentViolationDetails;

    public ManageViolationsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentManageViolationsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Initialize Firebase Realtime Database
        database = FirebaseDatabase.getInstance();
        violationsRef = database.getReference("violations");
        violationList = new ArrayList<>();
        currentViolationDetails = new ArrayList<>();

        violationAdapter = new ViolationAdapter(violationList, violation -> {
            // Optional: Handle RecyclerView item click
        });

        // Set LayoutManager and Adapter for RecyclerView
        binding.violationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.violationRecyclerView.setAdapter(violationAdapter);

        // Retrieve student details from arguments
        if (getArguments() != null) {
            studentId = getArguments().getString("studentId");
            studentName = getArguments().getString("fullName");
        }

        // Display student name and details in UI
        if (studentName != null) {
            binding.studentName.setText(studentName);
            binding.studentDetails.setText("ID: " + studentId);
        }

        loadViolations(studentId);
        setupViolationSpinner();

        // Set up button click to show violation dialog
        binding.addViolationButton.setOnClickListener(v -> {
            String selectedType = binding.violationSpinner.getSelectedItem().toString();

            if (selectedType.equals("Select Violation Type")) {
                Toast.makeText(getContext(), "Please select a violation type first.", Toast.LENGTH_SHORT).show();
            } else if (currentViolationDetails.isEmpty()) {
                Toast.makeText(getContext(), "No violations available for the selected type.", Toast.LENGTH_SHORT).show();
            } else {
                // Show the dialog to select a violation and input punishment
                ViolationDialog.showViolationDialog(getContext(), currentViolationDetails, (violationDescription, timestamp, customPunishment) -> {
                    if (!customPunishment.isEmpty()) {
                        Violation newViolation = new Violation(violationDescription, customPunishment, timestamp);
                        saveViolationToDatabase(newViolation);
                    } else {
                        Toast.makeText(getContext(), "Please enter a custom punishment.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        binding.settleViolationButton.setOnClickListener(v -> markAsSettled());

        return view;
    }

    private void loadViolations(String studentId) {
        violationsRef.child(studentId).child("violationRecords").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                violationList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Violation violation = snapshot.getValue(Violation.class);
                    if (violation != null) {
                        violationList.add(violation);
                    }
                }
                violationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load violations: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupViolationSpinner() {
        List<String> violationTypes = new ArrayList<>();
        violationTypes.add("Select Violation Type"); // Placeholder option
        violationTypes.add("Light Offenses");
        violationTypes.add("Serious Offenses");
        violationTypes.add("Major Offenses");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, violationTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.violationSpinner.setAdapter(adapter);

        binding.violationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = violationTypes.get(position);

                if (selectedType.equals("Select Violation Type")) {
                    // Reset the current violations list since no valid type is selected
                    currentViolationDetails.clear();
                } else {
                    updateViolationDetails(selectedType);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void updateViolationDetails(String violationType) {
        currentViolationDetails.clear();

        switch (violationType) {
            case "Light Offenses":
                currentViolationDetails.add(new Violation("Non-conformity to uniform regulations", "", 0));
                currentViolationDetails.add(new Violation("Littering and improper waste disposal", "", 0));
                currentViolationDetails.add(new Violation("Using electronic devices that disrupt classes", "", 0));
                currentViolationDetails.add(new Violation("Simple misconduct (disruptive behavior)", "", 0));
                currentViolationDetails.add(new Violation("Unauthorized entry to campus areas", "", 0));
                break;

            case "Serious Offenses":
                currentViolationDetails.add(new Violation("Possession or distribution of pornographic materials", "", 0));
                currentViolationDetails.add(new Violation("Defacing University property", "", 0));
                currentViolationDetails.add(new Violation("Intimidation during school activities", "", 0));
                currentViolationDetails.add(new Violation("Alcohol consumption or gambling in public while in uniform", "", 0));
                currentViolationDetails.add(new Violation("Unauthorized use of ID", "", 0));
                break;

            case "Major Offenses":
                currentViolationDetails.add(new Violation("Cheating and plagiarism", "", 0));
                currentViolationDetails.add(new Violation("Gross misconduct (theft, insubordination)", "", 0));
                currentViolationDetails.add(new Violation("Violent acts against peers or staff", "", 0));
                currentViolationDetails.add(new Violation("Possession of drugs or weapons", "", 0));
                currentViolationDetails.add(new Violation("Sexual misconduct and public displays of intimacy", "", 0));
                break;

            default:
                break;
        }
    }

    private void saveViolationToDatabase(Violation violation) {
        violationsRef.child(studentId).child("violationRecords").push().setValue(violation)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Violation added successfully.", Toast.LENGTH_SHORT).show();
                        loadViolations(studentId);
                    } else {
                        Toast.makeText(getContext(), "Failed to add violation.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void markAsSettled() {
        if (violationList.isEmpty()) {
            Toast.makeText(getContext(), "No violation to settle.", Toast.LENGTH_SHORT).show();
            return;
        }

        Violation currentViolation = violationList.get(0);

        DatabaseReference settledViolationsRef = database.getReference("violations")
                .child(studentId)
                .child("settledViolations");

        settledViolationsRef.push().setValue(currentViolation)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        removeViolation(studentId, currentViolation);
                        Toast.makeText(getContext(), "Violation marked as settled.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to settle violation.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeViolation(String studentId, Violation violation) {
        violationsRef.child(studentId)
                .child("violationRecords")
                .orderByChild("description")
                .equalTo(violation.getDescription())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        for (DataSnapshot snapshot : task.getResult().getChildren()) {
                            snapshot.getRef().removeValue();
                        }
                    } else {
                        Toast.makeText(getContext(), "Failed to remove violation.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
