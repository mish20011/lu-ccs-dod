package com.catignascabela.dodapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class ViolationListAdapter extends ArrayAdapter<String> {
    private Context context;
    private String[] offenses;
    private int selectedPosition = -1; // To track the selected position
    private OnViolationClickListener clickListener;

    // Define the click listener interface
    public interface OnViolationClickListener {
        void onViolationClick(String offense);
    }

    // Constructor
    public ViolationListAdapter(Context context, String[] offenses, OnViolationClickListener listener) {
        super(context, android.R.layout.simple_list_item_1, offenses);
        this.context = context;
        this.offenses = offenses;
        this.clickListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the default list item view
        View view = super.getView(position, convertView, parent);

        // Set the background color for the selected item
        if (position == selectedPosition) {
            // Highlight the selected item
            view.setBackgroundColor(ContextCompat.getColor(context, R.color.selectedItemBackground)); // Define this color in colors.xml
        } else {
            // Normal background
            view.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
        }

        // Set the text for the list item
        TextView textView = view.findViewById(android.R.id.text1);
        textView.setText(offenses[position]);

        // Set an OnClickListener to handle item clicks
        view.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onViolationClick(offenses[position]);  // Pass the selected offense to the listener
            }
            setSelectedPosition(position);  // Update the selected position
        });

        return view;
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
        notifyDataSetChanged();  // Notify the adapter to update the view
    }
}
