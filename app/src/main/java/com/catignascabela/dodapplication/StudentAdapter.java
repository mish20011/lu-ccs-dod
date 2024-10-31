package com.catignascabela.dodapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<Student> studentList;
    private OnStudentClickListener listener;

    public interface OnStudentClickListener {
        void onStudentClick(Student student);
    }

    public StudentAdapter(List<Student> studentList, OnStudentClickListener listener) {
        this.studentList = studentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false); // Use a custom layout
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.name.setText(student.getFullName());
        holder.studentId.setText(student.getStudentId());

        holder.itemView.setOnClickListener(v -> listener.onStudentClick(student));
    }

    @Override
    public int getItemCount() {
        return studentList != null ? studentList.size() : 0; // Handle null or empty list
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView studentId;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.student_name); // Make sure this ID matches your layout
            studentId = itemView.findViewById(R.id.student_id); // Make sure this ID matches your layout
        }
    }
}
