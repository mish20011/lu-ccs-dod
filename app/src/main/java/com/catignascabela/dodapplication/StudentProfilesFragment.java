package com.catignascabela.dodapplication;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class StudentProfilesFragment extends Fragment implements StudentAdapter.OnStudentClickListener {

    private RecyclerView recyclerView;
    private StudentAdapter studentAdapter;
    private List<Student> studentList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_profiles, container, false);
        recyclerView = view.findViewById(R.id.student_recycler_view); // Ensure the ID matches your layout
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        studentList = new ArrayList<>();
        studentAdapter = new StudentAdapter(studentList, this); // Pass the click listener
        recyclerView.setAdapter(studentAdapter);

        loadStudents();
        return view;
    }

    private void loadStudents() {
        DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference("students");
        studentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentList.clear();
                for (DataSnapshot studentSnapshot : snapshot.getChildren()) {
                    Student student = studentSnapshot.getValue(Student.class);
                    if (student != null) {
                        studentList.add(student);
                    }
                }
                studentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load students.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStudentClick(Student student) {
        // Handle click event, e.g., show student details
        Toast.makeText(getContext(), "Clicked: " + student.getFullName(), Toast.LENGTH_SHORT).show();
        // You can navigate to another fragment or activity to show more details about the student
        // Example: navigate to a StudentDetailFragment
    }
}
