package com.catignascabela.dodapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ViolationAdapter extends RecyclerView.Adapter<ViolationAdapter.ViolationViewHolder> {

    private final List<Violation> violationList;
    private final OnViolationClickListener clickListener;
    private int selectedPosition = -1; // Track selected item

    public interface OnViolationClickListener {
        void onViolationClick(Violation violation);
    }

    public ViolationAdapter(List<Violation> violationList, OnViolationClickListener listener) {
        this.violationList = violationList;
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public ViolationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_violation, parent, false);
        return new ViolationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViolationViewHolder holder, int position) {
        Violation violation = violationList.get(position);

        // Bind data to the view holder
        holder.bind(violation, clickListener);

        // Highlight the selected item
        if (holder.getAdapterPosition() == selectedPosition) {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_blue_light));
            holder.violationTextView.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.white));
        } else {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.transparent));
            holder.violationTextView.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.black));
        }

        // Update selection state on click
        holder.itemView.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition(); // Use getAdapterPosition() here
            notifyDataSetChanged(); // Refresh RecyclerView to highlight the selection
            if (clickListener != null) {
                clickListener.onViolationClick(violation);
            }
        });
    }

    @Override
    public int getItemCount() {
        return violationList.size();
    }

    public void setSelectedViolation(Violation violation) {
        selectedPosition = violationList.indexOf(violation); // Get index of selected violation
        notifyDataSetChanged(); // Refresh RecyclerView
    }

    public Violation getSelectedViolation() {
        return selectedPosition >= 0 && selectedPosition < violationList.size() ? violationList.get(selectedPosition) : null;
    }

    static class ViolationViewHolder extends RecyclerView.ViewHolder {
        TextView violationTextView;

        public ViolationViewHolder(@NonNull View itemView) {
            super(itemView);
            violationTextView = itemView.findViewById(R.id.violationTextView);
        }

        public void bind(final Violation violation, final OnViolationClickListener listener) {
            violationTextView.setText(violation.getDescription());
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViolationClick(violation);
                }
            });
        }
    }
}


