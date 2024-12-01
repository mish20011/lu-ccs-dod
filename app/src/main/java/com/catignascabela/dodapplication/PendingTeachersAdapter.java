package com.catignascabela.dodapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class PendingTeachersAdapter extends RecyclerView.Adapter<PendingTeachersAdapter.ViewHolder> {

    private List<Teacher> teachersList;
    private Context context;
    private DatabaseReference databaseReference;
    private OnApproveListener onApproveListener;
    private OnRejectListener onRejectListener;

    // Modify constructor to accept both OnApproveListener and OnRejectListener
    public PendingTeachersAdapter(List<Teacher> teachersList, Context context, OnApproveListener onApproveListener, OnRejectListener onRejectListener) {
        this.teachersList = teachersList;
        this.context = context;
        this.onApproveListener = onApproveListener;
        this.onRejectListener = onRejectListener;
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pending_teacher, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Teacher teacher = teachersList.get(position);
        holder.nameTextView.setText(teacher.getFullName());
        holder.departmentTextView.setText(teacher.getDepartment());

        // Set click listeners for approve and reject buttons
        holder.approveButton.setOnClickListener(v -> onApproveListener.onApprove(teacher));
        holder.rejectButton.setOnClickListener(v -> onRejectListener.onReject(teacher));  // Use the passed reject listener
    }

    @Override
    public int getItemCount() {
        return teachersList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, departmentTextView;
        Button approveButton, rejectButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.teacherName);
            departmentTextView = itemView.findViewById(R.id.teacherDepartment);
            approveButton = itemView.findViewById(R.id.approveButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);  // Ensure this button exists in your item layout
        }
    }

    // Define the OnApproveListener interface
    public interface OnApproveListener {
        void onApprove(Teacher teacher);
    }

    // Define the OnRejectListener interface
    public interface OnRejectListener {
        void onReject(Teacher teacher);
    }
}
