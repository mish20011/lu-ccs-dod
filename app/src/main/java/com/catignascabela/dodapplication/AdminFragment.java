package com.catignascabela.dodapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminFragment extends Fragment {

    private RecyclerView recyclerView;
    private PendingTeachersAdapter adapter;
    private FirebaseDatabase database;
    private DatabaseReference pendingTeachersRef;
    private DatabaseReference teachersRef;
    private DatabaseReference rejectedTeachersRef; // Declare rejectedTeachersRef
    private List<Teacher> pendingTeachersList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the fragment layout
        return inflater.inflate(R.layout.fragment_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.pendingTeachersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        pendingTeachersList = new ArrayList<>();

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance();
        pendingTeachersRef = database.getReference("pending_teachers");
        teachersRef = database.getReference("teachers");
        rejectedTeachersRef = database.getReference("rejected_teachers"); // Initialize rejectedTeachersRef

        fetchPendingTeachers();
    }

    private void fetchPendingTeachers() {
        pendingTeachersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot teacherSnapshot : snapshot.getChildren()) {
                        Teacher teacher = teacherSnapshot.getValue(Teacher.class);
                        if (teacher != null) {
                            pendingTeachersList.add(teacher);
                        }
                    }

                    // Pass both OnApproveListener and OnRejectListener to the adapter
                    adapter = new PendingTeachersAdapter(
                            pendingTeachersList,
                            requireContext(),
                            new PendingTeachersAdapter.OnApproveListener() {
                                @Override
                                public void onApprove(Teacher teacher) {
                                    approveTeacher(teacher); // Pass the Teacher object directly
                                }
                            },
                            new PendingTeachersAdapter.OnRejectListener() {
                                @Override
                                public void onReject(Teacher teacher) {
                                    rejectTeacher(teacher);  // Added reject functionality
                                }
                            }
                    );
                    recyclerView.setAdapter(adapter);
                } else {
                    Toast.makeText(requireContext(), "No pending teachers found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void approveTeacher(Teacher teacher) {
        String teacherId = teacher.getUid();

        // Check if teacherId is null
        if (teacherId == null) {
            Toast.makeText(requireContext(), "Error: Teacher ID is null!", Toast.LENGTH_SHORT).show();
            return; // Exit if teacherId is null
        }

        // Create a new HashMap for approved teacher data
        HashMap<String, Object> approvedTeacherData = new HashMap<>();
        approvedTeacherData.put("username", teacher.getUsername());
        approvedTeacherData.put("fullName", teacher.getFullName());
        approvedTeacherData.put("email", teacher.getEmail());
        approvedTeacherData.put("department", teacher.getDepartment());
        approvedTeacherData.put("isApproved", true);  // Set isApproved to true

        // Move teacher data to teachers node and update isApproved to true
        teachersRef.child(teacherId).setValue(approvedTeacherData)
                .addOnSuccessListener(aVoid -> {
                    // Remove the teacher from pending_teachers node
                    pendingTeachersRef.child(teacherId).removeValue()
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(requireContext(), "Teacher approved successfully!", Toast.LENGTH_SHORT).show();
                                pendingTeachersList.remove(teacher);
                                adapter.notifyDataSetChanged();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(requireContext(), "Error removing from pending: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error approving teacher: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void rejectTeacher(Teacher teacher) {
        String teacherId = teacher.getUid();

        // Check if teacherId is null
        if (teacherId == null) {
            Toast.makeText(requireContext(), "Error: Teacher ID is null!", Toast.LENGTH_SHORT).show();
            return; // Exit if teacherId is null
        }

        // Mark teacher as rejected by updating their status in the "teachers" node
        teachersRef.child(teacherId).child("status").setValue("rejected")
                .addOnSuccessListener(aVoid -> {
                    // Move teacher data to rejected_teachers node
                    rejectedTeachersRef.child(teacherId).setValue(teacher)
                            .addOnSuccessListener(aVoid1 -> {
                                // Remove the teacher from pending_teachers node
                                pendingTeachersRef.child(teacherId).removeValue()
                                        .addOnSuccessListener(aVoid2 -> {
                                            Toast.makeText(requireContext(), "Teacher rejected successfully!", Toast.LENGTH_SHORT).show();
                                            pendingTeachersList.remove(teacher);
                                            adapter.notifyDataSetChanged();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(requireContext(), "Error removing from pending: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(requireContext(), "Error moving to rejected_teachers: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error rejecting teacher: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
