package com.catignascabela.dodapplication;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.catignascabela.dodapplication.databinding.FragmentHomeTeacherBinding;

public class TeacherHomeFragment extends Fragment {

    private FragmentHomeTeacherBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeTeacherBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Set the teacher's name (You can retrieve it from SharedPreferences)
        String teacherName = "Teacher's Name"; // Replace with actual teacher name
        binding.teacherName.setText(teacherName);

        // Set up other features, such as a button to view students
        binding.viewStudentsButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new StudentProfilesFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Prevent memory leaks
    }
}
