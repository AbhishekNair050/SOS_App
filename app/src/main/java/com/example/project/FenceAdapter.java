package com.example.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

public class FenceAdapter extends ArrayAdapter<String> {
    private final List<String> fences;
    private final Context context;
    private int selectedPosition = -1;

    public FenceAdapter(Context context, List<String> fences) {
        super(context, R.layout.fence_item, fences);
        this.context = context;
        this.fences = fences;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.fence_item, parent, false);
        }

        String fence = fences.get(position);

        TextView fenceTextView = convertView.findViewById(R.id.fenceTextView);
        RadioButton fenceRadioButton = convertView.findViewById(R.id.fenceRadioButton);

        fenceTextView.setText(fence);
        fenceRadioButton.setChecked(position == selectedPosition);

        fenceRadioButton.setOnClickListener(v -> {
            selectedPosition = position;
            notifyDataSetChanged();
        });

        return convertView;
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }
}