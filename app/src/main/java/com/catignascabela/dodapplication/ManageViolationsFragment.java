package com.catignascabela.dodapplication;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.catignascabela.dodapplication.databinding.FragmentManageViolationsBinding;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ManageViolationsFragment extends Fragment {

    private FragmentManageViolationsBinding binding;
    private DatabaseReference databaseReference;
    private List<String> violationTypes;
    private HashMap<String, String> violationMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentManageViolationsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("violations");

        // Load violation types from the database
        loadViolationTypes();

        // Set up the add violation button
        binding.addViolationButton.setOnClickListener(v -> {
            String selectedType = binding.violationSpinner.getSelectedItem().toString();
            String comment = binding.commentEditText.getText().toString().trim();

            if (!selectedType.isEmpty() && !comment.isEmpty()) {
                addViolation(selectedType, comment);
            } else {
                Toast.makeText(getActivity(), "Please select a violation type and add a comment.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void loadViolationTypes() {
        databaseReference.child("types").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                violationTypes = new ArrayList<>();
                violationMap = new HashMap<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String type = snapshot.getKey();
                    String description = snapshot.getValue(String.class);
                    if (type != null && description != null) {
                        violationTypes.add(description);
                        violationMap.put(description, type); // Store mapping for later use
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                        android.R.layout.simple_spinner_item, violationTypes);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.violationSpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to load violation types.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addViolation(String violationType, String comment) {
        String violationKey = databaseReference.child("records").push().getKey(); // Unique key for each violation record

        if (violationKey != null) {
            HashMap<String, String> violationData = new HashMap<>();
            violationData.put("type", violationMap.get(violationType)); // Get the corresponding violation type ID
            violationData.put("comment", comment);

            databaseReference.child("records").child(violationKey).setValue(violationData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Violation added successfully.", Toast.LENGTH_SHORT).show();
                            binding.commentEditText.setText(""); // Clear comment input
                        } else {
                            Toast.makeText(getActivity(), "Failed to add violation.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks
    }
}
